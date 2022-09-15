package com.dp.plat.plus.certificate.action;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.action.BaseAction;
import com.dp.plat.context.UserContext;
import com.dp.plat.plus.certificate.service.CertificateService;

/**
 * 合格证查询
 * 
 * @author w02611
 *
 */
@SuppressWarnings("serial")
public class CertificateAction extends BaseAction {
	private CertificateService certificateService;
	private String barcode;
	private HashMap<String, String> results;
	private File file;

	/**
	 * 查询合格证主页
	 * 
	 * @return
	 */
	public String certificate() {
		results = new HashMap<>();
		results.put("canUpload", String.valueOf(UserContext.getUserContext().isHasRole(1)));
		return SUCCESS;
	}
	
	/**
	 * 查询合格证
	 * 
	 * @return
	 */
	public String queryCertificate() {
		if (StringUtils.isNotBlank(barcode)) {
			String oqcNo = null;
			barcode = StringUtils.trimToEmpty(barcode);
			List<Map<String, String>> oqcInfoList = certificateService.queryOQCInfo(barcode);
			if (!oqcInfoList.isEmpty()) {
				Map<String, String> oqcInfo = oqcInfoList.get(0);
				String info = oqcInfo.get("info");
				if (StringUtils.isNotBlank(info)) {
					Pattern pattern = Pattern.compile("\\d+");
					Matcher matcher = pattern.matcher(info);
					matcher.find();
					oqcNo = matcher.group();
				}
				if (StringUtils.isNotBlank(oqcNo)) {
					String productionDate = generateProductionDate(barcode);
					results = new HashMap<>();
					results.put("oqcNo", oqcNo);
					results.put("productionDate", productionDate);
					return SUCCESS;
				}
			}
			setErrmsg("没有找到[" + barcode + "]对应的OQC检验信息！");
		} else {
			setErrmsg("请输入设备序列号！");
		}
		return SUCCESS;
	}

	/**
	 * 根据序列号，获取生产日期
	 * 
	 * @param barCode
	 * @return productionDate
	 */
	public static String generateProductionDate(String barCode) {
		if (StringUtils.isNotBlank(barCode) && barCode.length() > 13) {
			String year = barCode.substring(9, 11);
			String month = barCode.substring(11, 12);
			StringBuilder dateStr = new StringBuilder(year);
			dateStr.append("-").append(Integer.valueOf(month, 16));
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM");
				Date date = dateFormat.parse(dateStr.toString());
				dateFormat.applyPattern("yyyy-MM");
				return dateFormat.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String uploadSealInfo() {
		try {
			certificateService.parseExcelFile(file);
		} catch (Exception e) {
			setErrmsg(e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}

	public CertificateService getCertificateService() {
		return certificateService;
	}

	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public HashMap<String, String> getResults() {
		return results;
	}

	public void setResults(HashMap<String, String> results) {
		this.results = results;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
