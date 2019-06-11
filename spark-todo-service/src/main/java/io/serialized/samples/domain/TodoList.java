package io.serialized.samples.domain;

import io.serialized.client.aggregate.Event;
import org.apache.commons.lang3.Validate;

import java.util.*;

import static io.serialized.samples.domain.event.TodoAdded.todoAdded;
import static io.serialized.samples.domain.event.TodoCompleted.todoCompleted;
import static io.serialized.samples.domain.event.TodoListCompleted.todoListCompleted;
import static io.serialized.samples.domain.event.TodoListCreated.todoListCreated;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class TodoList {

  private final UUID listId;
  private final Set<UUID> todos = new LinkedHashSet<>();
  private final Set<UUID> todosLeft = new LinkedHashSet<>();

  public TodoList(TodoListState state) {
    this.listId = state.listId();
    this.todos.addAll(state.todos());
    this.todosLeft.addAll(state.todosLeft());
  }

  public List<Event> createNew(UUID listId, String name) {
    Validate.notEmpty(trimToEmpty(name), "List must have a name");
    return singletonList(todoListCreated(listId, name));
  }

  public List<Event> addTodo(UUID todoId, String text) {
    Validate.notEmpty(trimToEmpty(text), "Todo must have a text");
    if (todos.contains(todoId)) {
      return emptyList();
    } else {
      return singletonList(todoAdded(todoId, text));
    }
  }

  public List<Event> completeTodo(UUID todoId) {
    if (this.todosLeft.contains(todoId)) {
      List<Event> events = new ArrayList<>();
      events.add(todoCompleted(todoId));
      if (todosLeft.size() == 1) {
        events.add(todoListCompleted(listId));
      }
      return events;
    } else {
      return emptyList();
    }
  }

}
