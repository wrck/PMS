package com.dp.plat.common.spi;

import com.dp.plat.common.dto.DeliverableViolation;

import java.util.List;

/**
 * 必需交付件校验 SPI（TD-P8-012）。
 *
 * <p>{@code pms-project} 的 {@code validateExitGate} DELIVERABLE 分支通过本 SPI 复用
 * {@code pms-deliverable} 中已实现的 {@code validateMandatoryDeliverables} 逻辑，
 * 避免两套并行校验逻辑（精确匹配 vs 集合判断）。</p>
 *
 * <p>由 {@code pms-deliverable} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载（bean 不存在），fallback 到 {@code ProjectPhaseServiceImpl} 内联的
 * 集合判断逻辑（已按 TD-P8-011 修复）。</p>
 *
 * <p>注：方法名刻意不同于 {@code DeliverableService.validateMandatoryDeliverables}（返回
 * {@code MandatoryDeliverableValidationResult}），避免 Java 方法签名冲突，
 * 同时 SPI 返回简化的 {@link DeliverableViolation} 列表便于跨模块传递。</p>
 */
public interface MandatoryDeliverableValidator {

    /**
     * 校验阶段下所有 {@code mandatory=true} 的交付件是否均已达到「已批准」状态
     * （PUBLISHED/REFERENCED/ARCHIVED）。
     *
     * @param phaseId 阶段ID
     * @return 未满足的必需交付件违规列表（空列表表示全部通过）
     */
    List<DeliverableViolation> findMandatoryDeliverableViolations(Long phaseId);
}
