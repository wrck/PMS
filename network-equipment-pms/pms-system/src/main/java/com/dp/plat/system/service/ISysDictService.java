package com.dp.plat.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.system.entity.SysDict;
import com.dp.plat.system.entity.SysDictItem;

import java.util.List;

/**
 * Service for {@link SysDict}.
 */
public interface ISysDictService extends IService<SysDict> {

    /**
     * Get dict by dict type.
     */
    SysDict getByDictType(String dictType);

    /**
     * List dict items by dict type.
     */
    List<SysDictItem> listItemsByDictType(String dictType);
}
