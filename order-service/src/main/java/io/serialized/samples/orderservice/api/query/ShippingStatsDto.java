package io.serialized.samples.orderservice.api.query;

import io.serialized.samples.orderservice.api.TransportObject;

import java.util.List;

public class ShippingStatsDto extends TransportObject {

  public List<String> trackingNumbers;
  public Long shippedOrdersCount;

}
