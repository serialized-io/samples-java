package io.serialized.samples.rockpaperscissors;

import io.serialized.samples.rockpaperscissors.query.ProjectionInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class GameApplication implements CommandLineRunner {

  private final ProjectionInitializer configurer;

  @Autowired
  public GameApplication(ProjectionInitializer configurer) {
    this.configurer = configurer;
  }

  @Override
  public void run(String... strings) {
    configurer.createWinnersProjection();
    configurer.createGameProjection();
    configurer.totalStatsProjection();
  }

  public static void main(String[] args) {
    SpringApplication.run(GameApplication.class, args);
  }
}
