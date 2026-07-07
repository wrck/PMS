package com.dp.plat.admin.file;

import com.dp.plat.admin.testconfig.AbstractIntegrationTest;
import com.dp.plat.common.result.ResultCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 文件上传 / 下载 / EXIF 集成测试（Task 13.2）。
 *
 * <p>覆盖 {@code FileController} 全链路：普通文件上传、图片上传带 EXIF GPS 围栏比对、
 * 文件下载、按业务查询附件、删除、空文件与缺参等拒绝场景。端点路径以
 * {@link com.dp.plat.file.controller.FileController} 源码为准：
 * {@code POST /api/file/upload}、{@code GET /api/file/{id}/download}、
 * {@code GET /api/file/biz}、{@code DELETE /api/file/{id}}。
 * 注意：控制器未暴露 {@code GET /api/file/{id}} 详情接口，「获取文件信息」场景以
 * {@code GET /api/file/biz?bizType=&bizId=} 列表查询替代。</p>
 *
 * <p>存储后端默认使用 {@link com.dp.plat.file.storage.LocalStorageServiceImpl}（本地磁盘，
 * 根目录 {@code /tmp/pms-files}），无需外部对象存储依赖。EXIF 测试通过手工构造包含
 * GPS IFD 的最小 JPEG（SOI + APP1/Exif + EOI）触发 {@link com.dp.plat.file.exif.GpsExifExtractor}
 * 解析路径。</p>
 *
 * <p>继承自 {@link AbstractIntegrationTest}，无 Docker 环境自动跳过。</p>
 */
@Transactional
@WithMockUser(username = "1", authorities = {
        "file:attachment:upload",
        "file:attachment:remove"
})
class FileControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BIZ_TYPE = "PUNCH_LIST";
    private static final long BIZ_ID = 9_000_001L;

    /** 上传一个普通文本文件并返回附件 id。 */
    private Long uploadTextFileAndGetId() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello pms".getBytes());
        MvcResult result = mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .at("/data/id").asLong();
    }

    @Test
    @DisplayName("POST /api/file/upload 上传普通文件返回 200 并落库元数据")
    void upload_textFile_shouldReturn200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello pms".getBytes());

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.fileName").value("hello.txt"))
                .andExpect(jsonPath("$.data.bizType").value(BIZ_TYPE))
                .andExpect(jsonPath("$.data.storagePath").exists());
    }

    @Test
    @DisplayName("POST /api/file/upload 上传带 EXIF GPS 的图片，照片坐标在围栏内 → geoFenceStatus=NORMAL")
    void upload_imageWithExifGps_insideFence_shouldReturnNormal() throws Exception {
        // 照片拍摄坐标与站点坐标一致，距离 0 ≤ 围栏半径 → NORMAL
        BigDecimal lat = new BigDecimal("31.5000000");
        BigDecimal lng = new BigDecimal("120.5000000");
        byte[] jpeg = buildJpegWithGpsExif(lat, lng);
        MockMultipartFile file = new MockMultipartFile(
                "file", "site-photo.jpg", MediaType.IMAGE_JPEG_VALUE, jpeg);

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID))
                        .param("siteLat", lat.toPlainString())
                        .param("siteLng", lng.toPlainString())
                        .param("fenceRadius", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.geoFenceStatus").value("NORMAL"))
                .andExpect(jsonPath("$.data.gpsLatitude").value(31.5000000))
                .andExpect(jsonPath("$.data.gpsLongitude").value(120.5000000));
    }

    @Test
    @DisplayName("POST /api/file/upload 上传带 EXIF GPS 的图片，照片坐标远离站点 → geoFenceStatus=ABNORMAL")
    void upload_imageWithExifGps_outsideFence_shouldReturnAbnormal() throws Exception {
        // 照片坐标 (31.5, 120.5)，站点坐标 (40.0, 116.4)（北京附近），距离数千公里，远超 100m 围栏
        BigDecimal photoLat = new BigDecimal("31.5000000");
        BigDecimal photoLng = new BigDecimal("120.5000000");
        BigDecimal siteLat = new BigDecimal("40.0000000");
        BigDecimal siteLng = new BigDecimal("116.4000000");
        byte[] jpeg = buildJpegWithGpsExif(photoLat, photoLng);
        MockMultipartFile file = new MockMultipartFile(
                "file", "site-photo-far.jpg", MediaType.IMAGE_JPEG_VALUE, jpeg);

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID))
                        .param("siteLat", siteLat.toPlainString())
                        .param("siteLng", siteLng.toPlainString())
                        .param("fenceRadius", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.geoFenceStatus").value("ABNORMAL"))
                .andExpect(jsonPath("$.data.gpsLatitude").exists())
                .andExpect(jsonPath("$.data.gpsLongitude").exists());
    }

    @Test
    @DisplayName("GET /api/file/{id}/download 下载文件返回原始字节")
    void download_shouldReturnFileBytes() throws Exception {
        Long id = uploadTextFileAndGetId();

        mockMvc.perform(get("/api/file/{id}/download", id))
                .andExpect(status().isOk())
                .andExpect(content().bytes("hello pms".getBytes()))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("hello.txt")));
    }

    @Test
    @DisplayName("GET /api/file/biz 按业务查询附件列表返回 200 并包含已上传附件")
    void listByBiz_shouldReturnAttachmentList() throws Exception {
        uploadTextFileAndGetId();

        mockMvc.perform(get("/api/file/biz")
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].fileName").value("hello.txt"))
                .andExpect(jsonPath("$.data[0].bizType").value(BIZ_TYPE));
    }

    @Test
    @DisplayName("DELETE /api/file/{id} 删除附件返回 200")
    void delete_shouldReturn200() throws Exception {
        Long id = uploadTextFileAndGetId();

        mockMvc.perform(delete("/api/file/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("POST /api/file/upload 上传空文件被拒绝（业务异常 code=1001）")
    void upload_emptyFile_shouldBeRejected() throws Exception {
        MockMultipartFile empty = new MockMultipartFile(
                "file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/file/upload")
                        .file(empty)
                        .param("bizType", BIZ_TYPE)
                        .param("bizId", String.valueOf(BIZ_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BUSINESS_ERROR.getCode()));
    }

    @Test
    @DisplayName("POST /api/file/upload 缺少业务类型被拒绝（业务异常 code=1001）")
    void upload_missingBizType_shouldBeRejected() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());

        mockMvc.perform(multipart("/api/file/upload")
                        .file(file)
                        .param("bizType", "")
                        .param("bizId", String.valueOf(BIZ_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.BUSINESS_ERROR.getCode()));
    }

    /**
     * 构造包含 EXIF GPS IFD 的最小 JPEG 字节流（SOI + APP1/Exif + EOI）。
     *
     * <p>TIFF 采用小端字节序（"II"），结构如下：
     * <pre>
     *   TIFF header (8B) → IFD0 (1 entry: GPS IFD pointer) → GPS IFD (4 entries)
     *     → 纬度 RATIONAL×3 (24B) → 经度 RATIONAL×3 (24B)
     * </pre>
     * GPS 坐标以度/分/秒三个 RATIONAL 表示，度数 = 整数部分，分秒置 0，
     * 从而还原出 {@code (lat, lng)} 的十进制度数。</p>
     *
     * <p>该方法仅用于测试触发 {@link com.dp.plat.file.exif.GpsExifExtractor} 的解析路径；
     * metadata-extractor 能从 APP1 段读取 GPS 目录而无需完整图像扫描数据。</p>
     */
    private byte[] buildJpegWithGpsExif(BigDecimal lat, BigDecimal lng) {
        try {
            ByteArrayOutputStream tiff = new ByteArrayOutputStream();
            // --- TIFF header (8B) ---
            tiff.write(0x49); tiff.write(0x49);          // byte order: II (little-endian)
            tiff.write(0x2A); tiff.write(0x00);          // magic 42
            tiff.write(0x08); tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); // offset to IFD0 = 8

            // --- IFD0 (offset 8): 1 entry + next-IFD ptr ---
            tiff.write(0x01); tiff.write(0x00);          // entry count = 1
            // entry: GPS IFD pointer (tag 0x8825, type LONG, count 1, value = offset of GPS IFD = 26)
            tiff.write(0x25); tiff.write(0x88);          // tag 0x8825
            tiff.write(0x04); tiff.write(0x00);          // type LONG
            tiff.write(0x01); tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); // count 1
            tiff.write(0x1A); tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); // value 26
            tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); // next IFD = 0

            // --- GPS IFD (offset 26): 4 entries + next-IFD ptr ---
            // IFD0 size = 2 + 12 + 4 = 18 → GPS IFD at offset 26
            // GPS IFD size = 2 + 4*12 + 4 = 54 → 纬度 RATIONAL 起始于 offset 80
            int gpsIfdStart = 26;
            int rationalStart = gpsIfdStart + 2 + 4 * 12 + 4; // = 80
            int latRationalOffset = rationalStart;            // 80
            int lngRationalOffset = latRationalOffset + 24;   // 104

            tiff.write(0x04); tiff.write(0x00);          // entry count = 4

            // entry1: GPSLatitudeRef (0x0001, ASCII, count 2, 'N' inline)
            writeIfdEntry(tiff, 0x0001, 0x0002, 2, packInline('N', (byte) 0));
            // entry2: GPSLatitude (0x0002, RATIONAL, count 3, offset)
            writeIfdEntry(tiff, 0x0002, 0x0005, 3, latRationalOffset);
            // entry3: GPSLongitudeRef (0x0003, ASCII, count 2, 'E' inline)
            writeIfdEntry(tiff, 0x0003, 0x0002, 2, packInline('E', (byte) 0));
            // entry4: GPSLongitude (0x0004, RATIONAL, count 3, offset)
            writeIfdEntry(tiff, 0x0004, 0x0005, 3, lngRationalOffset);
            // next IFD = 0
            tiff.write(0x00); tiff.write(0x00); tiff.write(0x00); tiff.write(0x00);

            // --- 纬度 RATIONAL ×3 (offset 80): degrees/1, 0/1, 0/1 ---
            writeRational(tiff, lat.intValue(), 1);
            writeRational(tiff, 0, 1);
            writeRational(tiff, 0, 1);
            // --- 经度 RATIONAL ×3 (offset 104): degrees/1, 0/1, 0/1 ---
            writeRational(tiff, lng.intValue(), 1);
            writeRational(tiff, 0, 1);
            writeRational(tiff, 0, 1);

            byte[] tiffBytes = tiff.toByteArray();

            // --- 组装 JPEG: SOI + APP1(Exif) + EOI ---
            ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
            jpeg.write(0xFF); jpeg.write(0xD8);          // SOI
            jpeg.write(0xFF); jpeg.write(0xE1);          // APP1 marker
            int app1Length = 2 + 6 + tiffBytes.length;   // length field + "Exif\0\0" + TIFF
            jpeg.write((app1Length >> 8) & 0xFF);        // length (big-endian)
            jpeg.write(app1Length & 0xFF);
            jpeg.write('E'); jpeg.write('x'); jpeg.write('i'); jpeg.write('f'); // "Exif\0\0"
            jpeg.write(0x00); jpeg.write(0x00);
            jpeg.write(tiffBytes);
            jpeg.write(0xFF); jpeg.write(0xD9);          // EOI
            return jpeg.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("构造 EXIF GPS JPEG 失败: " + e.getMessage(), e);
        }
    }

    /** 写入一个 IFD entry（12 字节）：tag(2) + type(2) + count(4) + value/offset(4)，小端序。 */
    private void writeIfdEntry(ByteArrayOutputStream out, int tag, int type, int count, int value) {
        writeLeShort(out, tag);
        writeLeShort(out, type);
        writeLeInt(out, count);
        writeLeInt(out, value);
    }

    /** 将两个字节内联为 4 字节 value 字段（小端序），用于 ASCII 短值。 */
    private int packInline(byte b1, byte b2) {
        return ((b2 & 0xFF) << 8) | (b1 & 0xFF);
    }

    /** 写入一个 RATIONAL（8 字节）：numerator(4) + denominator(4)，小端序。 */
    private void writeRational(ByteArrayOutputStream out, int numerator, int denominator) {
        writeLeInt(out, numerator);
        writeLeInt(out, denominator);
    }

    private void writeLeShort(ByteArrayOutputStream out, int v) {
        out.write(v & 0xFF);
        out.write((v >> 8) & 0xFF);
    }

    private void writeLeInt(ByteArrayOutputStream out, int v) {
        out.write(v & 0xFF);
        out.write((v >> 8) & 0xFF);
        out.write((v >> 16) & 0xFF);
        out.write((v >> 24) & 0xFF);
    }
}
