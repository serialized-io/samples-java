package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;
import io.serialized.samples.aggregate.order.event.OrderShippedEvent;
import org.junit.Test;

import static io.serialized.samples.aggregate.order.CustomerId.newCustomer;
import static io.serialized.samples.aggregate.order.Order.createNewOrder;
import static io.serialized.samples.aggregate.order.OrderId.newOrderId;
import static io.serialized.samples.aggregate.order.event.OrderPaidEvent.orderPaid;
import static io.serialized.samples.aggregate.order.event.OrderPlacedEvent.orderPlaced;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OrderTest {

  @Test
  public void placeNewOrderGeneratesEvent() throws Exception {
    Order order = createNewOrder();
    OrderPlacedEvent placedEvent = order.place(newCustomer(), new Amount(200));
    assertThat(placedEvent.data.orderAmount, is(200L));
  }

  @Test
  public void pay() throws Exception {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    OrderPaidEvent paidEvent = order.pay(new Amount(200));
    assertThat(paidEvent.data.amount, is(200L));
  }

  @Test(expected = IllegalStateException.class)
  public void cannotPayNewOrder() throws Exception {

    OrderState state = OrderState.builder(newOrderId()).build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    order.pay(new Amount(200));
  }

  @Test(expected = IllegalStateException.class)
  public void cannotShipUnpaidOrder() throws Exception {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    order.ship(TrackingNumber.newTrackingNumber());
  }

  @Test
  public void canShipPaidOrder() throws Exception {

    OrderState state = OrderState.builder(newOrderId())
        .apply(orderPlaced(newCustomer(), new Amount(200)))
        .apply(orderPaid(new Amount(200)))
        .build();

    Order order = new Order(state.orderStatus, state.orderAmount);
    TrackingNumber trackingNumber = TrackingNumber.newTrackingNumber();
    OrderShippedEvent shippedEvent = order.ship(trackingNumber);
    assertThat(shippedEvent.data.trackingNumber, is(trackingNumber.trackingNumber));
  }

  @Test
  public void cancel() throws Exception {
  }

}