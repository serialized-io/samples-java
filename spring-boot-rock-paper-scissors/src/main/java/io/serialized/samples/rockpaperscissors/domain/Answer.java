package io.serialized.samples.rockpaperscissors.domain;

import static io.serialized.samples.rockpaperscissors.domain.Result.LOSE;
import static io.serialized.samples.rockpaperscissors.domain.Result.TIE;

public enum Answer {

  NONE(false), ROCK(true), PAPER(true), SCISSORS(true);

  private boolean validAnswer;

  Answer(boolean validAnswer) {
    this.validAnswer = validAnswer;
  }

  Result calculateResult(Answer other) {
    if (this.equals(other)) {
      return TIE;
    } else {
      if (this.equals(ROCK)) {
        return other.equals(SCISSORS) ? Result.WIN : Result.LOSE;
      } else if (this.equals(PAPER)) {
        return other.equals(ROCK) ? Result.WIN : Result.LOSE;
      } else if (this.equals(SCISSORS)) {
        return other.equals(PAPER) ? Result.WIN : Result.LOSE;
      } else {
        return LOSE;
      }
    }
  }

  public void assertValid() {
    if (!validAnswer) {
      throw new IllegalStateException("Answer " + this + " is not a valid player answer");
    }
  }
}
