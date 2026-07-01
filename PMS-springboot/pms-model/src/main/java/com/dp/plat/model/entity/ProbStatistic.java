package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

/**
 * 技术公告统计实体 - 对应老系统 ProbStatistic (36字段)
 * 对应表: prob_statistic (视图/查询结果)
 */
@Data
public class ProbStatistic extends BaseEntity {
    private Long id;
    private String probNum;
    private String theme;
    private String status;
    private String statusName;
    private String watch;
    private String watchName;
    private String priority;
    private String priorityName;
    private String trackingUser;
    private String trackingUsername;
    private String officeCode;
    private String officeName;
    private String startTime;
    private String endTime;
    private Integer tabIndex;
    private Boolean autoAdjust;
    private Integer probCount;
    private Integer restoreCount;
    private Integer closedCount;
    private Integer openCount;
}
