package io.serialized.samples.guessinggame;

import io.dropwizard.Configuration;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.reaction.ReactionClient;
import io.serialized.samples.guessinggame.domain.GameState;
import io.serialized.samples.guessinggame.domain.event.GameFinished;
import io.serialized.samples.guessinggame.domain.event.GameStarted;
import io.serialized.samples.guessinggame.domain.event.HintAdded;
import io.serialized.samples.guessinggame.domain.event.PlayerGuessed;

import javax.validation.constraints.NotBlank;

import static io.serialized.client.aggregate.AggregateClient.aggregateClient;

public class DemoAppConfig extends Configuration {

  @NotBlank
  public String serializedAccessKey;

  @NotBlank
  public String serializedSecretAccessKey;

  public AggregateClient<GameState> gameAggregateClient(SerializedClientConfig config) {
    return aggregateClient("game", GameState.class, config)
        .registerHandler(GameStarted.class, GameState::handleGameStarted)
        .registerHandler(PlayerGuessed.class, GameState::handlePlayerGuessed)
        .registerHandler(HintAdded.class, GameState::handleHintAdded)
        .registerHandler(GameFinished.class, GameState::handleGameFinished)
        .build();
  }

  public ProjectionClient projectionClient(SerializedClientConfig config) {
    return new ProjectionClient.Builder(config).build();
  }

  public ReactionClient reactionClient(SerializedClientConfig config) {
    return ReactionClient.reactionClient(config).build();
  }

  public SerializedClientConfig serializedClientConfig() {
    return SerializedClientConfig.serializedConfig()
        .accessKey(serializedAccessKey)
        .secretAccessKey(serializedSecretAccessKey)
        .build();
  }

}
