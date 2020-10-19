package io.serialized.samples.orderservice.api.query.model;

import io.serialized.samples.orderservice.api.TransportObject;

import java.util.List;

public class OrdersResponseDto extends TransportObject {

  public List<OrderResponseDto> orders;

}
