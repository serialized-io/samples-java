package io.serialized.samples.rockpaperscissors.domain;

import java.util.Optional;

import static io.serialized.samples.rockpaperscissors.domain.Answer.*;

/**
 * Keeps the result of a game round by saving the winner/loser of the round.
 */
public class Result extends ValueObject {

  private final Player winner;
  private final Player loser;
  private final boolean tied;

  public Result(Player winner, Player loser, boolean tied) {
    this.winner = winner;
    this.loser = loser;
    this.tied = tied;
  }

  public Optional<Player> winner() {
    return Optional.ofNullable(winner);
  }

  public Optional<Player> loser() {
    return Optional.ofNullable(loser);
  }

  public boolean isTied() {
    return tied;
  }

  public static Result calculateResult(PlayerHand hand1, PlayerHand hand2) {

    if (hand1.answer.equals(NONE) || hand2.answer.equals(NONE)) {
      throw new IllegalStateException("Cannot calculate result for missing answer");
    }

    if (hand1.answer.equals(hand2.answer)) {
      return new Result(null, null, true);
    } else {
      if (hand1.answer.equals(ROCK)) {
        return hand2.answer.equals(SCISSORS) ?
            new Result(hand1.player, hand2.player, false) :
            new Result(hand2.player, hand1.player, false);
      } else if (hand1.answer.equals(PAPER)) {
        return hand2.answer.equals(ROCK) ?
            new Result(hand1.player, hand2.player, false) :
            new Result(hand2.player, hand1.player, false);
      } else
        return hand2.answer.equals(PAPER) ?
            new Result(hand1.player, hand2.player, false) :
            new Result(hand2.player, hand1.player, false);
    }
  }
}


