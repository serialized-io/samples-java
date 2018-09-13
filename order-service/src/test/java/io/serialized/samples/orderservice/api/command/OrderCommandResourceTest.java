package io.serialized.samples.orderservice.api.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.dropwizard.testing.junit.DropwizardClientRule;
import io.dropwizard.testing.junit.ResourceTestRule;
import io.serialized.samples.order.domain.Amount;
import io.serialized.samples.order.domain.CustomerId;
import io.serialized.samples.orderservice.integration.EventStoreService;
import io.serialized.samples.orderservice.integration.OrderAggregate;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static io.serialized.samples.order.domain.event.OrderFullyPaidEvent.orderFullyPaid;
import static io.serialized.samples.order.domain.event.OrderPlacedEvent.orderPlaced;
import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

public class OrderCommandResourceTest {

  private static final EventStore EVENT_STORE = mock(EventStore.class);

  private static CountDownLatch EXPECTED_RESOURCE_INVOCATIONS;

  @ClassRule
  public static final DropwizardClientRule DROPWIZARD = new DropwizardClientRule(new SerializedApiStubResource());

  @Path("/api-stub/aggregates/order")
  @Produces(APPLICATION_JSON)
  @Consumes(APPLICATION_JSON)
  public static class SerializedApiStubResource {

    @GET
    @Path("{id}")
    public Response loadEvents(@PathParam("id") String id) {
      EXPECTED_RESOURCE_INVOCATIONS.countDown();
      try {
        return Response.ok(EVENT_STORE.loadOrder(id)).build();
      } catch (ConflictException ce) {
        return Response.status(CONFLICT).build();
      } catch (RuntimeException e) {
        return Response.status(SERVICE_UNAVAILABLE).build();
      }
    }

    @POST
    @Path("events")
    public Response saveEvents(Map payload) {
      EXPECTED_RESOURCE_INVOCATIONS.countDown();
      EVENT_STORE.saveOrderEvents(payload);
      return Response.ok().build();
    }
  }

  private EventStoreService eventStoreService;

  {
    eventStoreService = new Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(DROPWIZARD.baseUri() + "/api-stub/")
        .build().create(EventStoreService.class);
  }

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addProvider(new OrderCommandResource(eventStoreService))
      .build();

  @Before
  public void setUp() {
    reset(EVENT_STORE);
  }

  @Test
  public void placeOrder() throws InterruptedException {
    // given
    PlaceOrderRequest request = new PlaceOrderRequest();
    request.orderId = newId();
    request.customerId = newId();
    request.orderAmount = 123456;

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(1);
    Response response = resources.target("/commands/place-order").request().post(json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(200));
    verify(EVENT_STORE, times(1)).saveOrderEvents(anyMap());
  }

  @Test
  public void cancelOrder() throws InterruptedException {
    // given
    CancelOrderRequest request = new CancelOrderRequest();
    request.orderId = newId();
    request.reason = "DOA";

    OrderAggregate aggregate = new OrderAggregate();
    aggregate.aggregateId = request.orderId;
    aggregate.aggregateVersion = 1;
    aggregate.events = ImmutableList.of(orderPlaced(CustomerId.newCustomer(), new Amount(1234L)));
    when(EVENT_STORE.loadOrder(request.orderId)).thenReturn(aggregate);

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(2);
    Response response = resources.target("/commands/cancel-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(200));
    verify(EVENT_STORE, times(1)).saveOrderEvents(anyMap());
  }

  @Test
  public void payOrder() throws InterruptedException {
    // given
    PayOrderRequest request = new PayOrderRequest();
    request.orderId = newId();
    request.amount = 1000;

    OrderAggregate aggregate = new OrderAggregate();
    aggregate.aggregateId = request.orderId;
    aggregate.aggregateVersion = 1;
    aggregate.events = ImmutableList.of(orderPlaced(CustomerId.newCustomer(), new Amount(1234L)));
    when(EVENT_STORE.loadOrder(request.orderId)).thenReturn(aggregate);

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(2);
    Response response = resources.target("/commands/pay-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(200));
    verify(EVENT_STORE, times(1)).saveOrderEvents(anyMap());
  }

  @Test
  public void shipOrder() throws InterruptedException {
    // given
    ShipOrderRequest request = new ShipOrderRequest();
    request.orderId = newId();
    request.trackingNumber = UUID.randomUUID().toString();

    CustomerId customerId = CustomerId.newCustomer();

    OrderAggregate aggregate = new OrderAggregate();
    aggregate.aggregateId = request.orderId;
    aggregate.aggregateVersion = 1;
    aggregate.events = ImmutableList.of(
        orderPlaced(customerId, new Amount(1234L)),
        orderFullyPaid(customerId)
    );
    when(EVENT_STORE.loadOrder(request.orderId)).thenReturn(aggregate);

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(1);
    Response response = resources.target("/commands/ship-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(200));
    verify(EVENT_STORE, times(1)).saveOrderEvents(anyMap());
  }

  @Test
  public void cannotShipOrderNotPaid() throws InterruptedException {
    // given
    ShipOrderRequest request = new ShipOrderRequest();
    request.orderId = newId();
    request.trackingNumber = UUID.randomUUID().toString();

    OrderAggregate aggregate = new OrderAggregate();
    aggregate.aggregateId = request.orderId;
    aggregate.aggregateVersion = 1;
    aggregate.events = ImmutableList.of(orderPlaced(CustomerId.newCustomer(), new Amount(1234L)));
    when(EVENT_STORE.loadOrder(request.orderId)).thenReturn(aggregate);

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(1);
    Response response = resources.target("/commands/ship-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(400));
    Map responseBody = response.readEntity(Map.class);
    assertThat(responseBody.get("error"), is("Expected order to be PAID but was PLACED"));
    verify(EVENT_STORE, never()).saveOrderEvents(anyMap());
  }

  @Test
  public void invalidTrackerNumberFormatCauses400() throws InterruptedException {
    // given
    ShipOrderRequest request = new ShipOrderRequest();
    request.orderId = newId();
    request.trackingNumber = "abc123";

    OrderAggregate aggregate = new OrderAggregate();
    aggregate.aggregateId = request.orderId;
    aggregate.aggregateVersion = 1;
    aggregate.events = ImmutableList.of(orderPlaced(CustomerId.newCustomer(), new Amount(1234L)));
    when(EVENT_STORE.loadOrder(request.orderId)).thenReturn(aggregate);

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(1);
    Response response = resources.target("/commands/ship-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(400));
    Map responseBody = response.readEntity(Map.class);
    assertThat(responseBody.get("error"), is("Invalid trackingNumber format"));
    verify(EVENT_STORE, never()).saveOrderEvents(anyMap());
  }

  @Test
  public void cancelOrder_ErrorOnLoadCauses503() throws InterruptedException {
    // given
    CancelOrderRequest request = new CancelOrderRequest();
    request.orderId = newId();
    request.reason = "DOA";

    when(EVENT_STORE.loadOrder(request.orderId)).thenThrow(new RuntimeException());

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(1);
    Response response = resources.target("/commands/cancel-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(503));
    verify(EVENT_STORE, never()).saveOrderEvents(anyMap());
  }

  @Test
  public void cancelOrder_ConflictCausesRetriesBeforeFailing() throws InterruptedException {
    // given
    CancelOrderRequest request = new CancelOrderRequest();
    request.orderId = newId();
    request.reason = "DOA";

    when(EVENT_STORE.loadOrder(request.orderId)).thenThrow(new ConflictException());

    // when
    EXPECTED_RESOURCE_INVOCATIONS = new CountDownLatch(4); // One call + 3 retries
    Response response = resources.target("/commands/cancel-order").request().post(Entity.json(request));
    EXPECTED_RESOURCE_INVOCATIONS.await();

    // then
    assertThat(response.getStatus(), is(409));
    verify(EVENT_STORE, never()).saveOrderEvents(anyMap());
  }

  private String newId() {
    return UUID.randomUUID().toString();
  }

  interface EventStore {

    void saveOrderEvents(Map map);

    OrderAggregate loadOrder(String id);

  }

  static class ConflictException extends RuntimeException {

  }

}