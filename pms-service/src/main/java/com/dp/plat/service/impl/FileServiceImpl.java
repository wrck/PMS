package com.dp.plat.service.impl;

import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.SysFileInfoMapper;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务 - 迁移自老系统 UploadServiceImpl
 *
 * 核心逻辑：
 * 1. 按模块分目录存储
 * 2. UUID 重命名防冲突
 * 3. 记录文件信息到数据库
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private SysFileInfoMapper fileInfoMapper;

    @Value("${pms.upload.base-path:upload}")
    private String basePath;

    @Override
    @Transactional
    public Map<String, Object> uploadFile(MultipartFile file, String module, String username) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }

        // 生成存储路径：basePath/module/yyyyMMdd/UUID.ext
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String relativePath = (module != null ? module : "common") + "/" + datePath + "/" + uuid + ext;

        String fullPath = basePath + "/" + relativePath;
        File dest = new File(fullPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        // 保存文件信息
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName(originalName);
        fileInfo.setFilePath(relativePath);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(ext);
        fileInfo.setUploadBy(username);
        fileInfo.setUploadTime(LocalDateTime.now());
        fileInfoMapper.insert(fileInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("fileId", fileInfo.getId());
        result.put("fileName", originalName);
        result.put("filePath", relativePath);
        result.put("fileSize", file.getSize());
        return result;
    }

    @Override
    @Transactional
    public void deleteFile(Long fileId) {
        SysFileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        // 删除物理文件
        File file = new File(basePath + "/" + fileInfo.getFilePath());
        if (file.exists()) {
            file.delete();
        }
        fileInfoMapper.deleteById(fileId);
    }

    @Override
    public Map<String, Object> getFileInfo(Long fileId) {
        SysFileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("fileId", fileInfo.getId());
        result.put("fileName", fileInfo.getFileName());
        result.put("filePath", fileInfo.getFilePath());
        result.put("fileSize", fileInfo.getFileSize());
        result.put("fileType", fileInfo.getFileType());
        result.put("uploadBy", fileInfo.getUploadBy());
        result.put("uploadTime", fileInfo.getUploadTime());
        return result;
    }
}
