package com.saveapenny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SaveAPennyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SaveAPennyApplication.class, args);
    }
}
