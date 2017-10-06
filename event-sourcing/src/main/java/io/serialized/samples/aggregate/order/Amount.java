package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.ValueObject;

import static com.google.common.base.Preconditions.checkArgument;

public class Amount extends ValueObject {

  public static final Amount ZERO = new Amount(0);

  public final long amount;

  public Amount(long amount) {
    checkArgument(amount >= 0, "Amount cannot be negative");
    this.amount = amount;
  }

  public Amount clear(Amount amount) {
    if (this.amount == amount.amount) {
      return this;
    } else {
      throw new IllegalArgumentException("Wrong amount!");
    }
  }

}
