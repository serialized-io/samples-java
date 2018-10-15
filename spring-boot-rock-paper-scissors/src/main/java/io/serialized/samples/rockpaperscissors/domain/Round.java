package io.serialized.samples.rockpaperscissors.domain;

import java.util.stream.Stream;

/**
 * Represents a Round of Rock, Paper, Scissors.
 */
class Round extends ValueObject {

  static final Round NONE = null;

  private final PlayerHand player1Hand;
  private final PlayerHand player2Hand;

  private Round(PlayerHand player1Hand, PlayerHand player2Hand) {
    this.player1Hand = player1Hand;
    this.player2Hand = player2Hand;
  }

  static Round newRound(String player1, String player2) {
    return new Round(new PlayerHand(player1, Answer.NONE), new PlayerHand(player2, Answer.NONE));
  }

  Round playerAnswered(String playerName, Answer answer) {
    assertHasNotAnswered(playerName);

    PlayerHand player = playerByName(playerName);
    PlayerHand opponent = opponentTo(playerName);
    return new Round(new PlayerHand(player.name, answer), opponent);
  }

  private PlayerHand opponentTo(String name) {
    return Stream.of(player1Hand, player2Hand)
        .filter(p -> !p.name.equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No such player: " + name));
  }

  private void assertHasNotAnswered(String player) {
    if (hasAnswered(playerByName(player))) {
      throw new IllegalStateException("Player " + player + " has already answered");
    }
  }

  private PlayerHand playerByName(String name) {
    return Stream.of(player1Hand, player2Hand).filter(p -> p.name.equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("No such player: " + name));
  }

  RoundResult result() {
    assertIsFinished();
    return player1Hand.calculateResult(player2Hand);
  }

  Answer answerForPlayer(String player) {
    return playerByName(player).answer;
  }

  boolean isFinished() {
    return isCompleted() && !isTied();
  }

  private boolean isCompleted() {
    return hasAnswered(player1Hand) && hasAnswered(player2Hand);
  }

  private boolean hasAnswered(PlayerHand player1Answer) {
    return !player1Answer.answer.equals(Answer.NONE);
  }

  boolean isTied() {
    return hasAnswered(player1Hand) &&
        hasAnswered(player2Hand) &&
        player1Hand.answer.equals(player2Hand.answer);
  }

  private void assertIsFinished() {
    if (!isFinished()) {
      throw new IllegalStateException("Round is not finished. Can't compute winner");
    }
  }

  Round clearAnswers() {
    return new Round(player1Hand.clear(), player2Hand.clear());
  }
}
