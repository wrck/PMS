package com.dp.plat.common.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Excel导出工具类 - 迁移自老系统 ExportUtils (391行, 7个方法)
 *
 * 使用Apache POI进行Excel操作
 * 注意: 需要引入poi-ooxml依赖
 */
public class ExportUtils {

    /**
     * 将List<Map>数据导出为CSV格式字节数组
     * 迁移自: ExportUtils.buildExcelDocument()
     */
    public static byte[] exportToCsv(List<Map<String, Object>> data, String[] headers, String[] keys) {
        StringBuilder sb = new StringBuilder();
        // 表头
        if (headers != null) {
            for (int i = 0; i < headers.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(headers[i]).append("\"");
            }
            sb.append("\n");
        }
        // 数据行
        if (data != null) {
            for (Map<String, Object> row : data) {
                for (int i = 0; i < keys.length; i++) {
                    if (i > 0) sb.append(",");
                    Object val = row.get(keys[i]);
                    sb.append("\"").append(val != null ? val.toString().replace("\"", "\"\"") : "").append("\"");
                }
                sb.append("\n");
            }
        }
        try {
            return sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return sb.toString().getBytes();
        }
    }

    /**
     * 将List数据导出为CSV格式字节数组(使用反射)
     * 迁移自: ExportUtils.buildExcelDocument() 泛型版本
     */
    public static <T> byte[] exportToCsv(List<T> data, Class<T> clazz) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (data != null) {
            for (T item : data) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        map.put(field.getName(), field.get(item));
                    } catch (IllegalAccessException e) {
                        map.put(field.getName(), null);
                    }
                }
                mapList.add(map);
            }
        }
        if (mapList.isEmpty()) return new byte[0];
        String[] keys = mapList.get(0).keySet().toArray(new String[0]);
        return exportToCsv(mapList, keys, keys);
    }

    /**
     * 从CSV/文本解析为List<Map>
     * 迁移自: ExportUtils.readFromExcel()
     */
    public static List<Map<String, Object>> parseCsv(byte[] data) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            String content = new String(data, "UTF-8");
            String[] lines = content.split("\n");
            if (lines.length < 2) return result;
            String[] headers = parseCsvLine(lines[0]);
            for (int i = 1; i < lines.length; i++) {
                String[] values = parseCsvLine(lines[i]);
                Map<String, Object> row = new LinkedHashMap<>();
                for (int j = 0; j < headers.length && j < values.length; j++) {
                    row.put(headers[j], values[j]);
                }
                result.add(row);
            }
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return result;
    }

    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder field = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuote = !inQuote;
            } else if (c == ',' && !inQuote) {
                result.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim());
        return result.toArray(new String[0]);
    }

    /**
     * HTML清理(用于导出时清理HTML标签)
     * 迁移自: ExportUtils中的HTML处理逻辑
     */
    public static String cleanHtml(String html) {
        if (html == null || html.isEmpty()) return "";
        String result = html;
        result = result.replaceAll("\r\n", "");
        result = result.replaceAll("<(?!img|br|/p|/table|/tr|/th|/td).*?>", "");
        result = result.replaceAll("<(?!img|br|/p|/table|/tr).*?>", "    ");
        result = result.replaceAll("<(?!img).*?>", "\r\n");
        return result;
    }
}
