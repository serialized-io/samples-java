package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

public class OrderPaidEvent extends AbstractOrderEvent {

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
