package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.*;

import java.util.List;

/**
 * Represents the immutable state of an {@link Order}.
 */
public class OrderState {

  public enum OrderStatus {
    NEW, PLACED, CANCELLED, PAID, SHIPPED;
  }

  public final String aggregateId;
  public final Integer version;
  public final OrderStatus orderStatus;
  public final long orderAmount;
  public final long paidAmount;
  public final String cancelReason;
  public final String trackingNumber;

  private OrderState(Builder builder) {
    aggregateId = builder.aggregateId;
    version = builder.version;
    orderStatus = builder.orderStatus;
    orderAmount = builder.orderAmount;
    paidAmount = builder.paidAmount;
    cancelReason = builder.cancelReason;
    trackingNumber = builder.trackingNumber;
  }

  public static OrderState loadFromEvents(String aggregateId, Integer version, List<AbstractOrderEvent> events) {
    Builder builder = OrderState.builder(aggregateId, version);
    for (AbstractOrderEvent event : events) {
      event.apply(builder);
    }
    return builder.build();
  }

  public static Builder builder(String aggregateId) {
    return new Builder(aggregateId, 0);
  }

  public static Builder builder(String aggregateId, Integer version) {
    return new Builder(aggregateId, version);
  }

  public static class Builder {
    private final String aggregateId;
    private final Integer version;

    private OrderStatus orderStatus = OrderStatus.NEW;
    private long orderAmount;
    private String cancelReason;
    private String trackingNumber;
    private long paidAmount;

    public Builder(String aggregateId, Integer version) {
      this.aggregateId = aggregateId;
      this.version = version;
    }

    public void apply(OrderPlacedEvent event) {
      this.orderStatus = OrderStatus.PLACED;
      this.orderAmount = event.data.orderAmount;
    }

    public void apply(OrderPaidEvent event) {
      this.orderStatus = OrderStatus.PAID;
      this.paidAmount = event.data.amount;
    }

    public void apply(OrderCancelledEvent event) {
      this.orderStatus = OrderStatus.CANCELLED;
      this.cancelReason = event.data.reason;
    }

    public void apply(OrderShippedEvent event) {
      this.orderStatus = OrderStatus.SHIPPED;
      this.trackingNumber = event.data.trackingNumber;
    }

    public OrderState build() {
      return new OrderState(this);
    }
  }

}
