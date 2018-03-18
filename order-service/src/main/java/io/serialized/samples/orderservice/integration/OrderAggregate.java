package io.serialized.samples.orderservice.integration;

import io.serialized.samples.order.domain.event.OrderEvent;

import java.util.List;

public class OrderAggregate {

  public String aggregateId;
  public Integer aggregateVersion;
  public List<OrderEvent> events;

}
