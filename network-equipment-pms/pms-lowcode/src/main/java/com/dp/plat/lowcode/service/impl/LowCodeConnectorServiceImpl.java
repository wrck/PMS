package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.apm.LowCodeApmService;
import com.dp.plat.lowcode.engine.connector.ConnectorCredentialEncryptor;
import com.dp.plat.lowcode.engine.connector.ConnectorResult;
import com.dp.plat.lowcode.engine.connector.DbConnectorExecutor;
import com.dp.plat.lowcode.engine.connector.FileConnectorExecutor;
import com.dp.plat.lowcode.engine.connector.MqConnectorExecutor;
import com.dp.plat.lowcode.engine.connector.RestConnectorExecutor;
import com.dp.plat.lowcode.entity.LowCodeConnector;
import com.dp.plat.lowcode.mapper.LowCodeConnectorMapper;
import com.dp.plat.lowcode.service.LowCodeConnectorService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 低代码连接器服务实现。
 *
 * <p>根据 type 分发到对应执行器：REST / DB / MQ / FILE。
 * 配置中的敏感字段（password/token/apiKey/secret/clientSecret）
 * 在持久化前由 {@link ConnectorCredentialEncryptor} 以 AES-GCM 加密，执行/测试前解密。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowCodeConnectorServiceImpl extends ServiceImpl<LowCodeConnectorMapper, LowCodeConnector>
        implements LowCodeConnectorService {

    private final RestConnectorExecutor restConnectorExecutor;
    private final DbConnectorExecutor dbConnectorExecutor;
    private final MqConnectorExecutor mqConnectorExecutor;
    private final FileConnectorExecutor fileConnectorExecutor;
    private final ConnectorCredentialEncryptor credentialEncryptor;
    private final ObjectMapper objectMapper;
    private final LowCodeApmService apmService;

    @Override
    public boolean save(LowCodeConnector connector) {
        encryptConfigInPlace(connector);
        return super.save(connector);
    }

    @Override
    public boolean saveOrUpdate(LowCodeConnector connector) {
        encryptConfigInPlace(connector);
        return super.saveOrUpdate(connector);
    }

    @Override
    public boolean updateById(LowCodeConnector connector) {
        encryptConfigInPlace(connector);
        return super.updateById(connector);
    }

    /**
     * 持久化前对 config 中敏感字段加密（幂等：已加密的密文不会被重复加密）。
     */
    private void encryptConfigInPlace(LowCodeConnector connector) {
        if (connector == null || connector.getConfig() == null) {
            return;
        }
        connector.setConfig(credentialEncryptor.encryptConfig(connector.getConfig()));
    }

    @Override
    public ConnectorResult execute(String code, Map<String, Object> params) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        String decryptedConfig = credentialEncryptor.decryptConfig(connector.getConfig());
        long apmStart = System.currentTimeMillis();
        ConnectorResult result;
        try {
            result = switch (connector.getType()) {
                case "REST" -> restConnectorExecutor.execute(decryptedConfig, params);
                case "DB" -> dbConnectorExecutor.execute(decryptedConfig, params);
                case "MQ" -> mqConnectorExecutor.execute(decryptedConfig, params);
                case "FILE" -> fileConnectorExecutor.execute(decryptedConfig, params);
                default -> ConnectorResult.error(400, "未知连接器类型: " + connector.getType());
            };
        } catch (Exception e) {
            apmService.recordConnectorCall(connector.getType(), "FAILED",
                    System.currentTimeMillis() - apmStart);
            throw e;
        }
        apmService.recordConnectorCall(connector.getType(),
                result.isSuccess() ? "SUCCESS" : "ERROR",
                System.currentTimeMillis() - apmStart);
        return result;
    }

    @Override
    public ConnectorResult test(String code) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        String decryptedConfig = credentialEncryptor.decryptConfig(connector.getConfig());
        return switch (connector.getType()) {
            case "REST" -> restConnectorExecutor.execute(decryptedConfig, Map.of());
            case "DB" -> dbConnectorExecutor.execute(decryptedConfig, Map.of());
            case "MQ" -> mqConnectorExecutor.execute(decryptedConfig, Map.of());
            case "FILE" -> fileConnectorExecutor.execute(decryptedConfig, Map.of());
            default -> ConnectorResult.error(400, "未知连接器类型: " + connector.getType());
        };
    }

    @Override
    public ConnectorResult testOperation(String code, String operationName, Map<String, Object> params) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        String decryptedConfig = credentialEncryptor.decryptConfig(connector.getConfig());
        if (operationName == null || operationName.isBlank()) {
            // 无操作名：直接执行整个 config
            return executeByType(connector.getType(), decryptedConfig, params == null ? Map.of() : params);
        }

        // 从 config 中按 operationName 查找操作配置，合并到顶层 config 后执行
        try {
            Map<String, Object> configMap = objectMapper.readValue(decryptedConfig, new TypeReference<>() {});
            List<Map<String, Object>> operations = (List<Map<String, Object>>) configMap.get("operations");
            if (operations != null) {
                for (Map<String, Object> op : operations) {
                    if (operationName.equals(op.get("name"))) {
                        // 合并操作级配置到顶层 config（操作级覆盖顶层）
                        Map<String, Object> merged = new HashMap<>(configMap);
                        merged.putAll(op);
                        merged.remove("operations"); // 避免嵌套
                        merged.remove("name");
                        String mergedJson = objectMapper.writeValueAsString(merged);
                        return executeByType(connector.getType(), mergedJson,
                                params == null ? Map.of() : params);
                    }
                }
                return ConnectorResult.error(404, "操作不存在: " + operationName);
            }
            // 无 operations 数组：直接执行
            return executeByType(connector.getType(), decryptedConfig, params == null ? Map.of() : params);
        } catch (Exception e) {
            log.error("测试操作失败: code={}, operation={}", code, operationName, e);
            return ConnectorResult.error(500, "测试操作失败: " + e.getMessage());
        }
    }

    private ConnectorResult executeByType(String type, String config, Map<String, Object> params) {
        return switch (type.toUpperCase()) {
            case "REST" -> restConnectorExecutor.execute(config, params);
            case "DB" -> dbConnectorExecutor.execute(config, params);
            case "MQ" -> mqConnectorExecutor.execute(config, params);
            case "FILE" -> fileConnectorExecutor.execute(config, params);
            default -> ConnectorResult.error(400, "未知连接器类型: " + type);
        };
    }
}
