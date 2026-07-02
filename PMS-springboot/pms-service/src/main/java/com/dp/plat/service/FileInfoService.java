package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysFileInfo;
import java.util.List;

/**
 * 文件信息服务 - migrated from Struts
 */
public interface FileInfoService {

    IPage<SysFileInfo> queryPage(Integer pageNum, Integer pageSize);

    SysFileInfo getById(Long id);

    void add(SysFileInfo entity);

    void update(SysFileInfo entity);

    void delete(Long id);

    List<SysFileInfo> listAll();

}