package com.dp.plat.common.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用 Saga 协调器：管理 Saga 步骤的顺序执行与反向补偿。
 *
 * <p>Saga 模式适用于跨多个独立事务/外部系统的长流程：每个步骤独立提交事务，
 * 任一步骤失败时，按反向顺序对已成功步骤执行补偿动作，最终使系统达到语义一致
 * （要么全部成功，要么全部回滚到流程开始前的状态）。</p>
 *
 * <p><b>核心语义</b>：</p>
 * <ul>
 *   <li>步骤按 {@code steps} 顺序执行，前一步成功（action 返回 {@code true}）才执行下一步。</li>
 *   <li>action 返回 {@code false} 或抛出异常视为失败，立即停止后续步骤并触发补偿。</li>
 *   <li>补偿按已成功步骤的反向顺序执行；compensation 为 {@code null} 的步骤跳过补偿。</li>
 *   <li><b>补偿失败不抛异常</b>：单个补偿动作失败仅记录日志，不影响后续补偿执行
 *       （避免一个补偿失败导致其他已执行步骤无法回滚）。</li>
 *   <li>补偿动作应设计为<b>幂等</b>：相同补偿可能被多次调用（如人工重试）。</li>
 * </ul>
 *
 * <p><b>事务边界</b>：本协调器不管理事务，每个步骤的 action / compensation 内部应
 * 自行控制事务（独立事务），确保步骤间数据相互可见，且补偿能看到已提交的数据。</p>
 *
 * @param <T> Saga 上下文类型，承载业务数据并在步骤间传递
 */
@Component
public class SagaCoordinator {

    private static final Logger log = LoggerFactory.getLogger(SagaCoordinator.class);

    /**
     * 执行一个 Saga 流程。
     *
     * @param sagaName Saga 名称，用于日志标识
     * @param steps    有序步骤列表
     * @param context  Saga 上下文，承载业务数据
     * @param <T>      上下文类型
     * @return Saga 执行结果，包含成功标志、已执行步骤、已补偿步骤及错误信息
     */
    public <T> SagaResult<T> execute(String sagaName, List<SagaStep<T>> steps, T context) {
        if (steps == null || steps.isEmpty()) {
            log.warn("Saga[{}] 步骤列表为空，直接返回成功", sagaName);
            return SagaResult.success(context, Collections.emptyList(), Collections.emptyList());
        }

        List<String> executedSteps = new ArrayList<>();
        List<String> compensatedSteps = new ArrayList<>();
        // 已成功执行的步骤（含补偿动作的引用），用于反向补偿
        List<SagaStep<T>> succeededSteps = new ArrayList<>();

        log.info("Saga[{}] 开始执行，共 {} 个步骤", sagaName, steps.size());

        // ---- 正向执行阶段 ----
        try {
            for (SagaStep<T> step : steps) {
                log.info("Saga[{}] 执行步骤: {}", sagaName, step.getName());
                Boolean result;
                try {
                    result = step.getAction().apply(context);
                } catch (Exception e) {
                    log.error("Saga[{}] 步骤[{}]执行抛出异常: {}", sagaName, step.getName(), e.getMessage(), e);
                    throw e;
                }
                if (result == null || !result) {
                    throw new SagaExecutionException("步骤[" + step.getName() + "]执行返回失败结果");
                }
                executedSteps.add(step.getName());
                succeededSteps.add(step);
                log.info("Saga[{}] 步骤[{}]执行成功", sagaName, step.getName());
            }

            log.info("Saga[{}] 全部 {} 个步骤执行成功", sagaName, steps.size());
            return SagaResult.success(context, executedSteps, compensatedSteps);

        } catch (Exception failureCause) {
            // ---- 反向补偿阶段 ----
            Throwable cause = failureCause instanceof SagaExecutionException
                    && failureCause.getCause() != null
                    ? failureCause.getCause()
                    : failureCause;
            String errorMessage = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
            log.error("Saga[{}] 执行失败，开始反向补偿。已执行步骤: {}，错误: {}",
                    sagaName, executedSteps, errorMessage, failureCause);

            compensate(sagaName, succeededSteps, context, compensatedSteps);

            log.warn("Saga[{}] 补偿完成，已补偿步骤: {}", sagaName, compensatedSteps);
            return SagaResult.failure(context, errorMessage, executedSteps, compensatedSteps);
        }
    }

    /**
     * 按反向顺序对已成功步骤执行补偿。
     * 单个补偿失败仅记录日志，不中断后续补偿。
     */
    private <T> void compensate(String sagaName, List<SagaStep<T>> succeededSteps,
                                T context, List<String> compensatedSteps) {
        for (int i = succeededSteps.size() - 1; i >= 0; i--) {
            SagaStep<T> step = succeededSteps.get(i);
            if (step.getCompensation() == null) {
                log.info("Saga[{}] 步骤[{}]无补偿动作，跳过", sagaName, step.getName());
                continue;
            }
            try {
                log.info("Saga[{}] 补偿步骤: {}", sagaName, step.getName());
                step.getCompensation().accept(context);
                compensatedSteps.add(step.getName());
                log.info("Saga[{}] 步骤[{}]补偿成功", sagaName, step.getName());
            } catch (Exception compEx) {
                // 关键：补偿失败不能打断后续补偿，仅记录错误日志
                log.error("Saga[{}] 步骤[{}]补偿失败（不影响后续补偿）: {}",
                        sagaName, step.getName(), compEx.getMessage(), compEx);
            }
        }
    }

    /**
     * Saga 步骤定义：一个正向动作 + 一个可选的补偿动作。
     *
     * @param <T> 上下文类型
     */
    public static class SagaStep<T> {

        /** 步骤名称，用于日志与结果追踪。 */
        private final String name;
        /** 正向动作：返回 true 表示成功，false 或抛异常表示失败。 */
        private final Function<T, Boolean> action;
        /** 补偿动作：null 表示该步骤无需补偿。应为幂等操作。 */
        private final Consumer<T> compensation;

        public SagaStep(String name, Function<T, Boolean> action, Consumer<T> compensation) {
            this.name = name;
            this.action = action;
            this.compensation = compensation;
        }

        /** 构造无补偿的步骤。 */
        public static <T> SagaStep<T> of(String name, Function<T, Boolean> action) {
            return new SagaStep<>(name, action, null);
        }

        /** 构造带补偿的步骤。 */
        public static <T> SagaStep<T> of(String name, Function<T, Boolean> action, Consumer<T> compensation) {
            return new SagaStep<>(name, action, compensation);
        }

        public String getName() {
            return name;
        }

        public Function<T, Boolean> getAction() {
            return action;
        }

        public Consumer<T> getCompensation() {
            return compensation;
        }
    }

    /**
     * Saga 执行结果。
     *
     * @param <T> 上下文类型
     */
    public static class SagaResult<T> {

        /** 是否全部步骤成功。 */
        private final boolean success;
        /** Saga 上下文（可能被步骤修改）。 */
        private final T context;
        /** 失败时的错误信息；成功时为 null。 */
        private final String errorMessage;
        /** 已成功执行的正向步骤名称列表（按执行顺序）。 */
        private final List<String> executedSteps;
        /** 已成功执行的补偿步骤名称列表（按补偿执行顺序）。 */
        private final List<String> compensatedSteps;

        public SagaResult(boolean success, T context, String errorMessage,
                          List<String> executedSteps, List<String> compensatedSteps) {
            this.success = success;
            this.context = context;
            this.errorMessage = errorMessage;
            this.executedSteps = executedSteps == null
                    ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(executedSteps));
            this.compensatedSteps = compensatedSteps == null
                    ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(compensatedSteps));
        }

        public static <T> SagaResult<T> success(T context, List<String> executedSteps, List<String> compensatedSteps) {
            return new SagaResult<>(true, context, null, executedSteps, compensatedSteps);
        }

        public static <T> SagaResult<T> failure(T context, String errorMessage,
                                                 List<String> executedSteps, List<String> compensatedSteps) {
            return new SagaResult<>(false, context, errorMessage, executedSteps, compensatedSteps);
        }

        public boolean isSuccess() {
            return success;
        }

        public T getContext() {
            return context;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public List<String> getExecutedSteps() {
            return executedSteps;
        }

        public List<String> getCompensatedSteps() {
            return compensatedSteps;
        }
    }

    /**
     * 内部异常：用于区分步骤返回 false 与抛出异常两种失败路径。
     */
    private static class SagaExecutionException extends RuntimeException {
        SagaExecutionException(String message) {
            super(message);
        }
    }
}
