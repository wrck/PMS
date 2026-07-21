package com.dp.plat.common.spi;

import com.dp.plat.common.dto.StoredBusinessFile;
import org.springframework.web.multipart.MultipartFile;

/** 跨模块业务文件存储端口，避免领域模块直接依赖 pms-file。 */
public interface BusinessFileStorage {
    StoredBusinessFile upload(MultipartFile file, String businessType, Long businessId);
    void delete(Long attachmentId);
}
