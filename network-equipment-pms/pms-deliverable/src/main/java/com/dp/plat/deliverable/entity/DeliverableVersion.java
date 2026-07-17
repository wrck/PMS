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
 * 交付件版本（不可变历史记录）。
 *
 * <p>关联设计文档：§2.2（行 198-211）。已发布版本的 file_path 不允许覆盖；
 * 修订时新建 versionNo + 1 的记录，旧版本保留不变。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable_version")
public class DeliverableVersion extends BaseEntity {

    /** 所属交付件ID。 */
    private Long deliverableId;

    /** 版本号 1, 2, 3...（同一交付件内唯一）。 */
    private Integer versionNo;

    /** 文件路径。 */
    private String filePath;

    /** 文件 SHA256 校验和。 */
    private String fileChecksum;

    /** 上传人ID。 */
    private Long uploadedBy;

    /** 上传时间。 */
    private LocalDateTime uploadedAt;

    /** 版本变更说明。 */
    private String changeLog;

    /** 该版本流转状态（DRAFT/SUBMITTED/REVIEWED/SIGNED/PUBLISHED/...）。 */
    private String status;
}
