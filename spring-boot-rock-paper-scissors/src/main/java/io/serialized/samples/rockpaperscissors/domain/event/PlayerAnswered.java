package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;
import io.serialized.samples.rockpaperscissors.domain.Answer;

import static io.serialized.client.aggregates.Event.newEvent;

public class PlayerAnswered {

  public String player;
  public Answer answer;

  public static Event<PlayerAnswered> playerAnswered(String player, Answer answer) {
    PlayerAnswered playerAnswered = new PlayerAnswered();
    playerAnswered.player = player;
    playerAnswered.answer = answer;
    return newEvent(playerAnswered).build();
  }

}
