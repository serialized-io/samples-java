package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregates.Event;
import io.serialized.samples.rockpaperscissors.domain.GameScore;

import static io.serialized.client.aggregates.Event.newEvent;

public class GameFinished {

  public String winner;

  public static Event<GameFinished> gameFinished(GameScore gameScore) {
    GameFinished gameFinished = new GameFinished();
    gameFinished.winner = gameScore.winner().orElseThrow(() -> new IllegalStateException("No winner was found"));
    return newEvent(gameFinished).build();
  }

}
