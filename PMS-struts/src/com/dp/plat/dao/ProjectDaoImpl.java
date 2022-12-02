package com.dp.plat.dao;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.Contract;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Instruction;
import com.dp.plat.data.bean.Item;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.Product;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectPlanEvent;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.SoftChangeLog;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.data.bean.WeeklyFeedback;
import com.dp.plat.maintenance.entity.ProjectMaintenance;
import com.dp.plat.maintenance.vo.ProjectMaintenanceVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.Person;
import com.dp.plat.param.ProjectParam;
import com.dp.plat.param.RealProductLineBean;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.supervision.vo.ProjectSupervisionVO;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.Util;

public class ProjectDaoImpl extends BaseDao implements ProjectDao {

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> queryProjectList(Project project, DisplayParam displayParam) {
        try {
	    	// 判断是否有产品类型这个搜索条件
	        if (StringUtils.isNotBlank(project.getItemModel())) {
	            // getSqlMapClientTemplate().insert("create_temp_tb_contractNo_filter_itemModel",
	            // project);
	            getSqlMapClientTemplate().insert("create_temp_tb_projectId_filter_itemModel", project);
	        }
	        // 判断是否有维保状态、维保级别、WAF服务查询条件
	        if (project.getCheckWarranty()) {
	        	getSqlMapClientTemplate().insert("create_temp_table_project_contract_warrantyState", project);
	        }
	        
	        getSqlMapClientTemplate().update("create_tmp_tb_project", project);// 创建临时表
	
	        int totalcount = (Integer) getSqlMapClientTemplate().queryForObject("find_project_count", project);
	
	        if (!displayParam.getExport()) {
	            displayParam.setPagesize(50);
	            displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
	        } else {
	            displayParam.setPagesize(totalcount);
	            displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
	        }
	
	        displayParam.setTotalcount(totalcount);
	        ProjectParam param = new ProjectParam();
	        param.setDisplayParam(displayParam);
	        param.setProject(project);
	        List<Project> projects = getSqlMapClientTemplate().queryForList("find_project_list", param);
	        return projects;
        } finally {
        	getSqlMapClientTemplate().update("drop_tmp_tb_project");// 删除临时表
        	// getSqlMapClientTemplate().update("drop_temp_tb_contractNo_filter_itemModel");//删除临时表
        	getSqlMapClientTemplate().update("drop_temp_tb_projectId_filter_itemModel");// 删除临时表
        	getSqlMapClientTemplate().update("drop_temp_table_project_contract_warrantyState");// 删除临时表
		}
    }

    @Override
    public List<Project> queryTransferProjectList(Project project) {
        List<Project> projects = getSqlMapClientTemplate().queryForList("find_transfer_project_list", project);
        return projects;
    }

    @Override
    public Integer insertProject(Project project) throws Exception {
        project.setCreateBy(getCurrUsername());
        return (Integer) getSqlMapClientTemplate().insert("insert-project", project);
    }

    @Override
    public void updateProjectByProjectId(Project project) {
        getSqlMapClientTemplate().update("update-project-byprojectid", project);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Person> queryPersonList() {
        return getSqlMapClientTemplate().queryForList("query_person_list");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Instruction> queryInstructionList(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_instruction_list", projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Instruction> queryFeedbackList(int id) {
        return getSqlMapClientTemplate().queryForList("query_feedback_list", id);
    }

    @Override
    public void insertProjectGroup(Project project) {
        getSqlMapClientTemplate().insert("insert-projectgroup", project);
    }

    @Override
    public void insertProjectContract(Project project) {
        getSqlMapClientTemplate().insert("insert-projectcontract", project);
    }

    @Override
    public void insertProjectGroupRelationship(Project project) {
        getSqlMapClientTemplate().insert("insert-projectgrouprelationship", project);
    }

    @Override
    public String queryMaxProjectGroupCode() {
//        return (String) getSqlMapClientTemplate().queryForObject("query-maxproject-groupcode");
        return (String) getSqlMapClientTemplate().queryForObject("query-max-project-group-id");
    }

    @Override
    public Project queryProjectByContractNo(String contractNo) {
    	Project project = (Project) getSqlMapClientTemplate().queryForObject("query-project-bycontractno", contractNo);
		if (project != null) {
			project.setProjectType(StringUtils.defaultIfBlank(project.getProjectType(), MessageUtil.PROJECT_TYPE_AFTERSALES));
		}
		return project;
//        return (Project) getSqlMapClientTemplate().queryForObject("query-project-bycontractno", contractNo);
//    	return this.queryProjectByContractNoAndType(contractNo, MessageUtil.PROJECT_TYPE_AFTERSALES);
    }
    
    @Override
	public Project queryProjectByContractNoAndType(String contractNo, String projectType) {
    	Map<String, Object> params = new HashMap<String, Object>(2);
   	 	params.put("contractNo", contractNo);
   	 	params.put("projectType", projectType);
    	return (Project) getSqlMapClientTemplate().queryForObject("queryProjectByContractNoAndType", params);
	}

    @Override
    public void insertProjectMember(Project project) {
        project.setCreateBy(getCurrUsername());
        getSqlMapClientTemplate().insert("insert-projectmember", project);
    }

    @Override
    public void insertInstruction(Instruction instruction) {
        getSqlMapClientTemplate().insert("insert_pm_instruction", instruction);
    }

    @Override
    public Project queryProjectById(int projectId) {
        return (Project) getSqlMapClientTemplate().queryForObject("query_project_byId", projectId);
    }
    
    @Override
    public Project queryProjectSimplifyByProjectId(Integer projectId) {
        return (Project) getSqlMapClientTemplate().queryForObject("queryProjectSimplifyByProjectId", projectId);
    }

    @Override
    public void updateProjectMember(Project project) {
        getSqlMapClientTemplate().update("update-projectmember", project);
    }

    @Override
    public void insertProjectRelatedParty(Project project) {
        getSqlMapClientTemplate().insert("insert-projectrelatedparty", project);
    }

    @Override
    public void updateProjectRelatedParty(Project project) {
        getSqlMapClientTemplate().update("update-projectrelatedparty", project);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> queryProjectListByPower(Project project, DisplayParam displayParam) {
        UserContext context = UserContext.getUserContext();
        if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_ADMIN)) {
            // 搜索权限条件不变
        } else if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
            // 只能搜服务经理或项目经理是当前用户的
            project.setOfficeCodes(Util.appendChar(context.getUser().getAreapower(), "'"));

            // old*Code 传递页面查询框的数据，避免无法筛选已闭环和不予跟踪的项目经理和服务经理
            project.setOldServiceManagerCode(project.getServiceManagerCode());
            project.setOldProgramManagerCode(project.getProgramManagerCode());

            project.setServiceManagerCode(getCurrUsername());
        } else if (context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)) {
            // 只能搜项目经理是当前用户的
            project.setOfficeCodes(Util.appendChar(context.getUser().getAreapower(), "'"));

            // old*Code 传递页面查询框的数据，避免无法筛选已闭环和不予跟踪的项目经理和服务经理
            project.setOldServiceManagerCode(project.getServiceManagerCode());
            project.setOldProgramManagerCode(project.getProgramManagerCode());

            project.setProgramManagerCode(getCurrUsername());
        } else {
            return null;
        }
        
        // 项目经理或者服务经理搜索时，不限制以下特殊情况权限特殊处理
        Project tempProject = project;
        if (!(context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_ADMIN))) {
            // 序列号查询，不限制权限
            if (StringUtils.isNotBlank(project.getBarCode()) && (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))) {
                project = new Project();
                BeanUtils.copyProperties(tempProject, project, new String[] { "officeCodes", "oldServiceManagerCode", "oldProgramManagerCode", "programManagerCode", "serviceManagerCode" });
            }
            // 项目名搜索，只限制办事处，不限制是否指派
            if (StringUtils.isNotBlank(project.getProjectName()) && (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))) {
                project = new Project();
                BeanUtils.copyProperties(tempProject, project, new String[] { "oldServiceManagerCode", "oldProgramManagerCode", "programManagerCode", "serviceManagerCode" });
            }
        }
        // 判断是否有产品类型这个搜索条件
        if (StringUtils.isNotBlank(project.getItemModel())) {
            // getSqlMapClientTemplate().insert("create_temp_tb_contractNo_filter_itemModel",
            // project);
            getSqlMapClientTemplate().insert("create_temp_tb_projectId_filter_itemModel", project);
        }
        
        // 判断是否有维保状态、维保级别、WAF服务查询条件
        if (project.getCheckWarranty()) {
        	getSqlMapClientTemplate().insert("create_temp_table_project_contract_warrantyState", project);
        }
        
        getSqlMapClientTemplate().update("create_tmp_tb_project", project);// 创建临时表

        Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("query_project_bypower_count", project);

        if (!displayParam.getExport()) {
            displayParam.setPagesize(50);
        } else {
            displayParam.setPagesize(totalcount);
            displayParam.setCurrentpage(1);
        }
        displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
        displayParam.setTotalcount(totalcount);
        ProjectParam param = new ProjectParam();
        param.setDisplayParam(displayParam);
        param.setProject(project);
        List<Project> projects = (List<Project>) getSqlMapClientTemplate().queryForList("query_project_bypower_list", param);
        getSqlMapClientTemplate().update("drop_tmp_tb_project");// 删除临时表
        // getSqlMapClientTemplate().update("drop_temp_tb_contractNo_filter_itemModel");//删除临时表
        getSqlMapClientTemplate().update("drop_temp_tb_projectId_filter_itemModel");// 删除临时表
        getSqlMapClientTemplate().update("drop_temp_table_project_contract_warrantyState");// 删除临时表
//        // 序列号查询去除权限查询后，还原原来的查询条件
//        if (StringUtils.isNotBlank(project.getBarCode()) && (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))) {
//            project = tempProject;
//        }
        project = tempProject;
        return projects;
    }

    @Override
    public int insertProjectWeekly(ProjectWeekly projectWeekly) {
        return (Integer) getSqlMapClientTemplate().insert("insert_project_weekly", projectWeekly);
    }

    @Override
    public void batchInsertWeeklyContent(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_weekly_content", paramMap);
    }

    @Override
    public void updateProjectStateByProjectId(Project project) {
        project.setUpdateBy(getCurrUsername());
        getSqlMapClientTemplate().update("update-projectstate-byprojectid", project);
    }

    @Override
    public String queryProjectStateByProjectId(Project project) {
        return (String) getSqlMapClientTemplate().queryForObject("query-projectstate-byprojectid", project);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderDataFromSap> queryOrderLineFromSapByContractNo(Project project) {
        return getSqlMapClientTemplate().queryForList("query-orderline-fromsap-bycontractno", project);
    }

    @Override
    public void insertProjectProductLine(OrderDataFromSap od) {
        getSqlMapClientTemplate().insert("insert-projectproductline", od);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderDataFromSap> queryOrderDataListByProjectId(int projectId) {
        return getSqlMapClientTemplate().queryForList("query-orderdatalist-byprojectid", projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectWeekly> queryProjectWeeklyList(int projectId, int weeklyState) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("weeklyState", weeklyState);
        return getSqlMapClientTemplate().queryForList("query_project_weekly", paramMap);
    }

    @Override
    public ProjectWeekly queryPorjectWeekly(int weeklyId) {
        return (ProjectWeekly) getSqlMapClientTemplate().queryForObject("query_project_weekly_one", weeklyId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WeeklyContent> queryWeeklyContentList(Map<String, Object> paramMap) {
        return getSqlMapClientTemplate().queryForList("query_weekly_contents", paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        if (StringUtils.isBlank(contractNo)) {
//            contractNo = "EMPTY";
//        }
//        map.put("contractNo", contractNo);
//        map.put("projectId", projectId);
//        return getSqlMapClientTemplate().queryForList("query-shipmentinfo-bycontractno", map);
        return queryShipmentInfoByContractNo(contractNo, projectId, null, null);
    }
    
    @Override
    public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId, String profitCenter) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        if (StringUtils.isBlank(contractNo)) {
//            contractNo = "EMPTY";
//        }
//        map.put("contractNo", contractNo);
//        map.put("projectId", projectId);
//        map.put("profitCenter", profitCenter);
//        map.put("sourceContractNo", contractNo.replaceAll("(-C)|(-L)", ""));
//        return getSqlMapClientTemplate().queryForList("query-shipmentinfo-bycontractno", map);
        if (StringUtils.isNotBlank(contractNo) && StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
        }
        return queryShipmentInfoByContractNo(contractNo, projectId, profitCenter, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId, boolean excludeTransferOut) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        if (StringUtils.isBlank(contractNo)) {
//            contractNo = "EMPTY";
//        }
//        map.put("contractNo", contractNo);
//        map.put("projectId", projectId);
//        map.put("excludeTransferOut", excludeTransferOut);
//        return getSqlMapClientTemplate().queryForList("query-shipmentinfo-bycontractno", map);
        return queryShipmentInfoByContractNo(contractNo, projectId, null, excludeTransferOut);
    }
    
    @SuppressWarnings("unchecked")
    public List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId, String profitCenter, Boolean excludeTransferOut) { Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(contractNo)) {
            contractNo = "EMPTY";
        }
        map.put("contractNo", contractNo);
        map.put("sourceContractNo", contractNo.replaceAll("(-L)|(-C)", ""));
        map.put("projectId", projectId);
        map.put("profitCenter", profitCenter);
        map.put("excludeTransferOut", excludeTransferOut);
        return getSqlMapClientTemplate().queryForList("query-shipmentinfo-bycontractno", map);
    }

    @Override
    public int queryShipmentInfoSizeByContractNo(String contractNos) {
        if (StringUtils.isBlank(contractNos)) {
            return 0;
        }
        return (int) getSqlMapClientTemplate().queryForObject("query_shipmentInfo_size_by_contractNo", contractNos);
    }
    
    @Override
    public int queryShipmentInfoSizeByContractNo(String contractNos, String profitCenter) {
        if (StringUtils.isBlank(contractNos)) {
            return 0;
        }
        if (StringUtils.isNotBlank(profitCenter)) {
            contractNos = contractNos.replaceAll("-L", "");
        }
        Map<String, Object> params = new HashMap<>(2);
        params.put("contractNo", contractNos);
        params.put("sourceContractNo", contractNos.replaceAll("(-L)|(-C)", ""));
        params.put("profitCenter", profitCenter);
        return (int) getSqlMapClientTemplate().queryForObject("query_shipmentInfo_size_by_contractNoAndProfitCenter", params);
    }

    @Override
    public List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        String contractNo = project.getContractNo();
//        if (StringUtils.isBlank(contractNo)) {
//            contractNo = "EMPTY";
//        }
//        map.put("contractNo", Util.appendChar(contractNo, "'"));
//        map.put("projectId", project.getProjectId());
//        String[] contractNos = contractNo.split(",");
//        for (int i = 0; i < contractNos.length; i++) {
//            contractNos[i] += "-C";
//        }
//        map.put("chContractNo", "'" + StringUtils.join(contractNos, "','") + "'");
//        map.put("chProjectId", transferProjectId);
//        return getSqlMapClientTemplate().queryForList("query-transferShipmentInfo-bycontractno", map);
        return this.queryTransferShipmentInfoByContractNo(project, transferProjectId, null);
    }
    
    @Override
    public List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId, String profitCenter) {
        Map<String, Object> map = new HashMap<String, Object>();
        String contractNo = project.getContractNo();
        if (StringUtils.isBlank(contractNo)) {
            contractNo = "EMPTY";
        }
        if (StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
            map.put("profitCenter", profitCenter);
        }
        map.put("contractNo", Util.appendChar(contractNo, "'"));
        map.put("sourceContractNo", Util.appendChar(contractNo, "'").replaceAll("(-L)|(-C)", ""));
        map.put("projectId", project.getProjectId());
        String[] contractNos = contractNo.split(",");
        for (int i = 0; i < contractNos.length; i++) {
            contractNos[i] += "-C";
        }
        map.put("chContractNo", "'" + StringUtils.join(contractNos, "','") + "'");
        map.put("chProjectId", transferProjectId);
        return getSqlMapClientTemplate().queryForList("query-transferShipmentInfo-bycontractno", map);
    }

    @Override
    public void insertProjectTransferShipment(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_project_transfer_shipment", paramMap);
    }

    @Override
    public void updateProjectTransferShipment(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().update("update_project_transfer_shipment", paramMap);
    }

    @Override
    public void insertTransferContract(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_transfer_contract", paramMap);
    }

    @Override
    public void deleteShipmentInstallInfoByProjectId(int projectId) {
        getSqlMapClientTemplate().insert("deleteShipmentInstallInfoByProjectId", projectId);
    }

    @Override
    public void updateProjectWeekly(ProjectWeekly projectWeekly) {
        getSqlMapClientTemplate().update("update_project_weekly", projectWeekly);
    }

    @Override
    public void deleteWeeklyContent(int weeklyId) {
        getSqlMapClientTemplate().update("delete_weekly_content", weeklyId);
    }

    @Override
    public void deleteFileById(int downFlileId) {
        getSqlMapClientTemplate().update("delete_upload_file", downFlileId);
    }

    @Override
    public void backToLastStep(int projectId, String projectState, String isback) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("projectState", projectState);
        paramMap.put("isback", isback);
        paramMap.put("updateBy", getCurrUsername());
        getSqlMapClientTemplate().update("update_project_state", paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectPlanEvent> queryProjectPlanEventByProject(Project project) {
        return getSqlMapClientTemplate().queryForList("query-projectplanevent-byproject", project);
    }

    @Override
    public int queryNeededUndelivedCount(ProjectDeliver projectDeliver) {
        return (Integer) getSqlMapClientTemplate().queryForObject("queryNeededUndelivedCount", projectDeliver);
    }
    
    @Override
    public List<ProjectDeliver> queryNeededUndelivedProjectDeliverList(ProjectDeliver projectDeliver) {
        return getSqlMapClientTemplate().queryForList("queryNeededUndelivedProjectDeliverList", projectDeliver);
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectMember> queryProjectMembers(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_pm_member_list", projectId);
    }

    @Override
    public int insertProjectMember(ProjectMember member) {
        return (Integer) getSqlMapClientTemplate().insert("insert_project_member", member);
    }

    @Override
    public void updateProjectMember(ProjectMember member) {
        getSqlMapClientTemplate().update("update_project_member", member);
    }

    @Override
    public void updateProjectPlanByProjectId(ProjectTask projectTask) {
        getSqlMapClientTemplate().update("update-projectplan-byprojectid", projectTask);
    }

    @Override
    public void insertProjectPlan(ProjectTask projectTask) {
        getSqlMapClientTemplate().insert("insert-projectplan", projectTask);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectTask> queryProjectTaskByProjectId(int projectId) {
        return getSqlMapClientTemplate().queryForList("query-projecttask-buprojectid", projectId);
    }

    @Override
    public void updateProjectShipment(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().update("update_project_shipment", paramMap);
    }

    @Override
    public void insertProjectShipment(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_project_shipment", paramMap);
    }

    @Override
    public int queryProjectShipment(Map<String, Object> paramMap) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_project_shipment", paramMap);
        if (obj != null) {
            return (Integer) obj;
        } else {
            return 0;
        }
    }

    @Override
    public void insertWeeklyFeedback(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_weekly_feedback", paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<WeeklyFeedback> queryWeeklyFeedbackList(int weeklyId) {

        return getSqlMapClientTemplate().queryForList("query_weekly_feedback", weeklyId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver) {
        return getSqlMapClientTemplate().queryForList("query-projectdeliver-list", projectDeliver);
    }

    @Override
    public void batchInsertDeliverFiles(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert-deliverfiles", paramMap);
    }

    @Override
    public List<ProjectDeliver> queryDeliverDetailByProjectId(int projectId) {
        return this.queryDeliverDetailByProjectIdAndProjectType(projectId, MessageUtil.PROJECT_TYPE_AFTERSALES);
        // return
        // getSqlMapClientTemplate().queryForList("query-deliverdetail-byprojectid",
        // projectId);
    }

    /**
     * @param projectId
     * @param basicDataPrjPhase
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectDeliver> queryDeliverDetailByProjectIdAndProjectType(int projectId, String projectType) {
        ProjectDeliver projectDeliver = new ProjectDeliver();
        projectDeliver.setProjectId(projectId);
        projectDeliver.setProjectType(projectType);
        return getSqlMapClientTemplate().queryForList("queryDeliverDetailByProjectIdAndProjectType", projectDeliver);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectDeliver> queryDeliverDetailByProjectIdAndDeliverType(int projectId, String dataTypeCode) {
        ProjectDeliver projectDeliver = new ProjectDeliver();
        projectDeliver.setProjectId(projectId);
        projectDeliver.setDataTypeCode(dataTypeCode);
        return getSqlMapClientTemplate().queryForList("queryDeliverDetailByProjectIdAndDeliverType", projectDeliver);
    }

    @Override
    public void deleteDeliverById(int deliverid) {
        getSqlMapClientTemplate().update("delete-deliver-byid", deliverid);
    }

    @Override
    public void updateProjectDeliverById(ProjectDeliver projectDeliver) {
        getSqlMapClientTemplate().update("updateProjectDeliverById", projectDeliver);
    }

    @Override
    public void updateProjectIsbackByProjectId(int projectId, String isback, String backCause) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("projectId", projectId);
        paramMap.put("isback", isback);
        paramMap.put("backCause", backCause);
        paramMap.put("updateBy", getCurrUsername());
        getSqlMapClientTemplate().update("update-projectisback-byprojectid", paramMap);
    }

    @Override
    public int insertProjecthandleLog(Map<String, Object> paramMap) {
        return (Integer) getSqlMapClientTemplate().insert("insert_project_log", paramMap);
    }

    @Override
    public void updateProjecthandleLog(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().update("update_project_log", paramMap);
    }

    @Override
    public void updateProjectImplByProjectId(Project project) {
        project.setUpdateBy(getCurrUsername());
        getSqlMapClientTemplate().update("update-projectimpl-byprojectid", project);
    }

    @Override
    public Person queryPersonFromOaByCode(String code) {
        return (Person) getSqlMapClientTemplate().queryForObject("query-person-fromoa-bycode", code);
    }

    @Override
    public int queryLastWeeklyId(int projectId) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_last_weeklyid", projectId);
        if (obj == null) {
            return 0;
        } else {
            return (Integer) obj;
        }
    }

    @Override
    public NotificationTemplate queryNotificationTemplate(String notificationCodeWeeklySubmit) {
        return (NotificationTemplate) getSqlMapClientTemplate().queryForObject("query_notifation_template", notificationCodeWeeklySubmit);
    }

    @Override
    public Integer queryDeliverDetailCountByProjectDeliver(ProjectDeliver pd) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query-deliverdetailcount-byprojectdeliver", pd);
    }

    @Override
    public void updateEventActualFinishDateByTask(ProjectTask pt) {
        getSqlMapClientTemplate().update("update-eventactualfinishdate-bytask", pt);
    }

    @Override
    public ProjectDeliver queryProjectDeliverById(int id) {
        return (ProjectDeliver) getSqlMapClientTemplate().queryForObject("query-projectdeliver-byid", id);
    }

    @Override
    public int insertNotification(Notification notice) {
        return (Integer) getSqlMapClientTemplate().insert("insert_into_notification", notice);
    }

    @Override
    public void notificationSetObjectList(int notifyId, List<String> objs) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("notifyId", notifyId);
        paramMap.put("objs", objs);
        paramMap.put("createBy", UserContext.getUserContext().getUsername());
        getSqlMapClientTemplate().insert("insert_into_notification_obj", paramMap);
    }

    @Override
    public Integer queryProjectMemberCountByProject(Project project) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query-projectmembercount-byproject", project);
    }

    @Override
    public String queryMailByUsername(String username) {
        return (String) getSqlMapClientTemplate().queryForObject("query_mail_byusername", username);
    }

    @Override
    public String queryMailByRoleId(int roleId) {
        return (String) getSqlMapClientTemplate().queryForObject("query_mail_byroleid", roleId);
    }

    @Override
    public String queryUsernamesByroleId(int roleId) {
        return (String) getSqlMapClientTemplate().queryForObject("query_usernames_byroleId", roleId);
    }

    @Override
    public String queryProjectNameByProjectId(int projectId) {
        return (String) getSqlMapClientTemplate().queryForObject("query-projectname-byprojectid", projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Contract> queryContractList(Map<String, Object> paramMap) {
        return getSqlMapClientTemplate().queryForList("query_contract_info", paramMap);
    }

    @Override
    public void insertMergeContract(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_merge_contract", paramMap);
    }

    @Override
    public void insertMergeProduct(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_merge_product", paramMap);
    }

    @Override
    public int queryProjectGroupSize(String projectCode) {
    	projectCode = projectCode.split("-")[0];
//        projectCode = projectCode.substring(0, projectCode.length() - 1);
        return (Integer) getSqlMapClientTemplate().queryForObject("query_project_group_count", projectCode);
    }

    @Override
    public void insertProjectGroup(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_branch_group_relationship", paramMap);
    }

    @Override
    public int insertProjectInfo(Map<String, Object> paramMap) {
        return (Integer) getSqlMapClientTemplate().insert("insert_branch_project_info", paramMap);
    }

    @Override
    public void insertProjectMember(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_branch_project_member", paramMap);
    }

    @Override
    public void updateProjectProduct(Product p) {
        getSqlMapClientTemplate().update("update_project_product", p);
    }

    @Override
    public void batchInsertProduct(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("batch_insert_product", paramMap);
    }

    @Override
    public int queryProjectTaskSize(int projectId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query_project_task_size", projectId);
    }

    @Override
    public void insertMergeTask(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().insert("insert_merge_task", paramMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Department> querySystemList() {
        return getSqlMapClientTemplate().queryForList("query_system_list");
    }

    @Override
    public String queryMemberAddress(int projectId) {
        String address = (String) getSqlMapClientTemplate().queryForObject("query_member_address", projectId);
        StringBuilder sb = new StringBuilder();
        if (address != null && address.length() > 0) {
            for (String str : address.split(",")) {
                sb.append(str);
                sb.append(";");
            }
        }
        return sb.toString();
    }

    @Override
    public void updateServiceProject(Map<String, Object> paramMap) {
        paramMap.put("updateBy", getCurrUsername());
        getSqlMapClientTemplate().update("service_update_project", paramMap);
    }

    @Override
    public Integer queryProjectContractCountByContractNo(String contractNo) {
//        return (Integer) getSqlMapClientTemplate().queryForObject("query-projectcontractcount-bycontractno", contractNo);
    	return this.queryProjectContractCountByContractNoAndType(contractNo, MessageUtil.PROJECT_TYPE_AFTERSALES);
    }
    
    @Override
	public Integer queryProjectContractCountByContractNoAndType(String contractNo, String projectType) {
    	 Map<String, Object> params = new HashMap<String, Object>(2);
    	 params.put("contractNo", contractNo);
    	 params.put("projectType", projectType);
    	 return (Integer) getSqlMapClientTemplate().queryForObject("queryProjectContractCountByContractNoAndType", params);
    }

    @Override
    public void invalidProjectHeader(int projectId) {
        getSqlMapClientTemplate().update("invalid_project_header", projectId);
    }

    @Override
    public void invalidProjectNotification(int projectId) {
        getSqlMapClientTemplate().update("invalid_project_notification", projectId);
    }

    @Override
    public void invalidProjectGroupRelationship(int projectId) {
        getSqlMapClientTemplate().update("invalid_project_group_relationship", projectId);
    }

    @Override
    public int queryProjectShipmentSize(int projectId) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_project_shipment_size", projectId);
        if (obj == null) {
            return 0;
        }
        return (Integer) obj;
    }
    
    @Override
    public int queryHistoryProjectShipmentSize(int projectId) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_history_project_shipment_size", projectId);
        if (obj == null) {
            return 0;
        }
        return (Integer) obj;
    }

    @Override
    public int queryProjectState(int projectId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query_project_state", projectId);
    }

    @Override
    public void insertProjectState(Project project) {
        getSqlMapClientTemplate().insert("insert_project_state", project);
    }

    @Override
    public void updateProjectState(Project project) {
        getSqlMapClientTemplate().update("update_pm_project_state", project);
    }

    @Override
    public int queryProjectShipmentState(int projectId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query_shipment_state", projectId);
    }

    @Override
    public String queryPlanState(int projectId) {
        return (String) getSqlMapClientTemplate().queryForObject("query_project_plan_state", projectId);
    }

    @Override
    public String queryProjectCurrentPlan(int projectId) {
        return (String) getSqlMapClientTemplate().queryForObject("query_current_plan", projectId);
    }

    @Override
    public void updateProjectCloseTime(int closeObjId) {
        getSqlMapClientTemplate().update("update_project_close_time_bycloseId", closeObjId);
    }

    @Override
    public void updateProjectDirectCloseTime(int projectId) {
        getSqlMapClientTemplate().update("update_project_close_time_by_projectId", projectId);
    }

    @Override
    public void clearProjectDirectCloseTime(int projectId) {
        getSqlMapClientTemplate().update("clear_project_close_time_by_projectId", projectId);
    }

    @Override
    public void updateProjectLastRefreshTime(int projectId) {
        getSqlMapClientTemplate().update("update_project_last_refresh_time", projectId);
    }

    @Override
    public void updateProjectPlanStateToClose(int closeObjId) {
        getSqlMapClientTemplate().update("update_project_plan_state_by_closeId", closeObjId);
    }

    @Override
    public String queryDeliverName(int deliverId) {
        return (String) getSqlMapClientTemplate().queryForObject("query_deliver_name", deliverId);
    }

    @Override
    public int queryDeliverTypeId(int deliverid) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query_deliver_type_id", deliverid);
    }

    @Override
    public int queryProjectIdBycloseId(int closeObjId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("query_projectId_bycloseId", closeObjId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CallBack> queryCallBackList(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_call_back_list", projectId);
    }

    @Override
    public int queryCallBackingSize(int projectId) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_callbacking_size", projectId);
        return obj == null ? 0 : (int) obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CallBack> queryCallBackRunList(Map<String, Integer> params) {
        return getSqlMapClientTemplate().queryForList("query_call_back_run_list", params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrderDataFromSap> queryRmaOrderDataByContractNo(String contract) {
        return getSqlMapClientTemplate().queryForList("query_rma_order_data", contract);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> queryProjectListByOfficeAndMemberCode(Project project) {
        return getSqlMapClientTemplate().queryForList("query_project_list_by_officeAndMemberCode", project);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RealProductLineBean> queryRealOrderDataListByProjectId(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_real_orderdatalist_by_projectid", projectId);
    }

    @Override
    public int queryRealOrderDataSizeByProjectId(int projectId) {
        Object obj = getSqlMapClientTemplate().queryForObject("query_real_orderdatasize_by_projectid", projectId);
        return obj == null ? 0 : (int) obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> querySoftversionList(String contractNo, int projectId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contractNo", contractNo);
        map.put("projectId", projectId);
        return getSqlMapClientTemplate().queryForList("query_soft_version_list", map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> querySoftversionList(String contractNo, int projectId, String profitCenter) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(contractNo) && StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
        }
        map.put("contractNo", contractNo);
        map.put("profitCenter", profitCenter);
        map.put("projectId", projectId);
        return getSqlMapClientTemplate().queryForList("query_soft_version_list", map);
    }

    @Override
    public void updateInvalidSoftversion(int projectId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("updateBy", getCurrUsername());
        map.put("projectId", projectId);
        getSqlMapClientTemplate().update("update_invalid_soft_version", map);
    }

    @Override
    public void insertSoftVersionList(List<ShipmentInfo> softversionList, int logId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("softversionList", softversionList);
        map.put("createBy", getCurrUsername());
        map.put("logId", logId);
        getSqlMapClientTemplate().insert("insert_project_soft_version", map);
    }

    @Override
    public int querySoftVersionNum(int projectId) {
        Object object = getSqlMapClientTemplate().queryForObject("query_hist_soft_version", projectId);
        return object == null ? 0 : (int) object;
    }

    @Override
    public void updateInvalidSoftVersionLog(int projectId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("projectId", projectId);
        map.put("updateBy", getCurrUsername());
        getSqlMapClientTemplate().update("update_invalid_soft_log", map);
    }

    @Override
    public int insertSoftVersionLog(SoftChangeLog softChangeLog) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("softChangeLog", softChangeLog);
        map.put("createBy", getCurrUsername());
        return (int) getSqlMapClientTemplate().insert("insert_soft_version_log", map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SoftChangeLog> queryHistSoftChangeLog(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_hist_soft_version_change", projectId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> queryHistSoftVersionList(SoftChangeLog softChangeLog) {
        return getSqlMapClientTemplate().queryForList("query_hist_soft_version_list", softChangeLog);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ShipmentInfo> queryHistSoftVersionList(SoftChangeLog softChangeLog, String contractNo) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("softChangeLog", softChangeLog);
        map.put("contractNo", Util.appendChar(contractNo, "'"));
        return getSqlMapClientTemplate().queryForList("query_hist_soft_version_list_factory", map);
    }

    @Override
    public SoftChangeLog queryOneSoftChangeLog(int id) {
        return (SoftChangeLog) getSqlMapClientTemplate().queryForObject("query_one_soft_change_log", id);
    }

    @Override
    public int queryShipemntSizeByContractNo(String contractNos) {
        return (int) getSqlMapClientTemplate().queryForObject("query_shipment_size_by_contractNo", contractNos);
    }

    @Override
    public String queryMailByUserNameFromOA(String userName) {
        return (String) getSqlMapClientTemplate().queryForObject("query_mail_by_userName_from_oa", userName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectMember> queryValidMemberByProjectId(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_valid_member_by_projectId", projectId);
    }

    @Override
    public List<ProjectMember> queryValidMemberEmailByProjectIdAndRoles(int projectId, String memberRoles) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("memberRoles", memberRoles);
        return getSqlMapClientTemplate().queryForList("query_valid_member_email_by_projectId_roles", params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectTask> queryProjectPreAndFinalInspection() {
        return getSqlMapClientTemplate().queryForList("query_Project_PreAndFinalInspection");
    }

    @Override
    public int batchDeleteProject(String contractNos) {
        return getSqlMapClientTemplate().delete("delete_project_by_contractNo", contractNos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Project> queryExistsProjectByContractNos(String contractNos) {
        return getSqlMapClientTemplate().queryForList("query_existsProject_by_contractNo", contractNos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ProjectTask> queryProjectArrivalReceipt() {
        return getSqlMapClientTemplate().queryForList("query_Project_ArrivalReceipt");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, String>> querySpotCheckList(String contractNo, int projectId) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("contractNo", contractNo);
//        map.put("projectId", projectId);
//        // 设置group_contract的最大长度，默认1024
//        getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
//        return getSqlMapClientTemplate().queryForList("querySpotCheckList", map);
        return this.querySpotCheckList(contractNo, projectId, null);
    }
    
    @Override
    public List<Map<String, String>> querySpotCheckList(String contractNo, int projectId, String profitCenter) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(contractNo) && StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
        }
        map.put("contractNo", contractNo);
        map.put("sourceContractNo", StringUtils.trimToEmpty(contractNo).replaceAll("(-L)|(-C)", ""));
        map.put("projectId", projectId);
        map.put("profitCenter", profitCenter);
        // 设置group_contract的最大长度，默认1024
        getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
        return getSqlMapClientTemplate().queryForList("querySpotCheckList", map);
    }

    @Override
    public void batchInsertSpotCheckIgnoreItem(List<Item> itemList) {
        getSqlMapClientTemplate().insert("batchInsertSpotCheckIgnoreItem", itemList);
    }

    @Override
    public void truncateSpotCheckIgnoreItem() {
        getSqlMapClientTemplate().delete("truncateSpotCheckIgnoreItem");
    }
    
    @Override
    public List<Map<String, String>> queryOverWarrantyRemindList(String contractNo, int projectId) {
        return this.queryOverWarrantyRemindList(contractNo, projectId, null);
    }
    
    @Override
    public List<Map<String, String>> queryOverWarrantyRemindList(String contractNo, int projectId, String profitCenter) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(contractNo) && StringUtils.isNotBlank(profitCenter)) {
            contractNo = contractNo.replaceAll("-L", "");
        }
        map.put("contractNo", contractNo);
        map.put("sourceContractNo", StringUtils.trimToEmpty(contractNo).replaceAll("(-L)|(-C)", ""));
        map.put("projectId", projectId);
        map.put("profitCenter", profitCenter);
        // 设置group_contract的最大长度，默认1024
        getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
        return getSqlMapClientTemplate().queryForList("queryOverWarrantyRemindList", map);
    }

    @Override
    public List<Notification> queryNotifyList(int projectId) {
        return getSqlMapClientTemplate().queryForList("query_project_notify_list", projectId);
    }

    @Override
    public List<String> queryProjectInspectionOffice() {
        return getSqlMapClientTemplate().queryForList("queryProjectInspection");
    }

    @Override
    public List<Map<String, Object>> queryProjectInspection(Map<String, Object> params) {
        return getSqlMapClientTemplate().queryForList("queryProjectInspection", params);
    }

    @Override
    public List<ProjectMember> queryValidMemberByProjectIdsAndRoles(String projectIds, String memberRoles) {
        Map<String, String> params = new HashMap<>();
        params.put("projectIds", projectIds);
        params.put("memberRoles", memberRoles);
        return getSqlMapClientTemplate().queryForList("queryValidMemberByProjectIdsAndRoles", params);
    }

    @Override
    public List<Map<String, Object>> queryProjectInspectionCounts() {
        return getSqlMapClientTemplate().queryForList("queryProjectInspectionCounts");
    }

    @Override
    public ProjectMaintenanceVO selectProjectMaintenanceById(Integer id) {
        return (ProjectMaintenanceVO) getSqlMapClientTemplate().queryForObject("selectProjectMaintenanceById", id);
    }

    @Override
    public List<ProjectMaintenanceVO> selectProjectMaintenanceList(ProjectMaintenanceVO projectMaintenance) {
        return getSqlMapClientTemplate().queryForList("selectProjectMaintenanceList", projectMaintenance);
    }

    @Override
    public List<ProjectMaintenanceVO> selectProjectMaintenanceVOList(ProjectMaintenanceVO projectMaintenance) {
        getSqlMapClientTemplate().insert("createTempQuesnaireResultLineTable", "projectMaintenance");
        List<ProjectMaintenanceVO> list = getSqlMapClientTemplate().queryForList("selectProjectMaintenanceVOList", projectMaintenance);
        getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", "projectMaintenance");
        return list;
    }

    @Override
    public List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance) {
        String quesType = "projectMaintenance";
        try {
            Map<String, Object> questionColumns = this.queryQuestionColumns(quesType, null);
            projectMaintenance.setQuestionColumns(questionColumns);

            getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);

            List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectMaintenanceMapList", projectMaintenance);
            return list;
        } finally {
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", "projectMaintenance");
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", "projectMaintenance");
        }
    }
    
    @Override
    public List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam) {
        String quesType = "projectMaintenance";
        Boolean hideQuesnaire = true;
        Boolean hideWarranty = true;
        Boolean hideFiles = true;
        Boolean checkServicePower = false;
        String userPower = "";
        Integer projectId = projectMaintenance != null ? projectMaintenance.getProjectId() : null;
        try {
        	// 是否需要隐藏问卷
            if (projectMaintenance == null || !Boolean.TRUE.equals(projectMaintenance.getHideQuesnaire())) {
                Map<String, Object> questionColumns = this.queryQuestionColumns(quesType, null);
                projectMaintenance.setQuestionColumns(questionColumns);
                
                getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);
                
                hideQuesnaire = false;
            }
            
            // 设置group_contract的最大长度，默认1024
            getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
            // 是否需要隐藏交付件
            if (projectMaintenance == null || Boolean.FALSE.equals(projectMaintenance.getHideFiles())) {
            	hideFiles = false;
            }
            // 是否需要清除维保
            if (projectMaintenance == null || Boolean.FALSE.equals(projectMaintenance.getHideWarranty())) {
            	hideWarranty = false;
            }
            
            // 创建服务经理人员权限表，服务经理-办事处-服务经理/项目经理的关系
            if (projectMaintenance != null && Boolean.TRUE.equals(projectMaintenance.isCheckServicePower())) {
            	userPower = StringUtils.trimToEmpty(projectMaintenance.getUserPower());
                getSqlMapClientTemplate().insert("createTempServicePowerTable", userPower);
                
                checkServicePower = projectMaintenance.isCheckServicePower();
            }
            
            
            Map<String, Object> params = new HashMap<>();
            params.put("checkServicePower", checkServicePower);
            params.put("projectId", projectId);
            if(displayParam != null) {
            	hideWarranty = hideWarranty && !displayParam.getExport();
            	if (!hideWarranty) {
            		getSqlMapClientTemplate().insert("createTempProjectWarrantyStateTable", params);
//                    getSqlMapClientTemplate().insert("createTempMaintenanceContractNoTable");
//                    getSqlMapClientTemplate().insert("createTempWarrantyStateTable");
            	}
                Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectMaintenanceList", projectMaintenance);
                displayParam.setTotalcount(totalcount);
                if (!displayParam.getExport()) {
                    params.put("hideFiles", hideFiles);
                    params.put("hideQuesnaire", hideQuesnaire);
                    params.put("hideWarranty", hideWarranty);
                    displayParam.setPagesize(50);
                    displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
                } else {
                    displayParam.setPagesize(totalcount);
                    displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
                }
            } else {
                DisplayParam tempDisplayParam = new DisplayParam();
                try {
                    tempDisplayParam.getParam();
                    hideWarranty = hideWarranty && !tempDisplayParam.getExport();
                    if (!hideWarranty) {
                    	getSqlMapClientTemplate().insert("createTempProjectWarrantyStateTable", params);
//                        getSqlMapClientTemplate().insert("createTempMaintenanceContractNoTable");
//                        getSqlMapClientTemplate().insert("createTempWarrantyStateTable");
                    }
                } catch (UnsupportedEncodingException e) {
                }
                params.put("hideWarranty", hideWarranty);
            }
            params.put("projectMaintenance", projectMaintenance);
            params.put("displayParam", displayParam);
            List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectMaintenanceMapList", params);
            return list;
        } finally {
            if (!Boolean.TRUE.equals(hideQuesnaire)) {
                getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", quesType);
                getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", quesType);
            }
            if (!Boolean.TRUE.equals(hideWarranty)) {
            	getSqlMapClientTemplate().delete("deleteTempProjectWarrantyStateTable");
//                getSqlMapClientTemplate().delete("deleteTempMaintenanceContractNoTable");
//                getSqlMapClientTemplate().delete("deleteTempWarrantyStateTable");
            }
            
            if (Boolean.TRUE.equals(checkServicePower)) {
                getSqlMapClientTemplate().delete("deleteTempServicePowerTable", userPower);
            }
            
        }
    }
    
    @Override
	public Map<String, Object> queryProjectWarrantyState(Integer projectId) {
		return (Map<String, Object>) getSqlMapClientTemplate().queryForObject("queryProjectWarrantyState", projectId);
	}
    
	@Override
	public Map<String, Long> queryProjectMaintenanceDeliverCount(Map<String, Object> params) {
//		return (Map<String, Long>) getSqlMapClientTemplate().queryForObject("queryProjectMaintenanceDeliverCount", params);
		return (Map<String, Long>) getSqlMapClientTemplate().queryForObject("queryProjectMaintenanceServiceDeliveriedCount", params);
	}
	
	@Override
	public int queryProjectMaintenanceDeliverCountByProjectDeliver(ProjectDeliver projectDeliver) {
		return (int) getSqlMapClientTemplate().queryForObject("queryProjectMaintenanceDeliverCount", projectDeliver);
	}
	
	@Override
	public Boolean queryProjectMaintenanceServiceDeliveriedByProjectDeliver(ProjectDeliver projectDeliver) {
		return Boolean.TRUE.equals(getSqlMapClientTemplate().queryForObject("queryProjectMaintenanceServiceDeliveriedByProjectDeliver", projectDeliver));
	}
	
	@Override
	public Boolean queryProjectMaintenanceServiceDeliveriedByMap(Map<String, Object> map) {
		return Boolean.TRUE.equals(getSqlMapClientTemplate().queryForObject("queryProjectMaintenanceServiceDeliveriedByMap", map));
	}

	@Override
	public Integer insertProjectServiceDeliveryBySelective(Map<String, Object> serviceDelivery) {
		return (Integer) getSqlMapClientTemplate().insert("insertProjectServiceDeliveryBySelective", serviceDelivery);
	}

	public List<Map<String, Object>> selectProjectMaintenanceServiceDeliveryMapList(Map<String, Object> params) {
		try {
			// 设置group_contract的最大长度，默认1024
			getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
			getSqlMapClientTemplate().insert("createTempProjectWarrantyStateTable");

			DisplayParam displayParam = (DisplayParam) params.get("displayParam");
			if (displayParam != null) {
				Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectMaintenanceServiceDeliveryMapList", params);
	        	displayParam.setTotalcount(totalcount);
	        	if (!displayParam.getExport()) {
	        		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
	        	} else {
	        		displayParam.setPagesize(totalcount);
	        		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
	        	}
			}
			return getSqlMapClientTemplate().queryForList("selectProjectMaintenanceServiceDeliveryMapList", params);
		} finally {
			Object skipDropTempTable = params.get("skipDropTempTable");
            if (skipDropTempTable == null || Boolean.FALSE.equals(skipDropTempTable)) {
                getSqlMapClientTemplate().delete("deleteTempProjectWarrantyStateTable");
            }
		}
	}
	
	@Override
	public List<Map<String, Object>> selectProjectMaintenanceServiceDeliveryList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (projectMaintenance != null) {
			JSONObject json = (JSONObject) JSON.toJSON(projectMaintenance);
			params.putAll(json);
		}
//		params.put("serviceTypes", projectMaintenance.getServiceTypes());
//		params.put("serviceType", projectMaintenance.getServiceType());
//		params.put("serviceDate", projectMaintenance.getServiceDate());
//		params.put("serviceQuarter", projectMaintenance.getServiceQuarter());
        params.put("displayParam", displayParam);
        params.put("joinProjectMembers", true);
//        try {
//        	// 设置group_contract的最大长度，默认1024
//        	getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
//        	getSqlMapClientTemplate().insert("createTempProjectWarrantyStateTable");
//        	Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectMaintenanceServiceDeliveryMapList", params);
//        	displayParam.setTotalcount(totalcount);
//        	if (!displayParam.getExport()) {
//        		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
//        	} else {
//        		displayParam.setPagesize(totalcount);
//        		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
//        	}
        	return this.selectProjectMaintenanceServiceDeliveryMapList(params);
//        } finally {
//        	Object skipDropTempTable = params.get("skipDropTempTable");
//            if (skipDropTempTable == null || Boolean.FALSE.equals(skipDropTempTable)) {
//                getSqlMapClientTemplate().delete("deleteTempProjectWarrantyStateTable");
//            }
//		}
	}

	@Override
    public List<String> selectDailyMaintenanceUsers(Map<String, Object> params) {
        return getSqlMapClientTemplate().queryForList("selectDailyMaintenanceUsers", params);
    }

    @Override
    public List<Map<String, Object>> selectDailyMaintenanceMapList(Map<String, Object> params) {
        try {
            getSqlMapClientTemplate().insert("createTempMaintenanceContractNoTable");
            getSqlMapClientTemplate().insert("createTempWarrantyStateTable");
            return getSqlMapClientTemplate().queryForList("selectDailyMaintenanceMapList", params);
        } finally {
            Object skipDropTempTable = params.get("skipDropTempTable");
            if (skipDropTempTable == null || Boolean.FALSE.equals(skipDropTempTable)) {
                getSqlMapClientTemplate().delete("deleteTempMaintenanceContractNoTable");
                getSqlMapClientTemplate().delete("deleteTempWarrantyStateTable");
            }
        }
    }

    @Override
    public Integer insertOrUpdateProjectMaintenance(ProjectMaintenance projectMaintenance) {
        if (projectMaintenance == null) {
            return null;
        }
//        projectMaintenance.setCreateBy(getCurrUsername());
//        projectMaintenance.setCreateTime(new Date());
        if (projectMaintenance.getId() != null) {
            projectMaintenance.setUpdateBy(getCurrUsername());
            projectMaintenance.setUpdateTime(new Date());
        } else {
            projectMaintenance.setCreateBy(getCurrUsername());
            projectMaintenance.setCreateTime(new Date());
        }
        return (Integer) getSqlMapClientTemplate().insert("insertOrUpdateProjectMaintenance", projectMaintenance);
    }
    
    @Override
    public Integer selectSingleProjectMaintenanceMaxId(ProjectMaintenance projectMaintenance) {
        Integer maxId = (Integer) getSqlMapClientTemplate().queryForObject("selectSingleProjectMaintenanceMaxId", projectMaintenance);
        return maxId != null ? maxId : Integer.valueOf(0);
    }

    @Override
    public ProjectSupervisionVO selectProjectSupervisionById(Integer id) {
        return (ProjectSupervisionVO) getSqlMapClientTemplate().queryForObject("selectProjectSupervisionById", id);
    }

    @Override
    public List<ProjectSupervisionVO> selectProjectSupervisionList(ProjectSupervisionVO projectSupervision) {
        return getSqlMapClientTemplate().queryForList("selectProjectSupervisionList", projectSupervision);
    }

    @Override
    public List<ProjectSupervisionVO> selectProjectSupervisionVOList(ProjectSupervisionVO projectSupervision) {
        getSqlMapClientTemplate().insert("createTempQuesnaireResultLineTable", "projectSupervision");
        List<ProjectSupervisionVO> list = getSqlMapClientTemplate().queryForList("selectProjectSupervisionVOList", projectSupervision);
        getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", "projectSupervision");
        return list;
    }

    @Override
    public List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision) {
        return this.selectProjectSupervisionMapList(projectSupervision, null);
//        try {
//            getSqlMapClientTemplate().insert("createTempQuesnaireResultLineTable", "projectSupervision");
//            Map<String, Object> questionColumns = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("ProjectMaintenanceQuesnaireResultColumns", "projectSupervision");
//
//            if (questionColumns != null) {
//                String titles = StringUtils.trimToEmpty((String) questionColumns.get("titles"));
//                if (StringUtils.isNotBlank(titles)) {
//                    String[] tagArr = StringUtils.split(titles, ",");
//                    String[] thArr = new String[tagArr.length];
//                    for (String tag : tagArr) {
//                        String[] kv = StringUtils.split(tag, "=");
//                        int idex = Integer.valueOf(StringUtils.replace(kv[0], "questionResult", ""));
//                        thArr[idex - 1] = kv[1];
//                    }
//                    questionColumns.put("tableQuestionHeader", StringUtils.join(thArr, "</th><th>"));
//                }
//            } else {
//                questionColumns = new HashMap<String, Object>();
//            }
//            questionColumns.put("quesType", "projectSupervision");
//            projectSupervision.setQuestionColumns(questionColumns);
//
//            getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);
//
//            List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectSupervisionMapList", projectSupervision);
//            return list;
//        } finally {
//            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", "projectSupervision");
//            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", "projectSupervision");
//        }
    }
    
    @Override
    public List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision, DisplayParam displayParam) {
        String quesType = "projectSupervision";
        try {
            String resultType = "1";
            if (displayParam != null && displayParam.getExport())  {
                resultType = null;
            } else if (displayParam != null && displayParam.getPagesize() == -1) {
                displayParam = null;
            }
            Map<String, Object> questionColumns = queryQuestionColumns(quesType, resultType);
            projectSupervision.setQuestionColumns(questionColumns);

            getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);

            Map<Object, Object> params = new HashMap<>();
            if(displayParam != null) {
                Integer totalcount = (Integer) getSqlMapClientTemplate().queryForObject("countProjectSupervisionList", projectSupervision);
                displayParam.setTotalcount(totalcount);
                if (!displayParam.getExport()) {
                    params.put("hideFiles", "true");
                    displayParam.setPagesize(50);
                    displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
                } else {
                    displayParam.setPagesize(totalcount);
                    displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
                }
            }
            params.put("projectSupervision", projectSupervision);
            params.put("displayParam", displayParam);
            List<Map<String, Object>> list = getSqlMapClientTemplate().queryForList("selectProjectSupervisionMapList", params);
            return list;
        } finally {
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", quesType);
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", quesType);
        }
    }

    @Override
    public Integer insertOrUpdateProjectSupervision(ProjectSupervision projectSupervision) {
        if (projectSupervision == null) {
            return null;
        }
        projectSupervision.setCreateBy(getCurrUsername());
        projectSupervision.setCreateTime(new Date());
        if (projectSupervision.getId() != null) {
            projectSupervision.setUpdateBy(getCurrUsername());
            projectSupervision.setUpdateTime(new Date());
        }
        return (Integer) getSqlMapClientTemplate().insert("insertOrUpdateProjectSupervision", projectSupervision);
    }
    
    @Override
    public Map<String, Object> queryQuestionColumns (String quesType, String resultType) {
    	// 设置group_contract的最大长度，默认1024
		getSqlMapClientTemplate().insert("setMaxGroupContractLength", 1024000);
        getSqlMapClientTemplate().insert("createTempQuesnaireResultLineTable", quesType);
        HashMap<String, Object> params = new  HashMap<>();
        params.put("quesType", quesType);
        params.put("resultType", resultType); //默认值查询单选
        if (StringUtils.isBlank(resultType)) {
            params.remove("resultType");
        }
        Map<String, Object> questionColumns = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("queryQuesnaireResultColumns", params);

        if (questionColumns != null) {
            String titles = StringUtils.trimToEmpty((String) questionColumns.get("titles"));
            if (StringUtils.isNotBlank(titles)) {
                String[] tagArr = StringUtils.split(titles, ",");
                String[] thArr = new String[tagArr.length];
                for (String tag : tagArr) {
                    String[] kv = StringUtils.split(tag, "=");
                    int idex = Integer.valueOf(StringUtils.replace(kv[0], "questionResult", ""));
                    thArr[idex - 1] = kv[1];
                }
                questionColumns.put("tableQuestionHeader", StringUtils.join(thArr, "</th><th>"));
            }
        } else {
            questionColumns = new HashMap<String, Object>();
        }
        questionColumns.put("quesType", quesType);
        return questionColumns;
    }

    @Override
    public List<Project> querySoleAgentProject() {
        return getSqlMapClientTemplate().queryForList("querySoleAgentProject");
    }

    @Override
    public List<Map<String, Object>> queryProjectOldOrderContractInfo(Integer projectId) {
        return getSqlMapClientTemplate().queryForList("queryProjectOldOrderContractInfo", projectId);
    }

    @Override
    public List<Map<String, Object>> queryProjectNewOrderContractInfo(Integer projectId) {
        return getSqlMapClientTemplate().queryForList("queryProjectNewOrderContractInfo", projectId);
    }

    @Override
    public void invalidSoleAgentProjectContract(Map<String, Object> paramMap) {
        getSqlMapClientTemplate().update("invalidSoleAgentProjectContract", paramMap);
    }

    @Override
    public void deleteProjectUnlinkedContractProductLine(Integer projectId) {
        getSqlMapClientTemplate().delete("deleteProjectUnlinkedContractProductLine", projectId);
    }

    @Override
    public void updateProjectSalesType(Integer projectId, String salesType) {
        Map<String, Object> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("salesType", salesType);
        getSqlMapClientTemplate().update("updateProjectSalesType", params);
    }
    
    @Override
    public void updateProjectSalesType(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return;
        }
        if (!params.containsKey("defaultSalesType")) {
            params.put("defaultSalesType", "01");
        }
        getSqlMapClientTemplate().update("updateProjectSalesType", params);
    }

	@Override
	public List<Map<String, Object>> queryMarketRelations() {
		return getSqlMapClientTemplate().queryForList("queryMarketRelations");
	}
	
    @Override
    public List<Map<String, Object>> selectContractAcceptanceDeliveryInfo(Map<String, Object> params) {
        return getSqlMapClientTemplate().queryForList("selectContractAcceptanceDeliveryInfo", params);
    }
    
}
