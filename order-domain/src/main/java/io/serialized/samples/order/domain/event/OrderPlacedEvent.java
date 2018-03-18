package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderState;

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
