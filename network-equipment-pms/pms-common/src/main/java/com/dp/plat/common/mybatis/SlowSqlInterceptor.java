package com.dp.plat.common.mybatis;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 慢 SQL 拦截器：监控 SQL 执行耗时，超过阈值时记录日志与指标。
 *
 * <p>拦截 {@link StatementHandler#prepare(Connection, Integer)} 方法，在 SQL 执行前后
 * 计算耗时，并按以下阈值分级处理：</p>
 * <ul>
 *   <li>耗时 &gt; 1s：WARN 级别日志 + {@code pms_slow_sql_total{threshold="warn"}} 计数 +1</li>
 *   <li>耗时 &gt; 5s：ERROR 级别日志 + {@code pms_slow_sql_total{threshold="error"}} 计数 +1</li>
 *   <li>正常 SQL：DEBUG 级别日志（可选，便于开发期排查）</li>
 * </ul>
 *
 * <p>同时通过 {@link Timer}（{@code pms_sql_duration_seconds}，按 {@code method} 标签：
 * select/insert/update/delete）记录所有 SQL 执行耗时分布，供 Prometheus 采集与
 * Grafana 面板（Task 8）展示。</p>
 *
 * <p><b>注册方式</b>：本拦截器为 MyBatis 原生 {@link Interceptor}（非 MyBatis-Plus 的
 * {@code InnerInterceptor}），不能加入 {@code MybatisPlusInterceptor}。通过 {@code @Component}
 * 由 MyBatis Spring 自动发现并注册为独立插件，与 {@code MybatisPlusInterceptor} 平行生效。</p>
 *
 * <p><b>异常安全</b>：拦截器内任何异常（SQL 上下文提取、指标记录）都不会影响业务 SQL 执行，
 * 仅记录 WARN 日志，确保监控逻辑不拖累主流程。</p>
 *
 * @see com.dp.plat.common.config.MyBatisPlusConfig
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
@Component
public class SlowSqlInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SlowSqlInterceptor.class);

    /** WARN 阈值：1 秒（1000 毫秒）。 */
    private static final long WARN_THRESHOLD_MS = 1000L;

    /** ERROR 阈值：5 秒（5000 毫秒）。 */
    private static final long ERROR_THRESHOLD_MS = 5000L;

    /** SQL 摘要最大长度（超过则截断并追加 "..."）。 */
    private static final int MAX_SQL_LENGTH = 200;

    /** SQL 执行耗时指标名（按 method 标签区分 select/insert/update/delete）。 */
    private static final String TIMER_NAME = "pms_sql_duration_seconds";

    /** 慢 SQL 计数指标名（按 threshold 标签区分 warn/error）。 */
    private static final String COUNTER_NAME = "pms_slow_sql_total";

    /** Micrometer 指标注册中心。 */
    private final MeterRegistry meterRegistry;

    /** 慢 SQL 警告计数器（threshold=warn，耗时 &gt; 1s）。 */
    private final Counter slowSqlWarnCounter;

    /** 慢 SQL 错误计数器（threshold=error，耗时 &gt; 5s）。 */
    private final Counter slowSqlErrorCounter;

    /**
     * 构造器注入 {@link MeterRegistry}，并预注册慢 SQL 计数器。
     *
     * <p>预注册 warn/error 计数器可确保指标在 Prometheus 抓取时始终存在
     * （即使未触发慢 SQL），避免仪表盘出现「无数据」状态。</p>
     *
     * @param meterRegistry Micrometer 指标注册中心
     */
    public SlowSqlInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.slowSqlWarnCounter = Counter.builder(COUNTER_NAME)
                .tag("threshold", "warn")
                .description("慢 SQL 警告计数（耗时 > 1s）")
                .register(meterRegistry);
        this.slowSqlErrorCounter = Counter.builder(COUNTER_NAME)
                .tag("threshold", "error")
                .description("慢 SQL 错误计数（耗时 > 5s）")
                .register(meterRegistry);
    }

    /**
     * 拦截 SQL 执行：记录开始时间，执行后计算耗时并评估告警等级。
     *
     * @param invocation MyBatis 拦截调用上下文
     * @return 原始方法返回值
     * @throws Throwable 业务 SQL 执行抛出的异常（透传，不吞没）
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 1. 提前提取 SQL 摘要与类型，避免 proceed() 抛异常后无法记录上下文
        String shortSql = "";
        String sqlMethod = "select";
        try {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            BoundSql boundSql = handler.getBoundSql();
            String sql = boundSql.getSql();
            shortSql = simplifySql(sql);
            sqlMethod = detectSqlMethod(sql);
        } catch (Throwable ex) {
            // 提取 SQL 失败不影响业务执行，仅记录日志
            log.warn("[慢SQL] 提取 SQL 上下文失败：{}", ex.getMessage());
        }

        // 2. 执行 SQL 并计时
        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            try {
                evaluateDuration(duration, sqlMethod, shortSql);
            } catch (Throwable ex) {
                // 指标记录失败不影响业务，仅记录日志
                log.warn("[慢SQL] 指标记录失败：{}", ex.getMessage());
            }
        }
    }

    /**
     * 评估 SQL 执行耗时并记录指标与日志。包级可见以便单元测试直接验证阈值分级逻辑，
     * 避免测试中通过 {@code Thread.sleep} 等待真实耗时。
     *
     * @param duration  执行耗时（毫秒）
     * @param sqlMethod SQL 类型（select/insert/update/delete）
     * @param shortSql  SQL 摘要（已简化截断）
     */
    void evaluateDuration(long duration, String sqlMethod, String shortSql) {
        // Timer 按 method 标签记录耗时分布（同 name+tags 的 Timer 由 MeterRegistry 复用）
        Timer.builder(TIMER_NAME)
                .description("SQL 执行耗时")
                .tag("method", sqlMethod)
                .register(meterRegistry)
                .record(duration, TimeUnit.MILLISECONDS);

        // 慢 SQL 分级告警
        if (duration > ERROR_THRESHOLD_MS) {
            log.error("[慢SQL-ERROR] 耗时={}ms 方法={} SQL={}", duration, sqlMethod, shortSql);
            slowSqlErrorCounter.increment();
        } else if (duration > WARN_THRESHOLD_MS) {
            log.warn("[慢SQL-WARN] 耗时={}ms 方法={} SQL={}", duration, sqlMethod, shortSql);
            slowSqlWarnCounter.increment();
        } else if (log.isDebugEnabled()) {
            log.debug("[SQL] 耗时={}ms 方法={} SQL={}", duration, sqlMethod, shortSql);
        }
    }

    /**
     * 简化 SQL：合并多余空白（空格/换行/制表符），并截断超长 SQL。
     * 包级可见以便单元测试验证截断逻辑。
     *
     * @param sql 原始 SQL
     * @return 简化后的 SQL 摘要（不超过 {@link #MAX_SQL_LENGTH} + 3 字符）
     */
    String simplifySql(String sql) {
        if (sql == null || sql.isEmpty()) {
            return "";
        }
        String simplified = sql.replaceAll("\\s+", " ").trim();
        if (simplified.length() > MAX_SQL_LENGTH) {
            return simplified.substring(0, MAX_SQL_LENGTH) + "...";
        }
        return simplified;
    }

    /**
     * 根据 SQL 前缀识别方法类型（select/insert/update/delete）。
     * 无法识别时默认 select（最常见的慢 SQL 来源）。
     *
     * @param sql 原始 SQL
     * @return 方法类型
     */
    private String detectSqlMethod(String sql) {
        if (sql == null || sql.isEmpty()) {
            return "select";
        }
        String trimmed = sql.trim().toLowerCase();
        if (trimmed.startsWith("insert")) {
            return "insert";
        } else if (trimmed.startsWith("update")) {
            return "update";
        } else if (trimmed.startsWith("delete")) {
            return "delete";
        }
        // select 或其它（DDL/存储过程等）默认归为 select
        return "select";
    }

    /**
     * 用当前拦截器包装目标对象，仅对 {@link StatementHandler} 生效。
     *
     * @param target 被拦截的目标对象
     * @return 包装后的代理对象（或原对象，若类型不匹配）
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 设置插件属性（MyBatis 配置注入），本拦截器无需额外配置。
     *
     * @param properties 属性集合
     */
    @Override
    public void setProperties(Properties properties) {
        // 无需额外配置
    }
}
