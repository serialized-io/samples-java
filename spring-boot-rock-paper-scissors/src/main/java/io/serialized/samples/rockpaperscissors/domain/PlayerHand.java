package io.serialized.samples.rockpaperscissors.domain;

import static io.serialized.samples.rockpaperscissors.domain.Result.WIN;

/**
 * Represents the hand shown by a player in a round
 */
class PlayerHand extends ValueObject {

  final String name;
  final Answer answer;

  PlayerHand(String name, Answer answer) {
    this.name = name;
    this.answer = answer;
  }

  RoundResult calculateResult(PlayerHand opponent) {
    Result result = answer.calculateResult(opponent.answer);
    String winner = WIN.equals(result) ? name : opponent.name;
    String loser = WIN.equals(result) ? opponent.name : name;
    return new RoundResult(winner, loser);
  }

  PlayerHand clear() {
    return new PlayerHand(name, Answer.NONE);
  }
}
