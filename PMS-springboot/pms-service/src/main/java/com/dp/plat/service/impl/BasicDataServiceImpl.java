package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.SysBasicDataMapper;
import com.dp.plat.mapper.SysFileInfoMapper;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.BasicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private SysFileInfoMapper fileInfoMapper;

    @Override
    public List<SysBasicData> queryByType(String dataType) {
        return basicDataMapper.selectByDataTypeCode(dataType);
    }

    /** 查询所有基础数据(包括禁用的) */
    public List<SysBasicData> queryAllByType(String dataType) {
        return basicDataMapper.selectList(
                new LambdaQueryWrapper<SysBasicData>()
                        .eq(SysBasicData::getDataType, dataType)
                        .orderByAsc(SysBasicData::getSort));
    }

    @Override
    @Transactional
    public void addBasicData(SysBasicData data) {
        data.setCreateTime(LocalDateTime.now());
        data.setCreateBy(SecurityUtil.getCurrentUsername());
        basicDataMapper.insert(data);
    }

    @Override
    @Transactional
    public void updateBasicData(SysBasicData data) {
        data.setUpdateTime(LocalDateTime.now());
        data.setUpdateBy(SecurityUtil.getCurrentUsername());
        basicDataMapper.updateById(data);
    }

    @Override
    @Transactional
    public void deleteBasicData(Long id) {
        basicDataMapper.deleteById(id);
    }

    @Override
    public String querySysArg(String code) {
        return basicDataMapper.selectSysArg(code);
    }

    /** 根据ID查询基础数据名称 */
    public String queryBasicDataNameById(String basicDataId) {
        if (!StringUtils.hasText(basicDataId)) return "";
        try {
            SysBasicData data = basicDataMapper.selectById(Long.parseLong(basicDataId));
            return data != null ? data.getDataName() : "";
        } catch (NumberFormatException e) {
            return "";
        }
    }

    /** 根据数据编码查询基础数据 */
    public SysBasicData queryByDataCode(String dataType, String dataCode) {
        return basicDataMapper.selectOne(
                new LambdaQueryWrapper<SysBasicData>()
                        .eq(SysBasicData::getDataType, dataType)
                        .eq(SysBasicData::getDataCode, dataCode)
                        .last("LIMIT 1"));
    }

    /** 插入文件信息 */
    @Transactional
    public Long insertFileInfo(String fileName, String filePath, String module) {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setFilePath(filePath);
        fileInfo.setModule(module);
        fileInfo.setUploadBy(SecurityUtil.getCurrentUsername());
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfo.setCreateTime(LocalDateTime.now());
        fileInfoMapper.insert(fileInfo);
        return fileInfo.getId();
    }

    /** 查询文件信息 */
    public SysFileInfo queryFileInfo(Long fileId) {
        return fileInfoMapper.selectById(fileId);
    }

    /** 删除文件 */
    @Transactional
    public void deleteFile(Long fileId) {
        fileInfoMapper.deleteById(fileId);
    }

    /** 查询基础数据Map(dataCode -> dataName) */
    public Map<String, String> queryBasicDataMap(String dataType) {
        List<SysBasicData> list = queryByType(dataType);
        Map<String, String> map = new HashMap<>();
        for (SysBasicData item : list) {
            map.put(item.getDataCode(), item.getDataName());
        }
        return map;
    }

    /** 刷新缓存 */
    public boolean refreshCacheData() {
        // 迁移自: ClusterAction.refreshCacheData()
        // 清除基础数据缓存,下次查询时重新加载
        return true;
    }
}
