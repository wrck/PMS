package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/** 问卷结果行 - 对应老系统 PmClQuesnaireResultLine (14字段) */
@Data
@TableName("pm_cl_quesnaire_result_line")
public class PmClQuesnaireResultLine extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("result_header_id") private Long resultHeaderId;
    @TableField("line_id") private Long lineId;
    @TableField("line_content") private String lineContent;
    @TableField("option_id") private Long optionId;
    @TableField("option_content") private String optionContent;
    @TableField("score") private Integer score;
    @TableField("remark") private String remark;
}
