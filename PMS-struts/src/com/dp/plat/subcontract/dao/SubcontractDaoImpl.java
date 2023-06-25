package com.dp.plat.subcontract.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.dp.plat.dao.BaseDao;
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

public class SubcontractDaoImpl extends BaseDao implements SubcontractDao {

	@Override
	public SubcontractProject selectSubcontractProjectById(Integer subcontractId) {
		return (SubcontractProject) getSqlMapClientTemplate().queryForObject("selectSubcontractProjectById",
				subcontractId);
	}

	@Override
	public SubcontractProjectVO selectSubcontractProjectVOById(Integer subcontractId) {
		return (SubcontractProjectVO) getSqlMapClientTemplate().queryForObject("selectSubcontractProjectVOById",
				subcontractId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubcontractProject> selectSubcontractProjectList(SubcontractProject subcontractProject) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractProjectList", subcontractProject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubcontractProjectVO> selectSubcontractProjectVOList(SubcontractProject subcontractProject) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractProjectVOList", subcontractProject);
	}

	@Override
	public List<SubcontractProjectVO> selectSubcontractProjectVOListPageable(SubcontractPageParam displayParam) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractProjectVOListPageable", displayParam);
	}

	@Override
	public Integer countSubcontractProjectVOListPageable(SubcontractPageParam displayParam) {
		return (Integer) getSqlMapClientTemplate().queryForObject("countSubcontractProjectVOListPageable", displayParam);
	}

	@Override
	public List<SubcontractFacilitator> selectSubcontractFacilitatorList(
			SubcontractFacilitator subcontractFacilitator) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractFacilitatorList", subcontractFacilitator);
	}

	@Override
	public List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(String contractNos, String projectIds,
			boolean excludeTransferOut) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("contractNos", contractNos);
		params.put("sourceContractNos", contractNos.replaceAll("(-L)|(-C)", ""));
		params.put("projectIds", projectIds);
		params.put("excludeTransferOut", excludeTransferOut);
		return getSqlMapClientTemplate().queryForList("queryShipmentinfoByContractNosAndProjectIds", params);
	}
	
	@Override
	public List<ShipmentInfo> queryShipmentinfoByContractNosAndProjectIds(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return Collections.emptyList();
		}
		String contractNos = StringUtils.trimToEmpty((String) params.get("contractNos"));
		params.put("sourceContractNos", contractNos.replaceAll("(-L)|(-C)", ""));
		
		List<Map> contractProfitCenter = (List<Map>) params.get("contractProfitCenter");
		if (contractProfitCenter != null && !contractProfitCenter.isEmpty()) {
			boolean hasProfitCenter = false;
			for (Map map : contractProfitCenter) {
				String contractNo = StringUtils.trimToEmpty((String) map.get("contractNo"));
				map.put("sourceContractNo", contractNo.replaceAll("(-L)|(-C)", ""));
				
				String officeCode = StringUtils.trimToEmpty((String) map.get("officeCode"));
				if (StringUtils.isNotBlank(officeCode)) {
					hasProfitCenter = true; 
				}
			}
			// 如果存在利润中心，则contractNos需要去掉-L后缀
			if (hasProfitCenter) {
				params.put("contractNos", contractNos.replaceAll("-L", ""));
			}
		}
		return getSqlMapClientTemplate().queryForList("queryShipmentinfoByContractNosAndProjectIds", params);
	}

	@Override
	public List<Project> queryProjectList(Project project) {
		if (StringUtils.isBlank(project.getContractNo())) {
			return new ArrayList<>();
		}
		String[] contractNoArr = project.getContractNo().split(",");
		List<String> contractNoList = Arrays.asList(contractNoArr);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("contractNos", contractNoList);
		return getSqlMapClientTemplate().queryForList("queryProjectList", params);
	}

	@Override
	public List<Project> queryProjectList(SubcontractProject subcontract) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("projectIds", subcontract.getProjectIds());
		return getSqlMapClientTemplate().queryForList("queryProjectList", params);
	}

	@Override
	public String checkSubcontractName(String subcontractName) {
		SubcontractProject subcontract = new SubcontractProject();
		subcontract.setSubcontractName(subcontractName);
		return checkSubcontractName(subcontract);
	}

	@Override
	public String checkSubcontractName(SubcontractProject subcontract) {
		return String.valueOf(getSqlMapClientTemplate().queryForObject("checkSubcontractName", subcontract));
	}

	@Override
	public void insertSubcontractProject(SubcontractProject subcontract) {
		getSqlMapClientTemplate().insert("insertSubcontractProject", subcontract);
	}
	
	@Override
	public void insertSubcontractProjectSelective(SubcontractProject subcontract) {
		getSqlMapClientTemplate().insert("insertSubcontractProjectSelective", subcontract);
	}

	@Override
	public void updateSubcontractProjectByIdSelective(SubcontractProject subcontract) {
		getSqlMapClientTemplate().update("updateSubcontractProjectByIdSelective", subcontract);
	}

	@Override
	public List<SubcontractLine> selectSubcontractLineList(SubcontractLine subcontractLine) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractLineList", subcontractLine);
	}

	@Override
	public SubcontractDeliver selectSubcontractDeliverById(Integer deliverId) {
		return (SubcontractDeliver) getSqlMapClientTemplate().queryForObject("selectSubcontractDeliverById", deliverId);
	}

	@Override
	public List<SubcontractDeliver> selectSubcontractDeliverList(SubcontractDeliver deliver) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractDeliverList", deliver);
	}

	@Override
	public List<SubcontractDeliverVO> selectSubcontractDeliverVOList(SubcontractDeliver deliver) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractDeliverVOList", deliver);
	}

	@Override
	public List<SubcontractPayment> selectSubcontractPaymentList(SubcontractPayment payment) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractPaymentList", payment);
	}

	@Override
	public void insertSubcontractPayment(SubcontractPayment subcontractPayment) {
		getSqlMapClientTemplate().insert("insertSubcontractPayment", subcontractPayment);
	}

	@Override
	public void deleteSubcontractPaymentById(Integer id) {
		getSqlMapClientTemplate().delete("deleteSubcontractPaymentById", id);
	}

	@Override
	public void updateSubcontractPaymentByIdSelective(SubcontractPayment subcontractPayment) {
		getSqlMapClientTemplate().update("updateSubcontractPaymentByIdSelective", subcontractPayment);
	}

	@Override
	public String querySubcontractPaiedAmmount(Integer subcontractId) {
		return (String) getSqlMapClientTemplate().queryForObject("querySubcontractPaiedAmount", subcontractId);
	}

	@Override
	public void insertSubcontractDeliver(SubcontractDeliver deliver) {
		getSqlMapClientTemplate().insert("insertSubcontractDeliver", deliver);
	}
	
	@Override
	public void deleteSubcontractDeliver(SubcontractDeliverVO subcontractDeliverVO) {
		List<Object> ids = subcontractDeliverVO.getIds();
		for (Object id : ids) {
			if (id == null) {
				continue;
			}
			SubcontractDeliver deliver = new SubcontractDeliver();
			try {
				deliver.setId(Integer.valueOf(id.toString()));
			} catch(NumberFormatException e) {
				continue;
			}
			deliver.setEffectiveTo(new Date());
			getSqlMapClientTemplate().update("updateSubcontractDeliverByIdSelective", deliver);
		}
	}

	@Override
	public void batchInsertSubcontractLine(HashMap<String, String> params) {
		getSqlMapClientTemplate().insert("batchInsertSubcontractLine", params);
	}

	@Override
	public void batchDeleteSubcontractLine(HashMap<String, String> params) {
		getSqlMapClientTemplate().insert("batchDeleteSubcontractLine", params);
	}

	@Override
	public List<Map<String, Object>> queryContractNoEngineeFee(String contractNos) {
		return getSqlMapClientTemplate().queryForList("queryContractNoEngineeFee", contractNos);
	}

	@Override
	public List<SubcontractPrice> queryContractNoEngineeFeeWithSubPrice(String contractNos, Integer subcontractId) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("contractNos", contractNos);
		params.put("subcontractId", subcontractId);
		return getSqlMapClientTemplate().queryForList("queryContractNoEngineeFeeWithSubPrice", params);
	}
	
	@Override
	public int insertSubcontractEvaluationHeader(SubcontractEvaluationHeader evaluationHeader) {
		return (int) getSqlMapClientTemplate().insert("insertSubcontractEvaluationHeader", evaluationHeader);
	}

	@Override
	public void updateSubcontractEvaluationHeader(SubcontractEvaluationHeader evaluationHeader) {
		getSqlMapClientTemplate().update("updateSubcontractEvaluationHeader", evaluationHeader);
	}

	@Override
	public List<Task> querySubcontractTaskList(HashMap<String, Object> params) {
		return getSqlMapClientTemplate().queryForList("querySubcontractActivitiTaskList", params);
	}

	@Override
	public void insertSubcontractCallback(SubcontractCallback callback) {
		getSqlMapClientTemplate().insert("insertSubcontractCallback", callback);
	}

	@Override
	public void insertSubcontractCallbackSelective(SubcontractCallback callback) {
		getSqlMapClientTemplate().insert("insertSubcontractCallbackSelective", callback);
	}

	@Override
	public void updateSubcontractCallbackByIdSelective(SubcontractCallback subcontractCallback) {
		getSqlMapClientTemplate().update("updateSubcontractCallbackByIdSelective", subcontractCallback);
	}

	@Override
	public List<SubcontractCallback> selectSubcontractCallbackList(SubcontractCallback subcontractCallback) {
		return getSqlMapClientTemplate().queryForList("selectSubcontractCallbackList", subcontractCallback);
	}

	@Override
	public SubcontractCallback selectMaxSubcontractCallback(SubcontractCallback subcontractCallback) {
		return (SubcontractCallback) getSqlMapClientTemplate().queryForObject("selectMaxSubcontractCallback",
				subcontractCallback);
	}

	@Override
	public int queryCallBackId(SubcontractCallback callback) {
		Object obj = getSqlMapClientTemplate().queryForObject("queryCallBackId", callback);
		return (Integer) (obj == null ? 0 : obj);
	}

	@Override
	public int queryCallBackQuesnaireId(SubcontractCallback callback) {
		Object obj = getSqlMapClientTemplate().queryForObject("queryCallBackQuesnaireId", callback);
		return (Integer) (obj == null ? 0 : obj);
	}

	@Override
	public int queryCallBackQuesnaireVersion(Integer subcontractId) {
		Object obj = getSqlMapClientTemplate().queryForObject("queryCallBackQuesnaireVersion", subcontractId);
		return obj == null ? 1 : (Integer) obj + 1;
	}

	@Override
	public List<Map<String, Object>> querySubcontractCommentList(Integer subcontractId) {
		return getSqlMapClientTemplate().queryForList("querySubcontractCommentList", subcontractId);
	}

	@Override
	public void insertSubcontractFacilitator(SubcontractFacilitator subcontractFacilitator) {
		getSqlMapClientTemplate().insert("insertSubcontractFacilitator", subcontractFacilitator);
	}

	@Override
	public void updateSubcontractFacilitatorByIdSelective(SubcontractFacilitator subcontractFacilitator) {
		getSqlMapClientTemplate().update("updateSubcontractFacilitatorByIdSelective", subcontractFacilitator);
	}

	@Override
	public SubcontractFacilitator selectSubcontractFacilitatorById(Integer id) {
		return (SubcontractFacilitator) getSqlMapClientTemplate().queryForObject("selectSubcontractFacilitatorById", id);
	}

	@Override
	public List<SubcontractProjectVO> querySubcontractInfoForProject(String projectIds) {
		return getSqlMapClientTemplate().queryForList("querySubcontractInfoForProject", projectIds);
	}

	@Override
	public void insertSubcontractPrice(SubcontractPrice price) {
		getSqlMapClientTemplate().insert("insertSubcontractPrice", price);
	}

	@Override
	public void updateSubcontractPriceByIdSelective(SubcontractPrice price) {
		getSqlMapClientTemplate().update("updateSubcontractPriceByIdSelective", price);
	}

	@Override
	public List<Map<String, Object>> selectRejectedSubcontractProjectList(HashMap<String, Object> params) {
		return getSqlMapClientTemplate().queryForList("selectRejectedSubcontractProjectList", params);
	}

	@Override
	public List<SubcontractProjectVO> querySubcontractExportData(SubcontractProjectVO subcontractVO) {
		return getSqlMapClientTemplate().queryForList("querySubcontractExportData", subcontractVO);
	}

	@Override
	public List<SubcontractProjectVO> queryNextPaymentTask() {
		return getSqlMapClientTemplate().queryForList("queryNextPaymentTask");
	}

    @Override
    public List<SubcontractPayment> querySSESubcontractPaymentList() {
        return getSqlMapClientTemplate().queryForList("querySSESubcontractPaymentList");
    }

    @Override
    public void deleteEmptySubcontractPayment(String subcontractIds) {
        getSqlMapClientTemplate().delete("deleteEmptySubcontractPayment", subcontractIds);
    }
    
    @Override
    public void updateSSESubcontractPaymentTime() {
        getSqlMapClientTemplate().update("updateSSESubcontractPaymentTime");
    }

    @Override
    public Map<String, String> selectDefaultMultiDimByDep(String depNum) {
        return (Map<String, String>) getSqlMapClientTemplateSSE().queryForObject("selectDefaultMultiDimByDep", depNum);
    }

    @Override
    public Map<String, String> selectDefaultMultiDimByDep(String depNum, boolean directWithoutCache) {
//        if ("319000".equals(depNum)) {
//            return JSON.parseObject("{\"dimDepartment\":\"319000\",\"dimTerritoryName\":\"\",\"dimIndustry\":\"\",\"dimBUName\":\"安全检测与服务产品BU\",\"dimBU\":\"14\",\"dimProductLine\":\"1022\",\"dimProductLineName\":\"安全服务\",\"dimIndustryName\":\"\",\"dimDepartmentName\":\"安全咨询服务部（用服）\",\"dimTerritory\":\"\"}", HashMap.class);
//        }
        if (directWithoutCache) {
            return (Map<String, String>) getSqlMapClientTemplateSSE().queryForObject("selectDefaultMultiDimByDepDirect", depNum);
        }
        return (Map<String, String>) getSqlMapClientTemplateSSE().queryForObject("selectDefaultMultiDimByDep", depNum);
    }

}