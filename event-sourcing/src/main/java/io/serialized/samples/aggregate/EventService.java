package io.serialized.samples.aggregate;

import java.util.List;
import java.util.UUID;

public interface EventService<E, T> {

  T load(UUID aggregateId);

  T loadFromEvents(String aggregateId, Integer aggregateVersion, List<E> events);

  void saveEvent(String aggregateId, Integer expectedVersion, E event);

  void saveEvents(String aggregateId, Integer expectedVersion, List<E> events);
}
