package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderState;

import java.io.Serializable;

public class OrderFullyPaidEvent extends OrderEvent {

  public Data data = new Data();

  public static OrderFullyPaidEvent orderFullyPaid(CustomerId customerId) {
    OrderFullyPaidEvent event = new OrderFullyPaidEvent();
    event.data.customerId = customerId.id;
    return event;
  }


  public static class Data implements Serializable {
    public String customerId;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
