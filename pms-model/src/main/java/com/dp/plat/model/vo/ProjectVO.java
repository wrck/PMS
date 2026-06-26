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
    private Integer projectState;
    private String projectStateName;
    private Integer executionState;
    private String executionStateName;
    private String smCode;
    private String smName;
    private String pmCode;
    private String pmName;
    private String salesManCode;
    private String salesManName;
    private String projectLevel;
    private Integer projectPlanState;
    private String planStateName;
    private Integer shipmentState;
    private String shipmentStateName;
    private LocalDateTime projectCreateTime;
    private LocalDateTime projectStartTime;
    private LocalDateTime projectRefreshTime;
    private String teamMemberCodes;
    private String teamMemberNames;

    /** 项目成员列表 */
    private List<PmsProjectMember> members;
}
