package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.Event;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.serialized.samples.rockpaperscissors.domain.Answer.PAPER;
import static io.serialized.samples.rockpaperscissors.domain.Answer.ROCK;
import static io.serialized.samples.rockpaperscissors.domain.Answer.SCISSORS;
import static io.serialized.samples.rockpaperscissors.domain.event.GameFinished.gameFinished;
import static io.serialized.samples.rockpaperscissors.domain.event.GameStarted.gameStarted;
import static io.serialized.samples.rockpaperscissors.domain.event.PlayerAnswered.playerAnswered;
import static io.serialized.samples.rockpaperscissors.domain.event.PlayerWonRound.playerWonRound;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundFinished.roundFinished;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundStarted.roundStarted;
import static io.serialized.samples.rockpaperscissors.domain.event.RoundTied.roundTied;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toCollection;

/**
 * Game is our aggregate root.
 * <p>
 * Contains the logic for the game and what events to emit in which situations.
 */
public class Game {

  private final GameState gameState;

  Game(GameState gameState) {
    this.gameState = gameState;
  }

  public static Game fromState(GameState state) {
    return new Game(state);
  }

  public List<Event<?>> startGame(Player player1, Player player2) {
    if (player1.equals(player2)) {
      throw new IllegalArgumentException("Cannot play against yourself");
    }
    Set<Player> players = Stream.of(player1, player2).collect(toCollection(LinkedHashSet::new));
    return singletonList(gameStarted(players));
  }

  public List<Event<?>> showHand(Player player, Answer answer) {

    if (gameState.handAlreadyShown(new PlayerHand(player, answer))) {
      return emptyList();
    }

    gameState.assertAllowsMoreAnswers();

    if (gameState.hasPlayerAnswered(player)) {
      throw new IllegalArgumentException("Player " + player.playerName + " has already answered");
    }

    PlayerHand playerHand = new PlayerHand(player, answer);

    List<Event<?>> events = new ArrayList<>();
    if (gameState.roundHasNotStarted()) {
      events.add(roundStarted(gameState.registeredPlayers()));
    }
    events.add(playerAnswered(playerHand));

    Set<PlayerHand> allHands = new LinkedHashSet<>(gameState.shownHands());
    allHands.add(playerHand);

    if (gameState.allPlayersAnswered(allHands)) {
      if (gameState.isRoundTied(allHands)) {
        events.add(roundTied(gameState.registeredPlayers()));
      } else {

        PlayerHand opponentHand = gameState.shownHands().iterator().next();
        Player roundWinner = calculateWinner(playerHand, opponentHand);
        Player roundLoser = calculateLoser(playerHand, opponentHand);

        events.add(playerWonRound(roundWinner));
        events.add(roundFinished(roundWinner, roundLoser));

        if (gameState.playerWonGame(roundWinner)) {
          events.add(gameFinished(roundWinner, roundLoser));
        }
      }
    }

    return events;
  }

  Player calculateWinner(PlayerHand hand1, PlayerHand hand2) {
    if (hand1.answer.equals(ROCK)) {
      return hand2.answer.equals(SCISSORS) ?
          hand1.player : hand2.player;
    } else if (hand1.answer.equals(PAPER)) {
      return hand2.answer.equals(ROCK) ?
          hand1.player : hand2.player;
    } else
      return hand2.answer.equals(PAPER) ?
          hand1.player : hand2.player;
  }

  Player calculateLoser(PlayerHand player1, PlayerHand player2) {
    return calculateWinner(player1, player2).equals(player1.player) ? player2.player : player1.player;
  }

}
