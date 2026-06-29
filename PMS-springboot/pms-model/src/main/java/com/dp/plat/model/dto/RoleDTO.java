package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 角色创建/更新 DTO
 */
@Data
public class RoleDTO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String menuIds;
    private Integer status;
    private String defaultPage;
}
