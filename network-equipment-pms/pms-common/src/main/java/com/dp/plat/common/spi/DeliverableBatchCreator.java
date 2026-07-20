package com.dp.plat.common.spi;

import com.dp.plat.common.dto.TemplateSnapshot.DeliverableDef;

import java.util.List;

/**
 * 交付件批量创建 SPI（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过本 SPI 跨模块调用 {@code pms-deliverable}
 * 批量插入交付件记录到 {@code pms_deliverable} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-deliverable}（防止新的依赖环）。</p>
 *
 * <p>由 {@code pms-deliverable} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载，跳过交付件深拷贝并 log.warn。</p>
 */
public interface DeliverableBatchCreator {

    /**
     * 批量创建交付件。
     *
     * <p>实现应将交付件初始化为 DRAFT 状态、currentVersion=1。
     * {@link DeliverableDef#getPhaseCode()} 已由调用方解析为 phaseId 参数。</p>
     *
     * @param projectId       项目ID
     * @param phaseId         阶段ID
     * @param deliverableDefs 交付件定义列表（已按 phaseCode 分组）
     */
    void batchCreateDeliverables(Long projectId, Long phaseId, List<DeliverableDef> deliverableDefs);
}
