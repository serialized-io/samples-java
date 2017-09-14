package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderPaidEvent extends AbstractOrderEvent {

  public Data data = new Data();

  OrderPaidEvent() {
    // Needed for serialization
  }

  public OrderPaidEvent(long amount) {
    data.amount = amount;
  }

  public static class Data implements Serializable {
    public long amount;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
