package com.dp.plat.baseline.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.baseline.dto.BaselineDiffResult;
import com.dp.plat.baseline.entity.BaselineSnapshot;

import java.util.List;

/**
 * 计划基线服务 — 保存基线（快照全部任务）、偏差分析、变更审批触发。
 *
 * <p>关联设计文档：§2.2 BaselineSnapshot、§3.6 计划基线偏差分析、§5.5。</p>
 */
public interface BaselineService extends IService<BaselineSnapshot> {

    /**
     * 保存新基线 — 快照项目下全部任务计划字段。
     *
     * <p>若项目已有 APPROVED 基线，将其状态置为 SUPERSEDED（单一活跃基线）；
     * 新建基线状态为 DRAFT。</p>
     *
     * @param projectId     项目ID
     * @param baselineName  基线名称（为空则按时间生成）
     * @return 已创建的基线快照
     */
    BaselineSnapshot saveBaseline(Long projectId, String baselineName);

    /**
     * 查询项目下全部基线（按创建时间倒序）。
     *
     * @param projectId 项目ID
     * @return 基线列表
     */
    List<BaselineSnapshot> listByProject(Long projectId);

    /**
     * 基线偏差分析 — 逐任务对比当前计划与基线快照。
     *
     * <p>关联设计文档：§3.6、§5.5 Story 4 验收 2。计算每个任务的开始/结束偏差天数
     * 与偏差百分比，并按双阈值（天数 OR 百分比）判定是否需要审批。</p>
     *
     * @param baselineId 基线ID
     * @return 偏差分析结果（含基线摘要 + diffs 列表 + needsApproval）
     */
    BaselineDiffResult compareWithBaseline(Long baselineId);

    /**
     * 申请基线变更 — 双阈值 OR 逻辑触发审批。
     *
     * <p>关联设计文档：§3.6「关键日期变更触发审批」、§5.5。流程：</p>
     * <ol>
     *   <li>调用 {@link #compareWithBaseline} 计算偏差（天数/百分比双阈值）。</li>
     *   <li>读取项目配置 {@code baseline.variance.threshold.count}（默认 3 个任务）。</li>
     *   <li>双阈值 OR：若任一任务偏差超 {@code baseline.variance.days.threshold}
     *       （默认 5 天）OR 偏差任务数超 count 阈值 → 触发 {@code BASELINE_CHANGE}
     *       审批（Phase 7 实现具体审批流程，此处留 TODO，基线保持 DRAFT 待审批通过）。</li>
     *   <li>未超阈值 → 直接更新基线状态为 APPROVED。</li>
     * </ol>
     *
     * @param baselineId   基线ID
     * @param changeReason 变更原因（超阈值时随审批记录提交）
     * @return 偏差分析结果（needsApproval 已合并 count 阈值；未超阈值时基线状态已置 APPROVED）
     */
    BaselineDiffResult requestBaselineChange(Long baselineId, String changeReason);
}
