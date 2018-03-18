package io.serialized.samples.orderservice.integration;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventStoreService {

  @GET("aggregates/order/{id}")
  Observable<OrderAggregate> loadOrder(@Path("id") String id);

  @POST("aggregates/order/events")
  Observable<Void> saveOrderEvents(@Body EventBatch eventBatch);

}
