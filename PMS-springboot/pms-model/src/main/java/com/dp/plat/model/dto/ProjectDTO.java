package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 项目创建/更新 DTO
 */
@Data
public class ProjectDTO {
    private Long id;
    private String projectCode;
    private String projectName;
    private String contractNo;
    private Long companyId;
    private String officeCode;
    private String projectType;
    private String projectState;
    private String executionState;
    private String smCode;
    private String pmCode;
    private String salesManCode;
    private String salesManName;
    private String projectLevel;
    private String majorProjectLevel;
    private String partnerChannel;
    private String serviceChannel;
    private String agentChannel;
    private String salesType;
    private String customerProjectName;
}
