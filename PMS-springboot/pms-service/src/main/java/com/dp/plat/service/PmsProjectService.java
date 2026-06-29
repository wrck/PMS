package com.dp.plat.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.base.BaseService;
import com.dp.plat.model.dto.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.model.vo.*;

import java.util.List;
import java.util.Map;

public interface PmsProjectService extends BaseService<PmsProject> {

    // ===== 基础CRUD =====
    IPage<ProjectVO> queryProjectPage(Integer pageNum, Integer pageSize,
                                       String projectCode, String projectName,
                                       String contractNo, String officeCode,
                                       Integer projectState);

    Page<ProjectVO> listProjects(int pageNum, int pageSize, String projectCode, String projectName,
                                  String projectType, String projectState, String pmCode);

    ProjectVO getProjectById(Long id);

    void addProject(ProjectDTO projectDTO);

    void updateProject(ProjectDTO projectDTO);

    void deleteProject(Long id);

    // ===== 项目成员 =====
    List<PmsProjectMember> getProjectMembers(Long projectId);

    void addProjectMember(ProjectMemberDTO memberDTO);

    void deleteProjectMember(Long memberId);

    // ===== 串货项目管理 =====
    void createCHProject(ProjectDTO projectDTO);

    void transferShipment(List<Long> selected, Long projectId, Long transferProjectId);

    List<PmsProject> queryTransferProjectList(String contractNo);

    // ===== 设备/订单查询 =====
    List<OrderDataVO> checkOrderData(Long projectId, String contractNo);

    List<OrderDataVO> checkRealOrderData(Long projectId);

    List<Map<String, Object>> projectLeaseLine(String projectCode);

    List<Map<String, Object>> projectProductConfigLevelInfo(String projectCode);

    // ===== 发货信息管理 =====
    List<ShipmentInfoVO> checkShipmentInfo(Long projectId, String contractNo);

    void deleteShipmentInfo(Long projectId);

    // ===== 软件版本管理 =====
    List<ProjectSoftVersionVO> checkSoftVersion(Long projectId, String contractNo, Map<String, Object> params);

    void updateSoftVersion(List<ProjectSoftVersionVO> softversionList, SoftChangeLogVO changeLog);

    List<SoftChangeLogVO> checkhistsoftversion(Long projectId);

    List<ProjectSoftVersionVO> queryHistSoftVersionList(Long logId);

    SoftChangeLogVO queryOneSoftChangeLog(Long id);

    // ===== 项目维护记录 =====
    List<Map<String, Object>> projectMaintenance(Long projectId, String officeCode);

    void createProjectMaintenance(PmsMaintenance maintenance);

    // ===== 工程计划 =====
    void editProjectPlan(Long projectId, String contractNo, String createBy);

    // ===== 项目回退 =====
    void backToLastStep(ProjectBackDTO dto);

    void updateProjectIsback(Long projectId, String isback, String backCause, String pm, int sendto, String notbackCause);

    // ===== 用户查询 =====
    List<Map<String, Object>> queryAllUser();

    List<Map<String, Object>> queryUserWithRoleId(Long roleId);

    List<Map<String, Object>> queryPersonList();

    List<Map<String, Object>> queryDpNoRoleUser(Long roleId, String dpNo);

    // ===== 批量变更成员 =====
    String batchChangeMember(BatchChangeMemberDTO dto);

    // ===== 导入导出 =====
    void importProject(List<PmsProject> projects, int batchFunc);

    int clearProject(List<PmsProject> projects, boolean delete);

    Map<String, String> exportSpotCheck(Long projectId);

    Map<String, String> exportOverWarrantyRemind(Long projectId);

    void importSpotCheckIgnoreItem(List<?> itemList);

    // ===== P0: 项目成员管理 =====

    /** 更新项目成员(生效截止时间等) */
    void updateProjectMember(Long memberId, LocalDateTime effectiveTo);

    /** 保存安装地址 */
    void saveInstallAddress(Long projectId, String[] selected, String installAddress);

    /** 更新项目实施状态 */
    void updateProjectExecutionState(Long projectId, String executionState);

    // ===== P0: 项目批示 =====

    /** 保存项目批示 */
    void saveInstruction(Long projectId, String instructionsInfo, Long instructionId);

    // ===== P0: 合同拆分合并 =====

    /** 查询合同列表(用于合并) */
    List<Map<String, Object>> queryContractList(String mergeContractNo);

    /** 合并合同 */
    void mergeContract(String[] selected, Long projectId);

    /** 拆分项目 */
    Long branchContract(Long projectId, String projectCode, String mergeBranchMark);

    // ===== P0: 交付件管理 =====

    /** 上传工程交付件 */
    void uploadDeliverableFile(Long projectId, String deliverableType, String fileIds);

    /** 删除工程交付件 */
    void deleteDeliverById(Long deliverId);
}
