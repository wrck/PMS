package com.dp.plat.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工作流流程描述 - 对应老系统 DpActProcDesc (28字段)
 * 用于工作台待办任务展示
 * 非数据库表实体,用于数据传输
 */
@Data
public class DpActProcDesc {
    private Long id;
    private String projectCode;
    private String projectName;
    private String projectPlanStateName;
    private String officeName;
    private String name;
    private Long applyNum;
    private int planDiffTime;
    private String assigneeName;
    private String taskId;
    private String username;
    private String realName;
    private LocalDateTime createTime;
    private LocalDateTime endTime;
    private String procTypeDesc;
    private String procTypeName;
    private String projectCustomer;
    private String projectImpl;
    private int evaluaResult;
    private String KEY_;
    private String instId;
    private String procdefKey;
    private String assignee;
    private String assigneeTime;
    private String result;
}
