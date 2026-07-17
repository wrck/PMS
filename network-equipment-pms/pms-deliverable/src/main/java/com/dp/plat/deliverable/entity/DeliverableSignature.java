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
 * 交付件签名记录。
 *
 * <p>关联设计文档：§2.2（行 198-211 后段）、§3.4 SIGNED 阶段。
 * 记录 REVIEWED → SIGNED 流转时的签核动作（电子/印章/数字签名）。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable_signature")
public class DeliverableSignature extends BaseEntity {

    /** 所属交付件ID。 */
    private Long deliverableId;

    /** 签名对应的版本号。 */
    private Integer versionNo;

    /** 签核人ID。 */
    private Long signerId;

    /** 签核人姓名（冗余）。 */
    private String signerName;

    /** 签核角色。 */
    private String signerRole;

    /** 签名类型：ELECTRONIC/STAMP/DIGITAL。 */
    private String signatureType;

    /** 签名数据（证书指纹/印章图URL）。 */
    private String signatureData;

    /** 签核时间。 */
    private LocalDateTime signedAt;
}
