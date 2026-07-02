package com.dp.plat.common.utils;

import java.util.*;
public class ChartUtil {
    public static Map<String, Object> buildLineChart(List<String> xData, List<List<Object>> series) {
        Map<String, Object> chart = new HashMap<>();
        chart.put("xAxis", Map.of("type", "category", "data", xData));
        chart.put("yAxis", Map.of("type", "value"));
        List<Map<String, Object>> seriesList = new ArrayList<>();
        for (List<Object> data : series) seriesList.add(Map.of("type", "line", "data", data));
        chart.put("series", seriesList);
        return chart;
    }
}
