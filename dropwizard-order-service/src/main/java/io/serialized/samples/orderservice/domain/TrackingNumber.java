package io.serialized.samples.orderservice.domain;

import java.util.UUID;

public class TrackingNumber extends ValueObject {

  public final String trackingNumber;

  public TrackingNumber(String trackingNumber) {
    this.trackingNumber = trackingNumber;
  }

  public static TrackingNumber newTrackingNumber() {
    return new TrackingNumber(UUID.randomUUID().toString());
  }

}
