package io.serialized.samples.rockpaperscissors.domain;

/**
 * Represents the hand shown by a player in a round
 */
class PlayerHand extends ValueObject {

  final Player player;
  final Answer answer;

  PlayerHand(Player player, Answer answer) {
    this.player = player;
    this.answer = answer;
  }

  public PlayerHand answer(Answer answer) {
    if (this.answer.equals(Answer.NONE) || answer.equals(this.answer)) {
      return new PlayerHand(player, answer);
    } else {
      throw new IllegalStateException("Player has already answered this round");
    }
  }

}
