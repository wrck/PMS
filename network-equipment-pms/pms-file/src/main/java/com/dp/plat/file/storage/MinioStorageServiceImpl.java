package com.dp.plat.file.storage;

import com.dp.plat.common.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 对象存储实现。
 *
 * <p>当 {@code pms.file.storage.type=minio} 时生效。需要配置：</p>
 * <ul>
 *     <li>{@code pms.file.minio.endpoint}</li>
 *     <li>{@code pms.file.minio.access-key}</li>
 *     <li>{@code pms.file.minio.secret-key}</li>
 *     <li>{@code pms.file.minio.bucket-name}</li>
 * </ul>
 *
 * <p>注意：minio 为 optional 依赖，下游模块按需引入。</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "pms.file.storage.type", havingValue = "minio")
public class MinioStorageServiceImpl implements StorageService {

    private static final DateTimeFormatter DATE_DIR_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${pms.file.minio.endpoint}")
    private String endpoint;

    @Value("${pms.file.minio.access-key}")
    private String accessKey;

    @Value("${pms.file.minio.secret-key}")
    private String secretKey;

    @Value("${pms.file.minio.bucket-name}")
    private String bucketName;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO bucket 自动创建: {}", bucketName);
            }
        } catch (Exception e) {
            throw new BusinessException("MinIO bucket 校验失败: " + e.getMessage());
        }
        log.info("MinIO 存储初始化完成，endpoint={}, bucket={}", endpoint, bucketName);
    }

    @Override
    public String upload(InputStream inputStream, String fileName, long fileSize, String mimeType) {
        try {
            String storagePath = buildStoragePath(fileName);
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storagePath)
                    .stream(inputStream, fileSize, -1);
            if (mimeType != null) {
                builder.contentType(mimeType);
            }
            minioClient.putObject(builder.build());
            return storagePath;
        } catch (Exception e) {
            throw new BusinessException("MinIO 文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(String storagePath) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storagePath)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("MinIO 文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storagePath)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("MinIO 文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String storagePath, int expireSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(storagePath)
                    .expiry(expireSeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("MinIO 预签名 URL 生成失败: " + e.getMessage());
        }
    }

    private String buildStoragePath(String fileName) {
        String dateDir = LocalDate.now().format(DATE_DIR_FORMAT);
        String uniqueName = UUID.randomUUID().toString().replace("-", "") + "_" + (fileName == null ? "file" : fileName);
        return dateDir + "/" + uniqueName;
    }
}
