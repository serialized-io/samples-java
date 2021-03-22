package io.serialized.samples.orderservice.api.query;

import io.serialized.client.ApiException;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.ProjectionResponse;
import io.serialized.client.projection.ProjectionsResponse;
import io.serialized.client.projection.query.ListProjectionQuery;
import io.serialized.client.projection.query.ProjectionQuery;
import io.serialized.samples.orderservice.api.query.model.CustomerDebtsResponseDto;
import io.serialized.samples.orderservice.api.query.model.OrderResponseDto;
import io.serialized.samples.orderservice.api.query.model.OrdersResponseDto;
import io.serialized.samples.orderservice.api.query.model.ShippingStatsResponseDto;
import io.serialized.samples.orderservice.api.query.projection.CustomerDebtProjection;
import io.serialized.samples.orderservice.api.query.projection.CustomerOrdersProjection;
import io.serialized.samples.orderservice.api.query.projection.OrderProjection;
import io.serialized.samples.orderservice.api.query.projection.ShippingStatsProjection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

import static io.serialized.client.projection.query.ProjectionQueries.aggregated;
import static io.serialized.client.projection.query.ProjectionQueries.list;
import static io.serialized.client.projection.query.ProjectionQueries.single;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/queries")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderQueryResource {

  private final ProjectionClient projectionClient;

  public OrderQueryResource(ProjectionClient projectionClient) {
    this.projectionClient = projectionClient;
  }

  @GET
  @Path("orders")
  public Response getOrders(@QueryParam("status") String status,
                            @QueryParam("skip") @DefaultValue("0") int skip,
                            @QueryParam("limit") @DefaultValue("100") int limit) {

    ListProjectionQuery query = list("orders").withReference(status)
        .withSkip(skip)
        .withLimit(limit)
        .build(OrderProjection.class);

    ProjectionsResponse<OrderProjection> response = projectionClient.query(query);
    OrdersResponseDto dto = toDto(response.projections().stream().map(ProjectionResponse::data).collect(toList()));

    return Response.ok(dto).build();
  }

  @GET
  @Path("customers/{customerId}/orders")
  public Response getOrdersPerCustomer(@PathParam("customerId") String customerId) {

    ProjectionQuery query = single("orders-per-customer").withId(customerId).build(CustomerOrdersProjection.class);
    ProjectionResponse<CustomerOrdersProjection> response = projectionClient.query(query);
    OrdersResponseDto dto = toDto(response.data());

    return Response.ok(dto).build();
  }

  @GET
  @Path("orders/{orderId}")
  public Response getOrder(@PathParam("orderId") String orderId) {

    ProjectionQuery query = single("orders").withId(orderId).build(OrderProjection.class);
    ProjectionResponse<OrderProjection> response = projectionClient.query(query);
    OrderResponseDto dto = toDto(response.data());

    return Response.ok(dto).build();
  }

  @GET
  @Path("total-customer-debt")
  public Response getCustomerDebt() {

    ProjectionQuery query = aggregated("total-customer-debt").build(CustomerDebtProjection.class);
    ProjectionResponse<CustomerDebtProjection> response = projectionClient.query(query);
    CustomerDebtsResponseDto dto = toDto(response.data());

    return Response.ok(dto).build();
  }

  @GET
  @Path("shipping-stats")
  public Response getShippingStats() {

    ProjectionQuery query = aggregated("shipping-stats").build(ShippingStatsProjection.class);
    try {
      ProjectionResponse<ShippingStatsProjection> response = projectionClient.query(query);
      ShippingStatsResponseDto dto = toDto(response.data());
      return Response.ok(dto).build();
    } catch (ApiException ae) {
      if (ae.statusCode() == 404) {
        return Response.ok(new ShippingStatsResponseDto()).build();
      } else {
        throw ae;
      }
    }
  }

  private ShippingStatsResponseDto toDto(ShippingStatsProjection projection) {
    return new ShippingStatsResponseDto(projection.trackingNumbers, projection.shippedOrdersCount);
  }

  private CustomerDebtsResponseDto toDto(CustomerDebtProjection projection) {
    return new CustomerDebtsResponseDto(projection.totalCustomerDebt);
  }

  private OrdersResponseDto toDto(CustomerOrdersProjection customerOrdersProjection) {
    OrdersResponseDto orderResponseDto = new OrdersResponseDto();
    orderResponseDto.orders = customerOrdersProjection.orders.stream().map(orderData -> {
      OrderResponseDto orderResponse = new OrderResponseDto();
      orderResponse.orderId = orderData.aggregateId;
      orderResponse.orderAmount = orderData.orderAmount;
      orderResponse.status = orderData.status;
      orderResponse.trackingNumber = orderData.trackingNumber;
      return orderResponse;
    }).collect(toList());
    return orderResponseDto;
  }

  private OrderResponseDto toDto(OrderProjection orderProjection) {
    OrderResponseDto orderResponseDto = new OrderResponseDto();
    orderResponseDto.orderId = orderProjection.orderId;
    orderResponseDto.customerId = orderProjection.customerId;
    orderResponseDto.orderAmount = orderProjection.orderAmount;
    orderResponseDto.status = orderProjection.status;
    orderResponseDto.trackingNumber = orderProjection.trackingNumber;
    return orderResponseDto;
  }

  private OrdersResponseDto toDto(List<OrderProjection> orderProjections) {
    OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
    ordersResponseDto.orders = orderProjections.stream().map(this::toDto).collect(toList());
    return ordersResponseDto;
  }

}
