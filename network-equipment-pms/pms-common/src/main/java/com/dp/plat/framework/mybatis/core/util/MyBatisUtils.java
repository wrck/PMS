package com.dp.plat.framework.mybatis.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dp.plat.framework.common.pojo.PageParam;
import com.dp.plat.framework.common.pojo.SortingField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * MyBatis 工具类
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-mybatis），
 * 简化移除了 JdbcUtils/DbTypeEnum/jsqlparser 等数据权限相关依赖，
 * 仅保留分页构建 {@link #buildPage} 与排序拼接 {@link #addOrder}。
 *
 * @author yudao
 */
public class MyBatisUtils {

    private static final Pattern SAFE_COLUMN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*$");

    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        // 页码 + 数量
        Page<T> page = new Page<>(pageParam.getPageNo(), pageParam.getPageSize());
        page.setOptimizeJoinOfCountSql(false);
        // 排序字段
        if (CollUtil.isNotEmpty(sortingFields)) {
            for (SortingField sortingField : sortingFields) {
                String columnName = buildSafeOrderColumn(sortingField.getField());
                if (columnName == null) {
                    continue;
                }
                page.addOrder(new OrderItem().setAsc(isAscOrder(sortingField.getOrder())).setColumn(columnName));
            }
        }
        return page;
    }

    @SuppressWarnings("PatternVariableCanBeUsed")
    public static <T> void addOrder(Wrapper<T> wrapper, Collection<SortingField> sortingFields) {
        if (CollUtil.isEmpty(sortingFields)) {
            return;
        }
        if (wrapper instanceof QueryWrapper<T>) {
            QueryWrapper<T> query = (QueryWrapper<T>) wrapper;
            for (SortingField sortingField : sortingFields) {
                String columnName = buildSafeOrderColumn(sortingField.getField());
                if (columnName == null) {
                    continue;
                }
                query.orderBy(true, isAscOrder(sortingField.getOrder()), columnName);
            }
        } else if (wrapper instanceof LambdaQueryWrapper<T>) {
            LambdaQueryWrapper<T> lambdaQuery = (LambdaQueryWrapper<T>) wrapper;
            StringBuilder orderBy = new StringBuilder();
            for (SortingField sortingField : sortingFields) {
                String columnName = buildSafeOrderColumn(sortingField.getField());
                if (columnName == null) {
                    continue;
                }
                if (StrUtil.isNotEmpty(orderBy)) {
                    orderBy.append(", ");
                }
                orderBy.append(columnName).append(" ").append(getOrderDirection(sortingField.getOrder()));
            }
            if (StrUtil.isNotEmpty(orderBy)) {
                lambdaQuery.last("ORDER BY " + orderBy);
            }
        } else {
            throw new IllegalArgumentException("Unsupported wrapper type: " + wrapper.getClass().getName());
        }
    }

    public static boolean isAscOrder(String order) {
        return SortingField.ORDER_ASC.equals(order);
    }

    public static String getOrderDirection(String order) {
        return isAscOrder(order) ? "ASC" : "DESC";
    }

    private static String buildSafeOrderColumn(String field) {
        String columnName = StrUtil.toUnderlineCase(field);
        if (StrUtil.isEmpty(columnName) || !SAFE_COLUMN_NAME_PATTERN.matcher(columnName).matches()) {
            return null;
        }
        return columnName;
    }

}
