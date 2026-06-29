package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工程计划事件节点VO
 */
@Data
public class ProjectPlanEventVO {
    private Long id;
    private Long projectId;
    private String dataTypeCode;
    private String basicDataId;
    private String eventKey;
    private String eventValue;
    private LocalDateTime eventPlanHappenDate;
    private LocalDateTime eventActualFinishDate;
    private String column010;
    private String column011;
}
