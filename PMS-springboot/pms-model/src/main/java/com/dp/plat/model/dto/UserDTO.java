package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 用户创建/更新 DTO
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realname;
    private String email;
    private String phone;
    private String deptCode;
    private Long deptId;
    private Integer status;
    private String roleIds;
    private String defaultPage;
}
