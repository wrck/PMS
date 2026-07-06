package com.dp.plat.admin.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 主数据库健康检查指示器。
 *
 * <p>检查项：
 * <ol>
 *   <li>通过 {@link Connection#isValid(int)} 验证连接有效性</li>
 *   <li>查询关键表 {@code pms_project}、{@code sys_user} 的行数以确认表结构可访问</li>
 * </ol>
 *
 * <p>Bean 名称 {@code pmsDatabaseHealthIndicator} 对应健康检查名称 {@code pmsDatabase}，
 * 不覆盖 Spring Boot 默认的 {@code db} 指标，二者并存互补。</p>
 */
@Component("pmsDatabaseHealthIndicator")
public class DatabaseHealthIndicator extends AbstractHealthIndicator {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthIndicator(DataSource dataSource) {
        super("数据库健康检查失败");
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        Map<String, Object> details = new LinkedHashMap<>();

        // 1. 检查连接有效性
        boolean connectionOk = false;
        try (Connection conn = dataSource.getConnection()) {
            boolean valid = conn.isValid(5);
            details.put("connection", valid ? "UP" : "DOWN");
            try {
                details.put("url", conn.getMetaData().getURL());
                details.put("driver", conn.getMetaData().getDriverName());
            } catch (Exception ignored) {
                // 元数据获取失败不致命，跳过
            }
            connectionOk = valid;
        } catch (Exception e) {
            details.put("connection", "ERROR");
            details.put("error", e.getMessage());
            builder.down().withDetails(details);
            return;
        }
        if (!connectionOk) {
            builder.down().withDetails(details);
            return;
        }

        // 2. 检查关键表可访问性（pms_project / sys_user）
        try {
            details.put("pms_project.rows", queryRowCount("pms_project"));
            details.put("sys_user.rows", queryRowCount("sys_user"));
            builder.up().withDetails(details);
        } catch (Exception e) {
            // 表查询失败说明表结构缺失或权限不足
            details.put("tableCheckError", e.getMessage());
            builder.down().withDetails(details);
        }
    }

    /**
     * 查询指定表的行数。
     *
     * @param table 表名（仅限内部常量调用，不存在 SQL 注入风险）
     * @return 表行数
     */
    private long queryRowCount(String table) {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
        return count == null ? 0 : count;
    }
}
