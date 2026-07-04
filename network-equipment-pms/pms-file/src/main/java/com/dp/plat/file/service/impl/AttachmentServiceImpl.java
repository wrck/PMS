package com.dp.plat.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.util.SecurityUtils;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.exif.GpsExifExtractor;
import com.dp.plat.file.mapper.AttachmentMapper;
import com.dp.plat.file.preview.ThumbnailService;
import com.dp.plat.file.service.GeoFenceService;
import com.dp.plat.file.service.IAttachmentService;
import com.dp.plat.file.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;

/**
 * 附件服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    /** 图片 MIME 前缀。 */
    private static final String IMAGE_MIME_PREFIX = "image/";

    private final StorageService storageService;
    private final GpsExifExtractor gpsExifExtractor;
    private final GeoFenceService geoFenceService;
    private final ThumbnailService thumbnailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upload(MultipartFile file, String bizType, Long bizId) {
        return uploadWithGeoFence(file, bizType, bizId, null, null, 0d);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment uploadWithGeoFence(MultipartFile file, String bizType, Long bizId,
                                         BigDecimal siteLat, BigDecimal siteLng, double fenceRadiusMeters) {
        validateUploadRequest(file, bizType);
        String mimeType = file.getContentType();
        long fileSize = file.getSize();
        String fileName = file.getOriginalFilename();

        // 1. 计算 MD5
        String md5 = computeMd5(file);

        // 2. 调用存储服务上传，拿到 storagePath
        String storagePath;
        try (InputStream uploadStream = file.getInputStream()) {
            storagePath = storageService.upload(uploadStream, fileName, fileSize, mimeType);
        } catch (Exception e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        // 3. 解析图片 EXIF GPS 和拍摄时间
        BigDecimal gpsLatitude = null;
        BigDecimal gpsLongitude = null;
        LocalDateTime photoTakenAt = null;
        String geoFenceStatus = GeoFenceService.STATUS_NORMAL;
        if (mimeType != null && mimeType.startsWith(IMAGE_MIME_PREFIX)) {
            try (InputStream exifStream = file.getInputStream()) {
                GpsExifExtractor.GpsInfo gpsInfo = gpsExifExtractor.extract(exifStream);
                if (gpsInfo != null) {
                    gpsLatitude = gpsInfo.getLatitude();
                    gpsLongitude = gpsInfo.getLongitude();
                    photoTakenAt = gpsInfo.getTakenAt();
                    if (siteLat != null && siteLng != null) {
                        geoFenceStatus = geoFenceService.checkFence(gpsLatitude, gpsLongitude,
                                siteLat, siteLng, fenceRadiusMeters);
                    }
                }
            } catch (Exception e) {
                log.warn("EXIF 解析读取失败: {}", e.getMessage());
            }
        }

        // 4. 保存附件元数据记录
        Attachment attachment = Attachment.builder()
                .bizType(bizType)
                .bizId(bizId)
                .fileName(fileName)
                .fileSize(fileSize)
                .mimeType(mimeType)
                .uploadUserId(SecurityUtils.getCurrentUserId())
                .uploadUserName(SecurityUtils.getCurrentUsername())
                .uploadTime(LocalDateTime.now())
                .md5(md5)
                .storagePath(storagePath)
                .storageType(resolveStorageType())
                .gpsLatitude(gpsLatitude)
                .gpsLongitude(gpsLongitude)
                .photoTakenAt(photoTakenAt)
                .geoFenceStatus(geoFenceStatus)
                .build();
        this.save(attachment);
        return attachment;
    }

    @Override
    public InputStream download(Long attachmentId) {
        Attachment attachment = loadAttachment(attachmentId);
        return storageService.download(attachment.getStoragePath());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long attachmentId) {
        Attachment attachment = loadAttachment(attachmentId);
        // 先删存储再删记录
        try {
            storageService.delete(attachment.getStoragePath());
        } catch (Exception e) {
            log.warn("存储删除失败，继续删除元数据记录: {}", e.getMessage());
        }
        return this.removeById(attachmentId);
    }

    @Override
    public List<Attachment> listByBiz(String bizType, Long bizId) {
        return this.list(new LambdaQueryWrapper<Attachment>()
                .eq(Attachment::getBizType, bizType)
                .eq(Attachment::getBizId, bizId)
                .orderByDesc(Attachment::getId));
    }

    @Override
    public byte[] generateThumbnail(Long attachmentId, int width, int height) {
        Attachment attachment = loadAttachment(attachmentId);
        if (attachment.getMimeType() == null || !attachment.getMimeType().startsWith(IMAGE_MIME_PREFIX)) {
            throw new BusinessException("非图片附件，无法生成缩略图");
        }
        try (InputStream input = storageService.download(attachment.getStoragePath())) {
            return thumbnailService.generate(input, width, height);
        } catch (Exception e) {
            throw new BusinessException("缩略图生成失败: " + e.getMessage());
        }
    }

    /** 校验上传请求参数。 */
    private void validateUploadRequest(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (bizType == null || bizType.isEmpty()) {
            throw new BusinessException("业务类型不能为空");
        }
    }

    /** 计算文件 MD5。 */
    private String computeMd5(MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (Exception e) {
            throw new BusinessException("MD5 计算失败: " + e.getMessage());
        }
    }

    /** 根据注入的 StorageService 实现推断存储类型。 */
    private String resolveStorageType() {
        String implName = storageService.getClass().getSimpleName();
        if (implName.contains("Oss")) {
            return "OSS";
        }
        if (implName.contains("Minio")) {
            return "MINIO";
        }
        return "LOCAL";
    }

    /** 加载附件，不存在抛业务异常。 */
    private Attachment loadAttachment(Long attachmentId) {
        if (attachmentId == null) {
            throw new BusinessException("附件 id 不能为空");
        }
        Attachment attachment = this.getById(attachmentId);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }
        return attachment;
    }
}
