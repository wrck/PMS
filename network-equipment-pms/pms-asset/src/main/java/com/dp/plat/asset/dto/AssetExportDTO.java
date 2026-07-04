package com.dp.plat.asset.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Excel export DTO for {@link com.dp.plat.asset.entity.Asset}.
 *
 * <p>Carries the full set of asset fields a downstream user is likely to inspect
 * in a spreadsheet export.</p>
 */
@Data
public class AssetExportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "资产ID", index = 0)
    private Long id;

    @ExcelProperty(value = "资产编号", index = 1)
    private String assetName;

    @ExcelProperty(value = "序列号", index = 2)
    private String serialNo;

    @ExcelProperty(value = "项目ID", index = 3)
    private Long projectId;

    @ExcelProperty(value = "设备类别ID", index = 4)
    private Long categoryId;

    @ExcelProperty(value = "型号ID", index = 5)
    private Long modelId;

    @ExcelProperty(value = "状态", index = 6)
    private String status;

    @ExcelProperty(value = "仓库", index = 7)
    private String warehouse;

    @ExcelProperty(value = "库位", index = 8)
    private String location;

    @ExcelProperty(value = "MAC地址", index = 9)
    private String macAddress;

    @ExcelProperty(value = "管理IP", index = 10)
    private String managementIp;

    @ExcelProperty(value = "主机名", index = 11)
    private String hostname;

    @ExcelProperty(value = "数据中心", index = 12)
    private String dataCenter;

    @ExcelProperty(value = "机柜", index = 13)
    private String rack;

    @ExcelProperty(value = "PO单号", index = 14)
    private String poNo;

    @ExcelProperty(value = "入库时间", index = 15)
    private LocalDateTime inboundTime;

    @ExcelProperty(value = "出库时间", index = 16)
    private LocalDateTime outboundTime;

    @ExcelProperty(value = "备注", index = 17)
    private String remarks;
}
