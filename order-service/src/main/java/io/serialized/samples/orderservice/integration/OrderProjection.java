package io.serialized.samples.orderservice.integration;

public class OrderProjection extends Projection {

  public ProjectionData data;

  public static class ProjectionData {
    public String customerId;
    public Long orderAmount;
    public String status;
    public String trackingNumber;
  }

}
