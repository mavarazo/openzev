package com.mav.openzev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OpenZevApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OpenZevApplication.class, args);
    }

}
