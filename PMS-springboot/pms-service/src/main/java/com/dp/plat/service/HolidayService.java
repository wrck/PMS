package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.Holiday;
import java.util.List;

/**
 * 假期服务 - migrated from Struts
 */
public interface HolidayService {

    IPage<Holiday> queryPage(Integer pageNum, Integer pageSize);

    Holiday getById(Long id);

    void add(Holiday entity);

    void update(Holiday entity);

    void delete(Long id);

    List<Holiday> listAll();

}