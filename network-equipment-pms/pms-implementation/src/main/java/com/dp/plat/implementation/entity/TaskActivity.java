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

/**
 * 任务活动记录实体（追加型），记录任务全生命周期事件。
 * 活动类型：CREATE/UPDATE/STATUS_CHANGE/SUBMIT_REVIEW/APPROVE/REJECT/
 * CHECKLIST_CHECK/COMMENT/PROGRESS_CHANGE/ASSIGN/MOVE。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_task_activity")
public class TaskActivity extends BaseEntity {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "操作人ID不能为空")
    private Long userId;

    @Size(max = 64, message = "操作人姓名长度不能超过 64 个字符")
    private String userName;

    /** 活动类型。 */
    @NotBlank(message = "活动类型不能为空")
    @Size(max = 50, message = "活动类型长度不能超过 50 个字符")
    private String activityType;

    /** 活动描述。 */
    private String content;

    /** 附加元数据（JSON 字符串，如 old_value/new_value）。 */
    private String metadata;

    /** 乐观锁版本号。 */
    @Version
    private Integer version;
}
