package io.serialized.samples.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.domain.event.TodoAdded;
import io.serialized.samples.domain.event.TodoCompleted;
import io.serialized.samples.domain.event.TodoListCompleted;
import io.serialized.samples.domain.event.TodoListCreated;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TodoListState {

  private UUID listId;
  private boolean completed;
  private final Map<UUID, String> todos = new LinkedHashMap<>();
  private final Map<UUID, String> todosLeft = new LinkedHashMap<>();

  public TodoListState handleTodoListCreated(Event<TodoListCreated> event) {
    this.listId = event.getData().getListId();
    return this;
  }

  public TodoListState handleTodoAdded(Event<TodoAdded> event) {
    this.todos.put(event.getData().getTodoId(), event.getData().getText());
    this.todosLeft.put(event.getData().getTodoId(), event.getData().getText());
    return this;
  }

  public TodoListState handleTodoCompleted(Event<TodoCompleted> event) {
    this.todosLeft.remove(event.getData().getTodoId());
    return this;
  }

  public TodoListState handleTodoListCompleted(Event<TodoListCompleted> event) {
    this.completed = true;
    return this;
  }

  public UUID listId() {
    return listId;
  }

  public boolean completed() {
    return completed;
  }

  public Set<UUID> todosLeft() {
    return todosLeft.keySet();
  }

  public Set<UUID> todos() {
    return todos.keySet();
  }

}
