package com.dp.plat.common.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dp.plat.common.dto.DeliverableContentBlock;
import com.dp.plat.common.dto.PhaseCriteria;
import com.dp.plat.common.dto.PhaseExitGate;
import com.dp.plat.common.dto.TaskPlanSnapshot;
import com.dp.plat.common.dto.TemplateSnapshot;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * JSON TypeHandler 子类集合（每个具体泛型类型一个子类，避免泛型擦除）
 * 关联设计文档：§6.11
 *
 * <p>使用方式（实体类）：
 * <pre>
 * {@literal @}TableName(value = "pms_project_template_version", autoResultMap = true)
 * public class ProjectTemplateVersion extends BaseDO {
 *     {@literal @}TableField(typeHandler = JsonTypeHandlers.TemplateSnapshotHandler.class)
 *     private TemplateSnapshot snapshotJson;
 * }
 * </pre>
 *
 * <p>注意：autoResultMap = true 必须开启，否则字段级 typeHandler 在 BaseMapper 方法中不生效。
 *
 * <p>构造函数说明：
 * <ul>
 *   <li>{@code (Class<?> type)} — MP 3.5.10+ 走此构造函数（newJsonTypeHandler 反射查找）</li>
 *   <li>{@code (Class<?> type, Field field)} — MP 3.5.10+ 优先走此构造函数（含字段元信息，泛型擦除场景下更优）</li>
 *   <li>no-arg — MP 3.5.9 及更早版本走此构造函数（向后兼容）</li>
 * </ul>
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

        public PhaseCriteriaHandler(Class<?> type) {
            super(PhaseCriteria.class);
        }

        public PhaseCriteriaHandler(Class<?> type, Field field) {
            super(PhaseCriteria.class, field);
        }
    }

    /**
     * PhaseExitGate（阶段退出条件，4 类结构化条件）TypeHandler
     */
    public static class PhaseExitGateHandler extends JacksonTypeHandler {
        public PhaseExitGateHandler() {
            super(PhaseExitGate.class);
        }

        public PhaseExitGateHandler(Class<?> type) {
            super(PhaseExitGate.class);
        }

        public PhaseExitGateHandler(Class<?> type, Field field) {
            super(PhaseExitGate.class, field);
        }
    }

    /**
     * TaskPlanSnapshot List TypeHandler
     * （BaselineSnapshot.snapshotJson 用，pms-baseline 模块 BaselineSnapshot 实体使用）
     *
     * <p>重写 parse 以 {@link TypeReference}#{@code List<TaskPlanSnapshot>} 反序列化，
     * 解决泛型擦除导致的元素退化为 LinkedHashMap 问题。使用自包含 ObjectMapper，
     * 不依赖父类 getObjectMapper 的可见性差异。</p>
     */
    public static class TaskPlanSnapshotListHandler extends JacksonTypeHandler {
        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final TypeReference<List<TaskPlanSnapshot>> TYPE_REF = new TypeReference<>() {
        };

        public TaskPlanSnapshotListHandler() {
            super(List.class);
        }

        public TaskPlanSnapshotListHandler(Class<?> type) {
            super(List.class);
        }

        public TaskPlanSnapshotListHandler(Class<?> type, Field field) {
            super(List.class, field);
        }

        @Override
        public Object parse(String json) {
            try {
                return MAPPER.readValue(json, TYPE_REF);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * TemplateSnapshot（模板内容快照）TypeHandler
     */
    public static class TemplateSnapshotHandler extends JacksonTypeHandler {
        public TemplateSnapshotHandler() {
            super(TemplateSnapshot.class);
        }

        public TemplateSnapshotHandler(Class<?> type) {
            super(TemplateSnapshot.class);
        }

        public TemplateSnapshotHandler(Class<?> type, Field field) {
            super(TemplateSnapshot.class, field);
        }
    }

    /**
     * DeliverableContentBlock List TypeHandler
     * （pms_deliverable.content_blocks 与 pms_deliverable_type_template.default_blocks 共用）
     *
     * <p>重写 parse 以 {@link TypeReference}#{@code List<DeliverableContentBlock>} 反序列化，
     * 解决泛型擦除导致的元素退化为 LinkedHashMap 问题。使用自包含 ObjectMapper，
     * 不依赖父类 getObjectMapper 的可见性差异。</p>
     */
    public static class DeliverableContentBlockListHandler extends JacksonTypeHandler {
        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final TypeReference<List<DeliverableContentBlock>> TYPE_REF = new TypeReference<>() {
        };

        public DeliverableContentBlockListHandler() {
            super(List.class);
        }

        public DeliverableContentBlockListHandler(Class<?> type) {
            super(List.class);
        }

        public DeliverableContentBlockListHandler(Class<?> type, Field field) {
            super(List.class, field);
        }

        @Override
        public Object parse(String json) {
            try {
                return MAPPER.readValue(json, TYPE_REF);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
