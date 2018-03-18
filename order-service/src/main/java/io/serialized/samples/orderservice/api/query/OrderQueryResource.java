package io.serialized.samples.orderservice.api.query;

import io.serialized.samples.orderservice.integration.OrderProjection;
import io.serialized.samples.orderservice.integration.ProjectionService;
import io.serialized.samples.orderservice.integration.ShippingStatsProjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/queries")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class OrderQueryResource {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ProjectionService projectionService;

  public OrderQueryResource(ProjectionService projectionService) {
    this.projectionService = projectionService;
  }

  @GET
  @Path("orders/{orderId}")
  public Response getOrder(@PathParam("orderId") String orderId) {
    try {
      OrderProjection projection = getResponseBody(projectionService.getOrder(orderId).execute());
      return Response.ok(toDto(projection)).build();
    } catch (IOException e) {
      logger.warn("Error getting order projection", e);
      return Response.serverError().build();
    }
  }

  @GET
  @Path("shipping-stats")
  public Response getShippingStats() {
    try {
      ShippingStatsProjection projection = getResponseBody(projectionService.getShippingStats().execute());
      return Response.ok(toDto(projection)).build();
    } catch (IOException e) {
      logger.warn("Error getting order projection", e);
      return Response.serverError().build();
    }
  }

  private <T> T getResponseBody(retrofit2.Response<T> response) {
    if (!response.isSuccessful() || response.body() == null) {
      logger.warn("Failed to get projection - response [{}]: {}", response.code(), response.errorBody());
      throw new WebApplicationException(Response.status(response.code()).build());
    }
    return response.body();
  }

  private ShippingStatsDto toDto(ShippingStatsProjection projection) {
    ShippingStatsDto shippingStatsDto = new ShippingStatsDto();
    shippingStatsDto.trackingNumbers = projection.data.trackingNumbers;
    shippingStatsDto.shippedOrdersCount = projection.data.shippedOrdersCount;
    return shippingStatsDto;
  }

  private OrderDto toDto(OrderProjection orderProjection) {
    OrderDto orderDto = new OrderDto();
    orderDto.orderId = orderProjection.projectionId;
    orderDto.customerId = orderProjection.data.customerId;
    orderDto.orderAmount = orderProjection.data.orderAmount;
    orderDto.status = orderProjection.data.status;
    orderDto.trackingNumber = orderProjection.data.trackingNumber;
    return orderDto;
  }

}
