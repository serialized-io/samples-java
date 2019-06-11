package io.serialized.samples.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class TodoListCompleted {

  private UUID listId;

  public static Event todoListCompleted(UUID listId) {
    TodoListCompleted event = new TodoListCompleted();
    event.listId = listId;
    return newEvent(event).build();
  }

  public UUID getListId() {
    return listId;
  }

}
