package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.mapper.SysOperateLogMapper;
import com.dp.plat.model.entity.SysOperateLog;
import com.dp.plat.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志服务 - 迁移自老系统 OpLogServiceImpl
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private SysOperateLogMapper logMapper;

    @Override
    public void recordLog(String username, String realname, String ip, String operation, String module) {
        SysOperateLog log = new SysOperateLog();
        log.setUsername(username);
        log.setRealname(realname);
        log.setIp(ip);
        log.setOperation(operation);
        log.setModule(module);
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }

    @Override
    public IPage<SysOperateLog> queryLogPage(Integer pageNum, Integer pageSize,
                                               String username, String module) {
        Page<SysOperateLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysOperateLog::getUsername, username)
               .like(StringUtils.hasText(module), SysOperateLog::getModule, module)
               .orderByDesc(SysOperateLog::getCreateTime);
        return logMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysOperateLog> queryAllLogs(String username, String module) {
        LambdaQueryWrapper<SysOperateLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysOperateLog::getUsername, username)
               .like(StringUtils.hasText(module), SysOperateLog::getModule, module)
               .orderByDesc(SysOperateLog::getCreateTime);
        return logMapper.selectList(wrapper);
    }

    @Override
    public void deleteLogs(List<Long> ids) {
        // 迁移自: OpLogServiceImpl.delete()
        if (ids != null && !ids.isEmpty()) {
            logMapper.deleteBatchIds(ids);
        }
    }
}
