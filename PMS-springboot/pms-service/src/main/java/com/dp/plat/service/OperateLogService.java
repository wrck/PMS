package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.model.entity.SysOperateLog;

import java.util.List;

public interface OperateLogService {
    void recordLog(String username, String realname, String ip, String operation, String module);
    IPage<SysOperateLog> queryLogPage(Integer pageNum, Integer pageSize, String username, String module);
    List<SysOperateLog> queryAllLogs(String username, String module);

    /**
     * 批量删除日志
     * 迁移自: OpLogServiceImpl.delete()
     */
    void deleteLogs(List<Long> ids);
}
