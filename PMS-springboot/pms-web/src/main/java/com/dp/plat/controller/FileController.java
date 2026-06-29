package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public R<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                          @RequestParam(defaultValue = "common") String module,
                                          HttpServletRequest request) {
        String username = (String) request.getAttribute("currentUsername");
        return R.ok(fileService.uploadFile(file, module, username));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<Map<String, Object>> info(@PathVariable Long id) {
        return R.ok(fileService.getFileInfo(id));
    }
}
