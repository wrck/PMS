package com.dp.plat.service;

import java.util.List;

import com.dp.plat.dao.DataAnalysisDao;
import com.dp.plat.data.bean.PmClCBData;
import com.dp.plat.param.DataQueryParam;

public class DataAnalysisServiceImpl extends BaseServiceImpl implements DataAnalysisService{
	private DataAnalysisDao dataAnalysisDao;

	public DataAnalysisDao getDataAnalysisDao() {
		return dataAnalysisDao;
	}

	public void setDataAnalysisDao(DataAnalysisDao dataAnalysisDao) {
		this.dataAnalysisDao = dataAnalysisDao;
	}

	@Override
	public List<PmClCBData> quesyCbDataList(DataQueryParam dataQueryParam) {
		return dataAnalysisDao.quesyCbDataList(dataQueryParam);
	}
	
}
