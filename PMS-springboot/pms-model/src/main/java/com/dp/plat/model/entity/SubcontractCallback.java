package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;
import java.time.LocalDateTime;

/** 分包回调 - 对应老系统 SubcontractCallback (14字段) */
@Data
@TableName("pm_subcontract_callback")
public class SubcontractCallback extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("subcontract_id") private Long subcontractId;
    @TableField("project_id") private Long projectId;
    @TableField("callback_state") private Integer callbackState;
    @TableField("apply_by") private String applyBy;
    @TableField("apply_time") private LocalDateTime applyTime;
    @TableField("inst_id") private String instId;
    @TableField("remark") private String remark;
}
