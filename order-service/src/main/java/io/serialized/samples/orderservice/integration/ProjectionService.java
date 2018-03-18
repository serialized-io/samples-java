package io.serialized.samples.orderservice.integration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.Map;

public interface ProjectionService {

  @PUT("projections/definitions/{name}")
  Call<Void> createOrUpdateDefinition(@Path("name") String name, @Body Map definition);

  @GET("projections/single/orders/{orderId}")
  Call<OrderProjection> getOrder(@Path("orderId") String orderId);

  @GET("projections/aggregated/shipping-stats")
  Call<ShippingStatsProjection> getShippingStats();

}
