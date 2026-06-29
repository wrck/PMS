package com.dp.plat.controller;

import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.model.entity.SysFileInfo;
import com.dp.plat.service.BasicDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system/basic-data")
public class BasicDataController {

    @Autowired
    private BasicDataService basicDataService;

    /** 根据类型查询基础数据 */
    @GetMapping("/list")
    public R<List<SysBasicData>> list(@RequestParam String dataType) {
        return R.ok(basicDataService.queryByType(dataType));
    }

    /** 查询所有基础数据(包括禁用的) */
    @GetMapping("/all")
    public R<List<SysBasicData>> all(@RequestParam String dataType) {
        return R.ok(basicDataService.queryAllByType(dataType));
    }

    /** 添加基础数据 */
    @PostMapping
    public R<Void> add(@RequestBody SysBasicData data) {
        basicDataService.addBasicData(data);
        return R.ok();
    }

    /** 更新基础数据 */
    @PutMapping
    public R<Void> update(@RequestBody SysBasicData data) {
        basicDataService.updateBasicData(data);
        return R.ok();
    }

    /** 删除基础数据 */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        basicDataService.deleteBasicData(id);
        return R.ok();
    }

    /** 查询系统参数 */
    @GetMapping("/sys-arg")
    public R<String> querySysArg(@RequestParam String code) {
        return R.ok(basicDataService.querySysArg(code));
    }

    /** 根据ID查询基础数据名称 */
    @GetMapping("/{id}/name")
    public R<String> queryName(@PathVariable String id) {
        return R.ok(basicDataService.queryBasicDataNameById(id));
    }

    /** 查询基础数据Map */
    @GetMapping("/map")
    public R<Map<String, String>> queryMap(@RequestParam String dataType) {
        return R.ok(basicDataService.queryBasicDataMap(dataType));
    }

    /** 查询文件信息 */
    @GetMapping("/file/{fileId}")
    public R<SysFileInfo> queryFileInfo(@PathVariable Long fileId) {
        return R.ok(basicDataService.queryFileInfo(fileId));
    }

    /** 删除文件 */
    @DeleteMapping("/file/{fileId}")
    public R<Void> deleteFile(@PathVariable Long fileId) {
        basicDataService.deleteFile(fileId);
        return R.ok();
    }
}
