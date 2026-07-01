package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 问卷模板头实体 - 对应老系统 PmClosedLoopQuesnaire (18个字段)
 * 对应表: pm_cl_quesnaire_header
 */
@Data
@TableName("pm_cl_quesnaire_header")
public class PmClosedLoopQuesnaire extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("quesnaire_name")
    private String quesnaireName;

    @TableField("quesnaire_desc")
    private String quesnaireDesc;

    @TableField("process_id")
    private String processId;

    @TableField("status")
    private Integer status;

    @TableField("effective_time")
    private java.time.LocalDateTime effectiveTime;

    @TableField("expire_time")
    private java.time.LocalDateTime expireTime;

    @TableField("total_score")
    private Integer totalScore;

    /** 评分规则索引（逗号分隔，如"0,1,2"对应PmClosedLoopMarkFactory中的规则） */
    @TableField("mark_indexs")
    private String markIndexs;
}
