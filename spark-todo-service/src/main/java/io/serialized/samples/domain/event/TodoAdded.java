package io.serialized.samples.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class TodoAdded {

  private UUID todoId;
  private String text;

  public static Event<TodoAdded> todoAdded(UUID todoId, String text) {
    TodoAdded event = new TodoAdded();
    event.todoId = todoId;
    event.text = text;
    return newEvent(event).build();
  }

  public UUID getTodoId() {
    return todoId;
  }

  public String getText() {
    return text;
  }

}
