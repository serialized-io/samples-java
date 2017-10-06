package io.serialized.samples.aggregate.order;

public enum OrderStatus {

  NEW, PLACED, CANCELLED, PAID, SHIPPED;

  void assertNotYetPlaced() {
    if (NEW != this) throw new IllegalStateException("Expected order to be NEW!");
  }

  void assertPlaced() {
    if (PLACED != this) throw new IllegalStateException("Expected order to be PLACED!");
  }

  void assertPaid() {
    if (PAID != this) throw new IllegalStateException("Expected order to be PAID!");
  }

}
