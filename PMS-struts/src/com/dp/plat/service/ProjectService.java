package com.dp.plat.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.dp.plat.data.bean.ProjectSoftVersion;
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
import com.dp.plat.param.RealProductLineBean;
import com.dp.plat.supervision.entity.ProjectSupervision;
import com.dp.plat.supervision.vo.ProjectSupervisionVO;


public interface ProjectService extends BaseService{
	/**
	 * 查询项目信息
	 * @param project
	 * @param displayParam
	 * @return
	 */
	List<Project> queryProjectList(Project project, DisplayParam displayParam);

	/**
	 * 保存项目信息
	 * @param project
	 * @return projectId
	 */
	int  insertProject(Project project) throws Exception;

	/**
	 * 更新项目信息
	 * @param project
	 */
	void updateProjectByProjectId(Project project);
	/**
	 * 查询销售信息
	 * @return
	 */
	List<Person> queryPersonList();
	/**
	 * 根据项目ID查询项目批示及反馈信息
	 * @param projectId
	 * @return
	 */
	List<Instruction> queryInstructionList(int projectId);

	/**
	 * 根据合同号查询项目信息
	 * @param contractNo
	 * @return
	 */
	Project queryProjectByContractNo(String contractNo);
	
	/**
	 * 根据合同号、项目类型查询项目信息
	 * @param contractNo
	 * @return
	 */
	Project queryProjectByContractNoAndType(String contractNo, String projectType);
	
	/**
	 * 插入项目批示信息
	 * @param instruction
	 */
	void insertInstruction(Instruction instruction);
	/**
	 * 根据projectId查询项目信息
	 * @param projectId
	 * @return
	 */
	Project queryProjectById(int projectId);
	
	/**
     * 根据project查询有权限的项目信息
     * 
     * @param project
     * @return
     */
    Project queryProjectByPowerId(Project project);

	/**
	 * 根据projectid更新项目经理-服务经理权限
	 * @param project
	 * @return 
	 */
	boolean updateProjectProgramManagerByProjectId(Project project);

	/**
	 * 根据projectid更新项目经理-服务经理权限,type:A or B ,更新项目经理A或项目经理B
	 * @param project
	 * @param type
	 * @return 
	 */
	boolean updateProjectProgramManagerByProjectId(Project project, String type);

	/**
	 * 根据projectid更新项目信息-项目经理权限
	 * @param project
	 */
	void updateProjectDetailByProjectId(Project project);
	/**
	 * 根据权限查询项目
	 * @param project
	 * @param displayParam
	 * @return
	 */
	List<Project> queryProjectListByPower(Project project,
			DisplayParam displayParam);
	/**
	 * 保存项目周报
	 * @param projectWeekly
	 * @param workcontentList
	 * @param riskcontentList
	 * @param helpcontentList
	 * @param progresscontentList
	 * @param plancontentList
	 * @param filecontentList
	 * @return
	 */
	int insertPorjectWeekly(ProjectWeekly projectWeekly,
			List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList,
			List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList,
			List<WeeklyContent> plancontentList,
			List<WeeklyContent> filecontentList);

	/**
	 * 根据projectid查询项目状态
	 * @param project
	 * @return
	 */
	String queryProjectStateByProjectId(Project project);

	/**
	 * 根据projectid查询产品信息汇总
	 * @param projectId
	 * @return
	 */
	List<OrderDataFromSap> queryOrderDataListByProjectId(int projectId);
	/**
     * 根据projectid查询产品信息明细
     * @param projectId
     * @return
     */
    List<OrderDataFromSap> queryOrderDataDetailListByProjectId(int projectId);
	/**
	 * 查询项目周报
	 * @param projectId
	 * @param weeklyState 
	 * @return
	 */
	List<ProjectWeekly> queryProjectWeeklyList(int projectId, int weeklyState);
	/**
	 * 查询周报基本信息
	 * @param weeklyId
	 * @return
	 */
	ProjectWeekly queryPorjectWeekly(int weeklyId);
	/**
	 * 查询周报内容信息
	 * @param weeklyId
	 * @param optionTypeWork
	 * @return
	 */
	List<WeeklyContent> queryWeeklyContentList(int weeklyId, int optionTypeWork);

	/**
	 * 根据合同号查询序列号清单
	 * @param contractNo
	 * @param projectId 
	 * @return
	 */
	List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId);
	   /**
     * 根据合同号、利润中心查询序列号清单
     * @param contractNo
     * @param projectId 
     * @param profitCenter 
     * @return
     */
    List<ShipmentInfo> queryShipmentInfoByContractNo(String contractNo, int projectId, String profitCenter);

	/**
	 * 更新项目周报
	 * @param projectWeekly
	 * @param workcontentList
	 * @param riskcontentList
	 * @param helpcontentList
	 * @param progresscontentList
	 * @param plancontentList
	 * @param filecontentList
	 */
	void updatePorjectWeekly(ProjectWeekly projectWeekly,
			List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList,
			List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList,
			List<WeeklyContent> plancontentList,
			List<WeeklyContent> filecontentList);
	/**
	 * 保存周报附件信息
	 * @param filecontentList
	 * @param weeklyId
	 */
	void insertWeeklyFiles(List<WeeklyContent> filecontentList, int weeklyId);
	/**
	 * 删除周报附件
	 * @param downFlileId
	 */
	void deleteFileById(int downFlileId);
	/**
	 * 更改项目状态
	 * @param projectId
	 * @param projectState
	 * @param isback 
	 */
	void backToLastStep(int projectId, String projectState, String isback ,Map<String , Object> paramMap);

	/**
	 * 根据项目查询事件节点列表
	 * @param project
	 * @return
	 */
	List<ProjectPlanEvent> queryProjectPlanEventByProject(Project project);
	/**
	 * 查询项目组成员
	 * @param projectId
	 * @return
	 */
	List<ProjectMember> queryProjectMembers(int projectId);
	/**
	 * 增加项目成员
	 * @param member
	 * @return
	 */
	int insertProjectMember(ProjectMember member);
	/**
	 * 更新项目成员
	 * @param member
	 */
	void updateProjectMember(ProjectMember member);

	/**
	 * 更新项目成员
	 * @param project
	 * @param membercode
	 * @param memberName
	 * @return
	 */
	boolean updateProjectMember(Project project, String membercode, String memberName);

	/**
	 * 更新项目计划
	 * @param projectTask
	 */
	void editProjectPlan(ProjectTask projectTask);

	/**
	 * 根据projectid查询项目计划列表
	 * @param projectId
	 * @return
	 */
	List<ProjectTask> queryProjectTaskByProjectId(int projectId);
	/**
	 * 更新安装地址
	 * @param selected
	 * @param projectId
	 * @param installAddress
	 * @param contractNo 
	 */
	void insertInstallAddress(String selected, int projectId, String installAddress, String contractNo);
	/**
     * 更新安装地址
     * @param selected
     * @param projectId
     * @param installAddress
     * @param contractNo 
     * @param profitCenter 
     */
    void insertInstallAddress(String selected, int projectId, String installAddress, String contractNo, String profitCenter);
	/**
	 * 插入项目周报回复内容
	 * @param paramMap
	 */
	void insertWeeklyFeedback(Map<String, Object> paramMap);
	/**
	 * 查询项目周报回复列表
	 * @param weeklyId
	 * @return
	 */
	List<WeeklyFeedback> queryFeedbackList(int weeklyId);

	/**
	 * 查询交付件下拉列表
	 * @param projectDeliver
	 * @return
	 */
	List<ProjectDeliver> queryProjectDeliverList(ProjectDeliver projectDeliver);
	/**
	 * 插入交付件表
	 * @param pd
	 * @param pdlist
	 * @param username
	 */
	boolean insertProjectDeliverFiles(ProjectDeliver pd, List<ProjectDeliver> pdlist, String username);

	/**
	 * 根据projectid查询交付件列表
	 * @param projectId
	 * @return
	 */
	List<ProjectDeliver> queryDeliverDetailByProjectId(int projectId);

	/**
	 * 根据projectid,项目类型查询交付件列表
	 * 
	 * @param projectId
	 * @return
	 */
	List<ProjectDeliver> queryDeliverDetailByProjectIdAndProjectType(int projectId, String projectTypes);

    /**
     * 根据projectid,数据类型查询交付件列表
     * @param projectId
     * @param dataTypeCode
     * @return
     */
    List<ProjectDeliver> queryDeliverDetailByProjectIdAndDeliverType(int projectId, String dataTypeCode);

	/**
	 * 根据交付件id删除（软删除）
	 * @param deliverid
	 * @return projectId
	 */
	int deleteDeliverById(int deliverid);

	/**
	 * 根据主键更改回退状态和回退说明字段
	 * @param projectId
	 * @param isback
	 * @param backCause
	 * @param sendto
	 */
	void updateProjectIsbackByProjectId(int projectId, String isback, String backCause,String pm, int sendto);
	/**
	 * 插入项目操作日志
	 * @param log
	 * @return
	 */
	int insertLog(String handleName ,String handleDesc ,Integer projectId);
	/**
	 * 更新项目操作日志
	 * @param handleId
	 */
	void updateLog(int handleId ,int handleState);

	/**
	 * 根据projectid更新项目实施方式
	 * @param project
	 */
	void updateProjectImplByProjectId(Project project);
	/**
	 * 查询上期周报ID
	 * @param projectId
	 * @return
	 */
	int queryLastWeeklyId(int projectId);
	/**
	 * 生成周报文件
	 * @param projectWeekly
	 * @param workcontentList
	 * @param riskcontentList
	 * @param helpcontentList
	 * @param progresscontentList
	 * @param plancontentList
	 * @return 
	 */
	String createProjectWeeklyExecl(ProjectWeekly projectWeekly,
			List<WeeklyContent> workcontentList,
			List<WeeklyContent> riskcontentList,
			List<WeeklyContent> helpcontentList,
			List<WeeklyContent> progresscontentList,
			List<WeeklyContent> plancontentList);
	/**
	 * 查询周报提交模板
	 * @param notificationCodeWeeklySubmit
	 * @return
	 */
	NotificationTemplate queryNotificationTemplate(
			String notificationCodeWeeklySubmit);

	
	/**
	 *更新项目状态，不需要发送通知与邮件
	 * @param projectId
	 * @param projectState
	 */
	void updateProjectStatus(int projectId, String projectState);

	/**
	 * 根据角色ID查询邮箱
	 * @param roleEngineemanager
	 * @return
	 */
	public String getMails(int roleId);
	/**
	 * 根据用户名查询邮箱
	 * @param username
	 * @return
	 */
	public String getMails(String username);
	/**
	 * 根据角色查找用户
	 */
	public List<String> getUsernames(int roleId);
	/**
	 * 根据合同号查询信息
	 * @param paramMap
	 * @return
	 */
	List<Contract> queryContractList(Map<String, Object> paramMap);
	/**
	 * 插入合并的合同
	 * @param selected
	 * @param projectId
	 */
	void insertMergeContract(String selected, int projectId);
	/**
	 * 创建新项目
	 * @param projectId
	 * @param projectCode
	 * @param productList
	 * @param mergeBranchMark 
	 * @return 
	 */
	int insertNewProject(int projectId, String projectCode,
			List<Product> productList, String mergeBranchMark);
	/**
	 * 查询系统信息
	 * @return
	 */
	List<Department> querySystemList();
	/**
	 * 查询项目组成员地址
	 * @param projectId
	 * @return
	 */
	String queryMemberAddress(int projectId);
	
	/**
	 * 根据合同号查询SAP刷新的产品清单
	 * @param project
	 * @return
	 */
	public List<OrderDataFromSap> queryOrderLineFromSapByContractNo(Project project);
	/**
	 * 更新项目信息
	 * @param paramMap
	 */
	void updateServiceProject(Map<String, Object> paramMap);

	/**
	 * 根据合同号查询合同表是否存在
	 * @param contractNo
	 * @return
	 */
	Integer queryProjectContractCountByContractNo(String contractNo);
	
	/**
	 * 根据合同号查询合同表是否存在
	 * @param contractNo
	 * @return
	 */
	Integer queryProjectContractCountByContractNoAndType(String contractNo, String projectType);
	
	/**
	 * 批量处理项目信息
	 * @param p
	 * @param batchFunc
	 * @throws Exception 
	 */
	void insertBatchProject(Project p, int batchFunc) throws Exception;
	/**
	 * 根据项目ID对项目相关数据进行失效处理
	 * @param projectId
	 */
	void invalidProject(int projectId);
	/**
	 * 查询项目列表信息
	 * @param project
	 * @param colMap
	 * @return
	 */
	List<Project> findProjectList(Object ...objs) throws UnsupportedEncodingException;
	/**
	 * 保存批示信息
	 * @param0 projectId
	 * @param1 instructionsInfo
	 * @param2 instructionId
	 */
	void saveInstruction(Object ...objs);
	/**
	 * 保存周报回复内容
	 * @param0 weeklyId
	 * @param1 feedback
	 * @param2 projectId
	 */
	void saveWeeklyFeedback(Object ...objs);
	/**
	 * 将上传的文件重命名
	 * @param targetFileName
	 * @return
	 */
	String getUploadFileRename(String targetFileName);
	
    /**
     * 上传交付件
     * @param pd
     * @param did
     * @param deliverFile
     * @return 返回是否发起了回访申请
     */
    boolean uploadFile(ProjectDeliver pd, String did, ProjectDeliver deliverFile);
    
	/**
	 * 上传交付件,带系统通知和邮件提醒
	 * @param projectDeliver
	 * @param string
	 * @param uploaddelivery
	 * @param uploaddeliveryFileName
	 * @return 返回是否发起了回访申请
	 */
	boolean uploadFile(ProjectDeliver projectDeliver, String string,
			File[] uploaddelivery, String uploaddeliveryFileName);
	/**
	 * 查询已经补充安装地址的设备数量
	 * @param projectId
	 * @return
	 */
	int queryProjectShipment(int projectId);
	/**
	 * 查询已保存安装地址的所有设备，含所有转销、退货设备
	 * 
	 * @param projectId
	 * @return
	 */
	int queryHistoryProjectShipmentSize(int projectId);
	/**
	 * 查询要生成的项目编码
	 * @param project
	 * @return
	 */
	String queryProjectCode(Project project);
	/**
	 * 插入或更新项目状态
	 */
	void insertOrUpdateProjectState(Project project);
	
	/**
	 * 更新项目实施状态
     * @param project
     * @param executionState
     */
    void updateProjectExecutionState(Project project, String executionState);
    
    /**
     * 更新项目实施状态
     * @param projectId
     * @param executionState
     */
    void updateProjectExecutionState(int projectId, String executionState);
    
    /**
     * 更新项目闭环流程状态,当闭环状态小于“项目闭环”状态时会查询项目判断项目的状态，如果项目状态为“不予跟踪”或“已闭环”则，闭环流程状态为“项目闭环”
     * @param projectId
     * @param closeProcessState
     */
    void updateProjectCloseProcessState(int projectId, String closeProcessState);
    
    /**
     * 更新项目闭环流程状态，如果项目状态为“不予跟踪”或“已闭环”则，闭环流程状态为“项目闭环”
     * @param project
     * @param closeProcessState
     */
    void updateProjectCloseProcessState(Project project, String closeProcessState);
    
	/**
	 * 判断第一次制定计划，从而变更计划状态
	 * @param projectId
	 * @return
	 */
	boolean queryProjectPlanState(int projectId);
	/**
	 * 查询项目当前工程计划处于的阶段
	 * @param projectId
	 * @return
	 */
	String queryProjectCurrentPlan(int projectId);
	/**
	 * 更新项目闭环时间
	 * @param closeObjId
	 */
	void updateProjectCloseTime(int closeObjId);
	/**
	 * 更新项目闭环时间
	 * @param projectId
	 */
	void updateProjectDirectCloseTime(int projectId);
	/**
	 * 更新项目数据最新刷新时间
	 * @param projectId
	 */
	void updateProjectLastRefreshTime(int projectId);
	/**
	 * 更新项目计划状态为"项目闭环"
	 * @param closeObjId
	 */
	void updateProjectPlanStateToClose(int closeObjId);
	/**
	 * 保存确定内容的通知
	 * @param templateCode
	 * @param projectId
	 */
	void addFixedNotification(String templateCode , int projectId);
	/**
	 * 添加内容为动态的通知
	 * @param templateCode
	 * @param projectId
	 * @param content
	 */
	void addDynamicNotification(String templateCode , int projectId ,String content);
	/**
	 * 添加内容为动态的通知
	 * @param templateCode
	 * @param projectId
	 * @param params
	 */
	void addDynamicNotification(String templateCode, int projectId, HashMap<String, Object> params);
	/**
	 * 根据项目闭环申请主表ID获取项目ID
	 * @param closeObjId
	 * @return
	 */
	int queryProjectIdBycloseId(int closeObjId);
	/**
	 * 查询回访流程任务
	 * @param projectId
	 * @return
	 */
	List<CallBack> queryCallBackList(int projectId);
	/**
	 * 查询正在审批中的回访申请
	 * @param projectId
	 * @return
	 */
	int queryCallBackingSize(int projectId);
	
	/**
	 * 是否可以闭环
	 * @param project
	 * @return
	 */
	int canCloseLoop(Project project);
	
	/**
	 * 查询退货订单信息
	 * @param contractNo
	 * @return
	 */
	List<OrderDataFromSap> queryRmaOrderDataByContractNo(String contractNo);

	List<Project> queryProjectListByOfficeAndMemberCode(Project project);

	/**
	 * 查询项目真实订单数据
	 * @param projectId
	 * @return
	 */
	List<RealProductLineBean> queryRealOrderDataListByProjectId(int projectId);

	/**
	 * 查询项目发货数量
	 * @param contractNos
	 * @return
	 */
	int queryShipmentInfoSizeByContractNo(String contractNos);

	/**
     * 根据利润中心、合同号查询项目发货数量
     * @param contractNos
     * @param profitCenter 
     * @return
     */
    int queryShipmentInfoSizeByContractNo(String contractNos, String profitCenter);

    
	/**
	 * @param projectId
	 * @return
	 */
	int queryRealOrderDataSizeByProjectId(int projectId);

	/**
	 * @param projectId
	 * @param flowRuning
	 * @return
	 */
	List<CallBack> queryCallBackRunList(int projectId, int flowRuning);
	
	/**
	 * 查询软件版本
	 * @param contractNo
	 * @param projectId
	 * @return
	 */
	List<ProjectSoftVersion> querySoftversionList(String contractNo, int projectId);

	/**
     * 查询软件版本
     * @param contractNo
     * @param projectId
     * @param profitCenter 
     * @return
     */
    List<ProjectSoftVersion> querySoftversionList(String contractNo, int projectId, String profitCenter);
    
    /**
     * 查询软件版本
     * 
     * @param contractNo
     * @param projectId
     * @param params 
     * @return
     */
    List<ProjectSoftVersion> querySoftversionList(String contractNo, int projectId, Map<String, Object> params);
    

    /**
     * 查询项目已保存的设备软件版本
     * @param projectSoftVersion
     * @param displayParam
     * @return
     */
    List<ProjectSoftVersion> selectProjectSoftVersionList(ProjectSoftVersion projectSoftVersion, DisplayParam displayParam);

	/**
	 * 更新软件版本
	 * @param softversionList
	 * @param softChangeLog 
	 */
	void updateSoftversion(List<? extends ShipmentInfo> softversionList, SoftChangeLog softChangeLog);
	/**
	 * 查询软件版本历史变更
	 * @param projectId
	 * @return
	 */
	List<SoftChangeLog> queryHistSoftChangeLog(int projectId);
	/**
	 * 检索具体的版本变更信息
	 * @param softChangeLog
	 * @return
	 */
	List<ProjectSoftVersion> queryHistSoftVersionList(SoftChangeLog softChangeLog);
	/**
	 * 查询单个版本更新日志
	 * @param id
	 * @return
	 */
	SoftChangeLog queryOneSoftChangeLog(int id);

	/**
	 * 更新渠道信息
	 * @param project
	 */
	void updateChannel(Project project);

	/**
	 * 从同步的OA数据库表中查询邮件地址
	 * @param userName
	 * @return
	 */
	String queryMailByUserNameFromOA(String userName);

	/**
	 * 查询项目有效的成员，只包括销售，服务经理和项目经理
	 * @param projectId
	 * @return
	 */
	List<ProjectMember> queryValidMemberByProjectId(int projectId);

	/**
	 * 批量删除项目，主要用于借货项目的实施
	 * @param projectList
	 */
	int batchDeleteProject(List<Project> projectList);

	/**
	 * 批量失效项目，主要用于借货项目的实施
	 * @param projectList
	 * @return 
	 */
	int batchInvalidProject(List<Project> projectList);

	/**
	 * 转移设备界面查询项目列表
	 * @param project
	 * @return
	 */
	List<Project> queryTransferProjectList(Project project);

	/**
	 * 查询可以转移的设备序列号
	 * @param project
	 * @param transferProjectId
	 * @return
	 */
	List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId);
    /**
     * 查询可以转移的设备序列号
     * @param project
     * @param transferProjectId
     * @param profitCenter 
     * @return
     */
    List<ShipmentInfo> queryTransferShipmentInfoByContractNo(Project project, int transferProjectId, String profitCenter);

	/**
	 * 插入转移的设备序列号
     * @param selected
     * @param project
     * @param transferProject
	 */
	void insertTransferShipment(String selected, Project project, Project transferProject);

	/**
     * 插入转移的设备序列号
	 * @param selected
	 * @param project
	 * @param transferProject
	 * @param profitCenter
	 */
    void insertTransferShipment(String selected, Project project, Project transferProject, String profitCenter);

	/**
	 * 导出现场验货单
	 * @param project
	 */
	Map<String, String> exportSpotCheckList(Project project);
	
	/**
	 * 导出过保提醒函
     * @param project
     */
    Map<String, String> exportOverWarrantyRemindList(Project project);

	/**
	 * 导入现场验货单需要忽略序列号明细的item
	 * @param itemList
	 */
	void importSpotCheckIgnoreItem(List<Item> itemList);

	/**
	 * @param projectId
	 * @return
	 */
	List<Notification> queryNotifyList(int projectId);

    /**
     * @param projectMaintenance
     * @return
     */
    List<ProjectMaintenanceVO> selectProjectMaintenanceList(ProjectMaintenanceVO projectMaintenance);

    /**
     * @param projectMaintenance
     * @return 
     */
    Integer insertOrUpdateProjectMaintenance(ProjectMaintenance projectMaintenance);

    /**
     * @param id
     * @return 
     */
    ProjectMaintenanceVO selectProjectMaintenanceById(Integer id);

    /**
     * @param projectMaintenance
     * @return
     */
    List<ProjectMaintenanceVO> selectProjectMaintenanceVOList(ProjectMaintenanceVO projectMaintenance);

    /**
     * @param projectMaintenance
     * @return
     */
    List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance);

    /**
     * @param projectMaintenance
     * @param displayParam
     * @return
     */
    List<Map<String, Object>> selectProjectMaintenanceMapList(ProjectMaintenanceVO projectMaintenance, DisplayParam displayParam);

    /**
     * 查询项目简化信息，只包含服务经理，项目经理，部门
     * @param projectId
     * @return
     */
    Project queryProjectSimplifyByProjectId(Integer projectId);

    /**
     * @param projectId
     * @param isback
     * @param backCause
     * @param pm
     * @param sendto
     * @param nobackCause
     */
    void updateProjectIsbackByProjectId(int projectId, String isback, String backCause, String pm, int sendto, String nobackCause);

    /**
     * 查询未传的必传交付件数量
     * @param project
     * @return
     */
    int queryNeededUndelivedCount(Project project);

    /**
     * 查询未传的必传交付件
     * @param project
     * @return
     */
    List<ProjectDeliver> queryNeededUndelivedProjectDeliverList(Project project);

    /**
     * @param id
     * @return
     */
    ProjectSupervisionVO selectProjectSupervisionById(Integer id);

    /**
     * @param projectSupervision
     * @return
     */
    List<ProjectSupervisionVO> selectProjectSupervisionList(ProjectSupervisionVO projectSupervision);

    /**
     * @param projectSupervision
     * @return
     */
    List<ProjectSupervisionVO> selectProjectSupervisionVOList(ProjectSupervisionVO projectSupervision);

    /**
     * @param projectSupervision
     * @return
     */
    List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision);

    /**
     * @param projectSupervision
     * @param displayParam
     * @return
     */
    List<Map<String, Object>> selectProjectSupervisionMapList(ProjectSupervisionVO projectSupervision, DisplayParam displayParam);

    /**
     * @param projectSupervision
     * @return
     */
    Integer insertOrUpdateProjectSupervision(ProjectSupervision projectSupervision);

    /**
     * 查询单个项目中最大的维护记录id
     * @param projectMaintenance 
     * @return maxId
     */
    Integer selectSingleProjectMaintenanceMaxId(ProjectMaintenance projectMaintenance);

    /**
     * 更新总代借货的项目信息，合同号、及项目名称
     */
    void updateSoleAgentLendProject();

    /**
     * 删除安装信息
     * @param projectId
     */
    void deleteShipmentInstallInfoByProjectId(int projectId);

    /**
     * 查询项目的维保状态，维保级别，增值服务
     * @param projectId
     */
	Map<String, Object> queryProjectWarrantyState(Integer projectId);

	/**
	 * 项目维护上传交付件
	 * @param projectDeliver
	 * @param deliverId
	 * @param deliverFile
	 * @return
	 */
	boolean uploadMaintenanceFile(ProjectMaintenanceVO projectMaintenance, ProjectDeliver projectDeliver,
			String deliverId, ProjectDeliver deliverFile);

	/**
	 * 查询服务交付列表
	 * @param projectMaintenance
	 * @return
	 */
	List<Map<String, Object>> selectProjectMaintenanceServiceDeliveryList(ProjectMaintenanceVO projectMaintenance,
			DisplayParam displayParam);

	List<Map<String, Object>> queryMarketRelations();

	/**
	 * 查询合同号的交付件完成情况
	 * @param paramMap
	 * @return
	 */
    List<Map<String, Object>> selectContractAcceptanceDeliveryInfo(Map<String, Object> paramMap);

    /**
     * 查询项目的工单信息
     * @param params
     * @return
     */
    List<Map<String, Object>> selectProblemTicket(Map<String, Object> params);

    /**
     * 查询项目的License授权信息
     * @param params
     * @return
     */
    List<Map<String, Object>> selectLicenseInfo(Map<String, Object> params);

    /**
     * 查询项目的工单信息,通过项目的发货列表
     * @param params
     * @return
     */
    List<Map<String, Object>> selectProblemTicketByProject(Project project);

    /**
     * 更新项目信息
     * @param project
     * @return
     */
    int updateProjectByProjectIdSelective(Project project);

    /**
     * 查询租赁配置
     * @param projectCode
     * @return
     */
    List<?> queryProjectLeaseLineByProjectCode(String smsProjectCode);

    /**
     * 查询租赁配置数量
     * @param projectCode
     * @return
     */
    int queryProjectLeaseLineSizeByProjectCode(String smsProjectCode);

    List<?> queryProjectProductConfigLevelInfoByProjectCode(String smsProjectCode);
    
    int queryProjectProductConfigLevelInfoSizeByProjectCode(String smsProjectCode);
}
