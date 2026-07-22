package com.dp.plat.framework.datapermission.core.rule.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dp.plat.framework.common.biz.system.permission.PermissionCommonApi;
import com.dp.plat.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import com.dp.plat.framework.common.enums.UserTypeEnum;
import com.dp.plat.framework.common.util.json.JsonUtils;
import com.dp.plat.framework.datapermission.core.rule.DataPermissionRule;
import com.dp.plat.framework.mybatis.core.dataobject.BaseDO;
import com.dp.plat.framework.mybatis.core.util.MyBatisUtils;
import com.dp.plat.framework.security.core.LoginUser;
import com.dp.plat.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于部门的 {@link DataPermissionRule} 数据权限规则实现
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * <p><b>调整说明</b>：
 * <ul>
 *   <li>原 yudao 实现通过 {@code CollectionUtils.convertList(deptIds, LongValue::new)}
 *       将部门编号集合转为 {@code List<LongValue>}；本仓库未引入 yudao 的 CollectionUtils 工具类，
 *       改用内联的 {@code deptIds.stream().map(LongValue::new).collect(Collectors.toList())} 等价实现。</li>
 *   <li>原 yudao 实现使用 jsqlparser 4.9+ 的 {@code ParenthesedExpressionList} 包装
 *       {@code InExpression} 的值列表与 {@code OrExpression}；本仓库 jsqlparser 版本为 4.6，
 *       该类尚不存在。改用 {@link ExpressionList}（{@code InExpression} 会自动渲染括号）和
 *       {@link Parenthesis}（显式包装 OR 表达式）等价实现。</li>
 * </ul>
 *
 * <p>注意，使用 {@code DeptDataPermissionRule} 时，需要保证表中有 {@code dept_id} 部门编号的字段，可自定义。
 *
 * @author yudao
 */
@AllArgsConstructor
@Slf4j
public class DeptDataPermissionRule implements DataPermissionRule {

    /**
     * LoginUser 的 Context 缓存 Key
     */
    protected static final String CONTEXT_KEY = DeptDataPermissionRule.class.getSimpleName();

    private static final String DEPT_COLUMN_NAME = "dept_id";
    private static final String USER_COLUMN_NAME = "user_id";

    private final PermissionCommonApi permissionApi;

    /**
     * 基于部门的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     *
     * key：表名
     * value：字段名
     */
    private final Map<String, String> deptColumns = new HashMap<>();
    /**
     * 基于用户的表字段配置
     * 一般情况下，每个表的部门编号字段是 dept_id，通过该配置自定义。
     *
     * key：表名
     * value：字段名
     */
    private final Map<String, String> userColumns = new HashMap<>();
    /**
     * 所有表名，是 {@link #deptColumns} 和 {@link #userColumns} 的合集
     */
    private final Set<String> TABLE_NAMES = new HashSet<>();

    @Override
    public Set<String> getTableNames() {
        return TABLE_NAMES;
    }

    @Override
    public Expression getExpression(String tableName, Alias tableAlias) {
        // 只有有登陆用户的情况下，才进行数据权限的处理
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // 只有管理员类型的用户，才进行数据权限的处理
        if (ObjectUtil.notEqual(loginUser.getUserType(), UserTypeEnum.ADMIN.getValue())) {
            return null;
        }

        // 获得数据权限
        DeptDataPermissionRespDTO deptDataPermission = loginUser.getContext(CONTEXT_KEY, DeptDataPermissionRespDTO.class);
        // 从上下文中拿不到，则调用逻辑进行获取
        if (deptDataPermission == null) {
            deptDataPermission = permissionApi.getDeptDataPermission(loginUser.getId());
            if (deptDataPermission == null) {
                log.error("[getExpression][LoginUser({}) 获取数据权限为 null]", JsonUtils.toJsonString(loginUser));
                throw new NullPointerException(String.format("LoginUser(%d) Table(%s/%s) 未返回数据权限",
                        loginUser.getId(), tableName, tableAlias.getName()));
            }
            // 添加到上下文中，避免重复计算
            loginUser.setContext(CONTEXT_KEY, deptDataPermission);
        }

        // 情况一，如果是 ALL 可查看全部，则无需拼接条件
        if (deptDataPermission.getAll()) {
            return null;
        }

        // 情况二，即不能查看部门，又不能查看自己，则说明 100% 无权限
        if (CollUtil.isEmpty(deptDataPermission.getDeptIds())
            && Boolean.FALSE.equals(deptDataPermission.getSelf())) {
            return new EqualsTo(null, null); // WHERE null = null，可以保证返回的数据为空
        }

        // 情况三，拼接 Dept 和 User 的条件，最后组合
        Expression deptExpression = buildDeptExpression(tableName, tableAlias, deptDataPermission.getDeptIds());
        Expression userExpression = buildUserExpression(tableName, tableAlias, deptDataPermission.getSelf(), loginUser.getId());
        if (deptExpression == null && userExpression == null) {
            // TODO 芋艿：获得不到条件的时候，暂时不抛出异常，而是不返回数据
            log.warn("[getExpression][LoginUser({}) Table({}/{}) DeptDataPermission({}) 构建的条件为空]",
                    JsonUtils.toJsonString(loginUser), tableName, tableAlias, JsonUtils.toJsonString(deptDataPermission));
            return new EqualsTo(null, null); // WHERE null = null，可以保证返回的数据为空
        }
        if (deptExpression == null) {
            return userExpression;
        }
        if (userExpression == null) {
            return deptExpression;
        }
        // 目前，如果有指定部门 + 可查看自己，采用 OR 条件。即，WHERE (dept_id IN ? OR user_id = ?)
        // 使用 Parenthesis 显式包装，保证 OR 子句作为一个整体参与外层 AND 拼接
        return new Parenthesis(new OrExpression(deptExpression, userExpression));
    }

    private Expression buildDeptExpression(String tableName, Alias tableAlias, Set<Long> deptIds) {
        // 如果不存在配置，则无需作为条件
        String columnName = deptColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        // 如果为空，则无条件
        if (CollUtil.isEmpty(deptIds)) {
            return null;
        }
        // 拼接条件：InExpression 会自动在 ItemsList 外渲染括号，即 column IN (1, 2, 3)
        return new InExpression(MyBatisUtils.buildColumn(tableName, tableAlias, columnName),
                new ExpressionList(deptIds.stream().map(LongValue::new).collect(Collectors.toList())));
    }

    private Expression buildUserExpression(String tableName, Alias tableAlias, Boolean self, Long userId) {
        // 如果不查看自己，则无需作为条件
        if (Boolean.FALSE.equals(self)) {
            return null;
        }
        String columnName = userColumns.get(tableName);
        if (StrUtil.isEmpty(columnName)) {
            return null;
        }
        // 拼接条件
        return new EqualsTo(MyBatisUtils.buildColumn(tableName, tableAlias, columnName), new LongValue(userId));
    }

    // ==================== 添加配置 ====================

    public void addDeptColumn(Class<? extends BaseDO> entityClass) {
        addDeptColumn(entityClass, DEPT_COLUMN_NAME);
    }

    public void addDeptColumn(Class<? extends BaseDO> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addDeptColumn(tableName, columnName);
    }

    public void addDeptColumn(String tableName, String columnName) {
        deptColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

    public void addUserColumn(Class<? extends BaseDO> entityClass) {
        addUserColumn(entityClass, USER_COLUMN_NAME);
    }

    public void addUserColumn(Class<? extends BaseDO> entityClass, String columnName) {
        String tableName = TableInfoHelper.getTableInfo(entityClass).getTableName();
        addUserColumn(tableName, columnName);
    }

    public void addUserColumn(String tableName, String columnName) {
        userColumns.put(tableName, columnName);
        TABLE_NAMES.add(tableName);
    }

}
