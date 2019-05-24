package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class RoundFinished {

  public String winner;
  public String loser;

  public static Event<RoundFinished> roundFinished(Player winner, Player loser) {
    RoundFinished roundFinished = new RoundFinished();
    roundFinished.winner = winner.playerName;
    roundFinished.loser = loser.playerName;
    return newEvent(roundFinished).build();
  }

}
