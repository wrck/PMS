package com.dp.plat.baseline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 循环依赖闭环路径中的一个节点（任务）。
 *
 * <p>关联设计文档：§5.5 Story 4 验收 1 — cyclePath 元素结构
 * {@code {"taskId": 101, "taskName": "任务A"}}。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CycleNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 任务ID。 */
    private Long taskId;

    /** 任务名称。 */
    private String taskName;
}
