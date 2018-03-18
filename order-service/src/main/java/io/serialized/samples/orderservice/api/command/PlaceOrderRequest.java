package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

public class PlaceOrderRequest extends TransportObject {

  @NotBlank
  public String orderId;

  @NotBlank
  public String customerId;

  @Min(1)
  public long orderAmount;

}
