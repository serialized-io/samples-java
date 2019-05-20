package io.serialized.samples.rockpaperscissors.domain;

public class Player extends ValueObject {

  public final String playerName;

  public Player(String playerName) {
    this.playerName = playerName;
  }

  public PlayerHand newHand() {
    return new PlayerHand(this, Answer.NONE);
  }

  public static Player fromString(String playerName) {
    return new Player(playerName);
  }

}
