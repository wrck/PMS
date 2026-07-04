package com.dp.plat.file.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 文件附件实体。
 *
 * <p>统一管理各业务模块（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE 等）的附件，
 * 记录文件元数据、存储位置、上传人、MD5 校验，以及图片类附件的 GPS EXIF 信息和围栏比对结果。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("pms_attachment")
public class Attachment extends BaseEntity {

    /** 业务类型（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE 等）。 */
    private String bizType;

    /** 业务对象 id。 */
    private Long bizId;

    /** 文件名。 */
    private String fileName;

    /** 文件大小（字节）。 */
    private Long fileSize;

    /** MIME 类型。 */
    private String mimeType;

    /** 上传人 id。 */
    private Long uploadUserId;

    /** 上传人姓名。 */
    private String uploadUserName;

    /** 上传时间。 */
    private LocalDateTime uploadTime;

    /** 文件 MD5 摘要。 */
    private String md5;

    /** 存储路径（本地相对路径或对象存储 key）。 */
    private String storagePath;

    /** 存储类型（LOCAL/OSS/MINIO）。 */
    private String storageType;

    /** GPS 纬度（精度 10,7）。 */
    private BigDecimal gpsLatitude;

    /** GPS 经度（精度 10,7）。 */
    private BigDecimal gpsLongitude;

    /** 照片拍摄时间（来自 EXIF）。 */
    private LocalDateTime photoTakenAt;

    /** GPS 围栏比对结果（NORMAL/ABNORMAL）。 */
    private String geoFenceStatus;
}
