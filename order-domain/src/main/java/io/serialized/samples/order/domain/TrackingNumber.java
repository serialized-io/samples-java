package io.serialized.samples.order.domain;

import java.util.UUID;
import java.util.regex.Pattern;

public class TrackingNumber extends ValueObject {

  private static final Pattern UUID_PATTERN = Pattern.compile("^(([0-9a-fA-F]){8}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){12})$");
  public final String trackingNumber;

  public TrackingNumber(String trackingNumber) {
    assertValidTrackingNumberFormat(trackingNumber);
    this.trackingNumber = trackingNumber;
  }

  private void assertValidTrackingNumberFormat(String trackingNumber) {
    if (!UUID_PATTERN.matcher(trackingNumber).matches()) {
      throw new IllegalArgumentException("Invalid trackingNumber format");
    }
  }


  public static TrackingNumber newTrackingNumber() {
    return new TrackingNumber(UUID.randomUUID().toString());
  }
}
