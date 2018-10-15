package io.serialized.samples.rockpaperscissors.query;

import io.serialized.samples.rockpaperscissors.domain.ValueObject;

public class HighScore extends ValueObject {
  public String playerName;
  public int wins;
}
