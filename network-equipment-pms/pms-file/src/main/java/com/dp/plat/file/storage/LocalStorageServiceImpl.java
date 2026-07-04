package com.dp.plat.file.storage;

import com.dp.plat.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地磁盘存储实现。
 *
 * <p>当 {@code pms.file.storage.type=local}（或未配置，默认值）时生效。
 * 文件按日期分目录存储：{@code ${pms.file.local.base-dir}/yyyy/MM/dd/<uuid>+<原名>}。</p>
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "pms.file.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageServiceImpl implements StorageService {

    private static final DateTimeFormatter DATE_DIR_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /** 本地存储根目录，默认 /tmp/pms-files。 */
    @Value("${pms.file.local.base-dir:/tmp/pms-files}")
    private String baseDir;

    private Path basePath;

    @PostConstruct
    public void init() throws IOException {
        this.basePath = Paths.get(baseDir).toAbsolutePath().normalize();
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
        }
        log.info("本地文件存储初始化完成，根目录: {}", basePath);
    }

    @Override
    public String upload(InputStream inputStream, String fileName, long fileSize, String mimeType) {
        try {
            String dateDir = LocalDate.now().format(DATE_DIR_FORMAT);
            String uniqueName = UUID.randomUUID().toString().replace("-", "") + "_" + sanitizeFileName(fileName);
            // storagePath 形如 yyyy/MM/dd/<uuid>_<原名>，相对根目录
            String storagePath = dateDir + "/" + uniqueName;
            Path target = basePath.resolve(storagePath);
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return storagePath;
        } catch (IOException e) {
            throw new BusinessException("本地文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public InputStream download(String storagePath) {
        try {
            Path target = basePath.resolve(storagePath).normalize();
            if (!target.startsWith(basePath) || !Files.exists(target)) {
                throw new BusinessException("文件不存在: " + storagePath);
            }
            return new FileInputStream(target.toFile());
        } catch (IOException e) {
            throw new BusinessException("本地文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Path target = basePath.resolve(storagePath).normalize();
            if (!target.startsWith(basePath)) {
                throw new BusinessException("非法的存储路径: " + storagePath);
            }
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new BusinessException("本地文件删除失败: " + e.getMessage());
        }
    }

    @Override
    public String generatePresignedUrl(String storagePath, int expireSeconds) {
        // 本地实现返回下载接口 URL，过期时间由服务端会话控制忽略
        return "/api/file/download?path=" + URLEncoder.encode(storagePath, StandardCharsets.UTF_8);
    }

    /** 去除文件名中的路径分隔符，防止目录穿越。 */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "file";
        }
        return fileName.replace("/", "_").replace("\\", "_").replace("..", "_");
    }
}
