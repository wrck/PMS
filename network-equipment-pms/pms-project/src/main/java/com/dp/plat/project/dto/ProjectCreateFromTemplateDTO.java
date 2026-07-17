package com.dp.plat.project.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 从模板创建项目请求体
 * 关联设计文档：§5.2 Story 1 验收 1
 */
@Data
public class ProjectCreateFromTemplateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long templateId;
    private Long versionId;

    private String projectCode;
    private String projectName;
    private String customerName;
    private String customerContact;
    private String customerPhone;
    private String contractNo;
    private BigDecimal contractAmount;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private Long projectManagerId;

    private String projectObjective;
    private String projectScope;

    /** 初始成员 */
    private List<MemberDef> members;

    /** 配置覆盖（key → value） */
    private Map<String, String> configOverrides;

    @Data
    public static class MemberDef implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long userId;
        private String role; // PROJECT_MANAGER / PROJECT_MEMBER / APPROVER / VIEWER / CUSTOMER
    }
}
