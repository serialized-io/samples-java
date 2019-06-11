package io.serialized.samples.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class TodoCompleted {

  private UUID todoId;

  public static Event<TodoCompleted> todoCompleted(UUID todoId) {
    TodoCompleted event = new TodoCompleted();
    event.todoId = todoId;
    return newEvent(event).build();
  }

  public UUID getTodoId() {
    return todoId;
  }

}
