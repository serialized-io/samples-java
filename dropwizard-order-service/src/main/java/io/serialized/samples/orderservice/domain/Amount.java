package io.serialized.samples.orderservice.domain;

public class Amount extends ValueObject {

  public static final Amount ZERO = new Amount(0);

  public final long amount;

  public Amount(long amount) {
    this.amount = amount;
  }

  public Amount subtract(Amount other) {
    return new Amount(this.amount - other.amount);
  }

  public Amount pay(Amount other) {
    Amount difference = subtract(other);
    if (difference.isPositive()) {
      return difference;
    } else {
      throw new IllegalArgumentException("Payment exceeds total amount");
    }
  }

  public boolean isPositive() {
    return amount > 0;
  }

  public boolean largerThan(Amount other) {
    return amount > other.amount;
  }

  public boolean largerThanEq(Amount other) {
    return amount >= other.amount;
  }

}
