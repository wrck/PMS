package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发货信息VO
 */
@Data
public class ShipmentInfoVO {
    private Long id;
    private Long projectId;
    private String projectCode;
    private String projectName;
    private String contractNo;
    private String barCode;
    private String itemCode;
    private String itemModel;
    private String itemName;
    private String receiveName;
    private String emsNum;
    private String emsCompany;
    private LocalDateTime packdate;
    private String installAddress;
    private String pcb;
    private String cpld;
    private String boot;
    private String conp;
    private String conpType;
    private String conpSeries;
    private String conpMark;
    private LocalDateTime executeTime;
    private Integer conpChange;
    private Integer cpldChange;
    private Integer bootChange;
    private Integer pcbChange;
    private String conpBak;
    private String cpldBak;
    private String bootBak;
    private String pcbBak;
    private Long chProjectId;
    private String chContractNo;
    private Long transferProjectId;
    private String transferContractNo;
    private String transferFlag;
    private String rmaNo;
    private String barCode2;
    private String itemCode2;
    private String itemModel2;
    private String itemName2;
}
