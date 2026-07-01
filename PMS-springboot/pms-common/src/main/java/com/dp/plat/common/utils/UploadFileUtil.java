package com.dp.plat.common.utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * 文件上传工具类 - 迁移自老系统 UploadFileUtil (424行, 18个方法)
 *
 * 使用Spring Boot的MultipartFile替代老系统的File[]上传方式
 * 此类保留文件路径处理和辅助方法
 */
public class UploadFileUtil {

    public static final String UPLOAD_PATH = "uploads";

    /** 生成文件存储路径 */
    public static String generateFilePath(String module, String originalName) {
        String datePath = DateUtil.getCurrentDate().replace("-", "/");
        String ext = getExtension(originalName);
        String fileName = UUID.randomUUID().toString().replace("-", "") + (ext.isEmpty() ? "" : "." + ext);
        return UPLOAD_PATH + "/" + module + "/" + datePath + "/" + fileName;
    }

    /** 获取文件扩展名 */
    public static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /** 获取文件名(不含扩展名) */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf(".");
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    /** 判断是否为图片文件 */
    public static boolean isImageFile(String fileName) {
        String ext = getExtension(fileName);
        return ext.matches("jpg|jpeg|png|gif|bmp|webp|svg");
    }

    /** 判断是否为文档文件 */
    public static boolean isDocumentFile(String fileName) {
        String ext = getExtension(fileName);
        return ext.matches("doc|docx|xls|xlsx|ppt|pptx|pdf|txt|csv");
    }

    /** 安全文件名(去除特殊字符) */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) return "";
        return fileName.replaceAll("[^a-zA-Z0-9._\\-\\u4e00-\\u9fa5]", "_");
    }

    /** 格式化文件大小 */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) return sizeInBytes + " B";
        if (sizeInBytes < 1024 * 1024) return String.format("%.1f KB", sizeInBytes / 1024.0);
        if (sizeInBytes < 1024 * 1024 * 1024) return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024));
        return String.format("%.1f GB", sizeInBytes / (1024.0 * 1024 * 1024));
    }

    /** 创建目录(如果不存在) */
    public static void ensureDirectoryExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /** 保存文件到磁盘 */
    public static void saveFile(InputStream inputStream, String targetPath) throws IOException {
        ensureDirectoryExists(Paths.get(targetPath).getParent().toString());
        Files.copy(inputStream, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
    }

    /** 删除文件 */
    public static boolean deleteFile(String filePath) {
        if (filePath == null) return false;
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            return false;
        }
    }

    /** 解析文件ID列表(逗号分隔) */
    public static List<Long> parseFileIds(String fileIds) {
        List<Long> result = new ArrayList<>();
        if (fileIds == null || fileIds.isEmpty()) return result;
        for (String id : fileIds.split(",")) {
            try {
                result.add(Long.parseLong(id.trim()));
            } catch (NumberFormatException ignored) {}
        }
        return result;
    }
}
