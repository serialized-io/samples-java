package io.serialized.samples.rockpaperscissors.command;

import io.serialized.client.aggregate.AggregateClient;
import io.serialized.samples.rockpaperscissors.domain.Game;
import io.serialized.samples.rockpaperscissors.domain.GameState;
import io.serialized.samples.rockpaperscissors.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class GameCommandController {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final AggregateClient<GameState> gameClient;

  @Autowired
  public GameCommandController(AggregateClient<GameState> gameClient) {
    this.gameClient = gameClient;
  }

  @RequestMapping(value = "/start-game", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void startGame(@RequestBody StartGameCommand command) {

    Player player1 = Player.fromString(command.player1);
    Player player2 = Player.fromString(command.player2);
    GameState state = GameState.newGame();

    Game game = Game.fromState(state);
    gameClient.save(command.gameId, game.startGame(player1, player2));
    logger.info("Game [{}] started with players [{}, {}]", command.gameId, command.player1, command.player2);
  }

  @RequestMapping(value = "/show-hand", method = POST, consumes = "application/json")
  @ResponseStatus(value = HttpStatus.OK)
  public void showHand(@RequestBody ShowHandCommand command) {

    // Load the aggregate state from all events, execute domain logic and store the result
    gameClient.update(command.gameId, gameState -> {
      Game game = Game.fromState(gameState);
      return game.showHand(Player.fromString(command.player), command.answer);
    });

    logger.info("Player [{}] answered [{}] in game [{}]", command.player, command.answer, command.gameId);
  }

}
