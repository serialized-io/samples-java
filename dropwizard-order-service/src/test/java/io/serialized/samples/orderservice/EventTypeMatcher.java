package io.serialized.samples.orderservice;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.EventBatch;
import org.mockito.ArgumentMatcher;

import java.util.Arrays;
import java.util.List;

public class EventTypeMatcher implements ArgumentMatcher<EventBatch> {

  private final List<String> eventTypes;

  private EventTypeMatcher(String... eventTypes) {
    this.eventTypes = Arrays.asList(eventTypes);
  }

  public static EventTypeMatcher containsEventType(String... eventTypes) {
    return new EventTypeMatcher(eventTypes);
  }

  @Override
  public boolean matches(EventBatch argument) {
    for (Event<?> event : argument.events()) {
      if (!eventTypes.contains(event.eventType())) {
        return false;
      }
    }
    return true;
  }

}
