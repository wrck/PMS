package com.dp.plat.model.vo;

import lombok.Data;

/**
 * SAP订单数据VO
 */
@Data
public class OrderDataVO {
    private Long id;
    private String contractNo;
    private String rmaContractNo;
    private Long projectId;
    private String itemCode;
    private String itemName;
    private String model;
    private Integer projectQuantity;
    private Integer orderQuantity;
    private Integer deliverQuantity;
    private Integer openQuantity;
    private String orderNumber;
    private String lineNum;
    private String barcode;
    private String receiveName;
    private String emsNum;
    private String emsCompany;
}
