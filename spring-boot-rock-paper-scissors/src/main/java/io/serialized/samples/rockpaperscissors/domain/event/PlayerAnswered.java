package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Answer;
import io.serialized.samples.rockpaperscissors.domain.PlayerHand;

import static io.serialized.client.aggregate.Event.newEvent;

public class PlayerAnswered {

  public String player;
  public Answer answer;

  public static Event<PlayerAnswered> playerAnswered(PlayerHand playerHand) {
    PlayerAnswered playerAnswered = new PlayerAnswered();
    playerAnswered.player = playerHand.player.playerName;
    playerAnswered.answer = playerHand.answer;
    return newEvent(playerAnswered).build();
  }

}
