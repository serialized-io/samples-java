package io.serialized.samples.aggregate.order;

import io.serialized.samples.infrastructure.order.SerializedOrderEventService;
import io.serialized.samples.order.domain.*;
import io.serialized.samples.order.domain.event.OrderCancelledEvent;
import io.serialized.samples.order.domain.event.OrderEvent;
import io.serialized.samples.order.domain.event.OrderPlacedEvent;
import io.serialized.samples.order.domain.event.OrderShippedEvent;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static io.serialized.samples.order.domain.CustomerId.newCustomer;
import static io.serialized.samples.order.domain.OrderId.newOrderId;
import static io.serialized.samples.order.domain.TrackingNumber.newTrackingNumber;
import static org.apache.commons.lang.StringUtils.defaultString;

public class OrderTest {

  private static final URI EVENT_API_URI = URI.create("https://api.serialized.io/aggregates");

  public static void main(String[] args) {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", EVENT_API_URI, accessKey);

    OrderEventService orderEventStore = new SerializedOrderEventService(EVENT_API_URI, accessKey, secretAccessKey);

    // ======================================================================================================

    OrderId orderId1 = newOrderId();
    // Create..
    OrderState orderInitState = OrderState.builder(orderId1).build();
    Order order = new Order(orderInitState.orderStatus, Amount.ZERO);
    //.. and place a new order
    CustomerId customer = newCustomer();
    OrderPlacedEvent orderPlacedEvent = order.place(customer, new Amount(4321));
    System.out.println("Placing order: " + orderId1);
    orderEventStore.saveEvent(orderInitState.orderId.id, orderInitState.version, orderPlacedEvent);

    // --------------

    // Load..
    OrderState orderToCancelState = orderEventStore.load(orderId1.id);
    Order orderToCancel = new Order(orderToCancelState.orderStatus, orderInitState.orderAmount);
    // ..and cancel order
    OrderCancelledEvent orderCancelledEvent = orderToCancel.cancel(customer, "DOA");
    System.out.println("Cancelling order: " + orderId1);
    orderEventStore.saveEvent(orderToCancelState.orderId.id, orderToCancelState.version, orderCancelledEvent);

    // ======================================================================================================

    OrderId orderId2 = newOrderId();
    // Create..
    OrderState orderInitState1 = OrderState.builder(orderId2).build();
    Order order1 = new Order(orderInitState1.orderStatus, orderInitState.orderAmount);
    // ..and place a new order
    OrderPlacedEvent orderPlacedEvent1 = order1.place(newCustomer(), new Amount(1234));
    System.out.println("Placing order: " + orderId2);
    orderEventStore.saveEvent(orderInitState1.orderId.id, orderInitState1.version, orderPlacedEvent1);

    // --------------

    // Load..
    OrderState orderToPayState = orderEventStore.load(orderId2.id);
    Order orderToPay = new Order(orderToPayState.orderStatus, orderToPayState.orderAmount);
    // ..and pay order
    List<OrderEvent> events = orderToPay.pay(customer, new Amount(1234));

    System.out.println("Paying order: " + orderId2);
    orderEventStore.saveEvents(orderToPayState.orderId.id, orderToPayState.version, events);

    // --------------

    // Load..
    OrderState orderToShipState = orderEventStore.load(orderId2.id);
    Order orderToShip = new Order(orderToShipState.orderStatus, orderToShipState.orderAmount);
    // ..and ship order
    OrderShippedEvent orderShippedEvent = orderToShip.ship(customer, newTrackingNumber());
    System.out.println("Shipping order: " + orderId2);
    orderEventStore.saveEvent(orderToShipState.orderId.id, orderToShipState.version, orderShippedEvent);

    // ======================================================================================================

  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
