package io.serialized.samples.rockpaperscissors.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.Player;

import java.util.Set;

import static io.serialized.client.aggregate.Event.newEvent;
import static java.util.stream.Collectors.toSet;

public class RoundStarted {

  public Set<String> players;

  public static Event<RoundStarted> roundStarted(Set<Player> players) {
    RoundStarted data = new RoundStarted();
    data.players = players.stream().map(player -> player.playerName).collect(toSet());
    return newEvent(data).build();
  }

}
