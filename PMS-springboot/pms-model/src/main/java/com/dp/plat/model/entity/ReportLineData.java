package com.dp.plat.model.entity;

import lombok.Data;

/**
 * 报表折线数据 - 对应老系统 ReportLineData (8字段)
 * 用于ECharts图表数据传输
 */
@Data
public class ReportLineData {
    private String xAxis;
    private String yAxis;
    private String seriesName;
    private Object value;
    private String category;
    private String groupBy;
    private Integer count;
    private Double rate;
}
