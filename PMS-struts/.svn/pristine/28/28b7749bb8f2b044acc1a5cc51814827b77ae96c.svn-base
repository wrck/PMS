package com.dp.plat.plus.certificate.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CertificateDao {

	/**
	 * 根据序列号查询OQC检验员及检验时间
	 * 
	 * @param barcode
	 * @return
	 */
	List<Map<String, String>> queryOQCInfo(String barcode);

	/**
	 * 批量插入印章登记记录
	 * 
	 * @param sealInfoList
	 * 
	 * @deprecated
	 * 
	 * 			list&lt;map&gt; 无法批量插入，故暂时弃用
	 */
	@Deprecated
	void insertSealInfo(List<HashMap<String, Object>> sealInfoList);

	/**
	 * 插入印章登记记录
	 * 
	 * @param sealInfo
	 */
	void insertSealInfo(HashMap<String, Object> sealInfo);

	/**
	 * 清空印章登记表
	 */
	void deleteSealInfo();

}
