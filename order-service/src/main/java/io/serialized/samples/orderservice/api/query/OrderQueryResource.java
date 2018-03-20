package io.serialized.samples.orderservice.api.query;

import com.google.common.collect.ImmutableMap;
import io.dropwizard.jersey.params.IntParam;
import io.serialized.samples.orderservice.integration.*;
import retrofit2.HttpException;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.*;

@Path("/queries")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderQueryResource {

  private final ProjectionService projectionService;

  public OrderQueryResource(ProjectionService projectionService) {
    this.projectionService = projectionService;
  }

  @GET
  @Path("orders")
  public void getOrders(@QueryParam("status") String status,
                        @QueryParam("skip") @DefaultValue("0") IntParam skip,
                        @QueryParam("limit") @DefaultValue("100") IntParam limit,
                        @Suspended AsyncResponse asyncResponse) {
    projectionService.findOrdersByStatus(status, skip.get(), limit.get())
        .map(this::toDto)
        .subscribe(
            responseDto -> asyncResponse.resume(ok(responseDto).build()),
            onError -> asyncResponse.resume(createErrorResponse(onError))
        );
  }

  @GET
  @Path("customers/{customerId}/orders")
  public void getOrdersPerCustomer(@PathParam("customerId") String customerId, @Suspended AsyncResponse asyncResponse) {
    projectionService.findOrdersByCustomer(customerId)
        .map(this::toDto)
        .subscribe(
            responseDto -> asyncResponse.resume(ok(responseDto).build()),
            onError -> asyncResponse.resume(createErrorResponse(onError))
        );
  }

  @GET
  @Path("orders/{orderId}")
  public void getOrder(@PathParam("orderId") String orderId, @Suspended AsyncResponse asyncResponse) {
    projectionService.getOrder(orderId)
        .map(this::toDto)
        .subscribe(
            responseDto -> asyncResponse.resume(ok(responseDto).build()),
            onError -> asyncResponse.resume(createErrorResponse(onError))
        );
  }

  @GET
  @Path("total-customer-debt")
  public void getCustomerDebt(@Suspended AsyncResponse asyncResponse) {
    projectionService.getCustomerDebt()
        .map(this::toDto)
        .subscribe(
            responseDto -> asyncResponse.resume(ok(responseDto).build()),
            onError -> asyncResponse.resume(createErrorResponse(onError))
        );
  }

  @GET
  @Path("shipping-stats")
  public void getShippingStats(@Suspended AsyncResponse asyncResponse) {
    projectionService.getShippingStats()
        .map(this::toDto)
        .subscribe(
            responseDto -> asyncResponse.resume(ok(responseDto).build()),
            onError -> asyncResponse.resume(createErrorResponse(onError))
        );
  }

  private ShippingStatsResponseDto toDto(ShippingStatsProjection projection) {
    ShippingStatsResponseDto shippingStatsResponseDto = new ShippingStatsResponseDto();
    shippingStatsResponseDto.trackingNumbers = projection.data.trackingNumbers;
    shippingStatsResponseDto.shippedOrdersCount = projection.data.shippedOrdersCount;
    return shippingStatsResponseDto;
  }

  private CustomerDebtsResponseDto toDto(CustomerDebtProjection projection) {
    CustomerDebtsResponseDto responseDto = new CustomerDebtsResponseDto();
    responseDto.totalCustomerDebt = projection.data.totalCustomerDebt;
    return responseDto;
  }

  private OrdersResponseDto toDto(CustomerOrdersProjection customerOrdersProjection) {
    OrdersResponseDto orderResponseDto = new OrdersResponseDto();
    orderResponseDto.orders = customerOrdersProjection.data.orders.stream().map(orderData -> {
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
    orderResponseDto.orderId = orderProjection.projectionId;
    orderResponseDto.customerId = orderProjection.data.customerId;
    orderResponseDto.orderAmount = orderProjection.data.orderAmount;
    orderResponseDto.status = orderProjection.data.status;
    orderResponseDto.trackingNumber = orderProjection.data.trackingNumber;
    return orderResponseDto;
  }

  private OrdersResponseDto toDto(OrderProjections orderProjections) {
    OrdersResponseDto ordersResponseDto = new OrdersResponseDto();
    ordersResponseDto.orders = orderProjections.projections.stream().map(this::toDto).collect(toList());
    return ordersResponseDto;
  }

  private Response createErrorResponse(Throwable throwable) {
    if (throwable instanceof HttpException) {
      return status(((HttpException) throwable).code()).entity(ImmutableMap.of("error", throwable.getMessage())).build();
    } else {
      return serverError().build();
    }
  }

}
