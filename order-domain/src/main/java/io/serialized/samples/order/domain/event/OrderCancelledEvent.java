package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderState;

import java.io.Serializable;

public class OrderCancelledEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderCancelledEvent orderCancelled(CustomerId customerId, Amount orderAmount, String reason) {
    OrderCancelledEvent event = new OrderCancelledEvent();
    event.data.customerId = customerId.id;
    event.data.orderAmount = orderAmount.amount;
    event.data.reason = reason;
    return event;
  }

  public static class Data implements Serializable {
    public String customerId;
    public long orderAmount;
    public String reason;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
