package com.dp.plat.common.spi;

import com.dp.plat.common.dto.ApprovalViolation;

import java.util.List;

/**
 * 审批状态校验 SPI（TD-P8-005）。
 *
 * <p>{@code pms-project} 的 {@code validateExitGate} APPROVAL 分支通过本 SPI 跨模块校验
 * {@code pms-workflow} 审批中心中关联审批是否已通过。设计文档 §3.4 定义 APPROVAL 类退出条件为
 * 「关联审批通过」，本 SPI 提供按项目+审批类型查询审批状态的能力。</p>
 *
 * <p>由 {@code pms-workflow} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载（bean 不存在），APPROVAL 分支跳过校验（仅 log.warn）。</p>
 *
 * <p>注：TD-P8-001 修复后，{@code pms-project} 仍单向依赖 {@code pms-workflow}，
 * 但通过 SPI 注入可保持解耦语义，并为未来完全移除依赖留出空间。</p>
 */
public interface ApprovalStatusChecker {

    /**
     * 查询指定项目下指定审批类型的违规项（未通过审批）。
     *
     * <p>若 {@code mustApproved=true} 但审批未通过或不存在，返回对应违规；
     * 若审批已 APPROVED，返回空列表。</p>
     *
     * @param projectId     项目ID
     * @param approvalType  审批类型（如 PHASE_EXIT）
     * @param mustApproved  是否必须已通过
     * @return 违规列表（空列表表示已通过或不要求）
     */
    List<ApprovalViolation> findApprovalViolations(Long projectId, String approvalType, boolean mustApproved);
}
