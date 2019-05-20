package io.serialized.samples.rockpaperscissors.domain;

import java.util.stream.Stream;

/**
 * Represents a Round of Rock, Paper, Scissors.
 */
class Round extends ValueObject {

  static final Round NONE = null;

  private final PlayerHand player1;
  private final PlayerHand player2;

  private Round(PlayerHand player1, PlayerHand player2) {
    this.player1 = player1;
    this.player2 = player2;
  }

  static Round newRound(Player player1, Player player2) {
    return new Round(player1.newHand(), player2.newHand());
  }

  Round playerAnswered(Player player, Answer answer) {
    PlayerHand hand = getHandFor(player).answer(answer);
    PlayerHand opponentHand = opponentTo(player);
    return new Round(hand, opponentHand);
  }

  Result calculateResult() {
    return Result.calculateResult(player1, player2);
  }

  private PlayerHand getHandFor(Player player) {
    return Stream.of(player1, player2)
        .filter(hand -> hand.player.equals(player))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Could not find player " + player.playerName));
  }

  private PlayerHand opponentTo(Player player) {
    return Stream.of(player1, player2)
        .filter(p -> !p.player.equals(player))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such player: " + player.playerName));
  }

  Result result() {
    assertIsFinished();
    return calculateResult();
  }

  boolean isFinished() {
    return isCompleted() && !isTied();
  }

  private boolean isCompleted() {
    return !player1.answer.equals(Answer.NONE) && !player2.answer.equals(Answer.NONE);
  }


  boolean isTied() {
    return isCompleted() && player1.answer.equals(player2.answer);
  }

  private void assertIsFinished() {
    if (!isFinished()) {
      throw new IllegalStateException("Round is not finished. Can't compute winner");
    }
  }

  Round clearAnswers() {
    return new Round(player1, player2);
  }

  public boolean hasPlayerAnswered(Player player) {
    return !getHandFor(player).answer.equals(Answer.NONE);
  }
}
