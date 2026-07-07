package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.connector.ConnectorResult;
import com.dp.plat.lowcode.engine.connector.RestConnectorExecutor;
import com.dp.plat.lowcode.entity.LowCodeConnector;
import com.dp.plat.lowcode.mapper.LowCodeConnectorMapper;
import com.dp.plat.lowcode.service.LowCodeConnectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 低代码连接器服务实现。
 *
 * <p>根据 type 分发到对应执行器：REST / DB。</p>
 *
 * <p>注：DB 执行器在 Task 4 引入后注入，DB 分支当前返回未实现错误。</p>
 */
@Service
@RequiredArgsConstructor
public class LowCodeConnectorServiceImpl extends ServiceImpl<LowCodeConnectorMapper, LowCodeConnector>
        implements LowCodeConnectorService {

    private final RestConnectorExecutor restConnectorExecutor;

    @Override
    public ConnectorResult execute(String code, Map<String, Object> params) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        return switch (connector.getType()) {
            case "REST" -> restConnectorExecutor.execute(connector.getConfig(), params);
            case "DB" -> ConnectorResult.error(501, "DB 执行器尚未注入：等待 Task 4 实现");
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
        return switch (connector.getType()) {
            case "REST" -> restConnectorExecutor.execute(connector.getConfig(), Map.of());
            case "DB" -> ConnectorResult.error(501, "DB 执行器尚未注入：等待 Task 4 实现");
            default -> ConnectorResult.error(400, "未知连接器类型: " + connector.getType());
        };
    }
}
