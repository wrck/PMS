package com.dp.plat.baseline.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.dp.plat.common.dto.TaskPlanSnapshot;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 计划基线快照实体。
 *
 * <p>单一活跃基线：项目同时只有一条 {@code APPROVED} 状态基线；新建基线时
 * 将前一条置为 {@code SUPERSEDED}。{@code snapshotJson} 以
 * {@code List<TaskPlanSnapshot>} 序列化存储全部任务计划快照。</p>
 *
 * <p>{@code autoResultMap = true} 必须开启，否则字段级 typeHandler 在
 * BaseMapper 方法中不生效（参见 {@link JacksonTypeHandler} 文档）。</p>
 *
 * <p>关联设计文档：§2.2 BaselineSnapshot（行 170-182）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_baseline_snapshot", autoResultMap = true)
public class BaselineSnapshot extends BaseEntity {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "基线名称不能为空")
    @Size(max = 128, message = "基线名称长度不能超过 128 个字符")
    private String baselineName;

    /** DRAFT / APPROVED / SUPERSEDED。 */
    @Builder.Default
    private String status = "DRAFT";

    /** 全部任务计划快照（JSON 列）。 */
    @TableField(typeHandler = JsonTypeHandlers.TaskPlanSnapshotListHandler.class)
    private List<TaskPlanSnapshot> snapshotJson;

    /** 变更原因（关联审批）。 */
    @Size(max = 500, message = "变更原因长度不能超过 500 个字符")
    private String changeReason;

    /** 关联审批记录ID。 */
    private Long approvalRecordId;

    /** 审批时间。 */
    private LocalDateTime approvedAt;

    /** 审批人ID。 */
    private Long approvedBy;

    /** 乐观锁版本号（MyBatis-Plus @Version）。 */
    @Version
    private Integer version;
}
