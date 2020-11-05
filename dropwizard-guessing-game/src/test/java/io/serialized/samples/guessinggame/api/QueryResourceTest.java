package io.serialized.samples.guessinggame.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import io.dropwizard.testing.junit5.DropwizardClientExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import io.serialized.client.SerializedClientConfig;
import io.serialized.client.projection.ProjectionClient;
import io.serialized.client.projection.ProjectionResponse;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.Response;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@ExtendWith(DropwizardExtensionsSupport.class)
public class QueryResourceTest {

  private static final ProjectionApiStub.ProjectionApiCallback projectionApiCallback = mock(ProjectionApiStub.ProjectionApiCallback.class);
  private static final DropwizardClientExtension dropwizard = new DropwizardClientExtension(new ProjectionApiStub(projectionApiCallback));

  private final ProjectionClient projectionClient = ProjectionClient.projectionClient(createConfig(dropwizard)).build();
  private final QueryResource queryResource = new QueryResource(projectionClient);

  private final ResourceExtension resources = ResourceExtension.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addProvider(queryResource)
      .build();

  @BeforeEach
  public void setUp() {
    reset(projectionApiCallback);
    resources.getObjectMapper().setSerializationInclusion(NON_NULL).setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    dropwizard.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
  }

  @Test
  public void shouldGetGame() {
    UUID gameId = UUID.randomUUID();

    GameProjection projection = new GameProjection();
    projection.guessCount = 0;

    ProjectionResponse<GameProjection> projectionResponse = new ProjectionResponse<>(
        gameId.toString(), System.currentTimeMillis(), projection);

    when(projectionApiCallback.singleProjectionLoaded("games", gameId)).thenReturn(projectionResponse);

    Response response = resources.target("/queries/games/" + gameId.toString()).request().get();

    assertThat(response.getStatus()).isEqualTo(200);
    GameProjection payload = response.readEntity(GameProjection.class);
    assertThat(payload.guessCount).isEqualTo(projection.guessCount);
  }

  public static SerializedClientConfig createConfig(DropwizardClientExtension dropwizard) {
    return new SerializedClientConfig.Builder()
        .rootApiUrl(dropwizard.baseUri() + "/api-stub")
        .accessKey("dummy")
        .secretAccessKey("dummy")
        .build();
  }

}
