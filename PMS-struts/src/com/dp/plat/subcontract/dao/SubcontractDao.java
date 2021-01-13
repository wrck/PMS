package com.dp.plat.subcontract.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;

import com.dp.plat.data.bean.Project;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.subcontract.entity.SubcontractCallback;
import com.dp.plat.subcontract.entity.SubcontractDeliver;
import com.dp.plat.subcontract.entity.SubcontractFacilitator;
import com.dp.plat.subcontract.entity.SubcontractLine;
import com.dp.plat.subcontract.entity.SubcontractPayment;
import com.dp.plat.subcontract.entity.SubcontractPrice;
import com.dp.plat.subcontract.entity.SubcontractProject;
import com.dp.plat.subcontract.vo.SubcontractDeliverVO;
import com.dp.plat.subcontract.vo.SubcontractEvaluationHeader;
import com.dp.plat.subcontract.vo.SubcontractPageParam;
import com.dp.plat.subcontract.vo.SubcontractProjectVO;

public interface SubcontractDao {
	/**
	 * @param subcontractId
	 * @return
	 */
	SubcontractProject selectSubcontractProjectById(Integer subcontractId);

	/**
	 * @param subcontractId
	 * @return
	 */
	SubcontractProjectVO selectSubcontractProjectVOById(Integer subcontractId);

	/**
	 * @param subcontractProject
	 * @return
	 */
	List<SubcontractProject> selectSubcontractProjectList(SubcontractProject subcontractProject);

	/**
	 * @param subcontractProject
	 * @return
	 */
	List<SubcontractProjectVO> selectSubcontractProjectVOList(SubcontractProject subcontractProject);

	/**
	 * @param displayParam
	 * @return
	 */
	List<SubcontractProjectVO> selectSubcontractProjectVOListPageable(SubcontractPageParam displayParam);
	
	/**
	 * @param displayParam
	 * @return
	 */
	Integer countSubcontractProjectVOListPageable(SubcontractPageParam displayParam);
	
	/**
	 * @param subcontractFacilitator
	 * @return
	 */
	List<SubcontractFacilitator> selectSubcontractFacilitatorList(SubcontractFacilitator subcontractFacilitator);

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
	 * 
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
	 * @param subcontractLine
	 * @return
	 */
	List<SubcontractLine> selectSubcontractLineList(SubcontractLine subcontractLine);

	/**
	 * @param deliverId
	 * @return
	 */
	SubcontractDeliver selectSubcontractDeliverById(Integer deliverId);

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
	 * @param subcontract
	 */
	void updateSubcontractProjectByIdSelective(SubcontractProject subcontract);

	/**
	 * @param params
	 */
	void batchInsertSubcontractLine(HashMap<String, String> params);

	/**
	 * @param params
	 */
	void batchDeleteSubcontractLine(HashMap<String, String> params);

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
	 * @param pmScEvaluationHeader
	 * @return
	 */
	int insertSubcontractEvaluationHeader(SubcontractEvaluationHeader pmScEvaluationHeader);

	/**
	 * @param procInstId
	 */
	void updateSubcontractEvaluationHeader(SubcontractEvaluationHeader pmScEvaluationHeader);

	/**
	 * @param params
	 * @return
	 */
	List<Task> querySubcontractTaskList(HashMap<String, Object> params);

	/**
	 * @param payment
	 * @return
	 */
	List<SubcontractPayment> selectSubcontractPaymentList(SubcontractPayment payment);

	/**
	 * @param subcontractPayment
	 */
	void insertSubcontractPayment(SubcontractPayment subcontractPayment);

	/**
	 * @param id
	 */
	void deleteSubcontractPaymentById(Integer id);

	/**
	 * @param subcontractPayment
	 */
	void updateSubcontractPaymentByIdSelective(SubcontractPayment subcontractPayment);

	/**
	 * @param callback
	 */
	void insertSubcontractCallback(SubcontractCallback callback);

	/**
	 * @param callback
	 */
	void insertSubcontractCallbackSelective(SubcontractCallback callback);

	/**
	 * @param callback
	 * @return
	 */
	int queryCallBackId(SubcontractCallback callback);

	/**
	 * @param callback
	 * @return
	 */
	int queryCallBackQuesnaireId(SubcontractCallback callback);

	/**
	 * @param id
	 * @return
	 */
	int queryCallBackQuesnaireVersion(Integer id);

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

	/**
	 * @param subcontractCallback
	 */
	void updateSubcontractCallbackByIdSelective(SubcontractCallback subcontractCallback);

	/**
	 * @param subcontractId
	 * @return
	 */
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
	 * @param projectIds
	 * @return
	 */
	List<SubcontractProjectVO> querySubcontractInfoForProject(String projectIds);

	/**
	 * @param price
	 * @return
	 */
	void insertSubcontractPrice(SubcontractPrice price);

	/**
	 * @param price
	 */
	void updateSubcontractPriceByIdSelective(SubcontractPrice price);

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
	 * 查询转包项目已付金额
	 * @param subcontractId
	 * @return
	 */
	String querySubcontractPaiedAmmount(Integer subcontractId);

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
     * 删除指定转包项目的空付款信息
     * @param subcontractIds
     */
    void deleteEmptySubcontractPayment(String subcontractIds);

    /**
     * 更新同步SSE付款信息后未付款的付款时间和备注
     */
    void updateSSESubcontractPaymentTime();

}
