package io.serialized.samples.orderservice.integration;

public class CustomerDebtProjection extends Projection {

  public ProjectionData data;

  public static class ProjectionData {
    public Long totalCustomerDebt;
  }


}
