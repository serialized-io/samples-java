package io.serialized.samples.guessinggame;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.aggregate.AggregateClient;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.reaction.ReactionClient;
import io.serialized.samples.guessinggame.api.ApiExceptionMapper;
import io.serialized.samples.guessinggame.api.CommandResource;
import io.serialized.samples.guessinggame.api.IllegalStateExceptionMapper;
import io.serialized.samples.guessinggame.api.QueryResource;
import io.serialized.samples.guessinggame.domain.GameState;
import io.serialized.samples.guessinggame.domain.event.GameFinished;
import io.serialized.samples.guessinggame.domain.event.GameStarted;
import io.serialized.samples.guessinggame.domain.event.HintAdded;
import io.serialized.samples.guessinggame.domain.event.PlayerGuessed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static io.serialized.client.projection.EventSelector.eventSelector;
import static io.serialized.client.projection.Functions.append;
import static io.serialized.client.projection.Functions.inc;
import static io.serialized.client.projection.Functions.set;
import static io.serialized.client.projection.Functions.unset;
import static io.serialized.client.projection.ProjectionDefinition.singleProjection;
import static io.serialized.client.projection.RawData.rawData;
import static io.serialized.client.projection.TargetSelector.targetSelector;

public class DemoApp extends Application<DemoAppConfig> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DemoApp.class);

  @Override
  public void initialize(Bootstrap<DemoAppConfig> bootstrap) {
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
        new EnvironmentVariableSubstitutor(false))
    );
  }

  @Override
  public void run(DemoAppConfig config, Environment environment) {

    environment.getObjectMapper() // Customize JSON handling
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false) // Be tolerant
        .configure(INDENT_OUTPUT, true) // Look nice
        .setSerializationInclusion(NON_NULL); // Ignore fields with null value

    SerializedClientConfig serializedClientConfig = config.serializedClientConfig();
    AggregateClient<GameState> gameAggregateClient = config.gameAggregateClient(serializedClientConfig);
    ProjectionClient projectionClient = config.projectionClient(serializedClientConfig);
    ReactionClient reactionClient = config.reactionClient(serializedClientConfig); // Not used yet...

    environment.jersey().register(new ApiExceptionMapper()); // For showing error codes from Serialized
    environment.jersey().register(new IllegalStateExceptionMapper()); // For showing domain logic errors

    // Register endpoints
    environment.jersey().register(new CommandResource(gameAggregateClient));
    environment.jersey().register(new QueryResource(projectionClient));

    environment.lifecycle().addServerLifecycleListener(server -> {

      // Create/update your Serialized Projections & Reactions here!
      projectionClient.createOrUpdate(singleProjection("games") // Name projection
          .feed("game") // Event feed to project
          .addHandler(GameStarted.class.getSimpleName(),
              set()
                  .with(targetSelector("guessCount"))
                  .with(rawData(0))
                  .build(),
              set()
                  .with(targetSelector("hint"))
                  .with(rawData("Start guessing!"))
                  .build())
          .addHandler(PlayerGuessed.class.getSimpleName(),
              inc()
                  .with(targetSelector("guessCount"))
                  .build())
          .addHandler(HintAdded.class.getSimpleName(),
              set()
                  .with(eventSelector("hint"))
                  .with(targetSelector("hint"))
                  .build())
          .addHandler(GameFinished.class.getSimpleName(),
              set()
                  .with(eventSelector("correctAnswer"))
                  .with(targetSelector("correctAnswer"))
                  .build(),
              unset()
                  .with(targetSelector("hint"))
                  .build(),
              set()
                  .with(eventSelector("result"))
                  .with(targetSelector("result"))
                  .build())
          .build());

      projectionClient.createOrUpdate(singleProjection("game-history") // Name projection
          .feed("game") // Event feed to project
          .addHandler(PlayerGuessed.class.getSimpleName(),
              append()
                  .with(targetSelector("rounds"))
                  .build())
          .build());

      LOGGER.info("Setup complete!");
    });
  }

  public static void main(String[] args) throws Exception {
    try {
      LOGGER.info("Starting application...");
      new DemoApp().run(args);
    } catch (Exception e) {
      LOGGER.error("Unable to start application", e);
      throw e;
    }
  }

}
