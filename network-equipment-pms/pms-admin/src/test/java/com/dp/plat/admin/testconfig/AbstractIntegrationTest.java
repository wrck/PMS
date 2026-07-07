package com.dp.plat.admin.testconfig;

import com.dp.plat.admin.PmsApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

/**
 * 集成测试基类（Task 12）：通过 Testcontainers 启动真实的 MySQL 8 + Redis 容器，
 * 由 Flyway 自动执行数据库迁移，从而以最贴近生产的方式验证 Controller 全链路。
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>{@code @SpringBootTest(RANDOM_PORT)} 加载完整应用上下文，聚合所有业务模块。</li>
 *   <li>{@code @AutoConfigureMockMvc(addFilters = false)} 关闭 Servlet 过滤器链（JWT、
 *       限流、XSS、幂等过滤器），避免对数据库中真实用户/权限数据的强依赖；方法级安全
 *       （{@code @PreAuthorize}）仍由 Spring Security AOP 强制执行，配合子类的
 *       {@code @WithMockUser} 注入权限。</li>
 *   <li>{@code @DynamicPropertySource} 将 Testcontainers 容器的动态端口/账号注入到
 *       Spring 配置，覆盖 {@code application.yml} 中的本地默认值。</li>
 *   <li>{@code @EnabledIfSystemProperty(docker.available=true)} 条件启用：沙箱/CI 环境
 *       无 Docker 时自动跳过，保证构建可编译通过。</li>
 * </ul>
 *
 * <p>子类应继承本类，并按需通过 {@code @WithMockUser(authorities = {...})} 注入权限，
 * 通过 {@link #mockMvc} 发起 HTTP 请求，通过 {@link #objectMapper} 序列化请求体。</p>
 */
@SpringBootTest(classes = PmsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
@ActiveProfiles("test")
@EnabledIfSystemProperty(named = "docker.available", matches = "true")
public abstract class AbstractIntegrationTest {

    /** MySQL 8 容器：Flyway 在应用启动时自动执行 classpath:db/migration 下的迁移脚本。 */
    @Container
    protected static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("network_equipment_pms_test")
            .withUsername("test")
            .withPassword("test")
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci");

    /** Redis 容器：供幂等切面、限流、Token 黑名单、缓存等组件使用。 */
    @Container
    protected static final RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:7-alpine"));

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        // 覆盖 application.yml 中的本地 MySQL 数据源配置
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        // 覆盖 Redis 连接配置
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.data.redis.password", () -> "");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
