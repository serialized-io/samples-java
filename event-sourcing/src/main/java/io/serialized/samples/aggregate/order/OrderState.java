package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.AbstractOrderEvent;
import io.serialized.samples.aggregate.order.event.OrderCancelledEvent;
import io.serialized.samples.aggregate.order.event.OrderPaidEvent;
import io.serialized.samples.aggregate.order.event.OrderPlacedEvent;

import java.util.List;

/**
 * Represents the immutable state of an {@link Order}.
 */
public class OrderState {

  public enum OrderStatus {
    NEW, PLACED, CANCELLED
  }

  public final String aggregateId;
  public final Integer version;
  public final OrderStatus orderStatus;
  public final long orderAmount;
  public final boolean paid;

  private OrderState(Builder builder) {
    aggregateId = builder.aggregateId;
    version = builder.version;
    orderStatus = builder.orderStatus;
    orderAmount = builder.orderAmount;
    paid = builder.paid;
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
    private boolean paid;

    public Builder(String aggregateId, Integer version) {
      this.aggregateId = aggregateId;
      this.version = version;
    }

    public void apply(OrderPlacedEvent event) {
      this.orderStatus = OrderStatus.PLACED;
      this.orderAmount = event.data.orderAmount;
    }

    public void apply(OrderPaidEvent event) {
      this.paid = true;
    }

    public void apply(OrderCancelledEvent event) {
      this.orderStatus = OrderStatus.CANCELLED;
    }

    public OrderState build() {
      return new OrderState(this);
    }
  }

}
