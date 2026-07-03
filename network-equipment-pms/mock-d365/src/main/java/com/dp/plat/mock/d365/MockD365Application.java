package com.dp.plat.mock.d365;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standalone Spring Boot application emulating a Microsoft Dynamics 365
 * endpoint for local integration testing.
 */
@SpringBootApplication
public class MockD365Application {

    public static void main(String[] args) {
        SpringApplication.run(MockD365Application.class, args);
    }
}
