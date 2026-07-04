package com.dp.plat.project.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Excel import DTO for {@link com.dp.plat.project.entity.Milestone}.
 *
 * <p>Fields are typed as {@code String} so that raw user input can be validated
 * (project existence, milestone type enum membership, date format) before any
 * coercion happens.</p>
 */
@Data
public class MilestoneImportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "项目ID", index = 0)
    private String projectId;

    @ExcelProperty(value = "里程碑类型", index = 1)
    private String milestoneType;

    @ExcelProperty(value = "里程碑名称", index = 2)
    private String milestoneName;

    @ExcelProperty(value = "计划完成日", index = 3)
    private String planDate;

    @ExcelProperty(value = "实际完成日", index = 4)
    private String actualDate;

    @ExcelProperty(value = "状态", index = 5)
    private String status;

    @ExcelProperty(value = "描述", index = 6)
    private String description;
}
