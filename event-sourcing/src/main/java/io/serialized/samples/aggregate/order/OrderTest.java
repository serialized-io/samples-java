package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;
import io.serialized.samples.aggregate.order.event.OrderShippedEvent;
import io.serialized.samples.aggregate.order.store.OrderEventStore;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static io.serialized.samples.aggregate.order.CustomerId.newCustomer;
import static io.serialized.samples.aggregate.order.OrderId.newOrder;
import static io.serialized.samples.aggregate.order.TrackingNumber.newTrackingNumber;
import static org.apache.commons.lang.StringUtils.defaultString;

public class OrderTest {

  private static final URI EVENT_API_URI = URI.create("https://api.serialized.io/aggregates");

  public static void main(String[] args) throws IOException {
    String accessKey = getConfig("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = getConfig("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", EVENT_API_URI, accessKey);

    OrderEventStore orderEventStore = new OrderEventStore(EVENT_API_URI, accessKey, secretAccessKey);

    // ======================================================================================================

    OrderId orderId1 = newOrder();
    // Create..
    OrderState orderInitState = OrderState.builder(orderId1).build();
    Order order = new Order(orderInitState.orderStatus, Amount.ZERO);
    //.. and place a new order
    OrderPlacedEvent orderPlacedEvent = order.place(newCustomer(), new Amount(4321));
    System.out.println("Placing order: " + orderId1);
    orderEventStore.saveEvent(orderInitState.aggregateId, orderInitState.version, orderPlacedEvent);

    // --------------

    // Load..
    OrderState orderToCancelState = orderEventStore.load(orderId1.id);
    Order orderToCancel = new Order(orderToCancelState.orderStatus, orderInitState.orderAmount);
    // ..and cancel order
    OrderCancelledEvent orderCancelledEvent = orderToCancel.cancel("DOA");
    System.out.println("Cancelling order: " + orderId1);
    orderEventStore.saveEvent(orderToCancelState.aggregateId, orderToCancelState.version, orderCancelledEvent);

    // ======================================================================================================

    OrderId orderId2 = OrderId.newOrder();
    // Create..
    OrderState orderInitState1 = OrderState.builder(orderId2).build();
    Order order1 = new Order(orderInitState1.orderStatus, orderInitState.orderAmount);
    // ..and place a new order
    OrderPlacedEvent orderPlacedEvent1 = order1.place(newCustomer(), new Amount(1234));
    System.out.println("Placing order: " + orderId2);
    orderEventStore.saveEvent(orderInitState1.aggregateId, orderInitState1.version, orderPlacedEvent1);

    // --------------

    // Load..
    OrderState orderToPayState = orderEventStore.load(orderId2.id);
    Order orderToPay = new Order(orderToPayState.orderStatus, orderToPayState.orderAmount);
    // ..and pay order
    OrderPaidEvent orderPaidEvent = orderToPay.pay(new Amount(1234));
    System.out.println("Paying order: " + orderId2);
    orderEventStore.saveEvent(orderToPayState.aggregateId, orderToPayState.version, orderPaidEvent);

    // --------------

    // Load..
    OrderState orderToShipState = orderEventStore.load(orderId2.id);
    Order orderToShip = new Order(orderToShipState.orderStatus, orderToShipState.orderAmount);
    // ..and ship order
    OrderShippedEvent orderShippedEvent = orderToShip.ship(newTrackingNumber());
    System.out.println("Shipping order: " + orderId2);
    orderEventStore.saveEvent(orderToShipState.aggregateId, orderToShipState.version, orderShippedEvent);

    // ======================================================================================================

  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}
