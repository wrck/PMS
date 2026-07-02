package com.dp.plat.config;

public class DataSourceContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSourceType(String type) { CONTEXT_HOLDER.set(type); }
    public static String getDataSourceType() { return CONTEXT_HOLDER.get(); }
    public static void clearDataSourceType() { CONTEXT_HOLDER.remove(); }
}
