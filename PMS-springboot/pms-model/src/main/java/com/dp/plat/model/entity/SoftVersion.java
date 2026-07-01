package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 软件版本实体 - 对应老系统 SoftVersion (43字段)
 * 对应表: prob_soft_version
 */
@Data
@TableName("prob_soft_version")
public class SoftVersion extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO) private Long id;
    @TableField("prob_id") private Integer probId;
    @TableField("platform_type") private String platformType;
    @TableField("soft_version_types") private String softVersionTypes;
    @TableField("manual_entry") private String manualEntry;
    @TableField("manual_entry_sub") private String manualEntrySub;
    @TableField("entry_type") private String entryType;
    @TableField("entry_series") private String entrySeries;
    @TableField("entry_start") private String entryStart;
    @TableField("entry_end") private String entryEnd;
    @TableField("mark_start") private String markStart;
    @TableField("mark_end") private String markEnd;
    @TableField("affected_type") private Integer affectedType;
    @TableField("group_id") private Long groupId;
    @TableField("splited") private Integer splited;
}
