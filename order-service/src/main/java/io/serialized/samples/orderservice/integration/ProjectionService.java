package io.serialized.samples.orderservice.integration;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ProjectionService {

  @PUT("projections/definitions/{name}")
  Call<Void> createOrUpdateDefinition(@Path("name") String name, @Body Map definition);

  @GET("projections/single/orders/{orderId}")
  Observable<OrderProjection> getOrder(@Path("orderId") String orderId);

  @GET("projections/single/orders")
  Observable<OrderProjections> findOrdersByStatus(@Query("reference") String status, @Query("skip") int skip, @Query("limit") int limit);

  @GET("projections/aggregated/shipping-stats")
  Observable<ShippingStatsProjection> getShippingStats();

}
