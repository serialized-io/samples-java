package io.serialized.samples.aggregate.order;

import io.serialized.samples.aggregate.EventService;
import io.serialized.samples.aggregate.order.event.OrderEvent;

public interface OrderEventService extends EventService<OrderEvent, OrderState> {
}
