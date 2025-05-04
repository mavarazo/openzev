package com.mav.openzev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("local")
public class LocalOpenZevApplication {

  public static void main(final String[] args) {
    SpringApplication.from(OpenZevApplication::main)
        .with(LocalTestcontainersConfiguration.class)
        .run(args);
  }
}
