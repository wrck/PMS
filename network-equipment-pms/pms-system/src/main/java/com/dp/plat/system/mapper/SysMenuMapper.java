package com.dp.plat.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for {@link SysMenu}.
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * List menu entities authorized to the given user via role-menu / user-role joins.
     */
    @Select("SELECT m.* FROM sys_menu m "
            + "JOIN sys_role_menu rm ON m.id = rm.menu_id "
            + "JOIN sys_user_role ur ON rm.role_id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND m.deleted = 0 "
            + "ORDER BY m.order_num")
    List<SysMenu> listMenusByUserId(@Param("userId") Long userId);

    /**
     * List distinct permission strings authorized to the given user.
     */
    @Select("SELECT DISTINCT m.perms FROM sys_menu m "
            + "JOIN sys_role_menu rm ON m.id = rm.menu_id "
            + "JOIN sys_user_role ur ON rm.role_id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND m.deleted = 0 "
            + "AND m.perms IS NOT NULL AND m.perms <> ''")
    List<String> listPermsByUserId(@Param("userId") Long userId);

    /**
     * 列出 sys_menu 中所有已注册的权限标识（去重）。
     * 用于超级管理员加载全部具体权限，使 {@code @PreAuthorize("hasAuthority('xxx')")}
     * 注解也能通过。
     */
    @Select("SELECT DISTINCT perms FROM sys_menu "
            + "WHERE deleted = 0 AND perms IS NOT NULL AND perms <> ''")
    List<String> listAllPerms();
}
