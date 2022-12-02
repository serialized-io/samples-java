package io.serialized.samples.orderservice.domain.event;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.Amount;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class PaymentReceived {

  private UUID orderId;
  private UUID customerId;
  private long amountPaid;
  private long receivedAt;
  private long paidAt;

  public static Event<PaymentReceived> paymentReceived(OrderId orderId, CustomerId customerId, Amount amountPaid, long receivedAt) {
    PaymentReceived event = new PaymentReceived();
    event.orderId = orderId.asUUID();
    event.customerId = customerId.asUUID();
    event.amountPaid = amountPaid.amount;
    event.receivedAt = receivedAt;
    event.paidAt = receivedAt;
    return newEvent(event).build();
  }

  public UUID getOrderId() {
    return orderId;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public long getAmountPaid() {
    return amountPaid;
  }

  public long getReceivedAt() {
    return receivedAt;
  }

  public long getPaidAt() {
    return paidAt;
  }

}
