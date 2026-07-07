package com.dp.plat.lowcode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.lowcode.engine.connector.ConnectorResult;
import com.dp.plat.lowcode.entity.LowCodeConnector;

import java.util.Map;

/**
 * 低代码连接器服务。
 */
public interface LowCodeConnectorService extends IService<LowCodeConnector> {

    /**
     * 执行连接器。
     *
     * @param code   连接器编码
     * @param params 输入参数
     * @return 执行结果
     */
    ConnectorResult execute(String code, Map<String, Object> params);

    /**
     * 测试连接。
     *
     * @param code 连接器编码
     * @return 测试结果
     */
    ConnectorResult test(String code);
}
