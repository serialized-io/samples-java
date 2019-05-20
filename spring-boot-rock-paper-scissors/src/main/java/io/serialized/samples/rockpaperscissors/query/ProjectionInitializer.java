package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.ProjectionDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static io.serialized.client.projection.Function.*;
import static io.serialized.client.projection.ProjectionDefinition.aggregatedProjection;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.Selector.eventSelector;
import static io.serialized.client.projection.Selector.targetSelector;

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
                inc("wins"),
                set(targetSelector("playerName"), eventSelector("winner")),
                setref("wins"))
            .build());
  }

  public void totalStatsProjection() {
    ProjectionDefinition build = aggregatedProjection("total-game-stats")
        .feed("game")
        .addHandler("GameStarted", inc("gameCount"))
        .build();
    projectionClient.createOrUpdate(
        build);
  }

}
