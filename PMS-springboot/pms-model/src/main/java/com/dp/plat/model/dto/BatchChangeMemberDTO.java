package com.dp.plat.model.dto;

import lombok.Data;

/**
 * 批量变更成员DTO
 */
@Data
public class BatchChangeMemberDTO {
    /** 变更类型: service=服务经理, program=项目经理, both=两者 */
    private String changeType;
    /** 部门编码 */
    private String dpNo;
    /** 旧成员编码 */
    private String oldMemberCode;
    /** 新成员编码 */
    private String newMemberCode;
    /** 新成员名称 */
    private String newMemberName;
}
