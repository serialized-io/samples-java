package io.serialized.samples.guessinggame.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class PlayerGuessed {

  private UUID gameId;
  private int guess;
  private long guessedAt;

  public static Event<PlayerGuessed> playerGuessed(UUID gameId, int guess, long guessedAt) {
    PlayerGuessed event = new PlayerGuessed();
    event.gameId = gameId;
    event.guess = guess;
    event.guessedAt = guessedAt;
    return newEvent(event).build();
  }

  public int getGuess() {
    return guess;
  }

}
