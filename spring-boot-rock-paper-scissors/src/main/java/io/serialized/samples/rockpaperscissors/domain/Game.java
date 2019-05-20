package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.State;

import java.util.ArrayList;
import java.util.List;

import static io.serialized.samples.rockpaperscissors.domain.event.GameFinished.gameFinished;
import static io.serialized.samples.rockpaperscissors.domain.event.GameStarted.gameStarted;
import static io.serialized.samples.rockpaperscissors.domain.event.PlayerAnswered.playerAnswered;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundFinished.roundFinished;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundStarted.roundStarted;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundTied.roundTied;
import static java.util.Arrays.asList;

/**
 * Game is our aggregate root.
 * <p>
 * Contains the logic for the game and what events to emit in which situations.
 */
public class Game {

  private final GameState gameState;

  public Game(GameState gameState) {
    this.gameState = gameState;
  }

  public static Game fromState(State<GameState> state) {
    return new Game(state.data());
  }

  public List<Event> startGame(Player player1, Player player2) {
    return asList(
        gameStarted(player1, player2),
        roundStarted(player1, player2)
    );
  }

  public List<Event> showHand(Player player, Answer answer) {
    gameState.assertAllowsMoreAnswers();

    List<Event> events = new ArrayList<>();

    Round round = gameState.currentRound().playerAnswered(player, answer);
    if (!gameState.currentRound().hasPlayerAnswered(player)) {

      events.add(playerAnswered(player, answer));

      if (round.isTied()) {
        events.add(roundTied(answer, gameState.player1(), gameState.player2()));
      } else if (round.isFinished()) {
        events.add(roundFinished(round.result()));
        GameScore gameScore = GameScore.addRounds(gameState.finishedRounds()).addRound(round);

        // If the game has a winner, it was the last round,
        // otherwise start the next round
        if (gameScore.hasWinner()) {
          events.add(gameFinished(gameScore));
        } else {
          events.add(roundStarted(gameState.player1(), gameState.player2()));
        }
      }
    }
    return events;
  }

}
