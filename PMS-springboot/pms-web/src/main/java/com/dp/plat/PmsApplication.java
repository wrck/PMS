package com.dp.plat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dp.plat.mapper")
public class PmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
        System.out.println("============================================");
        System.out.println("  PMS Spring Boot 启动成功！ ");
        System.out.println("  访问地址: http://localhost:8080/pms");
        System.out.println("============================================");
    }
}
