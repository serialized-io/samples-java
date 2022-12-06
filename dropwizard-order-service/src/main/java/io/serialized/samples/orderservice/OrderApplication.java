package io.serialized.samples.orderservice;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.samples.orderservice.api.ApiExceptionMapper;
import io.serialized.samples.orderservice.api.command.OrderCommandResource;
import io.serialized.samples.orderservice.api.query.OrderQueryResource;
import io.serialized.samples.orderservice.domain.OrderState;
import io.serialized.samples.orderservice.domain.event.OrderCanceled;
import io.serialized.samples.orderservice.domain.event.OrderFullyPaid;
import io.serialized.samples.orderservice.domain.event.OrderPlaced;
import io.serialized.samples.orderservice.domain.event.OrderShipped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static io.serialized.client.projection.EventSelector.eventSelector;
import static io.serialized.client.projection.Functions.add;
import static io.serialized.client.projection.Functions.append;
import static io.serialized.client.projection.Functions.delete;
import static io.serialized.client.projection.Functions.inc;
import static io.serialized.client.projection.Functions.merge;
import static io.serialized.client.projection.Functions.set;
import static io.serialized.client.projection.Functions.setref;
import static io.serialized.client.projection.ProjectionDefinition.aggregatedProjection;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.RawData.rawData;
import static io.serialized.client.projection.TargetFilter.targetFilter;
import static io.serialized.client.projection.TargetSelector.targetSelector;

public class OrderApplication extends Application<OrderApplicationConfig> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderApplication.class);

  @Override
  public void initialize(Bootstrap<OrderApplicationConfig> bootstrap) {
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false))
    );
  }

  @Override
  public void run(OrderApplicationConfig config, Environment environment) {
    environment.getObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(INDENT_OUTPUT, true)
        .setSerializationInclusion(NON_NULL);

    SerializedClientConfig serializedClientConfig = config.serializedClientConfig();
    AggregateClient<OrderState> orderClient = config.orderClient(serializedClientConfig);
    ProjectionClient projectionClient = config.projectionClient(serializedClientConfig);

    environment.jersey().register(new OrderCommandResource(orderClient));
    environment.jersey().register(new OrderQueryResource(projectionClient));
    environment.jersey().register(new ApiExceptionMapper());

    environment.lifecycle().addServerLifecycleListener(server -> {
      createOrUpdateProjections(projectionClient);
      LOGGER.info("Setup complete!");
    });

  }

  private void createOrUpdateProjections(ProjectionClient projectionClient) {

    projectionClient.createOrUpdate(singleProjection("orders-by-id")
        .description("All orders by ID")
        .feed("order")
        .addHandler(OrderPlaced.class.getSimpleName(),
            merge().build(), // Merge all event field into projection
            set().with(targetSelector("status")).with(rawData("PLACED")).build(), // Add/populate a 'status' field
            setref().with(targetSelector("placedAt")).build()) // Make it possible to query/filter from/to on 'placedAt' timestamp field.
        .addHandler(OrderCanceled.class.getSimpleName(),
            merge().build(), // Merge all event field into projection
            set().with(targetSelector("status")).with(rawData("CANCELED")).build()) // Update 'status' field
        .addHandler(OrderFullyPaid.class.getSimpleName(),
            merge().build(), // Merge all event field into projection
            set().with(targetSelector("status")).with(rawData("PAID")).build()) // Update 'status' field
        .addHandler(OrderShipped.class.getSimpleName(),
            merge().build(), // Merge all event field into projection
            set().with(targetSelector("status")).with(rawData("SHIPPED")).build()) // Update 'status' field
        .build());

    projectionClient.createOrUpdate(singleProjection("orders-to-ship")
        .description("Fully paid orders ready to be shipped")
        .feed("order")
        .addHandler(OrderFullyPaid.class.getSimpleName(),
            merge().build(), // Merge all event field into projection
            setref().with(targetSelector("paidAt")).build()) // Set timestamp for query/filtering from/to
        .addHandler(OrderShipped.class.getSimpleName(),
            delete().build()) // Delete instance when the order has been shipped
        .build());

    projectionClient.createOrUpdate(singleProjection("orders-per-customer")
        .description("All orders per customer")
        .feed("order")
        .withIdField("customerId") // Use 'customerId' field as primary key instead of 'aggregateId'
        .addHandler(OrderPlaced.class.getSimpleName(),
            append().with(targetSelector("orders")).build(), // Append order to an array called 'orders'
            set() // Find and update 'status' field of given order in the projected array.
                .with(targetSelector("orders[?].status"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]"))
                .with(rawData("PLACED")).build())
        .addHandler(OrderCanceled.class.getSimpleName(),
            merge() // Find and merge in fields from event
                .with(targetSelector("orders[?]"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]")).build(),
            set() // Find and update 'status' field of given order in the projected array.
                .with(targetSelector("orders[?].status"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]"))
                .with(rawData("CANCELED")).build())
        .addHandler(OrderFullyPaid.class.getSimpleName(),
            merge() // Find and merge in fields from event
                .with(targetSelector("orders[?]"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]")).build(),
            set() // Find and update 'status' field of given order in the projected array.
                .with(targetSelector("orders[?].status"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]"))
                .with(rawData("PAID")).build())
        .addHandler(OrderShipped.class.getSimpleName(),
            merge() // Find and merge in fields from event
                .with(targetSelector("orders[?]"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]")).build(),
            set() // Find and update 'status' field of given order in the projected array.
                .with(targetSelector("orders[?].status"))
                .with(targetFilter("[?(@.orderId == $.event.orderId)]"))
                .with(rawData("SHIPPED")).build())
        .build());

    projectionClient.createOrUpdate(aggregatedProjection("total-revenue")
        .description("The total revenue for all fully paid orders")
        .feed("order")
        .addHandler(OrderFullyPaid.class.getSimpleName(),
            add() // Summarize all order amounts in one field called 'revenue'
                .with(eventSelector("orderAmount"))
                .with(targetSelector("revenue"))
                .build(),
            set() // Field to show when last event was processed
                .with(eventSelector("paidAt"))
                .with(targetSelector("updatedAt"))
                .build())
        .build());

    projectionClient.createOrUpdate(aggregatedProjection("shipped-orders-count")
        .description("The total number of shipped orders")
        .feed("order")
        .addHandler(OrderShipped.class.getSimpleName(),
            inc() // Update the total count
                .with(targetSelector("shippedOrdersCount")).build(),
            set() // Field to show when last event was processed
                .with(eventSelector("shippedAt"))
                .with(targetSelector("updatedAt"))
                .build())
        .build());
  }

  public static void main(String[] args) throws Exception {
    try {
      LOGGER.info("Starting application...");
      new OrderApplication().run(args);
    } catch (Exception e) {
      LOGGER.error("Unable to start application", e);
      throw e;
    }
  }

}
