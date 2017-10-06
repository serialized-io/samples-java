package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.Amount;
import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderPaidEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderPaidEvent orderPaid(Amount paidAmount) {
    OrderPaidEvent event = new OrderPaidEvent();
    event.data.amount = paidAmount.amount;
    return event;
  }

  public static class Data implements Serializable {
    public long amount;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
