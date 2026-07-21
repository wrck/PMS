package com.dp.plat.file.spi;

import com.dp.plat.common.dto.StoredBusinessFile;
import com.dp.plat.common.spi.BusinessFileStorage;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.service.IAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** 文件模块对业务文件存储端口的实现。 */
@Component
@RequiredArgsConstructor
public class BusinessFileStorageImpl implements BusinessFileStorage {
    private final IAttachmentService attachmentService;

    @Override
    public StoredBusinessFile upload(MultipartFile file, String businessType, Long businessId) {
        Attachment attachment = attachmentService.upload(file, businessType, businessId);
        return StoredBusinessFile.builder()
                .attachmentId(attachment.getId())
                .fileName(attachment.getFileName())
                .accessPath("/api/file/" + attachment.getId() + "/download")
                .uploadedBy(attachment.getUploadUserId())
                .build();
    }

    @Override
    public void delete(Long attachmentId) {
        attachmentService.delete(attachmentId);
    }
}
