package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;
import org.hibernate.validator.constraints.NotBlank;

public class CancelOrderRequest extends TransportObject {

  @NotBlank
  public String orderId;

  public String reason;

}
