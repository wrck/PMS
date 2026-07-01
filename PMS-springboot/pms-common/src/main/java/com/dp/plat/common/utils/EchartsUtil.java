package com.dp.plat.common.utils;

import java.util.*;

/**
 * ECharts图表工具类 - 迁移自老系统 EchartsUtil (341行, 10个方法)
 *
 * 生成ECharts图表所需的JSON数据结构
 */
public class EchartsUtil {

    /** 构建折线图数据 */
    public static Map<String, Object> buildLineChart(String title, List<String> xAxisData, Map<String, List<Object>> seriesData) {
        Map<String, Object> chart = new HashMap<>();
        chart.put("title", title);
        chart.put("xAxis", xAxisData);
        List<Map<String, Object>> series = new ArrayList<>();
        for (Map.Entry<String, List<Object>> entry : seriesData.entrySet()) {
            Map<String, Object> s = new HashMap<>();
            s.put("name", entry.getKey());
            s.put("type", "line");
            s.put("data", entry.getValue());
            series.add(s);
        }
        chart.put("series", series);
        return chart;
    }

    /** 构建柱状图数据 */
    public static Map<String, Object> buildBarChart(String title, List<String> xAxisData, Map<String, List<Object>> seriesData) {
        Map<String, Object> chart = buildLineChart(title, xAxisData, seriesData);
        for (Map<String, Object> s : (List<Map<String, Object>>) chart.get("series")) {
            s.put("type", "bar");
        }
        return chart;
    }

    /** 构建饼图数据 */
    public static Map<String, Object> buildPieChart(String title, Map<String, Object> data) {
        Map<String, Object> chart = new HashMap<>();
        chart.put("title", title);
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            pieData.add(item);
        }
        chart.put("data", pieData);
        return chart;
    }

    /** 包装表格HTML(用于报表展示) */
    public static String packagingTableHtml(List<Map<String, Object>> dataList) {
        if (dataList == null || dataList.isEmpty()) return "<p>暂无数据</p>";
        StringBuilder sb = new StringBuilder("<table border='1' cellpadding='5' cellspacing='0'><thead><tr>");
        Map<String, Object> firstRow = dataList.get(0);
        for (String key : firstRow.keySet()) {
            sb.append("<th>").append(key).append("</th>");
        }
        sb.append("</tr></thead><tbody>");
        for (Map<String, Object> row : dataList) {
            sb.append("<tr>");
            for (Object val : row.values()) {
                sb.append("<td>").append(val != null ? val : "").append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    /** 构建统计摘要 */
    public static Map<String, Object> buildSummary(String label, Object value) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("label", label);
        summary.put("value", value);
        return summary;
    }
}
