package io.serialized.samples.rockpaperscissors.command;

import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.EventBatch;
import io.serialized.client.aggregate.State;
import io.serialized.samples.rockpaperscissors.domain.Game;
import io.serialized.samples.rockpaperscissors.domain.GameState;
import io.serialized.samples.rockpaperscissors.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static io.serialized.client.aggregate.EventBatch.newBatch;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GameCommandController {

  private final AggregateClient<GameState> gameClient;

  @Autowired
  public GameCommandController(AggregateClient<GameState> gameClient) {
    this.gameClient = gameClient;
  }

  @RequestMapping(value = "/start-game", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void startGame(@RequestBody StartGameCommand request) {

    State<GameState> state = GameState.newGame();

    List<Event> events = Game.fromState(state).startGame(Player.fromString(request.player1), Player.fromString(request.player2));

    EventBatch eventBatch = newBatch(request.gameId)
        .withExpectedVersion(0)
        .addEvents(events).build();

    gameClient.storeEvents(eventBatch);
  }

  @RequestMapping(value = "/show-hand", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void showHand(@RequestBody ShowHandCommand request) {

    // Load the aggregate state from all events
    String gameId = request.gameId;
    State<GameState> gameState = gameClient.loadState(gameId);

    // Initialize our aggregate root with the current state
    // Execute domain logic
    List<Event> events = Game.fromState(gameState).showHand(Player.fromString(request.player), request.answer);

    EventBatch eventBatch = newBatch(gameId)
        .withExpectedVersion(gameState.aggregateVersion())
        .addEvents(events).build();

    // Save events as an event batch
    gameClient.storeEvents(eventBatch);
  }

}
