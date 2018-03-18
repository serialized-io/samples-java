package io.serialized.samples.aggregate;

import java.util.List;
import java.util.UUID;

public interface EventService<E, T> {

  T load(UUID aggregateId);

  T loadFromEvents(UUID aggregateId, Integer aggregateVersion, List<E> events);

  void saveEvent(UUID aggregateId, Integer expectedVersion, E event);

  void saveEvents(UUID aggregateId, Integer expectedVersion, List<E> events);
}
