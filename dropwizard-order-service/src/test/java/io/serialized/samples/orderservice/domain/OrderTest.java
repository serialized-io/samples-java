package io.serialized.samples.orderservice.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.StateBuilder;
import io.serialized.samples.orderservice.domain.event.OrderCanceled;
import io.serialized.samples.orderservice.domain.event.OrderFullyPaid;
import io.serialized.samples.orderservice.domain.event.OrderPlaced;
import io.serialized.samples.orderservice.domain.event.OrderShipped;
import io.serialized.samples.orderservice.domain.event.PaymentReceived;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.serialized.samples.orderservice.domain.event.OrderFullyPaid.orderFullyPaid;
import static io.serialized.samples.orderservice.domain.event.OrderPlaced.orderPlaced;
import static io.serialized.samples.orderservice.domain.event.PaymentReceived.paymentReceived;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

  private final StateBuilder<OrderState> orderStateBuilder = StateBuilder.stateBuilder(OrderState.class)
      .withHandler(OrderPlaced.class, OrderState::handleOrderPlaced)
      .withHandler(OrderCanceled.class, OrderState::handleOrderCanceled)
      .withHandler(OrderFullyPaid.class, OrderState::handleOrderFullyPaid)
      .withHandler(PaymentReceived.class, OrderState::handlePaymentReceived)
      .withHandler(OrderShipped.class, OrderState::handleOrderShipped);

  @Test
  public void placeNewOrderGeneratesEvent() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();
    Order order = new Order(new OrderState());
    List<Event<?>> events = order.place(orderId, customerId, "abc123", new Amount(200));
    Event<OrderPlaced> orderPlaced = (Event<OrderPlaced>) events.iterator().next();
    assertThat(orderPlaced.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(orderPlaced.data().getCustomerId()).isEqualTo(customerId.asUUID());
    assertThat(orderPlaced.data().getSku()).isEqualTo("abc123");
    assertThat(orderPlaced.data().getOrderAmount()).isEqualTo(200L);
  }

  @Test
  public void payCorrectAmount() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();

    Order order = new Order(orderStateBuilder.buildState(singletonList(
        orderPlaced(orderId, customerId, "abc123", new Amount(200), currentTimeMillis())
    )));

    List<Event<?>> events = order.pay(new Amount(200));

    Event<PaymentReceived> paymentReceived = firstEventOfType(events, PaymentReceived.class);
    assertThat(paymentReceived.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(paymentReceived.data().getCustomerId()).isEqualTo(customerId.asUUID());
    assertThat(paymentReceived.data().getAmountPaid()).isEqualTo(new Amount(200).amount);

    Event<OrderFullyPaid> orderPaid = firstEventOfType(events, OrderFullyPaid.class);
    assertThat(orderPaid.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(orderPaid.data().getCustomerId()).isEqualTo(customerId.asUUID());
  }

  @Test
  public void payWithExceedingAmount() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();

    Order order = new Order(orderStateBuilder.buildState(singletonList(
        orderPlaced(orderId, customerId, "abc123", new Amount(200), currentTimeMillis())
    )));

    List<Event<?>> events = order.pay(new Amount(500));

    Event<PaymentReceived> paymentReceived = firstEventOfType(events, PaymentReceived.class);
    assertThat(paymentReceived.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(paymentReceived.data().getCustomerId()).isEqualTo(customerId.asUUID());
    assertThat(paymentReceived.data().getAmountPaid()).isEqualTo(new Amount(500).amount);

    Event<OrderFullyPaid> orderPaid = firstEventOfType(events, OrderFullyPaid.class);
    assertThat(orderPaid.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(orderPaid.data().getCustomerId()).isEqualTo(customerId.asUUID());
  }

  @Test
  public void cannotPayNewOrder() {

    Order order = new Order(new OrderState());

    // when
    Throwable exception = assertThrows(IllegalOrderStateException.class, () -> {
      order.pay(new Amount(200));
    });

    // then
    assertThat(exception.getMessage()).isEqualTo("Expected order to be PLACED but was NEW");
  }

  @Test
  public void cannotShipUnpaidOrder() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();

    Order order = new Order(orderStateBuilder.buildState(singletonList(
        orderPlaced(orderId, customerId, "abc123", new Amount(200), currentTimeMillis())
    )));

    // when
    Throwable exception = assertThrows(IllegalOrderStateException.class, () -> {
      order.ship(TrackingNumber.newTrackingNumber());
    });

    // then
    assertThat(exception.getMessage()).isEqualTo("Expected order to be PAID but was PLACED");
  }

  @Test
  public void cannotCancelOrderNotPlaced() {

    Order order = new Order(new OrderState());

    // when
    Throwable exception = assertThrows(IllegalOrderStateException.class, () -> {
      order.cancel("DOA");
    });

    // then
    assertThat(exception.getMessage()).isEqualTo("Expected order to be PLACED but was NEW");
  }

  @Test
  public void canCancelPlacedOrder() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();

    Order order = new Order(orderStateBuilder.buildState(singletonList(
        orderPlaced(orderId, customerId, "abc123", new Amount(200), currentTimeMillis())
    )));

    String reason = "DOA";

    List<Event<?>> events = order.cancel(reason);

    Event<OrderCanceled> orderCanceled = firstEventOfType(events, OrderCanceled.class);
    assertThat(orderCanceled.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(orderCanceled.data().getCustomerId()).isEqualTo(customerId.asUUID());
    assertThat(orderCanceled.data().getReason()).isEqualTo("DOA");
  }

  @Test
  public void canShipPaidOrder() {

    OrderId orderId = OrderId.newId();
    CustomerId customerId = CustomerId.newId();

    Amount orderAmount = new Amount(200);
    Order order = new Order(orderStateBuilder.buildState(asList(
        orderPlaced(orderId, customerId, "abc123", orderAmount, currentTimeMillis()),
        paymentReceived(orderId, customerId, orderAmount, currentTimeMillis()),
        orderFullyPaid(orderId, customerId, orderAmount, currentTimeMillis())
    )));

    TrackingNumber trackingNumber = TrackingNumber.newTrackingNumber();
    List<Event<?>> events = order.ship(trackingNumber);

    Event<OrderShipped> orderShipped = firstEventOfType(events, OrderShipped.class);
    assertThat(orderShipped.data().getOrderId()).isEqualTo(orderId.asUUID());
    assertThat(orderShipped.data().getCustomerId()).isEqualTo(customerId.asUUID());
    assertThat(orderShipped.data().getTrackingNumber()).isEqualTo(trackingNumber);
  }


  private <T> Event<T> firstEventOfType(List<Event<?>> events, Class<T> clazz) {
    return (Event<T>) events.stream()
        .filter(e -> e.eventType().equals(clazz.getSimpleName())).findFirst()
        .orElseThrow(() -> new RuntimeException("Missing event"));

  }

}
