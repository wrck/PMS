package com.dp.plat.project.punchlist.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Punch list defect entity.
 *
 * <p>Records defects/issues discovered during the walkdown phase of a project milestone.
 * Safety-severity items block the related milestone until resolved and verified.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_punch_list")
public class PunchList extends BaseEntity {

    /** Project id. */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** Milestone id the punch list item is associated with. */
    private Long milestoneId;

    /** Severity (SAFETY, FUNCTIONAL, COSMETIC). */
    @NotBlank(message = "严重等级不能为空")
    @Size(max = 20, message = "严重等级长度不能超过 20 个字符")
    private String severity;

    /** Defect title. */
    @NotBlank(message = "缺陷标题不能为空")
    @Size(max = 200, message = "缺陷标题长度不能超过 200 个字符")
    private String title;

    /** Defect description. */
    @Size(max = 2000, message = "缺陷描述长度不能超过 2000 个字符")
    private String description;

    /** Walkdown stage (PRE_PUNCH, FORMAL). */
    @Size(max = 20, message = "走查阶段长度不能超过 20 个字符")
    private String walkdownStage;

    /** Assignee user id. */
    private Long assigneeId;

    /** Assignee user name. */
    @Size(max = 50, message = "处理人名称长度不能超过 50 个字符")
    private String assigneeName;

    /** Deadline for resolution. */
    private LocalDate deadline;

    /** Status (OPEN, RESOLVED, VERIFIED). */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    /** Resolution time. */
    private LocalDateTime resolvedAt;

    /** Verification time. */
    private LocalDateTime verifiedAt;

    /** Verifier user id. */
    private Long verifiedBy;

    /** Verifier user name. */
    @Size(max = 50, message = "验证人名称长度不能超过 50 个字符")
    private String verifiedByName;

    /** Comma-separated attachment ids (reserved for future attachment integration). */
    private String attachmentIds;
}
