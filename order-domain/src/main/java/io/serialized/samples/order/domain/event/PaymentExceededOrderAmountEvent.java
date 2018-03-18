package io.serialized.samples.order.domain.event;

import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.OrderState;

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
