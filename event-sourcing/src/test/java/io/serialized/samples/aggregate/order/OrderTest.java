package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.*;
import org.junit.Test;

import java.util.List;

import static com.google.common.base.Predicates.instanceOf;
import static io.serialized.samples.aggregate.order.CustomerId.newCustomer;
import static io.serialized.samples.aggregate.order.Order.createNewOrder;
import static io.serialized.samples.aggregate.order.OrderId.newOrderId;
import static io.serialized.samples.aggregate.order.event.OrderFullyPaidEvent.orderFullyPaid;
import static io.serialized.samples.aggregate.order.event.OrderPlacedEvent.orderPlaced;
import static io.serialized.samples.aggregate.order.event.PaymentReceivedEvent.paymentReceived;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderTest {

  @Test
  public void placeNewOrderGeneratesEvent() {
    Order order = createNewOrder();
    OrderPlacedEvent placedEvent = order.place(newCustomer(), new Amount(200));
    assertThat(placedEvent.data.orderAmount, is(200L));
  }

  @Test
  public void payCorrectAmount() {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    List<OrderEvent> events = order.pay(new Amount(200));

    assertThat(events.stream().filter(instanceOf(PaymentReceivedEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().filter(instanceOf(OrderFullyPaidEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().anyMatch(instanceOf(PaymentExceededOrderAmountEvent.class)::apply), is(false));
  }

  @Test
  public void payWithExceedingAmount() {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    List<OrderEvent> events = order.pay(new Amount(500));

    assertThat(events.stream().filter(instanceOf(PaymentReceivedEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().filter(instanceOf(PaymentExceededOrderAmountEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().filter(instanceOf(OrderFullyPaidEvent.class)::apply).count(), is(1L));
  }

  @Test(expected = IllegalStateException.class)
  public void cannotPayNewOrder() {

    OrderState state = OrderState.builder(newOrderId()).build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    order.pay(new Amount(200));
  }

  @Test(expected = IllegalStateException.class)
  public void cannotShipUnpaidOrder() {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    order.ship(TrackingNumber.newTrackingNumber());
  }

  @Test
  public void canShipPaidOrder() {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .apply(paymentReceived(new Amount(200L)))
        .apply(orderFullyPaid())
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    TrackingNumber trackingNumber = TrackingNumber.newTrackingNumber();
    OrderShippedEvent shippedEvent = order.ship(trackingNumber);
    assertThat(shippedEvent.data.trackingNumber, is(trackingNumber.trackingNumber));
  }

}