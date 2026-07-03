package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysOperLog;
import com.dp.plat.system.mapper.SysOperLogMapper;
import com.dp.plat.system.service.ISysOperLogService;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ISysOperLogService}.
 */
@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {
}
