package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.DailyReport;
import java.util.List;

/**
 * 日报服务 - migrated from Struts
 */
public interface DailyReportService {

    IPage<DailyReport> queryPage(Integer pageNum, Integer pageSize);

    DailyReport getById(Long id);

    void add(DailyReport entity);

    void update(DailyReport entity);

    void delete(Long id);

    List<DailyReport> listAll();

}