package io.serialized.samples.order.domain;

public class OrderId extends Id {

  public OrderId(String id) {
    super(id);
  }

  public static final OrderId newOrderId() {
    return new OrderId(newId().id);
  }

}
