package com.dp.plat.mock.oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Standalone Spring Boot application emulating Seeyon OA for local
 * integration testing.
 */
@SpringBootApplication
public class MockOaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockOaApplication.class, args);
    }
}
