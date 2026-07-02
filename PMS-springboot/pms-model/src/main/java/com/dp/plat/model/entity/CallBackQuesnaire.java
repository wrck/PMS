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
 * CallBackQuesnaire entity - migrated from Struts
 */
@Data
@TableName("pm_callback_quesnaire")
public class CallBackQuesnaire extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("callbackId")
    private Long callbackId;

    @TableField("quesnaireTemplateId")
    private String quesnaireTemplateId;

    @TableField("quesnaireScore")
    private Integer quesnaireScore;

    @TableField("quesnaireResult")
    private String quesnaireResult;

    @TableField("quesnaireStatus")
    private String quesnaireStatus;

    @TableField("createdPerson")
    private String createdPerson;

    @TableField("createdTime")
    private LocalDateTime createdTime;

    @TableField("updatedPerson")
    private String updatedPerson;

    @TableField("updatedTime")
    private LocalDateTime updatedTime;

    @TableField("version")
    private String version;

    @TableField("evaluationHeaderId")
    private Integer evaluationHeaderId;

    @TableField("nextAcceptPerson")
    private String nextAcceptPerson;

}