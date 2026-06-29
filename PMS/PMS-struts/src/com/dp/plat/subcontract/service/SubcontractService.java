package com.dp.plat.subcontract.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.task.Task;

import com.dp.plat.data.activity.ActComment;
import com.dp.plat.data.bean.PmClQuesnaireResultHeader;
import com.dp.plat.data.bean.PmClQuesnaireResultLine;
import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.bean.WorkflowCommonParam;
import com.dp.plat.data.vo.Result;
import com.dp.plat.service.BaseService;
import com.dp.plat.subcontract.entity.SubcontractCallback;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractFacilitator;
import com.dp.plat.subcontract.entity.SubcontractLine;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.vo.SubcontractComment;
import com.dp.plat.subcontract.vo.SubcontractDeliverVO;
import com.dp.plat.subcontract.vo.SubcontractEvaluationHeader;
import com.dp.plat.subcontract.vo.SubcontractPageParam;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;

public interface SubcontractService extends BaseService {

	/**
	 * 查询列表
	 * 
	 * @param subcontractProject
	 */
	List<SubcontractProject> selectSubcontractProjectList(SubcontractProject subcontractProject);

	/**
	 * @param subcontractProject
	 * @return
	 */
	List<SubcontractProjectVO> selectSubcontractProjectVOList(SubcontractProject subcontractProject);

	/**
	 * 查询服务商列表
	 * 
	 * @param subcontractFacilitator
	 * @return
	 */
	List<SubcontractFacilitator> selectSubcontractFacilitatorList(SubcontractFacilitator subcontractFacilitator);

	/**
	 * @param contractNos
	 * @param projectIds
	 * @return
	 */
	List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String contractNos, String projectIds);

	/**
	 * @param contractNos
	 * @param projectIds
	 * @param excludeTransferOut
	 * @return
	 */
	List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String contractNos, String projectIds,
			boolean excludeTransferOut);

	/**
	 * 查询转包项目序列号，必须包含字段
 	 * @param contractNos
	 * @param projectIds
	 * @param excludeTransferOut, 默认false
	 * @param contractProfitCenter, 合同号和办事处的对应关系，主要在总代借货时用来拆分发货数据
	 * @return
	 */
	List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(Map<String, Object> params);

	/**
	 * @param project
	 * @return
	 */
	List<Project> queryProjectList(Project project);

	/**
	 * @param subcontract
	 * @return
	 */
	List<Project> queryProjectList(SubcontractProject subcontract);

	/**
	 * @param subcontractName
	 * @return
	 */
	String checkSubcontractName(String subcontractName);

	/**
	 * @param subcontract
	 * @return
	 */
	String checkSubcontractName(SubcontractProject subcontract);
	
	/**
	 * @param subcontract
	 */
	void insertSubcontractProject(SubcontractProject subcontract);

	/**
	 * @param subcontract
	 */
	void insertSubcontractProjectSelective(SubcontractProject subcontract);
	
	/**
	 * @param subcontract
	 * @param subcontractDeliverList
	 * @param uploadFiles
	 */
	void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			List<SubcontractDeliver> subcontractDeliverList, File[] uploadFiles);

	/**
	 * @param subcontract
	 * @param subcontractLineList
	 * @param uploadFiles
	 * @param deliverNames
	 * @param deliverTypes
	 */
	void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			File[] uploadFiles, String[] deliverNames, String[] deliverTypes);

	/**
	 * @param subcontractLine
	 * @return
	 */
	List<SubcontractLine> selectSubcontractLineList(SubcontractLine subcontractLine);

	/**
	 * @param integer
	 * @return
	 */
	SubcontractDeliver selectSubcontractDeliverById(Integer integer);
	
	/**
	 * @param deliver
	 * @return
	 */
	List<SubcontractDeliver> selectSubcontractDeliverList(SubcontractDeliver deliver);
	
	/**
	 * @param deliver
	 * @return
	 */
	List<SubcontractDeliverVO> selectSubcontractDeliverVOList(SubcontractDeliver deliver);

	/**
	 * @param deliver
	 */
	void insertSubcontractDeliver(SubcontractDeliver deliver);
	
    /**
     * @param subcontractDeliver
     */
    void updateSubcontractDeliverByIdSelective(SubcontractDeliver subcontractDeliver);
	
	/**
     * 删除转包交付件
     * @param subcontractDeliverVO
     * @return
     */
	void deleteSubcontractDeliver(SubcontractDeliverVO subcontractDeliverVO);

	/**
	 * @param subcontract
	 * @param subcontractLineList
	 * @param uploadDeliverList
	 */
	void createSubcontractProject(SubcontractProject subcontract, List<SubcontractLine> subcontractLineList,
			List<SubcontractDeliverVO> uploadDeliverList);

	/**
	 * @param subcontract
	 * @param uploadDeliverList
	 */
	void updateSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList);

	/**
	 * @param subcontract
	 */
	void updateSubcontractProjectByIdSelective(SubcontractProject subcontract);

	/**
	 * @param subcontract
	 * @param uploadDeliverList
	 */
	void createSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList);

	/**
	 * @param subcontractId
	 * @param uploadDeliverList
	 * @return
	 */
	boolean saveDeliverFiles(Integer subcontractId, List<SubcontractDeliverVO> uploadDeliverList);

	/**
	 * @param id
	 * @return
	 */
	SubcontractProject selectSubcontractProjectById(Integer id);

	/**
	 * @param subcontract
	 * @param uploadDeliverList
	 * @param selected
	 */
	void createSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList,
			String[] selected);

	/**
	 * @param subcontract
	 * @param uploadDeliverList
	 * @param selected
	 */
	void updateSubcontractProject(SubcontractProject subcontract, List<SubcontractDeliverVO> uploadDeliverList,
			String[] selected);

	/**
	 * @param contractNos
	 * @return
	 */
	List<Map<String, Object>> queryContractNoEngineeFee(String contractNos);
	
	/**
	 * 查询工程服务费，附带转包价
	 * @param contractNos
	 * @param subcontractId
	 * @return
	 */
	List<SubcontractPrice> queryContractNoEngineeFeeWithSubPrice(String contractNos, Integer subcontractId);

	/**
	 * @param workflowCommonParam
	 * @param pmClEvaluationHeader
	 * @param subcontract
	 * @return
	 */
	String startSubcontractFlow(WorkflowCommonParam workflowCommonParam,
			SubcontractEvaluationHeader pmClEvaluationHeader, SubcontractProject subcontract);

	/**
	 * @param id
	 * @return
	 */
	Task queryCurrentTask(Integer id);

	/**
	 * @param subcontractId
	 * @return
	 */
	// SubcontractComment queryCurrentTaskParam(Integer subcontractId);

	/**
	 * @param subcontract
	 * @return
	 */
	String startSubcontractFlow(SubcontractProject subcontract);

	/**
	 * 受益部门服务经理审批
	 * @param workflowCommonParam
	 * @param subcontract
	 * @return
	 */
	String profitSerivceManagerFlow(WorkflowCommonParam workflowCommonParam, SubcontractProject subcontract);
	
	/**
	 * @param id
	 * @return
	 */
	SubcontractProjectVO selectSubcontractProjectVOById(Integer id);

	/**
	 * {@link Deprecated} SubcontractEvaluationHeader 参数弃用
	 * @param taskParam
	 * @param pmClEvaluationHeader
	 * @param subcontract
	 * @return
	 */
	@Deprecated
	String auditSubcontractFlow(WorkflowCommonParam taskParam, SubcontractEvaluationHeader pmClEvaluationHeader,
			SubcontractProject subcontract);

	/**
	 * @param taskParam
	 * @param subcontract
	 * @param subcontractPriceList
	 * @return
	 */
	String auditSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract,
			List<SubcontractPrice> subcontractPriceList);
	
	/**
	 * @param taskParam
	 * @param subcontract
	 * @return
	 */
	String auditSubcontractFlow(SubcontractComment taskParam, SubcontractProject subcontract);

	/**
	 * @param subcontractId
	 * @return
	 */
	SubcontractComment queryCurrentSubcontractCommon(Integer subcontractId);

	/**
	 * @param subcontractId
	 * @return
	 */
	WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId);

	/**
	 * @param taskParam
	 * @param pmClEvaluationHeader
	 * @param subcontract
	 * @return
	 */
	String closeSubcontractFlow(WorkflowCommonParam taskParam, SubcontractEvaluationHeader pmClEvaluationHeader,
			SubcontractProject subcontract);

	/**
	 * @param subcontractId
	 * @return
	 */
	SubcontractCallback startCallBackFlow(Integer subcontractId);
	
	/**
	 * @param subcontractId
	 * @param comment
	 * @return
	 */
	SubcontractCallback startCallBackFlow(Integer subcontractId, ActComment comment);

	/**
	 * 回访人员回访，单独的回访流程
	 * @param workflowCommonParam
	 * @param subcontractCallback
	 * @return
	 */
	String submitCallBackFlow(WorkflowCommonParam workflowCommonParam, SubcontractCallback subcontractCallback);

	/**
	 * 回访人员回访，在一个流程中
	 * @param taskParam
	 * @param subcontractCallback
	 * @return
	 */
	String submitCallBackFlow2(WorkflowCommonParam taskParam, SubcontractCallback subcontractCallback);
	
	/**
	 * @param payment
	 * @return
	 */
	List<SubcontractPayment> selectSubcontractPaymentList(SubcontractPayment payment);
	
	/**
	 * 
	 * @param paymentId
	 * @return
	 */
    SubcontractPayment selectSubcontractPaymentById(Integer paymentId);

	/**
	 * @param subcontractPaymentList
	 * @return
	 */
	void saveSubcontractPayment(List<SubcontractPayment> subcontractPaymentList);

	/**
	 * @param subcontractPayment
	 */
	void insertSubcontractPayment(SubcontractPayment subcontractPayment);

	/**
	 * @param subcontractPaymentList
	 * @param delIds
	 */
	void saveSubcontractPayment(List<SubcontractPayment> subcontractPaymentList, Integer[] delIds);
	
    /**
     * @param subcontractPayment
     */
    void updateSubcontractPaymentByIdSelective(SubcontractPayment subcontractPayment);

	/**
	 * @param subcontractId
	 * @return
	 */
	String querySubcontractPaiedAmount(Integer subcontractId);
	
	/**
	 * @param subcontractId
	 * @param taskKey
	 * @return
	 */
	WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey);
	
	/**
	 * 
	 * @param subcontractId
	 * @param taskKey
	 * @param roleGroup
	 * @return
	 */
	WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey, String roleGroup);

	/**
	 * 
	 * @param subcontractId
	 * @param taskKey
	 * @param roleGroup
	 * @param params
	 * @return
	 */
	WorkflowCommonParam queryCurrentWorkFlowCommonParam(Integer subcontractId, String taskKey, String roleGroup,
			HashMap<String, Object> params);
	/**
	 * @param callback
	 */
	void insertSubcontractCallback(SubcontractCallback callback);

	/**
	 * @param callback
	 * @param pmClQuesnaireResultHeader
	 * @param pmClQuesnaireResultLineList
	 */
	void insertSubcontractQuesnaire(SubcontractCallback callback, PmClQuesnaireResultHeader pmClQuesnaireResultHeader,
			List<PmClQuesnaireResultLine> pmClQuesnaireResultLineList);

	/**
	 * @param subcontractCallback
	 * @return
	 */
	List<SubcontractCallback> selectSubcontractCallbackList(SubcontractCallback subcontractCallback);

	/**
	 * @param subcontractCallback
	 * @return
	 */
	SubcontractCallback selectMaxSubcontractCallback(SubcontractCallback subcontractCallback);

	List<Map<String, Object>> querySubcontractCommentList(Integer subcontractId);

	/**
	 * @param subcontractFacilitator
	 */
	void insertSubcontractFacilitator(SubcontractFacilitator subcontractFacilitator);

	/**
	 * @param subcontractFacilitator
	 */
	void updateSubcontractFacilitatorByIdSelective(SubcontractFacilitator subcontractFacilitator);

	/**
	 * @param id
	 * @return
	 */
	SubcontractFacilitator selectSubcontractFacilitatorById(Integer id);

	/**
	 * @param taskParam
	 * @param subcontract
	 * @return
	 */
	String approveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract);

	/**
	 * 查询项目转包记录，供项目管理中使用
	 * @param projectIds
	 * @return
	 */
	List<SubcontractProjectVO> querySubcontractInfoForProject(String projectIds);

	/**
	 * @param price
	 */
	void insertSubcontractPrice(SubcontractPrice price);

	/**
	 * @param price
	 */
	void updateSubcontractPriceByIdSelective(SubcontractPrice price);

	/**
	 * 生成合同号
	 * @param taskParam
	 * @param subcontract
	 * @return
	 */
	String generateContractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract);

	/**
	 * 服务经理提交付款信息
	 * @param taskParam
	 * @param subcontract
	 * @return
	 */
	String applyPaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract);

	/**
	 * 工程管理部付款
	 * @param taskParam
	 * @param subcontract
	 * @return
	 */
	String approvePaymentFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract);
	
    /**
     * 验收审批流程
     * @param workflowCommonParam
     * @param subcontract
     */
	String submitAcceptanceFlow(WorkflowCommonParam workflowCommonParam, SubcontractProject subcontract);

	/**
	 * 服务经理查询被驳回的转包申请，显示在待办事项中
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectRejectedSubcontractProjectList(HashMap<String, Object> params);

	/**
	 * 转包列表数据导出
	 * @param subcontractVO
	 * @return
	 */
	List<SubcontractProjectVO> querySubcontractExportData(SubcontractProjectVO subcontractVO);

	/**
	 * @param displayParam
	 * @return
	 */
	List<SubcontractProjectVO> selectSubcontractProjectVOListPageable(SubcontractPageParam displayParam);

	/**
	 * 终止流程
	 * @param subcontractId
	 */
	void terminateWorkFlow(Integer subcontractId);

	/**
	 * 终止流程
	 * @param subcontractId
	 * @param comment
	 */
	void terminateWorkFlow(Integer subcontractId, String comment);
	
	/**
     * 终止流程
     * @param subcontractId
     * @param comment
     */
    void terminateWorkFlow(Integer subcontractId, WorkflowCommonParam comment);

	/**
	 * @return
	 */
	List<SubcontractProjectVO> queryNextPaymentTask();

    /**
     * 查询SSE新增的转包付款信息
     * @return
     */
    List<SubcontractPayment> querySSESubcontractPaymentList();

    /**
     * 根据部门查询默认多维度信息
     * @param depNum
     * @return
     */
    Map<String, String> selectDefaultMultiDimByDep(String depNum);

    /**
     * 根据部门查询默认多维度信息
     * @param depNum
     * @param directWithoutChace 直接查询数据库，不查询缓存
     * @return
     */
    Map<String, String> selectDefaultMultiDimByDep(String depNum, boolean directWithoutChace);

    /**
     * 通用审批节点
     * @param taskParam
     * @param subcontract
     * @return
     */
    String normalApproveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract);

    /**
     * 工程管理部主管通用审批节点
     * @param taskParam
     * @param subcontract
     * @return
     */
    String auditNormalApproveSubcontractFlow(WorkflowCommonParam taskParam, SubcontractProject subcontract, List<SubcontractPrice> subcontractPriceList);

    /**
     * 付款附件查验
     * @param payment
     * @return 
     */
    Result verifySubcontractPaymentDeliver(Integer subcontractId, Integer paymentId);

    /**
     * 付款附件查验
     * @param payment
     * @return 
     */
    Result verifySubcontractPaymentDeliver(SubcontractPayment payment);

    /**
     * 更新付款申请对应的发票编号
     * @param paymentIds
     */
    Result updateSubcontractPaymentInvoiceNumber(Set<Integer> paymentIds);

}
