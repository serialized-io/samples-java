package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;
import io.serialized.samples.rockpaperscissors.domain.RoundResult;

import static io.serialized.client.aggregates.Event.newEvent;

public class RoundFinished {

  public String winner;
  public String loser;

  public static Event<RoundFinished> roundFinished(RoundResult result) {
    RoundFinished roundFinished = new RoundFinished();
    roundFinished.winner = result.winner;
    roundFinished.loser = result.loser;
    return newEvent(roundFinished).build();
  }

}
