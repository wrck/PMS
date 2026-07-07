package com.dp.plat.lowcode.version;

import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 环境晋升服务。
 *
 * <p>将 DEV 环境的配置包晋升到 TEST/PROD 环境。</p>
 */
@Service
@RequiredArgsConstructor
public class EnvironmentPromotionService {

    private final LowCodeConfigVersionService configVersionService;

    /**
     * 晋升配置到目标环境。
     */
    public void promote(String targetEnvironment, List<String> configCodes) {
        ConfigPackageDTO pkg = configVersionService.exportPackage("DEV", configCodes);
        pkg.setTargetEnvironment(targetEnvironment);
        configVersionService.importPackage(pkg);
    }

    /**
     * 导出配置包（JSON 字符串，便于下载）。
     */
    public String exportPackageJson(List<String> configCodes) {
        ConfigPackageDTO pkg = configVersionService.exportPackage("DEV", configCodes);
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(pkg);
        } catch (Exception e) {
            throw new RuntimeException("导出配置包失败", e);
        }
    }
}
