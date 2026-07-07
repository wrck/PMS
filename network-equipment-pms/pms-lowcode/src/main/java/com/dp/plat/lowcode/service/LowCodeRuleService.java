package com.dp.plat.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.lowcode.entity.LowCodeRule;

import java.util.Map;

/**
 * 低代码规则服务。
 */
public interface LowCodeRuleService extends IService<LowCodeRule> {

    /**
     * 执行规则。
     *
     * @param code  规则编码
     * @param facts 输入事实
     * @return 执行结果（含 actions 或 result）
     */
    Map<String, Object> execute(String code, Map<String, Object> facts);
}
