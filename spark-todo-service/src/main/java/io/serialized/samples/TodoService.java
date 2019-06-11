package io.serialized.samples;

import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.RawData;
import io.serialized.client.projection.query.ListProjectionQuery;
import io.serialized.client.projection.query.SingleProjectionQuery;
import io.serialized.samples.api.CompleteTodoRequest;
import io.serialized.samples.api.CreateTodoListRequest;
import io.serialized.samples.api.CreateTodoRequest;
import io.serialized.samples.domain.TodoList;
import io.serialized.samples.domain.TodoListState;
import io.serialized.samples.domain.event.TodoAdded;
import io.serialized.samples.domain.event.TodoCompleted;
import io.serialized.samples.domain.event.TodoListCompleted;
import io.serialized.samples.domain.event.TodoListCreated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.serialized.client.aggregate.AggregateClient.aggregateClient;
import static io.serialized.client.projection.Filter.filter;
import static io.serialized.client.projection.Function.*;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.Selector.eventSelector;
import static io.serialized.client.projection.Selector.targetSelector;
import static io.serialized.samples.JsonConverter.fromJson;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static spark.Spark.*;

public class TodoService {

  private static final String LIST_TYPE = "list";
  private static final String LISTS_PROJECTION = "lists";

  private static final AggregateClient<TodoListState> listClient;
  private static final ProjectionClient projectionClient;

  static {
    SerializedClientConfig serializedClientConfig = getConfig();

    listClient = aggregateClient(LIST_TYPE, TodoListState.class, serializedClientConfig)
        .registerHandler(TodoListCreated.class, TodoListState::handleTodoListCreated)
        .registerHandler(TodoAdded.class, TodoListState::handleTodoAdded)
        .registerHandler(TodoCompleted.class, TodoListState::handleTodoCompleted)
        .registerHandler(TodoListCompleted.class, TodoListState::handleTodoListCompleted)
        .build();

    projectionClient = ProjectionClient.projectionClient(serializedClientConfig).build();

    // Make sure projection is configured in Serialized
    projectionClient.createOrUpdate(
        singleProjection(LISTS_PROJECTION)
            .feed(LIST_TYPE)
            .addHandler("TodoListCreated",
                set(targetSelector("name"),
                    eventSelector("name")))
            .addHandler("TodoAdded",
                prepend(targetSelector("todos")))
            .addHandler("TodoCompleted",
                merge(targetSelector("todos[?]"),
                    filter("[?(@.todoId == $.event.todoId)]"),
                    RawData.fromString("{\"status\" : \"COMPLETED\"}")))
            .addHandler("TodoListCompleted",
                set(targetSelector("status"),
                    RawData.fromString("COMPLETED")))
            .build());
  }

  public static void main(String[] args) {
    port(8080);

    exception(IllegalArgumentException.class, (exception, request, response) -> {
      response.status(400);
      response.type("application/json");
      response.body("{\"message\":\"" + exception.getMessage() + "\"}");
    });

    before((request, response) -> response.type("application/json"));

    post("/commands/create-list", (request, response) -> {
      // Convert incoming JSON payload to request class
      CreateTodoListRequest req = fromJson(request.body(), CreateTodoListRequest.class);
      // Construct initial state of the domain object
      TodoList todoList = new TodoList(new TodoListState());
      // Execute domain logic
      List<Event> events = todoList.createNew(req.listId, req.name);
      // Store event in Serialized
      listClient.save(req.listId, events);

      return "";
    });

    post("/commands/create-todo", (request, response) -> {
      // Convert incoming JSON payload to request class
      CreateTodoRequest req = fromJson(request.body(), CreateTodoRequest.class);
      // Load current state, update aggregate and save
      listClient.update(req.listId, state -> {
        // Init domain object with current state
        TodoList todoList = new TodoList(state);
        // Execute domain logic
        return todoList.addTodo(req.todoId, req.todoText);
      });

      return "";
    });

    post("/commands/complete-todo", (request, response) -> {
      // Convert incoming JSON payload to request class
      CompleteTodoRequest req = fromJson(request.body(), CompleteTodoRequest.class);
      // Load current state, update aggregate and save
      listClient.update(req.listId, state -> {
        // Init domain object with current state
        TodoList todoList = new TodoList(state);
        // Execute domain logic
        return todoList.completeTodo(req.todoId);
      });

      return "";
    });

    get("/queries/lists", (request, response) -> {
      ListProjectionQuery query = new ListProjectionQuery.Builder("lists").build(Map.class);
      return projectionClient.list(query);
    }, new JsonConverter());

    get("/queries/lists/:listId", (request, response) -> {
      String listId = request.params(":listId");
      SingleProjectionQuery query = new SingleProjectionQuery.Builder("lists").id(listId).build(Map.class);
      return projectionClient.query(query);
    }, new JsonConverter());

  }

  private static SerializedClientConfig getConfig() {
    return SerializedClientConfig.serializedConfig()
        .accessKey(getConfig("SERIALIZED_ACCESS_KEY"))
        .secretAccessKey(getConfig("SERIALIZED_SECRET_ACCESS_KEY")).build();
  }

  private static String getConfig(String key) {
    return Optional.ofNullable(defaultString(System.getenv(key), System.getProperty(key)))
        .orElseThrow(() -> new IllegalStateException("Missing environment property: " + key));
  }

}