package io.serialized.samples.guessinggame.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.guessinggame.domain.event.GameFinished;
import io.serialized.samples.guessinggame.domain.event.GameStarted;
import io.serialized.samples.guessinggame.domain.event.HintAdded;
import io.serialized.samples.guessinggame.domain.event.PlayerGuessed;

public class GameState {

  private int number;
  private int guessCount;
  private boolean started;
  private boolean finished;

  public GameState handleGameStarted(Event<GameStarted> event) {
    this.started = true;
    this.number = event.data().getNumber();
    return this;
  }

  public GameState handlePlayerGuessed(Event<PlayerGuessed> event) {
    this.guessCount++;
    return this;
  }

  public GameState handleHintAdded(Event<HintAdded> event) {
    return this;
  }

  public GameState handleGameFinished(Event<GameFinished> event) {
    this.finished = true;
    return this;
  }

  public int getNumber() {
    return number;
  }

  public int getGuessCount() {
    return guessCount;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isFinished() {
    return finished;
  }

}
