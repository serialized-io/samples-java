package io.serialized.samples.rockpaperscissors.command;

import io.serialized.client.aggregates.AggregateClient;
import io.serialized.client.aggregates.Event;
import io.serialized.client.aggregates.EventBatch;
import io.serialized.client.aggregates.State;
import io.serialized.samples.rockpaperscissors.domain.Game;
import io.serialized.samples.rockpaperscissors.domain.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static io.serialized.client.aggregates.EventBatch.newBatch;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GameCommandController {

  @Autowired
  AggregateClient<GameState> gameClient;

  @RequestMapping(value = "/start-game", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void startGame(@RequestBody StartGameCommand request) {

    State<GameState> state = GameState.newGame();

    List<Event> events = Game.fromState(state).startGame(request.player1, request.player2);

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
    List<Event> events = Game.fromState(gameState).showHand(request.player, request.answer);

    EventBatch eventBatch = newBatch(gameId)
        .withExpectedVersion(gameState.aggregateVersion())
        .addEvents(events).build();

    // Save events as an event batch
    gameClient.storeEvents(eventBatch);
  }

}
