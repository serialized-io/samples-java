package io.serialized.samples.guessinggame.api;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class StartGameCommand {

  @NotNull
  public UUID gameId;

}
