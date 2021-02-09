package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionClient;
import io.serialized.samples.rockpaperscissors.domain.event.GameFinished;
import io.serialized.samples.rockpaperscissors.domain.event.GameStarted;
import io.serialized.samples.rockpaperscissors.domain.event.RoundFinished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.serialized.client.projection.EventSelector.eventSelector;
import static io.serialized.client.projection.Functions.append;
import static io.serialized.client.projection.Functions.inc;
import static io.serialized.client.projection.Functions.merge;
import static io.serialized.client.projection.Functions.set;
import static io.serialized.client.projection.ProjectionDefinition.aggregatedProjection;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.RawData.rawData;
import static io.serialized.client.projection.TargetSelector.targetSelector;

@Service
public class ProjectionInitializer {

  private final ProjectionClient projectionClient;

  @Autowired
  public ProjectionInitializer(ProjectionClient projectionClient) {
    this.projectionClient = projectionClient;
  }

  public void createWinnersProjection() {
    projectionClient.createOrUpdate(
        singleProjection("winners")
            .feed("game")
            .withIdField("winner")
            .addHandler(GameFinished.class.getSimpleName(),
                inc().with(targetSelector("wins")).build(),
                set().with(targetSelector("playerName")).with(eventSelector("winner")).build())
            .build());
    projectionClient.createOrUpdate(
        singleProjection("high-score2")
            .feed("game")
            .withIdField("winner")
            .addHandler(GameFinished.class.getSimpleName(),
                inc().with(targetSelector("wins")).build(),
                set().with(targetSelector("playerName")).with(eventSelector("winner")).build())
            .build());
  }

  public void createGameProjection() {
    projectionClient.createOrUpdate(
        singleProjection("games")
            .feed("game")
            .addHandler(GameStarted.class.getSimpleName(),
                merge().build(),
                set().with(targetSelector("status")).with(rawData("IN_PROGRESS")).build())
            .addHandler(RoundFinished.class.getSimpleName(),
                append()
                    .with(targetSelector("rounds"))
                    .build())
            .addHandler(GameFinished.class.getSimpleName(),
                merge().build(),
                set().with(targetSelector("status")).with(rawData("FINISHED")).build())
            .build());
  }

  public void totalStatsProjection() {
    projectionClient.createOrUpdate(aggregatedProjection("total-game-stats")
        .feed("game")
        .addHandler(GameStarted.class.getSimpleName(), inc().with(targetSelector("gameCount")).build())
        .build());
  }

}
