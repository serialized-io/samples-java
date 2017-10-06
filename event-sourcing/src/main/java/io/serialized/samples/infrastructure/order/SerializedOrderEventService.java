package io.serialized.samples.infrastructure.order;

import io.serialized.samples.aggregate.order.OrderEventService;
import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.event.OrderEvent;
import io.serialized.samples.infrastructure.SerializedEventService;

import java.net.URI;
import java.util.List;

public class SerializedOrderEventService extends SerializedEventService<OrderEvent, OrderState, SerializedOrderEventService.OrderAggregate> implements OrderEventService {

  public SerializedOrderEventService(URI eventStoreUri, String accessKey, String secretAccessKey) {
    super("order", eventStoreUri, accessKey, secretAccessKey, OrderAggregate.class);
  }

  @Override
  public OrderState loadFromEvents(String aggregateId, Integer aggregateVersion, List<OrderEvent> events) {
    return OrderState.loadFromEvents(aggregateId, aggregateVersion, events);
  }

  public static class OrderAggregate extends SerializedEventService.Aggregate<OrderEvent> {
  }

}
