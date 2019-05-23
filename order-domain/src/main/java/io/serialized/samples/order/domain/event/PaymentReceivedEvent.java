package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.OrderState;

import java.io.Serializable;

public class PaymentReceivedEvent extends OrderEvent {

  public Data data = new Data();

  public static PaymentReceivedEvent paymentReceived(OrderId orderId, CustomerId customerId, Amount amountPaid) {
    PaymentReceivedEvent event = new PaymentReceivedEvent();
    event.data.orderId = orderId.id;
    event.data.customerId = customerId.id;
    event.data.amountPaid = amountPaid.amount;
    return event;
  }

  public static class Data implements Serializable {
    public String orderId;
    public String customerId;
    public long amountPaid;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
