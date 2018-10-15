package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionApiClient;
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

  @Autowired
  ProjectionApiClient projectionApiClient;

  public void createHighScoreProjection() {
    projectionApiClient.createOrUpdate(
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
        .addHandler("GameStarted", inc("games"))
        .build();
    projectionApiClient.createOrUpdate(
        build);
  }

}
