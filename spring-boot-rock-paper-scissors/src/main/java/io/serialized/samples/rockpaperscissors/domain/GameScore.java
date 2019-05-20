package io.serialized.samples.rockpaperscissors.domain;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * The score for the Game given a number of game rounds.
 */
public class GameScore extends ValueObject {

  private final List<Result> rounds;

  private GameScore(List<Result> rounds) {
    this.rounds = rounds;
  }

  static GameScore addRounds(List<Round> finishedRounds) {
    return new GameScore(finishedRounds.stream().map(Round::result).collect(Collectors.toList()));
  }

  /**
   * Calculates a new {@link GameScore} by adding a Round to the current {@link GameScore}
   *
   * @param round the round to be added
   * @return the new score
   */
  GameScore addRound(Round round) {
    ArrayList<Result> results = new ArrayList<>(rounds);
    results.add(round.result());
    return new GameScore(results);
  }

  boolean hasWinner() {
    return gameScore().entrySet().stream().anyMatch(e -> e.getValue() >= 2);
  }

  private Map<Player, Long> gameScore() {
    Map<Player, Long> results = new HashMap<>();
    results.put(rounds.get(0).loser().get(), 0L);
    results.put(rounds.get(0).winner().get(), 0L);
    Map<Player, Long> wins = rounds.stream().collect(groupingBy(r -> r.winner().get(), counting()));
    results.putAll(wins);
    return results;
  }

  /**
   * @return the winner of the Game, if one exists.
   * @throws IllegalStateException if the Game does not have a winner.
   */
  public Optional<Player> winner() {
    Map<Player, Long> wins = rounds.stream().collect(groupingBy(r -> r.winner().get(), counting()));

    LinkedHashMap<Player, Long> sortedMap = new LinkedHashMap<>();
    wins.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(reverseOrder()))
        .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

    return sortedMap.entrySet().stream().findFirst().map(Map.Entry::getKey);
  }
}
