package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.system.entity.ExceptionLog;
import com.dp.plat.system.mapper.ExceptionLogMapper;
import com.dp.plat.system.service.IExceptionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 异常日志服务实现。
 */
@Service
@RequiredArgsConstructor
public class ExceptionLogServiceImpl implements IExceptionLogService {

    private final ExceptionLogMapper exceptionLogMapper;

    @Override
    public boolean record(ExceptionLog exceptionLog) {
        return exceptionLogMapper.insert(exceptionLog) > 0;
    }

    @Override
    public IPage<ExceptionLog> page(int page, int size, ExceptionLog filter) {
        Page<ExceptionLog> p = new Page<>(page, size);
        LambdaQueryWrapper<ExceptionLog> wrapper = new LambdaQueryWrapper<>();
        if (filter != null) {
            wrapper.like(StringUtils.hasText(filter.getUsername()), ExceptionLog::getUsername, filter.getUsername());
            wrapper.like(StringUtils.hasText(filter.getRequestUri()), ExceptionLog::getRequestUri, filter.getRequestUri());
            wrapper.eq(filter.getUserId() != null, ExceptionLog::getUserId, filter.getUserId());
        }
        wrapper.orderByDesc(ExceptionLog::getOccurTime);
        return exceptionLogMapper.selectPage(p, wrapper);
    }
}
