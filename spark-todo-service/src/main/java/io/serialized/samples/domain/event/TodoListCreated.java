package io.serialized.samples.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class TodoListCreated {

  private UUID listId;
  private String name;

  public static Event<TodoListCreated> todoListCreated(UUID listId, String name) {
    TodoListCreated event = new TodoListCreated();
    event.listId = listId;
    event.name = name;
    return newEvent(event).build();
  }

  public UUID getListId() {
    return listId;
  }

  public String getName() {
    return name;
  }

}
