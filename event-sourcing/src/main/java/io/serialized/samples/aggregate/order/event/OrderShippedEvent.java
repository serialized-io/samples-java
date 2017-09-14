package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderShippedEvent extends AbstractOrderEvent {

  public Data data = new Data();

  OrderShippedEvent() {
    // Needed for serialization
  }

  public OrderShippedEvent(String trackingNumber) {
    data.trackingNumber = trackingNumber;
  }

  public static class Data implements Serializable {
    public String trackingNumber;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
