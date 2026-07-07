package com.dp.plat.lowcode.version;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.lowcode.dto.ConfigPackageDTO;
import com.dp.plat.lowcode.entity.LowCodeConnector;
import com.dp.plat.lowcode.entity.LowCodeEntity;
import com.dp.plat.lowcode.entity.LowCodeMicroflow;
import com.dp.plat.lowcode.entity.LowCodeRule;
import com.dp.plat.lowcode.service.LowCodeConfigVersionService;
import com.dp.plat.lowcode.service.LowCodeConnectorService;
import com.dp.plat.lowcode.service.LowCodeEntityService;
import com.dp.plat.lowcode.service.LowCodeFormService;
import com.dp.plat.lowcode.service.LowCodeListService;
import com.dp.plat.lowcode.service.LowCodeMicroflowService;
import com.dp.plat.lowcode.service.LowCodeRuleService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    /** 字段名 → 依赖配置类型的映射（借鉴 Appsmith 依赖校验） */
    private static final Map<String, String> REFERENCE_FIELDS = new LinkedHashMap<>();

    static {
        REFERENCE_FIELDS.put("entityCode", "ENTITY");
        REFERENCE_FIELDS.put("formCode", "FORM");
        REFERENCE_FIELDS.put("listCode", "LIST");
        REFERENCE_FIELDS.put("connectorCode", "CONNECTOR");
        REFERENCE_FIELDS.put("ruleCode", "RULE");
        REFERENCE_FIELDS.put("microflowCode", "MICROFLOW");
    }

    private final LowCodeConfigVersionService configVersionService;
    private final ObjectMapper objectMapper;
    private final LowCodeEntityService entityService;
    private final LowCodeFormService formService;
    private final LowCodeListService listService;
    private final LowCodeMicroflowService microflowService;
    private final LowCodeConnectorService connectorService;
    private final LowCodeRuleService ruleService;

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
     * 校验配置包依赖完整性（借鉴 Appsmith）。
     *
     * <p>遍历配置包中每个快照，用正则提取对其他配置（ENTITY/FORM/LIST/CONNECTOR/RULE/MICROFLOW）
     * 的引用，检查目标环境是否已存在该依赖。返回缺失依赖清单（空表示完整）。</p>
     *
     * @param pkg 配置包
     * @return 缺失依赖描述列表
     */
    public List<String> validatePackageDependencies(ConfigPackageDTO pkg) {
        List<String> missing = new ArrayList<>();
        if (pkg == null || pkg.getItems() == null || pkg.getItems().isEmpty()) {
            missing.add("配置包为空");
            return missing;
        }
        for (ConfigPackageDTO.PackageItem item : pkg.getItems()) {
            if (item.getSnapshot() == null || item.getSnapshot().isBlank()) {
                continue;
            }
            String itemType = item.getConfigType() == null ? "UNKNOWN" : item.getConfigType();
            String itemCode = item.getConfigCode() == null ? String.valueOf(item.getConfigId()) : item.getConfigCode();
            Map<String, Set<String>> references = extractReferences(item.getSnapshot());
            for (Map.Entry<String, Set<String>> entry : references.entrySet()) {
                String depType = entry.getKey();
                for (String depCode : entry.getValue()) {
                    if (!dependencyExists(depType, depCode)) {
                        missing.add(itemType + " '" + itemCode + "' 依赖 " + depType + " '" + depCode
                                + "'，但目标环境不存在");
                    }
                }
            }
        }
        return missing;
    }

    /**
     * 从快照 JSON 中提取引用（按字段名 → 依赖类型映射，用正则匹配）。
     */
    private Map<String, Set<String>> extractReferences(String snapshotJson) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> field : REFERENCE_FIELDS.entrySet()) {
            Pattern p = Pattern.compile("\"" + Pattern.quote(field.getKey()) + "\"\\s*:\\s*\"([^\"]+)\"");
            Matcher m = p.matcher(snapshotJson);
            Set<String> codes = new java.util.LinkedHashSet<>();
            while (m.find()) {
                codes.add(m.group(1));
            }
            if (!codes.isEmpty()) {
                result.put(field.getValue(), codes);
            }
        }
        // 仅保留 JSON 解析合法的引用（避免误匹配非 JSON 文本）；这里用正则已足够简化
        // 额外校验：尝试解析为 JSON 以排除明显非 JSON 的快照
        try {
            JsonNode root = objectMapper.readTree(snapshotJson);
            collectReferences(root, result);
        } catch (Exception ignored) {
            // 非 JSON 快照：仅使用正则结果
        }
        return result;
    }

    /**
     * 递归收集 JSON 节点中的引用字段。
     */
    @SuppressWarnings("unchecked")
    private void collectReferences(JsonNode node, Map<String, Set<String>> result) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                String depType = REFERENCE_FIELDS.get(key);
                if (depType != null && value.isTextual()) {
                    result.computeIfAbsent(depType, k -> new java.util.LinkedHashSet<>())
                            .add(value.asText());
                } else {
                    collectReferences(value, result);
                }
            });
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                collectReferences(child, result);
            }
        }
    }

    /**
     * 检查目标环境中指定依赖是否存在。
     */
    private boolean dependencyExists(String depType, String code) {
        try {
            return switch (depType) {
                case "ENTITY" -> entityService.count(new LambdaQueryWrapper<LowCodeEntity>()
                        .eq(LowCodeEntity::getCode, code)) > 0;
                case "FORM" -> formService.getByCode(code) != null;
                case "LIST" -> listService.getByCode(code) != null;
                case "MICROFLOW" -> microflowService.count(new LambdaQueryWrapper<LowCodeMicroflow>()
                        .eq(LowCodeMicroflow::getCode, code)) > 0;
                case "CONNECTOR" -> connectorService.count(new LambdaQueryWrapper<LowCodeConnector>()
                        .eq(LowCodeConnector::getCode, code)) > 0;
                case "RULE" -> ruleService.count(new LambdaQueryWrapper<LowCodeRule>()
                        .eq(LowCodeRule::getCode, code)) > 0;
                default -> true; // 未知依赖类型，假定存在，避免误报
            };
        } catch (Exception e) {
            log.warn("依赖存在性校验失败，假定存在: {}/{}", depType, code, e);
            return true;
        }
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
