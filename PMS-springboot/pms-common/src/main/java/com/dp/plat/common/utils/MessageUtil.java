package com.dp.plat.common.utils;

import java.util.ResourceBundle;
public class MessageUtil {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");
    public static String get(String key) { try { return bundle.getString(key); } catch (Exception e) { return key; } }
    public static String get(String key, Object... args) { return String.format(get(key), args); }
}
