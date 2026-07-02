package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.Job;
import java.util.List;

/**
 * 岗位服务 - migrated from Struts
 */
public interface JobService {

    IPage<Job> queryPage(Integer pageNum, Integer pageSize);

    Job getById(Long id);

    void add(Job entity);

    void update(Job entity);

    void delete(Long id);

    List<Job> listAll();

}