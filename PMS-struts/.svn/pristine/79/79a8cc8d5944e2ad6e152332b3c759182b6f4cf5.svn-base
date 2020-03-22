package com.dp.plat.service;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.param.FileParam;

/**
 * 基础数据service
 * @author admin
 *
 */
public interface BasicDataService{
	
	/**
	 * 根据基础数据类型编码查询基础数据
	 */
	List<BasicDataBean> queryBasicDataBeans(String basicDataType);
	/**
	 * 查询基础数据类型
	 * @return
	 */
	List<BasicDataBean> queryBasicDataType();
	/**
	 * 查询单一数据类型
	 * @param id
	 * @return
	 */
	BasicDataBean queryBasicDataBean(int id);
	/**
	 * 查询全部基础数据
	 * @param basicDataType
	 * @return
	 */
	List<BasicDataBean> queryBasicDataBeanAll(String basicDataType);
	/**
	 * 更新基础数据
	 * @param basicData
	 */
	void updateBasicData(BasicDataBean basicData);
	/**
	 * 插入基础数据
	 * @param basicData
	 */
	void insertBasicDataBean(BasicDataBean basicData);
	/**
	 * 判断用户编码是否存在
	 * @param paramMap
	 * @return
	 */
	int findBasicDataId(Map<String, Object> paramMap);
	/**
	 * 查询系统级参数
	 */
	String querySysArg(String code);
	/**
	 * 执行SQL
	 * @param executeSql
	 */
	void executeSql(String executeSql);
	/**
	 * 保存上传文件信息
	 * @param uPLOAD_PATH
	 * @param uploadFileName
	 * @return
	 */
	String insertFileInfo(String uPLOAD_PATH, String uploadFileName);
    /**
     * 保存上传文件信息
     * @param uPLOAD_PATH
     * @param uploadFileName
     * @param uploadFileType
     * @return
     */
    String insertFileInfo(String uPLOAD_PATH, String uploadFileName, String uploadFileType);
	/**
	 * 查询上传文件信息
	 * @param fileId
	 * @return
	 */
	FileParam queryFileInfo(int fileId);
	/**
	 * 查询上传文件信息
	 * @param fileIds
	 * @return
	 */
	Map<Integer, String> queryFileMap(String fileIds);
	/**
	 * 查询文件信息集合
	 * @param confirmFileIds
	 * @return
	 */
	List<FileParam> queryFileList(String confirmFileIds);
	/**
	 * 查询基础数据返回Map
	 * @param bASIC_DATA_TARGET_CODE
	 * @return
	 */
	Map<String, String> queryBasicDataBeanMap(String bASIC_DATA_TARGET_CODE);
	/**
	 * 根据BasicDataId 查询BasicDataName
	 * @param basicDataId
	 * @return
	 */
	String queryBasicDataNameById(String basicDataId);
	/**
	 * 查询BasicDataBean By basicDataId
	 * @param basicDataId
	 * @return
	 */
	BasicDataBean queryBasicDataBeanByDataId(String basicDataId);
	/**
	 * 删除上传文件信息
	 * @param fileId
	 */
	void deleteFile(int fileId);
	/**
	 * 根据属性值查询分类数据
	 * @param dataType
	 * @param attri1
	 * @return
	 */
	List<BasicDataBean> queryBasicDataBeanByAttri(String dataType, String attri1);
    /**
     * @param dataTypeCode
     * @param subDataTypeCode
     * @param extra
     * @return
     */
    List<Map<String, Object>> queryBasicDataBeanMapWithSub(String dataTypeCode, String subDataTypeCode, Map extra);
}
