package io.serialized.samples.orderservice.api.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.serialized.samples.order.domain.*;
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
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static javax.ws.rs.core.Response.*;

@Path("/commands")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderCommandResource {

  private static final int RETRY_TIMES = 3;
  private static final long REQUEST_TIMEOUT_SECONDS = 10;

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final EventStoreService eventStoreService;

  public OrderCommandResource(EventStoreService eventStoreService) {
    this.eventStoreService = eventStoreService;
  }

  @POST
  @Path("place-order")
  public void placeOrder(@Valid @NotNull PlaceOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    Observable<EventBatch> eventBatch = Observable.fromCallable(() -> {
      CustomerId customerId = new CustomerId(request.customerId);
      Amount orderAmount = new Amount(request.orderAmount);
      Order order = Order.createNewOrder(customerId);
      logger.info("Placing order: {}", orderId);
      OrderPlacedEvent event = order.place(orderAmount);
      return new EventBatch(orderId.id, ImmutableList.of(event));
    });
    saveEventsAndResume(eventBatch, asyncResponse, throwable -> false);
  }

  @POST
  @Path("pay-order")
  public void payOrder(@Valid @NotNull PayOrderRequest request, @Suspended AsyncResponse asyncResponse) {
    OrderId orderId = new OrderId(request.orderId);
    Observable<EventBatch> eventBatch = eventStoreService.loadOrder(orderId.toString())
        .map(aggregate -> loadFromEvents(orderId, aggregate.aggregateVersion, aggregate.events))
        .map(orderState -> {
          Order order = new Order(orderState.customerId, orderState.orderStatus, orderState.orderAmount);
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
          Order order = new Order(orderState.customerId, orderState.orderStatus, orderState.orderAmount);
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
          Order order = new Order(orderState.customerId, orderState.orderStatus, orderState.orderAmount);
          logger.info("Cancelling order: {}", orderId);
          OrderCancelledEvent event = order.cancel(request.reason);
          return new EventBatch(orderId.id, orderState.version, ImmutableList.of(event));
        });
    saveEventsAndResume(eventBatch, asyncResponse, this::isHttpConflict);
  }

  private void saveEventsAndResume(Observable<EventBatch> observable, AsyncResponse asyncResponse, Predicate<Throwable> retry) {
    configureTimeoutHandling(asyncResponse);
    observable
        .filter(eventBatch -> !eventBatch.events.isEmpty()) // No-ops are represented by empty event batch
        .flatMap(eventStoreService::saveOrderEvents)
        .retry(RETRY_TIMES, retry)
        .subscribeOn(Schedulers.io())
        .subscribe(
            onNext -> asyncResponse.resume(ok().build()),
            onError -> asyncResponse.resume(createErrorResponse(onError)),
            () -> asyncResponse.resume(ok().build()) // Also resume all no-ops (ie. order already cancelled)
        );
  }

  private Response createErrorResponse(Throwable throwable) {
    if (throwable instanceof HttpException) {
      logger.warn("Error: " + throwable.getMessage());
      return status(((HttpException) throwable).code()).entity(ImmutableMap.of("error", throwable.getMessage())).build();
    } else if (throwable instanceof IllegalOrderStateException || throwable instanceof IllegalArgumentException) {
      logger.warn("Client Error: " + throwable.getMessage());
      return status(400).entity(ImmutableMap.of("error", throwable.getMessage())).build();
    } else {
      logger.error("Server Error: " + throwable.getMessage(), throwable);
      return serverError().build();
    }
  }

  private boolean isHttpConflict(Throwable throwable) {
    return throwable instanceof HttpException && ((HttpException) throwable).code() == CONFLICT.getStatusCode();
  }

  private void configureTimeoutHandling(AsyncResponse response) {
    ImmutableMap errorResponse = ImmutableMap.of("message", "Operation timed out");
    response.setTimeout(REQUEST_TIMEOUT_SECONDS, SECONDS);
    response.setTimeoutHandler(ar -> ar.resume(status(SERVICE_UNAVAILABLE).entity(errorResponse).build()));
  }

}
