package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class GameFinished {

  public String winner;
  public String loser;

  public static Event<GameFinished> gameFinished(Player winner, Player loser) {
    GameFinished gameFinished = new GameFinished();
    gameFinished.winner = winner.playerName;
    gameFinished.loser = loser.playerName;
    return newEvent(gameFinished).build();
  }

}
