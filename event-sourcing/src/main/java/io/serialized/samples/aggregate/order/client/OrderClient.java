package io.serialized.samples.aggregate.order.client;

import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.event.AbstractOrderEvent;

import java.util.List;
import java.util.UUID;

public interface OrderClient {

  void saveEvent(String aggregateId, Integer expectedVersion, AbstractOrderEvent event);

  OrderState load(UUID aggregateId);

  class Aggregate {
    public String aggregateId;
    public String aggregateType;
    public Integer aggregateVersion;
    public List<AbstractOrderEvent> events;
  }

  class EventBatch {
    public String aggregateId;
    public Integer expectedVersion;
    public List<AbstractOrderEvent> events;

    public static EventBatch newEventBatch(String aggregateId, Integer expectedVersion, List<AbstractOrderEvent> events) {
      EventBatch eventBatch = new EventBatch();
      eventBatch.aggregateId = aggregateId;
      eventBatch.expectedVersion = expectedVersion;
      eventBatch.events = events;
      return eventBatch;
    }
  }

}
