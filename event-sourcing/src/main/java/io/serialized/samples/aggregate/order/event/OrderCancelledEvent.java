package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderCancelledEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderCancelledEvent orderCancelled(String reason) {
    OrderCancelledEvent event = new OrderCancelledEvent();
    event.data.reason = reason;
    return event;
  }

  public static class Data implements Serializable {
    public String reason;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
