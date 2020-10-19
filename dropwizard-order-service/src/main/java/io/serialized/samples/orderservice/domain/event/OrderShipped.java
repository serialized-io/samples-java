package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;
import io.serialized.samples.orderservice.domain.TrackingNumber;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class OrderShipped {

  private UUID orderId;
  private UUID customerId;
  private TrackingNumber trackingNumber;
  private long shippedAt;

  public static Event<OrderShipped> orderShipped(OrderId orderId, CustomerId customerId, TrackingNumber trackingNumber, long shippedAt) {
    OrderShipped event = new OrderShipped();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.trackingNumber = trackingNumber;
    event.shippedAt = shippedAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public TrackingNumber getTrackingNumber() {
    return trackingNumber;
  }

  public long getShippedAt() {
    return shippedAt;
  }

}
