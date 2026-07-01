package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 问卷结果头实体 - 对应老系统 PmClQuesnaireResultHeader (17个字段)
 * 对应表: pm_cl_quesnaire_result_header
 */
@Data
@TableName("pm_cl_quesnaire_result_header")
public class PmClQuesnaireResultHeader extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("quesnaire_template_header_id")
    private Long quesnaireTemplateHeaderId;

    @TableField("project_id")
    private Long projectId;

    @TableField("project_code")
    private String projectCode;

    @TableField("apply_header_id")
    private Long applyHeaderId;

    @TableField("fill_people_id")
    private String fillPeopleId;

    @TableField("fill_people_name")
    private String fillPeopleName;

    @TableField("fill_time")
    private java.time.LocalDateTime fillTime;

    @TableField("total_score")
    private Integer totalScore;

    @TableField("status")
    private Integer status;
}
