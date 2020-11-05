package io.serialized.samples.guessinggame.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class HintAdded {

  private UUID gameId;
  private String hint;
  private long addedAt;

  public static Event<HintAdded> hintAdded(UUID gameId, String hint, long addedAt) {
    HintAdded event = new HintAdded();
    event.gameId = gameId;
    event.hint = hint;
    event.addedAt = addedAt;
    return newEvent(event).build();
  }

  public String getHint() {
    return hint;
  }

}
