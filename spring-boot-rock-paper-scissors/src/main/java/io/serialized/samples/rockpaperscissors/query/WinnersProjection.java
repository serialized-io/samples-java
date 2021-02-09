package io.serialized.samples.rockpaperscissors.query;

import io.serialized.client.projection.ProjectionResponse;
import io.serialized.client.projection.ProjectionsResponse;
import io.serialized.samples.rockpaperscissors.domain.ValueObject;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class WinnersProjection extends ValueObject {

  public List<HighScore> highScores;

  public static WinnersProjection fromProjections(ProjectionsResponse<HighScore> highScores) {
    WinnersProjection winnersProjection = new WinnersProjection();
    winnersProjection.highScores = highScores.projections().stream()
        .sorted(Comparator.comparingInt(o -> -o.data().wins))
        .map(ProjectionResponse::data)
        .collect(toList());
    return winnersProjection;
  }

}
