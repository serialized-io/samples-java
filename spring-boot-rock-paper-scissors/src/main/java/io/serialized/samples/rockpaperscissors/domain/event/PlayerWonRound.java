package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class PlayerWonRound {

  public String winner;

  public static Event<PlayerWonRound> playerWonRound(Player winner) {
    PlayerWonRound playerWonRound = new PlayerWonRound();
    playerWonRound.winner = winner.playerName;
    return newEvent(playerWonRound).build();
  }

}
