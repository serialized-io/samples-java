package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.ValueObject;

import java.util.UUID;
import java.util.regex.Pattern;

public class CustomerId extends ValueObject {

  private static final Pattern UUID_PATTERN = Pattern.compile("^(([0-9a-fA-F]){8}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){12})$");
  public final String id;

  public CustomerId(String id) {
    assertValidIdFormat(id);
    this.id = id;
  }

  private void assertValidIdFormat(String id) {
    if (!UUID_PATTERN.matcher(id).matches()) {
      throw new IllegalArgumentException("Invalid id format");
    }
  }

  public static final CustomerId newCustomer() {
    return new CustomerId(UUID.randomUUID().toString());
  }

}
