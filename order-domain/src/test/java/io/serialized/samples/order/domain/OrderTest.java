package io.serialized.samples.order.domain;

import io.serialized.samples.order.domain.event.*;
import org.junit.Test;

import java.util.List;

import static com.google.common.base.Predicates.instanceOf;
import static io.serialized.samples.order.domain.CustomerId.newCustomer;
import static io.serialized.samples.order.domain.Order.createNewOrder;
import static io.serialized.samples.order.domain.OrderId.newOrderId;
import static io.serialized.samples.order.domain.event.OrderFullyPaidEvent.orderFullyPaid;
import static io.serialized.samples.order.domain.event.OrderPlacedEvent.orderPlaced;
import static io.serialized.samples.order.domain.event.PaymentReceivedEvent.paymentReceived;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderTest {

  @Test
  public void placeNewOrderGeneratesEvent() {
    Order order = createNewOrder(newCustomer());
    OrderPlacedEvent placedEvent = order.place(new Amount(200));
    assertThat(placedEvent.data.orderAmount, is(200L));
  }

  @Test
  public void payCorrectAmount() {

    CustomerId customerId = newCustomer();
    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(customerId, new Amount(200)))
        .build();

    Order order = new Order(customerId, state.orderStatus, state.orderAmount);
    List<OrderEvent> events = order.pay(new Amount(200));

    assertThat(events.stream().filter(instanceOf(PaymentReceivedEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().filter(instanceOf(OrderFullyPaidEvent.class)::apply).count(), is(1L));
  }

  @Test
  public void payWithExceedingAmount() {

    CustomerId customerId = newCustomer();
    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(customerId, new Amount(200)))
        .build();

    Order order = new Order(customerId, state.orderStatus, state.orderAmount);
    List<OrderEvent> events = order.pay(new Amount(500));

    assertThat(events.stream().filter(instanceOf(PaymentReceivedEvent.class)::apply).count(), is(1L));
    assertThat(events.stream().filter(instanceOf(OrderFullyPaidEvent.class)::apply).count(), is(1L));
  }

  @Test(expected = IllegalOrderStateException.class)
  public void cannotPayNewOrder() {

    OrderState state = OrderState.builder(newOrderId()).build();

    Order order = new Order(newCustomer(), state.orderStatus, state.orderAmount);
    order.pay(new Amount(200));
  }

  @Test(expected = IllegalOrderStateException.class)
  public void cannotShipUnpaidOrder() {

    CustomerId customerId = newCustomer();
    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(customerId, new Amount(200)))
        .build();

    Order order = new Order(customerId, state.orderStatus, state.orderAmount);
    order.ship(TrackingNumber.newTrackingNumber());
  }

  @Test(expected = IllegalOrderStateException.class)
  public void cannotCancelOrderNotPlaced() {

    OrderState state = OrderState.builder(newOrderId())
        .build();

    Order order = new Order(newCustomer(), state.orderStatus, state.orderAmount);
    order.cancel("DOA");
  }

  @Test
  public void canCancelPlacedOrder() {

    CustomerId customerId = newCustomer();
    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(customerId, new Amount(200)))
        .build();

    Order order = new Order(customerId, state.orderStatus, state.orderAmount);
    String reason = "DOA";
    OrderCancelledEvent cancelledEvent = order.cancel(reason);
    assertThat(cancelledEvent.data.reason, is(reason));
  }

  @Test
  public void canShipPaidOrder() {

    CustomerId customerId = newCustomer();
    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(customerId, new Amount(200)))
        .apply(paymentReceived(customerId, new Amount(200L)))
        .apply(orderFullyPaid(customerId))
        .build();

    Order order = new Order(customerId, state.orderStatus, state.orderAmount);
    TrackingNumber trackingNumber = TrackingNumber.newTrackingNumber();
    OrderShippedEvent shippedEvent = order.ship(trackingNumber);
    assertThat(shippedEvent.data.trackingNumber, is(trackingNumber.trackingNumber));
  }

}