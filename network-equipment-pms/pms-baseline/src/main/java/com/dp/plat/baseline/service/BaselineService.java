package com.dp.plat.baseline.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
}
