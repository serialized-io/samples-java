package io.serialized.samples.rockpaperscissors.domain;

public enum GameStatus {

  NEW(false), STARTED(true), FINISHED(false);

  private boolean allowsMoreAnswers;

  GameStatus(boolean allowsMoreAnswers) {
    this.allowsMoreAnswers = allowsMoreAnswers;
  }

  void assertAllowsMoreAnswers() {
    if (!allowsMoreAnswers) {
      throw new IllegalStateException("Game is in status " + this + " and don't allow any more answers");
    }
  }

}
