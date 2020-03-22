package com.dp.plat.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.data.bean.Department;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaire;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireLine;
import com.dp.plat.data.bean.PmClosedLoopQuesnaireOpt;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.PresalesProduct;
import com.dp.plat.data.bean.PresalesTask;
import com.dp.plat.data.bean.ProjectDeliver;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.User;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.param.ProjectTypeParam;
import com.dp.plat.service.BasicDataService;
import com.dp.plat.service.CallBackService;
import com.dp.plat.service.DepartmentManageService;
import com.dp.plat.service.PmClosedLoopQuesnaireService;
import com.dp.plat.service.PmClosedLoopService;
import com.dp.plat.service.PresalesService;
import com.dp.plat.util.MessageUtil;
import com.dp.plat.util.PmClosedLoopConstant;
import com.dp.plat.util.PmClosedLoopMark;
import com.dp.plat.util.PmClosedLoopMarkFactory;
import com.opensymphony.xwork2.Preparable;

/**
 * 售前测试项目
 * 
 * @author admin
 *
 */
public class PresalesAction extends BaseAction implements Preparable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 基础数据管理
	 */
	private BasicDataService basicDataService;
	/**
	 * 问卷管理
	 */
	private PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService;
	/**
	 * 售前业务管理
	 */
	private PresalesService presalesService;
	/**
	 * 回访问卷管理
	 */
	private CallBackService callBackService;

	/**
	 * 闭环管理
	 */
	private PmClosedLoopService pmClosedLoopService;
	/**
	 * 办事处管理
	 */
	private DepartmentManageService departmentManageService;
	/**
	 * 转发URL
	 */
	private String redirect;
	private String redirectUrl;
	private String urlParams;

	private Presales presales;
	private List<PresalesProduct> productList;

	private PresalesComment param;
	private List<PresalesComment> commentList;
	private List<PresalesTask> taskList;
	private Map<Integer, String> fileMap;
	private ProjectDeliver projectDeliver;
	private String filePath;

	// 项目列表管理
	private List<Presales> presalesList;
	private DisplayParam displayParam;
	private List<Department> officeList;
	private List<BasicDataBean> projectStateList;
	private List<BasicDataBean> projectTypeList;
	private String PROJECT_STATE_CODE = "27";
	public final static String PROJECT_TYPE_CODE = "presalesType";

	// 回访相关
	private List<PmClosedLoopQuesnaire> pmClosedLoopQuesnaireList;
	private PmClosedLoopQuesnaire pmClosedLoopQuesnaire;
	private List<PmClosedLoopQuesnaireLine> pmClosedLoopQuesnaireLineList;
	private List<PmClosedLoopQuesnaireOpt> pmClosedLoopQuesnaireOptList;
	private PmClQuesnaireResultHeader pmClQuesnaireResultHeader;
	private List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList;
	private PmClQuesnaireResultLine pmClQuesnaireResultLine;

	// 项目计划任务管理
	private int presalesTaskId;
	private Date taskFinshedTime;
	private String remark;
	private String message;
	private int fileId;

	// 查询售前测试项目发货信息
	private String presalesCode;
    private boolean containRma;// 是否包含退货设备，默认不包含退货
	private List<ShipmentInfo> shipmentInfos;

	// 查询售前测试项目借转销信息/核销信息
	private List<Map<String, Object>> commonList;
	
	// 终止流程是获取presalesIds
	private String presalesIds;

	private User user;
	
	private List<PresalesExportVO> presalesExportVOList;

    private List<ProjectDeliver> projectDeliverList;

	public void prepareList() {
		officeList = departmentManageService.queryDepartments();
		projectStateList = basicDataService.queryBasicDataBeans(PROJECT_STATE_CODE);
		projectTypeList = basicDataService.queryBasicDataBeans(PROJECT_TYPE_CODE);
	}

	/**
	 * 售前项目管理
	 * 
	 * @return
	 */
	public String list() {
		try {
			user = UserContext.getUserContext().getUser();
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();
			if (presales == null) {
				presales = new Presales();
				UserContext context = UserContext.getUserContext();
				if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
						|| context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF)) {
					presales.setProjectStates(MessageUtil.PROJECT_STATE_CREATING);
				} else {
				    presales.setProjectStates("30,31,32,33");
				}
			}
			//String export = getServletRequest().getParameter("6578706f7274");
			if (displayParam.getExport()) {
			    String exportDetail = getServletRequest().getParameter("exportDetail");
			    if(StringUtils.isBlank(presales.getExportDetail())) {
			        presales.setExportDetail(exportDetail);
			    }
				presalesExportVOList = presalesService.queryPresalesExportData(presales);
				displayParam.setPagesize(presalesExportVOList.size());
				displayParam.setTotalcount(presalesExportVOList.size());
				presalesList = new ArrayList<>(presalesExportVOList.size());
				presalesList.addAll(presalesExportVOList);
				presalesExportVOList = null;
			} else {
				presalesList = presalesService.queryPresalesList(presales, displayParam);
			}
//			presalesList = presalesService.queryPresalesList(presales, displayParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "list";
	}

	@Override
	public void prepare() {
		// System.out.println(0);
	}

	/**
	 * 准备发起流程申请,或重新申请
	 */
	public String input() {
		try {
			presales = presalesService.queryPresalesById(presales.getPresalesId());
			productList = presalesService.queryPresalesProductByPresalesId(presales.getPresalesId());
			commentList = presalesService.queryPresalesCommentList(presales.getPresalesId());
			projectTypeList = basicDataService.queryBasicDataBeans(PROJECT_TYPE_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "input";
	}

	/**
	 * 发起售前流程
	 * 
	 */
	public String apply() {
		try {
			if (param.getTaskId() == null || "".equals(param.getTaskId())) {
				presalesService.startPresalesFlow(presales, param);
			} else {
				presalesService.submitReApply(presales, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}

		redirect = "module/presales_list.action";
		return SUCCESS;
	}

	/**
	 * 查看页面
	 * 
	 * @return
	 */
	public String read() {
		try {
			presales = presalesService.queryPresalesById(presales.getPresalesId());
			productList = presalesService.queryPresalesProductByPresalesId(presales.getPresalesId());
			commentList = presalesService.queryPresalesCommentList(presales.getPresalesId());
			taskList = presalesService.queryPresalesTaskList(presales.getPresalesId(),
					ProjectTypeParam.TYPE_OF_PRESALES);
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		return "read";
	}

	/**
	 * 审批任务跳转到哪个页面判断
	 * 
	 * @return
	 */
	public String aduit() {
		presales = presalesService.queryPresalesById(presales.getPresalesId());
		String taskDefKey = presales.getTaskDefKey();
		if ("usertask2".equals(taskDefKey)) {
			redirect = "module/presales_smaduit.action?presales.presalesId=" + presales.getPresalesId();
		} else if ("usertask3".equals(taskDefKey)) {
			redirect = "module/presales_pmaduit.action?presales.presalesId=" + presales.getPresalesId();
		} else if ("usertask4".equals(taskDefKey)) {
			redirect = "module/presales_emaduit.action?presales.presalesId=" + presales.getPresalesId();
		} else if ("usertask1".equals(taskDefKey)) {
			redirect = "module/presales_input.action?presales.presalesId=" + presales.getPresalesId();
		}

		return SUCCESS;
	}

	/**
	 * 服务经理任务办理
	 * 
	 * @param
	 */
	public String smaduit() {
		try {
			if (param == null) {
				presales = presalesService.queryPresalesById(presales.getPresalesId());
				productList = presalesService.queryPresalesProductByPresalesId(presales.getPresalesId());
				commentList = presalesService.queryPresalesCommentList(presales.getPresalesId());
				taskList = presalesService.queryPresalesTaskList(presales.getPresalesId(),
						ProjectTypeParam.TYPE_OF_PRESALES);
				user = UserContext.getUserContext().getUser();
				return "smaduit";
			}
			if (param.getInstId() != null) {// 进行审批
				presalesService.submitSmAduit(presales, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		redirect = "module/presales_list.action";
		return SUCCESS;
	}

	/**
	 * 项目经理任务办理
	 * 
	 * @param
	 */
	public String pmaduit() {
		try {
			// 进入待审批页面
			if (param == null) {
				int taskId = 0;
				if (urlParams != null) {
					String[] params = urlParams.split("\\|");
					if (presales == null) {
						presales = new Presales();
					}
					presales.setPresalesId(Integer.parseInt(params[0]));
					taskId = Integer.parseInt(params[1]);
				}

				productList = presalesService.queryPresalesProductByPresalesId(presales.getPresalesId());
				commentList = presalesService.queryPresalesCommentList(presales.getPresalesId());
				
//				// 上传附件已切换为其他方法，以下代码失效
//				// 将上传的附件更新TASK中
//				HttpSession session = getServletRequest().getSession();
//				String fileIds = (String) session.getAttribute("fileIds");
//				presalesService.updatePresalesTaskDeliverFiles(taskId, fileIds);
//				// 将附件更新到主表上
//				presalesService.updatePresalesConfirmFileIds(presales.getPresalesId(), fileIds);

				presales = presalesService.queryPresalesById(presales.getPresalesId());
				// 检查是否否则不存在时，已完成测试

				taskList = presalesService.queryPresalesTaskList(presales.getPresalesId(),
						ProjectTypeParam.TYPE_OF_PRESALES);
//				session.removeAttribute("fileIds");
				
				taskFinshedTime = new Date();
				// 获取每个阶段的交付件列表，变更交付件类型时使用
				ProjectDeliver projectDeliver = new ProjectDeliver();
                projectDeliver.setColumn010("presales");
                projectDeliver.setColumn011("");
                projectDeliver.setDataTypeCode("29");
                projectDeliverList = presalesService.queryProjectDeliverList(projectDeliver);
				return "pmaduit";
			}
			if (param.getInstId() != null) {// 进行审批
				presalesService.submitpmAduit(presales, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}
		redirect = "module/presales_list.action";
		return SUCCESS;
	}

	/**
	 * 更新项目计划完成时间
	 * 
	 * @return
	 */
	public String updateTask() {
		try {
		    Date nowDate = new Date();
		    if(taskFinshedTime != null && nowDate.before(taskFinshedTime)) {
		        taskFinshedTime = nowDate;
		    }
			presalesService.updatePresalesTask(taskFinshedTime, remark, presalesTaskId);
			message = "更新成功!";
		} catch (Exception e) {
			e.printStackTrace();
			message = "更新失败!";
		}
		return SUCCESS;
	}

	/**
	 * 工程管理部回访
	 */
	public String emaduit() {
		try {
			if (param == null) {
				presales = presalesService.queryPresalesById(presales.getPresalesId());
				productList = presalesService.queryPresalesProductByPresalesId(presales.getPresalesId());
				commentList = presalesService.queryPresalesCommentList(presales.getPresalesId());
				taskList = presalesService.queryPresalesTaskList(presales.getPresalesId(),
						ProjectTypeParam.TYPE_OF_PRESALES);
				projectTypeList = basicDataService.queryBasicDataBeans(PROJECT_TYPE_CODE);
				// 获取每个阶段的交付件列表，变更交付件类型时使用
                ProjectDeliver projectDeliver = new ProjectDeliver();
                projectDeliver.setColumn010("presales");
                projectDeliver.setColumn011("");
                projectDeliver.setDataTypeCode("29");
                projectDeliverList = presalesService.queryProjectDeliverList(projectDeliver);
				if (presales.getQuesnaireState() == 1) {
					getCbForm(presales.getQuesnaireId());
				}
				return "emaduit";
			}
			if (param.getInstId() != null) {// 进行审批
				presalesService.submitEmAduit(presales, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			return ERROR;
		}

		redirect = "module/presales_list.action";
		return SUCCESS;
	}

	/**
	 * 回访问卷
	 * 
	 * @return
	 */
	public String callback() {
		// 问卷提交
		if (pmClQuesnaireResultHeader != null && pmClQuesnaireResultHeader.getStatus() != 0) {
			if (pmClQuesnaireResultHeader.getStatus() == 1) {// 已提交，计算分数
				queryQuesnaireScore();
			}
			// 每次保存问卷草稿或提交问卷都会重新生成一份数据保存在数据库
			presalesService.insertPresalesQuesnaire(presales, pmClQuesnaireResultHeader, pmClQuesnaireResultLineList);

			return SUCCESS;
		}
		presales = presalesService.queryPresalesById(presales.getPresalesId());
		// 获取生效的问卷分类
		findPmClosedLoopQuesnaireList();
		// 获取问卷模板的内容或者已填写的问卷内容
		if ((pmClosedLoopQuesnaire != null && pmClosedLoopQuesnaire.getId() != 0) || presales.getQuesnaireId() != 0) {
			getCbForm(presales.getQuesnaireId());
		}
		return "callback";
	}

	/**
	 * 查询发货信息
	 * 
	 * @return
	 */
	public String shipmentInfo() {
		shipmentInfos = presalesService.queryPresaleShipmentInfo(presalesCode, containRma);
		return "shipmentInfo";
	}
	
	/**
     * 查询借转销信息
     * 
     * @return
     */
    public String lend2SaleInfo() {
        commonList = presalesService.queryPresaleLend2SaleInfo(presalesCode);
        return "lend2SaleInfo";
    }
    
    /**
     * 查询核销信息
     * 
     * @return
     */
    public String lend2RmaInfo() {
        commonList = presalesService.queryPresaleLend2RmaInfo(presalesCode);
        return "lend2RmaInfo";
    }

	public String terminate2Close() {
		try {
			presalesService.terminate2Close(presalesIds, message);
			message = "success";
		} catch (Exception e) {
			message = e.getMessage();
			e.printStackTrace();
			setErrmsg(ExceptionUtils.getStackTrace(e));
			//return ERROR;
		}
		return SUCCESS;
	}
	
//	//附件上传
//    public String toUploadPresalesDeliverFile(){
//        String ek = null;
//        try{
//            ek = projectDeliver.getEventKey();//获取事件节点
//            String[] eksplit = ek.split("-");
//            projectDeliver.setDataTypeCode(eksplit[0]);
//            projectDeliver.setBasicDataId(eksplit[1]);
//            ProjectService projectService = SpringContext.getApplicationContext().getBean("projectService", ProjectService.class);
//            projectDeliverList = projectService.queryProjectDeliverList(projectDeliver);
//        }catch(Exception e){
//            setErrmsg(ExceptionUtils.getStackTrace(e));
//            return ERROR;
//        }
//        return "toUploadPresalesDeliverFile";
//    }
//    /**
//     *  上传工程交付件
//     * @return
//     */
//    public String uploadPresalesDeliverFile(){
//        String[] deliverIds = projectDeliver.getDeliverId().split(",");
//        if(projectDeliverList != null && projectDeliverList.size() > 0){
//            for(int i = 0;i < projectDeliverList.size();i++){
//                if(projectDeliverList.get(i) == null){
//                    continue;
//                }
//                ProjectDeliver deliver = new ProjectDeliver();
//                BeanUtils.copyProperties(projectDeliver, deliver);
//                deliver.setDeliverableType(projectDeliverList.get(i).getDeliverableType());
//                boolean isCB = presalesService.uploadFile(deliver, deliverIds[i], projectDeliverList.get(i).getUploaddelivery(), projectDeliverList.get(i).getUploaddeliveryFileName());
//                
//                int isCallBack = isCB ? 1:0;
//            }
//        }
//        return SUCCESS;
//    }
	
    /**
     * 上传工程交付件
     * 
     * @return
     */
    public String upload() {
        if(projectDeliverList != null && projectDeliverList.size() > 0){
            String[] deliverIds = projectDeliver.getDeliverId().split(",");
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
                presalesService.uploadFile(deliver, deliverIds[i], pd.getUploaddelivery(), pd.getUploaddeliveryFileName());
            }
            return SUCCESS;
        } else if (StringUtils.isNotBlank(projectDeliver.getEventKey())) {
            String ek = null;
            try{
                ek = projectDeliver.getEventKey();//获取事件节点
                String[] eksplit = ek.split("-");
                projectDeliver.setDataTypeCode(eksplit[0]);
                projectDeliver.setBasicDataId(eksplit[1]);
                projectDeliverList = presalesService.queryProjectDeliverList(projectDeliver);
            }catch(Exception e){
                setErrmsg(ExceptionUtils.getStackTrace(e));
                return ERROR;
            }
        }
        return "upload";
    }
    
    public String deleteDeliverById() {
        try{
            int pId = presalesService.deleteDeliverById(fileId);
        }catch(Exception e){
            fileId = 0;
        }
        return SUCCESS;
    }
    
    public String updateDeliverById() {
        try{
            presalesService.updateProjectDeliverById(projectDeliver);
        }catch(Exception e){
            fileId = 0;
        }
        return SUCCESS;
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
		pmClosedLoopQuesnaire = pmClosedLoopQuesnaireService
				.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
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
		pmClosedLoopQuesnaireList = pmClosedLoopQuesnaireService.selectQuesnaireHeaderList(quesObj, displayParam);
	}

	private List<BasicDataBean> quesTypeList;

	private void getCbForm(int quesnaireId) {
		if (quesnaireId != 0) {

			// 2.复制给pmClosedLoopQuesnaire传递需要的问卷模板信息
			int templateId = callBackService.queryQuesnaireTemplateId(quesnaireId);

			if (pmClosedLoopQuesnaire == null) {
				pmClosedLoopQuesnaire = new PmClosedLoopQuesnaire();
				pmClosedLoopQuesnaire.setId(templateId);
				;
			}
			// 3.判断选择的问卷模板是否等于已有草稿问卷的模板，等于则获取问卷结果行信息
			if (templateId == pmClosedLoopQuesnaire.getId()) {
				pmClQuesnaireResultLine = new PmClQuesnaireResultLine();
				pmClQuesnaireResultLine.setQuesnaireResultHeaderId(quesnaireId);
				pmClQuesnaireResultLineList = pmClosedLoopService.queryPmClQuesResultLineList(pmClQuesnaireResultLine);
			}

			// 问卷状态 已提交 1 草稿-1
			if (presales.getQuesnaireState() != -1) {
				// 获取问卷结果信息
				quesTypeList = basicDataService.queryBasicDataBeanAll(PmClosedLoopConstant.CL_QUESNAIRE_LINEID); // 获取问题类型
				presales.setQuesResultMarkList(getQuesTypeScore(pmClQuesnaireResultLineList));

				// 获取总分以及是否通过
				if (pmClQuesnaireResultHeader == null) {
					pmClQuesnaireResultHeader = new PmClQuesnaireResultHeader();
				}
				pmClQuesnaireResultHeader.setId(presales.getQuesnaireId());
				pmClQuesnaireResultHeader = pmClosedLoopService.queryPmClQuesResultHeaderList(pmClQuesnaireResultHeader)
						.get(0);
			}
		}

		// 1.获取问卷模板头信息
		pmClosedLoopQuesnaire = pmClosedLoopQuesnaireService
				.selectQuesnaireHeaderList(pmClosedLoopQuesnaire, displayParam).get(0);
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

		if (quesTypeList != null) {
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

	public String updateConfirmFiles() {
		try {
			if (fileId != 0) {
				presalesService.updatePrealesFileIds(presales.getPresalesId(), presalesTaskId, fileId);
				message = "删除成功!";
			}
		} catch (Exception e) {
			e.printStackTrace();
			message = "删除失败!";
		}
		return SUCCESS;
	}

	public String exportPresales() {
		try {
			user = UserContext.getUserContext().getUser();
			if (displayParam == null) {
				displayParam = new DisplayParam();
			}
			displayParam.getParam();
			if (presales == null) {
				presales = new Presales();
				UserContext context = UserContext.getUserContext();
				if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER)
						|| context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF)) {
					presales.setProjectStates(MessageUtil.PROJECT_STATE_CREATING);
				}
			}
			String export = getServletRequest().getParameter("6578706f7274");
			if ("1".equals(export)) {
				presalesExportVOList = presalesService.queryPresalesExportData(presales);
				displayParam.setPagesize(presalesExportVOList.size());
				displayParam.setTotalcount(presalesExportVOList.size());
			} else {
				presalesList = presalesService.queryPresalesList(presales, displayParam);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "list";
	}
	
	public void setBasicDataService(BasicDataService basicDataService) {
		this.basicDataService = basicDataService;
	}

	public void setPmClosedLoopQuesnaireService(PmClosedLoopQuesnaireService pmClosedLoopQuesnaireService) {
		this.pmClosedLoopQuesnaireService = pmClosedLoopQuesnaireService;
	}

	public void setPresalesService(PresalesService presalesService) {
		this.presalesService = presalesService;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}

	public Presales getPresales() {
		return presales;
	}

	public void setPresales(Presales presales) {
		this.presales = presales;
	}

	public List<PresalesProduct> getProductList() {
		return productList;
	}

	public void setProductList(List<PresalesProduct> productList) {
		this.productList = productList;
	}

	public PresalesComment getParam() {
		return param;
	}

	public void setParam(PresalesComment param) {
		this.param = param;
	}

	public List<PresalesComment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<PresalesComment> commentList) {
		this.commentList = commentList;
	}

	public List<Presales> getPresalesList() {
		return presalesList;
	}

	public void setPresalesList(List<Presales> presalesList) {
		this.presalesList = presalesList;
	}

	public DisplayParam getDisplayParam() {
		return displayParam;
	}

	public void setDisplayParam(DisplayParam displayParam) {
		this.displayParam = displayParam;
	}

	public Map<Integer, String> getFileMap() {
		return fileMap;
	}

	public void setFileMap(Map<Integer, String> fileMap) {
		this.fileMap = fileMap;
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

	public void setCallBackService(CallBackService callBackService) {
		this.callBackService = callBackService;
	}

	public void setPmClosedLoopService(PmClosedLoopService pmClosedLoopService) {
		this.pmClosedLoopService = pmClosedLoopService;
	}

	public PmClQuesnaireResultLine getPmClQuesnaireResultLine() {
		return pmClQuesnaireResultLine;
	}

	public void setPmClQuesnaireResultLine(PmClQuesnaireResultLine pmClQuesnaireResultLine) {
		this.pmClQuesnaireResultLine = pmClQuesnaireResultLine;
	}

	public List<Department> getOfficeList() {
		return officeList;
	}

	public void setOfficeList(List<Department> officeList) {
		this.officeList = officeList;
	}

	public List<BasicDataBean> getProjectStateList() {
		return projectStateList;
	}

	public void setProjectStateList(List<BasicDataBean> projectStateList) {
		this.projectStateList = projectStateList;
	}

	public List<BasicDataBean> getProjectTypeList() {
		return projectTypeList;
	}

	public void setProjectTypeList(List<BasicDataBean> projectTypeList) {
		this.projectTypeList = projectTypeList;
	}

	public void setDepartmentManageService(DepartmentManageService departmentManageService) {
		this.departmentManageService = departmentManageService;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public List<PresalesTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<PresalesTask> taskList) {
		this.taskList = taskList;
	}

	public String getUrlParams() {
		return urlParams;
	}

	public void setUrlParams(String urlParams) {
		this.urlParams = urlParams;
	}

	public int getPresalesTaskId() {
		return presalesTaskId;
	}

	public void setPresalesTaskId(int presalesTaskId) {
		this.presalesTaskId = presalesTaskId;
	}

	public Date getTaskFinshedTime() {
		return taskFinshedTime;
	}

	public void setTaskFinshedTime(Date taskFinshedTime) {
		this.taskFinshedTime = taskFinshedTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}

	public String getPresalesCode() {
		return presalesCode;
	}

	public void setPresalesCode(String presalesCode) {
		this.presalesCode = presalesCode;
	}
	
	public boolean getContainRma() {
        return containRma;
    }

    public void setContainRma(boolean containRma) {
        this.containRma = containRma;
    }

    public List<ShipmentInfo> getShipmentInfos() {
		return shipmentInfos;
	}

	public void setShipmentInfos(List<ShipmentInfo> shipmentInfos) {
		this.shipmentInfos = shipmentInfos;
	}

	public String getPresalesIds() {
		return presalesIds;
	}

	public void setPresalesIds(String presalesIds) {
		this.presalesIds = presalesIds;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<PresalesExportVO> getPresalesExportVOList() {
		return presalesExportVOList;
	}

	public void setPresalesExportVOList(List<PresalesExportVO> presalesExportVOList) {
		this.presalesExportVOList = presalesExportVOList;
	}

    public List<Map<String, Object>> getCommonList() {
        return commonList;
    }

    public void setCommonList(List<Map<String, Object>> commonList) {
        this.commonList = commonList;
    }

}
