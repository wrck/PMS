package com.dp.plat.lowcode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.lowcode.engine.connector.ConnectorResult;
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
 * <p>注：执行器在 Task 3/4 引入后注入，此处先返回未实现错误以保证编译通过。</p>
 */
@Service
@RequiredArgsConstructor
public class LowCodeConnectorServiceImpl extends ServiceImpl<LowCodeConnectorMapper, LowCodeConnector>
        implements LowCodeConnectorService {

    @Override
    public ConnectorResult execute(String code, Map<String, Object> params) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        return ConnectorResult.error(501, "执行器尚未注入：等待 Task 3/4 实现");
    }

    @Override
    public ConnectorResult test(String code) {
        LowCodeConnector connector = getOne(new LambdaQueryWrapper<LowCodeConnector>()
                .eq(LowCodeConnector::getCode, code));
        if (connector == null) {
            throw new RuntimeException("连接器不存在: " + code);
        }
        return ConnectorResult.error(501, "执行器尚未注入：等待 Task 3/4 实现");
    }
}
