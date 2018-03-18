package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;
import org.hibernate.validator.constraints.NotBlank;

public class ShipOrderRequest extends TransportObject {

  @NotBlank
  public String orderId;

  @NotBlank
  public String trackingNumber;

}
