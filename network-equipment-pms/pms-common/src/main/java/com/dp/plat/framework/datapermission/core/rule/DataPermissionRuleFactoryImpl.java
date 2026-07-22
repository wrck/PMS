package com.dp.plat.framework.datapermission.core.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.dp.plat.framework.datapermission.core.annotation.DataPermission;
import com.dp.plat.framework.datapermission.core.aop.DataPermissionContextHolder;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的 {@link DataPermissionRuleFactory} 实现
 *
 * <p>直接复用自 yudao-framework（yudao-spring-boot-starter-biz-data-permission）。
 *
 * <p><b>调整说明</b>：原 yudao 实现内含 {@code isTranslateCall()} 检测 easy-trans
 * {@code SimpleTransService} 调用栈，以避免数据翻译时触发数据权限。本仓库未引入
 * easy-trans 依赖，已移除该检测分支。
 *
 * @author yudao
 */
@RequiredArgsConstructor
public class DataPermissionRuleFactoryImpl implements DataPermissionRuleFactory {

    /**
     * 数据权限规则数组
     */
    private final List<DataPermissionRule> rules;

    @Override
    public List<DataPermissionRule> getDataPermissionRules() {
        return rules;
    }

    @Override // mappedStatementId 参数，暂时没有用。以后，可以基于 mappedStatementId + DataPermission 进行缓存
    public List<DataPermissionRule> getDataPermissionRule(String mappedStatementId) {
        // 1.1 无数据权限
        if (CollUtil.isEmpty(rules)) {
            return Collections.emptyList();
        }
        // 1.2 未配置，则默认开启
        DataPermission dataPermission = DataPermissionContextHolder.get();
        if (dataPermission == null) {
            return rules;
        }
        // 1.3 已配置，但禁用
        if (!dataPermission.enable()) {
            return Collections.emptyList();
        }

        // 2.1 情况一：已配置，只选择部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.includeRules())) {
            return rules.stream().filter(rule -> ArrayUtil.contains(dataPermission.includeRules(), rule.getClass()))
                    .collect(Collectors.toList());
        }
        // 2.2 已配置，只排除部分规则
        if (ArrayUtil.isNotEmpty(dataPermission.excludeRules())) {
            return rules.stream().filter(rule -> !ArrayUtil.contains(dataPermission.excludeRules(), rule.getClass()))
                    .collect(Collectors.toList());
        }
        // 2.3 已配置，全部规则
        return rules;
    }

}
