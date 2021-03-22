package io.serialized.samples;

import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.aggregate.Event;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.query.ListProjectionQuery;
import io.serialized.client.projection.query.ProjectionQueries;
import io.serialized.samples.api.CompleteTodoCommand;
import io.serialized.samples.api.CreateTodoCommand;
import io.serialized.samples.api.CreateTodoListCommand;
import io.serialized.samples.domain.TodoList;
import io.serialized.samples.domain.TodoListState;
import io.serialized.samples.domain.event.TodoAdded;
import io.serialized.samples.domain.event.TodoCompleted;
import io.serialized.samples.domain.event.TodoListCompleted;
import io.serialized.samples.domain.event.TodoListCreated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.QueryParamsMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.serialized.client.aggregate.AggregateClient.aggregateClient;
import static io.serialized.client.aggregate.AggregateRequest.saveRequest;
import static io.serialized.client.projection.Functions.merge;
import static io.serialized.client.projection.Functions.prepend;
import static io.serialized.client.projection.Functions.set;
import static io.serialized.client.projection.Functions.setref;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.RawData.rawData;
import static io.serialized.client.projection.TargetFilter.targetFilter;
import static io.serialized.client.projection.TargetSelector.targetSelector;
import static io.serialized.samples.JsonConverter.fromJson;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

public class TodoService {

  private static final String LIST_TYPE = "list";
  private static final String LISTS_PROJECTION = "lists";

  private static final Logger logger = LoggerFactory.getLogger(TodoService.class);
  private static final AggregateClient<TodoListState> listClient;
  private static final ProjectionClient projectionClient;

  static {
    SerializedClientConfig serializedClientConfig = getConfig();

    // Register mapping between event types and handler methods
    listClient = aggregateClient(LIST_TYPE, TodoListState.class, serializedClientConfig)
        .registerHandler(TodoListCreated.class, TodoListState::handleTodoListCreated)
        .registerHandler(TodoAdded.class, TodoListState::handleTodoAdded)
        .registerHandler(TodoCompleted.class, TodoListState::handleTodoCompleted)
        .registerHandler(TodoListCompleted.class, TodoListState::handleTodoListCompleted)
        .build();

    projectionClient = ProjectionClient.projectionClient(serializedClientConfig).build();

    // Make sure the projection is configured in Serialized
    projectionClient.createOrUpdate(
        singleProjection(LISTS_PROJECTION)
            .feed(LIST_TYPE)
            .addHandler("TodoListCreated",
                merge().build(),
                set()
                    .with(targetSelector("status"))
                    .with(rawData("EMPTY"))
                    .build(),
                setref()
                    .with(targetSelector("status"))
                    .build()
            )
            .addHandler("TodoAdded",
                prepend()
                    .with(targetSelector("todos"))
                    .build(),
                set()
                    .with(targetSelector("todos[?].status"))
                    .with(targetFilter("[?(@.todoId == $.event.todoId)]"))
                    .with(rawData("IN_PROGRESS")).build(),
                set()
                    .with(targetSelector("status"))
                    .with(rawData("IN_PROGRESS"))
                    .build(),
                setref()
                    .with(targetSelector("status"))
                    .build()
            )
            .addHandler("TodoCompleted",
                set()
                    .with(targetSelector("todos[?].status"))
                    .with(targetFilter("[?(@.todoId == $.event.todoId)]"))
                    .with(rawData("COMPLETED")).build(),
                setref()
                    .with(targetSelector("status"))
                    .build())
            .addHandler("TodoListCompleted",
                set()
                    .with(targetSelector("status"))
                    .with(rawData("COMPLETED"))
                    .build(),
                setref()
                    .with(targetSelector("status"))
                    .build()
            ).build());
  }

  public static void main(String[] args) {
    port(8080);

    before((request, response) -> response.type("application/json"));

    post("/commands/create-list", (request, response) -> {
      // Convert incoming JSON payload to request class
      CreateTodoListCommand command = fromJson(request.body(), CreateTodoListCommand.class);
      // Construct initial state of the domain object
      TodoList todoList = new TodoList(new TodoListState());
      // Execute domain logic
      List<Event<?>> events = todoList.createNew(command.listId, command.name);
      // Store event in Serialized
      logger.info("Creating list: {}", command.listId);
      listClient.save(saveRequest().withAggregateId(command.listId).withEvents(events).build());

      return "";
    });

    post("/commands/create-todo", (request, response) -> {
      // Convert incoming JSON payload to request class
      CreateTodoCommand command = fromJson(request.body(), CreateTodoCommand.class);
      // Load current state, update aggregate and save
      listClient.update(command.listId, state -> {
        // Init domain object with current state
        TodoList todoList = new TodoList(state);
        // Execute domain logic
        logger.info("Creating todo: {}", command.todoId);
        return todoList.addTodo(command.todoId, command.todoText);
      });

      return "";
    });

    post("/commands/complete-todo", (request, response) -> {
      // Convert incoming JSON payload to request class
      CompleteTodoCommand command = fromJson(request.body(), CompleteTodoCommand.class);
      // Load current state, update aggregate and save
      listClient.update(command.listId, state -> {
        // Init domain object with current state
        TodoList todoList = new TodoList(state);
        // Execute domain logic
        logger.info("Completing todo: {}", command.todoId);
        return todoList.completeTodo(command.todoId);
      });

      return "";
    });

    get("/queries/lists", (request, response) -> {
      ListProjectionQuery.Builder builder = ProjectionQueries.list("lists");
      QueryParamsMap queryParamsMap = request.queryMap("status");
      // Fetch projected lists from Serialized
      if (queryParamsMap.hasValue()) {
        String status = queryParamsMap.value();
        logger.info("Returning all lists with status: {}", status);
        return projectionClient.query(builder.withReference(status).build(Map.class));
      } else {
        logger.info("Returning all lists");
        return projectionClient.query(builder.build(Map.class));
      }
    }, new JsonConverter());

    get("/queries/lists/:listId", (request, response) -> {
      String listId = request.params(":listId");
      logger.info("Returning lists with ID: {}", listId);
      // Fetch the projected to do list from Serialized
      return projectionClient.query(ProjectionQueries.single("lists").withId(listId).build(Map.class));
    }, new JsonConverter());

    exception(IllegalArgumentException.class, (exception, request, response) -> {
      logger.warn("Error: " + exception.getMessage(), exception);
      response.status(400);
      response.type("application/json");
      response.body("{\"message\":\"" + exception.getMessage() + "\"}");
    });

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
