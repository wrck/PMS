package com.dp.plat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dp.plat.common.result.R;
import com.dp.plat.model.dto.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.model.vo.*;
import com.dp.plat.service.PmsProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/project")
public class PmsProjectController {

    @Autowired
    private PmsProjectService pmsProjectService;

    @GetMapping("/list")
    public R<IPage<ProjectVO>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String projectCode,
                                    @RequestParam(required = false) String projectName,
                                    @RequestParam(required = false) String contractNo,
                                    @RequestParam(required = false) String officeCode,
                                    @RequestParam(required = false) Integer projectState) {
        IPage<ProjectVO> page = pmsProjectService.queryProjectPage(
                pageNum, pageSize, projectCode, projectName, contractNo, officeCode, projectState);
        return R.ok(page);
    }

    @PostMapping
    public R<Void> add(@RequestBody ProjectDTO dto) {
        pmsProjectService.addProject(dto);
        return R.ok();
    }

    @PutMapping
    public R<Void> update(@RequestBody ProjectDTO dto) {
        pmsProjectService.updateProject(dto);
        return R.ok();
    }

    @GetMapping("/{id}")
    public R<ProjectVO> detail(@PathVariable Long id) {
        ProjectVO vo = pmsProjectService.getProjectDetail(id);
        return R.ok(vo);
    }

    @GetMapping("/{id}/members")
    public R<List<PmsProjectMember>> members(@PathVariable Long id) {
        List<PmsProjectMember> list = pmsProjectService.getProjectMembers(id);
        return R.ok(list);
    }

    @PostMapping("/member")
    public R<Void> addMember(@RequestBody ProjectMemberDTO dto) {
        pmsProjectService.addProjectMember(dto);
        return R.ok();
    }

    @DeleteMapping("/member/{id}")
    public R<Void> deleteMember(@PathVariable Long id) {
        pmsProjectService.deleteProjectMember(id);
        return R.ok();
    }

    // ===== 迁移自老系统的新增接口 =====

    /**
     * 创建串货项目
     */
    @PostMapping("/ch")
    public R<Void> createCHProject(@RequestBody ProjectDTO dto) {
        pmsProjectService.createCHProject(dto);
        return R.ok();
    }

    /**
     * 查询可转移的目标项目
     */
    @GetMapping("/transfer/list")
    public R<List<PmsProject>> queryTransferProjectList(@RequestParam String contractNo) {
        return R.ok(pmsProjectService.queryTransferProjectList(contractNo));
    }

    /**
     * 转移设备到其他项目
     */
    @PostMapping("/transfer/shipment")
    public R<Void> transferShipment(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> selected = (List<Long>) params.get("selected");
        Long projectId = Long.valueOf(params.get("projectId").toString());
        Long transferProjectId = Long.valueOf(params.get("transferProjectId").toString());
        pmsProjectService.transferShipment(selected, projectId, transferProjectId);
        return R.ok();
    }

    /**
     * 查询设备清单
     */
    @GetMapping("/{id}/order-data")
    public R<List<OrderDataVO>> checkOrderData(@PathVariable Long id,
                                                @RequestParam(required = false) String contractNo) {
        return R.ok(pmsProjectService.checkOrderData(id, contractNo));
    }

    /**
     * 查询实施发货设备清单
     */
    @GetMapping("/{id}/real-order-data")
    public R<List<OrderDataVO>> checkRealOrderData(@PathVariable Long id) {
        return R.ok(pmsProjectService.checkRealOrderData(id));
    }

    /**
     * 查询租赁配置清单
     */
    @GetMapping("/{id}/lease-line")
    public R<List<Map<String, Object>>> projectLeaseLine(@PathVariable Long id,
                                                          @RequestParam String projectCode) {
        return R.ok(pmsProjectService.projectLeaseLine(projectCode));
    }

    /**
     * 查询配置关系清单
     */
    @GetMapping("/{id}/config-level-info")
    public R<List<Map<String, Object>>> projectProductConfigLevelInfo(@PathVariable Long id,
                                                                       @RequestParam String projectCode) {
        return R.ok(pmsProjectService.projectProductConfigLevelInfo(projectCode));
    }

    /**
     * 查询发货序列号
     */
    @GetMapping("/{id}/shipment-info")
    public R<List<ShipmentInfoVO>> checkShipmentInfo(@PathVariable Long id,
                                                      @RequestParam String contractNo) {
        return R.ok(pmsProjectService.checkShipmentInfo(id, contractNo));
    }

    /**
     * 删除发货安装信息
     */
    @DeleteMapping("/{id}/shipment-info")
    public R<Void> deleteShipmentInfo(@PathVariable Long id) {
        pmsProjectService.deleteShipmentInfo(id);
        return R.ok();
    }

    /**
     * 查询设备软件版本信息
     */
    @GetMapping("/{id}/soft-version")
    public R<List<ProjectSoftVersionVO>> checkSoftVersion(@PathVariable Long id,
                                                           @RequestParam String contractNo,
                                                           @RequestParam(required = false) String filterItem) {
        Map<String, Object> params = new HashMap<>();
        params.put("filterItem", filterItem);
        return R.ok(pmsProjectService.checkSoftVersion(id, contractNo, params));
    }

    /**
     * AJAX更新设备软件版本
     */
    @PutMapping("/soft-version")
    public R<Void> updateSoftVersion(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<ProjectSoftVersionVO> softversionList = (List<ProjectSoftVersionVO>) params.get("softversionList");
        SoftChangeLogVO changeLog = new SoftChangeLogVO();
        changeLog.setProjectId(Long.valueOf(params.get("projectId").toString()));
        changeLog.setChangeVersion(params.get("changeVersion").toString());
        changeLog.setChangeRemark(params.get("changeRemark").toString());
        pmsProjectService.updateSoftVersion(softversionList, changeLog);
        return R.ok();
    }

    /**
     * 获取软件版本历史数据
     */
    @GetMapping("/soft-version/history")
    public R<List<SoftChangeLogVO>> checkhistsoftversion(@RequestParam Long projectId) {
        return R.ok(pmsProjectService.checkhistsoftversion(projectId));
    }

    /**
     * 获取历史软件版本详情
     */
    @GetMapping("/soft-version/history/{logId}")
    public R<List<ProjectSoftVersionVO>> queryHistSoftVersionList(@PathVariable Long logId) {
        return R.ok(pmsProjectService.queryHistSoftVersionList(logId));
    }

    /**
     * 获取单个变更记录
     */
    @GetMapping("/soft-version/log/{id}")
    public R<SoftChangeLogVO> queryOneSoftChangeLog(@PathVariable Long id) {
        return R.ok(pmsProjectService.queryOneSoftChangeLog(id));
    }

    /**
     * 获取项目维护记录
     */
    @GetMapping("/{id}/maintenance")
    public R<List<Map<String, Object>>> projectMaintenance(@PathVariable Long id,
                                                            @RequestParam(required = false) String officeCode) {
        return R.ok(pmsProjectService.projectMaintenance(id, officeCode));
    }

    /**
     * 创建/编辑项目维护记录
     */
    @PostMapping("/maintenance")
    public R<Void> createProjectMaintenance(@RequestBody PmsMaintenance maintenance) {
        pmsProjectService.createProjectMaintenance(maintenance);
        return R.ok();
    }

    /**
     * 制定或修改工程计划
     */
    @PostMapping("/{id}/plan")
    public R<Void> editProjectPlan(@PathVariable Long id,
                                    @RequestParam String contractNo,
                                    @RequestParam String createBy) {
        pmsProjectService.editProjectPlan(id, contractNo, createBy);
        return R.ok();
    }

    /**
     * 项目回退到上一步
     */
    @PostMapping("/back-to-last-step")
    public R<Void> backToLastStep(@RequestBody ProjectBackDTO dto) {
        pmsProjectService.backToLastStep(dto);
        return R.ok();
    }

    /**
     * 项目回退流程
     */
    @PostMapping("/update-isback")
    public R<Void> updateProjectIsback(@RequestBody Map<String, Object> params) {
        Long projectId = Long.valueOf(params.get("projectId").toString());
        String isback = params.get("isback").toString();
        String backCause = params.containsKey("backCause") ? params.get("backCause").toString() : null;
        String pm = params.containsKey("pm") ? params.get("pm").toString() : null;
        int sendto = params.containsKey("sendto") ? Integer.parseInt(params.get("sendto").toString()) : 0;
        String notbackCause = params.containsKey("notbackCause") ? params.get("notbackCause").toString() : null;
        pmsProjectService.updateProjectIsback(projectId, isback, backCause, pm, sendto, notbackCause);
        return R.ok();
    }

    /**
     * 根据角色查询用户
     */
    @GetMapping("/users")
    public R<List<Map<String, Object>>> queryalluser(@RequestParam(required = false) Long roleId) {
        if (roleId == null || roleId == 0) {
            return R.ok(pmsProjectService.queryAllUser());
        }
        return R.ok(pmsProjectService.queryUserWithRoleId(roleId));
    }

    /**
     * 查询项目干系人
     */
    @GetMapping("/persons")
    public R<List<Map<String, Object>>> queryperson() {
        return R.ok(pmsProjectService.queryPersonList());
    }

    /**
     * 查询指定部门无特定角色的用户
     */
    @GetMapping("/users/no-role")
    public R<List<Map<String, Object>>> queryDpNoRoleUser(@RequestParam Long roleId,
                                                           @RequestParam(required = false) String dpNo) {
        return R.ok(pmsProjectService.queryDpNoRoleUser(roleId, dpNo));
    }

    /**
     * 批量变更项目成员
     */
    @PostMapping("/batch-change-member")
    public R<String> batchChangeMember(@RequestBody BatchChangeMemberDTO dto) {
        return R.ok(pmsProjectService.batchChangeMember(dto));
    }

    /**
     * 批量创建项目（Excel导入）
     */
    @PostMapping("/import")
    public R<Void> importProject(@RequestBody List<PmsProject> projects,
                                  @RequestParam(defaultValue = "1") int batchFunc) {
        pmsProjectService.importProject(projects, batchFunc);
        return R.ok();
    }

    /**
     * 批量删除/无效化项目
     */
    @PostMapping("/clear")
    public R<Integer> clearProject(@RequestBody List<PmsProject> projects,
                                    @RequestParam(defaultValue = "false") boolean delete) {
        return R.ok(pmsProjectService.clearProject(projects, delete));
    }

    /**
     * 现场验货单下载
     */
    @GetMapping("/{id}/export-spot-check")
    public R<Map<String, String>> exportSpotCheck(@PathVariable Long id) {
        return R.ok(pmsProjectService.exportSpotCheck(id));
    }

    /**
     * 超期保修提醒导出
     */
    @GetMapping("/{id}/export-over-warranty-remind")
    public R<Map<String, String>> exportOverWarrantyRemind(@PathVariable Long id) {
        return R.ok(pmsProjectService.exportOverWarrantyRemind(id));
    }

    /**
     * 导入现场验货单
     */
    @PostMapping("/import-spot-check-ignore-item")
    public R<Void> importSpotCheckIgnoreItem(@RequestBody List<?> itemList) {
        pmsProjectService.importSpotCheckIgnoreItem(itemList);
        return R.ok();
    }

    // ===== P0: 项目成员管理 =====

    /**
     * 更新项目成员
     */
    @PutMapping("/member/{id}")
    public R<Void> updateMember(@PathVariable Long id,
                                 @RequestParam(required = false) String effectiveTo) {
        LocalDateTime to = StringUtils.hasText(effectiveTo) ? LocalDateTime.parse(effectiveTo) : null;
        pmsProjectService.updateProjectMember(id, to);
        return R.ok();
    }

    /**
     * 保存安装地址
     */
    @PostMapping("/{id}/install-address")
    public R<Void> saveInstallAddress(@PathVariable Long id,
                                       @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        String[] selected = ((List<String>) params.get("selected")).toArray(new String[0]);
        String installAddress = (String) params.get("installAddress");
        pmsProjectService.saveInstallAddress(id, selected, installAddress);
        return R.ok();
    }

    /**
     * 更新项目实施状态
     */
    @PutMapping("/{id}/execution-state")
    public R<Void> updateProjectExecutionState(@PathVariable Long id,
                                                @RequestParam String executionState) {
        pmsProjectService.updateProjectExecutionState(id, executionState);
        return R.ok();
    }

    // ===== P0: 项目批示 =====

    /**
     * 保存项目批示
     */
    @PostMapping("/{id}/instruction")
    public R<Void> saveInstruction(@PathVariable Long id,
                                    @RequestBody Map<String, Object> params) {
        String instructionsInfo = (String) params.get("instructionsInfo");
        Long instructionId = params.containsKey("instructionId") ? Long.valueOf(params.get("instructionId").toString()) : null;
        pmsProjectService.saveInstruction(id, instructionsInfo, instructionId);
        return R.ok();
    }

    // ===== P0: 合同拆分合并 =====

    /**
     * 查询合同列表(用于合并)
     */
    @GetMapping("/contract/list")
    public R<List<Map<String, Object>>> queryContractList(@RequestParam String mergeContractNo) {
        return R.ok(pmsProjectService.queryContractList(mergeContractNo));
    }

    /**
     * 合并合同
     */
    @PostMapping("/{id}/merge-contract")
    public R<Void> mergeContract(@PathVariable Long id,
                                  @RequestBody String[] selected) {
        pmsProjectService.mergeContract(selected, id);
        return R.ok();
    }

    /**
     * 拆分项目
     */
    @PostMapping("/{id}/branch-contract")
    public R<Long> branchContract(@PathVariable Long id,
                                   @RequestParam String projectCode,
                                   @RequestParam(required = false) String mergeBranchMark) {
        return R.ok(pmsProjectService.branchContract(id, projectCode, mergeBranchMark));
    }

    // ===== P0: 交付件管理 =====

    /**
     * 上传工程交付件
     */
    @PostMapping("/{id}/deliver")
    public R<Void> uploadDeliverableFile(@PathVariable Long id,
                                          @RequestBody Map<String, String> params) {
        pmsProjectService.uploadDeliverableFile(id, params.get("deliverableType"), params.get("fileIds"));
        return R.ok();
    }

    /**
     * 删除工程交付件
     */
    @DeleteMapping("/deliver/{deliverId}")
    public R<Void> deleteDeliverById(@PathVariable Long deliverId) {
        pmsProjectService.deleteDeliverById(deliverId);
        return R.ok();
    }
}
