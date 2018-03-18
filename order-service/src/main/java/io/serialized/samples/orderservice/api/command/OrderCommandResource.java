package io.serialized.samples.orderservice.api.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.order.domain.Order;
import io.serialized.samples.order.domain.OrderId;
import io.serialized.samples.order.domain.TrackingNumber;
import io.serialized.samples.order.domain.event.OrderCancelledEvent;
import io.serialized.samples.order.domain.event.OrderEvent;
import io.serialized.samples.order.domain.event.OrderPlacedEvent;
import io.serialized.samples.order.domain.event.OrderShippedEvent;
import io.serialized.samples.orderservice.integration.EventBatch;
import io.serialized.samples.orderservice.integration.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.List;

import static io.serialized.samples.order.domain.OrderState.loadFromEvents;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.*;

@Path("/commands")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderCommandResource {

  private static final int RETRY_TIMES = 3;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventStoreService eventStoreService;

  public OrderCommandResource(EventStoreService eventStoreService) {
    this.eventStoreService = eventStoreService;
  }

  @POST
  @Path("place-order")
  public void placeOrder(@Valid @NotNull PlaceOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    CustomerId customerId = new CustomerId(request.customerId);
    Amount orderAmount = new Amount(request.orderAmount);
    Order order = Order.createNewOrder();
    logger.info("Placing order: {}", orderId);
    OrderPlacedEvent event = order.place(customerId, orderAmount);
    Observable<EventBatch> eventBatch = Observable.just(new EventBatch(orderId.id, ImmutableList.of(event)));
    saveEventsAndResume(eventBatch, asyncResponse, throwable -> false);
  }

  @POST
  @Path("pay-order")
  public void payOrder(@Valid @NotNull PayOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    Observable<EventBatch> eventBatch = eventStoreService.loadOrder(orderId.toString())
        .map(aggregate -> loadFromEvents(orderId, aggregate.aggregateVersion, aggregate.events))
        .map(orderState -> {
          Order order = new Order(orderState.orderStatus, orderState.orderAmount);
          Amount amount = new Amount(request.amount);
          logger.info("Paying [{}] for order: {}", amount, orderId);
          List<OrderEvent> events = order.pay(amount);
          return new EventBatch(orderId.id, orderState.version, events);
        });
    saveEventsAndResume(eventBatch, asyncResponse, this::isHttpConflict);
  }

  @POST
  @Path("ship-order")
  public void shipOrder(@Valid @NotNull ShipOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    Observable<EventBatch> eventBatch = eventStoreService.loadOrder(orderId.toString())
        .map(aggregate -> loadFromEvents(orderId, aggregate.aggregateVersion, aggregate.events))
        .map(orderState -> {
          Order order = new Order(orderState.orderStatus, orderState.orderAmount);
          TrackingNumber trackingNumber = new TrackingNumber(request.trackingNumber);
          logger.info("Shipping order: {}", orderId);
          OrderShippedEvent event = order.ship(trackingNumber);
          return new EventBatch(orderId.id, orderState.version, ImmutableList.of(event));
        });
    saveEventsAndResume(eventBatch, asyncResponse, this::isHttpConflict);
  }

  @POST
  @Path("cancel-order")
  public void cancelOrder(@Valid @NotNull CancelOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    Observable<EventBatch> eventBatch = eventStoreService.loadOrder(orderId.toString())
        .map(aggregate -> loadFromEvents(orderId, aggregate.aggregateVersion, aggregate.events))
        .map(orderState -> {
          Order order = new Order(orderState.orderStatus, orderState.orderAmount);
          logger.info("Cancelling order: {}", orderId);
          OrderCancelledEvent event = order.cancel(request.reason);
          return new EventBatch(orderId.id, orderState.version, ImmutableList.of(event));
        });
    saveEventsAndResume(eventBatch, asyncResponse, this::isHttpConflict);
  }

  private void saveEventsAndResume(Observable<EventBatch> observable, AsyncResponse asyncResponse, Predicate<Throwable> retry) {
    observable
        .filter(eventBatch -> !eventBatch.events.isEmpty())
        .flatMap(eventStoreService::saveOrderEvents)
        .retry(RETRY_TIMES, retry)
        .subscribe(
            onNext -> asyncResponse.resume(ok().build()),
            onError -> asyncResponse.resume(createErrorResponse(onError)),
            () -> asyncResponse.resume(ok().build()) // Also resume all no-ops (ie. order already cancelled)
        );
  }

  private Response createErrorResponse(Throwable throwable) {
    if (throwable instanceof HttpException) {
      return status(((HttpException) throwable).code()).entity(ImmutableMap.of("error", throwable.getMessage())).build();
    } else {
      return serverError().build();
    }
  }

  private boolean isHttpConflict(Throwable throwable) {
    return throwable instanceof HttpException && ((HttpException) throwable).code() == CONFLICT.getStatusCode();
  }

}
