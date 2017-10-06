package io.serialized.samples.aggregate.order;

public class Amount extends ValueObject {

  public static final Amount ZERO = new Amount(0);

  public final long amount;

  public Amount(long amount) {
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
