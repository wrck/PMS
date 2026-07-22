package com.dp.plat.framework.mybatis.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.dp.plat.framework.common.pojo.PageParam;
import com.dp.plat.framework.common.pojo.SortingField;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * MyBatis 工具类
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-mybatis）。
 *
 * <p><b>调整说明</b>：移除了原 yudao 实现中的
 * {@code JdbcUtils.getDbType()} / {@code DbTypeEnum} / {@code findInSet(dbType, ...)}
 * 等跨数据库支持（本仓库当前仅 MySQL），以及 {@code toUnderlineCase(Func1)} 等 Hutool Lambda 依赖。
 * 保留分页构建 {@link #buildPage}、排序拼接 {@link #addOrder}、
 * 数据权限所需的 {@link #getTableName(Table)} / {@link #buildColumn(String, Alias, String)} /
 * {@link #addInterceptor(MybatisPlusInterceptor, InnerInterceptor, int)}。
 *
 * @author yudao
 */
public class MyBatisUtils {

    private static final String MYSQL_ESCAPE_CHARACTER = "`";

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

    /**
     * 将拦截器添加到链中
     *
     * <p>由于 {@link MybatisPlusInterceptor} 不支持按位置添加拦截器，所以只能全量重设。
     *
     * @param interceptor 链
     * @param inner       拦截器
     * @param index       位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        inners.add(index, inner);
        interceptor.setInterceptors(inners);
    }

    /**
     * 获得 Table 对应的表名
     *
     * <p>兼容 MySQL 转义表名 {@code `t_xxx`}
     *
     * @param table 表
     * @return 去除转义字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构建 Column 对象
     *
     * @param tableName  表名
     * @param tableAlias 别名
     * @param column     字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            tableName = tableAlias.getName();
        }
        return new Column(tableName + StringPool.DOT + column);
    }

}
