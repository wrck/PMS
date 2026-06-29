package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 项目回退DTO
 */
@Data
public class ProjectBackDTO {
    /** 项目ID */
    private Long projectId;
    /** 项目状态 */
    private String projectState;
    /** 回退状态 */
    private String isback;
    /** 回退原因 */
    private String backCause;
    /** 不同意回退原因 */
    private String notbackCause;
    /** 项目经理 */
    private String pm;
    /** 实施方式 */
    private String column012;
    /** 渠道名称 */
    private String channelName;
    /** 扩展字段13 */
    private String column013;
    /** 未授权尾款原因 */
    private String notGrantTailCause;
    /** 是否更新 */
    private Integer isupdate;
}
