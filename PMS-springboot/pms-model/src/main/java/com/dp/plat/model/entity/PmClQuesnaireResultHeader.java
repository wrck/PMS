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

    // ===== 评分相关字段（迁移自老系统 PmClQuesnaireResultHeader） =====

    /** 问卷得分（计算后写入） */
    @TableField("ques_mark_score")
    private Double quesMarkScore;

    /** 问卷达标分数 */
    @TableField("ques_pass_score")
    private Double quesPassScore;

    /** 问卷答案字符串（格式：题目类型:题号-答案,题号-答案;） */
    @TableField("ques_anw")
    private String quesAnw;

    /** 问卷评分结果: 1=通过, -1=驳回 */
    @TableField("ques_mark_result")
    private Integer quesMarkResult;
}
