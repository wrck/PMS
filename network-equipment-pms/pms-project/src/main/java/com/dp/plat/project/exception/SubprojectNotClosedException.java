package com.dp.plat.project.exception;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.project.dto.UncompletedSubProject;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

/**
 * 子项目未全部关闭异常。
 *
 * <p>关联设计文档：§3.2 Story 2 验收 2。
 * 由 {@code closeProject} 在存在未关闭（状态非 CLOSED / CANCELLED）的子项目时抛出，
 * 携带 {@code uncompletedSubProjects} 列表。响应格式由 {@link ProjectExceptionHandler}
 * 转换为 {@code Result.ok(SubprojectCloseResult)}（code=200、data.success=false）。
 */
@Getter
public class SubprojectNotClosedException extends BusinessException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<UncompletedSubProject> uncompletedSubProjects;

    public SubprojectNotClosedException(String message, List<UncompletedSubProject> uncompletedSubProjects) {
        super(message);
        this.uncompletedSubProjects = uncompletedSubProjects;
    }
}
