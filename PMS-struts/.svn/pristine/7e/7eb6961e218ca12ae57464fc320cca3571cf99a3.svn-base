package com.dp.plat.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dp.plat.dao.BasicDataDao;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.param.FileParam;

public class BasicDataServiceImpl extends BaseServiceImpl implements BasicDataService{
	private BasicDataDao basicDataDao;

	public BasicDataDao getBasicDataDao() {
		return basicDataDao;
	}

	public void setBasicDataDao(BasicDataDao basicDataDao) {
		this.basicDataDao = basicDataDao;
	}

	@Override
	public List<BasicDataBean> queryBasicDataBeans(String basicDataType) {
		return basicDataDao.queryBasicDataBeans(basicDataType);
	}

	@Override
	public List<BasicDataBean> queryBasicDataType() {
		return basicDataDao.queryBasicDataType();
	}

	@Override
	public BasicDataBean queryBasicDataBean(int id) {
		return basicDataDao.queryBasicDataBean(id);
	}

	@Override
	public List<BasicDataBean> queryBasicDataBeanAll(String basicDataType) {
		return basicDataDao.queryBasicDataBeanAll(basicDataType);
	}

	@Override
	public void updateBasicData(BasicDataBean basicData) {
		basicDataDao.updateBasicData(basicData);
	}

	@Override
	public void insertBasicDataBean(BasicDataBean basicData) {
		basicData.setCreateBy(getLoginName());
		basicData.setCreateTime(new Date());
		basicDataDao.insertBasicDataBean(basicData);
	}

	@Override
	public int findBasicDataId(Map<String, Object> paramMap) {
		return basicDataDao.findBasicDataId(paramMap);
	}

	@Override
	public String querySysArg(String code) {
		return basicDataDao.querySysArg(code);
	}

	@Override
	public void executeSql(String executeSql) {
		basicDataDao.executeSql(executeSql);
	}

	@Override
	public String insertFileInfo(String path, String uploadFileName) {
		StringBuilder fileIds = new StringBuilder();
		String[] fileNames = uploadFileName.split(",");
		Map<String, Object> params = new HashMap<String, Object>();
		for(String fileName : fileNames){
			params.put("fileName", fileName.trim());
			params.put("filePath", path+fileName.trim());
			params.put("uploadBy", getLoginName());
			params.put("uploadTime", new Date());
			int id = basicDataDao.insertFileInfo(params);
			fileIds.append(id);
			fileIds.append(",");
		}
		if(fileIds.length() > 0){
			fileIds.deleteCharAt(fileIds.length()-1);
		}
		return fileIds.toString();
	}
	
	@Override
    public String insertFileInfo(String path, String uploadFileName, String uploadFileType) {
        StringBuilder fileIds = new StringBuilder();
        String[] fileNames = uploadFileName.split(",");
        String[] fileTypes = StringUtils.trimToEmpty(uploadFileType).split(",");
        Map<String, Object> params = new HashMap<String, Object>();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = StringUtils.trimToEmpty(fileNames[i]);
            params.put("fileName", fileName);
            params.put("filePath", path+fileName);
            if (fileTypes.length == fileNames.length) {
                params.put("fileType", StringUtils.trimToEmpty(fileTypes[i]));
            }
            params.put("uploadBy", getLoginName());
            params.put("uploadTime", new Date());
            int id = basicDataDao.insertFileInfo(params);
            fileIds.append(id);
            fileIds.append(",");
        }
        if(fileIds.length() > 0){
            fileIds.deleteCharAt(fileIds.length()-1);
        }
        return fileIds.toString();
    }


	@Override
	public FileParam queryFileInfo(int fileId) {
		return basicDataDao.queryFileInfo(fileId);
	}

	@Override
	public Map<Integer, String> queryFileMap(String fileIds) {
		if(fileIds != null){
			return basicDataDao.queryFileMap(fileIds);
		}
		return null;
	}

	@Override
	public List<FileParam> queryFileList(String confirmFileIds) {
		return basicDataDao.queryFileList(confirmFileIds);
	}

	@Override
	public Map<String, String> queryBasicDataBeanMap(
			String dataTypeCode) {
		return basicDataDao.queryBasicDataBeanMap(dataTypeCode);
	}

	@Override
	public String queryBasicDataNameById(String basicDataId) {
		return basicDataDao.queryBasicDataNameById(basicDataId);
	}

	@Override
	public BasicDataBean queryBasicDataBeanByDataId(String basicDataId) {
		return basicDataDao.queryBasicDataBeanByDataId(basicDataId);
	}

	@Override
	public void deleteFile(int fileId) {
		basicDataDao.deleteFile(fileId);
	}

	@Override
	public List<BasicDataBean> queryBasicDataBeanByAttri(String dataType, String attri1) {
		return basicDataDao.queryBasicDataBeanByAttri(dataType , attri1);
	}
	
	@Override
    public List<Map<String, Object>> queryBasicDataBeanMapWithSub(
            String dataTypeCode, String subDataTypeCode, Map extra) {
        return basicDataDao.queryBasicDataBeanMapWithSub(dataTypeCode, subDataTypeCode, extra);
    }
}
