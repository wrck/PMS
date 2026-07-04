package com.dp.plat.file.preview;

import com.dp.plat.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 图片缩略图生成服务。
 *
 * <p>基于 Thumbnailator 库，按比例缩放至指定宽高范围内（不拉伸变形），输出 PNG 字节。</p>
 */
@Slf4j
@Component
public class ThumbnailService {

    /**
     * 从输入流生成缩略图。
     *
     * @param inputStream 原图输入流
     * @param width       目标宽度
     * @param height      目标高度
     * @return PNG 格式缩略图字节
     */
    public byte[] generate(InputStream inputStream, int width, int height) {
        if (inputStream == null) {
            throw new BusinessException("缩略图输入流不能为空");
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat("png")
                    .toOutputStream(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("缩略图生成失败: " + e.getMessage());
        }
    }

    /**
     * 从字节数组生成缩略图。
     *
     * @param imageBytes 原图字节
     * @param width      目标宽度
     * @param height     目标高度
     * @return PNG 格式缩略图字节
     */
    public byte[] generate(byte[] imageBytes, int width, int height) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BusinessException("缩略图输入字节不能为空");
        }
        try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Thumbnails.of(input)
                    .size(width, height)
                    .outputFormat("png")
                    .toOutputStream(output);
            return output.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("缩略图生成失败: " + e.getMessage());
        }
    }
}
