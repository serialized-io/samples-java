package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;
import io.serialized.samples.order.domain.TrackingNumber;

import java.io.Serializable;

public class OrderShippedEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderShippedEvent orderShipped(OrderId orderId, CustomerId customerId, TrackingNumber trackingNumber) {
    OrderShippedEvent event = new OrderShippedEvent();
    event.data.orderId = orderId.id;
    event.data.customerId = customerId.id;
    event.data.trackingNumber = trackingNumber.trackingNumber;
    return event;
  }

  public static class Data implements Serializable {
    public String orderId;
    public String customerId;
    public String trackingNumber;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
