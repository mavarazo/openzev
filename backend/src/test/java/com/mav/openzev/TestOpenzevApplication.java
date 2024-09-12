package com.mav.openzev;

import org.springframework.boot.SpringApplication;

public class TestOpenzevApplication {

    public static void main(String[] args) {
        SpringApplication.from(OpenzevApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
