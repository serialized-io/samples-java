package io.serialized.samples.aggregate.order.store;

import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.event.OrderEvent;

import java.net.URI;
import java.util.List;

public class OrderEventStore extends SerializedEventStore<OrderEvent, OrderState, OrderEventStore.OrderAggregate> {

  public OrderEventStore(URI eventStoreUri, String accessKey, String secretAccessKey) {
    super("order", eventStoreUri, accessKey, secretAccessKey, OrderAggregate.class);
  }

  @Override
  protected OrderState loadFromEvents(String aggregateId, Integer aggregateVersion, List<OrderEvent> events) {
    return OrderState.loadFromEvents(aggregateId, aggregateVersion, events);
  }

  public static class OrderAggregate extends SerializedEventStore.Aggregate<OrderEvent> {
  }

}
