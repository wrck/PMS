package com.dp.plat.file.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.dp.plat.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云 OSS 存储实现。
 *
 * <p>当 {@code pms.file.storage.type=oss} 时生效。需要配置：</p>
 * <ul>
 *     <li>{@code pms.file.oss.endpoint}</li>
 *     <li>{@code pms.file.oss.access-key-id}</li>
 *     <li>{@code pms.file.oss.access-key-secret}</li>
 *     <li>{@code pms.file.oss.bucket-name}</li>
 * </ul>
 *
 * <p>注意：aliyun-sdk-oss 为 optional 依赖，下游模块按需引入。</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "pms.file.storage.type", havingValue = "oss")
public class OssStorageServiceImpl implements StorageService {

    private static final DateTimeFormatter DATE_DIR_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${pms.file.oss.endpoint}")
    private String endpoint;

    @Value("${pms.file.oss.access-key-id}")
    private String accessKeyId;

    @Value("${pms.file.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${pms.file.oss.bucket-name}")
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        this.ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        log.info("阿里云 OSS 存储初始化完成，endpoint={}, bucket={}", endpoint, bucketName);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    @Override
    public String upload(InputStream inputStream, String fileName, long fileSize, String mimeType) {
        try {
            String storagePath = buildStoragePath(fileName);
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(fileSize);
            if (mimeType != null) {
                meta.setContentType(mimeType);
            }
            PutObjectRequest request = new PutObjectRequest(bucketName, storagePath, inputStream, meta);
            ossClient.putObject(request);
            return storagePath;
        } catch (Exception e) {
            throw new BusinessException("OSS 文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(String storagePath) {
        try {
            OSSObject object = ossClient.getObject(new GetObjectRequest(bucketName, storagePath));
            return object.getObjectContent();
        } catch (Exception e) {
            throw new BusinessException("OSS 文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            ossClient.deleteObject(bucketName, storagePath);
        } catch (Exception e) {
            throw new BusinessException("OSS 文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String storagePath, int expireSeconds) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, storagePath);
            request.setExpiration(expiration);
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } catch (Exception e) {
            throw new BusinessException("OSS 预签名 URL 生成失败: " + e.getMessage());
        }
    }

    private String buildStoragePath(String fileName) {
        String dateDir = LocalDate.now().format(DATE_DIR_FORMAT);
        String uniqueName = UUID.randomUUID().toString().replace("-", "") + "_" + (fileName == null ? "file" : fileName);
        return dateDir + "/" + uniqueName;
    }
}
