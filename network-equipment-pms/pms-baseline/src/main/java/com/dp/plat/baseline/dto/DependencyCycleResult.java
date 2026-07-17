package com.dp.plat.baseline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 循环依赖检测结果（HTTP 200 + success=false 的结构化数据载荷）。
 *
 * <p>关联设计文档：§5.5 Story 4 验收 1。当 {@code saveDependency} 检测到闭环时，
 * 由 {@code BaselineExceptionHandler} 包装为本对象返回，前端按 {@code success}
 * 分支处理并提示闭环路径。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DependencyCycleResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务是否成功（检测到循环时为 false）。 */
    private Boolean success;

    /** 错误码（CYCLE_DETECTED）。 */
    private String errorCode;

    /** 错误描述（含闭环路径任务名）。 */
    private String errorMessage;

    /** 闭环路径节点列表（首尾为同一任务）。 */
    private List<CycleNode> cyclePath;
}
