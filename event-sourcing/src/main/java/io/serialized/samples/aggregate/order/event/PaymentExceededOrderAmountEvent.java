package io.serialized.samples.aggregate.order.event;

import io.serialized.samples.aggregate.order.Amount;
import io.serialized.samples.aggregate.order.OrderState;

import java.io.Serializable;

public class PaymentExceededOrderAmountEvent extends OrderEvent {

  public Data data = new Data();

  public static PaymentExceededOrderAmountEvent paymentExceededOrderAmount(Amount exceedingAmount) {
    PaymentExceededOrderAmountEvent event = new PaymentExceededOrderAmountEvent();
    event.data.exceedingAmount = exceedingAmount.amount;
    return event;
  }

  public static class Data implements Serializable {
    public long exceedingAmount;
  }

  @Override
  public void apply(OrderState.Builder builder) {
    builder.apply(this);
  }

}
