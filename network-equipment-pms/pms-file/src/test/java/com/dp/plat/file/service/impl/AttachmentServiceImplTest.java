package com.dp.plat.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.exif.GpsExifExtractor;
import com.dp.plat.file.mapper.AttachmentMapper;
import com.dp.plat.file.preview.ThumbnailService;
import com.dp.plat.file.service.GeoFenceService;
import com.dp.plat.file.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link AttachmentServiceImpl} 的单元测试。
 *
 * <p>覆盖附件上传（含 MD5 计算、EXIF GPS 解析、围栏比对）、下载、删除、按业务查询、
 * 缩略图生成，以及各类异常分支。</p>
 *
 * <p>说明：</p>
 * <ul>
 *   <li>使用 {@link Mockito#spy} 创建被测对象 spy，以便 stub 继承自 ServiceImpl 的
 *       {@code removeById(Serializable)} 单参方法 —— 该方法在纯单元测试中因 TableInfo
 *       未初始化会抛 NPE。</li>
 *   <li>{@code baseMapper} 通过 {@link ReflectionTestUtils#setField} 反射注入 mock。</li>
 *   <li>{@link com.dp.plat.common.util.SecurityUtils} 为静态工具类，无 Spring Security 上下文时
 *       {@code getCurrentUserId()} 返回 null，{@code getCurrentUsername()} 返回 "system"，
 *       不影响测试逻辑验证。</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    private static final String IMAGE_MIME = "image/jpeg";
    private static final String TEXT_MIME = "text/plain";

    @Mock
    private StorageService storageService;

    @Mock
    private GpsExifExtractor gpsExifExtractor;

    @Mock
    private GeoFenceService geoFenceService;

    @Mock
    private ThumbnailService thumbnailService;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private MultipartFile file;

    private AttachmentServiceImpl attachmentService;

    @BeforeEach
    void setUp() {
        attachmentService = Mockito.spy(new AttachmentServiceImpl(
                storageService, gpsExifExtractor, geoFenceService, thumbnailService));
        ReflectionTestUtils.setField(attachmentService, "baseMapper", attachmentMapper);
    }

    /** 构造一个非图片 multipart mock，每次 getInputStream 返回新流。 */
    private void stubNonImageFile(String content, String fileName) throws Exception {
        byte[] bytes = content.getBytes();
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(TEXT_MIME);
        when(file.getSize()).thenReturn((long) bytes.length);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenAnswer(inv -> new ByteArrayInputStream(bytes));
    }

    /** 构造一个图片 multipart mock。 */
    private void stubImageFile(byte[] content, String fileName) throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(IMAGE_MIME);
        when(file.getSize()).thenReturn((long) content.length);
        when(file.getOriginalFilename()).thenReturn(fileName);
        when(file.getInputStream()).thenAnswer(inv -> new ByteArrayInputStream(content));
    }

    // ==================== upload ====================

    @Test
    @DisplayName("upload: 正常上传非图片文件，计算 MD5 并保存元数据")
    void upload_nonImage_success() throws Exception {
        stubNonImageFile("hello", "test.txt");
        when(storageService.upload(any(InputStream.class), eq("test.txt"), eq(5L), eq(TEXT_MIME)))
                .thenReturn("2024/01/01/abc_test.txt");
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.upload(file, "RMA", 100L);

        assertNotNull(result);
        assertEquals("RMA", result.getBizType());
        assertEquals(100L, result.getBizId());
        assertEquals("test.txt", result.getFileName());
        assertEquals(5L, result.getFileSize());
        assertEquals(TEXT_MIME, result.getMimeType());
        assertEquals("5d41402abc4b2a76b9719d911017c592", result.getMd5());
        assertEquals("2024/01/01/abc_test.txt", result.getStoragePath());
        assertEquals("LOCAL", result.getStorageType());
        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
        assertNotNull(result.getUploadTime());
        verify(storageService, times(1)).upload(any(InputStream.class), eq("test.txt"), eq(5L), eq(TEXT_MIME));
        verify(attachmentMapper, times(1)).insert(any(Attachment.class));
        verify(gpsExifExtractor, never()).extract(any(InputStream.class));
    }

    @Test
    @DisplayName("upload: 图片文件有 EXIF GPS 数据，解析纬度/经度/拍摄时间")
    void upload_imageWithGps_parsesExif() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        LocalDateTime takenAt = LocalDateTime.of(2024, 6, 15, 10, 30);
        GpsExifExtractor.GpsInfo gpsInfo = new GpsExifExtractor.GpsInfo(
                new BigDecimal("39.9042000"), new BigDecimal("116.4074000"), takenAt);
        when(gpsExifExtractor.extract(any(InputStream.class))).thenReturn(gpsInfo);
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.upload(file, "PUNCH_LIST", 200L);

        assertEquals(new BigDecimal("39.9042000"), result.getGpsLatitude());
        assertEquals(new BigDecimal("116.4074000"), result.getGpsLongitude());
        assertEquals(takenAt, result.getPhotoTakenAt());
        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
        verify(gpsExifExtractor, times(1)).extract(any(InputStream.class));
        verify(geoFenceService, never()).checkFence(any(), any(), any(), any(), anyDouble());
    }

    @Test
    @DisplayName("upload: 图片 GPS 在站点围栏内，geoFenceStatus = NORMAL")
    void upload_imageGpsWithinFence_normal() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        GpsExifExtractor.GpsInfo gpsInfo = new GpsExifExtractor.GpsInfo(
                new BigDecimal("39.9042000"), new BigDecimal("116.4074000"), LocalDateTime.now());
        when(gpsExifExtractor.extract(any(InputStream.class))).thenReturn(gpsInfo);
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(geoFenceService.checkFence(
                eq(new BigDecimal("39.9042000")), eq(new BigDecimal("116.4074000")),
                eq(new BigDecimal("39.9040000")), eq(new BigDecimal("116.4070000")),
                eq(500.0)))
                .thenReturn(GeoFenceService.STATUS_NORMAL);
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.uploadWithGeoFence(file, "ACCEPTANCE", 300L,
                new BigDecimal("39.9040000"), new BigDecimal("116.4070000"), 500.0);

        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
        verify(geoFenceService, times(1)).checkFence(any(), any(), any(), any(), anyDouble());
    }

    @Test
    @DisplayName("upload: 图片 GPS 超出站点围栏 500m，geoFenceStatus = ABNORMAL")
    void upload_imageGpsOutsideFence_abnormal() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        GpsExifExtractor.GpsInfo gpsInfo = new GpsExifExtractor.GpsInfo(
                new BigDecimal("40.0000000"), new BigDecimal("117.0000000"), LocalDateTime.now());
        when(gpsExifExtractor.extract(any(InputStream.class))).thenReturn(gpsInfo);
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(geoFenceService.checkFence(any(), any(), any(), any(), eq(500.0)))
                .thenReturn(GeoFenceService.STATUS_ABNORMAL);
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.uploadWithGeoFence(file, "ACCEPTANCE", 300L,
                new BigDecimal("39.9040000"), new BigDecimal("116.4070000"), 500.0);

        assertEquals(GeoFenceService.STATUS_ABNORMAL, result.getGeoFenceStatus());
    }

    @Test
    @DisplayName("upload: uploadWithGeoFence siteLat/siteLng 为 null 时不做围栏比对")
    void upload_geoFenceNullSite_skipsFenceCheck() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        GpsExifExtractor.GpsInfo gpsInfo = new GpsExifExtractor.GpsInfo(
                new BigDecimal("39.9042000"), new BigDecimal("116.4074000"), LocalDateTime.now());
        when(gpsExifExtractor.extract(any(InputStream.class))).thenReturn(gpsInfo);
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.uploadWithGeoFence(file, "ACCEPTANCE", 300L,
                null, null, 500.0);

        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
        verify(geoFenceService, never()).checkFence(any(), any(), any(), any(), anyDouble());
    }

    @Test
    @DisplayName("upload: EXIF 解析抛异常，不影响上传，geoFenceStatus = NORMAL")
    void upload_exifParseError_continuesUpload() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        when(gpsExifExtractor.extract(any(InputStream.class))).thenThrow(new RuntimeException("EXIF parse error"));
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.upload(file, "PUNCH_LIST", 200L);

        assertNull(result.getGpsLatitude());
        assertNull(result.getGpsLongitude());
        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
    }

    @Test
    @DisplayName("upload: 图片无 EXIF GPS 数据（extract 返回 null），geoFenceStatus = NORMAL")
    void upload_imageNoGps_normal() throws Exception {
        stubImageFile(new byte[]{1, 2, 3}, "photo.jpg");
        when(gpsExifExtractor.extract(any(InputStream.class))).thenReturn(null);
        when(storageService.upload(any(InputStream.class), eq("photo.jpg"), eq(3L), eq(IMAGE_MIME)))
                .thenReturn("2024/06/15/xyz_photo.jpg");
        when(attachmentMapper.insert(any(Attachment.class))).thenReturn(1);

        Attachment result = attachmentService.upload(file, "PUNCH_LIST", 200L);

        assertNull(result.getGpsLatitude());
        assertNull(result.getGpsLongitude());
        assertEquals(GeoFenceService.STATUS_NORMAL, result.getGeoFenceStatus());
    }

    @Test
    @DisplayName("upload: 存储上传失败抛 BusinessException")
    void upload_storageFails_throwsBusinessException() throws Exception {
        stubNonImageFile("hello", "test.txt");
        when(storageService.upload(any(InputStream.class), eq("test.txt"), eq(5L), eq(TEXT_MIME)))
                .thenThrow(new RuntimeException("storage error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.upload(file, "RMA", 100L));
        assertTrue(ex.getMessage().contains("文件上传失败"));
        verify(attachmentMapper, never()).insert(any(Attachment.class));
    }

    @Test
    @DisplayName("upload: 文件为 null 抛业务异常")
    void upload_nullFile_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.upload(null, "RMA", 100L));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("upload: 空文件抛业务异常")
    void upload_emptyFile_throws() {
        when(file.isEmpty()).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.upload(file, "RMA", 100L));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    @DisplayName("upload: bizType 为空抛业务异常")
    void upload_emptyBizType_throws() {
        when(file.isEmpty()).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.upload(file, "", 100L));
        assertTrue(ex.getMessage().contains("业务类型"));
    }

    // ==================== download ====================

    @Test
    @DisplayName("download: 正常下载返回输入流")
    void download_success_returnsStream() {
        Attachment attachment = Attachment.builder().storagePath("2024/01/01/abc.txt").build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);
        InputStream expectedStream = new ByteArrayInputStream("content".getBytes());
        when(storageService.download("2024/01/01/abc.txt")).thenReturn(expectedStream);

        InputStream result = attachmentService.download(1L);

        assertEquals(expectedStream, result);
        verify(storageService, times(1)).download("2024/01/01/abc.txt");
    }

    @Test
    @DisplayName("download: 附件不存在抛业务异常")
    void download_notFound_throws() {
        when(attachmentMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.download(99L));
        assertTrue(ex.getMessage().contains("附件不存在"));
        verify(storageService, never()).download(any());
    }

    @Test
    @DisplayName("download: attachmentId 为 null 抛业务异常")
    void download_nullId_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.download(null));
        assertTrue(ex.getMessage().contains("不能为空"));
        verify(storageService, never()).download(any());
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete: 正常删除，先删存储再删记录")
    void delete_success_deletesStorageThenRecord() {
        Attachment attachment = Attachment.builder().storagePath("2024/01/01/abc.txt").build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);
        doReturn(true).when(attachmentService).removeById(1L);

        boolean result = attachmentService.delete(1L);

        assertTrue(result);
        verify(storageService, times(1)).delete("2024/01/01/abc.txt");
        verify(attachmentService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("delete: 存储删除失败仍删除元数据记录")
    void delete_storageFails_stillDeletesRecord() {
        Attachment attachment = Attachment.builder().storagePath("2024/01/01/abc.txt").build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);
        doThrow(new RuntimeException("storage delete error"))
                .when(storageService).delete("2024/01/01/abc.txt");
        doReturn(true).when(attachmentService).removeById(1L);

        boolean result = attachmentService.delete(1L);

        assertTrue(result);
        verify(storageService, times(1)).delete("2024/01/01/abc.txt");
        verify(attachmentService, times(1)).removeById(1L);
    }

    @Test
    @DisplayName("delete: 附件不存在抛业务异常")
    void delete_notFound_throws() {
        when(attachmentMapper.selectById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.delete(99L));
        assertTrue(ex.getMessage().contains("附件不存在"));
        verify(storageService, never()).delete(any());
    }

    // ==================== listByBiz ====================

    @Test
    @DisplayName("listByBiz: 按业务类型和 ID 查询返回列表")
    void listByBiz_returnsList() {
        Attachment a1 = Attachment.builder().bizType("RMA").bizId(100L).fileName("a.txt").build();
        a1.setId(1L);
        Attachment a2 = Attachment.builder().bizType("RMA").bizId(100L).fileName("b.txt").build();
        a2.setId(2L);
        when(attachmentMapper.selectList(any(Wrapper.class))).thenReturn(Arrays.asList(a1, a2));

        List<Attachment> result = attachmentService.listByBiz("RMA", 100L);

        assertEquals(2, result.size());
        verify(attachmentMapper, times(1)).selectList(any(Wrapper.class));
    }

    @Test
    @DisplayName("listByBiz: 无匹配附件返回空列表")
    void listByBiz_empty_returnsEmptyList() {
        when(attachmentMapper.selectList(any(Wrapper.class))).thenReturn(Collections.emptyList());

        List<Attachment> result = attachmentService.listByBiz("RMA", 999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== generateThumbnail ====================

    @Test
    @DisplayName("generateThumbnail: 正常生成图片缩略图")
    void generateThumbnail_success_returnsBytes() {
        Attachment attachment = Attachment.builder()
                .storagePath("2024/01/01/photo.jpg")
                .mimeType("image/jpeg")
                .build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);
        InputStream downloadStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(storageService.download("2024/01/01/photo.jpg")).thenReturn(downloadStream);
        byte[] thumbnailBytes = new byte[]{10, 20, 30};
        when(thumbnailService.generate(any(InputStream.class), eq(100), eq(100)))
                .thenReturn(thumbnailBytes);

        byte[] result = attachmentService.generateThumbnail(1L, 100, 100);

        assertArrayEquals(thumbnailBytes, result);
        verify(thumbnailService, times(1)).generate(any(InputStream.class), eq(100), eq(100));
    }

    @Test
    @DisplayName("generateThumbnail: 非图片附件抛业务异常")
    void generateThumbnail_nonImage_throws() {
        Attachment attachment = Attachment.builder()
                .storagePath("2024/01/01/doc.pdf")
                .mimeType("application/pdf")
                .build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.generateThumbnail(1L, 100, 100));
        assertTrue(ex.getMessage().contains("非图片"));
        verify(thumbnailService, never()).generate(any(InputStream.class), anyInt(), anyInt());
    }

    @Test
    @DisplayName("generateThumbnail: 缩略图生成失败抛业务异常")
    void generateThumbnail_generationFails_throws() {
        Attachment attachment = Attachment.builder()
                .storagePath("2024/01/01/photo.jpg")
                .mimeType("image/jpeg")
                .build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);
        when(storageService.download("2024/01/01/photo.jpg"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));
        when(thumbnailService.generate(any(InputStream.class), eq(100), eq(100)))
                .thenThrow(new RuntimeException("thumb error"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.generateThumbnail(1L, 100, 100));
        assertTrue(ex.getMessage().contains("缩略图生成失败"));
    }

    @Test
    @DisplayName("generateThumbnail: 附件不存在抛业务异常")
    void generateThumbnail_notFound_throws() {
        when(attachmentMapper.selectById(anyLong())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> attachmentService.generateThumbnail(99L, 100, 100));
        assertTrue(ex.getMessage().contains("附件不存在"));
    }

    // ==================== getById (继承自 ServiceImpl) ====================

    @Test
    @DisplayName("getById: 返回附件详情")
    void getById_found() {
        Attachment attachment = Attachment.builder().bizType("RMA").bizId(100L).build();
        attachment.setId(1L);
        when(attachmentMapper.selectById(1L)).thenReturn(attachment);

        Attachment result = attachmentService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("RMA", result.getBizType());
    }

    @Test
    @DisplayName("getById: 不存在返回 null")
    void getById_notFound() {
        when(attachmentMapper.selectById(anyLong())).thenReturn(null);

        Attachment result = attachmentService.getById(99L);

        assertNull(result);
    }
}
