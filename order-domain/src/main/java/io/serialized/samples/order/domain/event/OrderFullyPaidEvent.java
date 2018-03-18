package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.OrderState;

public class OrderFullyPaidEvent extends OrderEvent {

  public static OrderFullyPaidEvent orderFullyPaid() {
    return new OrderFullyPaidEvent();
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
