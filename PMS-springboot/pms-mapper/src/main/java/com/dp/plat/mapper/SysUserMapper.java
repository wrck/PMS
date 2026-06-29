package com.dp.plat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户 Mapper - 对应老系统 fnd_user_info 表
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /** 根据用户名查询用户（登录用） */
    SysUser selectByUsername(@Param("username") String username);

    /** 查询用户权限菜单 */
    List<Map<String, Object>> selectUserMenuMap(@Param("userId") Long userId);

    /** 查询用户默认首页 */
    String selectUserDefaultPage(@Param("userId") Long userId);

    /** 根据角色ID查询用户 */
    @Select("SELECT * FROM fnd_user_info WHERE role_ids LIKE CONCAT('%;', #{roleId}, ';%') AND status = 1 ORDER BY realname")
    List<SysUser> selectByRoleId(@Param("roleId") Long roleId);

    /** 根据角色ID和部门编码查询用户 */
    @Select("SELECT * FROM fnd_user_info WHERE role_ids LIKE CONCAT('%;', #{roleId}, ';%') AND dept_code = #{deptCode} AND status = 1 ORDER BY realname")
    List<SysUser> selectByRoleIdAndDeptCode(@Param("roleId") Long roleId, @Param("deptCode") String deptCode);

    /** 查询指定部门无特定角色的用户 */
    @Select("SELECT * FROM fnd_user_info WHERE dept_code = #{deptCode} AND (role_ids IS NULL OR role_ids NOT LIKE CONCAT('%;', #{roleId}, ';%')) AND status = 1 ORDER BY realname")
    List<SysUser> selectByDeptCodeWithoutRole(@Param("roleId") Long roleId, @Param("deptCode") String deptCode);
}
