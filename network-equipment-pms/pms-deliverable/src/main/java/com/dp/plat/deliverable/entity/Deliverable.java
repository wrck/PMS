package com.dp.plat.deliverable.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 交付件实体（全生命周期 7 态状态机）。
 *
 * <p>关联设计文档：§2.2（行 185-196）、§3.4 交付件状态机 7 态（行 393-428）。
 *
 * <p>状态流转：DRAFT → SUBMITTED → REVIEWED → SIGNED → PUBLISHED → REFERENCED → ARCHIVED
 * （其中 SUBMITTED/REVIEWED 可退回 DRAFT；PUBLISHED 可经修订新建版本回到 DRAFT）。
 *
 * <p>注：本实体映射 {@code pms_deliverable} 表（V2 建表、V70 扩展）。
 * pms-project 模块下的旧 {@code com.dp.plat.project.entity.Deliverable} 保留用于历史代码兼容，
 * 本模块为全生命周期的单一权威实体（含 7 态字段 + 版本/签核时间戳）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable")
public class Deliverable extends BaseEntity {

    /** 项目ID。 */
    private Long projectId;

    /** 交付件名称。 */
    private String deliverableName;

    /** 交付件类型（DOCUMENT/CONFIG/REPORT/OTHER）。 */
    private String deliverableType;

    /** 文件路径（最新版本）。 */
    private String filePath;

    /**
     * 状态（7 态）：DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/REFERENCED/ARCHIVED。
     *
     * <p>对应 {@link com.dp.plat.deliverable.enums.DeliverableStatus}。</p>
     */
    private String status;

    /** 所属阶段ID。 */
    private Long phaseId;

    /** 当前版本号，从 1 开始。修订时 +1 并新建 {@link DeliverableVersion} 记录。 */
    private Integer currentVersion;

    /** 是否必需交付件（影响阶段退出校验）。 */
    private Boolean mandatory;

    /** 签核角色。 */
    private String approverRole;

    /** 发布时间（SIGNED → PUBLISHED 时写入）。 */
    private LocalDateTime publishedAt;

    /** 归档时间（REFERENCED → ARCHIVED 时写入）。 */
    private LocalDateTime archivedAt;
}
