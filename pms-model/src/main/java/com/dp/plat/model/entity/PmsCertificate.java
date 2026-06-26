package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_certificate")
public class PmsCertificate extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("barcode")
    private String barcode;

    @TableField("oqcNo")
    private String oqcNo;

    @TableField("productionDate")
    private String productionDate;

    @TableField("sealName")
    private String sealName;

    @TableField("sealCode")
    private String sealCode;

    @TableField("sealFilePath")
    private String sealFilePath;

    @TableField("createBy")
    private String createBy;

    @TableField("createTime")
    private LocalDateTime createTime;
}
