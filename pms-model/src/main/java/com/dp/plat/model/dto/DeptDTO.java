package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 部门创建/更新 DTO
 */
@Data
public class DeptDTO {
    private Long id;
    private String deptName;
    private String deptCode;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
