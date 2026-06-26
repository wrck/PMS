package com.dp.plat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dp.plat.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体 - 对应老系统 fnd_file_info 表
 */
@Data
@TableName("fnd_file_info")
public class SysFileInfo extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    @TableField("fileName")
    private String fileName;

    /** 存储路径 */
    @TableField("filePath")
    private String filePath;

    /** 文件大小(字节) */
    @TableField("fileSize")
    private Long fileSize;

    /** 文件类型 */
    @TableField("fileType")
    private String fileType;

    /** 上传人 */
    @TableField("uploadBy")
    private String uploadBy;

    /** 上传时间 */
    @TableField("uploadTime")
    private LocalDateTime uploadTime;
}
