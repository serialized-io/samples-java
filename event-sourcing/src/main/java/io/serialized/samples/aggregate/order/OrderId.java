package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.ValueObject;

import java.util.UUID;
import java.util.regex.Pattern;

public class OrderId extends ValueObject {

  private static final Pattern UUID_PATTERN = Pattern.compile("^(([0-9a-fA-F]){8}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){12})$");
  public final UUID id;

  public OrderId(String id) {
    assertValidIdFormat(id);
    this.id = UUID.fromString(id);
  }

  private void assertValidIdFormat(String id) {
    if (!UUID_PATTERN.matcher(id).matches()) {
      throw new IllegalArgumentException("Invalid id format");
    }
  }

  public static final OrderId newOrder() {
    return new OrderId(UUID.randomUUID().toString());
  }

}
