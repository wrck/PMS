package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程计划VO
 */
@Data
public class ProjectPlanVO {
    private Long id;
    private String contractNo;
    private String batchCode;
    private String basicDataName;
    private String referenceEventName;
    private LocalDateTime eventPlanHappenDate;
    private Integer afterDaysNum;
    private LocalDateTime eventActualFinishDate;
    private String marketingFeedback;
    private String attachment;
}
