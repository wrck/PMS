package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/** 问卷选项 - 对应老系统 PmClosedLoopQuesnaireOpt (12字段) */
@Data
@TableName("pm_cl_quesnaire_option")
public class PmClosedLoopQuesnaireOpt extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("line_id") private Long lineId;
    @TableField("option_no") private Integer optionNo;
    @TableField("option_content") private String optionContent;
    @TableField("score") private Integer score;
    @TableField("is_default") private Integer isDefault;
    @TableField("sort_order") private Integer sortOrder;
}
