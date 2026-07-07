package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.connector.ConnectorResult;
import com.dp.plat.lowcode.engine.connector.DbConnectorExecutor;
import com.dp.plat.lowcode.engine.connector.RestConnectorExecutor;
import com.dp.plat.lowcode.entity.LowCodeConnector;
import com.dp.plat.lowcode.mapper.LowCodeConnectorMapper;
import com.dp.plat.lowcode.service.LowCodeConnectorService;
import com.dp.plat.lowcode.util.CredentialEncryptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 低代码连接器服务实现。
 *
 * <p>根据 type 分发到对应执行器：REST / DB。
 * 配置中的敏感字段（password/credentials/token/apiKey/secret/clientSecret）
 * 在持久化前由 {@link CredentialEncryptor} 加密，执行/测试前解密。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LowCodeConnectorServiceImpl extends ServiceImpl<LowCodeConnectorMapper, LowCodeConnector>
        implements LowCodeConnectorService {

    private final RestConnectorExecutor restConnectorExecutor;
    private final DbConnectorExecutor dbConnectorExecutor;
    private final CredentialEncryptor credentialEncryptor;

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
        return switch (connector.getType()) {
            case "REST" -> restConnectorExecutor.execute(decryptedConfig, params);
            case "DB" -> dbConnectorExecutor.execute(decryptedConfig, params);
            default -> ConnectorResult.error(400, "未知连接器类型: " + connector.getType());
        };
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
            default -> ConnectorResult.error(400, "未知连接器类型: " + connector.getType());
        };
    }
}
