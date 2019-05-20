package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.State;
import io.serialized.samples.rockpaperscissors.domain.event.*;

import java.util.ArrayList;
import java.util.List;

import static io.serialized.samples.rockpaperscissors.domain.GameStatus.NEW;

/**
 * The transient state of a game, built up from different events
 */
public class GameState {

  private Player player1;
  private Player player2;
  private GameStatus status = NEW;
  private Round currentRound = Round.NONE;
  private List<Round> finishedRounds = new ArrayList<>();

  public static State<GameState> newGame() {
    return new State<>(0, new GameState());
  }

  public GameState gameStarted(Event<GameStarted> event) {
    this.player1 = Player.fromString(event.getData().player1);
    this.player2 = Player.fromString(event.getData().player2);
    this.status = GameStatus.STARTED;
    this.currentRound = Round.newRound(player1, player2);
    return this;
  }

  public GameState roundStarted(Event<RoundStarted> event) {
    currentRound = Round.newRound(player1, player2);
    return this;
  }

  public GameState playerAnswered(Event<PlayerAnswered> event) {
    currentRound = currentRound.playerAnswered(Player.fromString(event.getData().player), event.getData().answer);
    return this;
  }

  public GameState roundTied(Event<RoundTied> event) {
    currentRound = currentRound.clearAnswers();
    return this;
  }

  public GameState roundFinished(Event<RoundFinished> event) {
    finishedRounds.add(currentRound);
    return this;
  }

  public GameState gameFinished(Event<GameFinished> event) {
    this.status = GameStatus.FINISHED;
    return this;
  }

  public GameStatus status() {
    return status;
  }

  public Round currentRound() {
    return currentRound;
  }

  public List<Round> finishedRounds() {
    return finishedRounds;
  }

  public Player player1() {
    return player1;
  }

  public Player player2() {
    return player2;
  }

  public void assertAllowsMoreAnswers() {
    status.assertAllowsMoreAnswers();
  }
}
