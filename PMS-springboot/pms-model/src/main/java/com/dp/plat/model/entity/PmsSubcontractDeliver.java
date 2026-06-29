package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分包交付件 - 对应 pm_subcontract_deliver 表
 */
@Data
@TableName("pm_subcontract_deliver")
public class PmsSubcontractDeliver extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 分包项目ID */
    @TableField("subcontractId")
    private Long subcontractId;

    /** 付款ID */
    @TableField("paymentId")
    private Long paymentId;

    /** 文件名 */
    @TableField("fileName")
    private String fileName;

    /** 文件路径 */
    @TableField("filePath")
    private String filePath;

    /** 类型 */
    @TableField("type")
    private String type;

    /** 上传人 */
    @TableField("uploadBy")
    private String uploadBy;

    /** 上传时间 */
    @TableField("uploadTime")
    private LocalDateTime uploadTime;
}
