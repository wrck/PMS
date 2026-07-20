package com.dp.plat.project.spi;

import com.dp.plat.common.spi.BusinessDataLoader;
import com.dp.plat.project.entity.Project;
import com.dp.plat.project.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目业务数据加载器（TD-P8-001）。
 *
 * <p>实现 {@link BusinessDataLoader} SPI，供审批中心加载 PROJECT 类型审批的业务字段用于脱敏展示。
 * 原 {@code pms-workflow} 模块通过直接依赖 {@code pms-project} 加载项目数据形成双向依赖环，
 * 现下沉到 {@code pms-common} 接口 + 本实现类打破环依赖。</p>
 *
 * <p>支持的审批类型：{@code PROJECT}（项目审批）。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectBusinessDataLoader implements BusinessDataLoader {

    /** 本加载器支持的审批类型 */
    public static final String SUPPORTED_TYPE = "PROJECT";

    private final ProjectMapper projectMapper;

    @Override
    public Map<String, Object> load(String approvalType, Long businessId) {
        if (businessId == null) {
            return new HashMap<>();
        }
        Project project = projectMapper.selectById(businessId);
        if (project == null) {
            log.warn("PROJECT 业务数据加载：项目不存在 businessId={}", businessId);
            return new HashMap<>();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("projectId", project.getId());
        data.put("projectCode", project.getProjectCode());
        data.put("projectName", project.getProjectName());
        data.put("projectType", project.getProjectType());
        data.put("status", project.getStatus());
        data.put("customerName", project.getCustomerName());
        data.put("customerContact", project.getCustomerContact());
        data.put("customerPhone", project.getCustomerPhone());
        data.put("contractNo", project.getContractNo());
        data.put("contractAmount", project.getContractAmount());
        data.put("planStartDate", project.getPlanStartDate());
        data.put("planEndDate", project.getPlanEndDate());
        data.put("projectManagerId", project.getProjectManagerId());
        data.put("projectManagerName", project.getProjectManagerName());
        data.put("description", project.getDescription());
        data.put("progress", project.getProgress());
        data.put("priority", project.getPriority());
        return data;
    }

    @Override
    public String supportedType() {
        return SUPPORTED_TYPE;
    }
}
