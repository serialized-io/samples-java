package io.serialized.samples.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.serialized.samples.aggregate.EventService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Event store implementation for Serialized IO Event Sourcing API.
 */
public abstract class SerializedEventService<E, T, A extends SerializedEventService.Aggregate<E>> implements EventService<E, T> {

  private static final String SERIALIZED_ACCESS_KEY = "Serialized-Access-Key";
  private static final String SERIALIZED_SECRET_ACCESS_KEY = "Serialized-Secret-Access-Key";

  private final ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
  private final Client client = ClientBuilder.newClient().register(new JacksonJsonProvider(objectMapper));

  private final String aggregateType;
  private final URI eventStoreUri;
  private final String accessKey;
  private final String secretAccessKey;
  private final Class<A> aggregateClass;

  public SerializedEventService(String aggregateType, URI eventStoreUri, String accessKey, String secretAccessKey, Class<A> aggregateClass) {
    this.aggregateType = aggregateType;
    this.eventStoreUri = eventStoreUri;
    this.accessKey = accessKey;
    this.secretAccessKey = secretAccessKey;
    this.aggregateClass = aggregateClass;
  }

  @Override
  public T load(UUID aggregateId) {
    System.out.println("Loading aggregate with ID: " + aggregateId);
    Invocation.Builder builder = client.target(eventStoreUri).path(aggregateType).path(aggregateId.toString()).request();
    A aggregate = addApiKeyHeaders(builder).get(aggregateClass);
    return loadFromEvents(aggregate.aggregateId, aggregate.aggregateVersion, aggregate.events);
  }

  @Override
  public void saveEvent(String aggregateId, Integer expectedVersion, E event) {
    List<E> events = Collections.singletonList(event);
    saveEvents(aggregateId, expectedVersion, events);
  }

  @Override
  public void saveEvents(String aggregateId, Integer expectedVersion, List<E> events) {
    doPost(newEventBatch(aggregateId, expectedVersion, events));
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

  public static class Aggregate<E> {
    public String aggregateId;
    public Integer aggregateVersion;
    public List<E> events;
  }

  public static class EventBatch<E> {
    public String aggregateId;
    public Integer expectedVersion;
    public List<E> events;
  }

  public EventBatch newEventBatch(String aggregateId, Integer expectedVersion, List<E> events) {
    EventBatch eventBatch = new EventBatch();
    eventBatch.aggregateId = aggregateId;
    eventBatch.expectedVersion = expectedVersion;
    eventBatch.events = events;
    return eventBatch;
  }
}
