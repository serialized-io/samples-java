package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;

import java.io.Serializable;

public class OrderPlacedEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderPlacedEvent orderPlaced(OrderId orderId, CustomerId customerId, Amount orderAmount) {
    OrderPlacedEvent event = new OrderPlacedEvent();
    event.data.orderId = orderId.id;
    event.data.orderAmount = orderAmount.amount;
    event.data.customerId = customerId.id;
    return event;
  }

  public static class Data implements Serializable {
    public String orderId;
    public String customerId;
    public long orderAmount;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
