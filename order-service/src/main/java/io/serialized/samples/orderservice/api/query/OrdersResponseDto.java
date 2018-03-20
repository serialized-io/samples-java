package io.serialized.samples.orderservice.api.query;

import io.serialized.samples.orderservice.api.TransportObject;

import java.util.List;

public class OrdersResponseDto extends TransportObject {

  public List<OrderResponseDto> orders;

}
