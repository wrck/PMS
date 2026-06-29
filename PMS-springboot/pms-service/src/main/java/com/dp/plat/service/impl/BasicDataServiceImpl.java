package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.mapper.SysBasicDataMapper;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.service.BasicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 基础数据服务 - 迁移自老系统 BasicDataServiceImpl
 *
 * 对应老系统 fnd_basic_data 表
 * 管理系统下拉选项：办事处、项目类型、项目状态等
 */
@Service
public class BasicDataServiceImpl implements BasicDataService {

    @Autowired
    private SysBasicDataMapper basicDataMapper;

    @Override
    public List<SysBasicData> queryByType(String dataType) {
        return basicDataMapper.selectList(
                new LambdaQueryWrapper<SysBasicData>()
                        .eq(SysBasicData::getDataType, dataType)
                        .eq(SysBasicData::getStatus, 1)
                        .orderByAsc(SysBasicData::getSort));
    }

    @Override
    @Transactional
    public void addBasicData(SysBasicData data) {
        data.setCreateTime(LocalDateTime.now());
        basicDataMapper.insert(data);
    }

    @Override
    @Transactional
    public void updateBasicData(SysBasicData data) {
        basicDataMapper.updateById(data);
    }

    @Override
    @Transactional
    public void deleteBasicData(Long id) {
        basicDataMapper.deleteById(id);
    }

    @Override
    public String querySysArg(String code) {
        List<SysBasicData> list = basicDataMapper.selectList(
                new LambdaQueryWrapper<SysBasicData>()
                        .eq(SysBasicData::getDataType, "sys.arg")
                        .eq(SysBasicData::getDataCode, code));
        return list.isEmpty() ? null : list.get(0).getDataName();
    }
}
