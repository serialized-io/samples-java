package io.serialized.samples.aggregate.order.client;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.event.AbstractOrderEvent;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static io.serialized.samples.aggregate.order.client.OrderClient.EventBatch.newEventBatch;

public class OrderClient {

  private static final String SERIALIZED_ACCESS_KEY = "Serialized-Access-Key";
  private static final String SERIALIZED_SECRET_ACCESS_KEY = "Serialized-Secret-Access-Key";

  private final Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);
  private final String aggregateType = "order";
  private final URI eventStoreUri;
  private final String accessKey;
  private final String secretAccessKey;

  public OrderClient(URI eventStoreUri, String accessKey, String secretAccessKey) {
    this.eventStoreUri = eventStoreUri;
    this.accessKey = accessKey;
    this.secretAccessKey = secretAccessKey;
  }

  public void saveEvent(String aggregateId, Integer expectedVersion, AbstractOrderEvent event) {
    EventBatch eventBatch = newEventBatch(aggregateId, expectedVersion, Collections.singletonList(event));
    assertSuccessful(doPost(eventBatch));
  }

  public OrderState load(String aggregateId) {
    System.out.println("Loading aggregate with ID: " + aggregateId);
    Invocation.Builder builder = client.target(eventStoreUri).path(aggregateType).path(aggregateId).request();
    Aggregate aggregate = addApiKeyHeaders(builder).get(Aggregate.class);
    return OrderState.loadFromEvents(aggregate.aggregateId, aggregate.aggregateVersion, aggregate.events);
  }

  private Response doPost(EventBatch eventBatch) {
    Invocation.Builder builder = client.target(eventStoreUri).path(aggregateType).path("events").request();
    return addApiKeyHeaders(builder).post(Entity.json(eventBatch));
  }

  private Invocation.Builder addApiKeyHeaders(Invocation.Builder builder) {
    return builder.header(SERIALIZED_ACCESS_KEY, accessKey).header(SERIALIZED_SECRET_ACCESS_KEY, secretAccessKey);
  }

  private void assertSuccessful(Response response) {
    if (Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
      System.out.println("Event(s) successfully saved");
    } else {
      throw new RuntimeException("Unable to save events!" + response.readEntity(String.class));
    }
  }

  public static class Aggregate {
    public String aggregateId;
    public String aggregateType;
    public Integer aggregateVersion;
    public List<AbstractOrderEvent> events;
  }

  public static class EventBatch {
    public String aggregateId;
    public Integer expectedVersion;
    public List<AbstractOrderEvent> events;

    public static EventBatch newEventBatch(String aggregateId, Integer expectedVersion, List<AbstractOrderEvent> events) {
      EventBatch eventBatch = new EventBatch();
      eventBatch.aggregateId = aggregateId;
      eventBatch.expectedVersion = expectedVersion;
      eventBatch.events = events;
      return eventBatch;
    }
  }

}
