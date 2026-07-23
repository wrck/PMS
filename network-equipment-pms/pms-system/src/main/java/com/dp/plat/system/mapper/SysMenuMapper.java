package com.dp.plat.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for {@link SysMenu}.
 *
 * <p>迁移说明：底层 RBAC 已从 PMS 自研 {@code sys_*} 表迁移到 yudao 的 {@code system_*}
 * 表。本 Mapper 的 3 条 SQL 已重写为 JOIN {@code system_menu} × {@code system_role_menu}
 * × {@code system_user_role}，字段名也改为 yudao 风格（{@code permission} 而非
 * {@code perms}，{@code sort} 而非 {@code order_num}）。</p>
 *
 * <p>注意：yudao 权限校验链走 {@code @ss.hasPermission('xxx')}（SecurityFrameworkService），
 * 调用 yudao {@code PermissionService.hasAnyPermissions()}，从 {@code system_menu.permission}
 * 加载权限。本 Mapper 仅保留给 PMS 自身的菜单树查询使用（如 SysMenuController 菜单列表）。</p>
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * List menu entities authorized to the given user via role-menu / user-role joins.
     * 底层已迁移到 yudao system_* 表。
     */
    @Select("SELECT m.* FROM system_menu m "
            + "JOIN system_role_menu rm ON m.id = rm.menu_id "
            + "JOIN system_user_role ur ON rm.role_id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND m.deleted = 0 "
            + "ORDER BY m.sort")
    List<SysMenu> listMenusByUserId(@Param("userId") Long userId);

    /**
     * List distinct permission strings authorized to the given user.
     * 底层已迁移到 yudao system_* 表，字段名 permission。
     */
    @Select("SELECT DISTINCT m.permission FROM system_menu m "
            + "JOIN system_role_menu rm ON m.id = rm.menu_id "
            + "JOIN system_user_role ur ON rm.role_id = ur.role_id "
            + "WHERE ur.user_id = #{userId} AND m.deleted = 0 "
            + "AND m.permission IS NOT NULL AND m.permission <> ''")
    List<String> listPermsByUserId(@Param("userId") Long userId);

    /**
     * 列出 system_menu 中所有已注册的权限标识（去重）。
     * 用于超级管理员加载全部具体权限，使 {@code @PreAuthorize("@ss.hasPermission('xxx')")}
     * 注解也能通过。
     */
    @Select("SELECT DISTINCT permission FROM system_menu "
            + "WHERE deleted = 0 AND permission IS NOT NULL AND permission <> ''")
    List<String> listAllPerms();
}
