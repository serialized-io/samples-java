package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.client.OrderClient;
import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class OrderTest {

  private static final URI EVENT_API_URI = URI.create("https://api.serialized.io/aggregates");

  public static void main(String[] args) throws IOException {
    String accessKey = System.getenv("SERIALIZED_ACCESS_KEY");
    String secretAccessKey = System.getenv("SERIALIZED_SECRET_ACCESS_KEY");

    System.out.format("Connecting to [%s] using [%s]\n", EVENT_API_URI, accessKey);

    OrderClient orderClient = new OrderClient(EVENT_API_URI, accessKey, secretAccessKey);

    // ======================================================================================================

    String orderId1 = UUID.randomUUID().toString();
    // Create..
    OrderState orderInitState = OrderState.builder(orderId1).build();
    Order order = new Order(orderInitState);
    //.. and place a new order
    OrderPlacedEvent orderPlacedEvent = order.place("someCustomerId1", 4321);
    System.out.println("Placing order: " + orderId1);
    orderClient.saveEvent(orderInitState.aggregateId, orderInitState.version, orderPlacedEvent);

    // Load..
    OrderState orderToCancelState = orderClient.load(orderId1);
    Order orderToCancel = new Order(orderToCancelState);
    // ..and cancel order
    OrderCancelledEvent orderCancelledEvent = orderToCancel.cancel("DOA");
    System.out.println("Cancelling order: " + orderId1);
    orderClient.saveEvent(orderToCancelState.aggregateId, orderToCancelState.version, orderCancelledEvent);

    // ======================================================================================================

    String orderId2 = UUID.randomUUID().toString();
    // Create..
    OrderState orderInitState1 = OrderState.builder(orderId2).build();
    Order order1 = new Order(orderInitState1);
    // ..and place a new order
    OrderPlacedEvent orderPlacedEvent1 = order1.place("someCustomerId2", 1234);
    System.out.println("Placing order: " + orderId2);
    orderClient.saveEvent(orderInitState1.aggregateId, orderInitState1.version, orderPlacedEvent1);

    // Load..
    OrderState orderToPayState = orderClient.load(orderId2);
    Order orderToPay = new Order(orderToPayState);
    // ..and pay order
    OrderPaidEvent orderPaidEvent = orderToPay.pay(1234);
    System.out.println("Paying order: " + orderId2);
    orderClient.saveEvent(orderToPayState.aggregateId, orderToPayState.version, orderPaidEvent);

    // ======================================================================================================

  }

}
