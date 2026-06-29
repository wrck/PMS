package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 售前审批意见 - 对应 pm_presales_comment 表
 */
@Data
@TableName("pm_presales_comment")
public class PmsPresalesComment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 售前项目ID */
    @TableField("presalesId")
    private Long presalesId;

    /** 审批人 */
    @TableField("commentBy")
    private String commentBy;

    /** 审批意见 */
    @TableField("comment")
    private String comment;

    /** 审批结果 (1=通过 2=驳回) */
    @TableField("result")
    private Integer result;

    /** 审批时间 */
    @TableField("commentTime")
    private LocalDateTime commentTime;
}
