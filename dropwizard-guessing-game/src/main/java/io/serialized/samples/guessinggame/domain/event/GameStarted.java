package io.serialized.samples.guessinggame.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class GameStarted {

  private UUID gameId;
  private int number;
  private long startedAt;

  public static Event<GameStarted> gameStarted(UUID gameId, int number, long startedAt) {
    GameStarted event = new GameStarted();
    event.gameId = gameId;
    event.number = number;
    event.startedAt = startedAt;
    return newEvent(event).build();
  }

  public int getNumber() {
    return number;
  }

}
