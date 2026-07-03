package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysDictItem;

import java.util.List;

/**
 * Service for {@link SysDictItem}.
 */
public interface ISysDictItemService extends IService<SysDictItem> {

    /**
     * List dict items by dict id.
     */
    List<SysDictItem> listByDictId(Long dictId);

    /**
     * Create a dict item.
     */
    boolean create(SysDictItem item);

    /**
     * Update a dict item.
     */
    boolean update(SysDictItem item);

    /**
     * Delete a dict item by id.
     */
    boolean deleteById(Long id);
}
