package com.dp.plat.prob.action;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dp.plat.action.BaseAction;
import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ReportLineData;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.report.EchartsUtil;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.prob.bean.Prob;
import com.dp.plat.prob.bean.ProbReadLog;
import com.dp.plat.prob.bean.ProbRestore;
import com.dp.plat.prob.bean.ProbRestoreWeekly;
import com.dp.plat.prob.bean.ProbStatistic;
import com.dp.plat.prob.bean.ProductComponent;
import com.dp.plat.prob.bean.SoftVersion;
import com.dp.plat.prob.param.ProbParam;
import com.dp.plat.prob.service.ProbManageService;
import com.dp.plat.prob.util.DisplayParamUtil;
import com.dp.plat.prob.util.ExportUtils;
import com.dp.plat.prob.util.SoftVersionUtil;
import com.dp.plat.prob.util.SoftVersionUtil.SoftVersionParser;
import com.dp.plat.prob.vo.ProductComponentPageParam;
import com.dp.plat.prob.vo.ProductComponentVO;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.util.DateUtil;
import com.dp.plat.util.ExceptionUtils;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.UploadFileUtil;
import com.dp.plat.util.Util;
import com.opensymphony.xwork2.Preparable;


/**
 * 技术公告管理 2016 -04- 14
 * 
 * @author j01441
 *
 */
public class ProbManageAction extends BaseAction implements Preparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProbManageService probManageService;
	private BasicDataService basicDataService;
	private DepartmentManageService departmentManageService;
	private List<BasicDataBean> watchList;// 跟踪
	private List<BasicDataBean> statusList;// 状态
	private List<BasicDataBean> priorityList;// 严重级别
	private List<BasicDataBean> navTabList;// 选项卡
	private int isContinue;// 判断是否继续
	private Prob prob;
	private DisplayParam displayParam;
	private List<Prob> probList;
	private List<SoftVersion> softVersionList;// 软件版本集合
	private SoftVersion softVersion;
	private String softVersionCodes;
	private List<ProbRestore> probRestoreList;// 受技术公告影响的设备数据
	private List<ProbRestore> probRestoreTaskList;// 修复任务数据集合
	private List<ProbRestore> probRestoreBackingTaskList;// 修复任务数据集合
	private List<ProbRestoreWeekly> weeklyList;// 进展周报集合
	private DisplayParam restoreDisplayParam;
	private ProbRestore probRestore;// 设备修复数据对象
	private List<Department> departmentList;// 办事处集合
	private List<BasicDataBean> restoreStatuList;// 技术公告修复状态集合
	private String restoreIds;// 要操作的子任务ID串
	// 文件上传
	private File[] upload;
	private String uploadFileName;
	private String seq = File.separator;
//	public String UPLOAD_PATH = "upload" + seq + "file" + seq + "prob";
	public String UPLOAD_PATH = UploadFileUtil.UPLOAD_PATH + seq + "file" + seq + "prob";
	private Map<Integer, String> fileMap;
	// 弹出窗口
	private String redirect;
	// 判断是否是技术公告员身份
	private int isProbAdmin;

	private User user;
	/**
	 * ajax返回结果
	 */
	private String result;
	private boolean firstCheck;
	/**
	 * 批量删除子任务时传值
	 */
	private String probRestoreIds;
	/**
	 * 技术公告统计表
	 */
	private ProbStatistic probStatistic;
	private List<ProbStatistic> probStatisticList;
	private List<Project> probProjectList;
	private ProbReadLog probReadLog;
	private List<ProbReadLog> readLogList;
	
	/**
	 * 产品组件
	 */
	private ProductComponentVO productComponent;
	
	/**
	 * 通用列表
	 */
	private List<? extends Object> commonList;
	/**
	 * 通用参数
	 */
	private Map<String, ? extends Object> commonMap;

	@Override
	public void prepare() throws Exception {
		user = UserContext.getUserContext().getUser();
		/*
		 * if(currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN)){ isProbAdmin =
		 * 1; }else if(currectUser.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)){
		 * isProbAdmin = 2; }else
		 * if(currectUser.isHasRole(MessageUtil.ROLE_PROB_RD)){ isProbAdmin = 3;
		 * }else{ isProbAdmin = 0; }
		 */
		// isProbAdmin = currectUser.isHasRole(MessageUtil.ROLE_PROB_ADMIN) ? 1
		// : 0;
	}

	/**
	 * 技术公告列表管理
	 * 
	 * @return
	 */
	public String list() {
		try {
			// 准备查询条件需要的数据集合
			// priorityList = basicDataService.queryBasicDataBeans("32");
			watchList = basicDataService.queryBasicDataBeans("30");
			statusList = basicDataService.queryBasicDataBeans("31");

			if (displayParam == null) {
				displayParam = new DisplayParam();
				displayParam.setColmap(DisplayParamUtil.initProbColMap());
			}
			if (prob == null) {
				prob = new Prob();
			}
//			if (prob != null && prob.getProbId() != 0) {
//				prob = new Prob();
//			}
			displayParam.getParam();
			probList = probManageService.queryProbList(prob, displayParam);
		} catch (Exception e) {
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "list";
	}

	/**
	 * 进入创建页面
	 */
	public String input() {
		watchList = basicDataService.queryBasicDataBeans("30");
		statusList = basicDataService.queryBasicDataBeans("31");
		priorityList = basicDataService.queryBasicDataBeans("32");

		if (prob == null || prob.getProbId() == 0) {
			// 查询公告编码
			// String probNum = probManageService.queryNextProbNum();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
			String probNum = "SP." + dateFormat.format(new Date());
			prob = new Prob();
			prob.setProbNum(probNum);
			prob.setStatus("1");
		} else { // 有传入参数，查询对象信息
			// .1查询主题信息
			prob = probManageService.queryOneProb(prob);
			// 查询附件名称
			fileMap = probManageService.queryProbFileMap(prob.getProbId());
			// .3查询受影响的软件版本信息
			softVersionList = probManageService.querySoftVersionList(prob.getProbId());
			prob.setAffectedVersion(JSON.toJSONString(softVersionList));
			// }
		}
		return INPUT;
	}

	/**
	 * 删除技术公告
	 * 
	 * @return
	 */
	public String delete() {
		probManageService.deleteProbInfo(prob.getProbId());
		return SUCCESS;
	}

	/**
	 * 批量删除子任务
	 * 
	 * @return
	 */
	public String bacthDeleteProbRestores() {
		try {
			probManageService.bacthDeleteProbRestores(probRestoreIds);
			result = "200";
		} catch (Exception e) {
			result = JSON.toJSONString(e);
			return SUCCESS;
		}
		return SUCCESS;
	}

	/**
	 * 进入修改查看页面
	 */
	public String edit() {
		try {
			long t = System.currentTimeMillis();
			watchList = basicDataService.queryBasicDataBeans("30");
			statusList = basicDataService.queryBasicDataBeans("31");
			priorityList = basicDataService.queryBasicDataBeans("32");
			// 办事处集合
			departmentList = departmentManageService.queryDepartments();
			restoreStatuList = basicDataService.queryBasicDataBeans("33");
			if (prob != null && prob.getProbId() != 0) {// 有传入参数，查询对象信息
				prob.setReader(UserContext.getUserContext().getUsername());
				// .1查询主题信息
				prob = probManageService.queryOneProb(prob);
				// 查询附件名称
				fileMap = probManageService.queryProbFileMap(prob.getProbId());
				// .2查询检索的软件版本信息
				// 如果session中有值的话，将其更新到数据库中
				// HttpSession session = getServletRequest().getSession();
				// if( session.getAttribute("softVersionList") != null){
				// softVersionList = (List<SoftVersion>)
				// session.getAttribute("softVersionList") ;
				// probManageService.updateProbSoftVersion(softVersionList ,
				// prob.getProbId());
				// }else {
				// .3查询受影响的软件版本信息
				softVersionList = probManageService.querySoftVersionList(prob.getProbId());
				prob.setAffectedVersion(JSON.toJSONString(softVersionList));
				// }
				// .4查询子任务
				if (probRestore == null)
					probRestore = new ProbRestore();
				probRestore.setProbId(prob.getProbId());
				UserContext context = UserContext.getUserContext();
				if (!user.isHasRole(MessageUtil.ROLE_PROB_ADMIN) && !user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)) {// 1：技术公告员、2:技术公告技术支持人员
					probRestore.setAssignee(context.getUsername());
					// 如果是服务经理角色，能看整个办事处的，否则只能是自己的任务
					/*
					 * probRestore.setAssigneeRole(
					 * context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) ?
					 * MessageUtil.ROLE_SERVICEMANAGER : 0); if
					 * (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {//
					 * 如果是服务经理，查询权限下的办事处
					 * probRestore.setAreapower(Util.appendChar(context.getUser(
					 * ).getAreapower(), "'"));
					 * probRestore.setAssigneeRole(null); } else {
					 * probRestore.setOfficeCode(null); }
					 */
					probRestore.setAreapower(Util.appendChar(context.getUser().getAreapower(), "'"));
					probRestore.setAssigneeRole(null);
				}
				if (restoreDisplayParam == null) {
					restoreDisplayParam = new DisplayParam();
				}
				restoreDisplayParam.setColmap(DisplayParamUtil.initProbRestoreTaskColMap());
				restoreDisplayParam.getParam();
				// probRestoreTaskList =
				// probManageService.queryProbRestoreTaskList(probRestore,
				// restoreDisplayParam);
				probRestoreTaskList = probManageService.queryProbRestoreTaskProjectList(probRestore,
						restoreDisplayParam);
				// 0.5查询进展附件 管理员、技术支持人员查询全部 其他人查询自己上传的
				// weeklyList =
				// probManageService.queryProbWeekly(probRestore.getProbId(),
				// (!user.isHasRole(MessageUtil.ROLE_PROB_ADMIN) ||
				// !user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)) ? null :
				// context.getUsername());
				
				
				probManageService.readLog(prob.getProbId(), 0);
			}
			System.out.println(System.currentTimeMillis() - t);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "edit";
	}

	/**
	 * 检索受技术公告影响的项目集合
	 * 
	 * @return
	 */
	public String checkProject() {
		try {
			// 办事处集合
			departmentList = departmentManageService.queryDepartments();
			// 第一次打开页面时不进行查询，避免数据量大
			if (firstCheck) {
				firstCheck = false;
			} else {
				// 查询受技术公告影响的数据对象集合
				if (restoreDisplayParam == null) {
					restoreDisplayParam = new DisplayParam();
				}
				restoreDisplayParam.getParam();
				probRestoreList = probManageService.queryProbRestoreList(probRestore, restoreDisplayParam);
				probRestoreTaskList = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	public String checkSubProject() {
		try {
			// 办事处集合
			// departmentList = departmentManageService.queryDepartments();
			// 第一次打开页面时不进行查询，避免数据量大
			// if (firstCheck) {
			// firstCheck = false;
			// } else {
			// 查询受技术公告影响的数据对象集合
			if (restoreDisplayParam == null) {
				restoreDisplayParam = new DisplayParam();
			}
			restoreDisplayParam.getParam();
			probRestoreList = probManageService.queryProbRestoreList(probRestore, restoreDisplayParam);
			probRestoreTaskList = null;
			// }
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 发布技术公告修复任务
	 * 
	 * @return
	 */
	public String releaseTask() {
		try {
			if (probRestore.getAssignee() == null || "".equals(probRestore.getAssignee())) {
				probRestore.setAssigneeRole(MessageUtil.ROLE_SERVICEMANAGER);// 服务经理
			} else {
				probRestore.setAssigneeRole(0);
			}
			if (probRestore.getRestoreStatus() == 0)// 即不是直接闭环的子任务
				probRestore.setRestoreStatus(10);// 开始流程
			String root = ServletActionContext.getServletContext().getRealPath("/");
			probManageService.insertBatchProbRestoreTask(probRestore, probRestoreTaskList, root);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 管理个人任务
	 * 
	 * @return
	 */
	public String managePrivateTask() {
		try {
			// 加载修复任务状态集合
			restoreStatuList = basicDataService.queryBasicDataBeanByAttri("33", "user");

			UserContext context = UserContext.getUserContext();
			probRestore.setAssignee(context.getUsername());
			// 如果是服务经理角色，能看权限下的办事处的，否则只能是自己的任务
			/*
			 * 希望办事处内所有人都能看到任务，注释该部分代码 probRestore.setAssigneeRole(
			 * context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) ?
			 * MessageUtil.ROLE_SERVICEMANAGER : 0); if
			 * (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
			 * probRestore.setAreapower(Util.appendChar(context.getUser().
			 * getAreapower(), "'")); }
			 */
			// probRestore.setOfficeCode(null);

			// 放开所有权限
			probRestore.setAreapower(Util.appendChar(context.getUser().getAreapower(), "'"));

			// 设置只能管理发布接受状态的子任务
			probRestore.setRestoreStatus(10);
			probRestoreTaskList = probManageService.queryProbRestoreTaskList(probRestore, null);
			softVersionList = probManageService.querySoftVersionList(probRestore.getProbId());
			result = JSON.toJSONString(softVersionList);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 更新个人任务状态
	 * 
	 * @return
	 */
	public String updatePrivateTask() {
		try {
			// int isProbAdmin = user.isHasRole(MessageUtil.ROLE_PROB_RD) ? 3 :
			// 0;
			probManageService.updateProbRestoreTask(probRestore, restoreIds, 0);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 上传任务进展周报
	 * 
	 * @return
	 */
	public String weeklyUpload() {
		try {
			// 上传附件
			if (upload != null) {
				String fileIds = null;
				String path = UPLOAD_PATH + seq + Util.getRandNumber();
				UploadFileUtil.upload(upload, path, uploadFileName);
				fileIds = basicDataService.insertFileInfo(path + seq, uploadFileName);
				String root = ServletActionContext.getServletContext().getRealPath("/");
				probManageService.insertProbTaskWeekly(Integer.parseInt(fileIds), probRestore.getProbId(), root,
						path + seq + uploadFileName + "," + uploadFileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 管理员管理所有任务
	 * 
	 * @return
	 */
	public String manageAllTask() {
		try {
			// 加载修复任务状态集合
			restoreStatuList = basicDataService.queryBasicDataBeanByAttri("33", "manage");
			// 选项卡集合
			navTabList = basicDataService.queryBasicDataBeans("34");
			// 办事处集合
			departmentList = departmentManageService.queryDepartments();
			// 查询子任务集合
			if (restoreDisplayParam == null) {
				restoreDisplayParam = new DisplayParam();
			}
			if (probRestore.getRestoreStatus() == 31) {// 闭环任务
				restoreDisplayParam.setColmap(DisplayParamUtil.initProbRestoreTaskColMap());
				restoreDisplayParam.getParam();
				// probRestore.setOfficeCode(null);
				probRestoreTaskList = probManageService.queryProbRestoreTaskList(probRestore, restoreDisplayParam);
			} else if (probRestore.getRestoreStatus() == 20) {// 办事处返回的任务
				probRestoreTaskList = probManageService.queryProbRestoreTaskList(probRestore, null);
			} else {// 待闭环任务
				probRestore.setRestoreStatus(30);// 办事处已处理的升级任务
				probRestoreTaskList = probManageService.queryProbRestoreTaskList(probRestore, null);
			}
			softVersionList = probManageService.querySoftVersionList(probRestore.getProbId());
			result = JSON.toJSONString(softVersionList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * 管理员更新任务
	 * 
	 * @return
	 */
	public String updateRestoreTask() {
		try {
			// int isProbAdmin = user.isHasRole(MessageUtil.ROLE_PROB_ADMIN) ? 1
			// : (user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER) ? 2 : 0);
			probManageService.updateProbRestoreTask(probRestore, restoreIds, 2);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 保存技术公告信息
	 * 
	 * @return
	 */
	public String save() {
		try {
			// 上传附件
			String fileIds = "";
			if (upload != null) {
				String path = UPLOAD_PATH + seq + Util.getRandNumber();
				UploadFileUtil.upload(upload, path, uploadFileName);
				fileIds = basicDataService.insertFileInfo(path + seq, uploadFileName);
			}
			prob.setAttachments(fileIds);
			prob.setStatus("1");
			// 保存信息
			// .1获取软件版本信息
			// HttpSession session = getServletRequest().getSession();
			// softVersionList = (List<SoftVersion>)
			// session.getAttribute("softVersionList");
			// session.setAttribute("softVersionList", null);
			// .2进行保存操作
			String root = ServletActionContext.getServletContext().getRealPath("/");
			int probId = probManageService.saveProb(prob, softVersionList, root);
			prob.setProbId(probId);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}

		if (isContinue == 0) {
			return SUCCESS;
		}
		return "continue";
	}

	/**
	 * 做更新操作
	 * 
	 * @return
	 */
	public String update() {
		try {
			// 0.1上传附件,如果没有上传则不执行更新
			String fileIds = "";
			if (upload != null) {
				String path = UPLOAD_PATH + seq + Util.getRandNumber();
				UploadFileUtil.upload(upload, path, uploadFileName);
				fileIds = basicDataService.insertFileInfo(path + seq, uploadFileName);// 为了邮件附件取绝对路径
				prob.setAttachments(fileIds);
			}
			// //0.2获取session中检索的软件版本
			// HttpSession session = getServletRequest().getSession();
			// if(session.getAttribute("softVersionList") != null){
			// softVersionList = (List<SoftVersion>)
			// session.getAttribute("softVersionList");
			// session.setAttribute("softVersionList", null);
			// }
			// 0.3做更新操作
			probManageService.updateProb(prob, softVersionList);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 技术公告管理员驳回，审批任务
	 * 
	 * @return
	 */
	public String audit() {
		try {
		    probManageService.updateProbSoftVersion(softVersionList, prob.getProbId());
			probManageService.updateProbStatus(prob);
			result = "success";
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		}
		return SUCCESS;
	}

	/**
	 * 导出技术公告
	 * 
	 * @return
	 */
	public String export() {
		try {
			List<ProbParam> probParams = probManageService.queryExportProbList(new HashMap<>());
			for (Prob probParam : probParams) {
				String desc = probParam.getDesc();
				if (StringUtils.isNotBlank(desc)) {
					desc = HtmlUtils.htmlUnescape(desc);
					desc = desc.replaceAll("\r\n", "");
					desc = desc.replaceAll("<(?!img|br|/p|/table|/tr|/th|/td).*?>", "");
					desc = desc.replaceAll("<(?!img|br|/p|/table|/tr).*?>", "    ");
					desc = desc.replaceAll("<(?!img).*?>", "\r\n");
					Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");// <img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
					Matcher m = p.matcher(desc);
					while (m.find()) {
						desc = desc.replace(m.group(), "\r\n" + m.group(1) + "\r\n");
					}
					probParam.setDesc(desc);
				}

				String solution = probParam.getSolution();
				if (StringUtils.isNotBlank(solution)) {
					solution = HtmlUtils.htmlUnescape(solution);
					solution = solution.replaceAll("\r\n", "");
					solution = solution.replaceAll("<(?!img|br|/p|/table|/tr|/th|/td).*?>", "");
					solution = solution.replaceAll("<(?!img|br|/p|/table|/tr).*?>", "    ");
					solution = solution.replaceAll("<(?!img).*?>", "\r\n");
					Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");// <img[^<>]*src=[\'\"]([0-9A-Za-z.\\/]*)[\'\"].(.*?)>");
					Matcher m = p.matcher(solution);
					while (m.find()) {
						solution = solution.replace(m.group(), "\r\n" + m.group(1) + "\r\n");
					}
					probParam.setSolution(solution);
				}
			}
			Workbook workbook = ExportUtils.buildExcelDocument(probParams);
			ExportUtils.writeToResponse(workbook, getServletResponse(),
					new String("技术公告.xlsx".getBytes("UTF-8"), "ISO-8859-1"));
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
		}
		return ERROR;
	}

	/**
	 * 上传xlx批量导入软件版本，
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String importSoftVersion() {
		try {
			if (upload != null) {
				if (user.isHasRole(MessageUtil.ROLE_PROB_SUPPORTER)) {
					List<SoftVersion> softVersions = (List<SoftVersion>) ExportUtils.readFromExcel(upload, uploadFileName,
							SoftVersion.class);
					probManageService.batchAddSoftVersion(softVersions);
					result = "success";
				} else {
					result = "authError";
				}
			}
			prob = new Prob();
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "importSoftVersion";
	}

	/**
	 * 进入查询软件版本页面
	 * 
	 * @return
	 */
	public String toCheckSoftVersion() {
		// if(softVersionCodes != null &&
		// !"".equals(softVersionCodes)){//选择保存软件版本
		// String[] split = softVersionCodes.split(", ");
		// SoftVersion version = null;
		// softVersionList = new ArrayList<SoftVersion>();
		// for(String software : split){
		// version = new SoftVersion(software.split(",")[0].split("-").length >1
		// ? software.split(",")[0].split("-")[1] :null,
		// software.split(",")[1].split("-").length >1 ?
		// software.split(",")[1].split("-")[1] :null,
		// software.split(",")[2].split("-").length >1 ?
		// software.split(",")[2].split("-")[1] :null,
		// software.split(",")[3].split("-").length >1 ?
		// software.split(",")[3].split("-")[1] :null);
		// softVersionList.add(version);
		// }
		// HttpSession session = getServletRequest().getSession();
		// session.setAttribute("softVersionList", softVersionList);
		// softVersionCodes = null;
		// return SUCCESS;
		// }

		if (softVersion != null) {
			softVersionList = probManageService.checkSoftVersionList(softVersion);
		} else {
			softVersionList = null;
		}
		return INPUT;
	}

	/**
	 * 确认选择软件版本
	 * 
	 * @return
	 */
	public String submitSoftVersion() {
		if (softVersionCodes != null && !"".equals(softVersionCodes)) {// 选择保存软件版本
			String[] split = softVersionCodes.split(", ");
			SoftVersion version = null;
			softVersionList = new ArrayList<SoftVersion>();
			for (String software : split) {
				version = new SoftVersion(
						software.split(",")[0].split("-").length > 1 ? software.split(",")[0].split("-")[1] : null,
						software.split(",")[1].split("-").length > 1 ? software.split(",")[1].split("-")[1] : null,
						software.split(",")[2].split("-").length > 1 ? software.split(",")[2].split("-")[1] : null,
						software.split(",")[3].split("-").length > 1 ? software.split(",")[3].split("-")[1] : null);
				softVersionList.add(version);
			}
			// HttpSession session = getServletRequest().getSession();
			// session.setAttribute("softVersionList", softVersionList);
			result = JSON.toJSONString(softVersionList);
			softVersionCodes = null;
		}
		return SUCCESS;
	}
	
	/**
	 * 根据手工录入的信息解析软件版本范围
	 * @return
	 */
	public String parserSoftVersion() {
		if (softVersion != null && StringUtils.isNotBlank(softVersion.getManualEntry())) {
			Map<String, Map<String, List<SoftVersionParser>>> versionParser = SoftVersionUtil.createSoftVersionRangeParsers(softVersion.getManualEntry());
			result = JSON.toJSONString(versionParser, SerializerFeature.DisableCircularReferenceDetect);
		} else {
			result = "{}";
		}
		return SUCCESS;
	}
	
	public String parserOldSoftVersion() {
		if (softVersion == null) {
			softVersion = new SoftVersion();
		}
		softVersion.setProbId(Integer.valueOf(0).equals(softVersion.getProbId()) ? null : softVersion.getProbId());
		softVersion.setSplited(0);
		List<SoftVersion> versionList = probManageService.querySoftVersionList(softVersion);
		Map<Integer, List<SoftVersion>> parsedVersionMap = new HashMap<Integer, List<SoftVersion>>(versionList.size());
		Set<Integer> manualEntryProbSet = new HashSet<Integer>();
		// 遍历所有版本，对手工输入的进行解析，按probId进行分组，重新保存
		Long prevGroupId = 0L;
		for (SoftVersion softVersion : versionList) {
			int probId = softVersion.getProbId();
			List<SoftVersion> parsedVersionList = parsedVersionMap.get(probId);
			if (parsedVersionList == null) {
				parsedVersionList = new ArrayList<SoftVersion>();
				parsedVersionMap.put(probId, parsedVersionList);
			}
			if (StringUtils.isNotBlank(softVersion.getManualEntry())) {
				manualEntryProbSet.add(probId);
				Map<String, Map<String, List<SoftVersionParser>>> parserMap = SoftVersionUtil.createSoftVersionRangeParsers(softVersion.getManualEntry());
				for (Entry<String, Map<String, List<SoftVersionParser>>> entry : parserMap.entrySet()) {
					String manualEntry = entry.getKey();
					Long groupId = System.currentTimeMillis();
					while(prevGroupId >= groupId) {
						groupId = System.currentTimeMillis();
					}
					Map<String, List<SoftVersionParser>> manualEntrySubMap = entry.getValue();
					if (manualEntrySubMap.isEmpty()) {
						SoftVersion parsedVersion = new SoftVersion();
						parsedVersion.setProbId(probId);
						parsedVersion.setManualEntry(manualEntry);
						parsedVersion.setManualEntrySub("");
						parsedVersion.setEntryStart("");
						parsedVersion.setEntryEnd("");
						parsedVersion.setMarkStart("");
						parsedVersion.setMarkEnd("");
						parsedVersion.setAffectedType(0);
						parsedVersion.setGroupId(groupId);
						parsedVersionList.add(parsedVersion);
					} else {
						for (Entry<String, List<SoftVersionParser>> entrySub : manualEntrySubMap.entrySet()) {
							String manualEntrySub = entrySub.getKey();
							List<SoftVersionParser> ranges = entrySub.getValue();
							SoftVersionParser start = ranges.get(0);
							SoftVersionParser end = ranges.get(1);
							SoftVersion parsedVersion = new SoftVersion();
							parsedVersion.setProbId(probId);
							parsedVersion.setManualEntry(manualEntry);
							parsedVersion.setManualEntrySub(manualEntrySub);
							parsedVersion.setEntryStart(start.getVersion());
							parsedVersion.setEntryEnd(end.getVersion());
							parsedVersion.setMarkStart(start.getMark());
							parsedVersion.setMarkEnd(end.getMark());
							parsedVersion.setAffectedType(0);
							parsedVersion.setGroupId(groupId);
							parsedVersionList.add(parsedVersion);
						}
					}
					prevGroupId = groupId;
				}
			} else {
				softVersion.setGroupId(0L);
				parsedVersionList.add(softVersion);
			}
		}
		for (Entry<Integer, List<SoftVersion>> entry : parsedVersionMap.entrySet()) {
			Integer probId = entry.getKey();
			List<SoftVersion> parsedVersionList = entry.getValue();
			probManageService.updateProbSoftVersion(parsedVersionList, probId);
		}
		return SUCCESS;
	}

	public String statistics() {
		try {
			if (probStatistic == null) {
				probStatistic = new ProbStatistic();
			}
			departmentList = departmentManageService.queryDepartments();
			if (probStatisticList == null) {
				probStatisticList = new ArrayList<>();
			}
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();
			
			if (probStatistic.getTabIndex() < 2) {
				if (StringUtils.isBlank(probStatistic.getStartTime())) {
					// 设置一个季度的第一天
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date startTime = DateUtil.getQuarterFirstDay(new Date());
					// probStatistic.setExecuteTime(startTime);
					probStatistic.setStartTime(dateFormat.format(startTime));
					Date endTime = DateUtil.getQuarterLastDay(new Date());
					probStatistic.setEndTime(dateFormat.format(endTime));
				} else if (probStatistic.getAutoAdjust()) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = dateFormat.parse(probStatistic.getStartTime());
					Date startTime = DateUtil.getQuarterFirstDay(date);
					probStatistic.setStartTime(dateFormat.format(startTime));
					Date endTime = DateUtil.getQuarterLastDay(date);
					probStatistic.setEndTime(dateFormat.format(endTime));
				}
				List<ReportLineData> reportLineDatas = new ArrayList<ReportLineData>();
				probStatisticList = probManageService.queryProbStatisticListWithReport(probStatistic, displayParam,
						reportLineDatas);
				result = EchartsUtil.packagingTableHtml(reportLineDatas);
			} else {
				probProjectList = probManageService.queryProbStatisticProjectList(probStatistic, displayParam);
			}
		} catch (UnsupportedEncodingException | ParseException e) {
			e.printStackTrace();
		}
		return "statistics";
	}

	/**
	 * 技术公告阅读确认
	 * @return
	 */
	public String readSure() {
		try {
			if (probReadLog != null && probReadLog.getProbId() != 0) {
				probManageService.readLog(probReadLog.getProbId(), 1);
				result = "success";
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = "error";
		}
		return SUCCESS;
	}
	
	/**
	 * 技术公告阅读记录
	 * @return
	 */
	public String readLog() {
		try {
			if (displayParam == null) {
				displayParam = new DisplayParam();
				displayParam.setColmap(DisplayParamUtil.initProbColMap());
			}
			displayParam.getParam();
			if (probReadLog != null && probReadLog.getProbId() != 0) {
				readLogList = probManageService.queryProbReadLogList(probReadLog, displayParam);
			} else {
				readLogList = new ArrayList<>(0);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	/**
	 * 查询产品组件列表
	 * @return
	 */
	public String listComponent() {
	    if (productComponent == null) {
	        productComponent = new ProductComponentVO();
	    }
	    if (displayParam == null) {
            displayParam = new DisplayParam();
        }
	    try {
            displayParam.getParam();
            
    	    if ("json".equalsIgnoreCase(result)) {
    	        displayParam.setExport(true);
    	    }
            
            ProductComponentPageParam pageParam = new ProductComponentPageParam();
            BeanUtils.copyProperties(productComponent, pageParam);
            pageParam.setDisplayParam(displayParam);
            commonList = probManageService.selectProductComponentListPageable(pageParam);
            
            if ("json".equalsIgnoreCase(result)) {
                result = JSON.toJSONString(commonList);
                return SUCCESS;
            }
	    } catch (Exception e) {
            setErrmsg(ExceptionUtils.getMessage(e));
        }
        return "list";
	}
	
	/**
     * 新建
     */
    public String inputComponent() throws Exception {
        if (!user.isHasAnyRole(MessageUtil.ROLE_ADMIN, MessageUtil.ROLE_COMPONENT_ADMIN)) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
        if (productComponent != null) {
            productComponent = probManageService.selectProductComponentVOById(productComponent.getId());
        }
        return INPUT;
    }
    
    /**
     * 保存
     */
    public String saveComponent() throws Exception {
        if (!user.isHasAnyRole(MessageUtil.ROLE_ADMIN, MessageUtil.ROLE_COMPONENT_ADMIN)) {
            setErrmsg("没有访问权限！");
            return ERROR;
        }
        if (productComponent.getId() == null || productComponent.getId() == 0) {
            probManageService.insertProductComponentSelective(productComponent);
        } else {
            probManageService.updateProductComponentByIdSelective(productComponent);
        }
        return SUCCESS;
    }
    
    /**
     * 上传xlx批量导入产品组件
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public String importComponent() {
        try {
            if (upload != null) {
                if (user.isHasAnyRole(MessageUtil.ROLE_ADMIN, MessageUtil.ROLE_COMPONENT_ADMIN)) {
                    List<ProductComponent> productComponents = ExportUtils.readFromExcel(upload, uploadFileName, ProductComponent.class, "component.info.");
                    for (ProductComponent component : productComponents) {
                        probManageService.insertOrUpdateProductComponentSelective(component);
                    }
                    result = "success";
                } else {
                    result = "authError";
                }
            }
            productComponent = new ProductComponentVO();
        } catch (Exception e) {
            setErrmsg(ExceptionUtils.getStackTrace(e));
            return ERROR;
        }
        return "import";
    }
	
	
	public void setProbManageService(ProbManageService probManageService) {
		this.probManageService = probManageService;
	}

	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public List<BasicDataBean> getWatchList() {
		return watchList;
	}

	public void setWatchList(List<BasicDataBean> watchList) {
		this.watchList = watchList;
	}

	public List<BasicDataBean> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<BasicDataBean> statusList) {
		this.statusList = statusList;
	}

	public List<BasicDataBean> getPriorityList() {
		return priorityList;
	}

	public void setPriorityList(List<BasicDataBean> priorityList) {
		this.priorityList = priorityList;
	}

	public int getIsContinue() {
		return isContinue;
	}

	public void setIsContinue(int isContinue) {
		this.isContinue = isContinue;
	}

	public Prob getProb() {
		return prob;
	}

	public void setProb(Prob prob) {
		this.prob = prob;
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

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public List<Prob> getProbList() {
		return probList;
	}

	public void setProbList(List<Prob> probList) {
		this.probList = probList;
	}

	public List<SoftVersion> getSoftVersionList() {
		return softVersionList;
	}

	public void setSoftVersionList(List<SoftVersion> softVersionList) {
		this.softVersionList = softVersionList;
	}

	public SoftVersion getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(SoftVersion softVersion) {
		this.softVersion = softVersion;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public String getSoftVersionCodes() {
		return softVersionCodes;
	}

	public void setSoftVersionCodes(String softVersionCodes) {
		this.softVersionCodes = softVersionCodes;
	}

	public int getIsProbAdmin() {
		return isProbAdmin;
	}

	public void setIsProbAdmin(int isProbAdmin) {
		this.isProbAdmin = isProbAdmin;
	}

	public Map<Integer, String> getFileMap() {
		return fileMap;
	}

	public void setFileMap(Map<Integer, String> fileMap) {
		this.fileMap = fileMap;
	}

	public List<ProbRestore> getProbRestoreList() {
		return probRestoreList;
	}

	public void setProbRestoreList(List<ProbRestore> probRestoreList) {
		this.probRestoreList = probRestoreList;
	}

	public DisplayParam getRestoreDisplayParam() {
		return restoreDisplayParam;
	}

	public void setRestoreDisplayParam(DisplayParam restoreDisplayParam) {
		this.restoreDisplayParam = restoreDisplayParam;
	}

	public ProbRestore getProbRestore() {
		return probRestore;
	}

	public void setProbRestore(ProbRestore probRestore) {
		this.probRestore = probRestore;
	}

	public List<Department> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<Department> departmentList) {
		this.departmentList = departmentList;
	}

	public List<ProbRestore> getProbRestoreTaskList() {
		return probRestoreTaskList;
	}

	public void setProbRestoreTaskList(List<ProbRestore> probRestoreTaskList) {
		this.probRestoreTaskList = probRestoreTaskList;
	}

	public List<BasicDataBean> getRestoreStatuList() {
		return restoreStatuList;
	}

	public void setRestoreStatuList(List<BasicDataBean> restoreStatuList) {
		this.restoreStatuList = restoreStatuList;
	}

	public String getRestoreIds() {
		return restoreIds;
	}

	public void setRestoreIds(String restoreIds) {
		this.restoreIds = restoreIds;
	}

	public List<BasicDataBean> getNavTabList() {
		return navTabList;
	}

	public void setNavTabList(List<BasicDataBean> navTabList) {
		this.navTabList = navTabList;
	}

	public List<ProbRestore> getProbRestoreBackingTaskList() {
		return probRestoreBackingTaskList;
	}

	public void setProbRestoreBackingTaskList(List<ProbRestore> probRestoreBackingTaskList) {
		this.probRestoreBackingTaskList = probRestoreBackingTaskList;
	}

	public List<ProbRestoreWeekly> getWeeklyList() {
		return weeklyList;
	}

	public void setWeeklyList(List<ProbRestoreWeekly> weeklyList) {
		this.weeklyList = weeklyList;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	public boolean getFirstCheck() {
		return firstCheck;
	}

	public void setFirstCheck(boolean firstCheck) {
		this.firstCheck = firstCheck;
	}

	public String getProbRestoreIds() {
		return probRestoreIds;
	}

	public void setProbRestoreIds(String probRestoreIds) {
		this.probRestoreIds = probRestoreIds;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ProbStatistic getProbStatistic() {
		return probStatistic;
	}

	public void setProbStatistic(ProbStatistic probStatistic) {
		this.probStatistic = probStatistic;
	}

	public List<ProbStatistic> getProbStatisticList() {
		return probStatisticList;
	}

	public void setProbStatisticList(List<ProbStatistic> probStatisticList) {
		this.probStatisticList = probStatisticList;
	}

	public List<Project> getProbProjectList() {
		return probProjectList;
	}

	public void setProbProjectList(List<Project> probProjectList) {
		this.probProjectList = probProjectList;
	}

	public ProbReadLog getProbReadLog() {
		return probReadLog;
	}

	public void setProbReadLog(ProbReadLog probReadLog) {
		this.probReadLog = probReadLog;
	}

	public List<ProbReadLog> getReadLogList() {
		return readLogList;
	}

	public void setReadLogList(List<ProbReadLog> readLogList) {
		this.readLogList = readLogList;
	}
	
    public ProductComponentVO getProductComponent() {
        return productComponent;
    }

    public void setProductComponent(ProductComponentVO productComponent) {
        this.productComponent = productComponent;
    }

    public List<? extends Object> getCommonList() {
        return commonList;
    }

    public void setCommonList(List<? extends Object> commonList) {
        this.commonList = commonList;
    }

    public Map<String, ? extends Object> getCommonMap() {
        return commonMap;
    }

    public void setCommonMap(Map<String, ? extends Object> commonMap) {
        this.commonMap = commonMap;
    }
	
}
