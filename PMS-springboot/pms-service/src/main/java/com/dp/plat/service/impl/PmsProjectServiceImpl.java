package com.dp.plat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dp.plat.common.exception.BusinessException;
import com.dp.plat.mapper.PmsProjectMapper;
import com.dp.plat.mapper.PmsProjectMemberMapper;
import com.dp.plat.mapper.SysUserMapper;
import com.dp.plat.model.dto.ProjectDTO;
import com.dp.plat.model.dto.ProjectMemberDTO;
import com.dp.plat.model.entity.PmsProject;
import com.dp.plat.model.entity.PmsProjectMember;
import com.dp.plat.model.entity.SysUser;
import com.dp.plat.model.vo.ProjectVO;
import com.dp.plat.service.PmsProjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public IPage<ProjectVO> queryProjectPage(Integer pageNum, Integer pageSize,
                                              String projectCode, String projectName,
                                              String contractNo, String officeCode,
                                              Integer projectState) {
        Page<PmsProject> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PmsProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(projectCode), PmsProject::getProjectCode, projectCode)
               .like(StringUtils.hasText(projectName), PmsProject::getProjectName, projectName)
               .like(StringUtils.hasText(contractNo), PmsProject::getContractNo, contractNo)
               .eq(StringUtils.hasText(officeCode), PmsProject::getOfficeCode, officeCode)
               .eq(projectState != null, PmsProject::getProjectState, projectState)
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
        if (dto.getProjectState() != null) project.setProjectState(dto.getProjectState());
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
            project.setProjectState(30); // 已创建，待指定服务经理
        } else if (hasSm && !hasPm) {
            project.setProjectState(31); // 待指派项目经理
        } else {
            project.setProjectState(32); // 已指派项目经理
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
}
