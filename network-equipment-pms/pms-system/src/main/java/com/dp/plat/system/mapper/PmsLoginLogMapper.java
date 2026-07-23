package com.dp.plat.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dp.plat.system.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for {@link LoginLog}.
 */
@Mapper
public interface PmsLoginLogMapper extends BaseMapper<LoginLog> {
}
