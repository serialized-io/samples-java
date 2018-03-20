package io.serialized.samples.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.serialized.samples.orderservice.api.command.OrderCommandResource;
import io.serialized.samples.orderservice.api.query.OrderQueryResource;
import io.serialized.samples.orderservice.integration.EventStoreService;
import io.serialized.samples.orderservice.integration.ProjectionService;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.google.common.base.Preconditions.checkState;
import static java.util.concurrent.TimeUnit.SECONDS;

public class OrderApplication extends Application<OrderApplicationConfig> {

  private static final int SERIALIZD_TIMEOUT_SECONDS = 30;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Override
  public void run(OrderApplicationConfig config, Environment environment) {
    ObjectMapper objectMapper = environment.getObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(INDENT_OUTPUT, true)
        .setSerializationInclusion(NON_NULL);

    Headers headers = new Headers.Builder()
        .add("Serialized-Access-Key", config.serializedAccessKey)
        .add("Serialized-Secret-Access-Key", config.serializedSecretAccessKey)
        .build();

    URI apiUrl = config.serializedApi;
    EventStoreService eventStoreService = newRetrofitClient(apiUrl, objectMapper, headers).create(EventStoreService.class);
    ProjectionService projectionService = newRetrofitClient(apiUrl, objectMapper, headers).create(ProjectionService.class);

    createOrUpdateProjections(projectionService);

    environment.jersey().register(new OrderCommandResource(eventStoreService));
    environment.jersey().register(new OrderQueryResource(projectionService));
  }

  private Retrofit newRetrofitClient(URI baseUrl, ObjectMapper objectMapper, Headers headers) {
    return new Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(HttpUrl.get(baseUrl))
        .client(new OkHttpClient.Builder()
            .readTimeout(SERIALIZD_TIMEOUT_SECONDS, SECONDS)
            .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().headers(headers).build()))
            .addInterceptor(chain -> {
              okhttp3.Response response = chain.proceed(chain.request());
              logger.info("SERIALIZED: " + response.toString());
              return response;
            })
            .build())
        .build();
  }

  private void createOrUpdateProjections(ProjectionService projectionService) {
    try {
      ObjectMapper objectMapper = new ObjectMapper().configure(ALLOW_COMMENTS, true);
      createOrUpdateDefinition("orders", projectionService, readDefinitionData(objectMapper, "projections/orders.json"));
      createOrUpdateDefinition("orders-per-customer", projectionService, readDefinitionData(objectMapper, "projections/orders-per-customer.json"));
      createOrUpdateDefinition("total-customer-debt", projectionService, readDefinitionData(objectMapper, "projections/total-customer-debt.json"));
      createOrUpdateDefinition("shipping-stats", projectionService, readDefinitionData(objectMapper, "projections/shipping-stats.json"));
    } catch (IOException e) {
      logger.warn("Failed to update projections", e);
    }
  }

  private void createOrUpdateDefinition(String name, ProjectionService projectionService, Map definition) throws IOException {
    logger.info("Creating/updating projection definition: {}", name);
    Response<Void> response = projectionService.createOrUpdateDefinition(name, definition).execute();
    checkState(response.isSuccessful(), "Unable to create/update projection: %s, %s", response.code(), response.message());
  }

  private Map readDefinitionData(ObjectMapper objectMapper, String resourceName) throws IOException {
    URL resource = Resources.getResource(resourceName);
    return objectMapper.readValue(resource, Map.class);
  }

  public static void main(String[] args) throws Exception {
    new OrderApplication().run(args);
  }

}
