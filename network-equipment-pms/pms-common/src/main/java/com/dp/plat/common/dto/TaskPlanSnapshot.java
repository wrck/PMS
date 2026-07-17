package com.dp.plat.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 任务计划快照（基线快照的单个元素）。
 *
 * <p>保存基线时，对项目下每个任务的计划字段做深拷贝，序列化为
 * {@code List<TaskPlanSnapshot>} 存入 {@code pms_baseline_snapshot.snapshot_json}。
 * 偏差分析时与任务当前计划字段逐项对比。</p>
 *
 * <p>日期字段使用 {@code String}（ISO 格式 yyyy-MM-dd），避免 MyBatis-Plus
 * JSON TypeHandler 默认 ObjectMapper 缺少 JavaTimeModule 的序列化问题，
 * 同时与 API 响应中的日期字符串格式保持一致。</p>
 *
 * <p>关联设计文档：§2.2 BaselineSnapshot、§5.5 Story 4 验收 2。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskPlanSnapshot implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID。 */
    private Long taskId;

    /** 任务名称（冗余，便于历史追溯）。 */
    private String taskName;

    /** 计划开始日期（ISO yyyy-MM-dd）。 */
    private String plannedStart;

    /** 计划结束日期（ISO yyyy-MM-dd）。 */
    private String plannedEnd;

    /** 计划工期（天）。 */
    private Integer duration;

    /** 计划工时（小时）。 */
    private Integer plannedHours;

    /** 任务类型（OEM/AGENT，冗余）。 */
    private String taskType;
}
