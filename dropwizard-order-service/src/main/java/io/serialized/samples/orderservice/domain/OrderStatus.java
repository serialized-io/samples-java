package io.serialized.samples.orderservice.domain;

public enum OrderStatus {

  NEW, PLACED, CANCELED, PAID, SHIPPED;

  void assertNotYetPlaced() {
    if (NEW != this) throw new IllegalOrderStateException("Expected order to be NEW but was " + this);
  }

  void assertPlaced() {
    if (PLACED != this) throw new IllegalOrderStateException("Expected order to be PLACED but was " + this);
  }

  void assertPaid() {
    if (PAID != this) throw new IllegalOrderStateException("Expected order to be PAID but was " + this);
  }

}
