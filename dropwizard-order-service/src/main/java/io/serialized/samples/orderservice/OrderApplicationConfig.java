package io.serialized.samples.orderservice;

import io.dropwizard.Configuration;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.samples.orderservice.domain.OrderState;
import io.serialized.samples.orderservice.domain.event.OrderCancelled;
import io.serialized.samples.orderservice.domain.event.OrderFullyPaid;
import io.serialized.samples.orderservice.domain.event.OrderPlaced;
import io.serialized.samples.orderservice.domain.event.OrderShipped;
import io.serialized.samples.orderservice.domain.event.PaymentReceived;

import static io.serialized.client.aggregate.AggregateClient.aggregateClient;

public class OrderApplicationConfig extends Configuration {

  public String serializedAccessKey;

  public String serializedSecretKey;

  public AggregateClient<OrderState> orderClient(SerializedClientConfig config) {
    return aggregateClient("order", OrderState.class, config)
        .registerHandler(OrderCancelled.class, OrderState::handleOrderCancelled)
        .registerHandler(OrderFullyPaid.class, OrderState::handleOrderFullyPaid)
        .registerHandler(OrderPlaced.class, OrderState::handleOrderPlaced)
        .registerHandler(OrderShipped.class, OrderState::handleOrderShipped)
        .registerHandler(PaymentReceived.class, OrderState::handlePaymentReceived)
        .build();
  }

  public ProjectionClient projectionClient(SerializedClientConfig config) {
    return new ProjectionClient.Builder(config).build();
  }

  public SerializedClientConfig serializedClientConfig() {
    return SerializedClientConfig.serializedConfig()
        .accessKey(serializedAccessKey)
        .secretAccessKey(serializedSecretKey)
        .build();
  }

}
