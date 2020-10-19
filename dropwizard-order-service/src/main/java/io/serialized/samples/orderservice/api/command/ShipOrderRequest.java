package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ShipOrderRequest extends TransportObject {

  @NotNull
  public UUID orderId;

  @NotBlank
  public String trackingNumber;

}
