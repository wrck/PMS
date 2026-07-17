package com.dp.plat.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dp.plat.common.dto.PhaseCriteria;
import com.dp.plat.common.dto.PhaseExitGate;
import com.dp.plat.common.dto.TemplateSnapshot;

import java.util.List;

/**
 * JSON TypeHandler 子类集合（每个具体泛型类型一个子类，避免泛型擦除）
 * 关联设计文档：§6.11
 *
 * <p>使用方式（实体类）：
 * <pre>
 * {@literal @}TableName(value = "pms_project_template_version", autoResultMap = true)
 * public class ProjectTemplateVersion extends BaseEntity {
 *     {@literal @}TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
 *     private TemplateSnapshot snapshotJson;
 * }
 * </pre>
 *
 * <p>注意：autoResultMap = true 必须开启，否则字段级 typeHandler 在 BaseMapper 方法中不生效。
 */
public final class JsonTypeHandlers {

    private JsonTypeHandlers() {
    }

    /**
     * PhaseCriteria（阶段进入条件）TypeHandler
     */
    public static class PhaseCriteriaHandler extends JacksonTypeHandler {
        public PhaseCriteriaHandler() {
            super(PhaseCriteria.class);
        }
    }

    /**
     * PhaseExitGate（阶段退出条件，4 类结构化条件）TypeHandler
     */
    public static class PhaseExitGateHandler extends JacksonTypeHandler {
        public PhaseExitGateHandler() {
            super(PhaseExitGate.class);
        }
    }

    /**
     * TaskPlanSnapshot List TypeHandler
     * （BaselineSnapshot.snapshotJson 用，本任务先创建占位，Task 11 BaselineSnapshot 实体使用）
     */
    public static class TaskPlanSnapshotListHandler extends JacksonTypeHandler {
        public TaskPlanSnapshotListHandler() {
            super(List.class);
        }
    }

    /**
     * TemplateSnapshot（模板内容快照）TypeHandler
     */
    public static class TemplateSnapshotHandler extends JacksonTypeHandler {
        public TemplateSnapshotHandler() {
            super(TemplateSnapshot.class);
        }
    }
}
