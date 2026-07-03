package com.dp.plat.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Equipment asset instance entity.
 * status: IN_STOCK=在库, ALLOCATED=已分配, IN_TRANSIT=调拨中, SCRAPPED=已报废.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_asset")
public class Asset extends BaseEntity {

    private String serialNo;

    private Long modelId;

    private Long categoryId;

    private String assetName;

    /** IN_STOCK, ALLOCATED, IN_TRANSIT, SCRAPPED. */
    private String status;

    private String warehouse;

    private String location;

    /** Current allocated project id, nullable. */
    private Long projectId;

    private LocalDateTime inboundTime;

    private LocalDateTime outboundTime;

    private String remarks;

    /** Serialization / deployment fields. */
    private String macAddress;

    private String managementIp;

    private String hostname;

    private String dataCenter;

    private String rack;

    /** Rack start U position. */
    private Integer startU;

    /** Rack end U position. */
    private Integer endU;

    private String imei;

    private String poNo;

    private String invoiceNo;

    private String warrantyContractNo;
}
