package io.serialized.samples.guessinggame.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/api-stub/projections")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class ProjectionApiStub {

  private final ProjectionApiCallback callback;

  public ProjectionApiStub(ProjectionApiCallback projectionApiCallback) {
    this.callback = projectionApiCallback;
  }

  @GET
  @Path("single/{projectionName}/{projectionId}")
  public Response loadSingleProjection(@PathParam("projectionName") String projectionName, @PathParam("projectionId") String projectionId) {
    try {
      Object responseBody = callback.singleProjectionLoaded(projectionName, UUID.fromString(projectionId));
      return Response.ok(APPLICATION_JSON_TYPE).entity(responseBody).build();
    } catch (Exception e) {
      return Response.status(SERVICE_UNAVAILABLE).build();
    }
  }

  public interface ProjectionApiCallback {

    Object singleProjectionLoaded(String projectionName, UUID projectionId);

  }

}
