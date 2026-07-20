package com.dp.plat.common.spi;

/**
 * 审批流程触发 SPI（TD-P8-008 BASELINE_CHANGE 审批实际触发用）。
 *
 * <p>供 {@code pms-baseline} 等业务模块在跨模块场景下触发审批流程，避免直接依赖
 * {@code pms-workflow}（与 TD-P8-001 解耦模式一致）。</p>
 *
 * <p>由 {@code pms-workflow} 模块实现并注册为 Spring Bean，业务模块通过
 * {@code @Autowired(required=false)} 注入。若模块未加载，调用方应 log.warn 跳过
 * 并保留业务数据（不阻断主流程）。</p>
 *
 * <p>实现应：
 * <ol>
 *   <li>构造 {@code ApprovalRecord}（approvalType/businessId/projectId/title/submitterId）</li>
 *   <li>调用 {@code ApprovalCenterService.createApproval} 落库（状态 PENDING，round=1）</li>
 *   <li>由 {@code ApprovalDispatcher} 启动 Flowable 流程实例并回填 processInstanceId</li>
 *   <li>返回审批记录ID供调用方回填业务对象的 approvalRecordId 字段</li>
 * </ol>
 * </p>
 *
 * <p>关联设计文档：§3.5 审批中心统一规则、§4.5 基线变更审批两阶段事务、§6.9 审批类型表。</p>
 */
public interface ApprovalTrigger {

    /**
     * 触发审批流程，返回审批记录ID。
     *
     * <p>调用方应在事务内调用，并将返回的 approvalRecordId 回填到业务对象的对应字段。</p>
     *
     * @param approvalType 审批类型（PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/RESOURCE/
     *                     COST/PHASE_EXIT/BASELINE_CHANGE，参见 §6.9）
     * @param businessId   业务对象ID（如 baselineId / projectId / taskId）
     * @param projectId    项目ID（用于项目维度审批列表，可空）
     * @param title        审批标题（不可空）
     * @param reason       变更原因/审批说明（可空，写入 ApprovalRecord 的扩展字段或历史记录）
     * @return 审批记录ID；触发失败时返回 null（调用方应保留业务数据并 log.warn）
     */
    Long triggerApproval(String approvalType, Long businessId, Long projectId,
                         String title, String reason);
}
