package com.dp.plat.plus.certificate.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dp.plat.dao.BaseDao;

public class CertificateDaoImpl extends BaseDao implements CertificateDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> queryOQCInfo(String barcode) {
		return getSqlMapClientTemplate().queryForList("queryOQCInfo", barcode);
	}

	@Override
	public void insertSealInfo(List<HashMap<String, Object>> sealInfoList) {
		getSqlMapClientTemplate().insert("insertSealInfo", sealInfoList);
	}

	@Override
	public void insertSealInfo(HashMap<String, Object> sealInfo) {
		getSqlMapClientTemplate().insert("insertSealInfo", sealInfo);
	}

	@Override
	public void deleteSealInfo() {
		getSqlMapClientTemplate().delete("truncateSealInfo");
	}
}
