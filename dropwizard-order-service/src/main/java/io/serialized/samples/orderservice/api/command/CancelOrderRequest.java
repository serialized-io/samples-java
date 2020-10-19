package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class CancelOrderRequest extends TransportObject {

  @NotNull
  public UUID orderId;

  public String reason;

}
