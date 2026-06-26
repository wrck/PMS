package com.dp.plat.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;

import com.dp.plat.data.bean.Instruction;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.util.MessageUtil;

/**
 * 项目通知批示管理 Action
 * 处理项目通知、批示、问题工单等操作
 * 
 * @author PMS Team
 */
public class ProjectNotificationAction extends ProjectBaseAction {
    
    private static final long serialVersionUID = 1L;
    
    // 通知参数
    private List<Notification> notificationList;
    
    // 批示参数
    private Instruction instruction;
    private String instructionsInfo;
    private int instructionId;
    
    // 问题工单参数
    private Map<String, Object> cbForm;
    private List<?> commonList;
    
    /**
     * 查询项目通知
     * @return
     */
    public String queryProjectNotification() {
        try {
            notificationList = projectService.queryNotifyList(projectId);
        } catch (Exception e) {
            e.printStackTrace();
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return SUCCESS;
    }
    
    /**
     * 获取项目的工单记录
     * @return
     */
    public String problemTicket() {
        try {
            String itrBaseUrl = basicDataService.querySysArg("itr.problemTicket.base.url");
            cbForm = new HashMap<String, Object>();
            cbForm.put("itrBaseUrl", itrBaseUrl);
            project = projectService.queryProjectById(projectId);
            if (project != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                String projectCode = StringUtils.split(project.getProjectCode(), "-")[0];
                params.put("projectId", project.getProjectId());
                params.put("projectCode", projectCode);
                commonList = projectService.selectProblemTicket(params);
                // 增加按项目编号查询不到的情况下按合同号查询的处理逻辑
                if (commonList == null || commonList.isEmpty()) {
                    params.remove("projectCode");
                    params.put("contractNoList", Arrays.asList(StringUtils.split(StringUtils.defaultIfBlank(project.getContractNo(), projectCode), ",")));
                    commonList = projectService.selectProblemTicket(params);
                }
            } else {
                commonList = Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return SUCCESS;
    }
    
    /**
     * 获取项目的License授权信息
     * @return
     */
    public String licenseInfo() {
        try {
            project = projectService.queryProjectById(projectId);
            if (project != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("projectId", project.getProjectId());
                String contractNo = StringUtils.defaultIfBlank(project.getContractNo(), "");
                List<String> contractNoList = new ArrayList<>();
                if (StringUtils.isNotBlank(contractNo)) {
                    contractNoList.addAll(Arrays.asList(StringUtils.split(contractNo, ",")));
                }
                String projectCode = StringUtils.split(project.getProjectCode(), "-")[0];
                contractNoList.add(projectCode);
                params.put("contractNoList", contractNoList);
                commonList = projectService.selectLicenseInfo(params);
            } else {
                commonList = Collections.emptyList();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return SUCCESS;
    }
    
    /**
     * 保存项目批示
     * @return
     */
    public String instruction() {
        try {
            projectService.saveInstruction(projectId, instructionsInfo, instructionId);
            result = 301;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    /**
     * 获取批示信息
     * @return
     */
    public String getInstructionsInfo() {
        try {
            List<Instruction> instructionList = projectService.queryInstructionList(projectId);
            if (instructionList != null && !instructionList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Instruction inst : instructionList) {
                    sb.append(inst.getInstructionsInfo());
                    sb.append("\n");
                }
                instructionsInfo = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }
    
    // Getter/Setter 方法
    public List<Notification> getNotificationList() {
        return notificationList;
    }
    
    public Instruction getInstruction() {
        return instruction;
    }
    
    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }
    
    public void setInstructionsInfo(String instructionsInfo) {
        this.instructionsInfo = instructionsInfo;
    }
    
    public int getInstructionId() {
        return instructionId;
    }
    
    public void setInstructionId(int instructionId) {
        this.instructionId = instructionId;
    }
    
    public Map<String, Object> getCbForm() {
        return cbForm;
    }
    
    public List<?> getCommonList() {
        return commonList;
    }
}
