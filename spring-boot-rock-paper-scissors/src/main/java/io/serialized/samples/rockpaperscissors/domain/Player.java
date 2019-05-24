package io.serialized.samples.rockpaperscissors.domain;

public class Player extends ValueObject {

  public final String playerName;

  public Player(String playerName) {
    this.playerName = playerName;
  }

  public static Player fromString(String playerName) {
    return new Player(playerName);
  }

}
