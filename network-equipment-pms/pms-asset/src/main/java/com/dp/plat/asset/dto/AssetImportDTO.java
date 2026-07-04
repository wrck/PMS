package com.dp.plat.asset.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Excel import DTO for {@link com.dp.plat.asset.entity.Asset}.
 *
 * <p>All fields are typed as {@code String} so that raw user input can be
 * validated before being coerced into entity field types.</p>
 */
@Data
public class AssetImportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "资产编号", index = 0)
    private String assetNo;

    @ExcelProperty(value = "序列号", index = 1)
    private String serialNo;

    @ExcelProperty(value = "项目ID", index = 2)
    private String projectId;

    @ExcelProperty(value = "设备类别ID", index = 3)
    private String categoryId;

    @ExcelProperty(value = "型号ID", index = 4)
    private String modelId;

    @ExcelProperty(value = "制造商", index = 5)
    private String manufacturer;

    @ExcelProperty(value = "状态", index = 6)
    private String status;
}
