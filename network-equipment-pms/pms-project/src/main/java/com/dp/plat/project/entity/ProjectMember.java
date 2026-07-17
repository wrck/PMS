package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 项目成员
 * 关联表：pms_project_member
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pms_project_member")
public class ProjectMember extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;
    private Long userId;
    private String userName;

    /** PROJECT_MANAGER / PROJECT_MEMBER / APPROVER / VIEWER / CUSTOMER */
    private String role;

    private LocalDate joinDate;
    private LocalDate leaveDate;
}
