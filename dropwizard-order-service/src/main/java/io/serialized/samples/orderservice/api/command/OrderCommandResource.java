package io.serialized.samples.orderservice.api.command;

import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.samples.orderservice.domain.Amount;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.Order;
import io.serialized.samples.orderservice.domain.OrderId;
import io.serialized.samples.orderservice.domain.OrderState;
import io.serialized.samples.orderservice.domain.TrackingNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static io.serialized.client.aggregate.AggregateRequest.saveRequest;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/commands")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderCommandResource {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final AggregateClient<OrderState> orderClient;

  public OrderCommandResource(AggregateClient<OrderState> orderClient) {
    this.orderClient = orderClient;
  }

  @POST
  @Path("place-order")
  public Response placeOrder(@Valid @NotNull PlaceOrderRequest request) {

    OrderId orderId = OrderId.fromUUID(request.orderId);
    CustomerId customerId = CustomerId.fromUUID(request.customerId);
    Amount orderAmount = new Amount(request.orderAmount);
    Order order = new Order(new OrderState());
    logger.info("Placing order: {}", orderId);
    List<Event<?>> events = order.place(orderId, customerId, orderAmount);
    orderClient.save(saveRequest().withAggregateId(orderId.asUUID()).withEvents(events).build());

    return Response.ok().build();
  }

  @POST
  @Path("pay-order")
  public Response payOrder(@Valid @NotNull PayOrderRequest request) {

    OrderId orderId = OrderId.fromUUID(request.orderId);
    Amount amount = new Amount(request.amount);

    int eventCount = orderClient.update(orderId.asUUID(), state -> {
      Order order = new Order(state);
      return order.pay(amount);
    });

    if (eventCount > 0) {
      logger.info("Paying [{}] for order: {}", amount, orderId.asString());
    }

    return Response.ok().build();
  }

  @POST
  @Path("ship-order")
  public Response shipOrder(@Valid @NotNull ShipOrderRequest request) {

    OrderId orderId = OrderId.fromUUID(request.orderId);

    int eventCount = orderClient.update(orderId.asUUID(), state -> {
      Order order = new Order(state);
      return order.ship(new TrackingNumber(request.trackingNumber));
    });

    if (eventCount > 0) {
      logger.info("Shipping order: {}", orderId.asString());
    }

    return Response.ok().build();
  }

  @POST
  @Path("cancel-order")
  public Response cancelOrder(@Valid @NotNull CancelOrderRequest request) {
    OrderId orderId = OrderId.fromUUID(request.orderId);

    int eventCount = orderClient.update(orderId.asUUID(), state -> {
      Order order = new Order(state);
      return order.cancel(request.reason);
    });

    if (eventCount > 0) {
      logger.info("Cancelling order: {}", orderId.asUUID());
    }

    return Response.ok().build();
  }

}
