package com.dp.plat.project.spi;

import com.dp.plat.common.spi.ProjectConfigProvider;
import com.dp.plat.project.service.ProjectConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 项目配置读取 SPI 实现（TD-P8-001）。
 *
 * <p>实现 {@link ProjectConfigProvider} SPI，桥接到 {@link ProjectConfigService}。
 * 使 {@code pms-workflow} 等基础设施模块无需直接依赖 {@code pms-project} 即可读取项目级配置。</p>
 */
@Component
@RequiredArgsConstructor
public class ProjectConfigProviderImpl implements ProjectConfigProvider {

    private final ProjectConfigService projectConfigService;

    @Override
    public String get(Long projectId, Long templateId, String key) {
        return projectConfigService.get(projectId, templateId, key);
    }
}
