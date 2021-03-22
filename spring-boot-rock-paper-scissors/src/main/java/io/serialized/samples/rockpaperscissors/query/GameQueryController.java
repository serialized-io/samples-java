package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.ProjectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

import static io.serialized.client.projection.query.ProjectionQueries.aggregated;
import static io.serialized.client.projection.query.ProjectionQueries.list;
import static io.serialized.client.projection.query.ProjectionQueries.single;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class GameQueryController {

  private final ProjectionClient projectionClient;

  @Autowired
  public GameQueryController(ProjectionClient projectionClient) {
    this.projectionClient = projectionClient;
  }

  @RequestMapping(value = "/high-score", method = GET, produces = "application/json")
  @ResponseBody
  public HighScore highScore() {
    return HighScore.fromProjections(projectionClient.query(list("winners").build(Winner.class)));
  }

  @RequestMapping(value = "/stats", method = GET, produces = "application/json")
  @ResponseBody
  public TotalGameStats gameStats() {
    ProjectionResponse<TotalGameStats> projection = projectionClient.query(aggregated("total-game-stats").build(TotalGameStats.class));
    return projection.data();
  }

  @RequestMapping(value = "/games/{gameId}", method = GET, produces = "application/json")
  @ResponseBody
  public GameProjection game(@PathVariable UUID gameId) {
    ProjectionResponse<GameProjection> game = projectionClient.query(single("games")
        .withId(gameId)
        .build(GameProjection.class));
    return game.data();
  }

}
