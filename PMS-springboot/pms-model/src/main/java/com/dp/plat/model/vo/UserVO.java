package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户列表 VO
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String realname;
    private String email;
    private String phone;
    private String deptCode;
    private String deptName;
    private String roleIds;
    private String roleName;
    private Integer status;
    private String defaultPage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime pwdOverdue;
}
