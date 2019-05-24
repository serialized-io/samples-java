package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.client.aggregate.State;
import io.serialized.samples.rockpaperscissors.domain.event.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * The transient state of a game, built up from different events
 */
public class GameState {
  private GameStatus gameStatus = GameStatus.NEW;

  private Set<Player> registeredPlayers = new LinkedHashSet<>();
  private Set<PlayerHand> shownHands = new HashSet<>();
  private Map<Player, Long> wins = new HashMap<>();

  public static State<GameState> newGame() {
    return new State<>(0, new GameState());
  }

  public void assertAllowsMoreAnswers() {
    gameStatus.assertAllowsMoreAnswers();
  }

  boolean allPlayersAnswered(Set<PlayerHand> allHands) {
    return allHands.size() == registeredPlayers.size();
  }

  boolean roundHasNotStarted() {
    return shownHands.isEmpty();
  }

  Set<Player> registeredPlayers() {
    return Collections.unmodifiableSet(registeredPlayers);
  }

  Set<PlayerHand> shownHands() {
    return Collections.unmodifiableSet(shownHands);
  }

  boolean playerWonGame(Player roundWinner) {
    return wins.getOrDefault(roundWinner, 0L) >= 1;
  }

  boolean isRoundTied(Set<PlayerHand> allHands) {
    return allAnswersAreSame(allHands);
  }

  private boolean allAnswersAreSame(Set<PlayerHand> allHands) {
    return allHands.stream().map(playerHand -> playerHand.answer).distinct().limit(2).count() <= 1;
  }

  boolean hasPlayerAnswered(Player player) {
    return shownHands.stream().anyMatch(playerHand -> playerHand.player.equals(player));
  }

  boolean handAlreadyShown(PlayerHand playerHand) {
    return shownHands.contains(playerHand);
  }

  public GameState handleGameStarted(Event<GameStarted> event) {
    gameStatus = GameStatus.STARTED;
    registeredPlayers.addAll(event.getData().players.stream().map(Player::fromString).collect(toSet()));
    return this;
  }

  public GameState handlePlayerWonRound(Event<PlayerWonRound> event) {
    Player winner = Player.fromString(event.getData().winner);
    long numberOfWins = wins.getOrDefault(winner, 0L);
    wins.put(winner, numberOfWins + 1);
    return this;
  }

  public GameState handleRoundStarted(Event<RoundStarted> event) {
    return this;
  }

  public GameState handlePlayerAnswered(Event<PlayerAnswered> event) {
    Player player = Player.fromString(event.getData().player);
    shownHands.add(new PlayerHand(player, event.getData().answer));
    return this;
  }

  public GameState handleRoundTied(Event<RoundTied> event) {
    shownHands.clear();
    return this;
  }

  public GameState handleRoundFinished(Event<RoundFinished> event) {
    shownHands.clear();
    return this;
  }

  public GameState handleGameFinished(Event<GameFinished> event) {
    this.gameStatus = GameStatus.FINISHED;
    return this;
  }
}
