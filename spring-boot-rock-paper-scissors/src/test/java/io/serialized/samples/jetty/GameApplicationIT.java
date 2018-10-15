package io.serialized.samples.jetty;

import io.serialized.samples.rockpaperscissors.command.ShowHandCommand;
import io.serialized.samples.rockpaperscissors.command.StartGameCommand;
import io.serialized.samples.rockpaperscissors.domain.Answer;
import io.serialized.samples.rockpaperscissors.query.HighScoreProjection;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

public class GameApplicationIT {

  public static final String LOCAL_SERVER = "http://localhost:8080";
  public static final String GAME_ID = UUID.randomUUID().toString();

  @Test
  public void bobWins() {

    startGame("Bob", "Lisa");

    // Round 1
    showHand("Lisa", Answer.ROCK);
    showHand("Bob", Answer.PAPER);

    // Round 2
    showHand("Lisa", Answer.PAPER);
    showHand("Bob", Answer.ROCK);

    // Round 3
    showHand("Lisa", Answer.PAPER);
    showHand("Bob", Answer.SCISSORS);

  }

  @Test
  public void lisaWins() {

    startGame("Bob", "Lisa");

    // Round 1
    showHand("Lisa", Answer.PAPER);
    showHand("Bob", Answer.ROCK);

    // Round 2
    showHand("Lisa", Answer.PAPER);
    showHand("Bob", Answer.SCISSORS);

    // Round 3
    showHand("Lisa", Answer.PAPER);
    showHand("Bob", Answer.ROCK);

  }

  @Test
  public void printHighScore() {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<?> headers = new HttpEntity<>(clientHeaders());
    ResponseEntity<HighScoreProjection> exchange = restTemplate.exchange(LOCAL_SERVER + "/high-score", HttpMethod.GET, headers, HighScoreProjection.class);
    System.out.println("HighScoreProjection = " + exchange.getBody().highScores);
  }

  private void startGame(String player1, String player2) {
    StartGameCommand startGameRequest = new StartGameCommand();
    startGameRequest.gameId = GAME_ID;
    startGameRequest.player1 = player1;
    startGameRequest.player2 = player2;

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<?> entity = new HttpEntity<>(startGameRequest, clientHeaders());
    restTemplate.postForObject(LOCAL_SERVER + "/start-game", entity, ResponseEntity.class);
  }

  private void showHand(String player, Answer answer) {
    RestTemplate restTemplate = new RestTemplate();
    ShowHandCommand showHandRequest = new ShowHandCommand();
    showHandRequest.gameId = GAME_ID;
    showHandRequest.player = player;
    showHandRequest.answer = answer;
    HttpEntity<?> entity2 = new HttpEntity<>(showHandRequest, clientHeaders());
    restTemplate.postForObject(LOCAL_SERVER + "/show-hand", entity2, ResponseEntity.class);
  }

  private HttpHeaders clientHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }


}
