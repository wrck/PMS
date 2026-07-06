package com.dp.plat.asset.rma.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "RMA单号不能为空")
    @Size(max = 50, message = "RMA单号长度不能超过 50 个字符")
    private String rmaNo;

    @NotNull(message = "资产ID不能为空")
    private Long assetId;

    /** Asset serial number (snapshot at registration time). */
    @Size(max = 100, message = "序列号长度不能超过 100 个字符")
    private String sn;

    @NotBlank(message = "故障描述不能为空")
    @Size(max = 2000, message = "故障描述长度不能超过 2000 个字符")
    private String faultDescription;

    /** Comma-separated attachment ids (reserved). */
    private String faultPhotos;

    /** REGISTERED/WARRANTY_CHECKED/RMA_ISSUED/RETURNING/INSPECTED/CLOSED. */
    @Size(max = 50, message = "工单状态长度不能超过 50 个字符")
    private String ticketStatus;

    /** IN_WARRANTY/OUT_OF_WARRANTY. */
    @Size(max = 50, message = "质保状态长度不能超过 50 个字符")
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

    @Size(max = 50, message = "登记人名称长度不能超过 50 个字符")
    private String registerUserName;

    /** Repair result description (set before/during inspection). */
    @Size(max = 2000, message = "处理结果长度不能超过 2000 个字符")
    private String resolution;

    @Size(max = 2000, message = "检验备注长度不能超过 2000 个字符")
    private String inspectorNotes;
}
