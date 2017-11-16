package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.Amount;
import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class PaymentReceivedEvent extends OrderEvent {

  public Data data = new Data();

  public static PaymentReceivedEvent paymentReceived(Amount amountPaid) {
    PaymentReceivedEvent event = new PaymentReceivedEvent();
    event.data.amountPaid = amountPaid.amount;
    return event;
  }

  public static class Data implements Serializable {
    public long amountPaid;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
