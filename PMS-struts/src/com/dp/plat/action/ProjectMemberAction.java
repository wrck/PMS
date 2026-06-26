package com.dp.plat.action;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.util.MessageUtil;

/**
 * 项目成员管理 Action
 * 处理项目成员的增删改查
 * 
 * @author PMS Team
 */
public class ProjectMemberAction extends ProjectBaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // 项目成员参数
    private int memberId;
    private Date memberEffectiveFrom;
    private Date memberEffectiveTo;
    private String memberCode;
    private String memberName;
    private String memberRole;
    private String memberRoleName;
    private String phoneNum;
    private String email;
    private ProjectMember member;
    
    // 安装地址参数
    private String selected;
    private String installAddress;
    
    /**
     * 创建项目成员
     * @return
     */
    public String createMember() {
        try {
            member = new ProjectMember();
            member.setProjectId(projectId);
            member.setProjectType(MessageUtil.PROJECT_TYPE_AFTERSALES);
            member.setMemberCode(memberCode);
            member.setMemberName(memberName);
            member.setMemberRole(memberRole);
            member.setPhoneNum(phoneNum.replaceAll("\\s", ""));
            member.setEmail(email);
            member.setCreateBy(UserContext.getUserContext().getUsername());
            member.setCreateTime(new Date());
            if (memberEffectiveFrom == null) {
                memberEffectiveFrom = new Date();
            }
            member.setEffectiveFrom(memberEffectiveFrom);
            memberId = projectService.insertProjectMember(member);
            projectService.updateProjectLastRefreshTime(projectId);
            projectService.addDynamicNotification(MessageUtil.NOTIFICATION_CODE_113, projectId, memberRoleName);
            result = memberId;
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return SUCCESS;
    }
    
    /**
     * 更新项目成员信息
     * @return
     */
    public String updateMember() {
        try {
            member = new ProjectMember();
            member.setId(memberId);
            if (memberEffectiveTo != null) {
                member.setEffectiveTo(memberEffectiveTo);
            }
            projectService.updateProjectMember(member);
            projectService.updateProjectLastRefreshTime(projectId);
            projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_116, projectId);
            result = memberId;
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return SUCCESS;
    }
    
    /**
     * 保存安装地址
     * @return
     */
    public String saveInstallAdress() {
        try {
            project = projectService.queryProjectById(projectId);
            String contractNo = project.getContractNo();
            if ("14".equals(project.getSalesType())) {
                projectService.insertInstallAddress(selected, projectId, installAddress, project.getContractNo(), project.getColumn001());
            } else {
                projectService.insertInstallAddress(selected, projectId, installAddress, project.getContractNo());
            }
            projectService.updateProjectLastRefreshTime(projectId);
            projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_114, projectId);
            result = 303;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    /**
     * 更新项目实施状态
     * @return
     */
    public String updateProjectExecutionState() {
        try {
            Integer projectId = project.getProjectId();
            String executionState = project.getExecutionState();
            if (!(projectId == null || projectId == 0 || StringUtils.isBlank(executionState))) {
                projectService.updateProjectExecutionState(projectId, executionState);
                result = 313;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }
        return SUCCESS;
    }
    
    // Getter/Setter 方法
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public Date getMemberEffectiveFrom() {
        return memberEffectiveFrom;
    }
    
    public void setMemberEffectiveFrom(Date memberEffectiveFrom) {
        this.memberEffectiveFrom = memberEffectiveFrom;
    }
    
    public Date getMemberEffectiveTo() {
        return memberEffectiveTo;
    }
    
    public void setMemberEffectiveTo(Date memberEffectiveTo) {
        this.memberEffectiveTo = memberEffectiveTo;
    }
    
    public String getMemberCode() {
        return memberCode;
    }
    
    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    public String getMemberRole() {
        return memberRole;
    }
    
    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }
    
    public String getMemberRoleName() {
        return memberRoleName;
    }
    
    public void setMemberRoleName(String memberRoleName) {
        this.memberRoleName = memberRoleName;
    }
    
    public String getPhoneNum() {
        return phoneNum;
    }
    
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public ProjectMember getMember() {
        return member;
    }
    
    public String getSelected() {
        return selected;
    }
    
    public void setSelected(String selected) {
        this.selected = selected;
    }
    
    public String getInstallAddress() {
        return installAddress;
    }
    
    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }
}
