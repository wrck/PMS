package com.dp.plat.implementation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * Implementation task entity (OEM or agent).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_impl_task")
public class ImplTask extends BaseEntity {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    private Long milestoneId;

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称长度不能超过 200 个字符")
    private String taskName;

    /** OEM=原厂实施, AGENT=代理商实施. */
    @NotBlank(message = "任务类型不能为空")
    @Size(max = 20, message = "任务类型长度不能超过 20 个字符")
    private String taskType;

    /** Agent id (for AGENT type). */
    private Long agentId;

    /** OEM engineer user id (for OEM type). */
    private Long engineerId;

    @Size(max = 50, message = "工程师名称长度不能超过 50 个字符")
    private String engineerName;

    private LocalDate planStartDate;

    private LocalDate planEndDate;

    private LocalDate actualStartDate;

    private LocalDate actualEndDate;

    /** PENDING, ACCEPTED, IN_PROGRESS, COMPLETED, CONFIRMED, REJECTED. */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    /** Progress percent 0-100. */
    @Min(value = 0, message = "进度不能小于 0")
    @Max(value = 100, message = "进度不能大于 100")
    private Integer progress;

    @Size(max = 2000, message = "工作描述长度不能超过 2000 个字符")
    private String workDescription;

    private String acceptOpinion;

    private Long acceptUserId;

    @Size(max = 50, message = "验收人名称长度不能超过 50 个字符")
    private String acceptUserName;

    private LocalDateTime acceptTime;

    /** Service execution fields. */
    @Size(max = 100, message = "客户联系人长度不能超过 100 个字符")
    private String customerContact;

    @Size(max = 500, message = "服务地址长度不能超过 500 个字符")
    private String serviceAddress;

    /** SITE_SURVEY/INSTALL/DEBUG/MAINTENANCE. */
    @Size(max = 50, message = "服务类型长度不能超过 50 个字符")
    private String serviceType;

    /** Standard operating procedure steps (long text). */
    private String sopSteps;

    /** Required materials list (long text). */
    private String materialList;

    private Integer plannedHours;

    /** Required skill level: JUNIOR/SENIOR/EXPERT. */
    @Size(max = 20, message = "技能等级长度不能超过 20 个字符")
    private String skillLevel;

    /** Safety requirements: PPE/LOTO/PERMIT. */
    private String safetyPpe;

    /** Evidence checkpoint definition (long text). */
    private String evidenceCheckpoints;

    /** Whether a formal sign-off is required (default true). */
    @Builder.Default
    private Boolean signOffRequired = true;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）. */
    @Version
    private Integer version;
}
