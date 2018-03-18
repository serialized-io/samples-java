package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.OrderState;

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
