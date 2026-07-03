package com.dp.plat.implementation.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.implementation.entity.ImplProgress;
import com.dp.plat.implementation.service.IImplProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Implementation progress management controller.
 */
@Tag(name = "实施进度管理", description = "Implementation progress management APIs")
@RestController
@RequestMapping("/api/impl/progress")
@RequiredArgsConstructor
public class ImplProgressController {

    private final IImplProgressService implProgressService;

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
}
