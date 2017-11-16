package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

public class OrderFullyPaidEvent extends OrderEvent {

  public static OrderFullyPaidEvent orderFullyPaid() {
    return new OrderFullyPaidEvent();
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
