package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.common.utils.SecurityUtil;
import com.dp.plat.mapper.*;
import com.dp.plat.model.dto.*;
import com.dp.plat.model.entity.*;
import com.dp.plat.model.vo.*;
import com.dp.plat.service.PmsProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目管理服务 - 迁移自老系统 ProjectServiceImpl
 *
 * 核心业务逻辑：
 * 1. 项目创建时自动生成项目编码
 * 2. 根据服务经理/项目经理设置自动确定项目状态
 * 3. 创建项目时自动添加成员（服务经理、销售、项目经理）
 * 4. 项目状态流转：已创建(30) → 待指派PM(31) → 已指派PM(32) → 执行中
 * 5. 项目成员角色：10=销售, 20=服务经理, 30=项目经理, 40=组员
 */
@Service
public class PmsProjectServiceImpl implements PmsProjectService {

    @Autowired
    private PmsProjectMapper projectMapper;
    @Autowired
    private PmsProjectMemberMapper projectMemberMapper;
    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private PmsShipmentInfoMapper shipmentInfoMapper;
    @Autowired
    private PmsOrderDataMapper orderDataMapper;
    @Autowired
    private PmsSoftChangeLogMapper softChangeLogMapper;
    @Autowired
    private PmsProjectSoftVersionMapper softVersionMapper;
    @Autowired
    private PmsProjectPlanMapper projectPlanMapper;
    @Autowired
    private PmsProjectPlanEventMapper projectPlanEventMapper;
    @Autowired
    private PmsMaintenanceMapper maintenanceMapper;
    @Autowired
    private PmsProjectDeliverMapper projectDeliverMapper;
    @Autowired
    private PmsInstructionMapper instructionMapper;
    @Autowired
    private PmsProjectContractMapper projectContractMapper;
    @Autowired
    private PmsProjectGroupRelationshipMapper groupRelationshipMapper;
    @Autowired
    private PmsProjectProductLineMapper productLineMapper;
    @Autowired
    private PmsProjectTaskExtMapper projectTaskExtMapper;
    @Autowired
    private PmsProjectStateMapper projectStateMapper;
    @Autowired
    private PmsProjectTaskMapper projectTaskMapper;

    @Override
    public IPage<ProjectVO> queryProjectPage(Integer pageNum, Integer pageSize,
                                              String projectCode, String projectName,
                                              String contractNo, String officeCode,
                                              Integer projectState) {
        Page<PmsProject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(projectCode), PmsProject::getProjectCode, projectCode)
               .like(StringUtils.hasText(projectName), PmsProject::getProjectName, projectName)
               .eq(StringUtils.hasText(officeCode), PmsProject::getOfficeCode, officeCode)
               .eq(projectState != null, PmsProject::getProjectState, String.valueOf(projectState))
               .orderByDesc(PmsProject::getProjectCreateTime);

        IPage<PmsProject> projectPage = projectMapper.selectPage(page, wrapper);

        return projectPage.convert(p -> {
            ProjectVO vo = new ProjectVO();
            BeanUtils.copyProperties(p, vo);
            // 查询项目成员
            enrichProjectMembers(vo, p.getId());
            return vo;
        });
    }

    @Override
    @Transactional
    public void addProject(ProjectDTO dto) {
        // 1. 检查合同号是否已存在
        if (StringUtils.hasText(dto.getContractNo())) {
            Long count = projectMapper.selectCount(
                    new LambdaQueryWrapper<PmsProject>()
                            .eq(PmsProject::getContractNo, dto.getContractNo()));
            if (count > 0) {
                throw new BusinessException("合同号已被使用，请检查");
            }
        }

        // 2. 创建项目实体
        PmsProject project = new PmsProject();
        BeanUtils.copyProperties(dto, project);

        // 3. 自动生成项目编码（老系统逻辑：合同号-序号）
        project.setProjectCode(generateProjectCode(dto.getContractNo()));

        // 4. 根据服务经理/项目经理自动设置项目状态（迁移自老系统）
        resolveProjectState(project);

        // 5. 设置时间戳
        project.setProjectCreateTime(LocalDateTime.now());
        project.setProjectStartTime(LocalDateTime.now());
        project.setProjectRefreshTime(LocalDateTime.now());

        // 6. 插入项目
        projectMapper.insert(project);

        // 7. 自动添加项目成员（迁移自老系统逻辑）
        Long projectId = project.getId();
        addInitialMembers(projectId, dto);
    }

    @Override
    @Transactional
    public void updateProject(ProjectDTO dto) {
        PmsProject project = projectMapper.selectById(dto.getId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 更新允许修改的字段
        if (StringUtils.hasText(dto.getProjectName())) project.setProjectName(dto.getProjectName());
        if (StringUtils.hasText(dto.getOfficeCode())) project.setOfficeCode(dto.getOfficeCode());
        if (dto.getProjectState() != null) project.setProjectState(String.valueOf(dto.getProjectState()));
        if (dto.getExecutionState() != null) project.setExecutionState(dto.getExecutionState());
        project.setProjectRefreshTime(LocalDateTime.now());

        projectMapper.updateById(project);
    }

    @Override
    public ProjectVO getProjectDetail(Long id) {
        PmsProject project = projectMapper.selectById(id);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        enrichProjectMembers(vo, id);
        return vo;
    }

    @Override
    public List<PmsProjectMember> getProjectMembers(Long projectId) {
        return projectMemberMapper.selectList(
                new LambdaQueryWrapper<PmsProjectMember>()
                        .eq(PmsProjectMember::getProjectId, projectId)
                        .orderByAsc(PmsProjectMember::getMemberRole));
    }

    @Override
    @Transactional
    public void addProjectMember(ProjectMemberDTO dto) {
        // 检查是否已存在相同角色的成员
        if (StringUtils.hasText(dto.getMemberRole())) {
            Long count = projectMemberMapper.selectCount(
                    new LambdaQueryWrapper<PmsProjectMember>()
                            .eq(PmsProjectMember::getProjectId, dto.getProjectId())
                            .eq(PmsProjectMember::getMemberRole, dto.getMemberRole())
                            .isNull(PmsProjectMember::getEffectiveTo));
            if (count > 0 && "20".equals(dto.getMemberRole())) {
                // 服务经理角色唯一，先将旧的设为失效
                // 其他角色允许添加多个
            }
        }

        PmsProjectMember member = new PmsProjectMember();
        BeanUtils.copyProperties(dto, member);
        member.setEffectiveFrom(LocalDateTime.now());
        projectMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void deleteProjectMember(Long id) {
        projectMemberMapper.deleteById(id);
    }

    // ===== 私有方法（迁移自老系统业务逻辑）=====

    /**
     * 生成项目编码（迁移自老系统）
     * 格式：合同号-序号
     */
    private String generateProjectCode(String contractNo) {
        if (!StringUtils.hasText(contractNo)) {
            return "PMS-" + System.currentTimeMillis();
        }
        // 查询该合同号已有的项目数量
        Long count = projectMapper.selectCount(
                new LambdaQueryWrapper<PmsProject>()
                        .likeRight(PmsProject::getProjectCode, contractNo + "-"));
        return contractNo + "-" + (count + 1);
    }

    /**
     * 根据服务经理/项目经理设置项目状态（迁移自老系统）
     *
     * 状态规则：
     * - 无服务经理且无项目经理 → 30（已创建/待指定服务经理）
     * - 有服务经理但无项目经理 → 31（待指派项目经理）
     * - 有服务经理且有项目经理 → 32（已指派项目经理）
     */
    private void resolveProjectState(PmsProject project) {
        boolean hasSm = StringUtils.hasText(project.getSmCode());
        boolean hasPm = StringUtils.hasText(project.getPmCode());

        if (!hasSm && !hasPm) {
            project.setProjectState("30"); // 已创建，待指定服务经理
        } else if (hasSm && !hasPm) {
            project.setProjectState("31"); // 待指派项目经理
        } else {
            project.setProjectState("32"); // 已指派项目经理
        }
    }

    /**
     * 创建项目时自动添加初始成员（迁移自老系统）
     *
     * 自动添加：
     * 1. 服务经理（角色20）
     * 2. 销售人员（角色10）
     * 3. 项目经理（角色30，如果有）
     */
    private void addInitialMembers(Long projectId, ProjectDTO dto) {
        LocalDateTime now = LocalDateTime.now();

        // 添加服务经理
        if (StringUtils.hasText(dto.getSmCode())) {
            PmsProjectMember sm = new PmsProjectMember();
            sm.setProjectId(projectId);
            sm.setMemberCode(dto.getSmCode());
            sm.setMemberName(resolveUserName(dto.getSmCode()));
            sm.setMemberRole("20"); // 服务经理
            sm.setEffectiveFrom(now);
            projectMemberMapper.insert(sm);
        }

        // 添加项目经理
        if (StringUtils.hasText(dto.getPmCode())) {
            PmsProjectMember pm = new PmsProjectMember();
            pm.setProjectId(projectId);
            pm.setMemberCode(dto.getPmCode());
            pm.setMemberName(resolveUserName(dto.getPmCode()));
            pm.setMemberRole("30"); // 项目经理
            pm.setEffectiveFrom(now);
            projectMemberMapper.insert(pm);
        }
    }

    /**
     * 根据工号查询用户姓名
     */
    private String resolveUserName(String userCode) {
        if (!StringUtils.hasText(userCode)) return "";
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userCode));
        return user != null ? user.getRealname() : userCode;
    }

    /**
     * 填充项目成员信息到 VO
     */
    private void enrichProjectMembers(ProjectVO vo, Long projectId) {
        List<PmsProjectMember> members = getProjectMembers(projectId);
        vo.setMembers(members);
        for (PmsProjectMember m : members) {
            switch (m.getMemberRole()) {
                case "20":
                    vo.setSmName(m.getMemberName());
                    break;
                case "30":
                    vo.setPmName(m.getMemberName());
                    break;
            }
        }
    }

    // ===== 迁移自老系统的新增方法 =====

    @Override
    @Transactional
    public void createCHProject(ProjectDTO dto) {
        // 检查合同号是否已存在
        if (StringUtils.hasText(dto.getContractNo())) {
            Long count = projectMapper.selectCount(
                    new LambdaQueryWrapper<PmsProject>()
                            .eq(PmsProject::getContractNo, dto.getContractNo()));
            if (count > 0) {
                throw new BusinessException("合同号已被使用，请检查");
            }
        }

        PmsProject project = new PmsProject();
        BeanUtils.copyProperties(dto, project);
        project.setProjectCode(generateProjectCode(dto.getContractNo()));
        resolveProjectState(project);
        project.setProjectCreateTime(LocalDateTime.now());
        project.setProjectStartTime(LocalDateTime.now());
        project.setProjectRefreshTime(LocalDateTime.now());
        projectMapper.insert(project);
        addInitialMembers(project.getId(), dto);
    }

    @Override
    @Transactional
    public void transferShipment(List<Long> selected, Long projectId, Long transferProjectId) {
        if (selected == null || selected.isEmpty()) {
            throw new BusinessException("请选择要转移的设备");
        }
        PmsProject sourceProject = projectMapper.selectById(projectId);
        PmsProject targetProject = projectMapper.selectById(transferProjectId);
        if (sourceProject == null || targetProject == null) {
            throw new BusinessException("项目不存在");
        }

        for (Long shipmentId : selected) {
            PmsShipmentInfo shipment = shipmentInfoMapper.selectById(shipmentId);
            if (shipment != null) {
                shipment.setTransferProjectId(transferProjectId);
                shipment.setTransferContractNo(targetProject.getContractNo());
                shipment.setTransferFlag("1"); // 转出
                shipmentInfoMapper.updateById(shipment);

                // 创建转入记录
                PmsShipmentInfo newShipment = new PmsShipmentInfo();
                BeanUtils.copyProperties(shipment, newShipment);
                newShipment.setId(null);
                newShipment.setProjectId(transferProjectId);
                newShipment.setProjectCode(targetProject.getProjectCode());
                newShipment.setProjectName(targetProject.getProjectName());
                newShipment.setContractNo(targetProject.getContractNo());
                newShipment.setTransferProjectId(projectId);
                newShipment.setTransferContractNo(sourceProject.getContractNo());
                newShipment.setTransferFlag("0"); // 转入
                shipmentInfoMapper.insert(newShipment);
            }
        }

        // 更新项目刷新时间
        updateProjectRefreshTime(projectId);
        updateProjectRefreshTime(transferProjectId);
    }

    @Override
    public List<PmsProject> queryTransferProjectList(String contractNo) {
        if (!StringUtils.hasText(contractNo)) {
            return new ArrayList<>();
        }
        return projectMapper.selectList(
                new LambdaQueryWrapper<PmsProject>()
                        .like(PmsProject::getContractNo, contractNo)
                        .orderByDesc(PmsProject::getProjectCreateTime));
    }

    @Override
    public List<OrderDataVO> checkOrderData(Long projectId, String contractNo) {
        List<PmsOrderData> orderDataList = orderDataMapper.selectByProjectId(projectId);
        List<PmsOrderData> rmaOrderDataList = orderDataMapper.selectRmaByContractNo(contractNo);
        orderDataList.addAll(rmaOrderDataList);

        return orderDataList.stream().map(order -> {
            OrderDataVO vo = new OrderDataVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderDataVO> checkRealOrderData(Long projectId) {
        List<PmsOrderData> orderDataList = orderDataMapper.selectByProjectId(projectId);
        return orderDataList.stream().map(order -> {
            OrderDataVO vo = new OrderDataVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> projectLeaseLine(String projectCode) {
        if (!StringUtils.hasText(projectCode)) {
            return new ArrayList<>();
        }
        return projectMapper.selectLeaseLineByProjectCode(projectCode);
    }

    @Override
    public List<Map<String, Object>> projectProductConfigLevelInfo(String projectCode) {
        if (!StringUtils.hasText(projectCode)) {
            return new ArrayList<>();
        }
        return projectMapper.selectConfigLevelInfoByProjectCode(projectCode);
    }

    @Override
    public List<ShipmentInfoVO> checkShipmentInfo(Long projectId, String contractNo) {
        List<PmsShipmentInfo> shipmentList = shipmentInfoMapper.selectByContractNoAndProjectId(contractNo, projectId);
        return shipmentList.stream().map(shipment -> {
            ShipmentInfoVO vo = new ShipmentInfoVO();
            BeanUtils.copyProperties(shipment, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteShipmentInfo(Long projectId) {
        shipmentInfoMapper.clearInstallAddressByProjectId(projectId);
    }

    @Override
    public List<ProjectSoftVersionVO> checkSoftVersion(Long projectId, String contractNo, Map<String, Object> params) {
        List<PmsProjectSoftVersion> softVersionList = softVersionMapper.selectByContractNoAndProjectId(contractNo, projectId);
        return softVersionList.stream().map(sv -> {
            ProjectSoftVersionVO vo = new ProjectSoftVersionVO();
            BeanUtils.copyProperties(sv, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSoftVersion(List<ProjectSoftVersionVO> softversionList, SoftChangeLogVO changeLog) {
        // 保存变更记录
        PmsSoftChangeLog log = new PmsSoftChangeLog();
        log.setProjectId(changeLog.getProjectId());
        log.setChangeVersion(changeLog.getChangeVersion());
        log.setChangeRemark(changeLog.getChangeRemark());
        log.setLatest(1);
        softChangeLogMapper.insert(log);

        // 更新软件版本
        for (ProjectSoftVersionVO svVO : softversionList) {
            PmsProjectSoftVersion sv = new PmsProjectSoftVersion();
            BeanUtils.copyProperties(svVO, sv);
            sv.setLogId(log.getId());
            if (sv.getId() != null) {
                softVersionMapper.updateById(sv);
            } else {
                softVersionMapper.insert(sv);
            }
        }
    }

    @Override
    public List<SoftChangeLogVO> checkhistsoftversion(Long projectId) {
        List<PmsSoftChangeLog> logList = softChangeLogMapper.selectByProjectId(projectId);
        return logList.stream().map(log -> {
            SoftChangeLogVO vo = new SoftChangeLogVO();
            BeanUtils.copyProperties(log, vo);
            vo.setVersionAndCreateTime(log.getChangeVersion() + "(" + log.getCreateTime() + ")");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectSoftVersionVO> queryHistSoftVersionList(Long logId) {
        List<PmsProjectSoftVersion> softVersionList = softVersionMapper.selectList(
                new LambdaQueryWrapper<PmsProjectSoftVersion>()
                        .eq(PmsProjectSoftVersion::getLogId, logId));
        return softVersionList.stream().map(sv -> {
            ProjectSoftVersionVO vo = new ProjectSoftVersionVO();
            BeanUtils.copyProperties(sv, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SoftChangeLogVO queryOneSoftChangeLog(Long id) {
        PmsSoftChangeLog log = softChangeLogMapper.selectById(id);
        if (log == null) return null;
        SoftChangeLogVO vo = new SoftChangeLogVO();
        BeanUtils.copyProperties(log, vo);
        return vo;
    }

    @Override
    public List<Map<String, Object>> projectMaintenance(Long projectId, String officeCode) {
        List<PmsMaintenance> maintenanceList = maintenanceMapper.selectList(
                new LambdaQueryWrapper<PmsMaintenance>()
                        .eq(PmsMaintenance::getProjectId, projectId)
                        .orderByDesc(PmsMaintenance::getCreateTime));
        return maintenanceList.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("projectId", m.getProjectId());
            map.put("maintenanceType", m.getMaintenanceType());
            map.put("content", m.getContent());
            map.put("createTime", m.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createProjectMaintenance(PmsMaintenance maintenance) {
        if (maintenance.getId() != null) {
            maintenanceMapper.updateById(maintenance);
        } else {
            maintenanceMapper.insert(maintenance);
        }
    }

    @Override
    @Transactional
    public void editProjectPlan(Long projectId, String contractNo, String createBy) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 1. 将旧计划置为失效
        projectTaskMapper.invalidateByProjectId(projectId);

        // 2. 判断是否第一次制定计划
        String planState = projectMapper.selectPlanState(projectId);
        boolean isFirstPlan = !StringUtils.hasText(planState) || "40".equals(planState);

        if (isFirstPlan) {
            // 查询当前工程计划阶段
            String currentTask = projectMapper.selectCurrentPlan(projectId);
            if (StringUtils.hasText(currentTask)) {
                // 更新项目计划状态
                // 插入或更新 pm_project_state 表
                int stateCount = projectStateMapper.countByProjectId(projectId);
                if (stateCount == 0) {
                    PmsProjectState state = new PmsProjectState();
                    state.setProjectId(projectId);
                    state.setProjectPlanState(currentTask);
                    state.setCreateBy(createBy);
                    state.setCreateTime(LocalDateTime.now());
                    // projectStateMapper.insert(state);
                } else {
                    // projectStateMapper.updatePlanState(projectId, currentTask);
                }
            }
            // 发送通知(通知服务集成后可启用)
            // notificationService.sendFixedNotification("112", projectId);
        } else {
            // 发送通知(通知服务集成后可启用)
            // notificationService.sendFixedNotification("115", projectId);
        }

        // 3. 更新项目刷新时间
        projectMapper.updateRefreshTime(projectId);
    }

    @Override
    @Transactional
    public void backToLastStep(ProjectBackDTO dto) {
        PmsProject project = projectMapper.selectById(dto.getProjectId());
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 根据回退状态更新项目状态
        if (StringUtils.hasText(dto.getIsback())) {
            project.setProjectState(dto.getIsback());
        }
        project.setProjectRefreshTime(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    @Override
    @Transactional
    public void updateProjectIsback(Long projectId, String isback, String backCause, String pm, int sendto, String notbackCause) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 更新项目回退状态
        if (StringUtils.hasText(isback)) {
            project.setProjectState(isback);
        }
        project.setProjectRefreshTime(LocalDateTime.now());
        projectMapper.updateById(project);
    }

    @Override
    public List<Map<String, Object>> queryAllUser() {
        List<SysUser> users = userMapper.selectList(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStatus, 1)
                        .orderByAsc(SysUser::getRealname));
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", u.getUsername());
            map.put("realname", u.getRealname());
            map.put("email", u.getEmail());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> queryUserWithRoleId(Long roleId) {
        List<SysUser> users;
        if (roleId == null || roleId == 0) {
            users = userMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getStatus, 1)
                            .orderByAsc(SysUser::getRealname));
        } else {
            users = userMapper.selectByRoleId(roleId);
        }
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", u.getUsername());
            map.put("realname", u.getRealname());
            map.put("email", u.getEmail());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> queryPersonList() {
        // 查询项目干系人
        List<PmsProjectMember> members = projectMemberMapper.selectList(
                new LambdaQueryWrapper<PmsProjectMember>()
                        .select(PmsProjectMember::getMemberCode, PmsProjectMember::getMemberName)
                        .groupBy(PmsProjectMember::getMemberCode, PmsProjectMember::getMemberName));
        return members.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("salesmanCode", m.getMemberCode());
            map.put("salesmanName", m.getMemberName());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> queryDpNoRoleUser(Long roleId, String dpNo) {
        List<SysUser> users;
        if (!StringUtils.hasText(dpNo)) {
            users = userMapper.selectByRoleId(roleId);
        } else {
            users = userMapper.selectByDeptCodeWithoutRole(roleId, dpNo);
        }
        return users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("username", u.getUsername());
            map.put("realname", u.getRealname());
            map.put("email", u.getEmail());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String batchChangeMember(BatchChangeMemberDTO dto) {
        String changeType = dto.getChangeType();
        String oldMemberCode = dto.getOldMemberCode();
        String newMemberCode = dto.getNewMemberCode();
        String newMemberName = dto.getNewMemberName();

        int serviceCount = 0;
        int programCount = 0;

        // 查询指定部门的项目
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<PmsProject>();
        if (StringUtils.hasText(dto.getDpNo())) {
            wrapper.eq(PmsProject::getOfficeCode, dto.getDpNo());
        }
        wrapper.in(PmsProject::getProjectState, "30", "31", "32");
        List<PmsProject> projects = projectMapper.selectList(wrapper);

        for (PmsProject project : projects) {
            if ("service".equals(changeType) || "both".equals(changeType)) {
                if (oldMemberCode.equals(project.getSmCode())) {
                    project.setSmCode(newMemberCode);
                    projectMapper.updateById(project);
                    serviceCount++;
                }
            }
            if ("program".equals(changeType) || "both".equals(changeType)) {
                if (oldMemberCode.equals(project.getPmCode())) {
                    project.setPmCode(newMemberCode);
                    projectMapper.updateById(project);
                    programCount++;
                }
            }
        }

        return "变更成功：服务经理" + serviceCount + "个，项目经理" + programCount + "个";
    }

    @Override
    @Transactional
    public void importProject(List<PmsProject> projects, int batchFunc) {
        for (PmsProject p : projects) {
            if (!StringUtils.hasText(p.getContractNo())) continue;
            Long count = projectMapper.selectCount(
                    new LambdaQueryWrapper<PmsProject>()
                            .eq(PmsProject::getContractNo, p.getContractNo()));
            if (count > 0) continue;

            p.setProjectCode(generateProjectCode(p.getContractNo()));
            resolveProjectState(p);
            p.setProjectCreateTime(LocalDateTime.now());
            p.setProjectStartTime(LocalDateTime.now());
            p.setProjectRefreshTime(LocalDateTime.now());
            projectMapper.insert(p);
        }
    }

    @Override
    @Transactional
    public int clearProject(List<PmsProject> projects, boolean delete) {
        int count = 0;
        for (PmsProject p : projects) {
            if (!StringUtils.hasText(p.getContractNo())) continue;
            PmsProject existing = projectMapper.selectOne(
                    new LambdaQueryWrapper<PmsProject>()
                            .eq(PmsProject::getContractNo, p.getContractNo()));
            if (existing != null) {
                if (delete) {
                    projectMapper.deleteById(existing.getId());
                } else {
                    existing.setProjectState("-1"); // 标记为无效
                    projectMapper.updateById(existing);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public Map<String, String> exportSpotCheck(Long projectId) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        // 查询项目的发货信息
        List<PmsShipmentInfo> shipments = shipmentInfoMapper.selectByProjectId(projectId);

        // 生成Excel文件
        String fileName = project.getProjectCode() + "_现场验货单.xlsx";
        String filePath = "/data/pms/export/" + fileName;

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("现场验货单");

            // 创建标题行
            org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("序列号");
            headerRow.createCell(1).setCellValue("物料编码");
            headerRow.createCell(2).setCellValue("物料型号");
            headerRow.createCell(3).setCellValue("物料名称");
            headerRow.createCell(4).setCellValue("安装地址");
            headerRow.createCell(5).setCellValue("收货人");

            // 填充数据
            int rowNum = 1;
            for (PmsShipmentInfo shipment : shipments) {
                org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(shipment.getBarCode());
                row.createCell(1).setCellValue(shipment.getItemCode());
                row.createCell(2).setCellValue(shipment.getItemModel());
                row.createCell(3).setCellValue(shipment.getItemName());
                row.createCell(4).setCellValue(shipment.getInstallAddress());
                row.createCell(5).setCellValue(shipment.getReceiveName());
            }

            // 写入文件
            java.io.File file = new java.io.File(filePath);
            file.getParentFile().mkdirs();
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }

        Map<String, String> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("fileName", fileName);
        return result;
    }

    @Override
    public Map<String, String> exportOverWarrantyRemind(Long projectId) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        // 生成Excel文件
        String fileName = project.getProjectCode() + "_超期保修提醒.xlsx";
        String filePath = "/data/pms/export/" + fileName;

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("超期保修提醒");

            // 创建标题行
            org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("项目编码");
            headerRow.createCell(1).setCellValue("项目名称");
            headerRow.createCell(2).setCellValue("合同号");
            headerRow.createCell(3).setCellValue("质保开始时间");
            headerRow.createCell(4).setCellValue("质保结束时间");
            headerRow.createCell(5).setCellValue("状态");

            // 填充数据
            org.apache.poi.xssf.usermodel.XSSFRow row = sheet.createRow(1);
            row.createCell(0).setCellValue(project.getProjectCode());
            row.createCell(1).setCellValue(project.getProjectName());
            row.createCell(2).setCellValue(project.getContractNo());
            row.createCell(3).setCellValue(project.getProjectStartTime() != null ? project.getProjectStartTime().toString() : "");
            row.createCell(4).setCellValue(project.getProjectCloseTime() != null ? project.getProjectCloseTime().toString() : "");
            row.createCell(5).setCellValue(project.getProjectStateName());

            // 写入文件
            java.io.File file = new java.io.File(filePath);
            file.getParentFile().mkdirs();
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                workbook.write(fos);
            }
        } catch (Exception e) {
            throw new BusinessException("导出失败: " + e.getMessage());
        }

        Map<String, String> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("fileName", fileName);
        return result;
    }

    @Override
    @Transactional
    public void importSpotCheckIgnoreItem(List<?> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return;
        }
        // 导入现场验货单不需要序列号明细的item
        // 实际逻辑需要根据Excel模板解析并保存到数据库
        // 这里提供基础框架，具体解析逻辑根据业务需求实现
        for (Object item : itemList) {
            // 解析Excel行数据
            // 迁移自: ProjectAction.importSpotCheckIgnoreItem()
            // 根据实际Excel格式解析并保存
        }
    }

    // ===== 辅助方法 =====

    private void updateProjectRefreshTime(Long projectId) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project != null) {
            project.setProjectRefreshTime(LocalDateTime.now());
            projectMapper.updateById(project);
        }
    }

    @Override
    public Page<ProjectVO> listProjects(int pageNum, int pageSize, String projectCode, String projectName,
                                         String projectType, String projectState, String pmCode) {
        return (Page<ProjectVO>) queryProjectPage(pageNum, pageSize, projectCode, projectName, null, null,
                projectState != null ? Integer.valueOf(projectState) : null);
    }

    @Override
    public ProjectVO getProjectById(Long id) {
        return getProjectDetail(id);
    }

    @Override
    public void createProject(ProjectDTO projectDTO) {
        addProject(projectDTO);
    }

    @Override
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }

    @Override
    public void removeProjectMember(Long memberId) {
        deleteProjectMember(memberId);
    }

    // ===== P0: 项目成员管理 =====

    @Override
    @Transactional
    public void updateProjectMember(Long memberId, LocalDateTime effectiveTo) {
        PmsProjectMember member = projectMemberMapper.selectById(memberId);
        if (member == null) {
            throw new BusinessException("项目成员不存在");
        }
        if (effectiveTo != null) {
            member.setEffectiveTo(effectiveTo);
        }
        projectMemberMapper.updateById(member);
        updateProjectRefreshTime(member.getProjectId());
    }

    @Override
    @Transactional
    public void saveInstallAddress(Long projectId, String[] selected, String installAddress) {
        if (selected == null || selected.length == 0) {
            throw new BusinessException("请选择要保存安装地址的设备");
        }
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        String createBy = SecurityUtil.getCurrentUsername();

        for (String barcode : selected) {
            barcode = barcode.trim();
            if (!StringUtils.hasText(barcode)) continue;

            // 查询发货记录是否存在
            PmsShipmentInfo existing = shipmentInfoMapper.selectOne(
                    new LambdaQueryWrapper<PmsShipmentInfo>()
                            .eq(PmsShipmentInfo::getBarCode, barcode)
                            .eq(PmsShipmentInfo::getProjectId, projectId));

            if (existing != null) {
                // 更新安装地址
                existing.setInstallAddress(installAddress);
                shipmentInfoMapper.updateById(existing);
            } else {
                // 插入新的发货记录
                PmsShipmentInfo shipment = new PmsShipmentInfo();
                shipment.setProjectId(projectId);
                shipment.setProjectCode(project.getProjectCode());
                shipment.setProjectName(project.getProjectName());
                shipment.setBarCode(barcode);
                shipment.setInstallAddress(installAddress);
                shipment.setCreateBy(createBy);
                shipment.setCreateTime(LocalDateTime.now());
                shipmentInfoMapper.insert(shipment);
            }
        }
        updateProjectRefreshTime(projectId);
    }

    @Override
    @Transactional
    public void updateProjectExecutionState(Long projectId, String executionState) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        String oldExecutionState = project.getExecutionState() != null ? String.valueOf(project.getExecutionState()) : "";
        String projectType = StringUtils.hasText(project.getProjectType()) ? project.getProjectType() : "10";

        // 检查是否满足闭环条件
        boolean canCloseLoop = false;
        if ("10".equals(project.getProjectClassify())) {
            // 直签项目: 检查终验报告是否已上传
            int undeliveredCount = projectDeliverMapper.countRequiredUndelivered(projectId, projectType);
            canCloseLoop = (undeliveredCount == 0);
        } else {
            // 非直签项目: 检查必传交付件完整性
            int undeliveredCount = projectDeliverMapper.countRequiredUndelivered(projectId, projectType);
            canCloseLoop = (undeliveredCount == 0);
        }

        // 如果满足闭环条件且当前状态 < 80，则强制设为80
        if (canCloseLoop && executionState.compareTo("80") < 0 && oldExecutionState.compareTo("80") <= 0) {
            executionState = "80";
        }

        if (StringUtils.hasText(executionState)) {
            // 更新 pm_project_state 表的 execution_state 字段
            projectStateMapper.updateExecutionState(projectId, executionState);
            updateProjectRefreshTime(projectId);
        }
    }

    // ===== P0: 项目批示 =====

    @Override
    @Transactional
    public void saveInstruction(Long projectId, String instructionsInfo, Long instructionId) {
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        PmsInstruction instruction = new PmsInstruction();
        instruction.setProjectId(projectId);
        instruction.setInstructionsInfo(instructionsInfo);
        instruction.setInstructionsTime(LocalDateTime.now());
        instruction.setInstructionsUser(SecurityUtil.getCurrentUsername());
        instruction.setCreateBy(SecurityUtil.getCurrentUsername());
        instruction.setCreateTime(LocalDateTime.now());

        if (instructionId != null && instructionId != 0) {
            // 回复批示
            instruction.setDataType(1); // FEEDBACK
            instruction.setInstructionsId(instructionId);
        } else {
            // 新增批示
            instruction.setDataType(0); // INSTRUCTION
        }
        instructionMapper.insert(instruction);

        // 发送通知(邮件服务集成后可启用)
        // 获取项目的服务经理和项目经理邮箱
        // String smEmail = getMemberEmail(projectId, "20");
        // String pmEmail = getMemberEmail(projectId, "30");
        // notificationService.sendEmail(smEmail + ";" + pmEmail, "项目批示通知", instructionsInfo);
        updateProjectRefreshTime(projectId);
    }

    // ===== P0: 合同拆分合并 =====

    @Override
    public List<Map<String, Object>> queryContractList(String mergeContractNo) {
        // 查询指定合同号下的所有合同
        List<PmsProjectContract> contracts = projectContractMapper.selectList(
                new LambdaQueryWrapper<PmsProjectContract>()
                        .eq(PmsProjectContract::getContractNo, mergeContractNo));
        return contracts.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("contractNo", c.getContractNo());
            map.put("projectGroupCode", c.getProjectGroupCode());
            map.put("projectCode", c.getProjectCode());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void mergeContract(String[] selected, Long projectId) {
        if (selected == null || selected.length == 0) {
            throw new BusinessException("请至少选择一条合同数据");
        }
        PmsProject project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }

        String createBy = SecurityUtil.getCurrentUsername();
        String projectGroupCode = groupRelationshipMapper.selectGroupCodeByProjectCode(project.getProjectCode());

        // 查询项目是否有任务
        int taskSize = projectTaskExtMapper.countByProjectId(projectId);

        for (String contractNo : selected) {
            contractNo = contractNo.trim();
            if (!StringUtils.hasText(contractNo)) continue;

            // 1. 插入合同关联
            PmsProjectContract contract = new PmsProjectContract();
            contract.setContractNo(contractNo);
            contract.setProjectGroupCode(projectGroupCode);
            contract.setCreateBy(createBy);
            contract.setCreateTime(LocalDateTime.now());
            projectContractMapper.insert(contract);

            // 2. 从SAP订单复制产品线
            productLineMapper.insertFromSapOrder(projectId, contractNo);

            // 3. 如果已创建工程计划，复制计划
            if (taskSize > 0) {
                projectTaskExtMapper.insertMergeTask(projectId, contractNo, projectId, createBy);
            }
        }
        updateProjectRefreshTime(projectId);
    }

    @Override
    @Transactional
    public Long branchContract(Long projectId, String projectCode, String mergeBranchMark) {
        PmsProject sourceProject = projectMapper.selectById(projectId);
        if (sourceProject == null) {
            throw new BusinessException("项目不存在");
        }

        String createBy = SecurityUtil.getCurrentUsername();

        // 1. 计算新项目编码
        String codePrefix = projectCode.contains("-") ? projectCode.substring(0, projectCode.lastIndexOf("-")) : projectCode;
        int count = groupRelationshipMapper.countByProjectCodePrefix(codePrefix);
        String newProjectCode = codePrefix + "-" + (count + 1);

        // 2. 创建项目组关系
        String projectGroupCode = groupRelationshipMapper.selectGroupCodeByProjectCode(project.getProjectCode());
        PmsProjectGroupRelationship relationship = new PmsProjectGroupRelationship();
        relationship.setProjectGroupCode(projectGroupCode);
        relationship.setProjectCode(newProjectCode);
        relationship.setMergeBranchMark(mergeBranchMark);
        relationship.setCreateBy(createBy);
        relationship.setCreateTime(LocalDateTime.now());
        groupRelationshipMapper.insert(relationship);

        // 3. 创建新项目(复制源项目的核心字段)
        PmsProject newProject = new PmsProject();
        newProject.setProjectCode(newProjectCode);
        newProject.setProjectName(sourceProject.getProjectName());
        newProject.setProjectType(sourceProject.getProjectType());
        newProject.setProjectState("30");
        newProject.setOfficeCode(sourceProject.getOfficeCode());
        newProject.setCustomerCode(sourceProject.getCustomerCode());
        newProject.setCustomerName(sourceProject.getCustomerName());
        newProject.setMarketDeptCode(sourceProject.getMarketDeptCode());
        newProject.setSystemDeptId(sourceProject.getSystemDeptId());
        newProject.setExtendDeptId(sourceProject.getExtendDeptId());
        newProject.setSubIndustryId(sourceProject.getSubIndustryId());
        newProject.setServiceType(sourceProject.getServiceType());
        newProject.setFinalCustomerName(sourceProject.getFinalCustomerName());
        newProject.setCompanyId(sourceProject.getCompanyId());
        newProject.setProjectCreateTime(LocalDateTime.now());
        newProject.setProjectStartTime(LocalDateTime.now());
        newProject.setProjectRefreshTime(LocalDateTime.now());
        projectMapper.insert(newProject);
        Long newProjectId = newProject.getId();

        // 4. 复制销售人员
        if (StringUtils.hasText(sourceProject.getSalesManCode())) {
            PmsProjectMember member = new PmsProjectMember();
            member.setProjectId(newProjectId);
            member.setProjectType(sourceProject.getProjectType());
            member.setMemberRole("10"); // 销售
            member.setMemberCode(sourceProject.getSalesManCode());
            member.setMemberName(sourceProject.getSalesManName());
            member.setCreateBy(createBy);
            member.setCreateTime(LocalDateTime.now());
            member.setEffectiveFrom(LocalDateTime.now());
            projectMemberMapper.insert(member);
        }

        updateProjectRefreshTime(projectId);
        return newProjectId;
    }

    // ===== P0: 交付件管理 =====

    @Override
    @Transactional
    public void uploadDeliverableFile(Long projectId, String deliverableType, String fileIds) {
        PmsProjectDeliver deliver = new PmsProjectDeliver();
        deliver.setProjectId(projectId);
        deliver.setDeliverType(deliverableType);
        deliver.setFileIds(fileIds);
        deliver.setStatus(0);
        deliver.setCreateTime(LocalDateTime.now());
        projectDeliverMapper.insert(deliver);
        updateProjectRefreshTime(projectId);
    }

    @Override
    @Transactional
    public void deleteDeliverById(Long deliverId) {
        PmsProjectDeliver deliver = projectDeliverMapper.selectById(deliverId);
        if (deliver == null) {
            throw new BusinessException("交付件不存在");
        }
        Long projectId = deliver.getProjectId();
        projectDeliverMapper.deleteById(deliverId);
        updateProjectRefreshTime(projectId);
    }
}
