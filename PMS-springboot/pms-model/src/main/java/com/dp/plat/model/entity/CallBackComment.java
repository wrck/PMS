package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CallBackComment entity - migrated from Struts
 */
@Data
@TableName("pm_callback_comment")
public class CallBackComment extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("callbackId")
    private Long callbackId;

    @TableField("commentPerson")
    private String commentPerson;

    @TableField("commentContent")
    private String commentContent;

    @TableField("commentTime")
    private LocalDateTime commentTime;

    @TableField("commentType")
    private String commentType;

}