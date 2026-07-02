package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysOperateLog;
import java.util.List;

/**
 * 系统日志服务 - migrated from Struts
 */
public interface SysLogService {

    IPage<SysOperateLog> queryPage(Integer pageNum, Integer pageSize);

    SysOperateLog getById(Long id);

    void add(SysOperateLog entity);

    void update(SysOperateLog entity);

    void delete(Long id);

    List<SysOperateLog> listAll();

}