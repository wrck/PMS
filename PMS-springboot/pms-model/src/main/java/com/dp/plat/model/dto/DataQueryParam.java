package com.dp.plat.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 数据分析查询参数 - 迁移自老系统 DataQueryParam
 */
@Data
public class DataQueryParam {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String officeCodes;
    private String pm;
    private String projectType;
    private String serviceType;
    private String projectPhase;
    private Double finishingRate;
    private LocalDateTime phaseStartTime;
    private LocalDateTime phaseEndTime;
    private LocalDateTime cbStartTime;
    private LocalDateTime cbEndTime;
    private String compId;
    private String officeCode;
}
