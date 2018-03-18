package io.serialized.samples.orderservice.integration;

import java.util.List;

public class ShippingStatsProjection extends Projection {

  public ProjectionData data;

  public static class ProjectionData {
    public List<String> trackingNumbers;
    public Long shippedOrdersCount;
  }

}
