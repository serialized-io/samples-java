package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionApiClient;
import io.serialized.client.projection.ProjectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static io.serialized.client.projection.query.ProjectionQueries.aggregated;
import static io.serialized.client.projection.query.ProjectionQueries.list;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class GameQueryController {

  @Autowired
  ProjectionApiClient projectionApiClient;

  @RequestMapping(value = "/high-score", method = GET, produces = "application/json")
  @ResponseBody
  public HighScoreProjection highScore() {
    return HighScoreProjection.fromProjections(projectionApiClient.list(list("high-score").build(HighScore.class)));
  }

  @RequestMapping(value = "/stats", method = GET, produces = "application/json")
  @ResponseBody
  public TotalGameStats gameStats() {
    ProjectionResponse<TotalGameStats> projection = projectionApiClient.query(aggregated("total-game-stats").build(TotalGameStats.class));
    return projection.data;
  }

}
