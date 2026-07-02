package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PmsProject;
import java.util.List;

/**
 * 资源服务 - migrated from Struts
 */
public interface ResourceService {

    IPage<PmsProject> queryPage(Integer pageNum, Integer pageSize);

    PmsProject getById(Long id);

    void add(PmsProject entity);

    void update(PmsProject entity);

    void delete(Long id);

    List<PmsProject> listAll();

}