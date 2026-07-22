package com.dp.plat.framework.common.core;

/**
 * 可生成 T 数组的接口
 *
 * <p>直接复用自 yudao-framework，供枚举基类使用。
 *
 * @author yudao
 */
public interface ArrayValuable<T> {

    /**
     * @return 数组
     */
    T[] array();

}
