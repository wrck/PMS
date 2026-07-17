package com.dp.plat.project.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import com.dp.plat.common.handler.JsonTypeHandlers;
import com.dp.plat.common.dto.PhaseCriteria;
import com.dp.plat.common.dto.PhaseExitGate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 项目阶段
 * 关联表：pms_project_phase
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "pms_project_phase", autoResultMap = true)
public class ProjectPhase extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long projectId;

    /** 来源模板阶段 ID（追溯用） */
    private Long templatePhaseId;

    private String phaseName;

    /** PREPARE / PLAN / DESIGN / IMPLEMENT / OPERATE 或自定义 */
    private String phaseCode;

    private Integer sortOrder;

    /** 进入条件（JSON） */
    @TableField(typeHandler = JsonTypeHandlers.PhaseCriteriaHandler.class)
    private PhaseCriteria entryCriteria;

    /** 退出条件（JSON，4 类条件） */
    @TableField(typeHandler = JsonTypeHandlers.PhaseExitGateHandler.class)
    private PhaseExitGate exitCriteria;

    /** NOT_STARTED / IN_PROGRESS / COMPLETED / SKIPPED */
    private String status;

    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
}
