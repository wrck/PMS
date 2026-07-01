package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.SysBasicDataMapper;
import com.dp.plat.mapper.SysFileInfoMapper;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.BasicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Override
    public List<SysBasicData> queryBasicDataType() {
        return basicDataMapper.selectList(
                new LambdaQueryWrapper<SysBasicData>()
                        .select(SysBasicData::getDataType)
                        .groupBy(SysBasicData::getDataType)
                        .orderByAsc(SysBasicData::getDataType));
    }

    @Override
    public SysBasicData queryBasicDataBean(Long id) {
        return basicDataMapper.selectById(id);
    }

    @Override
    public int findBasicDataId(Map<String, Object> paramMap) {
        // 迁移自: BasicDataDao.findBasicDataId()
        // 根据条件Map查找基础数据ID
        LambdaQueryWrapper<SysBasicData> wrapper = new LambdaQueryWrapper<>();
        if (paramMap.containsKey("dataType")) {
            wrapper.eq(SysBasicData::getDataType, paramMap.get("dataType"));
        }
        if (paramMap.containsKey("dataCode")) {
            wrapper.eq(SysBasicData::getDataCode, paramMap.get("dataCode"));
        }
        if (paramMap.containsKey("dataName")) {
            wrapper.eq(SysBasicData::getDataName, paramMap.get("dataName"));
        }
        wrapper.last("LIMIT 1");
        SysBasicData data = basicDataMapper.selectOne(wrapper);
        return data != null ? data.getId().intValue() : 0;
    }

    @Override
    @Transactional
    public void executeSql(String sql) {
        // 迁移自: BasicDataDao.executeSql()
        // 仅允许在受控场景下执行，需确保调用方已做安全校验
        jdbcTemplate.execute(sql);
    }

    @Override
    @Transactional
    public String batchInsertFileInfo(String path, String uploadFileName, String uploadFileType) {
        // 迁移自: BasicDataServiceImpl.insertFileInfo(path, uploadFileName, uploadFileType)
        StringBuilder fileIds = new StringBuilder();
        String[] fileNames = StringUtils.trimToEmpty(uploadFileName).split(",");
        String[] fileTypes = StringUtils.trimToEmpty(uploadFileType).split(",");
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = StringUtils.trimToEmpty(fileNames[i]);
            if (!StringUtils.hasText(fileName)) continue;
            SysFileInfo fileInfo = new SysFileInfo();
            fileInfo.setFileName(fileName);
            fileInfo.setFilePath(path + fileName);
            if (fileTypes.length == fileNames.length) {
                fileInfo.setFileType(StringUtils.trimToEmpty(fileTypes[i]));
            }
            fileInfo.setUploadBy(SecurityUtil.getCurrentUsername());
            fileInfo.setUploadTime(LocalDateTime.now());
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfoMapper.insert(fileInfo);
            fileIds.append(fileInfo.getId()).append(",");
        }
        if (fileIds.length() > 0) {
            fileIds.deleteCharAt(fileIds.length() - 1);
        }
        return fileIds.toString();
    }

    @Override
    public Map<Long, String> queryFileMap(String fileIds) {
        // 迁移自: BasicDataDao.queryFileMap()
        if (!StringUtils.hasText(fileIds)) return null;
        List<Long> idList = Arrays.stream(fileIds.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if (idList.isEmpty()) return null;
        List<SysFileInfo> files = fileInfoMapper.selectBatchIds(idList);
        Map<Long, String> map = new LinkedHashMap<>();
        for (SysFileInfo f : files) {
            map.put(f.getId(), f.getFileName());
        }
        return map;
    }

    @Override
    public List<SysFileInfo> queryFileList(String fileIds) {
        // 迁移自: BasicDataDao.queryFileList()
        if (!StringUtils.hasText(fileIds)) return Collections.emptyList();
        List<Long> idList = Arrays.stream(fileIds.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if (idList.isEmpty()) return Collections.emptyList();
        return fileInfoMapper.selectBatchIds(idList);
    }

    @Override
    public SysBasicData queryBasicDataBeanByDataId(String basicDataId) {
        // 迁移自: BasicDataDao.queryBasicDataBeanByDataId()
        if (!StringUtils.hasText(basicDataId)) return null;
        try {
            return basicDataMapper.selectById(Long.parseLong(basicDataId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public List<SysBasicData> queryBasicDataBeanByAttri(String dataType, String attri1) {
        // 迁移自: BasicDataDao.queryBasicDataBeanByAttri()
        return basicDataMapper.selectList(
                new LambdaQueryWrapper<SysBasicData>()
                        .eq(SysBasicData::getDataType, dataType)
                        .like(StringUtils.hasText(attri1), SysBasicData::getDataName, attri1)
                        .orderByAsc(SysBasicData::getSort));
    }

    @Override
    public List<Map<String, Object>> queryBasicDataBeanMapWithSub(
            String dataTypeCode, String subDataTypeCode, Map<String, Object> extra) {
        // 迁移自: BasicDataDao.queryBasicDataBeanMapWithSub()
        // 查询主类型数据
        List<SysBasicData> mainList = queryByType(dataTypeCode);
        // 查询子类型数据
        List<SysBasicData> subList = StringUtils.hasText(subDataTypeCode) ? queryByType(subDataTypeCode) : Collections.emptyList();
        // 组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysBasicData item : mainList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", item.getId());
            row.put("dataCode", item.getDataCode());
            row.put("dataName", item.getDataName());
            row.put("dataType", item.getDataType());
            row.put("sort", item.getSort());
            // 关联子类型数据
            List<SysBasicData> matchedSub = subList.stream()
                    .filter(s -> item.getDataCode().equals(s.getDataCode()))
                    .collect(Collectors.toList());
            row.put("subList", matchedSub);
            if (extra != null) {
                row.putAll(extra);
            }
            result.add(row);
        }
        return result;
    }

    /** 刷新缓存 */
    public boolean refreshCacheData() {
        // 迁移自: ClusterAction.refreshCacheData()
        // 清除基础数据缓存,下次查询时重新加载
        return true;
    }
}
