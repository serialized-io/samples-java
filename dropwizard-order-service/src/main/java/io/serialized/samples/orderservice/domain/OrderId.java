package io.serialized.samples.orderservice.domain;

import java.util.UUID;

public class OrderId extends ValueObject {

  private final UUID id;

  private OrderId(UUID id) {
    this.id = id;
  }

  public static OrderId fromString(String id) {
    return new OrderId(UUID.fromString(id));
  }

  public static OrderId fromUUID(UUID id) {
    return new OrderId(id);
  }

  public static OrderId newId() {
    return new OrderId(UUID.randomUUID());
  }

  public UUID asUUID() {
    return id;
  }

  public String asString() {
    return id.toString();
  }

}
