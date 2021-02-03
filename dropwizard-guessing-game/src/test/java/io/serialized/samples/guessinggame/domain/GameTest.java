package io.serialized.samples.guessinggame.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.StateBuilder;
import io.serialized.samples.guessinggame.domain.event.GameFinished;
import io.serialized.samples.guessinggame.domain.event.GameStarted;
import io.serialized.samples.guessinggame.domain.event.HintAdded;
import io.serialized.samples.guessinggame.domain.event.PlayerGuessed;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static io.serialized.samples.guessinggame.domain.event.GameStarted.gameStarted;
import static io.serialized.samples.guessinggame.domain.event.PlayerGuessed.playerGuessed;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {

  private final StateBuilder<GameState> gameStateBuilder = StateBuilder.stateBuilder(GameState.class)
      .withHandler(GameStarted.class, GameState::handleGameStarted)
      .withHandler(PlayerGuessed.class, GameState::handlePlayerGuessed)
      .withHandler(HintAdded.class, GameState::handleHintAdded)
      .withHandler(GameFinished.class, GameState::handleGameFinished);

  @Test
  public void shouldStartGame() {
    UUID id = UUID.randomUUID();

    Game game = new Game(new GameState());
    Event<GameStarted> gameStarted = (Event<GameStarted>) game.start(id);
    assertThat(gameStarted.data().getNumber()).isBetween(1, 100);
  }

  @Test
  public void cannotStartIfAlreadyStarted() {
    UUID id = UUID.randomUUID();

    Game game = new Game(gameStateBuilder.buildState(singletonList(
        gameStarted(id, 10, System.currentTimeMillis())
    )));

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      game.start(id);
    });

    assertThat(exception.getMessage()).isEqualTo("Game is already started!");
  }

  @Test
  public void shouldWinGame() {
    UUID id = UUID.randomUUID();

    Game game = new Game(gameStateBuilder.buildState(singletonList(
        gameStarted(id, 10, System.currentTimeMillis())
    )));

    List<Event<?>> events = game.guess(id, 10);

    Iterator<Event<?>> iterator = events.iterator();
    Event<PlayerGuessed> playerGuessed = (Event<PlayerGuessed>) iterator.next();
    Event<GameFinished> gameFinished = (Event<GameFinished>) iterator.next();
    assertThat(playerGuessed.data().getGuess()).isEqualTo(gameFinished.data().getCorrectAnswer());
    assertThat(gameFinished.data().getResult()).isEqualTo("Player won");
  }

  @Test
  public void shouldHintLower() {
    UUID id = UUID.randomUUID();

    Game game = new Game(gameStateBuilder.buildState(singletonList(
        gameStarted(id, 10, System.currentTimeMillis())
    )));

    List<Event<?>> events = game.guess(id, 70);

    Iterator<Event<?>> iterator = events.iterator();
    Event<PlayerGuessed> playerGuessed = (Event<PlayerGuessed>) iterator.next();
    Event<HintAdded> hintAdded = (Event<HintAdded>) iterator.next();
    assertThat(playerGuessed.data().getGuess()).isPositive();
    assertThat(hintAdded.data().getHint()).isEqualTo("Lower!");
  }

  @Test
  public void shouldHintHigher() {
    UUID id = UUID.randomUUID();

    Game game = new Game(gameStateBuilder.buildState(singletonList(
        gameStarted(id, 50, System.currentTimeMillis())
    )));

    List<Event<?>> events = game.guess(id, 20);

    Iterator<Event<?>> iterator = events.iterator();
    Event<PlayerGuessed> playerGuessed = (Event<PlayerGuessed>) iterator.next();
    Event<HintAdded> hintAdded = (Event<HintAdded>) iterator.next();
    assertThat(playerGuessed.data().getGuess()).isPositive();
    assertThat(hintAdded.data().getHint()).isEqualTo("Higher!");
  }

  @Test
  public void shouldLoseGame() {
    UUID id = UUID.randomUUID();

    Game game = new Game(gameStateBuilder.buildState(asList(
        gameStarted(id, 10, System.currentTimeMillis()),
        playerGuessed(id, 50, System.currentTimeMillis()),
        playerGuessed(id, 25, System.currentTimeMillis()),
        playerGuessed(id, 20, System.currentTimeMillis()),
        playerGuessed(id, 5, System.currentTimeMillis()),
        playerGuessed(id, 18, System.currentTimeMillis()),
        playerGuessed(id, 7, System.currentTimeMillis()),
        playerGuessed(id, 15, System.currentTimeMillis()),
        playerGuessed(id, 14, System.currentTimeMillis()),
        playerGuessed(id, 12, System.currentTimeMillis())
    )));

    List<Event<?>> events = game.guess(id, 9);

    Iterator<Event<?>> iterator = events.iterator();
    Event<PlayerGuessed> playerGuessed = (Event<PlayerGuessed>) iterator.next();
    Event<GameFinished> gameFinished = (Event<GameFinished>) iterator.next();
    assertThat(playerGuessed.data().getGuess()).isNotEqualTo(gameFinished.data().getCorrectAnswer());
    assertThat(gameFinished.data().getResult()).isEqualTo("Player lost");
  }

  @Test
  public void cannotGuessIfNotStarted() {
    UUID id = UUID.randomUUID();

    Game game = new Game(new GameState());

    Throwable exception = assertThrows(IllegalStateException.class, () -> {
      game.guess(id, 10);
    });

    assertThat(exception.getMessage()).isEqualTo("Game is not started!");
  }

}
