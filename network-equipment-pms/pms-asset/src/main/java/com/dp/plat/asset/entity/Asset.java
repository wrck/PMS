package com.dp.plat.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.dp.plat.common.entity.BaseEntity;
import jakarta.validation.constraints.Min;
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

    @NotBlank(message = "设备序列号不能为空")
    @Size(max = 100, message = "设备序列号长度不能超过 100 个字符")
    private String serialNo;

    @NotNull(message = "设备型号 ID 不能为空")
    private Long modelId;

    @NotNull(message = "设备分类 ID 不能为空")
    private Long categoryId;

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 200, message = "设备名称长度不能超过 200 个字符")
    private String assetName;

    /** IN_STOCK, ALLOCATED, IN_TRANSIT, SCRAPPED. */
    @NotBlank(message = "设备状态不能为空")
    @Size(max = 30, message = "状态长度不能超过 30 个字符")
    private String status;

    @Size(max = 100, message = "仓库名称长度不能超过 100 个字符")
    private String warehouse;

    @Size(max = 200, message = "位置长度不能超过 200 个字符")
    private String location;

    /** Current allocated project id, nullable. */
    private Long projectId;

    private LocalDateTime inboundTime;

    private LocalDateTime outboundTime;

    @Size(max = 500, message = "备注长度不能超过 500 个字符")
    private String remarks;

    /** Serialization / deployment fields. */
    @Size(max = 100, message = "MAC 地址长度不能超过 100 个字符")
    private String macAddress;

    @Size(max = 50, message = "管理 IP 长度不能超过 50 个字符")
    private String managementIp;

    @Size(max = 100, message = "主机名长度不能超过 100 个字符")
    private String hostname;

    @Size(max = 100, message = "数据中心长度不能超过 100 个字符")
    private String dataCenter;

    @Size(max = 50, message = "机柜长度不能超过 50 个字符")
    private String rack;

    /** Rack start U position. */
    @Min(value = 1, message = "起始 U 位必须大于 0")
    private Integer startU;

    /** Rack end U position. */
    @Min(value = 1, message = "结束 U 位必须大于 0")
    private Integer endU;

    @Size(max = 50, message = "IMEI 长度不能超过 50 个字符")
    private String imei;

    @Size(max = 100, message = "PO 编号长度不能超过 100 个字符")
    private String poNo;

    @Size(max = 100, message = "发票号长度不能超过 100 个字符")
    private String invoiceNo;

    @Size(max = 100, message = "保修合同号长度不能超过 100 个字符")
    private String warrantyContractNo;

    /** 乐观锁版本号（MyBatis-Plus @Version，并发更新冲突检测）. */
    @Version
    private Integer version;
}
