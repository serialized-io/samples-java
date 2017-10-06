package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;
import io.serialized.samples.aggregate.order.TrackingNumber;

import java.io.Serializable;

public class OrderShippedEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderShippedEvent orderShipped(TrackingNumber trackingNumber) {
    OrderShippedEvent event = new OrderShippedEvent();
    event.data.trackingNumber = trackingNumber.trackingNumber;
    return event;
  }

  public static class Data implements Serializable {
    public String trackingNumber;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
