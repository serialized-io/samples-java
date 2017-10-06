package io.serialized.samples.aggregate;

import java.util.List;
import java.util.UUID;

public interface EventStore<E, T> {

  T load(UUID aggregateId);

  T loadFromEvents(String aggregateId, Integer aggregateVersion, List<E> events);

  void saveEvent(String aggregateId, Integer expectedVersion, E event);

}
