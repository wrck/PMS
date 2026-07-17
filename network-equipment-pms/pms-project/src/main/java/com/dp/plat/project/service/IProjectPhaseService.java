package com.dp.plat.project.service;

import com.dp.plat.common.result.Result;
import com.dp.plat.project.entity.ProjectPhase;

import java.util.List;

/**
 * 项目阶段服务接口
 * 关联设计文档：§4.3 Story 2
 */
public interface IProjectPhaseService {

    /** 查询项目所有阶段（按 sortOrder 排序） */
    List<ProjectPhase> listByProjectId(Long projectId);

    /** 查询阶段详情 */
    ProjectPhase getById(Long id);

    /** 新增阶段 */
    ProjectPhase create(ProjectPhase phase);

    /** 更新阶段 */
    ProjectPhase update(ProjectPhase phase);

    /** 删除阶段 */
    void delete(Long id);

    /** 批量保存项目阶段（用于从模板创建项目时深拷贝） */
    List<ProjectPhase> batchCreate(List<ProjectPhase> phases);

    /**
     * 推进阶段（含 4 类退出条件校验）。
     *
     * <p>关联设计文档：§3.2 阶段状态机、§3.2 Story 2 验收 1、§5.3
     * POST /api/project/phase/{phaseId}/advance。
     *
     * <p>逻辑：
     * <ol>
     *   <li>校验当前阶段状态为 IN_PROGRESS</li>
     *   <li>加载 exitCriteria（PhaseExitGate），逐项校验 4 类退出条件</li>
     *   <li>任一未满足 → 抛 {@code PhaseExitGateFailedException}（含 violations）</li>
     *   <li>全部满足 → 当前阶段置 COMPLETED，激活下一阶段为 IN_PROGRESS</li>
     *   <li>若为最后阶段 → 更新项目状态为 CLOSING</li>
     * </ol>
     *
     * @param phaseId 阶段 ID
     * @return 推进成功后激活的阶段（或最后阶段本身）；失败时抛出异常
     */
    Result<ProjectPhase> advancePhase(Long phaseId);
}
