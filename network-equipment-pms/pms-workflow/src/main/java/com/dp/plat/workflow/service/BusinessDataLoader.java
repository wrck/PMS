package com.dp.plat.workflow.service;

import java.util.Map;

/**
 * 业务数据加载器（Story 6）。
 *
 * <p>审批详情接口需要按审批类型加载对应业务对象的字段用于脱敏展示。本接口为扩展点，
 * 各业务模块（交付件/风险/变更/项目等）可实现各自的加载器并注册为 Spring Bean。</p>
 *
 * <p>关联设计文档：§3.5 Story 6 验收 1（行 458 {@code businessLoader.load}）。</p>
 */
public interface BusinessDataLoader {

    /**
     * 加载业务对象为字段 Map。
     *
     * @param approvalType 审批类型（PROJECT/TASK/DELIVERABLE/RISK/ISSUE/CHANGE/...）
     * @param businessId   业务对象ID
     * @return 字段 Map（fieldName → value），无数据返回空 Map
     */
    Map<String, Object> load(String approvalType, Long businessId);

    /**
     * 本加载器支持的审批类型（用于路由匹配）。
     */
    String supportedType();
}
