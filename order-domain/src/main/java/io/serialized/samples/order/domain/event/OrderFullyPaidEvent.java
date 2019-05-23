package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;

import java.io.Serializable;

public class OrderFullyPaidEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderFullyPaidEvent orderFullyPaid(OrderId orderId, CustomerId customerId) {
    OrderFullyPaidEvent event = new OrderFullyPaidEvent();
    event.data.orderId = orderId.id;
    event.data.customerId = customerId.id;
    return event;
  }

  public static class Data implements Serializable {
    public String orderId;
    public String customerId;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
