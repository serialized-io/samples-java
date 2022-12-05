package io.serialized.samples.orderservice.api.command;

import io.serialized.samples.orderservice.api.TransportObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class PlaceOrderRequest extends TransportObject {

  @NotNull
  public UUID orderId;

  @NotNull
  public UUID customerId;

  @NotBlank
  public String sku;

  @Min(1)
  public long orderAmount;

}
