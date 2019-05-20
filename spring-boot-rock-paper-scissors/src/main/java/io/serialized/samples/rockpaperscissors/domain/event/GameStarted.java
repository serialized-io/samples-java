package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class GameStarted {

  public String player1;
  public String player2;

  public static Event<GameStarted> gameStarted(Player player1, Player player2) {
    GameStarted gameStarted = new GameStarted();
    gameStarted.player1 = player1.playerName;
    gameStarted.player2 = player2.playerName;
    return newEvent(gameStarted).build();
  }

}
