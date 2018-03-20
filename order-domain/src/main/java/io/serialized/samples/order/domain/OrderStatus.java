package io.serialized.samples.order.domain;

public enum OrderStatus {

  NEW, PLACED, CANCELLED, PAID, SHIPPED;

  void assertNotYetPlaced() {
    if (NEW != this) throw new IllegalOrderStateException("Expected order to be NEW! was" + this);
  }

  void assertPlaced() {
    if (PLACED != this) throw new IllegalOrderStateException("Expected order to be PLACED! was: " + this);
  }

  void assertPaid() {
    if (PAID != this) throw new IllegalOrderStateException("Expected order to be PAID! was: " + this);
  }

}
