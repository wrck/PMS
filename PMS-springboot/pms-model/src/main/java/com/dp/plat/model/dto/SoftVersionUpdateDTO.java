package com.dp.plat.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 软件版本更新DTO
 */
@Data
public class SoftVersionUpdateDTO {
    /** 项目ID */
    private Long projectId;
    /** 合同号 */
    private String contractNo;
    /** 软件版本列表(JSON) */
    private String softVersionJson;
    /** 变更版本号 */
    private String changeVersion;
    /** 变更说明 */
    private String changeRemark;
}
