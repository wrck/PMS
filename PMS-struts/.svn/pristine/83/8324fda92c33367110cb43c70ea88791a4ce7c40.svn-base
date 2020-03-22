package com.dp.plat.dao;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.context.UserContext;
import com.dp.plat.data.bean.Presales;
import com.dp.plat.data.bean.PresalesComment;
import com.dp.plat.data.bean.PresalesProduct;
import com.dp.plat.data.bean.PresalesTask;
import com.dp.plat.data.bean.ShipmentInfo;
import com.dp.plat.data.vo.PresalesExportVO;
import com.dp.plat.param.DisplayParam;
import com.dp.plat.util.MessageUtil;

public class PresalesDaoImpl extends BaseDao implements PresalesDao {

	@Override
	public Presales queryPresalesById(int presalesId) {
		return (Presales) getSqlMapClientTemplate().queryForObject("query_presales_byid", presalesId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PresalesProduct> queryPresalesProductByPresalesId(int presalesId) {
		return getSqlMapClientTemplate().queryForList("query_presalesproduct_by_presalesid", presalesId);
	}

	@Override
	public void invalidProjectMember(int objId, String memberRole) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("objId", objId);
		params.put("memberRole", memberRole);
		getSqlMapClientTemplate().update("update_invalid_member_bymemberRole", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Presales> queryPresalesList(Presales presales, DisplayParam displayParam)
			throws UnsupportedEncodingException {
		UserContext context = UserContext.getUserContext();
		if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_ADMIN)
				|| context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF)) {
			// 搜索权限条件不变
		} else if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
			// 只能搜服务经理或项目经理是当前用户的
			presales.setServiceManager(getCurrUsername());
		} else if (context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)) {
			// 只能搜项目经理是当前用户的
			presales.setProjectManager(getCurrUsername());
		} else {
			return null;
		}
		// 项目经理或者服务经理搜索时，不限制以下特殊情况权限特殊处理
		Presales tempPresales = presales;
//        if (!(context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_ADMIN) || context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF))) {
//            // 项目名搜索，只限制办事处，不限制是否指派
//            if (StringUtils.isNotBlank(presales.getProjectName()) && (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER) || context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER))) {
//                presales = new Presales();
//                BeanUtils.copyProperties(tempPresales, presales, new String[] { "projectManager", "serviceManager"});
//            }
//        }
		int totalcount = (Integer) getSqlMapClientTemplate().queryForObject("query_presales_count", presales);
		displayParam.setPagesize(50);
		if (displayParam.getExport()) {
			displayParam.setPagesize(totalcount);
		}
		displayParam.setOffset((displayParam.getCurrentpage() - 1) * displayParam.getPagesize());
		displayParam.setTotalcount(totalcount);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("presales", presales);
		paramMap.put("displayParam", displayParam);
		List<Presales> list = getSqlMapClientTemplate().queryForList("query_presales_list", paramMap);
		
		presales = tempPresales;
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PresalesComment> queryActComment(int presalesId, String procdefKey) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("objId", presalesId);
		param.put("procdefKey", procdefKey);
		return getSqlMapClientTemplate().queryForList("query_act_comment_list", param);
	}

	@Override
	public void updatePresaleHeader(Presales presales) {
		getSqlMapClientTemplate().update("update_presales_header", presales);
	}

	@Override
	public int queryCallBackQuesnaireId(Presales presales) {
		Object obj = getSqlMapClientTemplate().queryForObject("query_presales_callbackId", presales);
		return (Integer) (obj == null ? 0 : obj);
	}

	@Override
	public void updateCallBackQuesnaire(int callbackQuesnaireId, int pmClQuesnaireResultHeaderId, int status) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("callbackQuesnaireId", callbackQuesnaireId);
		paramMap.put("quesnaireId", pmClQuesnaireResultHeaderId);
		paramMap.put("quesnaireState", status);
		paramMap.put("updateBy", getCurrUsername());
		paramMap.put("updateTime", new Date());
		getSqlMapClientTemplate().update("update_presales_quesnaire", paramMap);
	}

	@Override
	public int queryCallBackQuesnaireVersion(int presalesId) {
		Object obj = getSqlMapClientTemplate().queryForObject("query_presales_version", presalesId);
		return obj == null ? 1 : (Integer) obj + 1;
	}

	@Override
	public void insertCallBackQuesnaire(Map<String, Object> paramMap) {
		getSqlMapClientTemplate().insert("insert_presales_quesnaire", paramMap);
	}

	@Override
	public int queryQuesnaireIdBycallbackId(Presales presales) {
		Object obj = getSqlMapClientTemplate().queryForObject("query_presales_quesnaireId", presales);
		return obj == null ? 1 : (Integer) obj;
	}

	@Override
	public void updatePresalesState(Map<String, Object> map) {
		getSqlMapClientTemplate().update("update_presales_state", map);
	}

	@Override
	public int queryPresalesCodeNum(int presalesId) {
		return (Integer) getSqlMapClientTemplate().queryForObject("query_presales_code_num", presalesId);
	}

	@Override
	public void updatePresalesCode(int presalesId, int num) {
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("presalesId", presalesId);
		paramMap.put("num", num);
		getSqlMapClientTemplate().update("update_presales_code", paramMap);
	}

	@Override
	public void updatePresalesProduct(int presalesId) {
		getSqlMapClientTemplate().update("update_presales_product", presalesId);
	}

	@Override
	public boolean queryIsHasProjectTask(int presalesId, int typeOfPresales) {
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("projectId", presalesId);
		paramMap.put("projectType", typeOfPresales);
		return (Integer) getSqlMapClientTemplate().queryForObject("query_presales_task_size", paramMap) == 0 ? false
				: true;
	}

	@Override
	public void insertPresaleTasks(int presalesId, int projectType, String basicDataProjectType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("projectId", presalesId);
		paramMap.put("projectType", projectType);
		paramMap.put("taskTypeCode", basicDataProjectType);
		paramMap.put("createBy", getCurrUsername());
		getSqlMapClientTemplate().insert("insert_presales_tasks", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PresalesTask> queryPresalesTaskList(int presalesId, int projectType) {
		Map<String, Integer> paramMap = new HashMap<String, Integer>();
		paramMap.put("projectId", presalesId);
		paramMap.put("projectType", projectType);
		return getSqlMapClientTemplate().queryForList("query_presales_en_task", paramMap);
	}

	@Override
	public void updatePresalesTaskDeliverFiles(int taskId, String fileIds) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("taskId", taskId);
		paramMap.put("fileIds", fileIds);
		getSqlMapClientTemplate().update("update_presales_task_files", paramMap);
	}

	@Override
	public void updatePresalesTask(Date taskFinshedTime, int presalesTaskId) {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("taskId", presalesTaskId);
//		paramMap.put("taskFinshedTime", taskFinshedTime);
//		getSqlMapClientTemplate().update("update_presales_task_finshedtime", paramMap);
		this.updatePresalesTask(taskFinshedTime, null, presalesTaskId);
	}
	
	@Override
	public void updatePresalesTask(Date taskFinshedTime, String remark, int presalesTaskId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("taskId", presalesTaskId);
		paramMap.put("taskFinshedTime", taskFinshedTime);
		paramMap.put("remark", remark);
		getSqlMapClientTemplate().update("update_presales_task_finshedtime", paramMap);
	}

	@Override
	public void updatePresalesConfirmFileIds(int presalesId, String fileIds) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("presalesid", presalesId);
		paramMap.put("fileIds", fileIds);
		getSqlMapClientTemplate().update("update_presales_confirmfiles", paramMap);
	}

	@Override
	public void updatePrealesFileIds(int presalesId, int taskId, int fileId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("presalesId", presalesId);
		paramMap.put("projectId", presalesId);
		paramMap.put("taskId", taskId);
		paramMap.put("fileId", fileId);
		getSqlMapClientTemplate().update("update_presales_confirmfiles_delete", paramMap);
		getSqlMapClientTemplate().update("update_presales_task_deliverFileIds_delete", paramMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode) {
//		if (StringUtils.isNotBlank(projectCode)) {
//			projectCode = projectCode.split("-")[0];
//		} else {
//			return new ArrayList<>();
//		}
//		return getSqlMapClientTemplate().queryForList("query_presale_shipmentInfo", projectCode);
	    return this.queryPresaleShipmentInfo(projectCode, Boolean.FALSE);
	}
	
	@Override
	public List<ShipmentInfo> queryPresaleShipmentInfo(String projectCode, boolean containRma) {
        if (StringUtils.isNotBlank(projectCode)) {
            projectCode = projectCode.split("-")[0];
        } else {
            return new ArrayList<>();
        }
        Map<String, Object> params = new HashMap<>();
        params.put("projectCode", projectCode);
        params.put("containRma", containRma);
        return getSqlMapClientTemplate().queryForList("query_presale_shipmentInfo", params);
    }
	
	

	@Override
    public List<Map<String, Object>> queryPresaleLend2SaleInfo(String projectCode) {
	    if (StringUtils.isNotBlank(projectCode)) {
            projectCode = projectCode.split("-")[0];
        } else {
            return new ArrayList<>();
        }
        return getSqlMapClientTemplate().queryForList("query_presale_lend_2_sale", projectCode);
    }

    @Override
    public List<Map<String, Object>> queryPresaleLend2RmaInfo(String projectCode) {
        if (StringUtils.isNotBlank(projectCode)) {
            projectCode = projectCode.split("-")[0];
        } else {
            return new ArrayList<>();
        }
        return getSqlMapClientTemplate().queryForList("query_presale_lend_2_rma", projectCode);
    
    }

    @Override
	public List<PresalesExportVO> queryPresalesExportData(Presales presales) {
		UserContext context = UserContext.getUserContext();
		if (context.isHasRole(MessageUtil.ROLE_ENGINEEMANAGER) || context.isHasRole(MessageUtil.ROLE_ADMIN)
				|| context.isHasRole(MessageUtil.ROLE_PRESALES_STAFF)) {
			// 搜索权限条件不变
		} else if (context.isHasRole(MessageUtil.ROLE_SERVICEMANAGER)) {
			// 只能搜服务经理或项目经理是当前用户的
			presales.setServiceManager(getCurrUsername());
		} else if (context.isHasRole(MessageUtil.ROLE_PROGRAMMANAGER)) {
			// 只能搜项目经理是当前用户的
			presales.setProjectManager(getCurrUsername());
		} else {
			return null;
		}
		List<PresalesExportVO> list;
		if ("1".equals(presales.getExportDetail())) {
		    getSqlMapClientTemplate().insert("createTempPresalesProductLine");
    		list = getSqlMapClientTemplate().queryForList("queryPresalesExportData", presales);
    		getSqlMapClientTemplate().delete("deleteTempPresalesProductLine");
		} else if ("2".equals(presales.getExportDetail())) {
		    String quesType = "presales";
            // String resultType = "1"; // 1-单选，3-文本框
		    Map<String, Object> questionColumns = this.queryQuestionColumns(quesType, null);
		    // 将问卷结果转为 字符串拼接，默认为多列
		    questionColumns.put("tempType", "oneColumn");
		    presales.setQuestionColumns(questionColumns);
		    getSqlMapClientTemplate().insert("createTempQuesnaireResultTable", questionColumns);
            list = getSqlMapClientTemplate().queryForList("queryPresalesExportData", presales);
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultLineTable", quesType);
            getSqlMapClientTemplate().delete("deleteTempQuesnaireResultTable", quesType);
		} else {
		    list = getSqlMapClientTemplate().queryForList("queryPresalesExportData", presales);
		}
        return list;
	}

    @Override
    public void updatePresalesDuration(int presalesId) {
        getSqlMapClientTemplate().insert("updatePresalesDuration", presalesId);
    }
    
    private Map<String, Object> queryQuestionColumns (String quesType, String resultType) {
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
}
