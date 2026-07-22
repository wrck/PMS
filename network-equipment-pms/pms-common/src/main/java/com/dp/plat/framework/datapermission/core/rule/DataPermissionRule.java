package com.dp.plat.framework.datapermission.core.rule;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;

import java.util.Set;

/**
 * 数据权限规则接口
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 * 实现 {@link #getTableNames()} 返回该规则关心的表名集合，{@link #getExpression(String, Alias)}
 * 返回要拼接到 WHERE 子句的 SQL 条件表达式。
 *
 * @author yudao
 */
public interface DataPermissionRule {

    /**
     * 返回当前规则生效的表名集合
     *
     * @return 表名集合
     */
    Set<String> getTableNames();

    /**
     * 基于表名生成 SQL 条件表达式
     *
     * @param tableName 表名
     * @param tableAlias 表别名
     * @return SQL 条件表达式。{@code null} 表示无需追加条件
     */
    Expression getExpression(String tableName, Alias tableAlias);

}
