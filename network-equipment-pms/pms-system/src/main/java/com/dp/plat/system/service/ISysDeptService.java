package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysDept;

import java.util.List;

/**
 * Service for {@link SysDept}.
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * Get child departments by parent id.
     */
    List<SysDept> listChildren(Long parentId);
}
