package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Result;

import static io.serialized.client.aggregate.Event.newEvent;

public class RoundFinished {

  public String winner;
  public String loser;

  public static Event<RoundFinished> roundFinished(Result result) {
    RoundFinished roundFinished = new RoundFinished();
    roundFinished.winner = result.winner().get().playerName;
    roundFinished.loser = result.loser().get().playerName;
    return newEvent(roundFinished).build();
  }

}
