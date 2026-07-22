package com.dp.plat.framework.datapermission.core.util;

import com.dp.plat.framework.datapermission.core.annotation.DataPermission;
import com.dp.plat.framework.datapermission.core.aop.DataPermissionContextHolder;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;

/**
 * 数据权限 Util
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 * 提供 {@link #executeIgnore(Runnable)} 等便捷方法，在指定代码段内临时忽略数据权限。
 *
 * @author yudao
 */
public class DataPermissionUtils {

    private static DataPermission DATA_PERMISSION_DISABLE;

    @DataPermission(enable = false)
    @SneakyThrows
    private static DataPermission getDisableDataPermissionDisable() {
        if (DATA_PERMISSION_DISABLE == null) {
            DATA_PERMISSION_DISABLE = DataPermissionUtils.class
                    .getDeclaredMethod("getDisableDataPermissionDisable")
                    .getAnnotation(DataPermission.class);
        }
        return DATA_PERMISSION_DISABLE;
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        addDisableDataPermission();
        try {
            // 执行 runnable
            runnable.run();
        } finally {
            removeDataPermission();
        }
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param callable 逻辑
     * @return 执行结果
     */
    @SneakyThrows
    public static <T> T executeIgnore(Callable<T> callable) {
        addDisableDataPermission();
        try {
            // 执行 callable
            return callable.call();
        } finally {
            removeDataPermission();
        }
    }

    /**
     * 添加忽略数据权限
     */
    public static void addDisableDataPermission() {
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
    }

    public static void removeDataPermission() {
        DataPermissionContextHolder.remove();
    }

}
