package com.dp.plat.file.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.dp.plat.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link OssStorageServiceImpl} 的单元测试。
 *
 * <p>{@link OSS} 为接口，可直接 mock。通过 {@link ReflectionTestUtils#setField}
 * 注入配置字段和 mock 的 {@code ossClient}，跳过 {@code @PostConstruct init()}
 * 中的 {@code OSSClientBuilder.build()} 真实客户端创建。</p>
 *
 * <p>覆盖上传、下载、删除、预签名 URL 生成，以及 OSS 异常包装为 BusinessException 的逻辑。</p>
 */
@ExtendWith(MockitoExtension.class)
class OssStorageServiceImplTest {

    private static final String BUCKET = "test-bucket";
    private static final String ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com";

    @Mock
    private OSS ossClient;

    private OssStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new OssStorageServiceImpl();
        ReflectionTestUtils.setField(storageService, "endpoint", ENDPOINT);
        ReflectionTestUtils.setField(storageService, "accessKeyId", "test-ak");
        ReflectionTestUtils.setField(storageService, "accessKeySecret", "test-sk");
        ReflectionTestUtils.setField(storageService, "bucketName", BUCKET);
        ReflectionTestUtils.setField(storageService, "ossClient", ossClient);
    }

    private static final Pattern PATH_PATTERN =
            Pattern.compile("\\d{4}/\\d{2}/\\d{2}/[a-f0-9]+_test\\.txt");

    // ==================== upload ====================

    @Test
    @DisplayName("upload: 正常上传，调用 ossClient.putObject 并返回 storagePath")
    void upload_normal_callsPutObject() {
        byte[] content = "hello".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        when(ossClient.putObject(any(PutObjectRequest.class))).thenReturn(null);

        String storagePath = storageService.upload(input, "test.txt", content.length, "text/plain");

        assertNotNull(storagePath);
        assertTrue(PATH_PATTERN.matcher(storagePath).matches(),
                "storagePath 应为 yyyy/MM/dd/<uuid>_test.txt 格式");
        verify(ossClient, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("upload: mimeType 为 null 时不设置 contentType，仍正常上传")
    void upload_nullMimeType_success() {
        byte[] content = "data".getBytes();
        InputStream input = new ByteArrayInputStream(content);

        when(ossClient.putObject(any(PutObjectRequest.class))).thenReturn(null);

        String storagePath = storageService.upload(input, "file.bin", content.length, null);

        assertNotNull(storagePath);
        verify(ossClient, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("upload: OSS putObject 抛异常时包装为 BusinessException")
    void upload_ossThrows_wrapsBusinessException() {
        InputStream input = new ByteArrayInputStream("data".getBytes());

        when(ossClient.putObject(any(PutObjectRequest.class)))
                .thenThrow(new RuntimeException("OSS connection error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.upload(input, "test.txt", 4L, "text/plain"));
        assertTrue(ex.getMessage().contains("OSS 文件上传失败"));
    }

    // ==================== download ====================

    @Test
    @DisplayName("download: 正常下载返回文件输入流")
    void download_normal_returnsStream() throws Exception {
        byte[] content = "oss content".getBytes();
        OSSObject ossObject = new OSSObject();
        ossObject.setObjectContent(new ByteArrayInputStream(content));
        when(ossClient.getObject(any(GetObjectRequest.class))).thenReturn(ossObject);

        InputStream result = storageService.download("2024/01/01/abc.txt");

        assertNotNull(result);
        assertArrayEquals(content, result.readAllBytes());
        verify(ossClient, times(1)).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("download: OSS getObject 抛异常时包装为 BusinessException")
    void download_ossThrows_wrapsBusinessException() {
        when(ossClient.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("OSS download error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.download("2024/01/01/abc.txt"));
        assertTrue(ex.getMessage().contains("OSS 文件下载失败"));
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete: 正常删除，调用 ossClient.deleteObject")
    void delete_normal_callsDeleteObject() {
        storageService.delete("2024/01/01/abc.txt");

        verify(ossClient, times(1)).deleteObject(BUCKET, "2024/01/01/abc.txt");
    }

    @Test
    @DisplayName("delete: OSS deleteObject 抛异常时包装为 BusinessException")
    void delete_ossThrows_wrapsBusinessException() {
        doThrow(new RuntimeException("OSS delete error"))
                .when(ossClient).deleteObject(anyString(), anyString());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.delete("2024/01/01/abc.txt"));
        assertTrue(ex.getMessage().contains("OSS 文件删除失败"));
    }

    // ==================== generatePresignedUrl ====================

    @Test
    @DisplayName("generatePresignedUrl: 正常生成预签名 URL")
    void generatePresignedUrl_normal_returnsUrl() throws Exception {
        URL mockUrl = new URL("https://test-bucket.oss-cn-hangzhou.aliyuncs.com/abc?signature=xxx");
        when(ossClient.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

        String url = storageService.generatePresignedUrl("2024/01/01/abc.txt", 3600);

        assertNotNull(url);
        assertTrue(url.contains("signature=xxx"));
        verify(ossClient, times(1)).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
    }

    @Test
    @DisplayName("generatePresignedUrl: OSS 生成抛异常时包装为 BusinessException")
    void generatePresignedUrl_ossThrows_wrapsBusinessException() {
        when(ossClient.generatePresignedUrl(any(GeneratePresignedUrlRequest.class)))
                .thenThrow(new RuntimeException("URL gen error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> storageService.generatePresignedUrl("2024/01/01/abc.txt", 3600));
        assertTrue(ex.getMessage().contains("OSS 预签名 URL 生成失败"));
    }

    // ==================== destroy ====================

    @Test
    @DisplayName("destroy: 调用 ossClient.shutdown")
    void destroy_callsShutdown() {
        storageService.destroy();
        verify(ossClient, times(1)).shutdown();
    }

    @Test
    @DisplayName("destroy: ossClient 为 null 时不抛异常")
    void destroy_nullClient_noException() {
        OssStorageServiceImpl serviceWithNullClient = new OssStorageServiceImpl();
        // ossClient 为 null（未调用 init）
        serviceWithNullClient.destroy();
        // 不抛异常即通过
        verify(ossClient, never()).shutdown();
    }
}
