package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;

import static io.serialized.client.aggregates.Event.newEvent;

public class RoundStarted {

  public String player1;
  public String player2;

  public static Event<RoundStarted> roundStarted(String player1, String player2) {
    RoundStarted data = new RoundStarted();
    data.player1 = player1;
    data.player2 = player2;
    return newEvent(data).build();
  }

}
