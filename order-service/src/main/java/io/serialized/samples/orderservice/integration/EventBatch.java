package io.serialized.samples.orderservice.integration;

import io.serialized.samples.order.domain.Id;

import java.util.List;

public class EventBatch {

  public final String aggregateId;
  public final Integer expectedVersion;
  public final List events;

  public EventBatch(Id aggregateId, List events) {
    this(aggregateId, 0, events);
  }

  public EventBatch(Id aggregateId, Integer expectedVersion, List events) {
    this.aggregateId = aggregateId.id;
    this.expectedVersion = expectedVersion;
    this.events = events;
  }

}
