package io.serialized.samples.orderservice.domain;

import java.util.UUID;

public class CustomerId extends ValueObject {

  private final UUID id;

  private CustomerId(UUID id) {
    this.id = id;
  }

  public static CustomerId fromString(String id) {
    return new CustomerId(UUID.fromString(id));
  }

  public static CustomerId fromUUID(UUID id) {
    return new CustomerId(id);
  }

  public static CustomerId newId() {
    return new CustomerId(UUID.randomUUID());
  }

  public UUID asUUID() {
    return id;
  }

  public String asString() {
    return id.toString();
  }

}
