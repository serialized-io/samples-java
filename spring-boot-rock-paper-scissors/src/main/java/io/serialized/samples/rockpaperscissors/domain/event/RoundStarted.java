package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class RoundStarted {

  public String player1;
  public String player2;

  public static Event<RoundStarted> roundStarted(Player player1, Player player2) {
    RoundStarted data = new RoundStarted();
    data.player1 = player1.playerName;
    data.player2 = player2.playerName;
    return newEvent(data).build();
  }

}
