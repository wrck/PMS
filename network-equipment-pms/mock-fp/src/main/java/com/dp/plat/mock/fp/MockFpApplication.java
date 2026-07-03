package com.dp.plat.mock.fp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standalone Spring Boot application emulating the Financial Platform (FP)
 * for local integration testing.
 */
@SpringBootApplication
public class MockFpApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockFpApplication.class, args);
    }
}
