package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderCancelledEvent extends AbstractOrderEvent {

  public Data data = new Data();

  OrderCancelledEvent() {
    // Needed for serialization
  }

  public OrderCancelledEvent(String reason) {
    data.reason = reason;
  }

  public static class Data implements Serializable {
    public String reason;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
