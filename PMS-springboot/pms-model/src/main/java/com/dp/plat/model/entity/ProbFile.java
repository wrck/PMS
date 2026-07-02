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
 * ProbFile entity - migrated from Struts
 */
@Data
@TableName("pm_prob_file")
public class ProbFile extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("probId")
    private Long probId;

    @TableField("fileName")
    private String fileName;

    @TableField("filePath")
    private String filePath;

    @TableField("fileSize")
    private Long fileSize;

    @TableField("uploadPerson")
    private String uploadPerson;

    @TableField("uploadTime")
    private LocalDateTime uploadTime;

}