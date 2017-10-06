package io.serialized.samples.aggregate.order;

public enum OrderStatus {
  NEW, PLACED, CANCELLED, PAID, SHIPPED;


  void assertNotYetPlaced() {
    if (OrderStatus.NEW != this) throw new IllegalStateException("Expected order to be NEW!");
  }

  void assertPlaced() {
    if (OrderStatus.PLACED != this) throw new IllegalStateException("Expected order to be PLACED!");
  }

  void assertPaid() {
    if (OrderStatus.PAID != this) throw new IllegalStateException("Expected order to be PAID!");
  }

}
