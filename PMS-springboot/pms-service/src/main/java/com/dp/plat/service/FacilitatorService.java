package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.Facilitator;
import java.util.List;

/**
 * 服务商服务 - migrated from Struts
 */
public interface FacilitatorService {

    IPage<Facilitator> queryPage(Integer pageNum, Integer pageSize);

    Facilitator getById(Long id);

    void add(Facilitator entity);

    void update(Facilitator entity);

    void delete(Long id);

    List<Facilitator> listAll();

}