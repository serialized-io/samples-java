package io.serialized.samples.orderservice.domain;

public class IllegalOrderStateException extends RuntimeException {

  public IllegalOrderStateException(String message) {
    super(message);
  }

}
