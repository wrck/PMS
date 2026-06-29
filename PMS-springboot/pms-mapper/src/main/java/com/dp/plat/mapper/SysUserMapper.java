package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper - 对应老系统 fnd_user_info 表
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（登录用）
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 查询用户权限菜单
     */
    List<Map<String, Object>> selectUserMenuMap(@Param("userId") Long userId);

    /**
     * 查询用户默认首页
     */
    String selectUserDefaultPage(@Param("userId") Long userId);
}
