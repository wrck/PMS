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
}
