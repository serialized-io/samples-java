package io.serialized.samples.order.domain;

import java.util.UUID;
import java.util.regex.Pattern;

public class Id extends ValueObject {

  private static final Pattern UUID_PATTERN = Pattern.compile("^(([0-9a-fA-F]){8}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){12})$");
  public final String id;

  public Id(String id) {
    assertValidIdFormat(id);
    this.id = id;
  }

  private void assertValidIdFormat(String id) {
    if (!UUID_PATTERN.matcher(id).matches()) {
      throw new IllegalArgumentException("Invalid id format: " + id);
    }
  }

  public static final Id newId() {
    return new Id(UUID.randomUUID().toString());
  }

}
