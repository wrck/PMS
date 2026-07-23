package com.dp.plat.file.controller;

import com.dp.plat.common.annotation.OperLog;
import com.dp.plat.common.result.Result;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.service.IAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件附件管理控制器。
 */
@Slf4j
@Tag(name = "文件附件", description = "File attachment management APIs")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final IAttachmentService attachmentService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermission('file:attachment:upload')")
    @OperLog(title = "文件附件", businessType = 1)
    public Result<Attachment> upload(@RequestParam("file") MultipartFile file,
                                     @RequestParam String bizType,
                                     @RequestParam(required = false) Long bizId,
                                     @RequestParam(required = false) BigDecimal siteLat,
                                     @RequestParam(required = false) BigDecimal siteLng,
                                     @RequestParam(required = false, defaultValue = "0") double fenceRadius) {
        Attachment attachment;
        if (siteLat != null && siteLng != null) {
            attachment = attachmentService.uploadWithGeoFence(file, bizType, bizId, siteLat, siteLng, fenceRadius);
        } else {
            attachment = attachmentService.upload(file, bizType, bizId);
        }
        return Result.ok(attachment);
    }

    @Operation(summary = "下载文件")
    @GetMapping("/{id}/download")
    @PreAuthorize("@ss.hasPermission('file:attachment:download')")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        Attachment attachment = attachmentService.getById(id);
        if (attachment == null) {
            throw new com.dp.plat.common.exception.BusinessException("附件不存在");
        }
        response.setContentType(attachment.getMimeType() != null ? attachment.getMimeType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String encodedName = URLEncoder.encode(attachment.getFileName() == null ? "file" : attachment.getFileName(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
        try (InputStream input = attachmentService.download(id);
             OutputStream output = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage());
        }
    }

    @Operation(summary = "生成缩略图")
    @GetMapping("/{id}/thumbnail")
    @PreAuthorize("@ss.hasPermission('file:attachment:download')")
    public void thumbnail(@PathVariable Long id,
                          @RequestParam(defaultValue = "200") int width,
                          @RequestParam(defaultValue = "200") int height,
                          HttpServletResponse response) {
        byte[] bytes = attachmentService.generateThumbnail(id, width, height);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        try (OutputStream output = response.getOutputStream()) {
            output.write(bytes);
            output.flush();
        } catch (Exception e) {
            log.error("缩略图输出失败: {}", e.getMessage());
        }
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('file:attachment:remove')")
    @OperLog(title = "文件附件", businessType = 3)
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(attachmentService.delete(id));
    }

    @Operation(summary = "按业务查询附件列表")
    @GetMapping("/biz")
    @PreAuthorize("@ss.hasPermission('file:attachment:list')")
    public Result<List<Attachment>> listByBiz(@RequestParam String bizType,
                                              @RequestParam Long bizId) {
        return Result.ok(attachmentService.listByBiz(bizType, bizId));
    }
}
