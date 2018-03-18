package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

public class PayOrderRequest extends TransportObject {

  @NotBlank
  public String orderId;

  @Min(1)
  public long amount;

}
