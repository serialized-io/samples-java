package io.serialized.samples.order.domain;

public class CustomerId extends Id {

  public CustomerId(String id) {
    super(id);
  }

  public static final CustomerId newCustomerId() {
    return new CustomerId(newId().id);
  }

}
