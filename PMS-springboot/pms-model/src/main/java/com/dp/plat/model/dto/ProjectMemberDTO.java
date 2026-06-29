package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 项目成员 DTO
 */
@Data
public class ProjectMemberDTO {
    private Long id;
    private Long projectId;
    private String memberCode;
    private String memberName;
    private String memberRole;
    private String phone;
    private String email;
}
