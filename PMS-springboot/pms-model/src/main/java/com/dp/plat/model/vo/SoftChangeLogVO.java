package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 软件版本变更记录VO
 */
@Data
public class SoftChangeLogVO {
    private Long id;
    private Long projectId;
    private String changeVersion;
    private String changeRemark;
    private Integer latest;
    private String createBy;
    private LocalDateTime createTime;
    private String versionAndCreateTime;
}
