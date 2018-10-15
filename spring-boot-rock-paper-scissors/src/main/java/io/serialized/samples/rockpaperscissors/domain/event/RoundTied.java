package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;
import io.serialized.samples.rockpaperscissors.domain.Answer;

import static io.serialized.client.aggregates.Event.newEvent;

public class RoundTied {

  public Answer answer;
  public String player1;
  public String player2;

  public static Event<RoundTied> roundTied(Answer answer, String player1, String player2) {
    RoundTied roundTied = new RoundTied();
    roundTied.answer = answer;
    roundTied.player1 = player1;
    roundTied.player2 = player2;
    return newEvent(roundTied).build();
  }

}
