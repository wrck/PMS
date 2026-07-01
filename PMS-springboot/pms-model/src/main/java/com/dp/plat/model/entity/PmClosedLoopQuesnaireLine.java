package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/** 问卷模板行 - 对应老系统 PmClosedLoopQuesnaireLine (15字段) */
@Data
@TableName("pm_cl_quesnaire_line")
public class PmClosedLoopQuesnaireLine extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("header_id") private Long headerId;
    @TableField("line_no") private Integer lineNo;
    @TableField("line_content") private String lineContent;
    @TableField("line_type") private String lineType;
    @TableField("max_score") private Integer maxScore;
    @TableField("is_required") private Integer isRequired;
    @TableField("sort_order") private Integer sortOrder;
}
