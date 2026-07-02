package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.PmsProjectPlan;
import java.util.List;

/**
 * 项目计划服务 - migrated from Struts
 */
public interface ProjectPlanService {

    IPage<PmsProjectPlan> queryPage(Integer pageNum, Integer pageSize);

    PmsProjectPlan getById(Long id);

    void add(PmsProjectPlan entity);

    void update(PmsProjectPlan entity);

    void delete(Long id);

    List<PmsProjectPlan> listAll();

}