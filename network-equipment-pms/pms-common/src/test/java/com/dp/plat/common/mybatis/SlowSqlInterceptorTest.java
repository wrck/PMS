package com.dp.plat.common.mybatis;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Invocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link SlowSqlInterceptor} 单元测试：验证慢 SQL 阈值分级、Micrometer 指标记录、
 * SQL 摘要截断与端到端拦截流程。
 *
 * <p>使用 {@link SimpleMeterRegistry}（内存版指标注册中心）替代 PrometheusMeterRegistry，
 * 避免测试依赖 Prometheus 客户端。通过直接调用包级可见的 {@link SlowSqlInterceptor#evaluateDuration}
 * 验证阈值分级逻辑，无需 {@code Thread.sleep} 等待真实耗时，保持测试快速稳定。</p>
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>正常 SQL（&lt; 1s）：不记录慢 SQL 计数，Timer 记录 1 次</li>
 *   <li>WARN 区间（1-5s）：warn 计数 +1，error 计数不变</li>
 *   <li>ERROR 区间（&gt; 5s）：error 计数 +1，warn 计数不变</li>
 *   <li>SQL 摘要超长被截断为 200 字符 + "..."</li>
 *   <li>SQL 摘要空白合并</li>
 *   <li>method 标签按 SQL 前缀正确分类</li>
 *   <li>端到端 intercept() 流程：Mock StatementHandler + 短耗时，验证集成正常</li>
 * </ul>
 */
class SlowSqlInterceptorTest {

    /** 内存版指标注册中心，避免依赖 Prometheus。 */
    private SimpleMeterRegistry meterRegistry;

    /** 被测拦截器。 */
    private SlowSqlInterceptor interceptor;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        interceptor = new SlowSqlInterceptor(meterRegistry);
    }

    // ==================== 阈值分级测试 ====================

    @Test
    @DisplayName("正常 SQL（<1s）不记录慢 SQL 计数，但 Timer 记录耗时")
    void shouldNotRecordSlowSqlWhenDurationBelowWarnThreshold() {
        // When: 耗时 100ms（远低于 1s WARN 阈值）
        interceptor.evaluateDuration(100L, "select", "SELECT * FROM pms_project");

        // Then: 慢 SQL 计数器均为 0
        assertEquals(0.0, warnCount(), "WARN 计数应为 0");
        assertEquals(0.0, errorCount(), "ERROR 计数应为 0");

        // And: Timer 记录了 1 次 select
        Timer timer = meterRegistry.find("pms_sql_duration_seconds")
                .tag("method", "select").timer();
        assertNotNull(timer, "select Timer 应已注册");
        assertEquals(1, timer.count(), "Timer 应记录 1 次");
    }

    @Test
    @DisplayName("1-5s 触发 WARN 计数，ERROR 计数不变")
    void shouldRecordWarnWhenDurationBetween1And5Seconds() {
        // When: 耗时 1500ms（介于 1s 与 5s 之间）
        interceptor.evaluateDuration(1500L, "select", "SELECT * FROM pms_project");

        // Then: WARN 计数 +1，ERROR 计数不变
        assertEquals(1.0, warnCount(), "WARN 计数应为 1");
        assertEquals(0.0, errorCount(), "ERROR 计数应为 0");
    }

    @Test
    @DisplayName(">5s 触发 ERROR 计数，WARN 计数不变")
    void shouldRecordErrorWhenDurationAbove5Seconds() {
        // When: 耗时 5500ms（超过 5s ERROR 阈值）
        interceptor.evaluateDuration(5500L, "select", "SELECT * FROM pms_project");

        // Then: ERROR 计数 +1，WARN 计数不变
        assertEquals(0.0, warnCount(), "WARN 计数应为 0");
        assertEquals(1.0, errorCount(), "ERROR 计数应为 1");
    }

    @Test
    @DisplayName("恰好 1s 不触发 WARN（边界：> 1000ms 才告警）")
    void shouldNotTriggerWarnAtExactThreshold() {
        // When: 耗时恰好 1000ms（等于 WARN 阈值，不满足 > 1000 条件）
        interceptor.evaluateDuration(1000L, "select", "SELECT 1");

        // Then: 不记录慢 SQL
        assertEquals(0.0, warnCount(), "恰好 1000ms 不应触发 WARN");
        assertEquals(0.0, errorCount(), "ERROR 计数应为 0");
    }

    @Test
    @DisplayName("恰好 5s 不触发 ERROR（边界：> 5000ms 才告警）")
    void shouldNotTriggerErrorAtExactThreshold() {
        // When: 耗时恰好 5000ms（等于 ERROR 阈值，不满足 > 5000 条件）
        interceptor.evaluateDuration(5000L, "select", "SELECT 1");

        // Then: 触发 WARN（>1s）但不触发 ERROR
        assertEquals(1.0, warnCount(), "5000ms 应触发 WARN");
        assertEquals(0.0, errorCount(), "恰好 5000ms 不应触发 ERROR");
    }

    @Test
    @DisplayName("多次调用累计计数正确")
    void shouldAccumulateCountersAcrossMultipleCalls() {
        // When: 2 次 WARN + 1 次 ERROR + 1 次正常
        interceptor.evaluateDuration(1200L, "select", "SQL1");
        interceptor.evaluateDuration(2000L, "select", "SQL2");
        interceptor.evaluateDuration(6000L, "select", "SQL3");
        interceptor.evaluateDuration(50L, "select", "SQL4");

        // Then: WARN=2, ERROR=1
        assertEquals(2.0, warnCount(), "WARN 计数应为 2");
        assertEquals(1.0, errorCount(), "ERROR 计数应为 1");
    }

    // ==================== method 标签分类测试 ====================

    @Test
    @DisplayName("Timer 按 method 标签分别记录 select/insert/update/delete")
    void shouldRecordTimerByMethodTag() {
        // When: 不同 SQL 类型分别记录
        interceptor.evaluateDuration(100L, "select", "SELECT 1");
        interceptor.evaluateDuration(100L, "insert", "INSERT 1");
        interceptor.evaluateDuration(100L, "update", "UPDATE 1");
        interceptor.evaluateDuration(100L, "delete", "DELETE 1");

        // Then: 各 method 标签的 Timer 各记录 1 次
        assertEquals(1, timerCount("select"), "select Timer 应记录 1 次");
        assertEquals(1, timerCount("insert"), "insert Timer 应记录 1 次");
        assertEquals(1, timerCount("update"), "update Timer 应记录 1 次");
        assertEquals(1, timerCount("delete"), "delete Timer 应记录 1 次");
    }

    // ==================== SQL 摘要截断测试 ====================

    @Test
    @DisplayName("SQL 摘要超长（>200 字符）被截断为 200 字符 + '...'")
    void shouldTruncateLongSql() {
        // Given: 构造一个超过 200 字符的 SQL
        StringBuilder sb = new StringBuilder("SELECT * FROM pms_project WHERE ");
        while (sb.length() < 300) {
            sb.append("col = ? AND ");
        }
        sb.append("id = ?");
        String longSql = sb.toString();
        assertTrue(longSql.length() > 200, "测试数据应超过 200 字符");

        // When
        String simplified = interceptor.simplifySql(longSql);

        // Then: 截断为 200 字符 + "..."，总长 203
        assertEquals(203, simplified.length(), "截断后长度应为 203（200 + '...'）");
        assertTrue(simplified.endsWith("..."), "截断后应以 '...' 结尾");
    }

    @Test
    @DisplayName("SQL 摘要恰好 200 字符不截断")
    void shouldNotTruncateSqlAtExactLimit() {
        // Given: 恰好 200 字符的 SQL
        String sql = "SELECT ".repeat(1) + "x".repeat(193); // "SELECT " (7) + 193 = 200
        assertEquals(200, sql.length(), "测试数据应恰好 200 字符");

        // When
        String simplified = interceptor.simplifySql(sql);

        // Then: 不截断，长度仍为 200
        assertEquals(200, simplified.length(), "恰好 200 字符不应截断");
        assertTrue(!simplified.endsWith("..."), "不应以 '...' 结尾");
    }

    @Test
    @DisplayName("SQL 摘要合并多余空白与换行")
    void shouldCollapseWhitespaceInSql() {
        // Given: 含换行、制表符、多空格的 SQL
        String sql = "SELECT  *\n\tFROM   pms_project\nWHERE  id = ?";

        // When
        String simplified = interceptor.simplifySql(sql);

        // Then: 多余空白合并为单空格
        assertEquals("SELECT * FROM pms_project WHERE id = ?", simplified,
                "多余空白应合并为单空格");
    }

    @Test
    @DisplayName("null 或空 SQL 返回空字符串")
    void shouldReturnEmptyForNullOrBlankSql() {
        assertEquals("", interceptor.simplifySql(null), "null SQL 应返回空串");
        assertEquals("", interceptor.simplifySql(""), "空 SQL 应返回空串");
    }

    // ==================== 端到端 intercept() 测试 ====================

    @Test
    @DisplayName("intercept() 端到端：Mock StatementHandler，短耗时 SQL 正常返回且不触发慢 SQL")
    void shouldInterceptAndReturnResultWithoutSlowSql() throws Throwable {
        // Given: Mock StatementHandler + BoundSql
        String sql = "SELECT * FROM pms_project WHERE id = ?";
        BoundSql boundSql = mock(BoundSql.class);
        when(boundSql.getSql()).thenReturn(sql);
        StatementHandler handler = mock(StatementHandler.class);
        when(handler.getBoundSql()).thenReturn(boundSql);

        // Invocation.proceed() 返回固定结果（耗时极短，<1s）
        Invocation invocation = new TestInvocation(handler, "prepareResult");

        // When
        Object result = interceptor.intercept(invocation);

        // Then: 原始结果透传
        assertEquals("prepareResult", result, "intercept 应透传 proceed() 结果");
        // And: 未触发慢 SQL
        assertEquals(0.0, warnCount(), "短耗时不应触发 WARN");
        assertEquals(0.0, errorCount(), "短耗时不应触发 ERROR");
        // And: Timer 记录了 1 次 select
        assertEquals(1, timerCount("select"), "Timer 应记录 1 次 select");
    }

    @Test
    @DisplayName("intercept() 端到端：业务异常透传，不吞没")
    void shouldPropagateBusinessExceptionFromProceed() throws Throwable {
        // Given: proceed() 抛出业务异常
        String sql = "SELECT * FROM pms_project";
        BoundSql boundSql = mock(BoundSql.class);
        when(boundSql.getSql()).thenReturn(sql);
        StatementHandler handler = mock(StatementHandler.class);
        when(handler.getBoundSql()).thenReturn(boundSql);

        RuntimeException bizEx = new RuntimeException("DB connection lost");
        Invocation invocation = new TestInvocation(handler, bizEx);

        // When & Then: 异常透传
        RuntimeException thrown = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class, () -> interceptor.intercept(invocation));
        assertEquals("DB connection lost", thrown.getMessage(), "业务异常应透传");
    }

    @Test
    @DisplayName("intercept() 端到端：update SQL 被正确识别为 update method 标签")
    void shouldDetectUpdateMethodInIntercept() throws Throwable {
        // Given: UPDATE 语句
        String sql = "UPDATE pms_project SET status = 1 WHERE id = ?";
        BoundSql boundSql = mock(BoundSql.class);
        when(boundSql.getSql()).thenReturn(sql);
        StatementHandler handler = mock(StatementHandler.class);
        when(handler.getBoundSql()).thenReturn(boundSql);
        Invocation invocation = new TestInvocation(handler, "ok");

        // When
        interceptor.intercept(invocation);

        // Then: method 标签为 update
        assertEquals(1, timerCount("update"), "应记录为 update 标签");
        assertEquals(0, timerCount("select"), "不应记录为 select 标签");
    }

    // ==================== 指标预注册测试 ====================

    @Test
    @DisplayName("构造时预注册 warn/error 计数器，确保 Prometheus 抓取时指标始终存在")
    void shouldPreRegisterCountersOnConstruction() {
        // Then: 构造后 warn/error 计数器已注册（即使未触发慢 SQL）
        Counter warnCounter = meterRegistry.find("pms_slow_sql_total")
                .tag("threshold", "warn").counter();
        Counter errorCounter = meterRegistry.find("pms_slow_sql_total")
                .tag("threshold", "error").counter();

        assertNotNull(warnCounter, "warn 计数器应预注册");
        assertNotNull(errorCounter, "error 计数器应预注册");
        assertEquals(0.0, warnCounter.count(), "初始 warn 计数为 0");
        assertEquals(0.0, errorCounter.count(), "初始 error 计数为 0");
    }

    @Test
    @DisplayName("未记录的 method 标签 Timer 查询返回 null（验证防御性查询）")
    void shouldReturnNullForUnrecordedMethodTag() {
        // When: 仅记录 select
        interceptor.evaluateDuration(100L, "select", "SELECT 1");

        // Then: 未记录的 delete 标签 Timer 不存在
        Timer deleteTimer = meterRegistry.find("pms_sql_duration_seconds")
                .tag("method", "delete").timer();
        assertNull(deleteTimer, "未记录的 method 标签 Timer 应为 null");
    }

    // ==================== 辅助方法 ====================

    /** 获取 warn 阈值的慢 SQL 计数。 */
    private double warnCount() {
        Counter c = meterRegistry.find("pms_slow_sql_total")
                .tag("threshold", "warn").counter();
        return c == null ? 0.0 : c.count();
    }

    /** 获取 error 阈值的慢 SQL 计数。 */
    private double errorCount() {
        Counter c = meterRegistry.find("pms_slow_sql_total")
                .tag("threshold", "error").counter();
        return c == null ? 0.0 : c.count();
    }

    /** 获取指定 method 标签的 Timer 记录次数。 */
    private long timerCount(String method) {
        Timer t = meterRegistry.find("pms_sql_duration_seconds")
                .tag("method", method).timer();
        return t == null ? 0 : t.count();
    }

    /**
     * 测试用 {@link Invocation} 子类：重写 {@link #proceed()} 控制返回值或抛出异常，
     * 避免反射调用真实方法。无需传入真实 {@link Method}（因 proceed() 被重写）。
     *
     * <p>注意：父类 {@link Invocation#proceed()} 声明抛出
     * {@code InvocationTargetException, IllegalAccessException}，子类覆盖方法不能声明
     * 更宽的受检异常。因此业务异常仅使用 {@link RuntimeException}（非受检，无需声明），
     * 覆盖方法不声明任何受检异常（Java 允许覆盖方法抛出更少的受检异常）。</p>
     */
    private static class TestInvocation extends Invocation {
        /** proceed() 返回值（当 throwEx 为 null 时生效）。 */
        private final Object result;
        /** proceed() 抛出的业务异常（非 null 时优先；必须为非受检异常）。 */
        private final RuntimeException throwEx;

        TestInvocation(Object target, Object result) {
            super(target, null, null);
            this.result = result;
            this.throwEx = null;
        }

        TestInvocation(Object target, RuntimeException throwEx) {
            super(target, null, null);
            this.result = null;
            this.throwEx = throwEx;
        }

        @Override
        public Object proceed() {
            if (throwEx != null) {
                throw throwEx;
            }
            return result;
        }
    }
}
