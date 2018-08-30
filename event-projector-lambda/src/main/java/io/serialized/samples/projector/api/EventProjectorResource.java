package io.serialized.samples.projector.api;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class EventProjectorResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventProjectorResource.class.getName());

  @POST
  @Path("project-event")
  public Response projectEvent(LinkedHashMap payload) {

    try {
      Map metadata = (Map) payload.get("metadata");
      String aggregateId = (String) metadata.get("aggregateId");
      long createdAt = (long) metadata.get("createdAt");
      long updatedAt = (long) metadata.get("updatedAt");

      Map event = (Map) payload.get("event");
      String eventType = (String) event.get("eventType");

      Map currentState = (Map) payload.get("currentState");
      Map<String, Object> updatedState = new LinkedHashMap<>();

      if ("UserLoggedInEvent".equals(eventType)) {
        Map data = (Map) event.get("data");
        String userId = (String) data.get("userId");
        int updatedLoginCount = (int) currentState.getOrDefault("loginCount", 0) + 1;
        updatedState.put("loginCount", updatedLoginCount);
        LOGGER.info("Updating loginCount for user [{}] to [{}]", userId, updatedLoginCount);
      } else {
        LOGGER.warn("Unsupported eventType: " + eventType);
      }

      return Response.ok(ImmutableMap.of("updatedState", updatedState)).build();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return Response.serverError().build();
  }

}