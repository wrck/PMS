package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 闭环评估头实体 - 对应老系统 PmClEvaluationHeader (28个字段)
 * 对应表: pm_cl_evaluation_header
 */
@Data
@TableName("pm_cl_evaluation_header")
public class PmClEvaluationHeader extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("apply_header_id")
    private Long applyHeaderId;

    @TableField("project_id")
    private Long projectId;

    @TableField("project_code")
    private String projectCode;

    @TableField("project_name")
    private String projectName;

    @TableField("office_code")
    private String officeCode;

    @TableField("office_name")
    private String officeName;

    @TableField("project_customer")
    private String projectCustomer;

    @TableField("project_impl")
    private String projectImpl;

    @TableField("evaluation_type")
    private Integer evaluationType;

    @TableField("evaluation_result")
    private Integer evaluationResult;

    @TableField("evaluation_people_id")
    private String evaluationPeopleId;

    @TableField("evaluation_people_name")
    private String evaluationPeopleName;

    @TableField("evaluation_time")
    private LocalDateTime evaluationTime;

    @TableField("apply_person_id")
    private String applyPersonId;

    @TableField("apply_person_name")
    private String applyPersonName;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("next_accept_person")
    private String nextAcceptPerson;

    @TableField("next_accept_person_name")
    private String nextAcceptPersonName;

    @TableField("status")
    private Integer status;

    @TableField("quesnaire_id")
    private Long quesnaireId;

    @TableField("inst_id")
    private String instId;

    @TableField(exist = false)
    private String areapower;
}
