package io.serialized.samples.infrastructure.order;

import io.serialized.samples.aggregate.order.OrderEventService;
import io.serialized.samples.infrastructure.SerializedEventService;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;
import io.serialized.samples.order.domain.event.OrderEvent;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class SerializedOrderEventService extends SerializedEventService<OrderEvent, OrderState, SerializedOrderEventService.OrderAggregate> implements OrderEventService {

  public SerializedOrderEventService(URI eventStoreUri, String accessKey, String secretAccessKey) {
    super("order", eventStoreUri, accessKey, secretAccessKey, OrderAggregate.class);
  }

  @Override
  public OrderState loadFromEvents(UUID aggregateId, Integer aggregateVersion, List<OrderEvent> events) {
    return OrderState.loadFromEvents(new OrderId(aggregateId.toString()), aggregateVersion, events);
  }

  public static class OrderAggregate extends SerializedEventService.Aggregate<OrderEvent> {
  }

}
