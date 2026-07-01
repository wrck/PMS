package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsMaintenanceMapper;
import com.dp.plat.model.entity.PmsMaintenance;
import com.dp.plat.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {
    @Autowired
    private PmsMaintenanceMapper mapper;

    @Override
    public IPage<PmsMaintenance> queryPage(Integer pageNum, Integer pageSize, Long projectId, String maintenanceType) {
        Page<PmsMaintenance> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsMaintenance> w = new LambdaQueryWrapper<>();
        w.eq(projectId != null, PmsMaintenance::getProjectId, projectId)
         .eq(StringUtils.hasText(maintenanceType), PmsMaintenance::getMaintenanceType, maintenanceType)
         .orderByDesc(PmsMaintenance::getCreateTime);
        return mapper.selectPage(page, w);
    }

    @Override
    public PmsMaintenance getDetail(Long id) {
        PmsMaintenance m = mapper.selectById(id);
        if (m == null) throw new BusinessException("维保记录不存在");
        return m;
    }

    @Override
    @Transactional
    public void create(PmsMaintenance m) {
        m.setCreateTime(LocalDateTime.now());
        mapper.insert(m);
    }

    @Override
    @Transactional
    public void update(PmsMaintenance m) {
        mapper.updateById(m);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> queryServiceDelivery(Long maintenanceId) {
        // 迁移自: MaintenanceServiceImpl.serviceDelivery()
        // 查询运维服务交付记录
        return mapper.selectServiceDelivery(maintenanceId);
    }

    @Override
    @Transactional
    public void uploadFile(Long maintenanceId, String fileIds) {
        PmsMaintenance m = mapper.selectById(maintenanceId);
        if (m != null) {
            m.setAttachmentIds(fileIds);
            m.setUpdateBy(com.dp.plat.common.utils.SecurityUtil.getCurrentUsername());
            m.setUpdateTime(LocalDateTime.now());
            mapper.updateById(m);
        }
    }

    @Override
    public List<Map<String, Object>> queryFiles(Long maintenanceId) {
        // 迁移自: MaintenanceAction.toUploadFile() -> BasicDataServiceImpl.queryFileInfo()
        PmsMaintenance m = mapper.selectById(maintenanceId);
        if (m == null || m.getAttachmentIds() == null || m.getAttachmentIds().isEmpty()) {
            return Collections.emptyList();
        }
        // 通过fileIds查询文件详情
        List<Map<String, Object>> fileList = new ArrayList<>();
        String[] fileIds = m.getAttachmentIds().split(",");
        for (String fileIdStr : fileIds) {
            try {
                Long fileId = Long.parseLong(fileIdStr.trim());
                // 查询文件信息
                SysFileInfo fileInfo = fileInfoMapper.selectById(fileId);
                if (fileInfo != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("fileId", fileInfo.getId());
                    item.put("fileName", fileInfo.getFileName());
                    item.put("filePath", fileInfo.getFilePath());
                    item.put("fileSize", fileInfo.getFileSize());
                    item.put("createTime", fileInfo.getCreateTime());
                    fileList.add(item);
                }
            } catch (NumberFormatException ignored) {}
        }
        return fileList;
    }
}
