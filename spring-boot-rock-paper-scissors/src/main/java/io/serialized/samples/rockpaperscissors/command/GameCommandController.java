package io.serialized.samples.rockpaperscissors.command;

import io.serialized.client.aggregate.AggregateClient;
import io.serialized.samples.rockpaperscissors.domain.Game;
import io.serialized.samples.rockpaperscissors.domain.GameState;
import io.serialized.samples.rockpaperscissors.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    Player player1 = Player.fromString(request.player1);
    Player player2 = Player.fromString(request.player2);
    GameState state = GameState.newGame();

    Game game = Game.fromState(state);
    gameClient.save(request.gameId, game.startGame(player1, player2));
  }

  @RequestMapping(value = "/show-hand", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void showHand(@RequestBody ShowHandCommand request) {

    // Load the aggregate state from all events, execute domain logic and store the result
    gameClient.update(request.gameId, gameState -> {
      Game game = Game.fromState(gameState);
      return game.showHand(Player.fromString(request.player), request.answer);
    });

  }

}
