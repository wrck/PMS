package com.dp.plat.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 回访数据分析VO - 迁移自老系统 PmClCBData
 */
@Data
public class CbDataVO {
    private Long id;
    private String projectName;
    private String projectCode;
    private String pmUserName;
    private String pmRealName;
    private String officeCode;
    private String officeName;
    private LocalDateTime cbTime;
    private Integer times;
    private Double projectScore;
    private Double otherScore;
    private Double equScore;
    private Double engScore;
    private Double totalScore;
    private Integer cbResult;
    private String approveRemark;
    private String opinion;
    private String equExplain;
    private Integer evaResult;
    private Long projectId;
    private String serviceType;
    private String compId;
    private String companyName;
    private String companyAbbr;
}
