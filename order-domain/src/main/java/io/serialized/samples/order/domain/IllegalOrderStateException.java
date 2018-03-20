package io.serialized.samples.order.domain;

public class IllegalOrderStateException extends RuntimeException {

  public IllegalOrderStateException(String message) {
    super(message);
  }

}
