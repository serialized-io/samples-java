package io.serialized.samples.orderservice.api.query.model;

import io.serialized.samples.orderservice.api.TransportObject;

public class CustomerDebtsResponseDto extends TransportObject {

  public final Long totalCustomerDebt;

  public CustomerDebtsResponseDto(Long totalCustomerDebt) {
    this.totalCustomerDebt = totalCustomerDebt;
  }

}
