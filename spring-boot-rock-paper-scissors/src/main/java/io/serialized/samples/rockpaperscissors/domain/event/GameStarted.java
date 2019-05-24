package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import java.util.Set;

import static io.serialized.client.aggregate.Event.newEvent;
import static java.util.stream.Collectors.toSet;

public class GameStarted {

  public Set<String> players;

  public static Event<GameStarted> gameStarted(Set<Player> players) {
    GameStarted gameStarted = new GameStarted();
    gameStarted.players = players.stream().map(player -> player.playerName).collect(toSet());
    return newEvent(gameStarted).build();
  }

}
