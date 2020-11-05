package io.serialized.samples.guessinggame.api;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.EventBatch;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/api-stub/aggregates")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class AggregateApiStub {

  private final AggregateApiCallback callback;

  public AggregateApiStub(AggregateApiCallback aggregateApiCallback) {
    this.callback = aggregateApiCallback;
  }

  @GET
  @Path("{aggregateType}/{aggregateId}")
  public Response loadAggregate(@PathParam("aggregateType") String aggregateType, @PathParam("aggregateId") String aggregateId) {
    try {
      AggregateResponse responseBody = callback.aggregateLoaded(aggregateType, UUID.fromString(aggregateId));
      return Response.ok(APPLICATION_JSON_TYPE).entity(responseBody).build();
    } catch (Exception e) {
      return Response.status(SERVICE_UNAVAILABLE).build();
    }
  }

  @POST
  @Path("{aggregateType}/{aggregateId}/events")
  public Response saveEvents(@PathParam("aggregateType") String aggregateType,
                             @PathParam("aggregateId") String aggregateId,
                             @NotNull EventBatch eventBatch) {

    try {
      return Response.status(callback.eventsStored(UUID.fromString(aggregateId), eventBatch)).build();
    } catch (Exception e) {
      return Response.status(SERVICE_UNAVAILABLE).build();
    }
  }

  public interface AggregateApiCallback {

    Response.Status eventsStored(UUID aggregateId, EventBatch eventBatch);

    AggregateResponse aggregateLoaded(String aggregateType, UUID aggregateId);

  }

  public static class AggregateResponse {

    public String aggregateId;
    public String aggregateType;
    public long aggregateVersion;
    public List<Event<?>> events;

    public AggregateResponse(String aggregateId, String aggregateType, long aggregateVersion, List<Event<?>> events) {
      this.aggregateId = aggregateId;
      this.aggregateType = aggregateType;
      this.aggregateVersion = aggregateVersion;
      this.events = events;
    }
  }

}
