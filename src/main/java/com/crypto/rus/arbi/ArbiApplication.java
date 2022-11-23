package com.crypto.rus.arbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArbiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArbiApplication.class, args);
    }

}
