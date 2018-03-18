package io.serialized.samples.order.domain;

import io.serialized.samples.order.domain.event.OrderCancelledEvent;
import io.serialized.samples.order.domain.event.OrderEvent;
import io.serialized.samples.order.domain.event.OrderFullyPaidEvent;
import io.serialized.samples.order.domain.event.OrderPlacedEvent;
import io.serialized.samples.order.domain.event.OrderShippedEvent;
import io.serialized.samples.order.domain.event.PaymentExceededOrderAmountEvent;
import io.serialized.samples.order.domain.event.PaymentReceivedEvent;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Represents the immutable state of an {@link Order}.
 */
public class OrderState {

  public final OrderId orderId;
  public final Integer version;
  public final OrderStatus orderStatus;
  public final Amount orderAmount;
  public final String cancelReason;
  public final String trackingNumber;

  private OrderState(Builder builder) {
    orderId = builder.orderId;
    version = builder.version;
    orderStatus = builder.orderStatus;
    orderAmount = builder.orderAmount;
    cancelReason = builder.cancelReason;
    trackingNumber = builder.trackingNumber;
  }

  public static OrderState loadFromEvents(OrderId orderId, Integer version, List<OrderEvent> events) {
    return OrderState.builder(orderId, version).apply(events).build();
  }

  public static Builder builder(OrderId orderId) {
    return new Builder(orderId, 0);
  }

  public static Builder builder(OrderId orderId, Integer version) {
    return new Builder(orderId, version);
  }

  public static class Builder {
    private final OrderId orderId;
    private final Integer version;

    private OrderStatus orderStatus = OrderStatus.NEW;
    private Amount orderAmount;
    private String cancelReason;
    private String trackingNumber;

    public Builder(OrderId orderId, Integer version) {
      this.orderId = orderId;
      this.version = version;
    }

    public Builder apply(List<OrderEvent> events) {
      events.forEach(event -> event.apply(this));
      return this;
    }

    public Builder apply(OrderPlacedEvent event) {
      this.orderStatus = OrderStatus.PLACED;
      this.orderAmount = new Amount(event.data.orderAmount);
      return this;
    }

    public Builder apply(PaymentReceivedEvent event) {
      this.orderAmount = this.orderAmount.subtract(event.data.amountPaid);
      return this;
    }

    public Builder apply(OrderFullyPaidEvent event) {
      this.orderStatus = OrderStatus.PAID;
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

    public Builder apply(PaymentExceededOrderAmountEvent event) {
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
