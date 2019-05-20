package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Answer;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class RoundTied {

  public Answer answer;
  public String player1;
  public String player2;

  public static Event<RoundTied> roundTied(Answer answer, Player player1, Player player2) {
    RoundTied roundTied = new RoundTied();
    roundTied.answer = answer;
    roundTied.player1 = player1.playerName;
    roundTied.player2 = player2.playerName;
    return newEvent(roundTied).build();
  }

}
