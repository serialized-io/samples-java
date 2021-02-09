package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionResponse;
import io.serialized.client.projection.ProjectionsResponse;
import io.serialized.samples.rockpaperscissors.domain.ValueObject;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class HighScore extends ValueObject {

  public List<Winner> winners;

  public static HighScore fromProjections(ProjectionsResponse<Winner> highScores) {
    HighScore highScore = new HighScore();
    highScore.winners = highScores.projections().stream()
        .sorted(Comparator.comparingInt(o -> -o.data().wins))
        .map(ProjectionResponse::data)
        .collect(toList());
    return highScore;
  }

}
