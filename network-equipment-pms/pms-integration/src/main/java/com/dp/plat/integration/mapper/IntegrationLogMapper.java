package com.dp.plat.integration.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.integration.entity.IntegrationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link IntegrationLog}.
 */
@Mapper
public interface IntegrationLogMapper extends BaseMapper<IntegrationLog> {
}
