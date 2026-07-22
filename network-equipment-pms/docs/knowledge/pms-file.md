# pms-file 模块知识库

> 源码路径：`/workspace/network-equipment-pms/pms-file`
> 基础包名：`com.dp.plat.file`
> 父项目：`com.dp.plat:network-equipment-pms:1.0.0-SNAPSHOT`

---

## 模块概述

`pms-file` 是网络设备 PMS 平台的**文件附件管理领域模块**，承担跨业务模块（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE 等）的附件统一存储、元数据登记、图片 GPS EXIF 解析、地理围栏比对与缩略图生成等职责。

- **Maven 坐标**：`com.dp.plat:pms-file:1.0.0-SNAPSHOT`，父工程为 `com.dp.plat:network-equipment-pms`。
- **artifactId / name**：`pms-file`，description 为 `File attachment management: unified storage abstraction, metadata, GPS EXIF, thumbnail`。
- **打包类型**：默认 `jar`（pom.xml 未显式声明 packaging，Maven 默认 `jar`）。
- **核心职责**：
  1. **统一存储抽象**：通过 `StorageService` 屏蔽底层差异，支持本地磁盘 / 阿里云 OSS / MinIO 三种实现，由 `pms.file.storage.type` 配置项切换；
  2. **附件元数据管理**：以 `pms_attachment` 表为中心，记录业务类型、业务对象 ID、文件名、大小、MIME、MD5 摘要、存储路径、上传人等元数据；
  3. **图片 GPS EXIF 解析**：基于 metadata-extractor 库，从 JPEG 图片中提取纬度、经度、拍摄时间；
  4. **地理围栏校验**：使用 Haversine 公式计算照片拍摄坐标与站点坐标的球面距离，超过围栏半径标记为 ABNORMAL；
  5. **缩略图生成**：基于 Thumbnailator 库，按指定宽高生成 PNG 缩略图；
  6. **跨模块 SPI 实现**：实现 `pms-common` 的 `BusinessFileStorage` 端口，避免业务模块直接依赖 `pms-file`。

---

## 包结构

`com.dp.plat.file` 下的子包组织如下：

| 子包 | 主要内容 |
|------|----------|
| `controller` | REST 控制器：`FileController`（上传/下载/缩略图/删除/按业务查询） |
| `entity` | 领域实体：`Attachment`（表 `pms_attachment`） |
| `mapper` | MyBatis-Plus Mapper：`AttachmentMapper`（继承 `BaseMapper`，无自定义 SQL） |
| `service` | 服务接口：`IAttachmentService`、地理围栏服务 `GeoFenceService` |
| `service.impl` | 服务实现：`AttachmentServiceImpl` |
| `storage` | 存储抽象与三种实现：`StorageService`（接口）、`LocalStorageServiceImpl`、`OssStorageServiceImpl`、`MinioStorageServiceImpl` |
| `exif` | EXIF GPS 提取器：`GpsExifExtractor`（含内部 `GpsInfo` 载体类） |
| `preview` | 缩略图服务：`ThumbnailService` |
| `spi` | 跨模块端口实现：`BusinessFileStorageImpl`（实现 `pms-common` 的 `BusinessFileStorage`） |

实体 `Attachment` 继承 `com.dp.plat.common.entity.BaseEntity`，公共字段为：`id`（`IdType.AUTO`）、`createTime`、`updateTime`、`createBy`、`updateBy`、`deleted`（`@TableLogic` 逻辑删除）。

---

## 核心实体模型

### Attachment — 文件附件（`pms_attachment`）

建表脚本：`pms-admin/src/main/resources/db/migration/V21__init_attachment_tables.sql`，索引补充脚本：`V23__add_core_indexes.sql`、`V81__fix_missing_sys_menu_perms.sql`。

| 字段 | Java 类型 | 数据库列 | DB 类型 | 说明 |
|------|-----------|----------|---------|------|
| `id` | Long | `id` | BIGINT, AUTO_INCREMENT, PK | 主键（继承自 `BaseEntity`） |
| `bizType` | String | `biz_type` | VARCHAR(50), NOT NULL | 业务类型（PUNCH_LIST/RMA/DELIVERABLE/IMPL_PROGRESS/ACCEPTANCE 等） |
| `bizId` | Long | `biz_id` | BIGINT | 业务对象 id（可空，先上传后绑定） |
| `fileName` | String | `file_name` | VARCHAR(255), NOT NULL | 原始文件名 |
| `fileSize` | Long | `file_size` | BIGINT | 文件大小（字节） |
| `mimeType` | String | `mime_type` | VARCHAR(100) | MIME 类型 |
| `uploadUserId` | Long | `upload_user_id` | BIGINT | 上传人 id（取自 `SecurityUtils.getCurrentUserId()`） |
| `uploadUserName` | String | `upload_user_name` | VARCHAR(100) | 上传人姓名（取自 `SecurityUtils.getCurrentUsername()`） |
| `uploadTime` | LocalDateTime | `upload_time` | DATETIME | 上传时间（服务端 `LocalDateTime.now()`） |
| `md5` | String | `md5` | VARCHAR(64) | 文件 MD5 摘要（流式计算，`HexFormat` 输出 32 位十六进制） |
| `storagePath` | String | `storage_path` | VARCHAR(500), NOT NULL | 存储路径（本地相对路径或对象存储 key） |
| `storageType` | String | `storage_type` | VARCHAR(20) | 存储类型（LOCAL/OSS/MINIO） |
| `gpsLatitude` | BigDecimal | `gps_latitude` | DECIMAL(10,7) | GPS 纬度（精度 7 位小数） |
| `gpsLongitude` | BigDecimal | `gps_longitude` | DECIMAL(10,7) | GPS 经度（精度 7 位小数） |
| `photoTakenAt` | LocalDateTime | `photo_taken_at` | DATETIME | 照片拍摄时间（来自 EXIF TAG_DATETIME_ORIGINAL） |
| `geoFenceStatus` | String | `geo_fence_status` | VARCHAR(20) | GPS 围栏比对结果（NORMAL/ABNORMAL） |
| `createBy` | String | `create_by` | VARCHAR(64) | 创建人（继承自 `BaseEntity`，自动填充） |
| `createTime` | LocalDateTime | `create_time` | DATETIME | 创建时间（继承自 `BaseEntity`，自动填充） |
| `updateBy` | String | `update_by` | VARCHAR(64) | 更新人（继承自 `BaseEntity`，自动填充） |
| `updateTime` | LocalDateTime | `update_time` | DATETIME | 更新时间（继承自 `BaseEntity`，自动填充） |
| `deleted` | Integer | `deleted` | TINYINT, DEFAULT 0 | 逻辑删除标记（0=未删除，1=已删除，`@TableLogic`） |

**索引**：
- `PRIMARY KEY (id)`
- `idx_biz (biz_type, biz_id)` — V21 建表时创建
- `idx_md5 (md5)` — V21 建表时创建
- `idx_pms_attachment_biz (biz_type, biz_id)` — V23 补充（与 `idx_biz` 同语义）
- `idx_pms_attachment_create_by_time (create_by, create_time)` — V23 补充

**关系**：`Attachment` 通过 `(biz_type, biz_id)` 与各业务模块（如 `pms_deliverable`、`pms_rma` 等）弱关联，无物理外键约束。

---

## 存储策略

`pms-file` 通过 `StorageService` 接口屏蔽底层存储差异，提供三种可插拔实现，通过 Spring `@ConditionalOnProperty(name = "pms.file.storage.type")` 在容器启动时按配置自动装配唯一实现。

### StorageService 接口

```java
public interface StorageService {
    String upload(InputStream inputStream, String fileName, long fileSize, String mimeType);
    InputStream download(String storagePath);
    void delete(String storagePath);
    String generatePresignedUrl(String storagePath, int expireSeconds);
}
```

### 三种实现对比

| 实现类 | 配置值 | 装配条件 | 必需配置项 | 依赖（pom.xml） | storagePath 格式 |
|--------|--------|----------|------------|-----------------|------------------|
| `LocalStorageServiceImpl` | `local` | `pms.file.storage.type=local` 或未配置（`matchIfMissing=true`，**默认**） | `pms.file.local.base-dir`（默认 `/tmp/pms-files`） | 无外部依赖 | `yyyy/MM/dd/<uuid>_<原名>`（相对根目录） |
| `OssStorageServiceImpl` | `oss` | `pms.file.storage.type=oss` | `pms.file.oss.endpoint` / `access-key-id` / `access-key-secret` / `bucket-name` | `com.aliyun.oss:aliyun-sdk-oss:3.17.4`（`optional=true`） | `yyyy/MM/dd/<uuid>_<原名>`（OSS object key） |
| `MinioStorageServiceImpl` | `minio` | `pms.file.storage.type=minio` | `pms.file.minio.endpoint` / `access-key` / `secret-key` / `bucket-name` | `io.minio:minio:8.5.10`（`optional=true`） | `yyyy/MM/dd/<uuid>_<原名>`（MinIO object key） |

### LocalStorageServiceImpl 细节

- **根目录初始化**：`@PostConstruct init()` 读取 `pms.file.local.base-dir`，转换为绝对路径并 `Files.createDirectories` 创建目录。
- **路径穿越防护**：
  - `sanitizeFileName(fileName)` 去除文件名中的 `/`、`\`、`..`，防止目录穿越攻击；
  - `download` / `delete` 中通过 `target.startsWith(basePath)` 二次校验解析后的绝对路径仍在根目录下。
- **预签名 URL**：本地实现返回 `/api/file/download?path=<urlencoded>`，过期时间被忽略（由服务端会话控制）。

### OssStorageServiceImpl 细节

- **客户端初始化**：`@PostConstruct init()` 使用 `OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)` 创建 `OSS` 客户端。
- **客户端销毁**：`@PreDestroy destroy()` 调用 `ossClient.shutdown()` 释放资源。
- **上传元数据**：`ObjectMetadata` 设置 `ContentLength` 和 `ContentType`，通过 `PutObjectRequest` 上传。
- **预签名 URL**：`GeneratePresignedUrlRequest` 设置过期时间 `Date`，调用 `ossClient.generatePresignedUrl(request)` 返回临时访问 URL。

### MinioStorageServiceImpl 细节

- **客户端初始化**：`@PostConstruct init()` 使用 `MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()` 创建客户端；并自动校验/创建 bucket（`bucketExists` → `makeBucket`）。
- **上传参数**：`PutObjectArgs` 设置 bucket、object、stream（含 `fileSize`、partSize=-1）和 `contentType`。
- **预签名 URL**：`GetPresignedObjectUrlArgs` 设置 `Method.GET`、bucket、object 和 `expiry(expireSeconds, TimeUnit.SECONDS)`。

### 配置示例（`pms-admin/src/main/resources/application.yml`）

```yaml
pms:
  file:
    storage:
      type: local
    local:
      base-dir: ${PMS_FILE_LOCAL_BASE_DIR:./pms-files}
```

生产环境通过环境变量 `PMS_FILE_LOCAL_BASE_DIR` 覆盖本地存储根目录；切换到 OSS/MinIO 需补充对应配置块并设置 `type: oss` 或 `type: minio`，同时下游模块（如 `pms-admin`）需显式引入 `aliyun-sdk-oss` 或 `minio` 依赖（pom.xml 中标注为 `optional=true`，不传递）。

---

## 文件上传机制

### 上传入口

上传统一通过 `FileController.upload` 接收 `MultipartFile`，根据是否传入站点坐标 `siteLat`/`siteLng` 路由到两个服务方法：

```java
if (siteLat != null && siteLng != null) {
    attachment = attachmentService.uploadWithGeoFence(file, bizType, bizId, siteLat, siteLng, fenceRadius);
} else {
    attachment = attachmentService.upload(file, bizType, bizId);
}
```

### 上传流程（`AttachmentServiceImpl.uploadWithGeoFence`）

整个上传流程在 `@Transactional(rollbackFor = Exception.class)` 事务中执行：

1. **参数校验** `validateUploadRequest(file, bizType)`：
   - `file == null || file.isEmpty()` → 抛 `BusinessException("上传文件不能为空")`；
   - `bizType == null || bizType.isEmpty()` → 抛 `BusinessException("业务类型不能为空")`。
2. **MD5 摘要计算** `computeMd5(file)`：使用 `MessageDigest.getInstance("MD5")` 流式读取（8192 字节缓冲），通过 `HexFormat.of().formatHex(digest.digest())` 输出 32 位小写十六进制字符串。
3. **存储上传** `storageService.upload(uploadStream, fileName, fileSize, mimeType)`：返回 `storagePath`；失败抛 `BusinessException("文件上传失败: ...")`，事务回滚。
4. **EXIF GPS 解析**（仅当 `mimeType.startsWith("image/")`）：
   - 调用 `gpsExifExtractor.extract(exifStream)` 解析 GPS 纬度/经度/拍摄时间；
   - 解析失败仅 `log.warn` 不影响上传（`geoFenceStatus` 默认 `NORMAL`）；
   - 若传入 `siteLat`/`siteLng` 且 GPS 解析成功，调用 `geoFenceService.checkFence(...)` 做围栏比对。
5. **保存元数据记录**：构建 `Attachment` 对象（含 `uploadUserId`/`uploadUserName` 取自 `SecurityUtils`、`uploadTime` 取 `LocalDateTime.now()`、`storageType` 由 `resolveStorageType()` 推断），调用 `this.save(attachment)` 持久化。
6. **返回**：返回保存后的 `Attachment`（含自增 `id`）。

### 存储类型推断（`resolveStorageType`）

根据注入的 `StorageService` 实现类的简单名推断：
- 包含 `Oss` → `"OSS"`
- 包含 `Minio` → `"MINIO"`
- 其他 → `"LOCAL"`

### 分片上传 / 断点续传

当前模块**未实现**分片上传和断点续传，所有上传均为单次完整文件流上传。MD5 摘要可用于后续秒传判定（数据库 `idx_md5` 索引支持按 MD5 查询去重），但 `IAttachmentService` 接口当前未暴露秒传检查方法。

---

## 文件下载与访问控制

### 下载接口

`GET /api/file/{id}/download`：
- 权限：`@PreAuthorize("hasAuthority('file:attachment:download')")`
- 流程：
  1. `attachmentService.getById(id)` 加载附件元数据，不存在抛 `BusinessException("附件不存在")`；
  2. 设置 `Content-Type`（取 `attachment.getMimeType()`，缺失时用 `application/octet-stream`）；
  3. 设置 `Content-Disposition: attachment; filename="<urlencoded 文件名>"`（UTF-8 URL 编码，支持中文文件名）；
  4. 调用 `attachmentService.download(id)` 获取 `InputStream`，以 8192 字节缓冲流式写入 `HttpServletResponse` 输出流。

### 缩略图接口

`GET /api/file/{id}/thumbnail?width=200&height=200`：
- 权限：`file:attachment:download`（与下载同权限）
- 流程：
  1. `attachmentService.generateThumbnail(id, width, height)` 加载附件，校验 `mimeType.startsWith("image/")`，非图片抛 `BusinessException("非图片附件，无法生成缩略图")`；
  2. 通过 `storageService.download(storagePath)` 获取原图输入流；
  3. 调用 `thumbnailService.generate(input, width, height)` 生成 PNG 缩略图字节；
  4. 设置 `Content-Type: image/png`，输出字节流。

### 预签名 URL

`StorageService.generatePresignedUrl(storagePath, expireSeconds)`：
- 本地实现：返回 `/api/file/download?path=<urlencoded>`，过期时间忽略；
- OSS/MinIO 实现：返回带签名的临时访问 URL，客户端可直接通过对象存储访问，无需经过应用服务器代理下载。

### 权限点（菜单/权限初始化于 `V24__init_permissions.sql` + `V81__fix_missing_sys_menu_perms.sql`）

| 权限码 | 类型 | 说明 |
|--------|------|------|
| `file:attachment:list` | 菜单/功能 | 查询附件列表 |
| `file:attachment:upload` | 功能 | 上传附件 |
| `file:attachment:download` | 功能 | 下载/查看附件（V81 补充注册） |
| `file:attachment:remove` | 功能 | 删除附件 |

菜单层级：`File (M, id=800)` → `Attachment (C, id=801, perms=file:attachment:list, 前端路由 /file/attachment/index)` → `Attachment Upload (F, perms=file:attachment:upload)` / `Attachment Delete (F, perms=file:attachment:remove)`。

---

## Service 层与 API 端点

### IAttachmentService 接口

继承 `IService<Attachment>`（MyBatis-Plus），自定义方法：

| 方法签名 | 说明 |
|----------|------|
| `Attachment upload(MultipartFile file, String bizType, Long bizId)` | 上传文件（内部委托 `uploadWithGeoFence(file, bizType, bizId, null, null, 0d)`） |
| `Attachment uploadWithGeoFence(MultipartFile file, String bizType, Long bizId, BigDecimal siteLat, BigDecimal siteLng, double fenceRadiusMeters)` | 上传文件并指定站点坐标做 GPS 围栏校验 |
| `InputStream download(Long attachmentId)` | 下载附件，返回文件输入流 |
| `boolean delete(Long attachmentId)` | 删除附件（先删存储再删记录；存储删除失败仅 warn 不阻断） |
| `List<Attachment> listByBiz(String bizType, Long bizId)` | 按业务类型和对象 id 查询附件列表（按 `id` 降序） |
| `byte[] generateThumbnail(Long attachmentId, int width, int height)` | 生成 PNG 缩略图字节 |

继承自 `IService<Attachment>` 的常用方法（如 `getById`、`save`、`removeById`、`list`）在 `AttachmentServiceImpl` 中直接复用。

### GeoFenceService（地理围栏服务）

`@Component`，提供 `checkFence(photoLat, photoLng, siteLat, siteLng, fenceRadiusMeters)`：
- 任一坐标为 null 返回 `STATUS_NORMAL`（"无法判定视为正常"）；
- 使用 Haversine 公式计算球面距离（地球半径 `6_371_000.0` 米）；
- 距离 ≤ 围栏半径返回 `STATUS_NORMAL`，否则返回 `STATUS_ABNORMAL`。

### GpsExifExtractor（EXIF GPS 提取器）

`@Component`，基于 `com.drewnoakes:metadata-extractor:2.19.0`：
- `extract(InputStream)` 返回内部类 `GpsInfo(latitude, longitude, takenAt)`，无 EXIF 或解析失败返回 `null`；
- 输入流自动包装为 `BufferedInputStream`（metadata-extractor 需要 mark/reset 支持）；
- 纬度/经度精度 7 位小数（`setScale(7, RoundingMode.HALF_UP)`）；
- 拍摄时间优先取 `ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL`，按系统时区转换为 `LocalDateTime`。

### ThumbnailService（缩略图服务）

`@Component`，基于 `net.coobird:thumbnailator:0.4.20`：
- `generate(InputStream, int width, int height)` — 按比例缩放至指定宽高范围内（不拉伸变形），输出 PNG 字节；
- `generate(byte[], int, int)` — 重载，从字节数组生成。

### SPI 实现 BusinessFileStorageImpl

`@Component`，实现 `pms-common` 的 `BusinessFileStorage` 端口：

| 方法 | 实现 |
|------|------|
| `StoredBusinessFile upload(MultipartFile file, String businessType, Long businessId)` | 委托 `attachmentService.upload(...)`，返回 `StoredBusinessFile`（含 `attachmentId`/`fileName`/`accessPath="/api/file/{id}/download"`/`uploadedBy`） |
| `void delete(Long attachmentId)` | 委托 `attachmentService.delete(attachmentId)` |

### API 端点清单（FileController，`@RequestMapping("/api/file")`）

| HTTP 方法 | 路径 | 权限 | OperLog | 说明 |
|-----------|------|------|---------|------|
| `POST` | `/api/file/upload` | `file:attachment:upload` | `title="文件附件", businessType=1` | 上传文件（支持站点坐标围栏校验） |
| `GET` | `/api/file/{id}/download` | `file:attachment:download` | — | 下载文件 |
| `GET` | `/api/file/{id}/thumbnail` | `file:attachment:download` | — | 生成缩略图（默认 200×200） |
| `DELETE` | `/api/file/{id}` | `file:attachment:remove` | `title="文件附件", businessType=3` | 删除文件（先删存储再删记录） |
| `GET` | `/api/file/biz?bizType=&bizId=` | `file:attachment:list` | — | 按业务查询附件列表 |

---

## 模块依赖关系

### pms-file 自身依赖

| 依赖 | 类型 | 用途 |
|------|------|------|
| `com.dp.plat:pms-common` | 内部模块（必选） | `BaseEntity`、`BusinessException`、`SecurityUtils`、`Result`、`@OperLog`、SPI 端口 `BusinessFileStorage`、DTO `StoredBusinessFile` |
| `org.springframework.boot:spring-boot-starter-web` | 第三方（必选） | `MultipartFile`、`@RestController`、`@RequestMapping` |
| `com.baomidou:mybatis-plus-spring-boot3-starter` | 第三方（必选） | `IService`/`ServiceImpl`/`BaseMapper`/`LambdaQueryWrapper` |
| `com.drewnoakes:metadata-extractor:2.19.0` | 第三方（必选） | EXIF GPS 解析 |
| `net.coobird:thumbnailator:0.4.20` | 第三方（必选） | 缩略图生成 |
| `com.aliyun.oss:aliyun-sdk-oss:3.17.4` | 第三方（`optional=true`） | 阿里云 OSS 存储实现，下游按需引入 |
| `io.minio:minio:8.5.10` | 第三方（`optional=true`） | MinIO 对象存储实现，下游按需引入 |
| `javax.xml.bind:jaxb-api:2.3.1` | 第三方（`optional=true`） | 部分 JDK JAXB 兼容 |
| `org.springframework.boot:spring-boot-starter-test` | 第三方（test） | 单元测试 |

### 依赖 pms-file 的模块

| 模块 | 依赖方式 |
|------|----------|
| `pms-admin` | 直接依赖（聚合启动模块，集成所有业务模块） |
| `pms-implementation` | 直接依赖（实施管理领域，使用附件存储） |
| `pms-asset` | 直接依赖（设备资产领域，RMA/质保等附件） |
| `pms-lowcode` | 直接依赖（低代码平台文件上传组件对接） |

### 跨模块 SPI 解耦

业务模块（如 `pms-deliverable`）通过 `pms-common` 定义的 SPI 端口 `BusinessFileStorage` 间接调用 `pms-file`，避免领域模块直接依赖 `pms-file`：

```
pms-deliverable --注入--> BusinessFileStorage (pms-common 接口)
                                  ↑
                          BusinessFileStorageImpl (pms-file 实现, @Component)
                                  ↓
                          IAttachmentService (pms-file)
```

例如 `DeliverableServiceImpl` 通过 `@RequiredArgsConstructor` 注入 `BusinessFileStorage`，调用 `upload(file, "DELIVERABLE", deliverableId)` 完成交付件附件上传，无需 import `com.dp.plat.file.*`。

---

## 关键技术点

1. **存储抽象 + 条件装配**：`@ConditionalOnProperty(name = "pms.file.storage.type", matchIfMissing=true)` 实现三选一装配，配置切换零代码改动；OSS/MinIO 依赖 `optional=true` 不强制下游引入。

2. **MD5 流式计算 + HexFormat**：使用 JDK 17+ 的 `HexFormat.of().formatHex(...)` 输出十六进制摘要，避免 Guava/Apache Commons Codec 额外依赖；数据库 `idx_md5` 索引支持按 MD5 查询去重。

3. **目录穿越防护**：`LocalStorageServiceImpl` 在 `sanitizeFileName`（替换 `/`、`\`、`..`）和 `target.startsWith(basePath)`（解析后路径校验）两层防护，避免恶意 `../` 路径逃逸根目录。

4. **EXIF 解析容错**：`GpsExifExtractor.extract` 内部捕获所有异常返回 `null`，`AttachmentServiceImpl.uploadWithGeoFence` 对图片 EXIF 解析失败仅 `log.warn` 不影响上传主流程，`geoFenceStatus` 默认 `NORMAL`。

5. **地理围栏 Haversine 公式**：`GeoFenceService` 使用球面三角公式计算两点距离，地球半径取 6,371,000 米；任一坐标缺失返回 `NORMAL`（"无法判定视为正常"），保证上传流程不被围栏校验阻断。

6. **删除顺序保证**：`AttachmentServiceImpl.delete` 先调用 `storageService.delete(storagePath)` 删除物理文件，再调用 `this.removeById(attachmentId)` 删除元数据记录；存储删除失败仅 `log.warn` 仍继续删除元数据，避免残留孤儿记录。

7. **存储类型自推断**：`resolveStorageType()` 通过反射 `storageService.getClass().getSimpleName()` 推断存储类型字符串写入 `storage_type` 字段，无需额外配置项，但耦合实现类命名约定（`Oss`/`Minio`/其他）。

8. **事务边界**：`upload`/`uploadWithGeoFence`/`delete` 均标注 `@Transactional(rollbackFor = Exception.class)`；下载和缩略图生成等只读操作不开启事务。

9. **审计字段自动填充**：`Attachment` 继承 `BaseEntity`，`createBy`/`createTime`/`updateBy`/`updateTime`/`deleted` 由 MyBatis-Plus `MetaObjectHandler` 自动填充（实现在 `pms-common`），`@TableLogic` 实现逻辑删除。

10. **OperLog 操作留痕**：上传（`businessType=1` 新增）和删除（`businessType=3` 删除）接口标注 `@OperLog(title="文件附件")`，操作日志通过 `pms-common` 的 AOP 切面落库。

11. **预签名 URL 双语义**：本地实现返回应用内下载接口 URL（需鉴权），对象存储实现返回带签名的临时直链（绕过应用服务器）；下游调用方需根据 `storageType` 区分使用方式。

12. **SecurityUtils 降级**：`SecurityUtils.getCurrentUserId()` 在无 Spring Security 上下文时返回 `null`，`getCurrentUsername()` 返回 `"system"`，保证单元测试和离线调用不抛 NPE。
