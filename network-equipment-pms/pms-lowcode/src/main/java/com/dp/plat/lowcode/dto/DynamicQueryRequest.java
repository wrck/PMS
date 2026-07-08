package com.dp.plat.lowcode.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态实体高级查询请求。
 *
 * <p>支持多种操作符（EQ/NE/LIKE/IN/BETWEEN/GT/GE/LT/LE/IS_NULL/IS_NOT_NULL）、
 * 排序、分页以及 OR 分组条件。同一 {@code orGroup} 的条件用 OR 连接，
 * 不同分组（含未分组的 default 组）之间用 AND 连接。</p>
 */
@Data
public class DynamicQueryRequest {

    /** 实体编码（由 Controller 根据路径参数注入） */
    private String entityCode;

    /** 查询条件列表 */
    private List<QueryCondition> conditions = new ArrayList<>();

    /** 排序字段列表 */
    private List<OrderBy> orderBy = new ArrayList<>();

    /** 页码，从 1 开始 */
    private Integer page = 1;

    /** 每页条数 */
    private Integer size = 20;

    /**
     * 单个查询条件。
     */
    @Data
    public static class QueryCondition {
        /** 字段名（必须为实体合法字段，防 SQL 注入） */
        private String field;
        /** 操作符: EQ/NE/LIKE/IN/BETWEEN/GT/GE/LT/LE/IS_NULL/IS_NOT_NULL */
        private String operator;
        /** 比较值；IN 时为集合 */
        private Object value;
        /** BETWEEN 上界 */
        private Object value2;
        /** OR 分组名：相同分组内用 OR 连接，留空则归入 default 组用 AND 连接 */
        private String orGroup;
    }

    /**
     * 排序项。
     */
    @Data
    public static class OrderBy {
        /** 字段名 */
        private String field;
        /** 排序方向: ASC/DESC */
        private String direction;
    }
}
