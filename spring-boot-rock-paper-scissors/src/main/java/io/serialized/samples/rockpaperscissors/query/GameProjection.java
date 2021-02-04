package io.serialized.samples.rockpaperscissors.query;

import io.serialized.samples.rockpaperscissors.domain.ValueObject;

import java.util.List;
import java.util.Map;

public class GameProjection extends ValueObject {

  public List<String> players;
  public String status;
  public List<Map> rounds;

}
