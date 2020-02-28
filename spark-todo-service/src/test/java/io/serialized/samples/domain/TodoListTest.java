package io.serialized.samples.domain;

import io.serialized.client.aggregate.AggregateFactory;
import io.serialized.client.aggregate.Command;
import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.StateBuilder;
import io.serialized.samples.domain.event.TodoAdded;
import io.serialized.samples.domain.event.TodoCompleted;
import io.serialized.samples.domain.event.TodoListCompleted;
import io.serialized.samples.domain.event.TodoListCreated;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TodoListTest {

  private final StateBuilder<TodoListState> todoListStateBuilder = StateBuilder.stateBuilder(TodoListState.class)
      .withHandler(TodoListCreated.class, TodoListState::handleTodoListCreated)
      .withHandler(TodoAdded.class, TodoListState::handleTodoAdded)
      .withHandler(TodoCompleted.class, TodoListState::handleTodoCompleted)
      .withHandler(TodoListCompleted.class, TodoListState::handleTodoListCompleted);

  private final AggregateFactory<TodoList, TodoListState> todoListFactory = AggregateFactory.newFactory(TodoList::new, todoListStateBuilder);

  @Test
  public void testCreateNew() {
    TodoList todoList = todoListFactory.fromCommands(Collections.emptyList());

    UUID listId = UUID.randomUUID();
    String name = "My List";

    List<Event<?>> listEvents = todoList.createNew(listId, name);
    assertThat(listEvents.size(), is(1));

    Event<TodoListCreated> todoListCreated = firstEventOfType(listEvents, TodoListCreated.class);
    assertThat(todoListCreated.getData().getListId(), is(listId));
    assertThat(todoListCreated.getData().getName(), is(name));
  }

  @Test
  public void addTodo() {
    TodoList todoList = todoListFactory.fromCommands(
        createNewList(UUID.randomUUID(), "My List")
    );

    UUID todoId = UUID.randomUUID();
    String todoText = "Buy milk";

    List<Event<?>> listEvents = todoList.addTodo(todoId, todoText);
    assertThat(listEvents.size(), is(1));

    Event<TodoAdded> todoAdded = firstEventOfType(listEvents, TodoAdded.class);
    assertThat(todoAdded.getData().getTodoId(), is(todoId));
    assertThat(todoAdded.getData().getText(), is(todoText));
  }

  @Test
  public void completeTodo() {
    UUID listId = UUID.randomUUID();
    UUID todoId = UUID.randomUUID();

    TodoList todoList = todoListFactory.fromCommands(
        createNewList(listId, "My List"),
        addTodo(todoId, "Buy milk")
    );

    List<Event<?>> listEvents = todoList.completeTodo(todoId);
    assertThat(listEvents.size(), is(2));

    Event<TodoCompleted> todoCompleted = firstEventOfType(listEvents, TodoCompleted.class);
    assertThat(todoCompleted.getData().getTodoId(), is(todoId));

    Event<TodoListCompleted> todoListCompleted = firstEventOfType(listEvents, TodoListCompleted.class);
    assertThat(todoListCompleted.getData().getListId(), is(listId));
  }

  private Command<TodoList> createNewList(UUID listId, String name) {
    return g -> g.createNew(listId, name);
  }

  private Command<TodoList> addTodo(UUID todoId, String text) {
    return g -> g.addTodo(todoId, text);
  }

  private <T> Event<T> firstEventOfType(List<Event<?>> events, Class<T> clazz) {
    return (Event<T>) events.stream()
        .filter(e -> e.getEventType().equals(clazz.getSimpleName())).findFirst()
        .orElseThrow(() -> new RuntimeException("Missing event"));
  }


}

