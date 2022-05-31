package com.dp.plat.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dp.plat.context.HttpContext;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.CallBack;
import com.dp.plat.data.bean.Company;
import com.dp.plat.data.bean.Contract;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Instruction;
import com.dp.plat.data.bean.Item;
import com.dp.plat.data.bean.MailContent;
import com.dp.plat.data.bean.MailSenderInfo;
import com.dp.plat.data.bean.Notification;
import com.dp.plat.data.bean.NotificationTemplate;
import com.dp.plat.data.bean.OrderDataFromSap;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.Product;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ProjectMember;
import com.dp.plat.data.bean.ProjectPlan;
import com.dp.plat.data.bean.ProjectPlanEvent;
import com.dp.plat.data.bean.ProjectTask;
import com.dp.plat.data.bean.ProjectWeekly;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.SoftChangeLog;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WeeklyContent;
import com.dp.plat.data.bean.WeeklyFeedback;
import com.dp.plat.maintenance.vo.ProjectMaintenanceVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.Person;
import com.dp.plat.param.ProjectBatchCgMbParam;
import com.dp.plat.param.RealProductLineBean;
import com.dp.plat.prob.util.ExportUtils;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.ProjectPlanService;
import com.dp.plat.service.ProjectService;
import com.dp.plat.service.SendMailService;
import com.dp.plat.service.UserManageService;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MailHandleUtil;
import com.dp.plat.util.Md5Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.NotificationTemplateUtil;
import com.dp.plat.util.ProjectUtils;
import com.dp.plat.util.QuestionnarieUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.Util;
import com.dp.plat.util.parser.ExcelParser;
import com.opensymphony.xwork2.Preparable;

public class ProjectAction extends BaseAction implements Preparable{
	private static final long serialVersionUID = 1L;
	
	//全局变量
	private int roleid;
	private int modifyflag;
	private User user;
	
	private ProjectService projectService;
	private DepartmentManageService departmentManageService;
	private UserManageService userManageService;
	private BasicDataService basicDataService;
	private ProjectPlanService projectPlanService;
	private SendMailService sendMailService;
	private DisplayParam displayParam;
	private Project project;
	private ProjectTask projectTask;
	private ProjectDeliver projectDeliver;
	
	private String eventKeyStr;
	private String eventValueStr;
	private String eventDoingStr;//正在发生的事件
	
	private List<Project> projectlist;
	private List<Department> departmentList;
	private List<Company> companyList;
	private List<BasicDataBean> projectTypeList;
	private List<BasicDataBean> projectRankList;//项目类型集合
	private List<BasicDataBean> majorProjectLevelList;//重大项目级别
	private Map<String, String> colMap;
	private List<User> allusernameList;
	private List<Person> personList;
	private List<Instruction> instructionList;
	private List<ProjectPlan> projectPlanList;
	private List<OrderDataFromSap> orderDataList;
	private List<RealProductLineBean> realOrderDataList;
	private int realOrderDataSize;
	private List<ShipmentInfo> shipmentInfoList;//序列号列表
	private List<ShipmentInfo> softversionList;//设备发货软件版本
	private List<ProjectPlanEvent> projectPlanEventList;//事件节点
	private List<ProjectMember> projectMemberList;//项目成员集合
	private ProjectMember member;
	private List<BasicDataBean> memberRoleList;//项目成员角色
	private List<BasicDataBean> navTabList;//项目维护页面选项卡集合
	private List<BasicDataBean> deliverStateList;//项目发货状态集合
	private List<BasicDataBean> projectPlanStateList;//工程计划状态
    private List<BasicDataBean> projectExecutionStateList;//工程实施状态
    private List<BasicDataBean> projectCloseProcessStateList;//工程闭环流程状态
	private List<BasicDataBean> ssfsList;//实施方式集合
	private List<BasicDataBean> projectTimeList;//项目查询条件--时间点集合
	private List<CallBack> callBackList;//回访流程集合
	//项目成员
	private int memberId;
	private Date memberEffectiveFrom;
	private Date memberEffectiveTo;
	private String memberCode;
	private String memberName;
	private String memberRole;
	private String memberRoleName;
	private String phoneNum;
	private String email;
	
	private int deliverid;
	
	private List<ProjectTask> projectTaskList;//项目计划列表
	private List<ProjectDeliver> projectDeliverList;//交付件下拉列表
	private List<ProjectDeliver> deliverDetailList;//交付件列表
	//项目批示
	private Instruction instruction;
	private String instructionsInfo;
	private int instructionId;
	private int projectId;
	private String contractNo;
	private String isback;
	private String backCause;
	private String notbackCause;
	private int result;//ajax返回结果
	private String message;
	//项目周报
	private List<ProjectWeekly> weeklyList;
	private ProjectWeekly projectWeekly;
	private List<WeeklyContent> workcontentList;
	private List<WeeklyContent> riskcontentList;
	private List<WeeklyContent> helpcontentList;
	private List<WeeklyContent> progresscontentList;
	private List<WeeklyContent> plancontentList;
	private List<WeeklyContent> filecontentList;
	private List<WeeklyContent> mailcontentList;
	private int weeklyId;
	private String feedback;
	private List<WeeklyFeedback> feedbackList;
	// 上传附件
	private File[] upload;
	private String uploadFileName;
	
	private File[] uploaddelivery1;
	private File[] uploaddelivery2;
	private File[] uploaddelivery3;
	private File[] uploaddelivery4;
	private String uploaddelivery1FileName;
	private String uploaddelivery2FileName;
	private String uploaddelivery3FileName;
	private String uploaddelivery4FileName;
	
	private String redirect;
	private String downpath;
	private String downname;
	private int downFlileId;
	//项目状态 项目状态更新
	private String projectState;
	private String column012;//实施方式
	private String channelName;//代理商自服，代理商名称
	private String column013;//最终客户
	private String notGrantTailCause;//不予跟踪理由
	private int isupdate;//是否做更新操作
	private String pm;
	//项目安装地址
	private String selected;
	private String installAddress;
	private int workSpaceReturnType;	//表示工作台跳转，100表示跳转到项目闭环
	//合同合并
	private String mergeContractNo;
	private List<Contract> contractList;
	private List<Product> productList;
	private String paramId;
	private String mergeBranchMark;
	//项目批量处理
	private ServletContext context;
	private int batchFunc;
	//判断是否可以发起闭环申请
	private int isToCloseProject;
	//判断是否发起回访流程
	private int isCallBack;
	//是否处于正在回访流程中
	private int isCallBacking;
	//软件版本记录
	private SoftChangeLog softChangeLog;
	private List<SoftChangeLog> changeLogList;
	
	private ProjectBatchCgMbParam batchCgMb;
	private String batchChangeResult;
	
	/**
	 * 转移之后的项目project
	 */
	private Project transferProject;
	private List<String> contractNoList;
	private int transferType;
	private String projectCode;
	
	// 项目系统通知
	private List<Notification> notificationList;
	
	// 项目维护记录
	private ProjectMaintenanceVO projectMaintenance;
	private List<ProjectMaintenanceVO> maintenanceList;
	private List<Map<String, Object>> maintenanceMapList;

    private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;

    private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;

    private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;

    private Map<String, Object> cbForm;

    private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;

    private List<BasicDataBean> maintenanceTypeList;

	public void prepareExecute(){
		//办事处集合
		departmentList = departmentManageService.queryDepartments();
		
		// 公司集合
		Company company = new Company();
		company.setStatus(1);
		companyList = departmentManageService.queryCompanyList(company);
		
		//项目分类
		projectTypeList = basicDataService.queryBasicDataBeans("02");
		//项目计划阶段
		
		
		//发货状态集合
		deliverStateList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_DELIVERSTATE);
		//工程计划状态
		projectPlanStateList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_ENGINEERSTATE);
		
		//工程实施状态
        projectExecutionStateList = basicDataService.queryBasicDataBeans("projectExecutionState");
        
        //工程闭环流程状态
        projectCloseProcessStateList = basicDataService.queryBasicDataBeans("projectCloseProcessState");
		
		//项目类型划分
		projectRankList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PRORANK);
		
		//重大项目界别
		majorProjectLevelList = basicDataService.queryBasicDataBeans("majorProjectLevel");
		
		projectTimeList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_PORJECT_TIME);
		
		ssfsList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_SERVICE_TYPE);
	}
	
	/**
	 * 页面排序字段初始化
	 */
	private Map<String, String> initClomnMap(){
		Map<String, String> colMap = new HashMap<String, String>();
		colMap.put("7", "orderCreateTime");
		return colMap;
	}
	/**
	 * @throws UnsupportedEncodingException 
	 */
	private void initProject() throws UnsupportedEncodingException{
		user = UserContext.getUserContext().getUser();
		if(project == null){
			project = new Project();
			if(user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ADMIN)){
				project.setProjectState(MessageUtil.PROJECT_STATE_CREATING);
			}else{
				project.setProjectState(MessageUtil.PROJECT_STATE_30);
			}
		}
		if( user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)){
			project.setProjectState(MessageUtil.PROJECT_STATE_CLOSEDLOOP);
		}
		projectState = project.getProjectState();
		if(MessageUtil.PROJECT_STATE_30.equals(project.getProjectState())){
			project.setProjectState(MessageUtil.PROJECT_STATE_30+"," + MessageUtil.PROJECT_STATE_31 +","+ MessageUtil.PROJECT_STATE_32);
		}
		
		if(displayParam == null){
			displayParam = new DisplayParam();
		}
		initClomnMap();
		displayParam.setColmap(colMap);
		displayParam.getParam();
	}
	/**
	 * 项目列表页面
	 */
	public String execute() throws Exception {
		try {
			initProject();
			if((user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) 
					||user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)
					|| user.isHasRole(MessageUtil.ROLE_COMMON))//服务经理 || 项目经理 || 普通用户
					&& !user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)){//按权限查询已创建项目
				projectlist = projectService.queryProjectListByPower(project, displayParam);
			}else if(user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) 
					|| user.isHasRole(MessageUtil.ROLE_ADMIN)
					|| user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)){//查询全部项目(工程管理部或管理员)
				projectlist = projectService.queryProjectList(project , displayParam);
			}
			project.setProjectState(projectState);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Map<String, Object>> marketRelations = projectService.queryMarketRelations();
		String jsonString = JSON.toJSONString(marketRelations);
		cbForm = new HashMap<String, Object>();
		cbForm.put("marketRelationsWithSubMap", marketRelations);
		return SUCCESS;
	}
	
	/**
	 * 创建项目 / 进入项目查询页面
	 * @return
	 */
	public String insertProject(){
		user = UserContext.getUserContext().getUser();
		//如果没有任何参数传入，则无需做保存操作
		if(checkProjectNull(project)){
			try {
				if (project == null) {
					setErrmsg("合同号不能为空");
					return ERROR;
				}
				project = projectService.queryProjectByContractNo(project.getContractNo());
				if (project == null) {
					setErrmsg("该合同号不存在");
					return ERROR;
				}
				try {
	    			// 设置重大项目级别到项目类别的对应关系
					String majorProjectLevel = project.getMajorProjectLevel();
					String majorProjectLevel2PorjectCategory = basicDataService.querySysArg("pm.project.majorProjectLevel2projectCategory");
					majorProjectLevel2PorjectCategory = StringUtils.defaultIfBlank(majorProjectLevel2PorjectCategory, "{}");
					Map<String, Object> relation = JSON.parseObject(majorProjectLevel2PorjectCategory, Map.class);
					String projectCategory = (String) relation.getOrDefault(majorProjectLevel, MessageUtil.PROJECT_TYPE_NORMAL);
					project.setColumn010(projectCategory);
    			} catch (Exception e) {
    				 e.printStackTrace();
				}
				project.setProjectCode(projectService.queryProjectCode(project));
				orderDataList = projectService.queryOrderLineFromSapByContractNo(project);//排除了退货数据
				Company company = new Company();
		        company.setStatus(1);
		        companyList = departmentManageService.queryCompanyList(company);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return INPUT;
		}else{
			try{
				//如果当前合同号已经创建项目，则直接返回不再创建
				Integer count = projectService.queryProjectContractCountByContractNo(Util.appendChar(project.getContractNo(), "'"));
				if(count != null && count != 0){
					setErrmsg("该合同号已创建项目");
					return ERROR;
				}
				projectService.insertProject(project);//保存
				//保存成功代码
				doSetCode(project, MessageUtil.SUCC_CODE);
				
				//立项通知邮件
				sendMailForApproval(project);
			}catch(Exception e){
				doSetCode(project, MessageUtil.ERR_CODE);
				e.printStackTrace();
				return INPUT;
			}
		}
		return SUCCESS;
	}
	
	/**
	 * 创建串货项目
	 * @return
	 */
	public String createCHProject() {
		user = UserContext.getUserContext().getUser();
		//如果没有任何参数传入，则无需做保存操作
		if(checkProjectNull(project)){
			departmentList = departmentManageService.queryDepartments();
			Company company = new Company();
            company.setStatus(1);
            companyList = departmentManageService.queryCompanyList(company);
			return INPUT;
		}else{
			try{
				//如果当前合同号已经创建项目，则直接返回不再创建
				Integer count = projectService.queryProjectContractCountByContractNo(Util.appendChar(project.getContractNo(), "'"));
				if(count != null && count != 0){
					return ERROR;
				}
				project.setProjectCode(projectService.queryProjectCode(project));
				projectService.insertProject(project);//保存
				//保存成功代码
				doSetCode(project, MessageUtil.SUCC_CODE);
				
				//立项通知邮件
				sendMailForApproval(project);
			}catch(Exception e){
				doSetCode(project, MessageUtil.ERR_CODE);
				e.printStackTrace();
				return INPUT;
			}
		}
		return SUCCESS;
	}
	
	/**
	 * 转移设备
	 * @return
	 */
	public String transferShipment() {
		if (result == 0) {
			if (StringUtils.isNotBlank(projectCode)) {
				Project temp = new Project();
				temp.setProjectCode(projectCode);
				projectlist = projectService.queryTransferProjectList(temp);
			} else {
				projectlist = new ArrayList<>();
			}
		}
		if (result == 2) {
		    Project temp = projectService.queryProjectById(project.getProjectId());
		    if ("14".equals(temp.getSalesType())) {
		        projectService.insertTransferShipment(selected, project, transferProject, temp.getColumn001());
		    } else {
		        projectService.insertTransferShipment(selected, project, transferProject);
		    }
			projectService.updateProjectLastRefreshTime(project.getProjectId());
			projectService.updateProjectLastRefreshTime(transferProject.getProjectId());
			result = 1;
		}
		if (result == 1) { 
			if (transferType == 0) {
				Project temp = projectService.queryProjectById(project.getProjectId());
				contractNo = temp.getContractNo();
			}
			if (StringUtils.isNotBlank(StringUtils.trimToNull(contractNo))) {
				contractNoList = Arrays.asList(contractNo.split(","));
			} else {
				contractNoList = new ArrayList<>();
			}
			if (StringUtils.isNotBlank(project.getContractNo())) {
			    Project temp = projectService.queryProjectById(project.getProjectId());
	            if ("14".equals(temp.getSalesType())) {
	                shipmentInfoList = projectService.queryTransferShipmentInfoByContractNo(temp, transferProject.getProjectId(), temp.getColumn001());
	            } else {
	                shipmentInfoList = projectService.queryTransferShipmentInfoByContractNo(project, transferProject.getProjectId());
	            }
			} else {
				shipmentInfoList = new ArrayList<>();
			}
		}
		return INPUT;
	}
	
	/**
	 * 需要选移到的项目查询
	 * @return
	 */
	public String transferProject() {
		if (StringUtils.isNotBlank(project.getContractNo())) {
			projectlist = projectService.queryTransferProjectList(project);
		} else {
			projectlist = new ArrayList<>();
		}
		return INPUT;
	}
	
	/**
	 * 现场验货单下载
	 * @return
	 */
	public String exportSpotCheck() {
		if (projectId != 0) {
			project = projectService.queryProjectById(projectId);
			Map<String, String> params = projectService.exportSpotCheckList(project);
			if (params != null) {
				downpath = params.get("filePath");
				downname = params.get("fileName");
				return SUCCESS;
			}
		}
		return ERROR;
	}
	
	/**
	 * 导入现场验货单不需要序列号明细的item
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String importSpotCheckIgnoreItem() {
		user = UserContext.getUserContext().getUser();
		if (!(user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER))) {
			batchChangeResult = "authError";
			return INPUT;
		}
		try {
			if (upload != null) {
				List<Item> itemList = (List<Item>) ExportUtils.readFromExcel(upload, uploadFileName, Item.class);
				projectService.importSpotCheckIgnoreItem(itemList);
				batchChangeResult = "导入成功，共导入" + itemList.size() + "条数据";
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			batchChangeResult = "exception";
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * 立项通知邮件
	 * @param project2
	 */
	private void sendMailForApproval(Project project2) {
		try {
			if(!MessageUtil.PROJECT_STATE_DENY.equals(project.getProjectState())){
				//通知邮件
				NotificationTemplate template = null;
				if(MessageUtil.PROJECT_TYPE_NORMAL.equals(project.getColumn010())){//普通类
					template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_NORMAL);
				}else if(MessageUtil.PROJECT_TYPE_ENGINEE.equals(project.getColumn010())){//工程类
					template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_CREATEPRJ_ENGINEE);
				}
				String serviceUsername = project.getServiceManagerCode();
				String serviceMail = projectService.getMails(serviceUsername);
				project.setTos(serviceMail);//主送服务经理
				//立项通知抄送工程管理部屏蔽掉
				this.keepMailInfo(project, template, project.getServiceManagerCodeforjson());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//初始化list
	public void prepareUpdateProject(){
		if(projectPlanList == null){
			projectPlanList = new ArrayList<ProjectPlan>();//财务收款计划列表
		}
		if(orderDataList == null){
			orderDataList = new ArrayList<OrderDataFromSap>();//初始化产品列表
		}
		if(shipmentInfoList == null){
			shipmentInfoList = new ArrayList<ShipmentInfo>();//交付件列表
		}
		if(projectPlanEventList == null){
			projectPlanEventList = new ArrayList<ProjectPlanEvent>();//参照事件列表
		}
		if(projectTaskList == null){
			projectTaskList = new ArrayList<ProjectTask>();//工程计划列表
		}
		if(deliverDetailList == null){
			deliverDetailList = new ArrayList<ProjectDeliver>();//交付件明细列表
		}
		if(weeklyList == null){
			weeklyList = new ArrayList<ProjectWeekly>();//工程周报
		}
		user = UserContext.getUserContext().getUser();
	}
	  
	public String updateProject(){
		if(checkProjectNull(project)){
			if(project.getProjectId() == 0 && project.getParamId() != null){
				project.setProjectId((Integer.parseInt(Base64Util.decodeBase64(project.getParamId()).toString())));
			}
			project = projectService.queryProjectById(project.getProjectId());
			String prjstate = projectService.queryProjectStateByProjectId(project);
			
			modifyflag = obtainModifyflag(prjstate);
			
			if(UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)){
				weeklyList = projectService.queryProjectWeeklyList(project.getProjectId(), MessageUtil.WEEKLY_STATE_ALL );
			}else{
				weeklyList = projectService.queryProjectWeeklyList(project.getProjectId(), MessageUtil.WEEKLY_STATE_SUBMIT );
			}
			//根据合同号查询财务验收计划列表
			projectPlanList = projectPlanService.queryProjectPlanListByContractNo(Util.appendChar(project.getContractNo(), "'"));
//			orderDataList = projectService.queryOrderDataListByProjectId(project.getProjectId());//查询产品列表
//			List<OrderDataFromSap> rmaOrderDataList = projectService.queryRmaOrderDataByContractNo(project.getContractNo());
//			orderDataList.addAll(rmaOrderDataList);
			
			//realOrderDataList = projectService.queryRealOrderDataListByProjectId(project.getProjectId());
			realOrderDataSize = projectService.queryRealOrderDataSizeByProjectId(project.getProjectId());
			//shipmentInfoList = projectService.queryShipmentInfoByContractNo(Util.appendChar(project.getContractNo(), "'"),project.getProjectId());
			//根据projectid查询项目计划列表
			projectTaskList = projectService.queryProjectTaskByProjectId(project.getProjectId());
			//根据项目类型生成事件节点列表
			projectPlanEventList = projectService.queryProjectPlanEventByProject(project);
			
			deliverDetailList = projectService.queryDeliverDetailByProjectId(project.getProjectId());
			if(projectTaskList == null || projectTaskList.size() == 0){
				addPlanList2EventList(projectPlanList, projectPlanEventList);
			}
			doSetCode(project, MessageUtil.SUCC_CODE);
			instructionList =  projectService.queryInstructionList(project.getProjectId());
			projectMemberList = projectService.queryProjectMembers(project.getProjectId());
			memberRoleList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_MEMBER_ROLE);
			memberRoleList = dowithMemberRoleList(memberRoleList);//处理项目成员角色,控制项目经理和服务经理从上面项目信息中指定
			navTabList = obtainNavTabList(project); 
            // 公司列表
			Company company = new Company();
			company.setStatus(1);
			companyList = departmentManageService.queryCompanyList(company);
			
			callBackList = projectService.queryCallBackList(project.getProjectId());
			
            // 未上传必传交付件数量，必传交付件都上传完毕才可闭环
            int undelivedCount = projectService.queryNeededUndelivedCount(project);
            if (undelivedCount == 0) {
                // 必传交付件上传完毕，更新项目闭环流程状态为15：闭环申请
                String closeProcessState = StringUtils.trimToEmpty(project.getCloseProcessState());
                if (closeProcessState.compareTo(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15) < 0) {
                    Project temp = new Project(project.getProjectId());
                    temp.setCloseProcessState(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15);
                    project.setCloseProcessState(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15);
                    project.setCloseProcessStateName(MessageUtil.PROJECT_CLOSE_PROCESS_STATE_15_NAME);
                    projectService.insertOrUpdateProjectState(temp);
                }

                // 最终客户、服务提供商/施工代理商
                String serviceType = project.getColumn012();
                String finalCustomer = project.getColumn013();
                String channel = "";
                if (StringUtils.isBlank(serviceType)) {
                    serviceType = String.valueOf(project.getColumn012Readonly());
                }
                if ("0".equals(serviceType) || "4".equals(serviceType)) {// 原厂直服、原厂集成
                    channel = project.getServiceChannel();
                } else if ("1".equals(serviceType) || "3".equals(serviceType)) {
                    channel = project.getAgentChannel();
                }
                if (StringUtils.isNotBlank(finalCustomer) && StringUtils.isNotBlank(channel)) {
                    // 回访
                    isCallBacking = projectService.queryCallBackingSize(project.getProjectId());
                    if (isCallBacking == 0) {
                        // 安装数量和发货数量
                        int shipmentInfoSize = 0;
                        if ("14".equals(project.getSalesType())) {
                            shipmentInfoSize = projectService.queryShipmentInfoSizeByContractNo(Util.appendChar(project.getContractNo(), "'"), project.getColumn001());
                        } else {
                            shipmentInfoSize = projectService.queryShipmentInfoSizeByContractNo(Util.appendChar(project.getContractNo(), "'"));
                        }
                        int anzhuangdizhisize = projectService.queryProjectShipment(project.getProjectId());
                        if (anzhuangdizhisize == shipmentInfoSize) {
                            isToCloseProject = 1;// 可以进行闭环申请
                        } else {
                            message = "安装数量与发货数量不一致";
                        }
                    } else {
                        message = "正在回访中";
                    }
                } else {
                    message = "请维护最终客户、服务提供商/施工代理商";
                }
            } else {
                message = "请检查必传交付件是否齐全，缺少" + undelivedCount + "份";
            }
			return INPUT;
		}else{
			try{
				
				String isback = projectService.queryProjectStateByProjectId(project);
				boolean b = false;//判断是否包含在以下3个if中
				if(UserContext.getUserContext().isHasRole( MessageUtil.ROLE_ENGINEEMANAGER )){//角色为工程管理部且项目状态为30
					if(project.getServiceManagerCode() == null || "".equals(project.getServiceManagerCode())){//项目回退到未创建状态
						projectService.invalidProject(project.getProjectId());
						return "invalid";//回到项目管理列表
					}else{
						projectService.updateProjectByProjectId(project);//工程管理部权限
					}
					b = true;
				}
				//服务经理指定项目经理
				else if(UserContext.getUserContext().getUsername().equals(project.getServiceManagerCode())&&
						checkPrjState(isback, new String[]{MessageUtil.PROJECT_CREATE_STATE30, MessageUtil.PROJECT_CREATE_STATE32 ,MessageUtil.PROJECT_CREATE_STATE34})){
					b =	projectService.updateProjectProgramManagerByProjectId(project,null);//服务经理 - 根据projectid更新项目经理
					// FIXME 在一个action中连续执行updateProjectProgramManagerByProjectId方法，第二次执行时updateChannel()时，第一次执行updateChannel()所做数据库update，insert操作不会失效，这里只能先用延时来解决
//					Thread.sleep(1000);
//					boolean ff = projectService.updateProjectProgramManagerByProjectId(project,"B");//服务经理 - 根据projectid更新项目经理B
//					if(f || ff){//成功指定项目经理B
//						NotificationTemplate template = null;
//						project = projectService.queryProjectById(project.getProjectId());
//						
//						if(MessageUtil.PROJECT_TYPE_NORMAL.equals(project.getColumn010())){//普通类
//							template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PMNOMINATE_NORMAL);
//						}else if(MessageUtil.PROJECT_TYPE_ENGINEE.equals(project.getColumn010())){//工程类
//							template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PMNOMINATE_ENGINEE);
//						}
//						String serviceUsername = project.getServiceManagerCode();
//						project.setCos(projectService.getMails(serviceUsername));//抄送服务经理
//						if(f){
//							String programUsername = project.getProgramManagerCode();
//							project.setTos(projectService.getMails(programUsername));//主送项目经理
//							this.keepMailInfo(project, template, project.getProgramManagerCodeforjson());
//						}
//						if(ff){
//							String programUsernameB = project.getProgramManagerCodeB();
//							project.setTos(projectService.getMails(programUsernameB));
//							this.keepMailInfo(project, template, project.getProgramManagerCodeforjsonB());
//						}
//					}
//					b = true;
				} else if ((UserContext.getUserContext().getUsername().equals(project.getProgramManagerCode())
						|| UserContext.getUserContext().getUsername().equals(project.getProgramManagerCodeB()))
						&& checkPrjState(isback, new String[] { MessageUtil.PROJECT_CREATE_STATE30,
								MessageUtil.PROJECT_CREATE_STATE32, MessageUtil.PROJECT_CREATE_STATE34 })) {
					projectService.updateChannel(project);// 更新渠道信息
					projectService.updateProjectImplByProjectId(project);// 更新项目实施方式和最终客户名称
				}
				if(!b){
					modifyflag = 1;
				}
			}catch(Exception e){
				doSetCode(project, MessageUtil.ERR_CODE);
				e.printStackTrace();
			}

		}
		return SUCCESS;
	}
	
	/**
	 * 查询设备清单
	 * @return
	 */
	public String checkOrderData() {
		try {
			orderDataList = projectService.queryOrderDataListByProjectId(project.getProjectId());// 查询产品列表
			List<OrderDataFromSap> rmaOrderDataList = projectService.queryRmaOrderDataByContractNo(project.getContractNo());
			orderDataList.addAll(rmaOrderDataList);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	/**
	 * 查询实施发货设备清单
	 * @return
	 */
	public String checkRealOrderData() {
		try {
			realOrderDataList = projectService.queryRealOrderDataListByProjectId(project.getProjectId());
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	/**
	 * 查询发货序列号
	 * @return
	 */
	public String checkShipmentInfo() {
		try {
			user = UserContext.getUserContext().getUser();
			result = projectService.queryHistoryProjectShipmentSize(project.getProjectId());
			Project temp = projectService.queryProjectSimplifyByProjectId(project.getProjectId());
			// 如果是总代借货项目
			if (temp != null && "14".equals(temp.getSalesType())) {
			    shipmentInfoList = projectService.queryShipmentInfoByContractNo(Util.appendChar(temp.getContractNo(), "'"), temp.getProjectId(), temp.getColumn001());
			} else {
			    shipmentInfoList = projectService.queryShipmentInfoByContractNo(Util.appendChar(project.getContractNo(), "'"), project.getProjectId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * 删除发货的安装信息
	 * @return
	 */
	public String deleteShipmentInfo() {
	    try {
	        if (projectId != 0) {
	            project = projectService.queryProjectById(projectId);
	            user = UserContext.getUserContext().getUser();
                if (project != null && (user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
                        || (user.getUsername().equals(project.getServiceManagerCode()) || user.getUsername().equals(project.getProgramManagerCode())
                                || user.getUsername().equals(project.getProgramManagerCodeB())))) {
                    projectService.deleteShipmentInstallInfoByProjectId(projectId);
                    result = 303;
                }
	        }
        } catch (Exception e) {
            e.printStackTrace();
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return SUCCESS;
    }
	/**
	 * 查询软件设备信息
	 * @return
	 */
	public String checkSoftVersion(){
		try {
		    Project temp = projectService.queryProjectSimplifyByProjectId(project.getProjectId());
		    if (temp != null && "14".equals(temp.getSalesType())) {
		        softversionList = projectService.querySoftversionList(Util.appendChar(temp.getContractNo(), "'"),temp.getProjectId(), temp.getColumn001());
		    } else {
		        softversionList = projectService.querySoftversionList(Util.appendChar(project.getContractNo(), "'"),project.getProjectId());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	
	/**
	 * ajax 更新设备软件版本
	 * @return
	 */
	public String updateSoftVersion(){
		try {
			HttpServletRequest request = getServletRequest();
	        String softVersionJson = request.getParameter("softVersionJson");
	        if (StringUtils.isNotBlank(softVersionJson)) {
	        	//将解码后的参数转换为 json 对象
	        	JSONObject json = JSONObject.parseObject(softVersionJson);
	        	JSONArray listArray = json.getJSONArray("softversionList");
	        	JSONObject logMap = json.getJSONObject("softChangeLog");
	        	softversionList = listArray.toJavaList(ShipmentInfo.class);
	        	softChangeLog = logMap.toJavaObject(SoftChangeLog.class);
	        }
            //从 json 对象中获取参数进行后续操作
			projectService.updateSoftversion(softversionList,softChangeLog);
			result = 310;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 获取软件版本历史数据
	 * @return
	 */
	public String checkhistsoftversion(){
		
		changeLogList = projectService.queryHistSoftChangeLog(softChangeLog.getProjectId());
		changeLogList.add(new SoftChangeLog(-1,"V0(出厂版本)"));
		contractNo = null;
		if(softChangeLog.getId() != 0){//若不是首次加载,检索具体的变更信息
			softversionList = projectService.queryHistSoftVersionList(softChangeLog);
			if(softChangeLog.getId() != -1){//出厂版本没有变更记录，不做查询
				softChangeLog = projectService.queryOneSoftChangeLog(softChangeLog.getId());
			}
		}
		return SUCCESS;
	}
	/**
	 * 获取项目的系统通知
	 * @return
	 */
	public String queryProjectNotification(){
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
	 * 获取项目维护记录
	 * @return
	 */
	public String projectMaintenance() {
        if (projectMaintenance != null) {
            user = UserContext.getUserContext().getUser();
            if (projectMaintenance.getOfficeCode() == null) {
                project = projectService.queryProjectById(projectMaintenance.getProjectId());
                projectMaintenance.setOfficeCode(project.getColumn001());
                projectMaintenance.setProjectName(project.getProjectName());
            }
            if (user.getAreapower().contains(projectMaintenance.getOfficeCode())
                   && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) 
                           || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))) {
                projectMaintenance.setHasPower(true);
            }
            //maintenanceList = projectService.selectProjectMaintenanceVOList(projectMaintenance);
            maintenanceMapList = projectService.selectProjectMaintenanceMapList(projectMaintenance, displayParam);
        } else {
            maintenanceList = new ArrayList<>();
            maintenanceMapList = new ArrayList<>();
        }
	    return SUCCESS;
	}
	
	public String createProjectMaintenance() {
	    if (checkPrjId(project)) {
	        setErrmsg("非法操作！");
            return ERROR;
	    }
	    project = projectService.queryProjectById(project.getProjectId());
	    user = UserContext.getUserContext().getUser();
        if (!(user.getAreapower().contains(project.getColumn001())
               && (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) 
                       || user.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)))) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }

        if (projectMaintenance == null || projectMaintenance.getProjectId() == null) {
            Integer quesnaireId = null;
            if (projectMaintenance != null) {
                projectMaintenance = projectService.selectProjectMaintenanceById(projectMaintenance.getId());
                quesnaireId = projectMaintenance.getQuesnaireId();
            }
	        maintenanceTypeList = basicDataService.queryBasicDataBeans("maintenanceType");
	        PmClosedLoopQuesnaire quesObj = new PmClosedLoopQuesnaire();
	        quesObj.setQuesType("projectMaintenance");
            // 获取生效的问卷分类
            pmClosedLoopQuesnaireList = QuestionnarieUtil.findPmClosedLoopQuesnaireList(quesObj);
            if (pmClosedLoopQuesnaireList != null  && !pmClosedLoopQuesnaireList.isEmpty() && pmClosedLoopQuesnaire == null) {
                pmClosedLoopQuesnaire = pmClosedLoopQuesnaireList.get(0);
            }
            // 获取问卷模板的内容或者已填写的问卷内容
            if ((pmClosedLoopQuesnaire != null && pmClosedLoopQuesnaire.getId() != 0)
                    || (projectMaintenance != null && quesnaireId != null
                            && !Integer.valueOf(0).equals(quesnaireId))) {
                int quesnaireState = 0;
                cbForm = QuestionnarieUtil.getCbForm(quesnaireId, pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, quesnaireState);
            }
	    } else {
	        // 问卷提交
            if (pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0) {
                if (pmClQuesnaireResultHeader.getStatus() == 1) {// 已提交，计算分数
                    QuestionnarieUtil.queryQuesnaireScore(pmClosedLoopQuesnaire, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                }
                // 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
                pmClQuesnaireResultHeader.setStatus(1);
                int quesnaireId = QuestionnarieUtil.addQuestionnaireResult(pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);
                projectMaintenance.setQuesnaireId(quesnaireId);
            }
            projectMaintenance.setProjectId(project.getProjectId());
            projectMaintenance.setProjectCode(project.getProjectCode());
            projectMaintenance.setProjectName(project.getProjectName());
            projectMaintenance.setOfficeCode(project.getColumn001());
            
	        projectService.insertOrUpdateProjectMaintenance(projectMaintenance);
	        return "redirect";
	    }
        return SUCCESS;
	}
	
	/**
	 * 获取当前项目需要的选项卡集合
	 * @param project2
	 * @return
	 */
	private List<BasicDataBean> obtainNavTabList(Project project2) {
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_TAB);
//		if(!"运营商市场部".equals(project2.getColumn004()) || !"10".equals(project2.getColumn011())){//运营商直签项目
//			navTabList.remove(6);
//		}
		HashMap<String, Integer> navTabWithIndex = new HashMap<>(); 
		for (int i = 0;i<navTabList.size();i++) {
			BasicDataBean navTab = navTabList.get(i);
			navTabWithIndex.put(navTab.getBasicDataId(), i);
		}
		if(!"运营商市场部".equals(project2.getColumn004()) || !"10".equals(project2.getColumn011())){//运营商直签项目
			//navTabList.remove(7);
			navTabList.remove(navTabWithIndex.get("callbackFlowDiv").intValue());
		}
		if(realOrderDataSize == 0){
//			navTabList.remove(5);
			navTabList.remove(navTabWithIndex.get("realOrderListDiv").intValue());
		}
			
		return navTabList;
	}

	/**
	 * 判断当前用户是否有权限维护项目
	 * @return
	 */
	private int obtainModifyflag(String prjstate) {
		if (UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)) {// 角色为工程管理部且项目状态为30
		} else if (UserContext.getUserContext().isHasRole(MessageUtil.ROLE_SERVICEMANAGER) && checkPrjState(prjstate,
				new String[] { MessageUtil.PROJECT_STATE_30, MessageUtil.PROJECT_CREATE_STATE32,
						MessageUtil.PROJECT_CREATE_STATE38, MessageUtil.PROJECT_CREATE_STATE40,
						MessageUtil.PROJECT_CREATE_STATE42 })) {
		} else if (UserContext.getUserContext().isHasRole(MessageUtil.ROLE_PROGRAMMANAGER) && checkPrjState(prjstate,
				new String[] { MessageUtil.PROJECT_CREATE_STATE32, MessageUtil.PROJECT_CREATE_STATE34,
						MessageUtil.PROJECT_CREATE_STATE40, MessageUtil.PROJECT_CREATE_STATE42 })) {
		} else {
			return 1;// 表示无法更改已填写的信息
		}
		return 0;
	}

	private List<BasicDataBean> dowithMemberRoleList(
			List<BasicDataBean> memberRoleList2) {
		Iterator<BasicDataBean> it = memberRoleList2.iterator();
		BasicDataBean bean  = null;
		while(it.hasNext()){
			bean = it.next();
			if(MessageUtil.MEMBER_PM.equals(bean.getBasicDataId()) ||
					MessageUtil.MEMBER_SM.equals(bean.getBasicDataId())){
				it.remove();
			}
		}
		return memberRoleList2;
	}
	private void keepMailInfo(Project p, NotificationTemplate template, String u) throws Exception{
		if(template != null){//如果有模板，则后续保存到邮件表中
			MailSenderInfo info = new MailSenderInfo();
			//创建替换变量对象，将需要替换的变量置入
			MailContent mc = new MailContent();
			mc.setProjectName(p.getProjectName());
			mc.setUsername(u);
			mc.setOfficeName(p.getOfficeName());
			mc.setBackcase(p.getColumn014());
			info.setSubject(MailHandleUtil.dealwithMail(template.getNotificationSubject(), mc));//邮件主题替换
			info.setContent(MailHandleUtil.dealwithMail(template.getNotificationContent(), mc));//邮件内容替换
			info.setTos(p.getTos());
			info.setCcs(p.getCos());
			sendMailService.keepMailInfo(info);
		}		
	}
	
	private void keepMailInfo(Project p, String templateCode, String username, String... extraContent) throws Exception{
        if(StringUtils.isNotBlank(templateCode)){//如果有模板，则后续保存到邮件表中
            Map<String, Object> mailContext = new HashMap<>();
            if (extraContent != null) {
                for (String extra : extraContent) {
                    if (StringUtils.isBlank(extra)) {
                        continue;
                    }
                    String[] kv = StringUtils.split(extra, ":");
                    if (kv.length > 1) {
                        mailContext.put(kv[0], kv[1]);
                    }
                }
            }
            mailContext.put("tos", project.getTos());
            mailContext.put("ccs", project.getCos());
            mailContext.put("templateCode", templateCode);
            mailContext.put("projectName", project.getProjectName());
            mailContext.put("officeName", project.getOfficeName());
            mailContext.put("backcase", project.getColumn014());
            mailContext.put("username", username);
            NotificationTemplateUtil.keepMail(mailContext);
        }       
    }

	/**
	 * planList的部分字段放到planeventList中
	 * @param planlist
	 * @param planeventlist
	 */
	private void addPlanList2EventList(List<ProjectPlan> planlist, List<ProjectPlanEvent> planeventlist) {
		for(ProjectPlan plan : planlist){
			for(ProjectPlanEvent event : planeventlist){
				if(plan.getReferenceEventName().equals(event.getEventValue())){
					event.setEventPlanHappenDate(plan.getEventPlanHappenDate());
					event.setEventActualFinishDate(plan.getEventActualFinishDate());
				}
			}
		}
	}


	/**
	 * 制定或修改工程计划
	 * @return
	 * @throws InterruptedException 
	 */
	public String editProjectPlan() throws InterruptedException{
		User user = UserContext.getUserContext().getUser();
		if(projectTask == null){
			projectTask = new ProjectTask();
		}
		projectTask.setProjectId(project.getProjectId());
		projectTask.setContractNoStr(project.getContractNo());
		if(checkPrjId(project)){
			return ERROR;
		}
		projectTask.setCreateBy(user.getUsername());
		projectTask.setUpdateBy(user.getUsername());
		projectService.editProjectPlan(projectTask);
		Thread.sleep(1000);
		
		if(projectService.queryProjectPlanState(project.getProjectId())){//判断第一次制定计划，从而变更计划状态
			//查询项目当前工程计划处于的阶段
			String currentTask = projectService.queryProjectCurrentPlan(project.getProjectId());
			project.setProjectPlanState(currentTask);
			projectService.insertOrUpdateProjectState(project);
			
			projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_112, project.getProjectId());
		}else {
			projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_115, project.getProjectId());
		}
		projectService.updateProjectLastRefreshTime(project.getProjectId());
		result = 305;
		return SUCCESS;
	}
	
	/**
	 * 判断projectId是否为空
	 * @param p
	 * @return
	 */
	private boolean checkPrjId(Project p) {
		if(p == null || p.getProjectId() == 0){
			return true;
		}
		return false;
	}
	/**
	 * 上传工程交付件
	 * @return
	 */
	public String uploadDeliverableFile(){
		String[] deliverIds = projectDeliver.getDeliverId().split(",");
		if(projectDeliverList != null && projectDeliverList.size() > 0){
			for(int i = 0;i < projectDeliverList.size();i++){
			    ProjectDeliver pd = projectDeliverList.get(i);
				if(pd == null || pd.getUploaddelivery() == null || pd.getUploaddelivery().length == 0){
					continue;
				}
				ProjectDeliver deliver = new ProjectDeliver();
                BeanUtils.copyProperties(projectDeliver, deliver);
                String deliverableType = pd.getDeliverableType();
                if (StringUtils.isNotBlank(deliverableType)) {
                    String[] splits = StringUtils.split(deliverableType, ",");
                    deliverableType = splits[0];
                }
                deliver.setDeliverableType(deliverableType);
				boolean isCB = projectService.uploadFile(deliver, deliverIds[i], pd.getUploaddelivery(), pd.getUploaddeliveryFileName());
				isCallBack = isCB ? 1:0;
			}
			projectService.updateProjectLastRefreshTime(projectDeliver.getProjectId());
		}
		redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(projectDeliver.getProjectId())+"&result=307&isCallBack="+isCallBack;
		return SUCCESS;
	}
	/**
	 * 删除工程交付件
	 * @return
	 */
	public String deleteDeliverById(){
		try{
			int pId = projectService.deleteDeliverById(deliverid);
			projectService.updateProjectLastRefreshTime(pId);
			result = 309;
		}catch(Exception e){
			deliverid = 0;
		}
		return SUCCESS;
	}
	/**
	 * 项目回退到上一步
	 * @return
	 */
	public String backToLastStep(){
		try {
			Map<String ,Object> paramMap = new HashMap<String, Object>();
			paramMap.put("projectId", projectId);
			paramMap.put("column012", column012);//实施方式
			paramMap.put("channelName", channelName);
			paramMap.put("column013", column013);
			paramMap.put("column008", notGrantTailCause);
			if(isupdate == 1){//做更新操作
				projectService.updateServiceProject(paramMap);
			}
			
			projectService.backToLastStep(projectId ,projectState ,isback ,paramMap);
			result = 201;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**
	 * 判断前一个参数是否在后一个数组中
	 * @param prjstate
	 * @param states
	 * @return
	 */
	private boolean checkPrjState(String prjstate, String[] states) {
		for(String state : states){
			if(state.equals(prjstate)){
				return true;
			}
		}
		return false;
	}

	/**
	 * project以及部分参数是否为空的验证
	 * @param p
	 * @return
	 */
	private boolean checkProjectNull(Project p){
		if(p == null){
			return true;
		}else{
			//如果项目编码为空或者安全标识为空或者不是md5加密的字符串，则返回查看界面，否则允许修改操作
			if(p.getValidateFlag() == null || !Md5Util.getMD5(MessageUtil.SAVE_SUCCESS.getBytes()).equals(p.getValidateFlag()) || project.getContractNo() == null){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 置入返回编码以及返回信息
	 * @param p
	 * @param code
	 */
	private void doSetCode(Project p, int code){
		p.setErrCode(code);
		if(code == MessageUtil.SUCC_CODE){//成功
			p.setErrMess(MessageUtil.SAVE_SUCCESS);
		}else if(code == MessageUtil.ERR_CODE){//失败
			p.setErrMess(MessageUtil.SAVE_FAILED);
		}
	}
	
	/**------------------------------------------项目周报---------------------------------------------------**/
	public String createWeekly(){
		try {
			if(projectWeekly == null){
				projectWeekly = new ProjectWeekly();
			}
			projectWeekly.setWeeklyStartTime(getWeeklyDateTime(new Date()).get(0));
			projectWeekly.setWeeklyEndTime(getWeeklyDateTime(new Date()).get(1));
			projectTaskList = projectService.queryProjectTaskByProjectId(project.getProjectId());
			int lastWeeklyId  = projectService.queryLastWeeklyId(project.getProjectId());
			if(lastWeeklyId != 0){
				ProjectWeekly Weekly = projectService.queryPorjectWeekly(lastWeeklyId);
				projectWeekly.setTaskDeviation(Weekly.getTaskDeviation());
				projectWeekly.setRemark(Weekly.getRemark());
				workcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_WORK);
				riskcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_RISK);
				plancontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_PLAN);
				helpcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_HELP);
				progresscontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_PROPGRESS);
				mailcontentList = projectService.queryWeeklyContentList(lastWeeklyId, MessageUtil.OPTION_TYPE_MAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 根据提供日期的及周报时间规则（上周六到本周五为一个周报时间）计算其开始 结束 时间
	 * @param date
	 * @return List<Date> list[0] 为开始时间 list[1]为往后推的结束时间
	 */
	public static List<Date> getWeeklyDateTime(Date date){
		List<Date> dates = new ArrayList<Date>();
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int day = ca.get(Calendar.DAY_OF_WEEK);
		ca.add(Calendar.DATE, 2-day);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		dates.add(ca.getTime());
		ca.add(Calendar.DATE, 6);
		ca.set(Calendar.HOUR_OF_DAY, 23);
		ca.set(Calendar.MINUTE, 59);
		ca.set(Calendar.SECOND, 59);
		dates.add(ca.getTime());
		return dates;
	}
	
	//保存周报
	public String saveWeekly(){
		try {
			if(projectWeekly.getWeeklyId() ==0 ){//尚未创建周报
				projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_RAFT);
				int weeklyId = projectService.insertPorjectWeekly(projectWeekly,workcontentList ,riskcontentList, helpcontentList, progresscontentList,plancontentList,mailcontentList);
				result = weeklyId;
			}else{//创建后在保存
				projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_RAFT);
				projectService.updatePorjectWeekly(projectWeekly,workcontentList ,riskcontentList, helpcontentList, progresscontentList,plancontentList,mailcontentList);
				result = projectWeekly.getWeeklyId();
			}
			
			projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return SUCCESS;
	}
	/**
	 * 提交周报
	 * @return
	 */
	public String submitWeekly(){
		try {
			if(projectWeekly.getWeeklyId() ==0 ){//尚未创建周报
				projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_SUBMIT);
				int weeklyId = projectService.insertPorjectWeekly(projectWeekly,workcontentList ,riskcontentList, helpcontentList, progresscontentList,plancontentList,mailcontentList);
				result = weeklyId;
			}else{//创建后在保存
				projectWeekly.setWeeklyState(MessageUtil.WEEKLY_STATE_SUBMIT);
				projectService.updatePorjectWeekly(projectWeekly,workcontentList ,riskcontentList, helpcontentList, progresscontentList,plancontentList,mailcontentList);
				result = projectWeekly.getWeeklyId();
			}
			
			//生成周报附件
			String path = projectService.createProjectWeeklyExecl(projectWeekly , workcontentList ,riskcontentList ,helpcontentList ,progresscontentList ,plancontentList);
			
			String ccs = dealWith(mailcontentList);
			ccs += UserContext.getUserContext().getUser().getEmail() + ";";
			String sp_dr = basicDataService.querySysArg("weekly.css.address");
			if(sp_dr != null){
				ccs += sp_dr;
			}
			String memberAddress = projectService.queryMemberAddress(projectWeekly.getProjectId());
			if(memberAddress != null && memberAddress.length() > 0){
				ccs += memberAddress;
			}
			
			project = projectService.queryProjectById(projectWeekly.getProjectId());
			//发送邮件
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("templateCode", MessageUtil.NOTIFICATION_CODE_WEEKLY_SUBMIT);
			context.put("username", UserContext.getUserContext().getUser().getRealName());
			context.put("projectName", project.getProjectName());
			context.put("attachFileNames", path);
			context.put("tos", ccs+basicDataService.querySysArg(MessageUtil.GCGLB)+projectService.getMails(project.getServiceManagerCode()));
			NotificationTemplateUtil.keepMail(context);
			//系统通知
			projectService.addFixedNotification(MessageUtil.NOTIFICATION_CODE_118, projectWeekly.getProjectId());
			projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}
		return SUCCESS;
	}
	/**
	 * 处理提交时填写的需要邮件抄送的邮件地址
	 * @param mailcontentList
	 * @return
	 */
	private String dealWith(List<WeeklyContent> mailcontentList) {
		StringBuilder sb = new StringBuilder();
		if(mailcontentList!= null && mailcontentList.size() > 0){
			for(WeeklyContent content : mailcontentList){
				if(content.getOptionDesc002()!= null && !"".equals(content.getOptionDesc002())){
					sb.append(content.getOptionDesc002());
					sb.append(";");
				}
			}
		}
		return sb.toString();
	}
	/**
	 * 更新周报
	 * @return
	 */
	public String updateWeekly(){
		projectWeekly = projectService.queryPorjectWeekly(projectWeekly.getWeeklyId());
		
		workcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_WORK);
		riskcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_RISK);
		helpcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_HELP);
		progresscontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_PROPGRESS);
		plancontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_PLAN);
		filecontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_FILE);
		mailcontentList = projectService.queryWeeklyContentList(projectWeekly.getWeeklyId(),MessageUtil.OPTION_TYPE_MAIL);
		if(projectWeekly.getWeeklyState() == 1){
			feedbackList = projectService.queryFeedbackList(projectWeekly.getWeeklyId());
		}
		projectService.updateProjectLastRefreshTime(projectWeekly.getProjectId());
		return SUCCESS;
	}
	//附件上传
	public String toUploadFile(){
		
		return SUCCESS;
	}
	//附件上传
	public String toUploadDeliverableFile(){
		String ek = null;
		try{
			ek = projectDeliver.getEventKey();//获取事件节点
			String[] eksplit = ek.split("-");
			projectDeliver.setDataTypeCode(eksplit[0]);
			if (eksplit.length > 1) {
			    projectDeliver.setBasicDataId(eksplit[1]);
			}
			projectDeliverList = projectService.queryProjectDeliverList(projectDeliver);
		}catch(Exception e){
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	/**
	 * 周报交付件上传
	 * @return
	 */
	public String UploadFile(){
		if (upload != null && !upload.equals("")) {
			filecontentList = new ArrayList<WeeklyContent>();
			WeeklyContent weeklyContent = null;
			
			/** 分隔符 **/
			String separator = java.io.File.separator;
			String path =separator + UploadFileUtil.UPLOAD_PATH + separator+"weekly"+separator +new Date().getTime() ;
			boolean bool = Util.mkdir(path);
			if (!bool) {
				addActionMessage(HttpContext.getMessage("sys.adderror"));
				return SUCCESS;
			}
			String uploadExtWhiteList = basicDataService.querySysArg("sys.upload.ext.whitelist");
			String targetDirectory = ServletActionContext.getServletContext()
					.getRealPath(path);
			String[] uploadFileNames = uploadFileName.split(",");
			
			for (int i = 0; i < uploadFileNames.length; i++) {
				String ufn = uploadFileNames[i];// 附件名称
				String targetFileName = ufn.trim();
				// 检查文件上传类型
				if (!UploadFileUtil.checkFileExt(ufn, uploadExtWhiteList)) {
					return ERROR;
				}
				String newName = projectService.getUploadFileRename(targetFileName);
				if(newName == null){
					newName = targetFileName;
				}
				File target = new File(targetDirectory, newName);
				try {
					FileUtils.copyFile(upload[i], target);
				} catch (IOException e) {
					e.printStackTrace();
				}
				weeklyContent = new WeeklyContent();
				weeklyContent.setOptionDesc001(targetFileName);
				weeklyContent.setOptionDesc002(path +separator+ newName );
				filecontentList.add(weeklyContent);
			}
			projectService.insertWeeklyFiles(filecontentList , projectWeekly.getWeeklyId());
		}
		
	//	redirect = "module/ProjectModify.action?project.projectId="+projectWeekly.getProjectId()+"&result=" + 200+"&projectWeekly.weeklyId="+projectWeekly.getWeeklyId();
		redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(projectWeekly.getProjectId())+"&result=" + 200+"&projectWeekly.weeklyId="+projectWeekly.getWeeklyId();
		return SUCCESS;
	}


	/**
	 * 下载交付件
	 */
	public String downloadFile() {

		return SUCCESS;
	}
	
	@org.apache.struts2.json.annotations.JSON(serialize = false)
	public String getDownloadFile() {

		ServletActionContext.getResponse().setHeader("charset", "ISO8859-1");
		try {
			if(downname != null){
				if (result == 0) {
					return new String(downname.getBytes(), "ISO8859-1");
				} else {
					downname = URLEncoder.encode(downname, "ISO8859-1");
					return downname;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "orderplan.xlsx";
	}

	@org.apache.struts2.json.annotations.JSON(serialize = false)
	public InputStream getFileStream() throws FileNotFoundException,
			UnsupportedEncodingException {
		InputStream in = ServletActionContext.getServletContext().getResourceAsStream(downpath);
		// 不存在时，对中文进行转换，再找一次，如果不一致，按原路径查询
		if (null == in) {
			// 对中文进行转换，防止乱码
			String path = new String(downpath.getBytes(Charset.forName("ISO8859-1")), "UTF-8");
			if (!downpath.equals(path)) {
				in = ServletActionContext.getServletContext().getResourceAsStream(path);
			}
		}
		if (null == in) {
			java.lang.System.out
					.println("Can not find a java.io.InputStream with the name [inputStream] in the invocation stack. Check the <param name=\"inputName\"> tag specified for this action.检查action中文件下载路径是否正确.");
		}
		return in;
	}
	

	
	/**
	 * 删除交付件
	 * @return
	 */
	public String deleteFile(){
		try {
			projectService.deleteFileById(downFlileId);
			result = 0;
		} catch (Exception e) {
			e.printStackTrace();
			result  = 1;
		}
		
		return SUCCESS;
	}
	/**
	 * 周报回复
	 * @return
	 */
	public String feedback(){
		try {
			
			projectService.saveWeeklyFeedback(weeklyId , feedback , projectId);
			result = 302;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**------------------------------------------项目周报---------------------------------------------------**/
	
	/**------------------------------------------项目批示-----------------------------------------------------*/
	public String instruction(){
		try {
			
			projectService.saveInstruction(projectId ,instructionsInfo, instructionId );
			result = 301;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	/**---------------------------------------------项目批示-----------------------------------------------------*/
	/**---------------------------------------------ajax---------------------------------------------------------*/
	/**
	 * 根据需求角色查询有该角色的用户
	 * @return
	 */
	public String queryalluser() {
		if(roleid == 0){
			allusernameList = userManageService.queryAllUser();
		}else{
			allusernameList = userManageService.queryUserWithRoleId(roleid);
		}
		return SUCCESS;
	}
	/**
	 * 项目干系人查询用户信息
	 * @return
	 */
	public String queryperson(){
		personList = projectService.queryPersonList();
		return SUCCESS;
	}
	/**
	 * 项目回退
	 * @return
	 */
	public String updateprojectisback(){
		boolean hasRole = false;
		int sendto = 0;//判断发送消息给谁
		NotificationTemplate template = null;
		try{
			project = projectService.queryProjectById(projectId);
			if(MessageUtil.PROJECT_CREATE_STATE36.equals(isback)){//服务经理或项目经理申请回退至工程管理部
				hasRole = projectService.getLoginName().equals(project.getProgramManagerCode()) 
						|| projectService.getLoginName().equals(project.getProgramManagerCodeB()) 
						|| projectService.getLoginName().equals(project.getServiceManagerCode());
				sendto = 1;
				if(hasRole){
//					template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PROJECT_BACK);
					String engineeMail = basicDataService.querySysArg(MessageUtil.GCGLB);
					String ccs = projectService.getMails(project.getServiceManagerCode());
					if(project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())){
						ccs += ";" + projectService.getMails(project.getProgramManagerCode());
					}
					if(project.getProgramManagerCodeB() != null && !"".equals(project.getProgramManagerCodeB())){
						ccs += ";" + projectService.getMails(project.getProgramManagerCodeB());
					}
//					project.setTos(engineeMail + ccs);//主送工程管理部
//					project.setColumn014(backCause);
//					this.keepMailInfo(project, template, "工程管理部");
				    
                    project.setTos(engineeMail);
                    project.setCos(ccs);
                    project.setColumn014(backCause);
                    this.keepMailInfo(project, MessageUtil.NOTIFICATION_CODE_PROJECT_BACK, "工程管理部", 
                            "content:正在回退中", 
                            "beforeSplit:[",
                            "afterSplit:]");
                }
			}else if(MessageUtil.PROJECT_CREATE_STATE38.equals(isback)){//项目经理申请回退申请回退至服务经理
				
				hasRole = projectService.getLoginName().equals(project.getProgramManagerCode()) 
						|| projectService.getLoginName().equals(project.getProgramManagerCodeB());
				sendto = 2;
				project.setColumn014(backCause);
				if(hasRole){
					//template = projectService.queryNotificationTemplate(MessageUtil.NOTIFICATION_CODE_PROJECT_BACK);
					String serviceMail = projectService.getMails(project.getServiceManagerCode());
					project.setTos(serviceMail);//主送服务经理
//					this.keepMailInfo(project, template, project.getServiceManagerCodeforjson());
					
                    project.setCos(null);
                    this.keepMailInfo(project, MessageUtil.NOTIFICATION_CODE_PROJECT_BACK, project.getServiceManagerCodeforjson(), 
                            "content:正在回退中", 
                            "beforeSplit:[",
                            "afterSplit:]");
				}
			}else if(MessageUtil.PROJECT_CREATE_STATE30.equals(isback)){//工程管理部同意回退
				hasRole = UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ENGINEEMANAGER);
				sendto = 3;
				if (StringUtils.isNotBlank(notbackCause)) {
				    sendto = -3;
				    String tos = projectService.getMails(project.getServiceManagerCode());
				    String username = StringUtils.trimToEmpty(project.getServiceManagerCodeforjson());
                    if(project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())){
                        tos += ";" + projectService.getMails(project.getProgramManagerCode());
                        username += "、" + project.getProgramManagerCodeforjson();
                    }
                    if(project.getProgramManagerCodeB() != null && !"".equals(project.getProgramManagerCodeB())){
                        tos += ";" + projectService.getMails(project.getProgramManagerCodeB());
                        username += "、" + project.getProgramManagerCodeforjsonB();
                    }
                    project.setTos(tos);
                    project.setCos(null);
                    project.setColumn014(null);
                    this.keepMailInfo(project, MessageUtil.NOTIFICATION_CODE_PROJECT_BACK, username, 
                            "disagree:驳回",
                            "content:回退申请被驳回", 
                            "notbackCause:" + notbackCause, 
                            "beforeSplit:[",
                            "afterSplit:]");
				}
			}else if(MessageUtil.PROJECT_CREATE_STATE32.equals(isback)){//服务经理同意回退
				hasRole = projectService.getLoginName().equals(project.getServiceManagerCode());
				sendto = 4;
				if (StringUtils.isNotBlank(notbackCause)) {
				    sendto = -4;
                    String tos = "";
                    List<String> username = new ArrayList<String>(2);
                    if(project.getProgramManagerCode() != null && !"".equals(project.getProgramManagerCode())){
                        tos += ";" + projectService.getMails(project.getProgramManagerCode());
                        username.add(project.getProgramManagerCodeforjson());
                    }
                    if(project.getProgramManagerCodeB() != null && !"".equals(project.getProgramManagerCodeB())){
                        tos += ";" + projectService.getMails(project.getProgramManagerCodeB());
                        username.add(project.getProgramManagerCodeforjsonB());
                    }
                    project.setTos(tos);
                    project.setCos(null);
                    project.setColumn014(null);
                    this.keepMailInfo(project, MessageUtil.NOTIFICATION_CODE_PROJECT_BACK, StringUtils.join(username, "、"), 
                            "disagree:驳回",
                            "content:回退申请被驳回", 
                            "notbackCause:" + notbackCause, 
                            "beforeSplit:[",
                            "afterSplit:]");
                }
			}
			if(!hasRole){//如果角色不匹配
				throw new RuntimeException("无权限[role=" + UserContext.getUserContext().getUsername() + 
											", projectId=" + projectId + ", isback=" + isback + "]");
			}
			projectService.updateProjectIsbackByProjectId(projectId, isback, backCause, pm, sendto, notbackCause);//根据主键更改回退状态和回退说明字段
			result = sendto;//成功
			
			projectService.updateProjectLastRefreshTime(projectId);
			
			if(sendto == 3 && (pm == null || "".equals(pm))){//项目管理部使项目回退到未创建状态，可以重新创建或合并到其他项目中，不发通知信息
				result = -1;//返回项目管理页面
			}
		}catch(Exception e){
			e.printStackTrace();
			result = 0;//失败
		}
		return SUCCESS;
	}
	/**
	 * 创建项目成员
	 * @return
	 */
	public String createMember(){
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
			if(memberEffectiveFrom == null ){
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
	public String updateMember(){
		try {
			member = new ProjectMember();
			member.setId(memberId);
			if(memberEffectiveTo != null){
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
	 * saveInstallAdress保存安装地址
	 */
	public String saveInstallAdress(){
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
    public String updateProjectExecutionState(){
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

	/**---------------------------------------------ajax---------------------------------------------------------*/
	
	/**---------------------------------------------合同拆分合并-------------------------------------------------------*/
	/**
	 * 进入合同拆分合并页面
	 * @return
	 */
	public String toMergeOrBranch(){
		navTabList = basicDataService.queryBasicDataBeans(MessageUtil.BASIC_DATA_NAV_MERGE_TAB);
		
		orderDataList = projectService.queryOrderDataListByProjectId(project.getProjectId());//查询产品列表
		
		return INPUT;
	}
	/**
	 * 查询要合并的合同信息
	 * @return
	 */
	public String checkMergeContract(){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("mergeContractNo", mergeContractNo);
		
		int size = projectService.queryProjectContractCountByContractNo(Util.appendChar(mergeContractNo ,"'"));
		if(size == 1){
			result = 404;
		}else{
			contractList = projectService.queryContractList(paramMap);
		}
		return SUCCESS;
	}
	/**
	 * 合并操作
	 */
	public String mergeContract(){
		if (selected == null || "".equals(selected)) {
			setErrmsg("请至少选择一条合同数据，谢谢！");
			return ERROR;
		}
		projectService.insertMergeContract(selected ,projectId);
//		redirect = "module/ProjectModify.action?project.projectId="+projectId+"&result=302";
		redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(projectId)+"&result=302";
		return SUCCESS;
	}
	/**
	 * 项目拆分
	 * @return
	 */
	public String branchContract(){
		int newProjectId = projectService.insertNewProject(projectId ,project.getProjectCode() ,productList ,mergeBranchMark );
//		redirect = "module/ProjectModify.action?project.projectId="+projectId+"&result=202&newProjectId="+newProjectId;
		redirect = "module/ProjectModify.action?project.paramId="+Base64Util.EncodeBase64(projectId)+"&result=202&paramId="+Base64Util.EncodeBase64(newProjectId);
		return SUCCESS;
	}
	/**---------------------------------------------合同拆分合并-------------------------------------------------------*/

	public String queryDpNoRoleUser() {
		HashMap<String, String> params = new HashMap<>();
		params.put("roleid", String.valueOf(roleid));
		if(batchCgMb != null) {
			params.put("dpNo", batchCgMb.getDpNo());
		}
		allusernameList = userManageService.queryUserWithRoleIdAndDpNo(params);
		return SUCCESS;
	}
	
	public String batchChangeMember(){
		departmentList = new ArrayList<Department>();
		if(batchCgMb == null){
			departmentList = departmentManageService.queryDepartments();
			batchCgMb = new ProjectBatchCgMbParam();
			return INPUT;
		}
		batchChangeResult = ProjectUtils.updateServiceAndProgramMember(batchCgMb);
		return SUCCESS;
	}
	
//	public String updateServiceAndProgramMember(ProjectBatchCgMbParam batchCgMb){
//		String newMemberCode = batchCgMb.getNewMemberName().split("-")[0];
//		String oldMemberCode = batchCgMb.getOldMemberCode();
//		batchCgMb.setNewMemberCode(newMemberCode);
//		String changeType = batchCgMb.getChangeType();
////		batchChangeResult = userManageService.updateServiceAndProgramMember(batchCgMb);
//		DisplayParam displayParam = new DisplayParam();
//		displayParam.setExport(true);
//		
//		int serviceCount = 0;
//		int programCount = 0;
//		StringBuffer projectIds = new StringBuffer();
//		
//		// 查询项目状态30，31，32，指定部门的项目
//		Project project = new Project();
//		project.setColumn001(batchCgMb.getDpNo());
//		project.setProjectState(MessageUtil.PROJECT_STATE_30+"," + MessageUtil.PROJECT_STATE_31 +","+ MessageUtil.PROJECT_STATE_32);
//		
//		// 保存更改的项目中存在工作流的taskID
//		List<String> tasks = new ArrayList<String>();
//		// 变更服务经理
//		if(changeType.equals("service") || changeType.equals("both")){
//			project.setServiceManagerCode(oldMemberCode);
//			List<Project> projects = projectService.queryProjectList(project , displayParam);
//			for (Project projectTemp : projects) {
//				// 更新项目服务经理
//				projectTemp.setDataTypeCode("20");
//				projectTemp.setUpdateBy(UserContext.getUserContext().getUsername());
//				projectTemp.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
//				boolean flag= projectService.updateProjectMember(projectTemp, newMemberCode, batchCgMb.getNewMemberName());
//				// 是否发生变更，如果变更则更新项目以及记录项目闭环流程taskID
//				if(flag){//指定服务经理后，更新项目状态
//					serviceCount++;
//					projectService.updateProjectStatus(projectTemp.getProjectId(), projectTemp.getProjectState());
//					projectIds.append("'" + projectTemp.getProjectId() + "',");
//					String taskId = pmClosedLoopService.queryTaskByBussinessKey(projectTemp);
//					if(taskId != null)
//						tasks.add(taskId);
//				}
//			}
//			// 服务经理更新pm_cl_evaluation_header，中的nextPerson,审批人发生变更，并且变更流程审批人
//			if(projectIds.length()>0){
//				HashMap<String, String> params = new HashMap<>();
//				params.put("oldNextAcceptPerson", oldMemberCode);
//				params.put("nextAcceptPerson", newMemberCode);
//				params.put("nextAcceptPersonName", batchCgMb.getNewMemberName().split("-")[1]);
//				params.put("projectIds", projectIds.substring(0, projectIds.length() - 1));
//				pmClosedLoopService.updateEvaluationHeaderNextAcceptPerson(params);
//				for (String taskId : tasks) {
//					taskService.setAssignee(taskId, batchCgMb.getNewMemberCode());
//				}
////				// 服务经理更新项目闭环流程的审批人
////				List<Task> tasks= taskService.createTaskQuery().processDefinitionKey("PmClosedLoop").taskAssignee(batchCgMb.getOldMemberCode()).list();
////				for (Task task : tasks) {
////					taskService.setAssignee(task.getId(), batchCgMb.getNewMemberCode());
////				}
//			}
//			
//		}
//		if(projectIds.length()>0)
//			projectIds.delete(0, projectIds.length() - 1);
//		if(changeType.equals("program") || changeType.equals("both")){
//			project.setServiceManagerCode(null);
//			project.setProgramManagerCode(oldMemberCode);
//			List<Project> projects = projectService.queryProjectList(project , displayParam);
//			for (Project projectTemp : projects) {
//				projectTemp.setDataTypeCode("30");
//				projectTemp.setUpdateBy(UserContext.getUserContext().getUsername());
//				projectTemp.setFromFlag(MessageUtil.FLAG_FROM_PROJECT);
//				boolean flag = projectService.updateProjectMember(projectTemp, newMemberCode, batchCgMb.getNewMemberName());
//				if(flag){
//					programCount++;
//					projectService.updateProjectStatus(projectTemp.getProjectId(), projectTemp.getProjectState());
//					projectIds.append("'" + projectTemp.getProjectId() + "',");
//				}
//			}
//		}
//		return serviceCount+":"+programCount;
//	}
	
	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	public List<Project> getProjectlist() {
		return projectlist;
	}

	public void setProjectlist(List<Project> projectlist) {
		this.projectlist = projectlist;
	}

	public void setDepartmentList(List<Department> departmentList) {
		this.departmentList = departmentList;
	}

	public List<Department> getDepartmentList() {
		return departmentList;
	}

	public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

	public void setUserManageService(UserManageService userManageService) {
		this.userManageService = userManageService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public List<BasicDataBean> getProjectTypeList() {
		return projectTypeList;
	}

	public void setProjectTypeList(List<BasicDataBean> projectTypeList) {
		this.projectTypeList = projectTypeList;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<User> getAllusernameList() {
		return allusernameList;
	}

	public void setAllusernameList(List<User> allusernameList) {
		this.allusernameList = allusernameList;
	}

	public void setDepartmentManageService(
			DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	

	public Map<String, String> getColMap() {
		return colMap;
	}
	public void setColMap(Map<String, String> colMap) {
		this.colMap = colMap;
	}
	public List<Person> getPersonList() {
		return personList;
	}

	public void setPersonList(List<Person> personList) {
		this.personList = personList;
	}

	public List<Instruction> getInstructionList() {
		return instructionList;
	}

	public void setInstructionList(List<Instruction> instructionList) {
		this.instructionList = instructionList;
	}

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public String getInstructionsInfo() {
		return instructionsInfo;
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

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public List<ProjectWeekly> getWeeklyList() {
		return weeklyList;
	}

	public void setWeeklyList(List<ProjectWeekly> weeklyList) {
		this.weeklyList = weeklyList;
	}

	public ProjectWeekly getProjectWeekly() {
		return projectWeekly;
	}

	public void setProjectWeekly(ProjectWeekly projectWeekly) {
		this.projectWeekly = projectWeekly;
	}

	public List<WeeklyContent> getWorkcontentList() {
		return workcontentList;
	}

	public void setWorkcontentList(List<WeeklyContent> workcontentList) {
		this.workcontentList = workcontentList;
	}

	public List<WeeklyContent> getRiskcontentList() {
		return riskcontentList;
	}

	public void setRiskcontentList(List<WeeklyContent> riskcontentList) {
		this.riskcontentList = riskcontentList;
	}

	public List<WeeklyContent> getHelpcontentList() {
		return helpcontentList;
	}

	public void setHelpcontentList(List<WeeklyContent> helpcontentList) {
		this.helpcontentList = helpcontentList;
	}

	public List<WeeklyContent> getProgresscontentList() {
		return progresscontentList;
	}

	public void setProgresscontentList(List<WeeklyContent> progresscontentList) {
		this.progresscontentList = progresscontentList;
	}

	public List<WeeklyContent> getPlancontentList() {
		return plancontentList;
	}

	public void setPlancontentList(List<WeeklyContent> plancontentList) {
		this.plancontentList = plancontentList;
	}

	public List<WeeklyContent> getFilecontentList() {
		return filecontentList;
	}

	public void setFilecontentList(List<WeeklyContent> filecontentList) {
		this.filecontentList = filecontentList;
	}

	public void setProjectPlanList(List<ProjectPlan> projectPlanList) {
		this.projectPlanList = projectPlanList;
	}

	public List<ProjectPlan> getProjectPlanList() {
		return projectPlanList;
	}

	public void setProjectPlanService(ProjectPlanService projectPlanService) {
		this.projectPlanService = projectPlanService;
	}

	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}

	public int getModifyflag() {
		return modifyflag;
	}

	public void setModifyflag(int modifyflag) {
		this.modifyflag = modifyflag;
	}

	public List<OrderDataFromSap> getOrderDataList() {
		return orderDataList;
	}

	public void setOrderDataList(List<OrderDataFromSap> orderDataList) {
		this.orderDataList = orderDataList;
	}

	/**
	 * @return the realOrderDataList
	 */
	public List<RealProductLineBean> getRealOrderDataList() {
		return realOrderDataList;
	}

	/**
	 * @param realOrderDataList the realOrderDataList to set
	 */
	public void setRealOrderDataList(List<RealProductLineBean> realOrderDataList) {
		this.realOrderDataList = realOrderDataList;
	}

	public List<ShipmentInfo> getShipmentInfoList() {
		return shipmentInfoList;
	}

	public void setShipmentInfoList(List<ShipmentInfo> shipmentInfoList) {
		this.shipmentInfoList = shipmentInfoList;
	}

	public File[] getUpload() {
		return upload;
	}

	public void setUpload(File[] upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getDownpath() {
		return downpath;
	}

	public void setDownpath(String downpath) {
		this.downpath = downpath;
	}

	public String getDownname() {
		return downname;
	}

	public void setDownname(String downname) {
		this.downname = downname;
	}

	public int getDownFlileId() {
		return downFlileId;
	}

	public void setDownFlileId(int downFlileId) {
		this.downFlileId = downFlileId;
	}

	public String getProjectState() {
		return projectState;
	}

	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}

	public List<ProjectPlanEvent> getProjectPlanEventList() {
		return projectPlanEventList;
	}

	public void setProjectPlanEventList(List<ProjectPlanEvent> projectPlanEventList) {
		this.projectPlanEventList = projectPlanEventList;
	}

	public List<ProjectTask> getProjectTaskList() {
		return projectTaskList;
	}

	public void setProjectTaskList(List<ProjectTask> projectTaskList) {
		this.projectTaskList = projectTaskList;
	}

	public ProjectTask getProjectTask() {
		return projectTask;
	}

	public void setProjectTask(ProjectTask projectTask) {
		this.projectTask = projectTask;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<ProjectMember> getProjectMemberList() {
		return projectMemberList;
	}
	public void setProjectMemberList(List<ProjectMember> projectMemberList) {
		this.projectMemberList = projectMemberList;
	}
	public ProjectMember getMember() {
		return member;
	}
	public void setMember(ProjectMember member) {
		this.member = member;
	}
	public List<BasicDataBean> getMemberRoleList() {
		return memberRoleList;
	}
	public void setMemberRoleList(List<BasicDataBean> memberRoleList) {
		this.memberRoleList = memberRoleList;
	}
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
	public String getUploaddelivery1FileName() {
		return uploaddelivery1FileName;
	}
	public void setUploaddelivery1FileName(String uploaddelivery1FileName) {
		this.uploaddelivery1FileName = uploaddelivery1FileName;
	}
	public String getUploaddelivery2FileName() {
		return uploaddelivery2FileName;
	}
	public void setUploaddelivery2FileName(String uploaddelivery2FileName) {
		this.uploaddelivery2FileName = uploaddelivery2FileName;
	}
	public String getUploaddelivery3FileName() {
		return uploaddelivery3FileName;
	}
	public void setUploaddelivery3FileName(String uploaddelivery3FileName) {
		this.uploaddelivery3FileName = uploaddelivery3FileName;
	}
	public String getUploaddelivery4FileName() {
		return uploaddelivery4FileName;
	}
	public void setUploaddelivery4FileName(String uploaddelivery4FileName) {
		this.uploaddelivery4FileName = uploaddelivery4FileName;
	}
	public List<BasicDataBean> getNavTabList() {
		return navTabList;
	}
	public void setNavTabList(List<BasicDataBean> navTabList) {
		this.navTabList = navTabList;
	}
	public ProjectDeliver getProjectDeliver() {
		return projectDeliver;
	}
	public void setProjectDeliver(ProjectDeliver projectDeliver) {
		this.projectDeliver = projectDeliver;
	}
	public List<ProjectDeliver> getProjectDeliverList() {
		return projectDeliverList;
	}
	public void setProjectDeliverList(List<ProjectDeliver> projectDeliverList) {
		this.projectDeliverList = projectDeliverList;
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
	public File[] getUploaddelivery1() {
		return uploaddelivery1;
	}
	public void setUploaddelivery1(File[] uploaddelivery1) {
		this.uploaddelivery1 = uploaddelivery1;
	}
	public File[] getUploaddelivery2() {
		return uploaddelivery2;
	}
	public void setUploaddelivery2(File[] uploaddelivery2) {
		this.uploaddelivery2 = uploaddelivery2;
	}
	public File[] getUploaddelivery3() {
		return uploaddelivery3;
	}
	public void setUploaddelivery3(File[] uploaddelivery3) {
		this.uploaddelivery3 = uploaddelivery3;
	}
	public File[] getUploaddelivery4() {
		return uploaddelivery4;
	}
	public void setUploaddelivery4(File[] uploaddelivery4) {
		this.uploaddelivery4 = uploaddelivery4;
	}
	public List<ProjectDeliver> getDeliverDetailList() {
		return deliverDetailList;
	}
	public void setDeliverDetailList(List<ProjectDeliver> deliverDetailList) {
		this.deliverDetailList = deliverDetailList;
	}
	public int getDeliverid() {
		return deliverid;
	}
	public void setDeliverid(int deliverid) {
		this.deliverid = deliverid;
	}
	public String getIsback() {
		return isback;
	}
	public void setIsback(String isback) {
		this.isback = isback;
	}
	public int getWeeklyId() {
		return weeklyId;
	}
	public void setWeeklyId(int weeklyId) {
		this.weeklyId = weeklyId;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public List<WeeklyFeedback> getFeedbackList() {
		return feedbackList;
	}
	public void setFeedbackList(List<WeeklyFeedback> feedbackList) {
		this.feedbackList = feedbackList;
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
	public void setSendMailService(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}
	public List<WeeklyContent> getMailcontentList() {
		return mailcontentList;
	}
	public void setMailcontentList(List<WeeklyContent> mailcontentList) {
		this.mailcontentList = mailcontentList;
	}
	public int getWorkSpaceReturnType() {
		return workSpaceReturnType;
	}
	public void setWorkSpaceReturnType(int workSpaceReturnType) {
		this.workSpaceReturnType = workSpaceReturnType;
	}

	public String getMergeContractNo() {
		return mergeContractNo;
	}

	public void setMergeContractNo(String mergeContractNo) {
		this.mergeContractNo = mergeContractNo;
	}

	public List<Contract> getContractList() {
		return contractList;
	}

	public void setContractList(List<Contract> contractList) {
		this.contractList = contractList;
	}
	public String getEventKeyStr() {
		return eventKeyStr;
	}
	public void setEventKeyStr(String eventKeyStr) {
		this.eventKeyStr = eventKeyStr;
	}
	public String getEventValueStr() {
		return eventValueStr;
	}
	public void setEventValueStr(String eventValueStr) {
		this.eventValueStr = eventValueStr;
	}
	public String getEventDoingStr() {
		return eventDoingStr;
	}
	public void setEventDoingStr(String eventDoingStr) {
		this.eventDoingStr = eventDoingStr;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public String getParamId() {
		return paramId;
	}
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}
	public String getMergeBranchMark() {
		return mergeBranchMark;
	}

	public void setMergeBranchMark(String mergeBranchMark) {
		this.mergeBranchMark = mergeBranchMark;
	}

	public String getColumn012() {
		return column012;
	}

	public void setColumn012(String column012) {
		this.column012 = column012;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getColumn013() {
		return column013;
	}

	public void setColumn013(String column013) {
		this.column013 = column013;
	}

	public String getNotGrantTailCause() {
		return notGrantTailCause;
	}

	public void setNotGrantTailCause(String notGrantTailCause) {
		this.notGrantTailCause = notGrantTailCause;
	}

	public int getIsupdate() {
		return isupdate;
	}

	public void setIsupdate(int isupdate) {
		this.isupdate = isupdate;
	}

	public String getBackCause() {
		return backCause;
	}

	public void setBackCause(String backCause) {
		this.backCause = backCause;
	}

	public void setServletContext(ServletContext context) {
		this.context = context;
	}

	public int getBatchFunc() {
		return batchFunc;
	}

	public void setBatchFunc(int batchFunc) {
		this.batchFunc = batchFunc;
	}

	public String getPm() {
		return pm;
	}

	public void setPm(String pm) {
		this.pm = pm;
	}
	public List<BasicDataBean> getProjectRankList() {
		return projectRankList;
	}
	public void setProjectRankList(List<BasicDataBean> projectRankList) {
		this.projectRankList = projectRankList;
	}
	public List<BasicDataBean> getMajorProjectLevelList() {
		return majorProjectLevelList;
	}
	public void setMajorProjectLevelList(List<BasicDataBean> majorProjectLevelList) {
		this.majorProjectLevelList = majorProjectLevelList;
	}

	/**
	 * 批量创建项目或关闭项目  系统管理员权限
	 * @return
	 */
	public String importProject(){
		user = UserContext.getUserContext().getUser();
		if(!UserContext.getUserContext().isHasRole(MessageUtil.ROLE_ADMIN)){//不是管理员无法继续操作
			result = 2;
			return SUCCESS;
		}
		try{
			List<Project> projectlist = parseFileToList("data.xlsx",batchFunc);
			if(projectlist.size() > 0){
				for(Project p : projectlist){
					Integer count = projectService.queryProjectContractCountByContractNo(Util.appendChar(p.getContractNo(), "'"));
					//如果当前合同号已经创建项目，则直接返回不再创建
					if(count != null && count != 0){
						continue;
					}
					projectService.insertBatchProject(p,batchFunc);//保存
				}
			}
			result = 1;
		}catch(Exception e){
			e.printStackTrace();
			result = 2;
			return SUCCESS;
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String clearProject() {
		user = UserContext.getUserContext().getUser();
		if (!(user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER))) {
			batchChangeResult = "authError";
			return INPUT;
		}
		try {
			if (upload != null) {
				List<Project> projectList = (List<Project>) ExportUtils.readFromExcel(upload, uploadFileName, Project.class);
				if (modifyflag == 1) {
					batchChangeResult = String.valueOf(projectService.batchDeleteProject(projectList));
				} else {
					batchChangeResult = String.valueOf(projectService.batchInvalidProject(projectList));
				}
			}
			project = new Project();
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			batchChangeResult = "exception";
			return ERROR;
		}
		return SUCCESS;
	}
	private List<Project> parseFileToList(String path ,int batchFunc) throws Exception {
		List<Project> resultlist = new ArrayList<Project>();
//		String targetDirectory = context.getRealPath("/upload/data");
		String targetDirectory = context.getRealPath("/" + UploadFileUtil.UPLOAD_PATH + "/data");
		ExcelParser parser;
		try {
			parser = new ExcelParser(targetDirectory + File.separator + path);
			XSSFSheet sheet  = null;
			if(batchFunc == 1){//直接闭环
				sheet = parser.parserExcel("闭环清单");
			}else if(batchFunc == 2){//进行中的清单 -- 指定服务经理
				sheet = parser.parserExcel("进行中清单-指定服务经理");
			}else if(batchFunc == 3){//进行中的清单-- 指定项目经理+服务经理
				sheet = parser.parserExcel("进行中清单-指定项目经理");
			}
			
			int rows = parser.size(sheet);
			// 从第二行开始读取，第一行是标题
			XSSFRow row = null;
			XSSFCell cell = null;
			for (int j = 1; j < rows; j++) {
				Project p = new Project();
				row = sheet.getRow(j);
				cell = row.getCell(2);//合同号列
				p.setContractNo(cell.getStringCellValue());
				
				if(batchFunc == 2){
					cell = row.getCell(3);//项目类型
					String str1 = cell.getStringCellValue();
					if("工程类".equals(str1)){
						str1 = "20";
					}else{
						str1 = "10";
					}
					p.setColumn010(str1);
					
					cell = row.getCell(4);
					String str2 = cell.getStringCellValue();
					if("直签类".equals(str2)){
						str2 = "10";
					}else{
						str2 = "20";
					}
					p.setColumn011(str2);
					
					cell = row.getCell(5);
					p.setServiceManagerCode(cell.getStringCellValue());//服务经理
					
					cell = row.getCell(6);
					p.setServiceManagerCodeforjson(cell.getStringCellValue());
				}
				
				if(batchFunc == 3){
					cell = row.getCell(7);//项目经理
					p.setProgramManagerCode(cell.getStringCellValue());
					
					cell = row.getCell(8);//项目经理
					p.setProgramManagerCodeforjson(cell.getStringCellValue());
				}
				if(p.getContractNo() != null && !"".equals(p.getContractNo())){
					resultlist.add(p);
				}
			}
		} catch (InvalidFormatException e) {
			setErrmsg(HttpContext.getMessage("sys.error.invalidfileformat"));
			throw e;
		} catch (IOException e) {
			setErrmsg(HttpContext
					.getMessage("sys.error.filenotfoundornotaccess"));
			throw e;
		} catch (IllegalArgumentException e) {
			setErrmsg(HttpContext.getMessage("sys.error.infoerror"));
			throw e;
		} catch (Exception e) {
			setErrmsg(HttpContext.getMessage("sys.error.invaliduploadfiletype"));
			e.printStackTrace();
			throw e;
		}
		return resultlist;
	}
	public List<BasicDataBean> getDeliverStateList() {
		return deliverStateList;
	}
	public void setDeliverStateList(List<BasicDataBean> deliverStateList) {
		this.deliverStateList = deliverStateList;
	}
	public int getIsToCloseProject() {
		return isToCloseProject;
	}
	public void setIsToCloseProject(int isToCloseProject) {
		this.isToCloseProject = isToCloseProject;
	}

	public List<BasicDataBean> getProjectPlanStateList() {
		return projectPlanStateList;
	}

	public void setProjectPlanStateList(List<BasicDataBean> projectPlanStateList) {
		this.projectPlanStateList = projectPlanStateList;
	}

	public List<BasicDataBean> getProjectExecutionStateList() {
        return projectExecutionStateList;
    }

    public void setProjectExecutionStateList(List<BasicDataBean> projectExecutionStateList) {
        this.projectExecutionStateList = projectExecutionStateList;
    }

    public List<BasicDataBean> getProjectCloseProcessStateList() {
        return projectCloseProcessStateList;
    }

    public void setProjectCloseProcessStateList(List<BasicDataBean> projectCloseProcessStateList) {
        this.projectCloseProcessStateList = projectCloseProcessStateList;
    }

    public List<BasicDataBean> getProjectTimeList() {
		return projectTimeList;
	}

	public void setProjectTimeList(List<BasicDataBean> projectTimeList) {
		this.projectTimeList = projectTimeList;
	}

	public String getMemberRoleName() {
		return memberRoleName;
	}

	public void setMemberRoleName(String memberRoleName) {
		this.memberRoleName = memberRoleName;
	}

	public List<CallBack> getCallBackList() {
		return callBackList;
	}

	public void setCallBackList(List<CallBack> callBackList) {
		this.callBackList = callBackList;
	}
	public int getIsCallBack() {
		return isCallBack;
	}
	public void setIsCallBack(int isCallBack) {
		this.isCallBack = isCallBack;
	}
	public List<BasicDataBean> getSsfsList() {
		return ssfsList;
	}
	public void setSsfsList(List<BasicDataBean> ssfsList) {
		this.ssfsList = ssfsList;
	}
	public int getIsCallBacking() {
		return isCallBacking;
	}
	public void setIsCallBacking(int isCallBacking) {
		this.isCallBacking = isCallBacking;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	
	public List<ShipmentInfo> getSoftversionList() {
		return softversionList;
	}
	public void setSoftversionList(List<ShipmentInfo> softversionList) {
		this.softversionList = softversionList;
	}
	public SoftChangeLog getSoftChangeLog() {
		return softChangeLog;
	}
	public void setSoftChangeLog(SoftChangeLog softChangeLog) {
		this.softChangeLog = softChangeLog;
	}
	public List<SoftChangeLog> getChangeLogList() {
		return changeLogList;
	}
	public void setChangeLogList(List<SoftChangeLog> changeLogList) {
		this.changeLogList = changeLogList;
	}
	/**
	 * @return the batchCgMb
	 */
	public ProjectBatchCgMbParam getBatchCgMb() {
		return batchCgMb;
	}

	/**
	 * @param batchCgMb the batchCgMb to set
	 */
	public void setBatchCgMb(ProjectBatchCgMbParam batchCgMb) {
		this.batchCgMb = batchCgMb;
	}

	/**
	 * @return the batchChangeResult
	 */
	public String getBatchChangeResult() {
		return batchChangeResult;
	}

	/**
	 * @param batchChangeResult the batchChangeResult to set
	 */
	public void setBatchChangeResult(String batchChangeResult) {
		this.batchChangeResult = batchChangeResult;
	}
	
	@Override
	public void prepare() throws Exception {
		
	}

	public Project getTransferProject() {
		return transferProject;
	}

	public void setTransferProject(Project transferProject) {
		this.transferProject = transferProject;
	}

	public List<String> getContractNoList() {
		return contractNoList;
	}

	public void setContractNoList(List<String> contractNoList) {
		this.contractNoList = contractNoList;
	}

	public int getTransferType() {
		return transferType;
	}

	public void setTransferType(int transferType) {
		this.transferType = transferType;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public List<Notification> getNotificationList() {
		return notificationList;
	}

	public void setNotificationList(List<Notification> notificationList) {
		this.notificationList = notificationList;
	}

    public String getNotbackCause() {
        return notbackCause;
    }

    public void setNotbackCause(String notbackCause) {
        this.notbackCause = notbackCause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectMaintenanceVO getProjectMaintenance() {
        return projectMaintenance;
    }

    public void setProjectMaintenance(ProjectMaintenanceVO projectMaintenance) {
        this.projectMaintenance = projectMaintenance;
    }

    public List<ProjectMaintenanceVO> getMaintenanceList() {
        return maintenanceList;
    }

    public void setMaintenanceList(List<ProjectMaintenanceVO> maintenanceList) {
        this.maintenanceList = maintenanceList;
    }

    public List<Map<String, Object>> getMaintenanceMapList() {
        return maintenanceMapList;
    }

    public void setMaintenanceMapList(List<Map<String, Object>> maintenanceMapList) {
        this.maintenanceMapList = maintenanceMapList;
    }

    public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
        return pmClosedLoopQuesnaireList;
    }

    public void setPmClosedLoopQuesnaireList(List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
        this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
    }

    public PmClosedLoopQuesnaire getPmClosedLoopQuesnaire() {
        return pmClosedLoopQuesnaire;
    }

    public void setPmClosedLoopQuesnaire(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
        this.pmClosedLoopQuesnaire = pmClosedLoopQuesnaire;
    }

    public PmClQuesnaireResultHeader getPmClQuesnaireResultHeader() {
        return pmClQuesnaireResultHeader;
    }

    public void setPmClQuesnaireResultHeader(PmClQuesnaireResultHeader pmClQuesnaireResultHeader) {
        this.pmClQuesnaireResultHeader = pmClQuesnaireResultHeader;
    }

    public List<PmClQuesnaireResultLine> getPmClQuesnaireResultLineList() {
        return pmClQuesnaireResultLineList;
    }

    public void setPmClQuesnaireResultLineList(List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList) {
        this.pmClQuesnaireResultLineList = pmClQuesnaireResultLineList;
    }

    public Map<String, Object> getCbForm() {
        return cbForm;
    }

    public void setCbForm(Map<String, Object> cbForm) {
        this.cbForm = cbForm;
    }

    public List<BasicDataBean> getMaintenanceTypeList() {
        return maintenanceTypeList;
    }

    public void setMaintenanceTypeList(List<BasicDataBean> maintenanceTypeList) {
        this.maintenanceTypeList = maintenanceTypeList;
    }
    
}
