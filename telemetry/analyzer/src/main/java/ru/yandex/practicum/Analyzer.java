package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Analyzer {
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        SpringApplication.run(Analyzer.class, args);
    }
}