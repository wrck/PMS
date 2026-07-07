package com.dp.plat.file.storage;

import com.dp.plat.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link LocalStorageServiceImpl} 的单元测试。
 *
 * <p>使用 {@link TempDir} 隔离文件系统操作，通过 {@link ReflectionTestUtils#setField}
 * 注入 baseDir 配置并手动调用 {@code init()} 初始化 basePath。</p>
 *
 * <p>覆盖上传（含路径穿越防护）、下载、删除、预签名 URL 生成，以及各类异常分支。</p>
 */
@ExtendWith(MockitoExtension.class)
class LocalStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private LocalStorageServiceImpl storageService;

    @BeforeEach
    void setUp() throws Exception {
        storageService = new LocalStorageServiceImpl();
        ReflectionTestUtils.setField(storageService, "baseDir", tempDir.toString());
        storageService.init();
    }

    // ==================== upload ====================

    @Test
    @DisplayName("upload: 正常上传文件，返回相对路径并写入磁盘")
    void upload_normal_writesFileAndReturnsPath() throws Exception {
        byte[] content = "hello world".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        String storagePath = storageService.upload(input, "test.txt", content.length, "text/plain");

        assertNotNull(storagePath);
        assertTrue(storagePath.matches("\\d{4}/\\d{2}/\\d{2}/[a-f0-9]+_test\\.txt"),
                "storagePath 应为 yyyy/MM/dd/<uuid>_test.txt 格式");

        Path uploadedFile = tempDir.resolve(storagePath);
        assertTrue(Files.exists(uploadedFile), "文件应写入磁盘");
        assertArrayEquals(content, Files.readAllBytes(uploadedFile));
    }

    @Test
    @DisplayName("upload: 文件名为 null，使用默认名 file")
    void upload_nullFileName_usesDefaultName() {
        byte[] content = "data".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        String storagePath = storageService.upload(input, null, content.length, "application/octet-stream");

        assertNotNull(storagePath);
        assertTrue(storagePath.contains("_file"), "null 文件名应使用默认名 file");
    }

    @Test
    @DisplayName("upload: 文件名含路径穿越字符被清理（/ \\ .. 替换为 _）")
    void upload_pathTraversalFileName_sanitized() {
        byte[] content = "data".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        String storagePath = storageService.upload(input, "../../etc/passwd", content.length, "text/plain");

        assertNotNull(storagePath);
        assertTrue(storagePath.contains("_etc_passwd"), "路径穿越字符应被替换为 _");
        assertTrue(!storagePath.contains(".."), "路径中不应包含 ..");
    }

    // ==================== download ====================

    @Test
    @DisplayName("download: 正常下载返回文件流")
    void download_normal_returnsStream() throws Exception {
        byte[] content = "download me".getBytes();
        String storagePath = storageService.upload(
                new ByteArrayInputStream(content), "file.bin", content.length, "application/octet-stream");

        InputStream result = storageService.download(storagePath);

        assertNotNull(result);
        assertArrayEquals(content, result.readAllBytes());
    }

    @Test
    @DisplayName("download: 文件不存在抛业务异常")
    void download_notFound_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.download("2024/01/01/nonexistent.txt"));
        assertTrue(ex.getMessage().contains("文件不存在"));
    }

    @Test
    @DisplayName("download: 路径穿越攻击抛业务异常")
    void download_pathTraversal_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.download("../../etc/passwd"));
        assertTrue(ex.getMessage().contains("文件不存在"));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete: 正常删除文件")
    void delete_normal_removesFile() throws Exception {
        byte[] content = "temp".getBytes();
        String storagePath = storageService.upload(
                new ByteArrayInputStream(content), "temp.txt", content.length, "text/plain");
        Path uploadedFile = tempDir.resolve(storagePath);
        assertTrue(Files.exists(uploadedFile));

        storageService.delete(storagePath);

        assertTrue(Files.notExists(uploadedFile), "删除后文件不应存在");
    }

    @Test
    @DisplayName("delete: 文件不存在不抛异常（deleteIfExists 语义）")
    void delete_nonExistent_noException() {
        // 不存在的文件不应抛异常
        storageService.delete("2024/01/01/never_existed.txt");
    }

    @Test
    @DisplayName("delete: 路径穿越攻击抛业务异常")
    void delete_pathTraversal_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.delete("../../etc/passwd"));
        assertTrue(ex.getMessage().contains("非法"));
    }

    // ==================== generatePresignedUrl ====================

    @Test
    @DisplayName("generatePresignedUrl: 返回下载接口 URL，包含编码后的 path 参数")
    void generatePresignedUrl_returnsDownloadUrl() {
        String url = storageService.generatePresignedUrl("2024/01/01/abc.txt", 3600);

        assertNotNull(url);
        assertTrue(url.startsWith("/api/file/download?path="));
        assertTrue(url.contains("2024"), "URL 应包含 storagePath 内容");
        assertTrue(url.contains("abc.txt"), "URL 应包含文件名");
    }

    @Test
    @DisplayName("generatePresignedUrl: storagePath 含特殊字符正确编码")
    void generatePresignedUrl_specialChars_encoded() {
        String url = storageService.generatePresignedUrl("2024/01/01/文件 名.txt", 3600);

        assertNotNull(url);
        assertTrue(url.contains("%E6%96%87%E4%BB%B6"), "中文字符应被 URL 编码");
    }

    // ==================== init ====================

    @Test
    @DisplayName("init: baseDir 不存在时自动创建目录")
    void init_createsBaseDir() throws Exception {
        Path newBaseDir = tempDir.resolve("new-storage-root");
        LocalStorageServiceImpl newService = new LocalStorageServiceImpl();
        ReflectionTestUtils.setField(newService, "baseDir", newBaseDir.toString());

        newService.init();

        assertTrue(Files.exists(newBaseDir), "init 应自动创建根目录");
    }
}
