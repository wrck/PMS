package com.dp.plat.framework.common.biz.system.permission;

import com.dp.plat.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;

/**
 * 权限通用 API 接口
 *
 * <p>直接复用自 yudao-framework（yudao-common）。由 pms-system 的权限服务实现，
 * 供数据权限框架（{@link com.dp.plat.framework.datapermission.core.rule.dept.DeptDataPermissionRule}）
 * 在执行 SQL 拦截时获取用户的部门数据权限。
 *
 * <p>采用 SPI 风格，使 pms-common 不依赖 pms-system，避免循环依赖。
 *
 * @author yudao
 */
public interface PermissionCommonApi {

    /**
     * 判断是否有权限，任一一个即可
     *
     * @param userId 用户编号
     * @param permissions 权限
     * @return 是否
     */
    boolean hasAnyPermissions(Long userId, String... permissions);

    /**
     * 判断是否有角色，任一一个即可
     *
     * @param userId 用户编号
     * @param roles 角色数组
     * @return 是否
     */
    boolean hasAnyRoles(Long userId, String... roles);

    /**
     * 获得登录用户的部门数据权限
     *
     * @param userId 用户编号
     * @return 部门数据权限
     */
    DeptDataPermissionRespDTO getDeptDataPermission(Long userId);

}
