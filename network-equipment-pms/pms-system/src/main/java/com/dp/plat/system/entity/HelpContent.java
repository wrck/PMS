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

/**
 * Help content entity for the user guide / help center.
 *
 * <p>Stores markdown-formatted help documents grouped by category
 * (quick start / FAQ / video / advanced), ordered by {@link #sortOrder}
 * and toggleable via {@link #status}.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_help_content")
public class HelpContent extends BaseEntity {

    /** 帮助内容分类：QUICK_START / FAQ / VIDEO / ADVANCED */
    @NotBlank(message = "分类不能为空")
    @Pattern(regexp = "^(QUICK_START|FAQ|VIDEO|ADVANCED)$",
            message = "分类只能为 QUICK_START / FAQ / VIDEO / ADVANCED")
    private String category;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过 200 个字符")
    private String title;

    /** 富文本内容（Markdown） */
    @NotBlank(message = "内容不能为空")
    private String content;

    /** 排序值，升序展示 */
    private Integer sortOrder;

    /** 状态：0=启用，1=禁用 */
    @Pattern(regexp = "^[01]$", message = "状态只能为 0(启用) 或 1(禁用)")
    private String status;

    /** 浏览次数 */
    private Integer viewCount;
}
