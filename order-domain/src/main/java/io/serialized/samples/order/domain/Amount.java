package io.serialized.samples.order.domain;

import static com.google.common.base.Preconditions.checkArgument;

public class Amount extends ValueObject {

  public static final Amount ZERO = new Amount(0);

  public final long amount;

  public Amount(long amount) {
    checkArgument(amount >= 0, "Amount cannot be negative");
    this.amount = amount;
  }

  public Amount subtract(long amountPaid) {
    return new Amount(this.amount - amountPaid);
  }

  public Amount difference(Amount other) {
    return new Amount(Math.abs(amount - other.amount));
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
