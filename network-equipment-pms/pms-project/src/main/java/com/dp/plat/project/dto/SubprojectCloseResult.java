package com.dp.plat.project.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 关闭主项目校验结果（作为 Result.data 返回，code=200）。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 2。
 * 关闭被拒绝时 {@code success=false}，{@code uncompletedSubProjects} 列出未关闭子项目；
 * 关闭成功时由 service 直接返回更新后的 Project，不使用本类。
 */
@Data
public class SubprojectCloseResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String errorCode;
    private String errorMessage;
    private List<UncompletedSubProject> uncompletedSubProjects;
}
