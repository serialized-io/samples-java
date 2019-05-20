package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Answer;
import io.serialized.samples.rockpaperscissors.domain.Player;

import static io.serialized.client.aggregate.Event.newEvent;

public class PlayerAnswered {

  public String player;
  public Answer answer;

  public static Event<PlayerAnswered> playerAnswered(Player player, Answer answer) {
    PlayerAnswered playerAnswered = new PlayerAnswered();
    playerAnswered.player = player.playerName;
    playerAnswered.answer = answer;
    return newEvent(playerAnswered).build();
  }

}
