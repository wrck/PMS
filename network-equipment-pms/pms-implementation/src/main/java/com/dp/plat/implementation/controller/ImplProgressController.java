package com.dp.plat.implementation.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.file.entity.Attachment;
import com.dp.plat.file.service.IAttachmentService;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.service.IImplProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation progress management controller.
 */
@Slf4j
@Tag(name = "实施进度管理", description = "Implementation progress management APIs")
@RestController
@RequestMapping("/api/impl/progress")
@RequiredArgsConstructor
public class ImplProgressController {

    /** Attachment business type for implementation progress photos. */
    private static final String ATTACHMENT_BIZ_TYPE_IMPL_PROGRESS = "IMPL_PROGRESS";

    private final IImplProgressService implProgressService;
    private final IAttachmentService attachmentService;

    @Operation(summary = "List progress logs by task id")
    @GetMapping("/task/{taskId}")
    public Result<List<ImplProgress>> listByTask(@PathVariable Long taskId) {
        return Result.ok(implProgressService.listByTaskId(taskId));
    }

    @Operation(summary = "Create a progress log")
    @PostMapping
    public Result<ImplProgress> create(@RequestBody ImplProgress progress) {
        return Result.ok(implProgressService.create(progress));
    }

    @Operation(summary = "Upload implementation photos for a progress log")
    @PostMapping("/{id}/photos")
    public Result<List<Attachment>> uploadPhotos(@PathVariable Long id,
                                                  @RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.ok(List.of());
        }
        List<Attachment> uploaded = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            uploaded.add(attachmentService.upload(file, ATTACHMENT_BIZ_TYPE_IMPL_PROGRESS, id));
        }
        return Result.ok(uploaded);
    }

    @Operation(summary = "List all implementation photos for a progress log")
    @GetMapping("/{id}/photos")
    public Result<List<Attachment>> listPhotos(@PathVariable Long id) {
        return Result.ok(attachmentService.listByBiz(ATTACHMENT_BIZ_TYPE_IMPL_PROGRESS, id));
    }

    @Operation(summary = "Delete a single implementation photo")
    @DeleteMapping("/photos/{attachmentId}")
    public Result<Boolean> deletePhoto(@PathVariable Long attachmentId) {
        return Result.ok(attachmentService.delete(attachmentId));
    }
}
