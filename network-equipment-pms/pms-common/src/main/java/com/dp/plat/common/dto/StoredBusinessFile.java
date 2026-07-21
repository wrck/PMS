package com.dp.plat.common.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/** 文件模块上传后返回给业务模块的稳定契约。 */
@Data
@Builder
public class StoredBusinessFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long attachmentId;
    private String fileName;
    private String accessPath;
    private Long uploadedBy;
}
