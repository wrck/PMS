package com.dp.plat.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application entry point for the network equipment PMS.
 *
 * <p>组件扫描覆盖 {@code com.dp.plat}（PMS 业务模块）与 {@code cn.iocoder.yudao}
 * （yudao framework 系统管理/基础设施模块），使 yudao 的 user/role/menu/auth、
 * security、mybatis、redis 等能力在运行时自动装配。</p>
 *
 * <p>排除 yudao 模块的 Controller 包（{@code cn.iocoder.yudao.module.**.controller.**}），
 * 避免 yudao Controller 与 PMS 业务 Controller 同名冲突（如 {@code FileController}、
 * {@code LoginLogServiceImpl} 等）。yudao 的 Service/DAO/Config 等底层组件仍被正常扫描，
 * 为 PMS 业务层提供基础能力。如需暴露 yudao 原生 admin API，可按需单独引入。</p>
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {"com.dp.plat", "cn.iocoder.yudao"},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "cn\\.iocoder\\.yudao\\.module\\..*\\.controller\\..*"
                )
        }
)
@MapperScan({"com.dp.plat.**.mapper", "com.dp.plat.**.dao", "com.dp.plat.**.engine.ddl",
        "cn.iocoder.yudao.module.**.dal.mysql"})
@EnableScheduling
@EnableRetry
public class PmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PmsApplication.class, args);
    }
}
