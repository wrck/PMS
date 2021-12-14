/**
 * 
 */
package com.dp.plat.subcontract.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.json.annotations.JSON;
import org.springframework.beans.BeanUtils;

import com.dp.plat.action.BaseAction;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.subcontract.constant.SubcontractConstant;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractStatus;
import com.dp.plat.subcontract.constant.SubcontractConstant.SubcontractType;
import com.dp.plat.subcontract.constant.SubcontractConstant.TaskKey;
import com.dp.plat.subcontract.entity.SubcontractCallback;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractFacilitator;
import com.dp.plat.subcontract.entity.SubcontractLine;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.exception.SubcontractException;
import com.dp.plat.subcontract.service.SubcontractService;
import com.dp.plat.subcontract.vo.SubcontractComment;
import com.dp.plat.subcontract.vo.SubcontractDeliverVO;
import com.dp.plat.subcontract.vo.SubcontractPageParam;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;
import com.dp.plat.util.Base64Util;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopMark;
import com.dp.plat.util.PmClosedLoopMarkFactory;
import com.dp.plat.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.Preparable;

/**
 * @author w02611
 *
 */
public class SubcontractAction extends BaseAction implements Preparable {
	private static final long serialVersionUID = 5839063081482766839L;

	private SubcontractService subcontractService;
	private DepartmentManageService departmentManageService;
	private BasicDataService basicDataService;
	private PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService;
	private CallBackService callBackService;
	private PmClosedLoopService pmClosedLoopService;

	// 基数数据
	private List<BasicDataBean> typeList;
	private List<Department> depList;
	private List<Department> profitDepList;
	private List<SubcontractFacilitator> facilitatorList;
	private List<BasicDataBean> stateList;
	private List<BasicDataBean> callbackStateList;
	private User user;

	// 主数据
	private SubcontractProject subcontract;
	private SubcontractProjectVO subcontractVO;
	private List<SubcontractProject> subcontractList;
	private List<SubcontractProjectVO> subcontractVOList;

	// 明细数据
	private List<SubcontractLine> subcontractLineList;
	private List<SubcontractPayment> subcontractPaymentList;
	private List<SubcontractDeliverVO> subcontractDeliverList;
	private List<Map<String, Object>> engineeFeeList;
	private List<Map<String, Object>> subcontractCommentList;
	private List<SubcontractPrice> subcontractPriceList;

	// 辅助参数
	private List<Project> projectList;
	private List<ShipmentInfo> shipmentInfoList;
	private String contractNos;
	private String projectIds;
	private File[] uploadFiles;
	private String[] deliverNames;
	private String[] deliverTypes;
	private List<SubcontractDeliverVO> uploadDeliverList;
	private SubcontractFacilitator subcontractFacilitator;
	private SubcontractDeliverVO subcontractDeliverVO;
	/**
	 * 选择的序列号
	 */
	private String[] selected;
	private SubcontractComment subcontractComment;
	private WorkflowCommonParam workflowCommonParam;
	private Integer[] delIds;
	private SubcontractDeliver deliver;

	// 回访相关参数
	private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;
	private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;
	private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;
	private SubcontractCallback subcontractCallback;
	private List<BasicDataBean> quesTypeList;
	private PmClQuesnaireResultLine pmClQuesnaireResultLine;
	private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;
	private List<String> quesResultMarkList;
	private List<PmClosedLoopQuesnaireLine> pmClosedLoopQuesnaireLineList;
	private List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList;

	// 返回结果
	private String result;
	private String redirect;
	private int tabIndex;
	private String autoCheckProjects;
	private String namespace;

	// 分页参数
	private DisplayParam displayParam;

	@Override
	public void prepare() throws Exception {
		HttpServletRequest request = getServletRequest();
		String referer = request.getHeader("Referer");
		if (StringUtils.isNotBlank(referer)) {
			URL refererUrl = new URL(referer);
//			if (refererUrl.getHost().equals(request.getRemoteHost())) {
				referer = refererUrl.getPath().replace(request.getContextPath(), "");
				namespace = referer.substring(0, referer.lastIndexOf("/"));
//			}
		}
		if (namespace == null) {
			ActionMapping actionMapping = (ActionMapping) request.getAttribute("struts.actionMapping");
			namespace = actionMapping.getNamespace();
		}
		if (namespace.startsWith("/")) {
			namespace = namespace.substring(1, namespace.length());
		}
		if (!namespace.startsWith("module")) {
			namespace = "module";
		}
		user = UserContext.getUserContext().getUser();
	}
	
	/**
	 * 快速查看，如果返回结果为1，则直接进入具体详情页，否则返回列表
	 * @return
	 * @throws Exception
	 */
	public String view() throws Exception {
		String view = list();
		if (subcontractVOList.size() == 1) {
			subcontract = subcontractVOList.get(0);
			view = input();
		} 
		return view;
	}

	public String list() throws Exception {
		if (!((user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) || user.isHasRole(MessageUtil.ROLE_ADMIN)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
				|| user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)
				|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_AREA_LEADER))) {
			setErrmsg("没有访问权限！");
			return ERROR;
		}
		if (subcontractVO == null) {
			subcontractVO = new SubcontractProjectVO();
		}
		if (!(user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
				|| user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)
				|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER))) {
			subcontractVO.setAreaPower(user.getAreapower());
		}
		stateList = basicDataService.queryBasicDataBeanAll(SubcontractConstant.SUBCONTRACT_STATE_KEY);
		callbackStateList = basicDataService.queryBasicDataBeanAll(SubcontractConstant.SUBCONTRACT_CALLBACK_STATE_KEY);
		typeList = basicDataService.queryBasicDataBeanAll(SubcontractConstant.SUBCONTRACT_TYPE_KEY);
		Department department = new Department();
		department.setDepartmentNum("16");// 市场部门
		depList = departmentManageService.queryAllDepartments(department);
		department.setDepartmentNum("31");// 用服部门
		List<Department> tmpList = departmentManageService.queryAllDepartments(department);
		depList.addAll(tmpList);
		depList = processDepartment(depList);

		// department.setIsparam(1);
		department.setDepartmentNum("31");// 用服部门
		profitDepList = departmentManageService.queryAllDepartments(department);
		department.setDepartmentNum("16");// 市场部门
		tmpList = departmentManageService.queryAllDepartments(department);
		profitDepList.addAll(tmpList);
		profitDepList = processDepartment(profitDepList);

		if (displayParam == null) {
			displayParam = new DisplayParam();
		}
		String export = getServletRequest().getParameter("6578706f7274");
		if ("1".equals(export)) {
			subcontractVOList = subcontractService.querySubcontractExportData(subcontractVO);
			displayParam.setPagesize(subcontractVOList.size());
			displayParam.setTotalcount(subcontractVOList.size());
		} else {
			SubcontractPageParam pageParam = new SubcontractPageParam();
			BeanUtils.copyProperties(subcontractVO, pageParam);
			pageParam.setDisplayParam(displayParam);
			subcontractVOList = subcontractService.selectSubcontractProjectVOListPageable(pageParam);
		}
		return "list";
	}
	
	/**
	 * 新建
	 */
	@Override
	public String input() throws Exception {
		if (!((user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) || user.isHasRole(MessageUtil.ROLE_ADMIN)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
				|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
				|| user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)
				|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER) || user.isHasRole(MessageUtil.ROLE_AREA_LEADER))) {
			setErrmsg("没有访问权限！");
			return ERROR;
		}
		if (subcontract == null || subcontract.getId() == null) {
			if (subcontract == null) {
				subcontract = new SubcontractProject();
			}
			// subcontractLineList = new ArrayList<>();
			// subcontractDeliverList = new ArrayList<>();
		} else {
			subcontract = subcontractService.selectSubcontractProjectVOById(subcontract.getId());
			if (!(((user.getAreapower().contains(subcontract.getProfitDepCode())
					|| user.getAreapower().contains(subcontract.getProfitDepCode2Office())
					|| user.getAreapower().contains(subcontract.getOfficeCode()))
					&& (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)
							|| user.isHasRole(MessageUtil.ROLE_AREA_LEADER)))
					|| user.isHasRole(MessageUtil.ROLE_ADMIN) || user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
					|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
					|| user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)
					|| user.isHasRole(MessageUtil.ROLE_CALLBACKPER))) {
				setErrmsg("没有访问权限！");
				return ERROR;
			}
			projectList = subcontractService.queryProjectList(subcontract);
			// SubcontractLine subcontractLine = new
			// SubcontractLine(subcontract.getId());
			// subcontractLineList =
			// subcontractService.selectSubcontractLineList(subcontractLine);
			// SubcontractDeliver deliver = new
			// SubcontractDeliver(subcontract.getId());
			// subcontractDeliverList =
			// subcontractService.selectSubcontractDeliverVOList(deliver);

			if (tabIndex == 0) {
				if (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)
						|| user.isHasRole(MessageUtil.ROLE_AREA_LEADER)) {
					tabIndex = 1;
				} else if (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)) {
					tabIndex = 2;
				} else if (user.isHasRole(MessageUtil.ROLE_FINANCIAL_STAFF)) {
					tabIndex = 3;
				} else if (user.isHasRole(MessageUtil.ROLE_CALLBACKPER)) {
					tabIndex = 4;
				} else if (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) && user.getAreapower() != null
						&& ((subcontract.getProfitDepCode() != null
								&& user.getAreapower().contains(subcontract.getProfitDepCode()))
								|| (subcontract.getProfitDepCode2Office() != null
										&& user.getAreapower().contains(subcontract.getProfitDepCode2Office())))
						&& subcontract.getState() != null && subcontract.getState() == SubcontractStatus.APPLY) {
					tabIndex = 5;
				}
			}
		}
		typeList = basicDataService.queryBasicDataBeanAll(SubcontractConstant.SUBCONTRACT_TYPE_KEY);
		Department department = new Department();
		department.setDepartmentNum("16");// 市场部门
		depList = departmentManageService.queryAllDepartments(department);
		department.setDepartmentNum("31");// 用服部门
		List<Department> tmpList = departmentManageService.queryAllDepartments(department);
		depList.addAll(tmpList);
		depList = processDepartment(depList);

		// department.setIsparam(1);
		department.setDepartmentNum("31");// 用服部门
		profitDepList = departmentManageService.queryAllDepartments(department);
		department.setDepartmentNum("16");// 市场部门
		tmpList = departmentManageService.queryAllDepartments(department);
		profitDepList.addAll(tmpList);
		profitDepList = processDepartment(profitDepList);

		autoCheckProjects = basicDataService.querySysArg("subcontract.autocheck.projects");
		return INPUT;
	}

	/**
	 * 创建
	 * 
	 */
	public String create() {
		if (subcontract.getId() == null) {
			subcontractService.createSubcontractProject(subcontract, uploadDeliverList, selected);
		} else {
			subcontractService.updateSubcontractProject(subcontract, uploadDeliverList, selected);
		}
		boolean callbackFlag = subcontractService.saveDeliverFiles(subcontract.getId(), uploadDeliverList);
		if (callbackFlag) {
			try {
				subcontractService.startCallBackFlow(subcontract.getId());
			} catch (Exception e) {
				result = e.getMessage();
			}
		}
		// 办理付款任务
		if (workflowCommonParam != null && workflowCommonParam.getTaskId() != null
				&& workflowCommonParam.getFlag() == 1) {
			if (TaskKey.PROFIT_SERVICE_APPROVE.equals(workflowCommonParam.getOutcome())) {
				subcontractService.profitSerivceManagerFlow(workflowCommonParam, subcontract);
			}
		}
		return "create";
	}

	/**
	 * 发起转包申请
	 * 
	 */
	public String apply() {
		try {
			// 发起申请前先保存申请内容
			create();
			//
			WorkflowCommonParam workflowCommonParam = new WorkflowCommonParam();
			// subcontractService.startSubcontractFlow(workflowCommonParam,
			// null, subcontract);
			subcontractService.startSubcontractFlow(subcontract);
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "create";
	}

	/**
	 * 审批转包申请，录入转包价
	 * 
	 */
	public String audit() {
		try {
			if (workflowCommonParam != null && workflowCommonParam.getTaskId() != null && subcontract != null
					&& subcontract.getId() != null) {
				if (SubcontractConstant.TASK_KEY_APPROVE.equals(workflowCommonParam.getOutcome())) {
					subcontractService.auditSubcontractFlow(workflowCommonParam, subcontract, subcontractPriceList);
				} else if (SubcontractConstant.TASK_KEY_ZR_APPROVE.equals(workflowCommonParam.getOutcome())) {
					subcontractService.approveSubcontractFlow(workflowCommonParam, subcontract);
				}
			}

			// if (subcontractComment != null && subcontractComment.getTaskId()
			// != null && subcontract != null
			// && subcontract.getId() != null) {
			// subcontractService.auditSubcontractFlow(subcontractComment,
			// subcontract);
			// }
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "audit";
	}

	/**
	 * 审批转包闭环
	 * 
	 */
	public String close() {
		try {
			if (workflowCommonParam != null && workflowCommonParam.getTaskId() != null && subcontract != null
					&& subcontract.getId() != null) {
				subcontractService.closeSubcontractFlow(workflowCommonParam, null, subcontract);
			}

			// if (subcontractComment != null && subcontractComment.getTaskId()
			// != null && subcontract != null
			// && subcontract.getId() != null) {
			// subcontractService.auditSubcontractFlow(subcontractComment,
			// subcontract);
			// }
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "redirect";
	}

	/**
	 * 回访问卷
	 * 
	 */
	public String querySubcontractCallback() {
		try {
			// 审批
			if (workflowCommonParam != null && workflowCommonParam.getInstId() != null) {
				// subcontractService.submitCallBackFlow(workflowCommonParam,
				// subcontractCallback);
				subcontractService.submitCallBackFlow2(workflowCommonParam, subcontractCallback);
				subcontract = new SubcontractProject();
				subcontract.setId(subcontractCallback.getSubcontractId());
				// return "redirect";
			}

			// 问卷提交
			if (pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0) {
				if (pmClQuesnaireResultHeader.getStatus() == 1) {// 已提交，计算分数
					queryQuesnaireScore();
				}
				// 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
				subcontractService.insertSubcontractQuesnaire(subcontractCallback, pmClQuesnaireResultHeader,
						pmClQuesnaireResultLineList);
				// return SUCCESS;
			}

			workflowCommonParam = subcontractService
					.queryCurrentWorkFlowCommonParam(subcontractCallback.getSubcontractId(), TaskKey.CALLBACK);
			subcontractCallback = subcontractService.selectMaxSubcontractCallback(subcontractCallback);

			if (workflowCommonParam != null && subcontractCallback != null) {
				subcontractCallback.setTaskId(workflowCommonParam.getTaskId());
			}
			// 获取生效的问卷分类
			findPmClosedLoopQuesnaireList();
			// 获取问卷模板的内容或者已填写的问卷内容
			if ((pmClosedLoopQuesnaire != null && pmClosedLoopQuesnaire.getId() != 0)
					|| (subcontractCallback != null && subcontractCallback.getQuesnaireId() != null
							&& !Integer.valueOf(0).equals(subcontractCallback.getQuesnaireId()))) {
				getCbForm(subcontractCallback.getQuesnaireId());
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "callback";

	}

	/**
	 * 查询转包项目列表
	 * 
	 */
	public String chooseSubcontractProject() {
		try {
			if (StringUtils.isNotBlank(contractNos)) {
				Project project = new Project();
				// project.setContractNo(Util.appendChar(contractNos, "'"));
				project.setContractNo(contractNos);
				projectList = subcontractService.queryProjectList(project);
			} else {
				projectList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询发货序列号
	 * 
	 */
	public String chooseShipmentInfo() {
		try {
			if (StringUtils.isNotBlank(contractNos)) {
//				shipmentInfoList = subcontractService.queryShipmentinfoByContractNosAndProjectIds(
//						Util.appendChar(contractNos, "'"), Util.appendChar(projectIds, "'"));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("contractNos", Util.appendChar(contractNos, "'"));
				params.put("projectIds", Util.appendChar(projectIds, "'"));
				if (selected != null && selected[0] != null) {
					try {
						List<Map> contractProfitCenter = com.alibaba.fastjson.JSON.parseArray(selected[0], Map.class);
						params.put("contractProfitCenter", contractProfitCenter);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				shipmentInfoList = subcontractService.queryShipmentinfoByContractNosAndProjectIds(params);
			} else {
				shipmentInfoList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询项目转包设备清单
	 * 
	 */
	public String querySubcontractLine() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				SubcontractLine subcontractLine = new SubcontractLine(subcontract.getId());
				subcontractLineList = subcontractService.selectSubcontractLineList(subcontractLine);
			} else {
				subcontractLineList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询项目转包附件列表
	 * 
	 */
	public String querySubcontractDeliver() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				SubcontractDeliver deliver = new SubcontractDeliver(subcontract.getId());
				deliver.setEffectiveTo(new Date());
				subcontractDeliverList = subcontractService.selectSubcontractDeliverVOList(deliver);
			} else {
				subcontractDeliverList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String deleteSubcontractDeliver() {
		try {
			if (subcontractDeliverVO != null && subcontractDeliverVO.getIds() != null) {
				subcontractService.deleteSubcontractDeliver(subcontractDeliverVO);
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 检查转包名是否存在
	 * 
	 */
	public String checkSubcontractName() {
		try {
			if (subcontract != null && StringUtils.isNotBlank(subcontract.getSubcontractName())) {
				result = subcontractService.checkSubcontractName(subcontract);
			} else {
				result = "0";
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询转包项目合同工程服务费
	 * 
	 */
	public String queryContractNoEngineeFee() {
		try {
			Integer subcontractId = null;
			if (subcontract != null && subcontract.getId() != null) {
				subcontractId = subcontract.getId();
				workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
						SubcontractConstant.TASK_KEY_APPROVE + ";" + SubcontractConstant.TASK_KEY_ZR_APPROVE);
			}
			if (StringUtils.isNotBlank(contractNos)) {
				subcontractPriceList = subcontractService.queryContractNoEngineeFeeWithSubPrice(contractNos,
						subcontractId);
				// engineeFeeList =
				// subcontractService.queryContractNoEngineeFee(contractNos);
			} else {
				// engineeFeeList = new ArrayList<>();
				subcontractPriceList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询转包项目付款信息
	 * 
	 */
	public String querySubcontractPayment() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				SubcontractPayment payment = new SubcontractPayment();
				payment.setSubcontractId(subcontract.getId());
				subcontractPaymentList = subcontractService.selectSubcontractPaymentList(payment);
				// workflowCommonParam =
				// subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
				// SubcontractConstant.TASK_KEY_CLOSE);
				workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
						TaskKey.GENERATE_CONTRACT);

				if (workflowCommonParam == null) {
					HashMap<String, Object> params = new HashMap<>();
					params.put("checkOffice", "true");
					workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
							TaskKey.APPLY_PAYMENT, "smRole", params);

					// 维护类的判断是否已经上传服务单
					if (Integer.valueOf(SubcontractType.MAINTENANCE).equals(subcontract.getType())) {
						SubcontractDeliver deliver = new SubcontractDeliver();
						deliver.setSubcontractId(subcontract.getId());
						deliver.setType("1"); // 服务单
						List<SubcontractDeliver> deliverList = subcontractService.selectSubcontractDeliverList(deliver);
						if (deliverList.isEmpty()) {
							// 用来标记是否允许提交任务，主要维护类的必须上传服务单之后才能提交付款申请，返回消息。
							result = "维护类项目，请先上传服务单！";
						}
					}
				}
				if (workflowCommonParam == null) {
					workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
							TaskKey.APPROVE_PAYMENT);
				}
			} else {
				subcontractPaymentList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 保存转包项目付款信息
	 */
	public String savePayment() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				if (user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
						|| user.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER_LEADER)) {
					create();
					subcontractService.saveSubcontractPayment(subcontractPaymentList, delIds);
				}
				// 办理付款任务
				if (workflowCommonParam != null && workflowCommonParam.getTaskId() != null
						&& workflowCommonParam.getFlag() == 1) {
					if (TaskKey.GENERATE_CONTRACT.equals(workflowCommonParam.getOutcome())) {
						subcontractService.generateContractFlow(workflowCommonParam, subcontract);
					} else if (TaskKey.APPROVE_PAYMENT.equals(workflowCommonParam.getOutcome())) {
						subcontractService.approvePaymentFlow(workflowCommonParam, subcontract);
					} else if (TaskKey.PROFIT_SERVICE_APPROVE.equals(workflowCommonParam.getOutcome())) {
						subcontractService.profitSerivceManagerFlow(workflowCommonParam, subcontract);
					} else {
						subcontractService.applyPaymentFlow(workflowCommonParam, subcontract);
					}
				}
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 终止流程
	 * 
	 * @return
	 */
	public String terminateWorkFlow() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				subcontractService.terminateWorkFlow(subcontract.getId(), workflowCommonParam.getComment());
				result = "1";
			}
		} catch (SubcontractException e) {
			result = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询项目转包审批记录
	 * 
	 */
	public String querySubcontractComment() {
		try {
			if (subcontract != null && subcontract.getId() != null) {
				subcontractCommentList = subcontractService.querySubcontractCommentList(subcontract.getId());

				if (user.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) && user.getAreapower() != null
						&& ((subcontract.getProfitDepCode() != null
								&& user.getAreapower().contains(subcontract.getProfitDepCode()))
								|| (subcontract.getProfitDepCode2Office() != null
										&& user.getAreapower().contains(subcontract.getProfitDepCode2Office())))
						&& subcontract.getState() != null
						&& subcontract.getState() < SubcontractStatus.PROFIT_SM_AGREE) {
					HashMap<String, Object> params = new HashMap<>();
					params.put("checkProfitDep", "true");
					workflowCommonParam = subcontractService.queryCurrentWorkFlowCommonParam(subcontract.getId(),
							TaskKey.PROFIT_SERVICE_APPROVE, "profitSmRole", params);
				}
			} else {
				subcontractCommentList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * ajax查询服务商信息，返回JSON
	 * 
	 */
	public String queryFacilitator() {
		try {
			SubcontractFacilitator subcontractFacilitator = new SubcontractFacilitator();
			subcontractFacilitator.setState(true);
			facilitatorList = subcontractService.selectSubcontractFacilitatorList(subcontractFacilitator);
			result = new ObjectMapper().writeValueAsString(facilitatorList);
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 查询转包记录，供项目管理调用
	 * 
	 * @return
	 */
	public String querySubcontractInfoForProject() {
		try {
			if (subcontractVO != null && subcontractVO.getProjectId() != null) {
				// subcontractVOList =
				// subcontractService.querySubcontractInfoForProject(subcontractVO.getProjectId());
				subcontractVOList = subcontractService.selectSubcontractProjectVOList(subcontractVO);
			} else {
				subcontractVOList = new ArrayList<>();
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 服务商查询列表
	 * 
	 * @return
	 */
	public String facilitatorList() {
		facilitatorList = subcontractService.selectSubcontractFacilitatorList(subcontractFacilitator);
		return "facilitatorList";
	}

	/**
	 * 服务商查询列表
	 * 
	 * @return
	 */
	public String facilitatorEdit() {
		if ("GET".equals(getServletRequest().getMethod())) {
			if (subcontractFacilitator != null && subcontractFacilitator.getId() != null) {
				subcontractFacilitator = subcontractService
						.selectSubcontractFacilitatorById(subcontractFacilitator.getId());
			}
		} else if ("POST".equals(getServletRequest().getMethod())) {
			if (subcontractFacilitator.getId() != null) {
				subcontractService.updateSubcontractFacilitatorByIdSelective(subcontractFacilitator);
			} else {
				subcontractService.insertSubcontractFacilitator(subcontractFacilitator);
			}
		}
		return "facilitatorEdit";
	}

	/**
	 * 下载附件
	 */
	public String downloadFile() {
		try {
			if (StringUtils.isNotBlank(redirect)) {
				Integer deliverId = Integer.valueOf((String) Base64Util.decodeBase64(redirect));
				deliver = subcontractService.selectSubcontractDeliverById(deliverId);
			}
		} catch (SubcontractException e) {
			setErrmsg(e.getMessage());
			return ERROR;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "download";
	}

	@JSON(serialize = false)
	public String getDownloadFile() {
		ServletActionContext.getResponse().setHeader("charset", "ISO8859-1");
		try {
			if (deliver != null) {
				if (StringUtils.isBlank(result)) {
					return new String(deliver.getFileName().getBytes(), "ISO8859-1");
				} else {
					return URLEncoder.encode(deliver.getFileName(), "ISO8859-1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@JSON(serialize = false)
	public InputStream getFileStream() throws FileNotFoundException, UnsupportedEncodingException {
		if (deliver != null) {
			InputStream in = ServletActionContext.getServletContext().getResourceAsStream(deliver.getFilePath());
			if (null == in) {
				java.lang.System.out.println(
						"Can not find a java.io.InputStream with the name [inputStream] in the invocation stack. Check the <param name=\"inputName\"> tag specified for this action.检查action中文件下载路径是否正确.");
			}
			return in;
		}
		return null;
	}

	/**
	 * 检查是否需要计算问卷分数，并进行计算
	 */
	private void queryQuesnaireScore() {
		Map<Integer, PmClosedLoopQuesnaireOpt> optMap = queryQuesnaireOpt();
		queryPmClosedLoopQuesnaire();
		quesMark(pmClosedLoopQuesnaire, optMap, pmClQuesnaireResultLineList, pmClQuesnaireResultHeader);
	}

	private void queryPmClosedLoopQuesnaire() {
		pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
		pmClosedLoopQuesnaire.setId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
		pmClosedLoopQuesnaire = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, null)
				.get(0);
	}

	private int quesMark(PmClosedLoopQuesnaire quesObj, Map<Integer, PmClosedLoopQuesnaireOpt> optMap,
			List<PmClQuesnaireResultLine> resultLineListObj, PmClQuesnaireResultHeader resultHeaderObj) {
		double totalScore = 0;
		StringBuilder quesAnwBuilder = new StringBuilder();
		String quesTypeForCB = resultLineListObj.get(0).getQuesTypeForCB();
		quesAnwBuilder.append(quesTypeForCB + ":");
		StringBuilder evaResultBuilder = new StringBuilder();
		int i = 0;
		for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : resultLineListObj) {
			if (pmClQuesnaireResultLineObj == null) {
				return -1;
			}
			// 总分计算与答案字符串拼接
			if (pmClQuesnaireResultLineObj.getQuestionTemplateOptId() != 0) {
				if (optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()) == null) {
					return -1;
				}
				if (!(quesTypeForCB.equals(pmClQuesnaireResultLineObj.getQuesTypeForCB()))) {
					quesAnwBuilder.append(";");
					quesAnwBuilder.append(pmClQuesnaireResultLineObj.getQuesTypeForCB() + ":");
				}
				quesTypeForCB = pmClQuesnaireResultLineObj.getQuesTypeForCB();

				char opt = (char) ((((int) 'A') - 1)
						+ optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionNum());
				quesAnwBuilder.append(i + "-" + pmClQuesnaireResultLineObj.getQuesTemplateLineNum() + "|" + opt + ","); // 10:1-2|C
																														// (10
																														// 题目回访类型，1
																														// 下表，
																														// 2
																														// 题号，
																														// C
																														// 选项)
				pmClQuesnaireResultLineObj.setQuestionScore(
						optMap.get(pmClQuesnaireResultLineObj.getQuestionTemplateOptId()).getQuestionOptionScore());
				totalScore += pmClQuesnaireResultLineObj.getQuestionScore();
			}
			i++;
		}
		quesAnwBuilder.append(";");

		resultHeaderObj.setQuesMarkScore(totalScore);
		resultHeaderObj.setQuesAnw(quesAnwBuilder.toString());

		// 获取计分规则并计分
		if (quesObj.getMarkIndexs() != null && !(quesObj.getMarkIndexs().equals(""))) {
			PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
			if (factory.getMarks(quesObj.getMarkIndexs()) != null) {
				for (PmClosedLoopMark pmClosedLoopMarkObj : factory.getMarks(quesObj.getMarkIndexs())) {
					String evaResultObj = pmClosedLoopMarkObj.quesMark(resultHeaderObj);
					if (evaResultObj.equals("-2")) {
						return -1;
					} else if (evaResultObj.equals("pass")) {
						evaResultObj = "1";
					} else if (!evaResultObj.equals("-1")) {
						if (evaResultObj.contains(",")) {
							for (String optIndex : evaResultObj.split(",")) {
								resultLineListObj.get(Integer.parseInt(optIndex)).setQuesEvaResult(-1);
							}
						} else {
							resultLineListObj.get(Integer.parseInt(evaResultObj)).setQuesEvaResult(-1);
						}
						evaResultObj = "-1";
					} else {

					}
					evaResultBuilder.append(evaResultObj);
				}
			}
		}
		if (evaResultBuilder.length() > 0
				&& evaResultBuilder.toString().contains(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT + "")) {
			resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_REJECT);

		} else {
			resultHeaderObj.setQuesMarkResult(PmClosedLoopConstant.CL_EVALU_RESULT_AGREE);
		}
		return 1;
	}

	private Map<Integer, PmClosedLoopQuesnaireOpt> queryQuesnaireOpt() {
		PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt = new PmClosedLoopQuesnaireOpt();
		pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClQuesnaireResultHeader.getQuesnaireTemplateHeaderId());
		pmClosedLoopQuesnaireOpt.setQuestionId(0);
		Map<Integer, PmClosedLoopQuesnaireOpt> optMap = pmClosedLoopQuesnaireService
				.queryPmClosedLoopQuesnaireOptMap(pmClosedLoopQuesnaireOpt);
		return optMap;
	}

	// 只获取生效的问卷
	private void findPmClosedLoopQuesnaireList() {
		PmClosedLoopQuesnaire quesObj = new PmClosedLoopQuesnaire();
		quesObj.setQuestionnaireStatus(PmClosedLoopConstant.CL_STATUS_SUBMIT);
		pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, null);
	}

	private void getCbForm(Integer quesnaireId) {
		if (quesnaireId != null && quesnaireId != 0) {
			// 2.复制给pmClosedLoopQuesnaire传递需要的问卷模板信息
			int templateId = callBackService.queryQuesnaireTemplateId(quesnaireId);

			if (pmClosedLoopQuesnaire == null) {
				pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
				pmClosedLoopQuesnaire.setId(templateId);
			}
			// 3.判断选择的问卷模板是否等于已有草稿问卷的模板，等于则获取问卷结果行信息
			if (templateId == pmClosedLoopQuesnaire.getId()) {
				pmClQuesnaireResultLine = new PmClQuesnaireResultLine();
				pmClQuesnaireResultLine.setQuesnaireResultHeaderId(quesnaireId);
				pmClQuesnaireResultLineList = pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
			}

			// 问卷状态 已提交 1 草稿-1
			if (subcontractCallback.getQuesnaireState() != -1) {
				// 获取问卷结果信息
				quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); // 获取问题类型
				quesResultMarkList = getQuesTypeScore(pmClQuesnaireResultLineList);

				// 获取总分以及是否通过
				if (pmClQuesnaireResultHeader == null) {
					pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
				}
				pmClQuesnaireResultHeader.setId(subcontractCallback.getQuesnaireId());
				pmClQuesnaireResultHeader = pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader)
						.get(0);
			}
		}

		// 1.获取问卷模板头信息
		pmClosedLoopQuesnaire = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, null)
				.get(0);
		// 获取评分规则说明
		PmClosedLoopMarkFactory factory = new PmClosedLoopMarkFactory();
		pmClosedLoopQuesnaire.setMarkList(factory.getMarks(pmClosedLoopQuesnaire.getMarkIndexs()));

		// 2.获取问卷模板行信息
		PmClosedLoopQuesnaireLine pmClosedLoopQuesnaireLine = new PmClosedLoopQuesnaireLine();
		pmClosedLoopQuesnaireLine.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireLineList = pmClosedLoopQuesnaireService
				.queryPmClQuesnaireLineList(pmClosedLoopQuesnaireLine, "asc");

		// 3.获取问卷模板选项信息
		PmClosedLoopQuesnaireOpt pmClosedLoopQuesnaireOpt = new PmClosedLoopQuesnaireOpt();
		pmClosedLoopQuesnaireOpt.setQuesnaireTemplateHeaderId(pmClosedLoopQuesnaire.getId());
		pmClosedLoopQuesnaireOpt.setQuestionId(0);
		pmClosedLoopQuesnaireOptList = pmClosedLoopQuesnaireService
				.queryPmClosedLoopQuesnaireOptList(pmClosedLoopQuesnaireOpt, "asc");

	}

	/**
	 * 计算问卷结果
	 * 
	 * @param quesnaireResultLineListObj
	 * @return
	 */
	private List<String> getQuesTypeScore(List<PmClQuesnaireResultLine> quesnaireResultLineListObj) {
		Map<String, Double> quesTypeMarkMap = new HashMap<String, Double>();
		if (quesTypeList != null && quesnaireResultLineListObj != null) {
			List<String> quesResultMarkList = new ArrayList<String>();
			for (PmClQuesnaireResultLine pmClQuesnaireResultLineObj : quesnaireResultLineListObj) {
				double scoreObj = pmClQuesnaireResultLineObj.getQuestionScore();
				if (quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB()) != null) {
					scoreObj += quesTypeMarkMap.get(pmClQuesnaireResultLineObj.getQuesTypeForCB());
				}
				quesTypeMarkMap.put(pmClQuesnaireResultLineObj.getQuesTypeForCB(), scoreObj);
			}

			for (BasicDataBean basicDataBeanObj : quesTypeList) {
				if (quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId()) != null) {
					quesResultMarkList
							.add(basicDataBeanObj.getBasicDataName() + "|" + basicDataBeanObj.getBasicDataId());
					quesResultMarkList.add(quesTypeMarkMap.get(basicDataBeanObj.getBasicDataId()) + "");
				}
			}
			return quesResultMarkList;
		}
		return null;
	}

	/**
	 * 处理部门，市场部的部门名称增加“市场-”前缀
	 * 
	 * @param list
	 * @return list
	 */
	private List<Department> processDepartment(List<Department> list) {
		if (list != null && !list.isEmpty()) {
			for (Department department : list) {
				if (department != null) {
					String departmentNum = department.getDepartmentNum();
					if (StringUtils.isNotBlank(departmentNum)) {
						if (departmentNum.startsWith("16")) {
							department.setDepartmentName("市场-" + department.getDepartmentName());
						}
					}
				}
			}
		}
		return list;
	}

	public SubcontractService getSubcontractService() {
		return subcontractService;
	}

	public void setSubcontractService(SubcontractService subcontractService) {
		this.subcontractService = subcontractService;
	}

	public DepartmentManageService getDepartmentManageService() {
		return departmentManageService;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public BasicDataService getBasicDataService() {
		return basicDataService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public PmClosedLoopQuesnaireService getPmClosedLoopQuesnaireService() {
		return pmClosedLoopQuesnaireService;
	}

	public void setPmClosedLoopQuesnaireService(PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService) {
		this.pmClosedLoopQuesnaireService = pmClosedLoopQuesnaireService;
	}

	public CallBackService getCallBackService() {
		return callBackService;
	}

	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}

	public PmClosedLoopService getPmClosedLoopService() {
		return pmClosedLoopService;
	}

	public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}

	public List<BasicDataBean> getTypeList() {
		return typeList;
	}

	public void setTypeList(List<BasicDataBean> typeList) {
		this.typeList = typeList;
	}

	public List<Department> getDepList() {
		return depList;
	}

	public void setDepList(List<Department> depList) {
		this.depList = depList;
	}

	public List<Department> getProfitDepList() {
		return profitDepList;
	}

	public void setProfitDepList(List<Department> profitDepList) {
		this.profitDepList = profitDepList;
	}

	public List<SubcontractFacilitator> getFacilitatorList() {
		return facilitatorList;
	}

	public void setFacilitatorList(List<SubcontractFacilitator> facilitatorList) {
		this.facilitatorList = facilitatorList;
	}

	public List<BasicDataBean> getStateList() {
		return stateList;
	}

	public void setStateList(List<BasicDataBean> stateList) {
		this.stateList = stateList;
	}

	public List<BasicDataBean> getCallbackStateList() {
		return callbackStateList;
	}

	public void setCallbackStateList(List<BasicDataBean> callbackStateList) {
		this.callbackStateList = callbackStateList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SubcontractProject getSubcontract() {
		return subcontract;
	}

	public void setSubcontract(SubcontractProject subcontract) {
		this.subcontract = subcontract;
	}

	public SubcontractProjectVO getSubcontractVO() {
		return subcontractVO;
	}

	public void setSubcontractVO(SubcontractProjectVO subcontractVO) {
		this.subcontractVO = subcontractVO;
	}

	public List<SubcontractProject> getSubcontractList() {
		return subcontractList;
	}

	public void setSubcontractList(List<SubcontractProject> subcontractList) {
		this.subcontractList = subcontractList;
	}

	public List<SubcontractProjectVO> getSubcontractVOList() {
		return subcontractVOList;
	}

	public void setSubcontractVOList(List<SubcontractProjectVO> subcontractVOList) {
		this.subcontractVOList = subcontractVOList;
	}

	public List<SubcontractLine> getSubcontractLineList() {
		return subcontractLineList;
	}

	public void setSubcontractLineList(List<SubcontractLine> subcontractLineList) {
		this.subcontractLineList = subcontractLineList;
	}

	public List<SubcontractPayment> getSubcontractPaymentList() {
		return subcontractPaymentList;
	}

	public void setSubcontractPaymentList(List<SubcontractPayment> subcontractPaymentList) {
		this.subcontractPaymentList = subcontractPaymentList;
	}

	public List<SubcontractDeliverVO> getSubcontractDeliverList() {
		return subcontractDeliverList;
	}

	public void setSubcontractDeliverList(List<SubcontractDeliverVO> subcontractDeliverList) {
		this.subcontractDeliverList = subcontractDeliverList;
	}

	public List<Map<String, Object>> getEngineeFeeList() {
		return engineeFeeList;
	}

	public void setEngineeFeeList(List<Map<String, Object>> engineeFeeList) {
		this.engineeFeeList = engineeFeeList;
	}

	public List<Map<String, Object>> getSubcontractCommentList() {
		return subcontractCommentList;
	}

	public void setSubcontractCommentList(List<Map<String, Object>> subcontractCommentList) {
		this.subcontractCommentList = subcontractCommentList;
	}

	public List<SubcontractPrice> getSubcontractPriceList() {
		return subcontractPriceList;
	}

	public void setSubcontractPriceList(List<SubcontractPrice> subcontractPriceList) {
		this.subcontractPriceList = subcontractPriceList;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

	public List<ShipmentInfo> getShipmentInfoList() {
		return shipmentInfoList;
	}

	public void setShipmentInfoList(List<ShipmentInfo> shipmentInfoList) {
		this.shipmentInfoList = shipmentInfoList;
	}

	public String getContractNos() {
		return contractNos;
	}

	public void setContractNos(String contractNos) {
		this.contractNos = contractNos;
	}

	public String getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}

	public File[] getUploadFiles() {
		return uploadFiles;
	}

	public void setUploadFiles(File[] uploadFiles) {
		this.uploadFiles = uploadFiles;
	}

	public String[] getDeliverNames() {
		return deliverNames;
	}

	public void setDeliverNames(String[] deliverNames) {
		this.deliverNames = deliverNames;
	}

	public String[] getDeliverTypes() {
		return deliverTypes;
	}

	public void setDeliverTypes(String[] deliverTypes) {
		this.deliverTypes = deliverTypes;
	}

	public List<SubcontractDeliverVO> getUploadDeliverList() {
		return uploadDeliverList;
	}

	public void setUploadDeliverList(List<SubcontractDeliverVO> uploadDeliverList) {
		this.uploadDeliverList = uploadDeliverList;
	}

	public SubcontractFacilitator getSubcontractFacilitator() {
		return subcontractFacilitator;
	}

	public void setSubcontractFacilitator(SubcontractFacilitator subcontractFacilitator) {
		this.subcontractFacilitator = subcontractFacilitator;
	}
	
	public SubcontractDeliverVO getSubcontractDeliverVO() {
		return subcontractDeliverVO;
	}

	public void setSubcontractDeliverVO(SubcontractDeliverVO subcontractDeliverVO) {
		this.subcontractDeliverVO = subcontractDeliverVO;
	}

	public String[] getSelected() {
		return selected;
	}

	public void setSelected(String[] selected) {
		this.selected = selected;
	}

	public SubcontractComment getSubcontractComment() {
		return subcontractComment;
	}

	public void setSubcontractComment(SubcontractComment subcontractComment) {
		this.subcontractComment = subcontractComment;
	}

	public WorkflowCommonParam getWorkflowCommonParam() {
		return workflowCommonParam;
	}

	public void setWorkflowCommonParam(WorkflowCommonParam workflowCommonParam) {
		this.workflowCommonParam = workflowCommonParam;
	}

	public Integer[] getDelIds() {
		return delIds;
	}

	public void setDelIds(Integer[] delIds) {
		this.delIds = delIds;
	}

	public SubcontractDeliver getDeliver() {
		return deliver;
	}

	public void setDeliver(SubcontractDeliver deliver) {
		this.deliver = deliver;
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

	public PmClosedLoopQuesnaire getPmClosedLoopQuesnaire() {
		return pmClosedLoopQuesnaire;
	}

	public void setPmClosedLoopQuesnaire(PmClosedLoopQuesnaire pmClosedLoopQuesnaire) {
		this.pmClosedLoopQuesnaire = pmClosedLoopQuesnaire;
	}

	public SubcontractCallback getSubcontractCallback() {
		return subcontractCallback;
	}

	public void setSubcontractCallback(SubcontractCallback subcontractCallback) {
		this.subcontractCallback = subcontractCallback;
	}

	public List<BasicDataBean> getQuesTypeList() {
		return quesTypeList;
	}

	public void setQuesTypeList(List<BasicDataBean> quesTypeList) {
		this.quesTypeList = quesTypeList;
	}

	public PmClQuesnaireResultLine getPmClQuesnaireResultLine() {
		return pmClQuesnaireResultLine;
	}

	public void setPmClQuesnaireResultLine(PmClQuesnaireResultLine pmClQuesnaireResultLine) {
		this.pmClQuesnaireResultLine = pmClQuesnaireResultLine;
	}

	public List<PmClosedLoopQuesnaire> getPmClosedLoopQuesnaireList() {
		return pmClosedLoopQuesnaireList;
	}

	public void setPmClosedLoopQuesnaireList(List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList) {
		this.pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireList;
	}

	public List<String> getQuesResultMarkList() {
		return quesResultMarkList;
	}

	public void setQuesResultMarkList(List<String> quesResultMarkList) {
		this.quesResultMarkList = quesResultMarkList;
	}

	public List<PmClosedLoopQuesnaireLine> getPmClosedLoopQuesnaireLineList() {
		return pmClosedLoopQuesnaireLineList;
	}

	public void setPmClosedLoopQuesnaireLineList(List<PmClosedLoopQuesnaireLine> pmClosedLoopQuesnaireLineList) {
		this.pmClosedLoopQuesnaireLineList = pmClosedLoopQuesnaireLineList;
	}

	public List<PmClosedLoopQuesnaireOpt> getPmClosedLoopQuesnaireOptList() {
		return pmClosedLoopQuesnaireOptList;
	}

	public void setPmClosedLoopQuesnaireOptList(List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList) {
		this.pmClosedLoopQuesnaireOptList = pmClosedLoopQuesnaireOptList;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getAutoCheckProjects() {
		return autoCheckProjects;
	}

	public void setAutoCheckProjects(String autoCheckProjects) {
		this.autoCheckProjects = autoCheckProjects;
	}
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
