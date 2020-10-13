package io.serialized.samples.rockpaperscissors.domain;

import io.serialized.client.aggregate.Event;
import io.serialized.samples.rockpaperscissors.domain.event.GameFinished;
import io.serialized.samples.rockpaperscissors.domain.event.GameStarted;
import io.serialized.samples.rockpaperscissors.domain.event.PlayerAnswered;
import io.serialized.samples.rockpaperscissors.domain.event.PlayerWonRound;
import io.serialized.samples.rockpaperscissors.domain.event.RoundFinished;
import io.serialized.samples.rockpaperscissors.domain.event.RoundStarted;
import io.serialized.samples.rockpaperscissors.domain.event.RoundTied;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * The transient state of a game, built up from different events
 */
public class GameState {

  private final Set<Player> registeredPlayers = new LinkedHashSet<>();
  private final Set<PlayerHand> shownHands = new HashSet<>();
  private final Map<Player, Long> wins = new HashMap<>();

  private GameStatus gameStatus = GameStatus.NEW;

  public static GameState newGame() {
    return new GameState();
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

  boolean isParticipant(Player player) {
    return registeredPlayers.contains(player);
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
    registeredPlayers.addAll(event.data().players.stream().map(Player::fromString).collect(toSet()));
    return this;
  }

  public GameState handlePlayerWonRound(Event<PlayerWonRound> event) {
    Player winner = Player.fromString(event.data().winner);
    long numberOfWins = wins.getOrDefault(winner, 0L);
    wins.put(winner, numberOfWins + 1);
    return this;
  }

  public GameState handleRoundStarted(Event<RoundStarted> event) {
    return this;
  }

  public GameState handlePlayerAnswered(Event<PlayerAnswered> event) {
    Player player = Player.fromString(event.data().player);
    shownHands.add(new PlayerHand(player, event.data().answer));
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
