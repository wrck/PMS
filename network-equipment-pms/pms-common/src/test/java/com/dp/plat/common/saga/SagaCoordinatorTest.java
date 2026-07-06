package com.dp.plat.common.saga;

import com.dp.plat.common.saga.SagaCoordinator.SagaResult;
import com.dp.plat.common.saga.SagaCoordinator.SagaStep;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SagaCoordinator} 通用框架单元测试。
 *
 * <p>使用可变上下文对象记录步骤执行与补偿调用顺序，验证协调器的核心语义：
 * 顺序执行、反向补偿、补偿失败隔离、空步骤列表处理。</p>
 */
class SagaCoordinatorTest {

    private final SagaCoordinator sagaCoordinator = new SagaCoordinator();

    /** 可变上下文：记录步骤执行顺序与补偿调用顺序。 */
    private static class TraceContext {
        final List<String> executed = new ArrayList<>();
        final List<String> compensated = new ArrayList<>();
        /** 控制某步骤是否失败（stepName -> 抛异常）。 */
        java.util.Map<String, RuntimeException> failures = new java.util.HashMap<>();
    }

    /** 创建一个步骤：执行时记录到 context.executed，补偿时记录到 context.compensated。 */
    private static SagaStep<TraceContext> step(String name, TraceContext ctx) {
        return SagaStep.of(name, c -> {
            c.executed.add(name);
            RuntimeException ex = c.failures.get(name);
            if (ex != null) {
                throw ex;
            }
            return true;
        }, c -> c.compensated.add(name));
    }

    /** 创建无补偿的步骤。 */
    private static SagaStep<TraceContext> stepNoCompensation(String name, TraceContext ctx) {
        return SagaStep.of(name, c -> {
            c.executed.add(name);
            RuntimeException ex = c.failures.get(name);
            if (ex != null) {
                throw ex;
            }
            return true;
        });
    }

    @Test
    @DisplayName("全部步骤成功：返回成功，executedSteps 按顺序记录，compensatedSteps 为空")
    void execute_allStepsSucceed_returnsSuccess() {
        TraceContext ctx = new TraceContext();
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                step("s1", ctx), step("s2", ctx), step("s3", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertTrue(result.isSuccess());
        assertNull(result.getErrorMessage());
        assertEquals(Arrays.asList("s1", "s2", "s3"), result.getExecutedSteps());
        assertTrue(result.getCompensatedSteps().isEmpty());
        assertEquals(Arrays.asList("s1", "s2", "s3"), ctx.executed);
        assertTrue(ctx.compensated.isEmpty());
    }

    @Test
    @DisplayName("中间步骤失败：已成功步骤反向补偿，失败步骤本身不补偿")
    void execute_middleStepFails_compensatesInReverseOrder() {
        TraceContext ctx = new TraceContext();
        ctx.failures.put("s2", new RuntimeException("s2 故障"));
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                step("s1", ctx), step("s2", ctx), step("s3", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("s2 故障"));
        // s1 成功，s2 失败（未加入 executed），s3 未执行
        assertEquals(Collections.singletonList("s1"), result.getExecutedSteps());
        // 仅 s1 被补偿（s2 失败未加入成功列表，s3 未执行）
        assertEquals(Collections.singletonList("s1"), result.getCompensatedSteps());
        // 补偿按反向顺序：s1
        assertEquals(Collections.singletonList("s1"), ctx.compensated);
    }

    @Test
    @DisplayName("补偿失败不影响其他补偿执行：所有补偿均被调用")
    void execute_compensationFailure_doesNotBlockOtherCompensations() {
        TraceContext ctx = new TraceContext();
        ctx.failures.put("s3", new RuntimeException("s3 故障"));
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                SagaStep.of("s1", c -> {
                    c.executed.add("s1");
                    return true;
                }, c -> {
                    c.compensated.add("s1");
                    // s1 补偿故意失败
                    throw new RuntimeException("s1 补偿失败");
                }),
                step("s2", ctx),
                step("s3", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertFalse(result.isSuccess());
        assertEquals(Arrays.asList("s1", "s2"), result.getExecutedSteps());
        // s1 补偿失败（未加入 compensatedSteps），s2 补偿成功
        assertEquals(Collections.singletonList("s2"), result.getCompensatedSteps());
        // 但两个补偿均被调用（s1 抛异常仍被记录到 ctx.compensated 之前，实际未加入）
        // 关键断言：s2 补偿执行了（s1 补偿失败没有阻断它）
        assertTrue(ctx.compensated.contains("s2"));
    }

    @Test
    @DisplayName("无补偿动作的步骤：失败时跳过补偿，不影响其他步骤补偿")
    void execute_stepWithoutCompensation_skippedDuringCompensation() {
        TraceContext ctx = new TraceContext();
        ctx.failures.put("s3", new RuntimeException("s3 故障"));
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                stepNoCompensation("s1", ctx),  // 无补偿
                step("s2", ctx),
                step("s3", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertFalse(result.isSuccess());
        assertEquals(Arrays.asList("s1", "s2"), result.getExecutedSteps());
        // s1 无补偿被跳过，仅 s2 被补偿
        assertEquals(Collections.singletonList("s2"), result.getCompensatedSteps());
    }

    @Test
    @DisplayName("步骤返回 false 视为失败：触发补偿")
    void execute_stepReturnsFalse_triggersCompensation() {
        TraceContext ctx = new TraceContext();
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                step("s1", ctx),
                SagaStep.of("s2", c -> {
                    c.executed.add("s2");
                    return false;  // 返回 false
                }, c -> c.compensated.add("s2")),
                step("s3", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertFalse(result.isSuccess());
        // s2 返回 false 但已记录到 executed（在 action 内手动 add），但协调器不会加入 executedSteps
        // 实际上协调器在 result=false 时不加入 executedSteps
        assertEquals(Collections.singletonList("s1"), result.getExecutedSteps());
        assertEquals(Collections.singletonList("s1"), result.getCompensatedSteps());
    }

    @Test
    @DisplayName("空步骤列表：直接返回成功")
    void execute_emptySteps_returnsSuccess() {
        TraceContext ctx = new TraceContext();

        SagaResult<TraceContext> result = sagaCoordinator.execute("EmptySaga",
                Collections.emptyList(), ctx);

        assertTrue(result.isSuccess());
        assertTrue(result.getExecutedSteps().isEmpty());
        assertTrue(result.getCompensatedSteps().isEmpty());
    }

    @Test
    @DisplayName("null 步骤列表：直接返回成功")
    void execute_nullSteps_returnsSuccess() {
        TraceContext ctx = new TraceContext();

        SagaResult<TraceContext> result = sagaCoordinator.execute("NullSaga", null, ctx);

        assertTrue(result.isSuccess());
        assertNotNull(result.getContext());
    }

    @Test
    @DisplayName("首步骤失败：无已成功步骤，无补偿执行")
    void execute_firstStepFails_noCompensation() {
        TraceContext ctx = new TraceContext();
        ctx.failures.put("s1", new RuntimeException("s1 故障"));
        List<SagaStep<TraceContext>> steps = Arrays.asList(
                step("s1", ctx), step("s2", ctx));

        SagaResult<TraceContext> result = sagaCoordinator.execute("TestSaga", steps, ctx);

        assertFalse(result.isSuccess());
        assertTrue(result.getExecutedSteps().isEmpty());
        assertTrue(result.getCompensatedSteps().isEmpty());
    }

    @Test
    @DisplayName("SagaResult 不可变：executedSteps/compensatedSteps 修改不影响原结果")
    void sagaResult_stepsAreImmutable() {
        List<String> executed = new ArrayList<>(Arrays.asList("s1", "s2"));
        SagaResult<TraceContext> result = SagaResult.success(new TraceContext(), executed,
                Collections.emptyList());
        executed.add("s3");

        assertEquals(2, result.getExecutedSteps().size());
    }
}
