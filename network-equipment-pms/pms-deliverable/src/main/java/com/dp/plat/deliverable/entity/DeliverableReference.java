package com.dp.plat.deliverable.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 交付件引用关系。
 *
 * <p>关联设计文档：§3.4 PUBLISHED → REFERENCED 流转。
 * 记录已发布交付件被其他业务对象（TASK/PHASE/PROJECT/DELIVERABLE/REPORT）引用的关系。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_deliverable_reference")
public class DeliverableReference extends BaseEntity {

    /** 被引用的交付件ID（源交付件）。 */
    private Long sourceDeliverableId;

    /** 引用方为交付件时填其 ID，否则 NULL。 */
    private Long targetDeliverableId;

    /** 引用方业务类型：TASK/PHASE/PROJECT/DELIVERABLE/REPORT。 */
    private String referenceType;

    /** 引用方业务ID。 */
    private Long referencedById;

    /** 引用方名称（冗余）。 */
    private String referencedByName;
}
