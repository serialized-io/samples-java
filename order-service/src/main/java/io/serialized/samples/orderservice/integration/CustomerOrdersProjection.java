package io.serialized.samples.orderservice.integration;

import io.serialized.samples.orderservice.api.TransportObject;

import java.util.List;

public class CustomerOrdersProjection extends Projection {

  public ProjectionData data;

  public static class ProjectionData extends TransportObject {
    public List<OrderData> orders;
  }

  public static class OrderData {
    public String aggregateId;
    public Long orderAmount;
    public String status;
    public String trackingNumber;
  }

}
