package com.dp.plat.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录返回 VO
 */
@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String realname;
    private String email;
    private String deptCode;
    private String deptName;
    private String roleIds;
    private String roleName;
    private String defaultPage;
    private String areaPower;
}
