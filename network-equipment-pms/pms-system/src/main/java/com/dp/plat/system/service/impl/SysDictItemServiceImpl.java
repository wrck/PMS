package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysDictItem;
import com.dp.plat.system.mapper.SysDictItemMapper;
import com.dp.plat.system.service.ISysDictItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link ISysDictItemService}.
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements ISysDictItemService {

    @Override
    public List<SysDictItem> listByDictId(Long dictId) {
        return this.list(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictId, dictId)
                .orderByAsc(SysDictItem::getSortOrder));
    }

    @Override
    public boolean create(SysDictItem item) {
        return this.save(item);
    }

    @Override
    public boolean update(SysDictItem item) {
        return this.updateById(item);
    }

    @Override
    public boolean deleteById(Long id) {
        return this.removeById(id);
    }
}
