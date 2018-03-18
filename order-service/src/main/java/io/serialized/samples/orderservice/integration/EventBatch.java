package io.serialized.samples.orderservice.integration;

import java.util.List;
import java.util.UUID;

public class EventBatch {

  public final String aggregateId;
  public final Integer expectedVersion;
  public final List events;

  public EventBatch(UUID aggregateId, List events) {
    this(aggregateId, 0, events);
  }

  public EventBatch(UUID aggregateId, Integer expectedVersion, List events) {
    this.aggregateId = aggregateId.toString();
    this.expectedVersion = expectedVersion;
    this.events = events;
  }

}
