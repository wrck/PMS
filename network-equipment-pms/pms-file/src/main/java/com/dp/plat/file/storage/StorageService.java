package com.dp.plat.file.storage;

import java.io.InputStream;

/**
 * 统一存储抽象。
 *
 * <p>屏蔽底层存储差异（本地磁盘 / 阿里云 OSS / MinIO），由具体实现通过
 * {@code pms.file.storage.type} 配置项切换：</p>
 * <ul>
 *     <li>{@code local}（默认）：本地磁盘</li>
 *     <li>{@code oss}：阿里云 OSS</li>
 *     <li>{@code minio}：MinIO 对象存储</li>
 * </ul>
 */
public interface StorageService {

    /**
     * 上传文件。
     *
     * @param inputStream 文件输入流
     * @param fileName    原始文件名
     * @param fileSize    文件大小（字节）
     * @param mimeType    MIME 类型
     * @return 存储路径（本地相对路径或对象存储 key）
     */
    String upload(InputStream inputStream, String fileName, long fileSize, String mimeType);

    /**
     * 下载文件。
     *
     * @param storagePath 存储路径
     * @return 文件输入流
     */
    InputStream download(String storagePath);

    /**
     * 删除文件。
     *
     * @param storagePath 存储路径
     */
    void delete(String storagePath);

    /**
     * 生成预签名访问 URL。
     *
     * <p>本地实现返回下载接口 URL；对象存储实现返回带签名的临时访问 URL。</p>
     *
     * @param storagePath  存储路径
     * @param expireSeconds 过期时间（秒）
     * @return 预签名 URL
     */
    String generatePresignedUrl(String storagePath, int expireSeconds);
}
