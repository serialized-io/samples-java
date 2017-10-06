package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.order.event.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Represents the immutable state of an {@link Order}.
 */
public class OrderState {

  public final String orderId;
  public final Integer version;
  public final OrderStatus orderStatus;
  public final Amount orderAmount;
  public final long paidAmount;
  public final String cancelReason;
  public final String trackingNumber;

  private OrderState(Builder builder) {
    orderId = builder.orderId;
    version = builder.version;
    orderStatus = builder.orderStatus;
    orderAmount = builder.orderAmount;
    paidAmount = builder.paidAmount;
    cancelReason = builder.cancelReason;
    trackingNumber = builder.trackingNumber;
  }

  public static OrderState loadFromEvents(String orderId, Integer version, List<OrderEvent> events) {
    Builder builder = OrderState.builder(orderId, version);
    for (OrderEvent event : events) {
      event.apply(builder);
    }
    return builder.build();
  }

  public static Builder builder(OrderId orderId) {
    return new Builder(orderId.id.toString(), 0);
  }

  public static Builder builder(String orderId, Integer version) {
    return new Builder(orderId, version);
  }

  public static class Builder {
    private final String orderId;
    private final Integer version;

    private OrderStatus orderStatus = OrderStatus.NEW;
    private Amount orderAmount;
    private String cancelReason;
    private String trackingNumber;
    private long paidAmount;

    public Builder(String orderId, Integer version) {
      this.orderId = orderId;
      this.version = version;
    }

    public Builder apply(OrderPlacedEvent event) {
      this.orderStatus = OrderStatus.PLACED;
      this.orderAmount = new Amount(event.data.orderAmount);
      return this;
    }

    public Builder apply(OrderPaidEvent event) {
      this.orderStatus = OrderStatus.PAID;
      this.paidAmount = event.data.amount;
      return this;
    }

    public Builder apply(OrderCancelledEvent event) {
      this.orderStatus = OrderStatus.CANCELLED;
      this.cancelReason = event.data.reason;
      return this;
    }

    public Builder apply(OrderShippedEvent event) {
      this.orderStatus = OrderStatus.SHIPPED;
      this.trackingNumber = event.data.trackingNumber;
      return this;
    }

    public OrderState build() {
      return new OrderState(this);
    }
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
  }

}
