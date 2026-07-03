package com.dp.plat.asset.rma.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RMA (Return Merchandise Authorization) return ticket entity.
 *
 * <p>Implements a 6-step closed loop:
 * REGISTERED → WARRANTY_CHECKED → RMA_ISSUED → RETURNING → INSPECTED → CLOSED.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_rma")
public class Rma extends BaseEntity {

    /** Auto-generated RMA number, format: RMA-YYYY-XXXX. */
    private String rmaNo;

    private Long assetId;

    /** Asset serial number (snapshot at registration time). */
    private String sn;

    private String faultDescription;

    /** Comma-separated attachment ids (reserved). */
    private String faultPhotos;

    /** REGISTERED/WARRANTY_CHECKED/RMA_ISSUED/RETURNING/INSPECTED/CLOSED. */
    private String ticketStatus;

    /** IN_WARRANTY/OUT_OF_WARRANTY. */
    private String warrantyStatus;

    /** Optional: asset's current project at registration time. */
    private Long projectId;

    private LocalDateTime registeredAt;

    private LocalDateTime warrantyCheckedAt;

    private LocalDateTime rmaIssuedAt;

    private LocalDateTime returningAt;

    private LocalDateTime inspectedAt;

    private LocalDateTime closedAt;

    private Long registerUserId;

    private String registerUserName;

    /** Repair result description (set before/during inspection). */
    private String resolution;

    private String inspectorNotes;
}
