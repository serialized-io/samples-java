package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class OrderFullyPaid {

  private UUID orderId;
  private UUID customerId;
  private long paidAt;

  public static Event<OrderFullyPaid> orderFullyPaid(OrderId orderId, CustomerId customerId, long paidAt) {
    OrderFullyPaid event = new OrderFullyPaid();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.paidAt = paidAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public long getPaidAt() {
    return paidAt;
  }

}
