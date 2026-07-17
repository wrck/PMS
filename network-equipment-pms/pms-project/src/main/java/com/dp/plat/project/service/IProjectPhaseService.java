package com.dp.plat.project.service;

import com.dp.plat.project.entity.ProjectPhase;

import java.util.List;

/**
 * 项目阶段服务接口
 * 关联设计文档：§4.3 Story 2
 *
 * <p>注：advancePhase / closeProject / validateExitGates / validateSubProjectsClosed
 * 在 Phase 3 实现计划中实现，本接口仅提供基本 CRUD。
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
}
