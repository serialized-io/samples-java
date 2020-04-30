package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionsResponse;
import io.serialized.samples.rockpaperscissors.domain.ValueObject;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class HighScoreProjection extends ValueObject {

  public List<HighScore> highScores;

  public static HighScoreProjection fromProjections(ProjectionsResponse<HighScore> highScores) {
    HighScoreProjection highScoreProjection = new HighScoreProjection();
    highScoreProjection.highScores = highScores.projections().stream().map(p -> p.data()).collect(toList());
    return highScoreProjection;
  }

}
