package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 任务检查项实体。强制检查项（mandatory=true）在提交评审前必须勾选。
 * 关联设计文档：§2.2 TaskChecklist（行 148-158）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_task_checklist")
public class TaskChecklist extends BaseEntity {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotBlank(message = "检查项标题不能为空")
    @Size(max = 128, message = "检查项标题长度不能超过 128 个字符")
    private String title;

    @Size(max = 500, message = "检查项描述长度不能超过 500 个字符")
    private String description;

    /** 是否强制检查项（提交评审前必须勾选）。 */
    @Builder.Default
    private Boolean mandatory = false;

    /** 是否已勾选。 */
    @Builder.Default
    private Boolean checked = false;

    /** 勾选人ID。 */
    private Long checkedBy;

    /** 勾选时间。 */
    private LocalDateTime checkedAt;

    /** 排序序号。 */
    @Builder.Default
    private Integer sortOrder = 0;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）。 */
    @Version
    private Integer version;
}
