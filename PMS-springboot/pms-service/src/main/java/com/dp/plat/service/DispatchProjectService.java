package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.DispatchProject;
import java.util.List;

/**
 * 调度项目服务 - migrated from Struts
 */
public interface DispatchProjectService {

    IPage<DispatchProject> queryPage(Integer pageNum, Integer pageSize);

    DispatchProject getById(Long id);

    void add(DispatchProject entity);

    void update(DispatchProject entity);

    void delete(Long id);

    List<DispatchProject> listAll();

}