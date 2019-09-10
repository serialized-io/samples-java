package io.serialized.samples.orderservice.integration;

import java.util.List;

public class EventBatch {

  public final Integer expectedVersion;
  public final List events;

  public EventBatch(List events) {
    this(0, events);
  }

  public EventBatch(Integer expectedVersion, List events) {
    this.expectedVersion = expectedVersion;
    this.events = events;
  }

}
