package io.serialized.samples.aggregate.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;
import io.serialized.samples.order.domain.event.OrderEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Event store implementation for Serialized Event Sourcing API.
 */
public class OrderService {

  private static final String SERIALIZED_ACCESS_KEY = "Serialized-Access-Key";
  private static final String SERIALIZED_SECRET_ACCESS_KEY = "Serialized-Secret-Access-Key";

  private final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final Client client = ClientBuilder.newClient().register(new JacksonJsonProvider(objectMapper));

  private final String aggregateType = "order";
  private final URI eventStoreUri;
  private final String accessKey;
  private final String secretAccessKey;

  public OrderService(URI eventStoreUri, String accessKey, String secretAccessKey) {
    this.eventStoreUri = eventStoreUri;
    this.accessKey = accessKey;
    this.secretAccessKey = secretAccessKey;
  }

  public OrderState load(OrderId orderId) {
    System.out.println("Loading aggregate with ID: " + orderId);
    Invocation.Builder builder = client.target(eventStoreUri).path(aggregateType).path(orderId.id).request();
    OrderAggregate aggregate = addApiKeyHeaders(builder).get(OrderAggregate.class);

    return OrderState.loadFromEvents(new OrderId(orderId.id), aggregate.aggregateVersion, aggregate.events);
  }

  public void saveEvent(OrderId orderId, Integer expectedVersion, OrderEvent event) {
    List<OrderEvent> events = Collections.singletonList(event);
    saveEvents(orderId, expectedVersion, events);
  }

  public void saveEvents(OrderId orderId, Integer expectedVersion, List<OrderEvent> events) {
    doPost(newEventBatch(orderId, expectedVersion, events));
  }

  protected Response doPost(EventBatch eventBatch) {
    Invocation.Builder builder = client.target(eventStoreUri).path(aggregateType).path("events").request();
    return assertSuccessful(addApiKeyHeaders(builder).post(Entity.json(eventBatch)));
  }

  private Response assertSuccessful(Response response) {
    if (Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
      System.out.println("Event(s) successfully saved");
      return response;
    } else {
      throw new RuntimeException("Unable to save events!" + response.readEntity(String.class));
    }
  }

  private Invocation.Builder addApiKeyHeaders(Invocation.Builder builder) {
    return builder.header(SERIALIZED_ACCESS_KEY, accessKey).header(SERIALIZED_SECRET_ACCESS_KEY, secretAccessKey);
  }

  public static class OrderAggregate<E> {
    public String aggregateId;
    public Integer aggregateVersion;
    public List<OrderEvent> events;
  }

  public static class EventBatch<E> {
    public String aggregateId;
    public Integer expectedVersion;
    public List<E> events;
  }

  public EventBatch newEventBatch(OrderId orderId, Integer expectedVersion, List<OrderEvent> events) {
    EventBatch eventBatch = new EventBatch();
    eventBatch.aggregateId = orderId.id;
    eventBatch.expectedVersion = expectedVersion;
    eventBatch.events = events;
    return eventBatch;
  }
}
