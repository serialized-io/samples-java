package io.serialized.samples.aggregate.order.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.serialized.samples.aggregate.order.OrderState;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Base class for all order events. Will be used by Jackson Serializer for polymorphism.
 */
@JsonTypeInfo(use = NAME, include = EXISTING_PROPERTY, property = "eventType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(OrderPlacedEvent.class),
    @JsonSubTypes.Type(OrderPaidEvent.class),
    @JsonSubTypes.Type(OrderCancelledEvent.class)
})
public abstract class AbstractOrderEvent implements Serializable {

  /**
   * Auto generated event ID.
   */
  public final String eventId = UUID.randomUUID().toString();

  /**
   * The event type discriminator field.
   */
  public final String eventType = getClass().getSimpleName();

  /**
   * Apply method for Double Dispatch, https://en.wikipedia.org/wiki/Double_dispatch.
   *
   * @param builder Mutable state builder
   */
  abstract public void apply(OrderState.Builder builder);

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
