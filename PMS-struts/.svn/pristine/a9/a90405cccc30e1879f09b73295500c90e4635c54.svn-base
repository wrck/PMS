package com.dp.plat.dao;

import java.util.List;

import com.dp.plat.data.bean.PmClCBData;
import com.dp.plat.param.DataQueryParam;

public class DataAnalysisDaoImpl extends BaseDao implements DataAnalysisDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<PmClCBData> quesyCbDataList(DataQueryParam dataQueryParam) {
		return getSqlMapClientTemplate().queryForList("query-cbData-list",dataQueryParam);
	}
}
