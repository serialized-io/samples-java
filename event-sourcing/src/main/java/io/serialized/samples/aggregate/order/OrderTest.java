package io.serialized.samples.aggregate.order;

import io.serialized.samples.infrastructure.OrderService;
import io.serialized.samples.order.domain.*;
import io.serialized.samples.order.domain.event.OrderCancelledEvent;
import io.serialized.samples.order.domain.event.OrderEvent;
import io.serialized.samples.order.domain.event.OrderPlacedEvent;
import io.serialized.samples.order.domain.event.OrderShippedEvent;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static io.serialized.samples.order.domain.CustomerId.newCustomerId;
import static io.serialized.samples.order.domain.OrderId.newOrderId;
import static io.serialized.samples.order.domain.TrackingNumber.newTrackingNumber;
import static org.apache.commons.lang.StringUtils.defaultString;

public class OrderTest {

  private static final URI EVENT_API_URI = URI.create("https://api.serialized.io/aggregates");

  public static void main(String[] args) {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", EVENT_API_URI, accessKey);
    OrderService orderEventStore = new OrderService(EVENT_API_URI, accessKey, secretAccessKey);

    // ======================================================================================================

    OrderId orderId1 = newOrderId();
    // Create..
    OrderState orderInitState1 = OrderState.builder(orderId1).build();

    CustomerId customer = newCustomerId();
    Order order = new Order(orderId1, customer, orderInitState1.orderStatus, Amount.ZERO);
    //.. and place a new order
    OrderPlacedEvent orderPlacedEvent = order.place(new Amount(4321));
    System.out.println("Placing order: " + orderId1);
    orderEventStore.saveEvent(orderInitState1.orderId, orderInitState1.version, orderPlacedEvent);

    // --------------

    // Load..
    OrderState orderToCancelState = orderEventStore.load(orderId1);
    Order orderToCancel = new Order(orderId1, customer, orderToCancelState.orderStatus, orderToCancelState.orderAmount);
    // ..and cancel order
    OrderCancelledEvent orderCancelledEvent = orderToCancel.cancel("DOA");
    System.out.println("Cancelling order: " + orderId1);
    orderEventStore.saveEvent(orderToCancelState.orderId, orderToCancelState.version, orderCancelledEvent);

    // ======================================================================================================

    OrderId orderId2 = newOrderId();
    // Create..
    OrderState orderInitState2 = OrderState.builder(orderId2).build();
    Order order1 = new Order(orderId2, newCustomerId(), orderInitState2.orderStatus, orderInitState2.orderAmount);
    // ..and place a new order
    OrderPlacedEvent orderPlacedEvent1 = order1.place(new Amount(1234));
    System.out.println("Placing order: " + orderId2);
    orderEventStore.saveEvent(orderInitState2.orderId, orderInitState2.version, orderPlacedEvent1);

    // --------------

    // Load..
    OrderState orderToPayState = orderEventStore.load(orderId2);
    Order orderToPay = new Order(orderId2, customer, orderToPayState.orderStatus, orderToPayState.orderAmount);
    // ..and pay order
    List<OrderEvent> events = orderToPay.pay(new Amount(1234));

    System.out.println("Paying order: " + orderId2);
    orderEventStore.saveEvents(orderToPayState.orderId, orderToPayState.version, events);

    // --------------

    // Load..
    OrderState orderToShipState = orderEventStore.load(orderId2);
    Order orderToShip = new Order(orderId2, customer, orderToShipState.orderStatus, orderToShipState.orderAmount);
    // ..and ship order
    OrderShippedEvent orderShippedEvent = orderToShip.ship(newTrackingNumber());
    System.out.println("Shipping order: " + orderId2);
    orderEventStore.saveEvent(orderToShipState.orderId, orderToShipState.version, orderShippedEvent);

    // ======================================================================================================

  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
