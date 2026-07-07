package com.dp.plat.file.storage;

import com.dp.plat.common.exception.BusinessException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link MinioStorageServiceImpl} 的单元测试。
 *
 * <p>{@link MinioClient} 在 minio 8.5.10 中为非 final 类（有 protected 构造器），
 * 可使用 mock-maker-subclass 进行 mock。通过 {@link ReflectionTestUtils#setField}
 * 注入配置字段和 mock 的 {@code minioClient}，跳过 {@code @PostConstruct init()}
 * 中的真实客户端创建和 bucket 校验。</p>
 *
 * <p>覆盖上传、下载、删除、预签名 URL 生成，以及 MinIO 异常包装为 BusinessException 的逻辑。</p>
 */
@ExtendWith(MockitoExtension.class)
class MinioStorageServiceImplTest {

    private static final String BUCKET = "test-bucket";
    private static final String ENDPOINT = "http://127.0.0.1:9000";

    @Mock
    private MinioClient minioClient;

    private MinioStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new MinioStorageServiceImpl();
        ReflectionTestUtils.setField(storageService, "endpoint", ENDPOINT);
        ReflectionTestUtils.setField(storageService, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(storageService, "secretKey", "test-secret-key");
        ReflectionTestUtils.setField(storageService, "bucketName", BUCKET);
        ReflectionTestUtils.setField(storageService, "minioClient", minioClient);
    }

    private static final Pattern PATH_PATTERN =
            Pattern.compile("\\d{4}/\\d{2}/\\d{2}/[a-f0-9]+_test\\.txt");

    // ==================== upload ====================

    @Test
    @DisplayName("upload: 正常上传，调用 minioClient.putObject 并返回 storagePath")
    void upload_normal_callsPutObject() throws Exception {
        byte[] content = "hello".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String storagePath = storageService.upload(input, "test.txt", content.length, "text/plain");

        assertNotNull(storagePath);
        assertTrue(PATH_PATTERN.matcher(storagePath).matches(),
                "storagePath 应为 yyyy/MM/dd/<uuid>_test.txt 格式");
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("upload: mimeType 为 null 时不设置 contentType，仍正常上传")
    void upload_nullMimeType_success() throws Exception {
        byte[] content = "data".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String storagePath = storageService.upload(input, "file.bin", content.length, null);

        assertNotNull(storagePath);
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("upload: MinIO putObject 抛异常时包装为 BusinessException")
    void upload_minioThrows_wrapsBusinessException() throws Exception {
        InputStream input = new ByteArrayInputStream("data".getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO connection error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.upload(input, "test.txt", 4L, "text/plain"));
        assertTrue(ex.getMessage().contains("MinIO 文件上传失败"));
    }

    // ==================== download ====================

    @Test
    @DisplayName("download: 正常下载返回文件输入流")
    void download_normal_returnsStream() throws Exception {
        byte[] content = "minio content".getBytes();
        GetObjectResponse response = new GetObjectResponse(
                null, null, null, null, new ByteArrayInputStream(content));
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(response);

        InputStream result = storageService.download("2024/01/01/abc.txt");

        assertNotNull(result);
        assertArrayEquals(content, result.readAllBytes());
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }

    @Test
    @DisplayName("download: MinIO getObject 抛异常时包装为 BusinessException")
    void download_minioThrows_wrapsBusinessException() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO download error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.download("2024/01/01/abc.txt"));
        assertTrue(ex.getMessage().contains("MinIO 文件下载失败"));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete: 正常删除，调用 minioClient.removeObject")
    void delete_normal_callsRemoveObject() throws Exception {
        storageService.delete("2024/01/01/abc.txt");

        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("delete: MinIO removeObject 抛异常时包装为 BusinessException")
    void delete_minioThrows_wrapsBusinessException() throws Exception {
        doThrow(new RuntimeException("MinIO delete error"))
                .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.delete("2024/01/01/abc.txt"));
        assertTrue(ex.getMessage().contains("MinIO 文件删除失败"));
    }

    // ==================== generatePresignedUrl ====================

    @Test
    @DisplayName("generatePresignedUrl: 正常生成预签名 URL")
    void generatePresignedUrl_normal_returnsUrl() throws Exception {
        String expectedUrl = "http://127.0.0.1:9000/test-bucket/abc?X-Amz-Signature=xxx";
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(expectedUrl);

        String url = storageService.generatePresignedUrl("2024/01/01/abc.txt", 3600);

        assertNotNull(url);
        assertTrue(url.contains("X-Amz-Signature=xxx"));
        verify(minioClient, times(1)).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @DisplayName("generatePresignedUrl: MinIO 生成抛异常时包装为 BusinessException")
    void generatePresignedUrl_minioThrows_wrapsBusinessException() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("URL gen error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.generatePresignedUrl("2024/01/01/abc.txt", 3600));
        assertTrue(ex.getMessage().contains("MinIO 预签名 URL 生成失败"));
    }
}
