package io.serialized.samples.orderservice.integration;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventStoreService {

  @GET("aggregates/order/{aggregateId}")
  Observable<OrderAggregate> loadOrder(@Path("aggregateId") String id);

  @POST("aggregates/order/{aggregateId}/events")
  Observable<Void> saveOrderEvents(@Path("aggregateId") String id, @Body EventBatch eventBatch);

}
