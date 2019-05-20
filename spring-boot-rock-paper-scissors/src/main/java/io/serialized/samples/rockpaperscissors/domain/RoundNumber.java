package io.serialized.samples.rockpaperscissors.domain;

import java.util.Arrays;

public enum RoundNumber {

  ONE(1), TWO(2), THREE(3);

  private int number;

  RoundNumber(int number) {
    this.number = number;
  }

  public RoundNumber next() {
    return Arrays.stream(RoundNumber.values()).filter(r -> r.number == this.number + 1)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Cannot find next round"));
  }

}
