package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目软件版本VO
 */
@Data
public class ProjectSoftVersionVO {
    private Long id;
    private Long shipmentId;
    private Long projectId;
    private String contractNo;
    private String barCode;
    private String itemCode;
    private String itemModel;
    private String itemName;
    private String pcb;
    private String cpld;
    private String boot;
    private String conp;
    private String conpType;
    private String conpSeries;
    private String conpMark;
    private Long logId;
    private Integer datastate;
    private String officeCode;
    private String marketCode;
    private String systemCode;
    private String expendCode;
    private String industryCode;
}
