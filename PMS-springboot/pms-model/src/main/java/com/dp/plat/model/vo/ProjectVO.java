package com.dp.plat.model.vo;

import com.dp.plat.model.entity.PmsProjectMember;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目详情 VO
 */
@Data
public class ProjectVO {
    private Long id;
    private String projectCode;
    private String projectName;
    private String contractNo;
    private Long companyId;
    private String companyName;
    private String officeCode;
    private String officeName;
    private String projectType;
    private String projectState;
    private String projectStateName;
    private String executionState;
    private String executionStateName;
    private String isback;
    private String smCode;
    private String smName;
    private String pmCode;
    private String pmName;
    private String pmCodeB;
    private String pmNameB;
    private String salesManCode;
    private String salesManName;
    private String projectLevel;
    private String majorProjectLevel;
    private String projectPlanState;
    private String planStateName;
    private Integer shipmentState;
    private String shipmentStateName;
    private String salesType;
    private String customerProjectName;
    private String serviceType;
    private String projectCategory;
    private String projectClassify;
    private String finalCustomerName;
    private String backCause;
    private LocalDateTime projectCreateTime;
    private LocalDateTime projectStartTime;
    private LocalDateTime projectRefreshTime;
    private LocalDateTime projectCloseTime;
    private String teamMemberCodes;
    private String teamMemberNames;
    private String partnerChannel;
    private String serviceChannel;
    private String agentChannel;

    /** 项目成员列表 */
    private List<PmsProjectMember> members;
}
