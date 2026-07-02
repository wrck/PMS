package com.dp.plat.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
public class DisplayParamUtil {
    public static <T> Page<T> toPage(int pageNum, int pageSize) { return new Page<>(pageNum, pageSize); }
    public static int calcOffset(int pageNum, int pageSize) { return (pageNum - 1) * pageSize; }
}
