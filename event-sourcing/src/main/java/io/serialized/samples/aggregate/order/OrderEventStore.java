package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.EventStore;
import io.serialized.samples.aggregate.order.event.OrderEvent;

public interface OrderEventStore extends EventStore<OrderEvent, OrderState> {
}
