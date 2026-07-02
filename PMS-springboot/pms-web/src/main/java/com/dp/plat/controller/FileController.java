package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 文件控制器 - 迁移自老系统 UploadAction
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /** 上传文件 */
    @PostMapping("/upload")
    public R<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                          @RequestParam(required = false) String module) {
        Map<String, Object> result = fileService.upload(file, module);
        return R.ok(result);
    }

    /** 上传图片 */
    @PostMapping("/upload-image")
    public R<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = fileService.uploadImage(file);
        return R.ok(result);
    }

    /** 下载文件 */
    @GetMapping("/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        fileService.download(id, response);
    }

    /** 删除文件 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return R.ok();
    }

    /** 查询文件列表 */
    @GetMapping("/list")
    public R<List<SysFileInfo>> list(@RequestParam(required = false) Long businessId,
                                      @RequestParam(required = false) String module) {
        return R.ok(fileService.queryFileList(businessId, module));
    }
}
