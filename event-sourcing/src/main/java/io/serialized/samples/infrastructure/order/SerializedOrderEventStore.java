package io.serialized.samples.infrastructure.order;

import io.serialized.samples.aggregate.order.OrderEventStore;
import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.event.OrderEvent;
import io.serialized.samples.infrastructure.SerializedEventStore;

import java.net.URI;
import java.util.List;

public class SerializedOrderEventStore extends SerializedEventStore<OrderEvent, OrderState, SerializedOrderEventStore.OrderAggregate> implements OrderEventStore {

  public SerializedOrderEventStore(URI eventStoreUri, String accessKey, String secretAccessKey) {
    super("order", eventStoreUri, accessKey, secretAccessKey, OrderAggregate.class);
  }

  @Override
  public OrderState loadFromEvents(String aggregateId, Integer aggregateVersion, List<OrderEvent> events) {
    return OrderState.loadFromEvents(aggregateId, aggregateVersion, events);
  }

  public static class OrderAggregate extends SerializedEventStore.Aggregate<OrderEvent> {
  }

}
