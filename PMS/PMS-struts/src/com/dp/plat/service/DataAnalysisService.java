package com.dp.plat.service;

import java.util.List;

import com.dp.plat.data.bean.PmClCBData;
import com.dp.plat.param.DataQueryParam;

public interface DataAnalysisService extends BaseService{
	
	/**
	 *回访数据统计
	 * @param dataQueryParam
	 * @return
	 */
	List<PmClCBData> quesyCbDataList(DataQueryParam dataQueryParam);

}
