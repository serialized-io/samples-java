package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.Amount;
import io.serialized.samples.aggregate.order.CustomerId;
import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderPlacedEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderPlacedEvent orderPlaced(CustomerId customerId, Amount orderAmount) {
    OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
    orderPlacedEvent.data.orderAmount = orderAmount.amount;
    orderPlacedEvent.data.customerId = customerId.id;
    return orderPlacedEvent;
  }

  public static class Data implements Serializable {
    public String customerId;
    public long orderAmount;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
