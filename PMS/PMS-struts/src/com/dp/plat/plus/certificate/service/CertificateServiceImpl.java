package com.dp.plat.plus.certificate.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.dp.plat.plus.certificate.dao.CertificateDao;
import com.dp.plat.service.BaseServiceImpl;
import com.dp.plat.util.parser.DateCellValueParser;
import com.dp.plat.util.parser.ExcelParser;
import com.dp.plat.util.parser.IntegerCellParser;
import com.dp.plat.util.parser.SheetSize;
import com.dp.plat.util.parser.StringCellParser;

public class CertificateServiceImpl extends BaseServiceImpl implements CertificateService {
	private CertificateDao certificateDao;

	public CertificateDao getCertificateDao() {
		return certificateDao;
	}

	public void setCertificateDao(CertificateDao certificateDao) {
		this.certificateDao = certificateDao;
	}

	@Override
	public List<Map<String, String>> queryOQCInfo(String barcode) {
		return certificateDao.queryOQCInfo(barcode);
	}

	@Override
	public void parseExcelFile(File file) {
		if (file == null) {
			return;
		}
		try {
			FileInputStream input;
			input = new FileInputStream(file);
			ExcelParser parser = new ExcelParser(input);
			List<SheetSize> sheetList = parser.parserExcel();
			// List<HashMap<String, Object>> sealInfoList = new
			// ArrayList<HashMap<String, Object>>();
			certificateDao.deleteSealInfo();
			for (int i = 0; i < sheetList.size(); i++) {
				SheetSize sheetSize = sheetList.get(i);
				// 从第二行开始读取，第一行是标题
				String prevName = "";
				String prevInfo = "";
				String prevDesc = "";
				for (int j = 2; j < sheetSize.getRowNum(); j++) {
					HashMap<String, Object> detail = new HashMap<>();
					DateCellValueParser dateCell = new DateCellValueParser();
					StringCellParser strCell = new StringCellParser();
					IntegerCellParser intCell = new IntegerCellParser();

					parser.parseCell(i, j, (short) 4, strCell);
					String user = strCell.getCellValue();
					strCell.setCellValue("");
					if (StringUtils.isBlank(user)) {
						continue;
					}

					parser.parseCell(i, j, (short) 0, intCell);
					detail.put("id", intCell.getCellValue());
					intCell.setCellValue(null);

					parser.parseCell(i, j, (short) 1, strCell);
					detail.put("name", StringUtils.isBlank(strCell.getCellValue()) ? prevName : strCell.getCellValue());
					prevName = strCell.getCellValue();
					strCell.setCellValue("");

					parser.parseCell(i, j, (short) 2, strCell);
					detail.put("info", StringUtils.isBlank(strCell.getCellValue()) ? prevInfo : strCell.getCellValue());
					prevInfo = strCell.getCellValue();
					strCell.setCellValue("");

					parser.parseCell(i, j, (short) 3, strCell);
					detail.put("description",
							StringUtils.isBlank(strCell.getCellValue()) ? prevDesc : strCell.getCellValue());
					prevDesc = strCell.getCellValue();
					strCell.setCellValue("");

					// parser.parseCell(i, j, (short) 4, strCell);
					detail.put("user", user);
					// strCell.setCellValue("");

					parser.parseCell(i, j, (short) 5, dateCell, true);
					detail.put("takeTime", dateCell.getCellValue());
					dateCell.setCellValue(null);

					parser.parseCell(i, j, (short) 6, dateCell, true);
					detail.put("backTime", dateCell.getCellValue());
					dateCell.setCellValue(null);

					parser.parseCell(i, j, (short) 7, strCell);
					detail.put("remark", strCell.getCellValue());
					strCell.setCellValue("");

					detail.put("uploadBy", getLoginName());

					// sealInfoList.add(detail);
					certificateDao.insertSealInfo(detail);
				}
			}
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("上传印章登记表失败，" + e.getMessage());
		}
	}

}
