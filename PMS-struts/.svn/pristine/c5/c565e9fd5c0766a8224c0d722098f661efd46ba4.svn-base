package com.dp.plat.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.param.FileParam;

public class BasicDataDaoImpl extends BaseDao implements BasicDataDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<BasicDataBean> queryBasicDataBeans(String basicDataTypeCode) {
		return getSqlMapClientTemplate().queryForList("query_basic_data", basicDataTypeCode);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BasicDataBean> queryBasicDataType() {
		return getSqlMapClientTemplate().queryForList("query_basic_data_type");
	}

	@Override
	public BasicDataBean queryBasicDataBean(int id) {
		return (BasicDataBean) getSqlMapClientTemplate().queryForObject("query_basic_data_one", id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BasicDataBean> queryBasicDataBeanAll(String basicDataType) {
		return getSqlMapClientTemplate().queryForList("query_basic_data_all" , basicDataType);
	}

	@Override
	public void updateBasicData(BasicDataBean basicData) {
		getSqlMapClientTemplate().update("update_basic_data", basicData);
	}

	@Override
	public void insertBasicDataBean(BasicDataBean basicData) {
		getSqlMapClientTemplate().insert("insert_basic_data", basicData);
	}

	@Override
	public int findBasicDataId(Map<String, Object> paramMap) {
		return (Integer) getSqlMapClientTemplate().queryForObject("find_basic_data_id", paramMap);
	}

	@Override
	public String querySysArg(String code) {
		return (String) getSqlMapClientTemplate().queryForObject("query_sys_arg", code);
	}

	@Override
	public void executeSql(String executeSql) {
		getSqlMapClientTemplate().update("execute_sql", executeSql);
	}

	@Override
	public int insertFileInfo(Map<String, Object> params) {
		return (Integer) getSqlMapClientTemplate().insert("insert_file_info", params);
	}

	@Override
	public FileParam queryFileInfo(int fileId) {
		return (FileParam) getSqlMapClientTemplate().queryForObject("query_flie_info", fileId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, String> queryFileMap(String fileIds) {
		return getSqlMapClientTemplate().queryForMap("query_file_map", fileIds, "id", "fileName");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FileParam> queryFileList(String confirmFileIds) {
		return getSqlMapClientTemplate().queryForList("query_file_list", confirmFileIds);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryBasicDataBeanMap(String dataTypeCode) {
		return getSqlMapClientTemplate().queryForMap("query_basic_data_for_map", dataTypeCode, "basicDataId", "basicDataName");
	}

	@Override
	public String queryBasicDataNameById(String basicDataId) {
		return (String) getSqlMapClientTemplate().queryForObject("query_basicdataname_byId", basicDataId);
	}

	@Override
	public BasicDataBean queryBasicDataBeanByDataId(String basicDataId) {
		return (BasicDataBean) getSqlMapClientTemplate().queryForObject("query_basicdata_bydataId", basicDataId);
	}

	@Override
	public void deleteFile(int fileId) {
		getSqlMapClientTemplate().delete("delete_file", fileId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BasicDataBean> queryBasicDataBeanByAttri(String dataType, String attri1) {
		Map<String, String> paramMap = new HashMap<String , String>();
		paramMap.put("dataType", dataType);
		paramMap.put("attri", attri1);
		return getSqlMapClientTemplate().queryForList("query_basicdata_by_attri", paramMap);
	}

    @Override
    public List<Map<String, Object>> queryBasicDataBeanMapWithSub(String dataTypeCode, String subDataTypeCode, Map extra) {
        Map<String, Object> paramMap = new HashMap<String , Object>();
        if (extra != null && !extra.isEmpty()) {
            paramMap.putAll(extra);
        }
        paramMap.put("dataTypeCode", dataTypeCode);
        paramMap.put("subDataTypeCode", subDataTypeCode);
        return getSqlMapClientTemplate().queryForList("query_basic_data_for_map_with_sub", paramMap);
    }

	
}
