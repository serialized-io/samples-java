package io.serialized.samples.guessinggame.api;

import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.samples.guessinggame.domain.Game;
import io.serialized.samples.guessinggame.domain.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static io.serialized.client.aggregate.AggregateRequest.saveRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("commands")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class CommandResource {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final AggregateClient<GameState> gameAggregateClient;

  public CommandResource(AggregateClient<GameState> gameAggregateClient) {
    this.gameAggregateClient = gameAggregateClient;
  }

  @POST
  @Path("start-game")
  public Response startGame(@Valid @NotNull StartGameCommand command) {

    logger.info("Starting new game, [{}]", command.gameId);

    Game game = new Game(new GameState());
    Event<?> event = game.start(command.gameId);

    gameAggregateClient.save(saveRequest()
        .withAggregateId(command.gameId)
        .withEvent(event)
        .build());

    URI location = UriBuilder.fromResource(QueryResource.class).path("games/{gameId}").build(command.gameId);
    return Response.created(location).build();
  }

  @POST
  @Path("guess-number")
  public Response guessNumber(@Valid @NotNull GuessNumberCommand command) {

    logger.info("Guessing number [{}] in game [{}]", command.number, command.gameId);

    gameAggregateClient.update(command.gameId, gameState -> {
      Game game = new Game(gameState);
      return game.guess(command.gameId, command.number);
    });

    return Response.ok().build();
  }

}
