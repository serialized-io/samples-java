package io.serialized.samples.guessinggame.domain.event;

import io.serialized.client.aggregate.Event;

import java.util.UUID;

import static io.serialized.client.aggregate.Event.newEvent;

public class GameFinished {

  private UUID gameId;
  private int correctAnswer;
  private String result;
  private long finishedAt;

  public static Event<GameFinished> gameFinished(UUID gameId, int correctAnswer, String result, long finishedAt) {
    GameFinished event = new GameFinished();
    event.gameId = gameId;
    event.correctAnswer = correctAnswer;
    event.result = result;
    event.finishedAt = finishedAt;
    return newEvent(event).build();
  }

  public int getCorrectAnswer() {
    return correctAnswer;
  }

  public String getResult() {
    return result;
  }

}
