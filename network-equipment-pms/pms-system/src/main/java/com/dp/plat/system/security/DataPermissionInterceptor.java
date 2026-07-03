package com.dp.plat.system.security;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.dp.plat.common.annotation.DataScope;
import com.dp.plat.common.constant.CommonConstants;
import com.dp.plat.common.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * MyBatis-Plus inner interceptor for data permission filtering.
 *
 * <p>Simplified implementation: for queries whose Mapper method carries the
 * {@link DataScope} annotation, restrict results to rows created by the current
 * user (i.e. append {@code create_by = 'username'} to the WHERE clause).
 * Administrators (users with the {@code admin} authority) bypass filtering.</p>
 */
@Slf4j
@Component
public class DataPermissionInterceptor implements InnerInterceptor {

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler,
                            BoundSql boundSql) throws SQLException {
        DataScope dataScope = resolveDataScope(ms.getId());
        if (dataScope == null) {
            return;
        }
        String username = SecurityUtils.getCurrentUsername();
        // No filter for unauthenticated/system context or admin users.
        if (username == null || username.isBlank()
                || CommonConstants.SUPER_ADMIN_ROLE.equals(username)
                || "system".equals(username)
                || isAdmin()) {
            return;
        }
        String originalSql = boundSql.getSql();
        try {
            Statement statement = CCJSqlParserUtil.parse(originalSql);
            if (!(statement instanceof Select select)) {
                return;
            }
            if (!(select.getSelectBody() instanceof PlainSelect plainSelect)) {
                return;
            }
            Expression cond = new EqualsTo(new Column("create_by"), new StringValue(username));
            Expression where = plainSelect.getWhere();
            plainSelect.setWhere(where == null ? cond : new AndExpression(where, cond));
            PluginUtils.mpBoundSql(boundSql).sql(select.toString());
        } catch (Exception e) {
            log.warn("Failed to apply data-scope filter on SQL: {}", e.getMessage());
        }
    }

    private DataScope resolveDataScope(String mappedStatementId) {
        if (mappedStatementId == null) {
            return null;
        }
        int lastDot = mappedStatementId.lastIndexOf('.');
        if (lastDot < 0) {
            return null;
        }
        String className = mappedStatementId.substring(0, lastDot);
        String methodName = mappedStatementId.substring(lastDot + 1);
        try {
            Class<?> mapperClass = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            for (Method method : mapperClass.getMethods()) {
                if (method.getName().equals(methodName)) {
                    DataScope ds = method.getAnnotation(DataScope.class);
                    if (ds != null) {
                        return ds;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            return null;
        }
        return null;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityUtils.getAuthentication();
        if (auth == null) {
            return false;
        }
        for (GrantedAuthority authority : auth.getAuthorities()) {
            if (CommonConstants.SUPER_ADMIN_ROLE.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
