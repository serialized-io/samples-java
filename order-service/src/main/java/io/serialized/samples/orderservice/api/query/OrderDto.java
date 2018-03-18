package io.serialized.samples.orderservice.api.query;

import io.serialized.samples.orderservice.api.TransportObject;

public class OrderDto extends TransportObject {

  public String orderId;
  public String customerId;
  public Long orderAmount;
  public String status;
  public String trackingNumber;

}
