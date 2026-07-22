package com.dp.plat.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for the network equipment PMS.
 *
 * <p>组件扫描覆盖 {@code com.dp.plat}（PMS 业务模块）与 {@code cn.iocoder.yudao}
 * （yudao framework 系统管理/基础设施模块），使 yudao 的 user/role/menu/auth、
 * security、mybatis、redis 等能力在运行时自动装配。</p>
 */
@SpringBootApplication(scanBasePackages = {"com.dp.plat", "cn.iocoder.yudao"})
@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl",
        "cn.iocoder.yudao.module.**.dal.mysql"})
@EnableScheduling
@EnableRetry
public class PmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
    }
}
