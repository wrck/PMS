package com.dp.plat.framework.datapermission.core.aop;

import com.dp.plat.framework.datapermission.core.annotation.DataPermission;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link DataPermission} 注解的上下文持有者
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 * 使用 {@link LinkedList} 作为栈结构，支持方法的嵌套调用。
 *
 * <p><b>调整说明</b>：原 yudao 实现使用 {@code TransmittableThreadLocal}（TTL）
 * 以兼容线程池异步执行；本仓库尚未引入 TTL 依赖，改用普通 {@link ThreadLocal}。
 * 后续若启用 TTL 线程池方案，可平滑切换。
 *
 * @author yudao
 */
public class DataPermissionContextHolder {

    /**
     * 使用 List 的原因，可能存在方法的嵌套调用
     */
    private static final ThreadLocal<LinkedList<DataPermission>> DATA_PERMISSIONS =
            ThreadLocal.withInitial(LinkedList::new);

    /**
     * 获得当前的 DataPermission 注解
     *
     * @return DataPermission 注解
     */
    public static DataPermission get() {
        return DATA_PERMISSIONS.get().peekLast();
    }

    /**
     * 入栈 DataPermission 注解
     *
     * @param dataPermission DataPermission 注解
     */
    public static void add(DataPermission dataPermission) {
        DATA_PERMISSIONS.get().addLast(dataPermission);
    }

    /**
     * 出栈 DataPermission 注解
     *
     * @return DataPermission 注解
     */
    public static DataPermission remove() {
        DataPermission dataPermission = DATA_PERMISSIONS.get().removeLast();
        // 无元素时，清空 ThreadLocal，避免内存泄漏
        if (DATA_PERMISSIONS.get().isEmpty()) {
            DATA_PERMISSIONS.remove();
        }
        return dataPermission;
    }

    /**
     * 获得所有 DataPermission
     *
     * @return DataPermission 队列
     */
    public static List<DataPermission> getAll() {
        return DATA_PERMISSIONS.get();
    }

    /**
     * 清空上下文
     *
     * 目前仅仅用于单测
     */
    public static void clear() {
        DATA_PERMISSIONS.remove();
    }

}
