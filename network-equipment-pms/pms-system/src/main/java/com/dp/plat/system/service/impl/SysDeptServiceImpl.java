package com.dp.plat.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.system.entity.SysDept;
import com.dp.plat.system.mapper.SysDeptMapper;
import com.dp.plat.system.service.ISysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of {@link ISysDeptService}.
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> listChildren(Long parentId) {
        return this.list(new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getParentId, parentId)
                .orderByAsc(SysDept::getOrderNum));
    }
}
