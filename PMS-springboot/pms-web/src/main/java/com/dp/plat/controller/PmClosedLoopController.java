package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.entity.PmClosedLoop;
import com.dp.plat.service.PmClosedLoopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/closed-loop")
public class PmClosedLoopController {

    @Autowired
    private PmClosedLoopService closedLoopService;

    /** 闭环列表 */
    @GetMapping("/list")
    public R<IPage<PmClosedLoop>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) Long projectId,
                                        @RequestParam(required = false) Integer applyState) {
        return R.ok(closedLoopService.queryClosedLoopPage(pageNum, pageSize, projectId, applyState));
    }

    /** 闭环详情 */
    @GetMapping("/{id}")
    public R<PmClosedLoop> detail(@PathVariable Long id) {
        return R.ok(closedLoopService.getDetail(id));
    }

    /** 发起闭环申请 */
    @PostMapping
    public R<Void> apply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.apply(closedLoop);
        return R.ok();
    }

    /** 审批闭环 */
    @PostMapping("/{id}/approve")
    public R<Void> approve(@PathVariable Long id,
                            @RequestParam String comment,
                            @RequestParam boolean approved,
                            @RequestParam(defaultValue = "sm") String role) {
        closedLoopService.approve(id, comment, approved, role);
        return R.ok();
    }

    /**
     * PM发起闭环申请
     * 迁移自: PmClosedLoopAction.addPmCLApply()
     */
    @PostMapping("/pm-apply")
    public R<Void> pmApply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.pmApply(closedLoop);
        return R.ok();
    }

    /**
     * SM发起闭环申请
     * 迁移自: PmClosedLoopAction.addSmCLApply()
     */
    @PostMapping("/sm-apply")
    public R<Void> smApply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.smApply(closedLoop);
        return R.ok();
    }

    /**
     * CB发起闭环申请
     * 迁移自: PmClosedLoopAction.addCbCLApply()
     */
    @PostMapping("/cb-apply")
    public R<Void> cbApply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.cbApply(closedLoop);
        return R.ok();
    }

    /**
     * 无法闭环
     * 迁移自: PmClosedLoopAction.cantCB()
     */
    @PostMapping("/{id}/cant-close")
    public R<Void> cantClose(@PathVariable Long id, @RequestParam String reason) {
        closedLoopService.cantClose(id, reason);
        return R.ok();
    }

    /**
     * CL发起闭环申请
     * 迁移自: PmClosedLoopAction.addClCLApply()
     */
    @PostMapping("/cl-apply")
    public R<Void> clApply(@RequestBody PmClosedLoop closedLoop) {
        closedLoopService.clApply(closedLoop);
        return R.ok();
    }

    /** 查询项目闭环历史 */
    @GetMapping("/project/{projectId}")
    public R<List<PmClosedLoop>> byProject(@PathVariable Long projectId) {
        return R.ok(closedLoopService.queryByProject(projectId));
    }

    /** 查询项目进行中的闭环 */
    @GetMapping("/project/{projectId}/running")
    public R<PmClosedLoop> runningByProject(@PathVariable Long projectId) {
        return R.ok(closedLoopService.queryRunningByProject(projectId));
    }
}
