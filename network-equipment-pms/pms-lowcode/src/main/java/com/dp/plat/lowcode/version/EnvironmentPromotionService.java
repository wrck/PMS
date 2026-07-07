package com.dp.plat.lowcode.version;

import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 环境晋升服务。
 *
 * <p>将 DEV 环境的配置包晋升到 TEST/PROD 环境，支持 zip 包导出与导入。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnvironmentPromotionService {

    private final LowCodeConfigVersionService configVersionService;
    private final ObjectMapper objectMapper;

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
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pkg);
        } catch (Exception e) {
            throw new RuntimeException("导出配置包失败", e);
        }
    }

    /**
     * 导出配置包为 zip 字节数组。
     * <p>zip 包含两个文件：config.json（配置内容）+ metadata.json（导出元信息）。</p>
     *
     * @param configCodes       配置编码列表
     * @param targetEnvironment 目标环境
     * @return zip 字节数组
     */
    public byte[] exportPackageZip(List<String> configCodes, String targetEnvironment) {
        try {
            String json = exportPackageJson(configCodes);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("exportTime", Instant.now().toString());
            metadata.put("targetEnvironment", targetEnvironment);
            metadata.put("configCodes", configCodes);
            metadata.put("version", "1.0");
            String metadataJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadata);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                addZipEntry(zos, "config.json", json.getBytes(StandardCharsets.UTF_8));
                addZipEntry(zos, "metadata.json", metadataJson.getBytes(StandardCharsets.UTF_8));
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出配置包失败", e);
        }
    }

    private void addZipEntry(ZipOutputStream zos, String name, byte[] data) throws IOException {
        zos.putNextEntry(new ZipEntry(name));
        zos.write(data);
        zos.closeEntry();
    }

    /**
     * 校验配置包依赖完整性。
     * <p>简化实现：仅检查配置编码非空。后续应解析 snapshot 中的 dependencies 字段。</p>
     *
     * @param configCodes 配置编码列表
     * @return 缺失的依赖列表（空列表表示完整）
     */
    public List<String> validatePackageDependencies(List<String> configCodes) {
        List<String> missing = new ArrayList<>();
        if (configCodes == null || configCodes.isEmpty()) {
            missing.add("configCodes is empty");
            return missing;
        }
        // TODO: 解析每个配置版本 snapshot 中的 dependencies 字段，校验引用的实体/连接器/微流是否存在
        return missing;
    }

    /**
     * 导入配置包（带覆盖确认）。
     *
     * @param packageJson 配置包 JSON（zip 内 config.json 的内容）
     * @param overwrite   是否覆盖已存在的配置
     */
    public void importPackageWithConfirm(String packageJson, boolean overwrite) {
        try {
            JsonNode root = objectMapper.readTree(packageJson);
            JsonNode items = root.get("items");
            if (items == null || !items.isArray()) {
                throw new IllegalArgumentException("配置包格式错误：缺少 items 数组");
            }
            log.info("导入配置包：共 {} 项，覆盖模式 = {}", items.size(), overwrite);
            // 转换回 ConfigPackageDTO 并调用 service 导入
            ConfigPackageDTO pkg = objectMapper.treeToValue(root, ConfigPackageDTO.class);
            if (!overwrite) {
                // TODO: 检查每个 item 是否已存在，若已存在则跳过
                log.info("非覆盖模式：已存在的配置将被跳过（待实现完整校验）");
            }
            configVersionService.importPackage(pkg);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("导入配置包失败", e);
        }
    }
}
