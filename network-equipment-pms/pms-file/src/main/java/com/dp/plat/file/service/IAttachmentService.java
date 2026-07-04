package com.dp.plat.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dp.plat.file.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * 附件服务接口。
 */
public interface IAttachmentService extends IService<Attachment> {

    /**
     * 上传文件：计算 MD5、调用存储服务上传、解析图片 EXIF、做围栏比对、保存元数据。
     *
     * @param file    multipart 文件
     * @param bizType 业务类型
     * @param bizId   业务对象 id
     * @return 附件元数据记录
     */
    Attachment upload(MultipartFile file, String bizType, Long bizId);

    /**
     * 上传文件并指定站点坐标做 GPS 围栏校验。
     *
     * @param file              multipart 文件
     * @param bizType           业务类型
     * @param bizId             业务对象 id
     * @param siteLat           站点纬度
     * @param siteLng           站点经度
     * @param fenceRadiusMeters 围栏半径（米）
     * @return 附件元数据记录
     */
    Attachment uploadWithGeoFence(MultipartFile file, String bizType, Long bizId,
                                  BigDecimal siteLat, BigDecimal siteLng, double fenceRadiusMeters);

    /**
     * 下载附件。
     *
     * @param attachmentId 附件 id
     * @return 文件输入流
     */
    InputStream download(Long attachmentId);

    /**
     * 删除附件（先删存储再删记录）。
     *
     * @param attachmentId 附件 id
     * @return 是否删除成功
     */
    boolean delete(Long attachmentId);

    /**
     * 按业务查询附件列表。
     *
     * @param bizType 业务类型
     * @param bizId   业务对象 id
     * @return 附件列表
     */
    List<Attachment> listByBiz(String bizType, Long bizId);

    /**
     * 生成缩略图字节。
     *
     * @param attachmentId 附件 id
     * @param width        目标宽度
     * @param height       目标高度
     * @return PNG 缩略图字节
     */
    byte[] generateThumbnail(Long attachmentId, int width, int height);
}
