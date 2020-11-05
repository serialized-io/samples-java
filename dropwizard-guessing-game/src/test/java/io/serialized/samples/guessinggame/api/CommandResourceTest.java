package io.serialized.samples.guessinggame.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.EventBatch;
import io.serialized.samples.guessinggame.DemoAppConfig;
import io.serialized.samples.guessinggame.domain.GameState;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static io.serialized.samples.guessinggame.api.EventTypeMatcher.containsEventType;
import static io.serialized.samples.guessinggame.domain.event.GameStarted.gameStarted;
import static java.util.Collections.singletonList;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CommandResourceTest {

  private static final AggregateApiStub.AggregateApiCallback aggregateApiCallback = mock(AggregateApiStub.AggregateApiCallback.class);
  private static final DropwizardClientExtension dropwizard = new DropwizardClientExtension(new AggregateApiStub(aggregateApiCallback));

  private final AggregateClient<GameState> aggregateClient = new DemoAppConfig().gameAggregateClient(createConfig(dropwizard));
  private final CommandResource commandResource = new CommandResource(aggregateClient);

  private final ResourceExtension resources = ResourceExtension.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addProvider(commandResource)
      .build();

  @BeforeEach
  public void setUp() {
    reset(aggregateApiCallback);
    dropwizard.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Test
  public void testStart() {

    StartGameCommand command = new StartGameCommand();
    command.gameId = UUID.randomUUID();

    when(aggregateApiCallback.eventsStored(eq(command.gameId), any(EventBatch.class))).thenReturn(OK);

    Response response = resources.target("/commands/start-game").request().post(json(command));

    // then
    assertThat(response.getStatus()).isEqualTo(201);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(command.gameId), argThat(containsEventType("GameStarted")));
  }

  @Test
  public void testGuess() {

    GuessNumberCommand command = new GuessNumberCommand();
    command.gameId = UUID.randomUUID();
    command.number = 50;

    AggregateApiStub.AggregateResponse aggregateResponse = new AggregateApiStub.AggregateResponse(
        command.gameId.toString(), "game", 1, singletonList(gameStarted(command.gameId, 10, System.currentTimeMillis())
    ));

    when(aggregateApiCallback.aggregateLoaded(eq("game"), eq(command.gameId))).thenReturn(aggregateResponse);
    when(aggregateApiCallback.eventsStored(eq(command.gameId), any(EventBatch.class))).thenReturn(OK);

    Response response = resources.target("/commands/guess-number").request().post(json(command));

    assertThat(response.getStatus()).isEqualTo(200);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(command.gameId), argThat(containsEventType("PlayerGuessed")));
  }

  public static SerializedClientConfig createConfig(DropwizardClientExtension dropwizard) {
    return new SerializedClientConfig.Builder()
        .rootApiUrl(dropwizard.baseUri() + "/api-stub")
        .accessKey("dummy")
        .secretAccessKey("dummy")
        .build();
  }

}
