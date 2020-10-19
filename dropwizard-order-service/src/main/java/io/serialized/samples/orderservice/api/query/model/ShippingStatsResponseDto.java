package io.serialized.samples.orderservice.api.query.model;

import io.serialized.samples.orderservice.api.TransportObject;

import java.util.ArrayList;
import java.util.List;

public class ShippingStatsResponseDto extends TransportObject {

  public final List<String> trackingNumbers;
  public final Long shippedOrdersCount;

  public ShippingStatsResponseDto() {
    this.trackingNumbers = new ArrayList<>();
    this.shippedOrdersCount = 0L;
  }

  public ShippingStatsResponseDto(List<String> trackingNumbers, Long shippedOrdersCount) {
    this.trackingNumbers = trackingNumbers;
    this.shippedOrdersCount = shippedOrdersCount;
  }

}
