package io.serialized.samples.orderservice.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.EventBatch;
import io.serialized.samples.orderservice.OrderApplicationConfig;
import io.serialized.samples.orderservice.api.ApiExceptionMapper;
import io.serialized.samples.orderservice.domain.Amount;
import io.serialized.samples.orderservice.domain.CustomerId;
import io.serialized.samples.orderservice.domain.OrderId;
import io.serialized.samples.orderservice.domain.OrderState;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.UUID;

import static io.serialized.samples.orderservice.EventTypeMatcher.containsEventType;
import static io.serialized.samples.orderservice.domain.event.OrderFullyPaid.orderFullyPaid;
import static io.serialized.samples.orderservice.domain.event.OrderPlaced.orderPlaced;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class OrderCommandResourceTest {

  private static final AggregateApiStub.AggregateApiCallback aggregateApiCallback = mock(AggregateApiStub.AggregateApiCallback.class);
  private static final DropwizardClientExtension dropwizard = new DropwizardClientExtension(new AggregateApiStub(aggregateApiCallback));

  private final AggregateClient<OrderState> orderClient = new OrderApplicationConfig().orderClient(createConfig(dropwizard));
  private final OrderCommandResource commandResource = new OrderCommandResource(orderClient);

  private final ResourceExtension resources = ResourceExtension.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addProvider(commandResource)
      .addProvider(new ApiExceptionMapper())
      .build();

  @BeforeEach
  public void setUp() {
    reset(aggregateApiCallback);
    dropwizard.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Test
  public void placeOrder() {

    // given
    PlaceOrderRequest request = new PlaceOrderRequest();
    request.orderId = UUID.randomUUID();
    request.customerId = UUID.randomUUID();
    request.sku = "1234";
    request.orderAmount = 123456;

    when(aggregateApiCallback.eventsStored(eq(request.orderId), any(EventBatch.class))).thenReturn(OK);

    // when
    Response response = resources.target("/commands/place-order").request().post(json(request));

    // then
    assertThat(response.getStatus()).isEqualTo(200);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(request.orderId), argThat(containsEventType("OrderPlaced")));
  }

  @Test
  public void cancelOrder() {

    // given
    CancelOrderRequest request = new CancelOrderRequest();
    request.orderId = UUID.randomUUID();
    request.reason = "DOA";

    OrderId orderId = OrderId.fromUUID(request.orderId);
    CustomerId customerId = CustomerId.newId();

    AggregateApiStub.AggregateResponse order = new AggregateApiStub.AggregateResponse(
        request.orderId.toString(), "order", 1, singletonList(orderPlaced(orderId, customerId, "abc1232", new Amount(1234L), currentTimeMillis())
    ));

    when(aggregateApiCallback.aggregateLoaded(eq("order"), eq(request.orderId))).thenReturn(order);
    when(aggregateApiCallback.eventsStored(eq(request.orderId), any(EventBatch.class))).thenReturn(OK);

    // when
    Response response = resources.target("/commands/cancel-order").request().post(json(request));

    // then
    assertThat(response.getStatus()).isEqualTo(200);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(request.orderId), argThat(containsEventType("OrderCanceled")));
  }

  @Test
  public void payOrder() {

    // given
    PayOrderRequest request = new PayOrderRequest();
    request.orderId = UUID.randomUUID();
    request.amount = 1000;

    OrderId orderId = OrderId.fromUUID(request.orderId);
    CustomerId customerId = CustomerId.newId();

    AggregateApiStub.AggregateResponse order = new AggregateApiStub.AggregateResponse(
        request.orderId.toString(), "order", 1, singletonList(orderPlaced(orderId, customerId, "abc123", new Amount(1234L), currentTimeMillis())
    ));

    when(aggregateApiCallback.aggregateLoaded(eq("order"), eq(request.orderId))).thenReturn(order);
    when(aggregateApiCallback.eventsStored(eq(request.orderId), any(EventBatch.class))).thenReturn(OK);

    // when
    Response response = resources.target("/commands/pay-order").request().post(json(request));

    // then
    assertThat(response.getStatus()).isEqualTo(200);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(request.orderId), argThat(
        containsEventType("PaymentReceived", "OrderFullyPaid")));
  }

  @Test
  public void shipOrder() {

    // given
    ShipOrderRequest request = new ShipOrderRequest();
    request.orderId = UUID.randomUUID();
    request.trackingNumber = UUID.randomUUID().toString();

    OrderId orderId = OrderId.fromUUID(request.orderId);
    CustomerId customerId = CustomerId.newId();

    Amount orderAmount = new Amount(1234L);
    AggregateApiStub.AggregateResponse order = new AggregateApiStub.AggregateResponse(
        request.orderId.toString(), "order", 1, Arrays.asList(
        orderPlaced(orderId, customerId, "abc123", orderAmount, currentTimeMillis()),
        orderFullyPaid(orderId, customerId, orderAmount, currentTimeMillis())
    ));

    when(aggregateApiCallback.aggregateLoaded(eq("order"), eq(request.orderId))).thenReturn(order);
    when(aggregateApiCallback.eventsStored(eq(request.orderId), any(EventBatch.class))).thenReturn(OK);

    // when
    Response response = resources.target("/commands/ship-order").request().post(json(request));

    // then
    assertThat(response.getStatus()).isEqualTo(200);
    verify(aggregateApiCallback, times(1)).eventsStored(eq(request.orderId), argThat(containsEventType("OrderShipped")));
  }

  @Test
  public void cancelOrder_ErrorOnLoadCauses500() {

    // given
    CancelOrderRequest request = new CancelOrderRequest();
    request.orderId = UUID.randomUUID();
    request.reason = "DOA";

    when(aggregateApiCallback.aggregateLoaded(eq("order"), eq(request.orderId))).thenThrow(new RuntimeException());

    // when
    Response response = resources.target("/commands/cancel-order").request().post(json(request));

    // then
    assertThat(response.getStatus()).isEqualTo(500);
    verify(aggregateApiCallback, never()).eventsStored(eq(request.orderId), any(EventBatch.class));
  }

  public static SerializedClientConfig createConfig(DropwizardClientExtension dropwizard) {
    return new SerializedClientConfig.Builder()
        .rootApiUrl(dropwizard.baseUri() + "/api-stub")
        .accessKey("dummy")
        .secretAccessKey("dummy")
        .build();
  }

}
