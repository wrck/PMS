package com.dp.plat.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.workflow.entity.ApprovalFieldPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 审批敏感字段权限 Mapper。
 */
@Mapper
public interface ApprovalFieldPermissionMapper extends BaseMapper<ApprovalFieldPermission> {
}
