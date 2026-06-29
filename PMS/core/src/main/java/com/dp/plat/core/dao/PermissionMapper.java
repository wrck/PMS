package com.dp.plat.core.dao;

import com.dp.plat.core.pojo.Permission;

public interface PermissionMapper {

	int deleteByPrimaryKey(Integer permissionId);

    int insert(Permission record);

    int insertSelective(Permission record);

    Permission selectByPrimaryKey(Integer permissionId);

    int updateByPrimaryKeySelective(Permission record);

    int updateByPrimaryKey(Permission record);
}