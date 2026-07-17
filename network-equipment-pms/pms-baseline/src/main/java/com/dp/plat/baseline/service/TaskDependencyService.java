package com.dp.plat.baseline.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.baseline.entity.TaskDependency;

import java.util.List;

/**
 * 任务依赖服务 — 保存依赖（含 DFS 循环检测）、删除、查询。
 *
 * <p>关联设计文档：§3.6 依赖与基线规则、§5.5 依赖 API。</p>
 */
public interface TaskDependencyService extends IService<TaskDependency> {

    /**
     * 保存任务依赖（含循环检测）。
     *
     * <p>校验依赖类型合法后，沿 predecessor→successor 方向做 DFS：从
     * {@code successorTaskId} 出发，若能到达 {@code predecessorTaskId} 则形成闭环，
     * 抛出 {@link com.dp.plat.baseline.exception.CycleDetectedException}
     * （携带 cyclePath，含首尾闭合节点）。无闭环则持久化依赖关系。</p>
     *
     * @param dependency 任务依赖（predecessor→successor）
     * @return 已保存的依赖（含生成的 id）
     */
    TaskDependency saveDependency(TaskDependency dependency);

    /**
     * 删除任务依赖（逻辑删除）。
     *
     * @param id 依赖ID
     */
    void deleteDependency(Long id);

    /**
     * 查询项目下全部任务依赖。
     *
     * @param projectId 项目ID
     * @return 依赖列表
     */
    List<TaskDependency> listByProject(Long projectId);
}
