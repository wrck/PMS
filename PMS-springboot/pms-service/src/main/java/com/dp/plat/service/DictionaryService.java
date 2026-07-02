package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.model.entity.SysBasicData;
import java.util.List;

/**
 * 字典服务 - migrated from Struts
 */
public interface DictionaryService {

    IPage<SysBasicData> queryPage(Integer pageNum, Integer pageSize);

    SysBasicData getById(Long id);

    void add(SysBasicData entity);

    void update(SysBasicData entity);

    void delete(Long id);

    List<SysBasicData> listAll();

}