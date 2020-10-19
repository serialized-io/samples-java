package io.serialized.samples.projector;

import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.HttpMethod.POST;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StreamLambdaHandlerTest {

  private static StreamLambdaHandler HANDLER;
  private static Context CONTEXT;

  @BeforeClass
  public static void setUp() {
    HANDLER = new StreamLambdaHandler();
    CONTEXT = new MockLambdaContext();
  }

  @Test
  public void testProjectEvent() throws IOException {
    // given
    ObjectMapper objectMapper = LambdaContainerHandler.getObjectMapper();
    String userId = UUID.randomUUID().toString();
    Map<String, Object> payload = ImmutableMap.of(
        "metadata", ImmutableMap.of("aggregateId", UUID.randomUUID().toString(), "createdAt", 1530116996162L, "updatedAt", 1530116996162L),
        "currentState", ImmutableMap.of(),
        "event", ImmutableMap.of("eventType", "UserLoggedInEvent", "data", ImmutableMap.of("userId", userId))
    );

    InputStream requestStream = new AwsProxyRequestBuilder("/project-event", POST)
        .header(CONTENT_TYPE, APPLICATION_JSON)
        .body(payload)
        .buildStream();

    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();

    // when
    HANDLER.handleRequest(requestStream, responseStream, CONTEXT);
    AwsProxyResponse response = objectMapper.readValue(responseStream.toByteArray(), AwsProxyResponse.class);

    // then
    assertEquals(OK.getStatusCode(), response.getStatusCode());
    assertFalse(response.isBase64Encoded());
    assertTrue(response.getHeaders().containsKey(CONTENT_TYPE));
    assertTrue(response.getHeaders().get(CONTENT_TYPE).startsWith(APPLICATION_JSON));

    Map<String, Object> responseData = objectMapper.readValue(response.getBody(), Map.class);
    Map<String, Object> projectionData = (Map<String, Object>) responseData.get("updatedState");
    assertThat(projectionData.get("loginCount"), is(1));
  }

}
