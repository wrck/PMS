package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysDict;
import com.dp.plat.system.entity.SysDictItem;
import com.dp.plat.system.mapper.SysDictItemMapper;
import com.dp.plat.system.mapper.SysDictMapper;
import com.dp.plat.system.service.ISysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link ISysDictService}.
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    private final SysDictItemMapper sysDictItemMapper;

    @Override
    public SysDict getByDictType(String dictType) {
        return this.getOne(new LambdaQueryWrapper<SysDict>()
                .eq(SysDict::getDictType, dictType));
    }

    @Override
    public List<SysDictItem> listItemsByDictType(String dictType) {
        SysDict dict = getByDictType(dictType);
        if (dict == null) {
            return Collections.emptyList();
        }
        return sysDictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dict.getId())
                .orderByAsc(SysDictItem::getSortOrder));
    }
}
