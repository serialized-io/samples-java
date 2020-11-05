package io.serialized.samples.guessinggame.domain;

import com.google.common.base.Preconditions;
import io.serialized.client.aggregate.Event;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.serialized.samples.guessinggame.domain.event.GameFinished.gameFinished;
import static io.serialized.samples.guessinggame.domain.event.GameStarted.gameStarted;
import static io.serialized.samples.guessinggame.domain.event.HintAdded.hintAdded;
import static io.serialized.samples.guessinggame.domain.event.PlayerGuessed.playerGuessed;
import static java.lang.System.currentTimeMillis;

public class Game {

  private static final SecureRandom RANDOMIZER = new SecureRandom();

  private final int number;
  private final int guessCount;
  private final boolean started;
  private final boolean finished;

  public Game(GameState gameState) {
    this.number = gameState.getNumber();
    this.guessCount = gameState.getGuessCount();
    this.started = gameState.isStarted();
    this.finished = gameState.isFinished();
  }

  public Event<?> start(UUID gameId) {
    Preconditions.checkState(!started, "Game is already started!");
    return gameStarted(gameId, generateRandomNumber(), currentTimeMillis());
  }

  public List<Event<?>> guess(UUID gameId, int guess) {
    Preconditions.checkState(started, "Game is not started!");
    Preconditions.checkState(!finished, "Game is already finished!");

    List<Event<?>> events = new ArrayList<>();

    if (guess == number) {
      events.add(playerGuessed(gameId, guess, currentTimeMillis()));
      events.add(gameFinished(gameId, number, "Player won", currentTimeMillis()));

    } else {
      events.add(playerGuessed(gameId, guess, currentTimeMillis()));

      if (guessCount == 9) {
        events.add(gameFinished(gameId, number, "Player lost", currentTimeMillis()));
      } else {
        if (guess > number) {
          events.add(hintAdded(gameId, "Lower!", currentTimeMillis()));
        } else {
          events.add(hintAdded(gameId, "Higher!", currentTimeMillis()));
        }
      }
    }
    return events;
  }

  /**
   * @return Random number between 1 and 100.
   */
  private int generateRandomNumber() {
    return RANDOMIZER.nextInt(100) + 1;
  }

}
