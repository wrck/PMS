package com.dp.plat.implementation.spi;

import com.dp.plat.common.dto.TemplateSnapshot.TaskDef;
import com.dp.plat.common.spi.TaskBatchCreator;
import com.dp.plat.implementation.entity.ImplTask;
import com.dp.plat.implementation.mapper.ImplTaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务批量创建 SPI 实现（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过 {@link TaskBatchCreator} 跨模块调用本实现，
 * 批量插入任务记录到 {@code pms_impl_task} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-implementation}。</p>
 *
 * <p>两阶段创建：
 * <ol>
 *   <li>第一遍：插入全部任务（parentTaskId/taskPath 留空），构建 {@code taskName → taskId} 映射</li>
 *   <li>第二遍：根据 {@link TaskDef#getParentTaskName()} 回填 parentTaskId、taskPath、depth</li>
 * </ol>
 * </p>
 *
 * <p>taskPath 约定：顶层任务 {@code /<id>/}，子任务 {@code <父taskPath><id>/}（与
 * {@code ImplTaskServiceImpl.moveTask} 一致）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskBatchCreatorImpl implements TaskBatchCreator {

    private static final String DEFAULT_STATUS = "PENDING";
    private static final String DEFAULT_PRIORITY = "MEDIUM";

    private final ImplTaskMapper implTaskMapper;

    @Override
    public void batchCreateTasks(Long projectId, Long phaseId, List<TaskDef> taskDefs) {
        if (projectId == null || phaseId == null || taskDefs == null || taskDefs.isEmpty()) {
            return;
        }

        // 第一遍：创建全部任务（暂不设 parentTaskId），构建 name → id 映射
        Map<String, Long> nameToId = new HashMap<>();
        for (TaskDef def : taskDefs) {
            ImplTask task = ImplTask.builder()
                    .projectId(projectId)
                    .phaseId(phaseId)
                    .taskName(def.getTaskName())
                    .taskType(def.getTaskType())
                    .plannedHours(def.getPlannedHours())
                    .priority(def.getPriority() != null ? def.getPriority() : DEFAULT_PRIORITY)
                    .status(DEFAULT_STATUS)
                    .progress(0)
                    .parentTaskId(null)
                    .taskPath("/")
                    .depth(0)
                    .signOffRequired(true)
                    .taskWeight(BigDecimal.ONE)
                    .build();
            implTaskMapper.insert(task);
            nameToId.put(def.getTaskName(), task.getId());
        }

        // 第二遍：回填 parentTaskId 与 taskPath
        for (TaskDef def : taskDefs) {
            if (def.getParentTaskName() == null || def.getParentTaskName().isBlank()) {
                // 顶层任务：taskPath = /<id>/
                Long topId = nameToId.get(def.getTaskName());
                if (topId != null) {
                    ImplTask top = implTaskMapper.selectById(topId);
                    if (top != null && (top.getTaskPath() == null || "/".equals(top.getTaskPath()))) {
                        top.setTaskPath("/" + topId + "/");
                        implTaskMapper.updateById(top);
                    }
                }
                continue;
            }
            Long parentId = nameToId.get(def.getParentTaskName());
            if (parentId == null) {
                log.warn("模板深拷贝：任务 {} 的父任务 {} 未找到，跳过父任务关联",
                        def.getTaskName(), def.getParentTaskName());
                continue;
            }
            Long childId = nameToId.get(def.getTaskName());
            ImplTask parent = implTaskMapper.selectById(parentId);
            ImplTask child = implTaskMapper.selectById(childId);
            if (parent == null || child == null) {
                continue;
            }
            String parentPath = parent.getTaskPath();
            if (parentPath == null || parentPath.isBlank() || "/".equals(parentPath)) {
                parentPath = "/" + parentId + "/";
            }
            child.setParentTaskId(parentId);
            child.setDepth((parent.getDepth() != null ? parent.getDepth() : 0) + 1);
            child.setTaskPath(parentPath + childId + "/");
            implTaskMapper.updateById(child);
        }

        log.info("模板深拷贝：批量创建任务成功 projectId={} phaseId={} count={}",
                projectId, phaseId, taskDefs.size());
    }
}
