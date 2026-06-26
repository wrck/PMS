package com.dp.plat.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileService {
    Map<String, Object> uploadFile(MultipartFile file, String module, String username);
    void deleteFile(Long fileId);
    Map<String, Object> getFileInfo(Long fileId);
}
