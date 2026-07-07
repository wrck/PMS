package com.dp.plat.lowcode.controller;

import com.dp.plat.common.result.Result;
import com.dp.plat.lowcode.entity.LowCodeComponentMeta;
import com.dp.plat.lowcode.mapper.LowCodeComponentMetaMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 低代码组件元数据 Controller。
 */
@Tag(name = "低代码组件元数据", description = "LowCode component metadata")
@RestController
@RequestMapping("/api/lowcode/component-meta")
@RequiredArgsConstructor
public class LowCodeComponentMetaController {

    private final LowCodeComponentMetaMapper mapper;

    @Operation(summary = "查询所有组件元数据")
    @GetMapping
    public Result<List<LowCodeComponentMeta>> list() {
        return Result.ok(mapper.selectList(null));
    }
}
