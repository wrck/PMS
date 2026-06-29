package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("pm_subcontract_project")
public class PmsSubcontract extends BaseEntity {
    @TableId(value = "subcontractId", type = IdType.AUTO)
    private Long id;
    @TableField("subcontractCode")
    private String subcontractCode;
    @TableField("subcontractName")
    private String subcontractName;
    @TableField("projectId")
    private Long projectId;
    @TableField("projectCode")
    private String projectCode;
    @TableField("projectName")
    private String projectName;
    @TableField("facilitatorId")
    private Long facilitatorId;
    @TableField("facilitatorName")
    private String facilitatorName;
    @TableField("contractAmount")
    private Double contractAmount;
    @TableField("officeCode")
    private String officeCode;
    @TableField("state")
    private Integer state;
    @TableField("applyState")
    private Integer applyState;
    @TableField("instId")
    private String instId;
    @TableField("createTime")
    private LocalDateTime createTime;
    @TableField("createBy")
    private String createBy;
    @TableField(exist = false)
    private String officeName;
}
