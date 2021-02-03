package io.serialized.samples.guessinggame.api;

import java.util.Date;
import java.util.List;

public class GameHistoryProjection {

  public List<Round> rounds;

  public static class Round {
    int guess;
    long guessedAt;

    public int getGuess() {
      return guess;
    }

    public String getTimestamp() {
      return new Date(guessedAt).toString();
    }

  }

}
