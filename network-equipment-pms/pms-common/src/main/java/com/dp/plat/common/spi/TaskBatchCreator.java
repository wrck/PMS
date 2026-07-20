package com.dp.plat.common.spi;

import com.dp.plat.common.dto.TemplateSnapshot.TaskDef;

import java.util.List;

/**
 * 任务批量创建 SPI（TD-P8-003 模板深拷贝用）。
 *
 * <p>从模板创建项目时，{@code pms-project} 通过本 SPI 跨模块调用 {@code pms-implementation}
 * 批量插入任务记录到 {@code pms_impl_task} 表，避免 {@code pms-project} 直接依赖
 * {@code pms-implementation}。</p>
 *
 * <p>由 {@code pms-implementation} 模块实现并注册为 Spring Bean，
 * {@code pms-project} 通过 {@code @Autowired(required=false)} 注入。
 * 若模块未加载，跳过任务深拷贝并 log.warn。</p>
 */
public interface TaskBatchCreator {

    /**
     * 批量创建任务。
     *
     * <p>实现需处理 {@link TaskDef#getParentTaskName()} 的解析：先创建全部任务构建
     * name → id 映射，再回填 parentTaskId。{@link TaskDef#getPhaseCode()} 已由调用方
     * 解析为 phaseId 参数，实现直接使用即可。</p>
     *
     * @param projectId 项目ID
     * @param phaseId   阶段ID（任务关联到此阶段）
     * @param taskDefs  任务定义列表（已按 phaseCode 分组）
     */
    void batchCreateTasks(Long projectId, Long phaseId, List<TaskDef> taskDefs);
}
