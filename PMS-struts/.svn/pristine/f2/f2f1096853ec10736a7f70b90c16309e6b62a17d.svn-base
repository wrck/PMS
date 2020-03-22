package com.dp.plat.plus.certificate.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.dp.plat.service.BaseService;


public interface CertificateService extends BaseService{

	/**
	 * 根据序列号查询OQC检验员及检验时间
	 * @param barcode
	 * @return 
	 */
	List<Map<String, String>> queryOQCInfo(String barcode);

	/**
	 * @param file
	 */
	void parseExcelFile(File file);
}
