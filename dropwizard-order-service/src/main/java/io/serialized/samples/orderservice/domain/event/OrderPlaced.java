package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.Amount;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class OrderPlaced {

  private UUID orderId;
  private UUID customerId;
  private String sku;
  private long orderAmount;
  private long placedAt;

  public static Event<OrderPlaced> orderPlaced(OrderId orderId, CustomerId customerId, String sku, Amount orderAmount, long placedAt) {
    OrderPlaced event = new OrderPlaced();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.sku = sku;
    event.orderAmount = orderAmount.amount;
    event.placedAt = placedAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public String getSku() {
    return sku;
  }

  public long getOrderAmount() {
    return orderAmount;
  }

  public long getPlacedAt() {
    return placedAt;
  }

}
