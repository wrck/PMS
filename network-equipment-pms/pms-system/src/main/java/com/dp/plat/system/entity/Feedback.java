package com.dp.plat.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * User feedback / support ticket entity.
 *
 * <p>Stores bug reports, suggestions, questions and other feedback submitted by
 * authenticated users. Administrators can reply to or close the ticket; the
 * lifecycle is tracked via {@link #status} (PENDING → PROCESSING → RESOLVED →
 * CLOSED).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_feedback")
public class Feedback extends BaseEntity {

    /** 提交人用户 ID（冗余字段，便于按用户筛选） */
    private Long userId;

    /** 提交人用户名（冗余字段，便于展示） */
    @Size(max = 50, message = "用户名长度不能超过 50 个字符")
    private String username;

    /** 反馈分类：BUG / SUGGESTION / QUESTION / OTHER */
    @NotBlank(message = "分类不能为空")
    @Pattern(regexp = "^(BUG|SUGGESTION|QUESTION|OTHER)$",
            message = "分类只能为 BUG / SUGGESTION / QUESTION / OTHER")
    private String category;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过 200 个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(max = 4000, message = "内容长度不能超过 4000 个字符")
    private String content;

    /** 联系方式（选填，电话 / 邮箱） */
    @Size(max = 100, message = "联系方式长度不能超过 100 个字符")
    private String contact;

    /** 状态：PENDING / PROCESSING / RESOLVED / CLOSED */
    @Pattern(regexp = "^(PENDING|PROCESSING|RESOLVED|CLOSED)$",
            message = "状态只能为 PENDING / PROCESSING / RESOLVED / CLOSED")
    private String status;

    /** 管理员回复内容 */
    @Size(max = 4000, message = "回复内容长度不能超过 4000 个字符")
    private String reply;

    /** 回复人用户名 */
    @Size(max = 50, message = "回复人长度不能超过 50 个字符")
    private String replyBy;

    /** 回复时间 */
    private LocalDateTime replyAt;
}
