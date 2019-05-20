package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.GameScore;

import static io.serialized.client.aggregate.Event.newEvent;

public class GameFinished {

  public String winner;

  public static Event<GameFinished> gameFinished(GameScore gameScore) {
    GameFinished gameFinished = new GameFinished();
    gameFinished.winner = gameScore.winner().map(p -> p.playerName).orElseThrow(() -> new IllegalStateException("No winner was found"));
    return newEvent(gameFinished).build();
  }

}
