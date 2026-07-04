package com.dp.plat.file.exif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 图片 EXIF GPS 信息提取器。
 *
 * <p>基于 metadata-extractor 库解析 JPEG 图片中的 GPS 经纬度和拍摄时间。
 * 非图片或无 EXIF 信息时返回 {@code null}。</p>
 */
@Slf4j
@Component
public class GpsExifExtractor {

    /**
     * 从图片输入流中提取 GPS 信息和拍摄时间。
     *
     * @param inputStream 图片输入流
     * @return GPS 信息；无 EXIF 或解析失败返回 {@code null}
     */
    public GpsInfo extract(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        // metadata-extractor 需要支持 mark/reset 的流
        BufferedInputStream buffered = inputStream instanceof BufferedInputStream
                ? (BufferedInputStream) inputStream
                : new BufferedInputStream(inputStream);
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(buffered);
            GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDir == null) {
                return null;
            }
            GeoLocation location = gpsDir.getGeoLocation();
            if (location == null) {
                return null;
            }
            BigDecimal latitude = BigDecimal.valueOf(location.getLatitude()).setScale(7, RoundingMode.HALF_UP);
            BigDecimal longitude = BigDecimal.valueOf(location.getLongitude()).setScale(7, RoundingMode.HALF_UP);
            LocalDateTime takenAt = extractTakenAt(metadata);
            return new GpsInfo(latitude, longitude, takenAt);
        } catch (Exception e) {
            log.warn("EXIF GPS 解析失败: {}", e.getMessage());
            return null;
        }
    }

    /** 从 EXIF 中提取拍摄时间，优先取 ExifSubIFDDirectory 的 TAG_DATETIME_ORIGINAL。 */
    private LocalDateTime extractTakenAt(Metadata metadata) {
        try {
            ExifSubIFDDirectory subDir = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            Date date = null;
            if (subDir != null) {
                date = subDir.getDate(ExifDirectoryBase.TAG_DATETIME_ORIGINAL);
            }
            if (date == null) {
                return null;
            }
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            log.warn("EXIF 拍摄时间解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * GPS 信息载体。
     */
    public static class GpsInfo {
        /** 纬度。 */
        private final BigDecimal latitude;
        /** 经度。 */
        private final BigDecimal longitude;
        /** 拍摄时间。 */
        private final LocalDateTime takenAt;

        public GpsInfo(BigDecimal latitude, BigDecimal longitude, LocalDateTime takenAt) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.takenAt = takenAt;
        }

        public BigDecimal getLatitude() {
            return latitude;
        }

        public BigDecimal getLongitude() {
            return longitude;
        }

        public LocalDateTime getTakenAt() {
            return takenAt;
        }
    }
}
