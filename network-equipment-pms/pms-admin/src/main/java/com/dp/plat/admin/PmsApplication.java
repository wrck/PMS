package com.dp.plat.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the network equipment PMS.
 *
 * <p>Component scan covers all modules under {@code com.dp.plat}.</p>
 */
@SpringBootApplication(scanBasePackages = "com.dp.plat")
@MapperScan("com.dp.plat.**.mapper")
public class PmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
    }
}
