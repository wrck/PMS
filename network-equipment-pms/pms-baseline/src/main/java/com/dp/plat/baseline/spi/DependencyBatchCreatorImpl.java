package com.dp.plat.baseline.spi;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.baseline.entity.TaskDependency;
import com.dp.plat.baseline.mapper.TaskDependencyMapper;
import com.dp.plat.common.dto.TemplateSnapshot.DependencyDef;
import com.dp.plat.common.spi.DependencyBatchCreator;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务依赖批量创建 SPI 实现（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过 {@link DependencyBatchCreator} 跨模块调用本实现，
 * 批量插入任务依赖记录到 {@code pms_task_dependency} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-baseline}。</p>
 *
 * <p>实现策略：
 * <ol>
 *   <li>查询项目下全部任务，构建 {@code taskName → taskId} 映射</li>
 *   <li>遍历 {@link DependencyDef}，解析 {@code predecessorTaskName/successorTaskName} 为任务 ID</li>
 *   <li>构造 {@link TaskDependency} 直接 insert（不走 {@code saveDependency} 循环检测，
 *       因为模板内的依赖关系在模板设计阶段已校验过，深拷贝无需重复校验）</li>
 * </ol>
 * </p>
 *
 * <p>注：模板深拷贝产生的依赖必然是项目内任务间的依赖（同项目内），且依赖关系在模板设计阶段
 * 已校验无环。若名称解析失败（任务被删或重命名），跳过该依赖并 log.warn。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DependencyBatchCreatorImpl implements DependencyBatchCreator {

    private static final String DEFAULT_DEPENDENCY_TYPE = "FS";
    private static final Integer DEFAULT_LAG_DAYS = 0;

    private final TaskDependencyMapper taskDependencyMapper;
    private final ImplTaskMapper implTaskMapper;

    @Override
    public void batchCreateDependencies(Long projectId, List<DependencyDef> dependencyDefs) {
        if (projectId == null || dependencyDefs == null || dependencyDefs.isEmpty()) {
            return;
        }

        // 1. 查询项目下全部任务，构建 name → id 映射
        List<ImplTask> tasks = implTaskMapper.selectList(
                new LambdaQueryWrapper<ImplTask>().eq(ImplTask::getProjectId, projectId));
        Map<String, Long> nameToId = new HashMap<>();
        for (ImplTask t : tasks) {
            nameToId.put(t.getTaskName(), t.getId());
        }

        // 2. 遍历依赖定义，解析名称 → ID 后插入
        int created = 0;
        int skipped = 0;
        for (DependencyDef def : dependencyDefs) {
            Long predecessorId = nameToId.get(def.getPredecessorTaskName());
            Long successorId = nameToId.get(def.getSuccessorTaskName());
            if (predecessorId == null || successorId == null) {
                log.warn("模板深拷贝：跳过依赖 {} → {}（任务名称解析失败，projectId={}）",
                        def.getPredecessorTaskName(), def.getSuccessorTaskName(), projectId);
                skipped++;
                continue;
            }
            TaskDependency dep = TaskDependency.builder()
                    .projectId(projectId)
                    .predecessorTaskId(predecessorId)
                    .successorTaskId(successorId)
                    .dependencyType(def.getDependencyType() != null
                            ? def.getDependencyType() : DEFAULT_DEPENDENCY_TYPE)
                    .lagDays(def.getLagDays() != null ? def.getLagDays() : DEFAULT_LAG_DAYS)
                    .build();
            taskDependencyMapper.insert(dep);
            created++;
        }
        log.info("模板深拷贝：批量创建任务依赖完成 projectId={} created={} skipped={}",
                projectId, created, skipped);
    }
}
