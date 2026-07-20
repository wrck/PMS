package com.dp.plat.common.spi;

import com.dp.plat.common.dto.TemplateSnapshot.DependencyDef;

import java.util.List;

/**
 * 任务依赖批量创建 SPI（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过本 SPI 跨模块调用 {@code pms-baseline}
 * 批量插入任务依赖记录到 {@code pms_task_dependency} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-baseline}。</p>
 *
 * <p>由 {@code pms-baseline} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载，跳过依赖深拷贝并 log.warn。</p>
 *
 * <p>注：依赖定义通过任务名称引用（{@code predecessorTaskName/successorTaskName}），
 * 实现需在项目下查询任务名称 → ID 映射后创建依赖记录。</p>
 */
public interface DependencyBatchCreator {

    /**
     * 批量创建任务依赖。
     *
     * <p>实现需自行查询项目下的任务（{@code pms_impl_task}）解析
     * {@link DependencyDef#getPredecessorTaskName()} / {@link DependencyDef#getSuccessorTaskName()}
     * 为任务 ID。</p>
     *
     * @param projectId      项目ID
     * @param dependencyDefs 依赖定义列表
     */
    void batchCreateDependencies(Long projectId, List<DependencyDef> dependencyDefs);
}
