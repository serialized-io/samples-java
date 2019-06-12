package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.serialized.client.projection.EventSelector.eventSelector;
import static io.serialized.client.projection.Function.*;
import static io.serialized.client.projection.ProjectionDefinition.aggregatedProjection;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.TargetSelector.targetSelector;

@Service
public class ProjectionInitializer {

  private final ProjectionClient projectionClient;

  @Autowired
  public ProjectionInitializer(ProjectionClient projectionClient) {
    this.projectionClient = projectionClient;
  }

  public void createHighScoreProjection() {
    projectionClient.createOrUpdate(
        singleProjection("high-score")
            .feed("game")
            .withIdField("winner")
            .addHandler("GameFinished",
                inc().with(targetSelector("wins")).build(),
                set().with(targetSelector("playerName")).with(eventSelector("winner")).build(),
                setref().with(targetSelector("wins")).build())
            .build());
  }

  public void totalStatsProjection() {
    projectionClient.createOrUpdate(aggregatedProjection("total-game-stats")
        .feed("game")
        .addHandler("GameStarted", inc().with(targetSelector("gameCount")).build())
        .build());
  }

}
