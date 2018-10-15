package io.serialized.samples.rockpaperscissors.command;

import io.serialized.samples.rockpaperscissors.domain.Answer;

public class ShowHandCommand extends Command {
  public String gameId;
  public String player;
  public Answer answer;
}
