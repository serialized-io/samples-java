package io.serialized.samples.rockpaperscissors.domain;

/**
 * Represents the hand shown by a player in a round
 */
public class PlayerHand extends ValueObject {

  public final Player player;
  public final Answer answer;

  PlayerHand(Player player, Answer answer) {
    this.player = player;
    this.answer = answer;
  }

}
