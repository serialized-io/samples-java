package io.serialized.samples.rockpaperscissors.domain;

/**
 * Keeps the result of a game round by saving the winner/loser of the round.
 */
public class RoundResult extends ValueObject {

  public final String winner;
  public final String loser;

  RoundResult(String winner, String loser) {
    this.winner = winner;
    this.loser = loser;
  }

  String winner() {
    return winner;
  }

  String loser() {
    return loser;
  }

}
