package com.dp.plat.lowcode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 版本树节点（借鉴 Appsmith Git / OutSystems LifeTime 版本树可视化）。
 *
 * <p>当前版本实体无 parentVersionId 字段，故按版本号升序线性构建：
 * 最早版本为根节点，后续每个版本挂在前一版本下（v1 → v2 → v3 链式树）。
 * 若未来引入 parentVersionId，可直接按父子关系构建真正的分支树。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionTreeNode {
    /** 版本号 */
    private Integer version;
    /** 配置编码 */
    private String configCode;
    /** 变更说明 */
    private String changeLog;
    /** 状态: ACTIVE/ARCHIVED */
    private String status;
    /** 环境: DEV/TEST/PROD */
    private String environment;
    /** 操作人 */
    private String createBy;
    /** 创建时间 */
    private String createTime;
    /** 子版本节点（线性构建时最多一个子节点） */
    @Builder.Default
    private List<VersionTreeNode> children = new ArrayList<>();
}
