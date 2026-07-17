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
 * 任务评论实体，支持二级回复（parent_comment_id）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_task_comment")
public class TaskComment extends BaseEntity {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "评论人ID不能为空")
    private Long userId;

    @Size(max = 64, message = "评论人姓名长度不能超过 64 个字符")
    private String userName;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 父评论ID（NULL=顶级评论，非 NULL=二级回复）。 */
    private Long parentCommentId;

    /** 乐观锁版本号。 */
    @Version
    private Integer version;
}
