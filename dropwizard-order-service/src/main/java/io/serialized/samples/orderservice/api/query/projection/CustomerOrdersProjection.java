package io.serialized.samples.orderservice.api.query.projection;

import java.util.List;

public class CustomerOrdersProjection {

  public List<OrderData> orders;

  public static class OrderData {
    public String aggregateId;
    public Long orderAmount;
    public String status;
    public String trackingNumber;
  }

}
