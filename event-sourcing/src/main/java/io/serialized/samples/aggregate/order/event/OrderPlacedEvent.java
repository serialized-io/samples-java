package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class OrderPlacedEvent extends AbstractOrderEvent {

  public Data data = new Data();

  OrderPlacedEvent() {
    // Needed for serialization
  }

  public OrderPlacedEvent(String customerId, long orderAmount) {
    data.customerId = customerId;
    data.orderAmount = orderAmount;
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
