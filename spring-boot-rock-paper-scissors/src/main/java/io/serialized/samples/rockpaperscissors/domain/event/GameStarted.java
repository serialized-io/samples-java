package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;

import static io.serialized.client.aggregates.Event.newEvent;

public class GameStarted {

  public String player1;
  public String player2;

  public static Event<GameStarted> gameStarted(String player1, String player2) {
    GameStarted gameStarted = new GameStarted();
    gameStarted.player1 = player1;
    gameStarted.player2 = player2;
    return newEvent(gameStarted).build();
  }

}
