package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.AggregateFactory;
import io.serialized.client.aggregate.Command;
import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.StateBuilder;
import io.serialized.samples.rockpaperscissors.domain.event.GameFinished;
import io.serialized.samples.rockpaperscissors.domain.event.GameStarted;
import io.serialized.samples.rockpaperscissors.domain.event.PlayerAnswered;
import io.serialized.samples.rockpaperscissors.domain.event.PlayerWonRound;
import io.serialized.samples.rockpaperscissors.domain.event.RoundFinished;
import io.serialized.samples.rockpaperscissors.domain.event.RoundStarted;
import io.serialized.samples.rockpaperscissors.domain.event.RoundTied;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static io.serialized.samples.rockpaperscissors.domain.Answer.PAPER;
import static io.serialized.samples.rockpaperscissors.domain.Answer.ROCK;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GameTest {

  private final StateBuilder<GameState> gameStateBuilder = StateBuilder.stateBuilder(GameState.class)
      .withHandler(PlayerWonRound.class, GameState::handlePlayerWonRound)
      .withHandler(RoundStarted.class, GameState::handleRoundStarted)
      .withHandler(RoundFinished.class, GameState::handleRoundFinished)
      .withHandler(RoundTied.class, GameState::handleRoundTied)
      .withHandler(PlayerAnswered.class, GameState::handlePlayerAnswered)
      .withHandler(GameStarted.class, GameState::handleGameStarted)
      .withHandler(GameFinished.class, GameState::handleGameFinished);

  private final AggregateFactory<Game, GameState> gameFactory = AggregateFactory.newFactory(Game::new, gameStateBuilder);

  @Test
  public void testStartGame() {

    Game game = gameFactory.fromCommands(Collections.emptyList());

    List<Event<?>> gameEvents = game.startGame(Player.fromString("Lisa"), Player.fromString("Bob"));
    assertThat(gameEvents.size(), is(1));

    Event<GameStarted> gameStarted = firstEventOfType(gameEvents, GameStarted.class);
    assertThat(gameStarted.getData().players.size(), is(2));
    assertThat(gameStarted.getData().players, hasItem("Lisa"));
    assertThat(gameStarted.getData().players, hasItem("Bob"));
  }

  @Test
  public void firstPlayerShowsHand() {

    Game game = gameFactory.fromCommands(
        // Start game
        startGame(Player.fromString("Lisa"), Player.fromString("Bob")));

    List<Event<?>> gameEvents = game.showHand(Player.fromString("Lisa"), ROCK);
    assertThat(gameEvents.size(), is(2));
    Event<RoundStarted> roundStarted = firstEventOfType(gameEvents, RoundStarted.class);
    assertThat(roundStarted.getData().players, hasItem("Lisa"));
    assertThat(roundStarted.getData().players, hasItem("Bob"));
    Event<PlayerAnswered> playerAnswered = firstEventOfType(gameEvents, PlayerAnswered.class);
    assertThat(playerAnswered.getData().player, is("Lisa"));
    assertThat(playerAnswered.getData().answer, is(ROCK));
  }

  @Test
  public void sameAnswerYieldsNoNewEvents() {

    Game game = gameFactory.fromCommands(
        // Start game
        startGame(Player.fromString("Lisa"), Player.fromString("Bob")),

        // Round 1
        showHand(Player.fromString("Lisa"), ROCK));

    List<Event<?>> gameEvents = game.showHand(Player.fromString("Lisa"), ROCK);

    assertThat(gameEvents.size(), is(0));
  }

  @Test
  public void secondPlayerShowsHandAndFinishesRound() {

    Game game = gameFactory.fromCommands(
        // Start game
        startGame(Player.fromString("Lisa"), Player.fromString("Bob")),

        // Round 1
        showHand(Player.fromString("Lisa"), ROCK));

    List<Event<?>> gameEvents = game.showHand(Player.fromString("Bob"), PAPER);
    assertThat(gameEvents.size(), is(3));
    Event<PlayerAnswered> playerAnswered = firstEventOfType(gameEvents, PlayerAnswered.class);
    assertThat(playerAnswered.getData().player, is("Bob"));
    assertThat(playerAnswered.getData().answer, is(PAPER));

    Event<RoundFinished> roundFinished = firstEventOfType(gameEvents, RoundFinished.class);
    assertThat(roundFinished.getData().winner, is("Bob"));
    assertThat(roundFinished.getData().loser, is("Lisa"));
  }

  @Test
  public void secondPlayerShowsHandAndTiesRound() {

    Game game = gameFactory.fromCommands(
        // Start game
        startGame(Player.fromString("Lisa"), Player.fromString("Bob")),

        // Round 1
        showHand(Player.fromString("Lisa"), ROCK));


    List<Event<?>> gameEvents = game.showHand(Player.fromString("Bob"), ROCK);

    assertThat(gameEvents.size(), is(2));
    Event<PlayerAnswered> playerAnswered = firstEventOfType(gameEvents, PlayerAnswered.class);
    assertThat(playerAnswered.getData().player, is("Bob"));
    assertThat(playerAnswered.getData().answer, is(ROCK));

    Event<RoundTied> roundTied = firstEventOfType(gameEvents, RoundTied.class);
    assertThat(roundTied.getData().players.size(), is(2));
  }

  @Test
  public void lastRoundIsFinished() {

    Game game = gameFactory.fromCommands(
        // Start game
        startGame(Player.fromString("Lisa"), Player.fromString("Bob")),

        // Round 1
        showHand(Player.fromString("Lisa"), ROCK),
        showHand(Player.fromString("Bob"), PAPER),

        // Round 2
        showHand(Player.fromString("Bob"), ROCK),
        showHand(Player.fromString("Lisa"), PAPER),

        // Round 3
        showHand(Player.fromString("Lisa"), ROCK));

    List<Event<?>> gameEvents = game.showHand(Player.fromString("Bob"), PAPER);

    assertThat(gameEvents.size(), is(4));
    Event<PlayerAnswered> playerAnswered = firstEventOfType(gameEvents, PlayerAnswered.class);
    assertThat(playerAnswered.getData().player, is("Bob"));
    assertThat(playerAnswered.getData().answer, is(PAPER));

    Event<RoundFinished> roundFinished = firstEventOfType(gameEvents, RoundFinished.class);
    assertThat(roundFinished.getData().winner, is("Bob"));
    assertThat(roundFinished.getData().loser, is("Lisa"));

    Event<GameFinished> gameFinished = firstEventOfType(gameEvents, GameFinished.class);
    assertThat(gameFinished.getData().winner, is("Bob"));
  }

  private Command<Game> showHand(Player player, Answer answer) {
    return g -> g.showHand(player, answer);
  }

  private Command<Game> startGame(Player player1, Player player2) {
    return g -> g.startGame(player1, player2);
  }

  @Test
  public void winsAfterTwoRounds() {
    Game game = gameFactory.fromCommands(g -> g.startGame(Player.fromString("Lisa"), Player.fromString("Bob")),
        // Start game
        showHand(Player.fromString("Lisa"), ROCK),

        // Round 1
        showHand(Player.fromString("Bob"), PAPER),

        // Round 2
        showHand(Player.fromString("Bob"), PAPER));

    List<Event<?>> gameEvents = game.showHand(Player.fromString("Lisa"), ROCK);
    assertThat(gameEvents.size(), is(4));

    Event<GameFinished> gameFinished = firstEventOfType(gameEvents, GameFinished.class);
    assertThat(gameFinished.getData().winner, is("Bob"));
  }

  private <T> Event<T> firstEventOfType(List<Event<?>> gameEvents, Class<T> clazz) {
    return (Event<T>) gameEvents.stream()
        .filter(e -> e.getEventType().equals(clazz.getSimpleName())).findFirst()
        .orElseThrow(() -> new RuntimeException("Missing event"));
  }

}
