package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 恢复任务周报 - 对应老系统 ProbRestoreWeekly (6字段)
 * 对应表: prob_restore_weekly
 */
@Data
@TableName("prob_restore_weekly")
public class ProbRestoreWeekly extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("restore_id") private Long restoreId;
    @TableField("prob_id") private Long probId;
    @TableField("file_id") private Long fileId;
    @TableField("file_path") private String filePath;
}
