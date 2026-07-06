package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Project main entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project")
public class Project extends BaseEntity {

    /** Project code (PMS-YYYY-XXXX). */
    @NotBlank(message = "项目编号不能为空")
    @Size(max = 50, message = "项目编号长度不能超过 50 个字符")
    private String projectCode;

    /** Project name. */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 200, message = "项目名称长度不能超过 200 个字符")
    private String projectName;

    /** Project type (NETWORK_DEVICE, SECURITY, DATACENTER, etc.). */
    @NotBlank(message = "项目类型不能为空")
    @Size(max = 50, message = "项目类型长度不能超过 50 个字符")
    private String projectType;

    /** Status (PENDING, APPROVED, IN_PROGRESS, INITIAL_ACCEPTANCE, FINAL_ACCEPTANCE, COMPLETED, CLOSED, REJECTED). */
    @Size(max = 50, message = "状态长度不能超过 50 个字符")
    private String status;

    /** Customer name. */
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过 200 个字符")
    private String customerName;

    /** Customer contact. */
    @Size(max = 100, message = "客户联系人长度不能超过 100 个字符")
    private String customerContact;

    /** Customer phone. */
    @Size(max = 50, message = "客户电话长度不能超过 50 个字符")
    private String customerPhone;

    /** Contract number. */
    @Size(max = 100, message = "合同编号长度不能超过 100 个字符")
    private String contractNo;

    /** Contract amount. */
    @DecimalMin(value = "0", message = "合同金额不能为负数")
    private BigDecimal contractAmount;

    /** Planned start date. */
    private LocalDate planStartDate;

    /** Planned end date. */
    private LocalDate planEndDate;

    /** Actual start date. */
    private LocalDate actualStartDate;

    /** Actual end date. */
    private LocalDate actualEndDate;

    /** Project manager user id. */
    private Long projectManagerId;

    /** Project manager name. */
    @Size(max = 50, message = "项目经理名称长度不能超过 50 个字符")
    private String projectManagerName;

    /** Project description. */
    @Size(max = 2000, message = "项目描述长度不能超过 2000 个字符")
    private String description;

    /** Progress percentage 0-100. */
    @Min(value = 0, message = "进度不能小于 0")
    @Max(value = 100, message = "进度不能大于 100")
    private Integer progress;

    /** Priority (HIGH, NORMAL, LOW). */
    @Size(max = 20, message = "优先级长度不能超过 20 个字符")
    private String priority;

    /** Workflow process instance id for the project approval flow. */
    private String processInstanceId;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）. */
    @Version
    private Integer version;
}
