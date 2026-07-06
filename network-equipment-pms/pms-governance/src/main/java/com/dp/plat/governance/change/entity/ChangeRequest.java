package com.dp.plat.governance.change.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Change request entity (governance three-books: change request book).
 *
 * <p>Tracks formal change requests raised against a project, including impact
 * assessment, CCB (Change Control Board) review outcome and baseline updates.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_change_request")
public class ChangeRequest extends BaseEntity {

    /** Change request number (CR-YYYY-XXXX). */
    @NotBlank(message = "变更单号不能为空")
    @Size(max = 50, message = "变更单号长度不能超过 50 个字符")
    private String crNo;

    /** Project id. */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** Project name (denormalized for display). */
    @Size(max = 200, message = "项目名称长度不能超过 200 个字符")
    private String projectName;

    /** Change title. */
    @NotBlank(message = "变更标题不能为空")
    @Size(max = 200, message = "变更标题长度不能超过 200 个字符")
    private String title;

    /** Change description. */
    @NotBlank(message = "变更描述不能为空")
    @Size(max = 2000, message = "变更描述长度不能超过 2000 个字符")
    private String description;

    /** Requester user id. */
    private Long requesterId;

    /** Requester user name. */
    @Size(max = 50, message = "请求人名称长度不能超过 50 个字符")
    private String requesterName;

    /** Request date. */
    private LocalDate requestDate;

    /** Impact scope (TEXT). */
    @Size(max = 2000, message = "影响范围长度不能超过 2000 个字符")
    private String impactScope;

    /** Impact on schedule. */
    @Size(max = 500, message = "进度影响长度不能超过 500 个字符")
    private String impactSchedule;

    /** Impact on cost. */
    @Size(max = 500, message = "成本影响长度不能超过 500 个字符")
    private String impactCost;

    /** Impact on quality. */
    @Size(max = 500, message = "质量影响长度不能超过 500 个字符")
    private String impactQuality;

    /** Priority (LOW, MEDIUM, HIGH, CRITICAL). */
    @NotBlank(message = "优先级不能为空")
    @Size(max = 20, message = "优先级长度不能超过 20 个字符")
    private String priority;

    /** Status (SUBMITTED, UNDER_REVIEW, CCB_APPROVED, CCB_REJECTED, IMPLEMENTING, CLOSED). */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    /** Approver user id. */
    private Long approverId;

    /** Approver user name. */
    @Size(max = 50, message = "审批人名称长度不能超过 50 个字符")
    private String approverName;

    /** Workflow process instance id. */
    private String processInstanceId;

    /** Whether the project baseline has been updated (default false). */
    @Builder.Default
    private Boolean baselineUpdated = false;

    /** Approval time. */
    private LocalDateTime approvedAt;

    /** Closure time. */
    private LocalDateTime closedAt;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）. */
    @Version
    private Integer version;
}
