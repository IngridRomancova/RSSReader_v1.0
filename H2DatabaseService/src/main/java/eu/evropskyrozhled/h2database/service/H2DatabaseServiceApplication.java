package eu.evropskyrozhled.h2database.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Runner for H2DatabaseServiceApplication.
 */
@ComponentScan
@SpringBootApplication
public class H2DatabaseServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(H2DatabaseServiceApplication.class, args);
  }

}
