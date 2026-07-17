package com.dp.plat.baseline.exception;

import com.dp.plat.baseline.dto.CycleNode;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

/**
 * 保存任务依赖时检测到循环依赖所抛出的异常。
 *
 * <p>关联设计文档：§3.6 循环依赖检测（Story 4 验收 1）、§5.5。
 * 携带闭环路径 {@code cyclePath}（首尾为同一任务，如 A→B→C→A），
 * 由 {@code BaselineExceptionHandler} 转换为 HTTP 200 + 结构化失败数据
 * （success=false、errorCode=CYCLE_DETECTED）。</p>
 */
@Getter
public class CycleDetectedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ERROR_CODE = "CYCLE_DETECTED";

    /** 闭环路径节点列表（含首尾闭合节点）。 */
    private final List<CycleNode> cyclePath;

    public CycleDetectedException(List<CycleNode> cyclePath) {
        super(buildMessage(cyclePath));
        this.cyclePath = cyclePath;
    }

    private static String buildMessage(List<CycleNode> cyclePath) {
        if (cyclePath == null || cyclePath.isEmpty()) {
            return "形成循环依赖";
        }
        StringBuilder sb = new StringBuilder("形成循环依赖，闭环路径: ");
        for (int i = 0; i < cyclePath.size(); i++) {
            if (i > 0) {
                sb.append(" → ");
            }
            sb.append(cyclePath.get(i).getTaskName());
        }
        return sb.toString();
    }
}
