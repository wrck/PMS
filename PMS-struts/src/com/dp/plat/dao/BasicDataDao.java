package com.dp.plat.dao;

import java.util.List;
import java.util.Map;

import com.dp.plat.data.bean.Arg;
import com.dp.plat.data.bean.BasicDataBean;
import com.dp.plat.param.FileParam;

/**
 * 基础数据操作DAO
 * @author admin
 *
 */
public interface BasicDataDao {
	
	List<BasicDataBean> queryBasicDataBeans(String basicDataTypeCode);

	List<BasicDataBean> queryBasicDataType();

	BasicDataBean queryBasicDataBean(int id);

	List<BasicDataBean> queryBasicDataBeanAll(String basicDataType);

	void updateBasicData(BasicDataBean basicData);

	void insertBasicDataBean(BasicDataBean basicData);

	int findBasicDataId(Map<String, Object> paramMap);

    List<Arg> querySysArgList(Arg arg);
    
	String querySysArg(String code);

	void executeSql(String executeSql);

	int insertFileInfo(Map<String, Object> params);

	FileParam queryFileInfo(int fileId);

	Map<Integer, String> queryFileMap(String fileIds);

	List<FileParam> queryFileList(String confirmFileIds);

	Map<String, String> queryBasicDataBeanMap(String dataTypeCode);

	String queryBasicDataNameById(String basicDataId);

	BasicDataBean queryBasicDataBeanByDataId(String basicDataId);

	void deleteFile(int fileId);

	List<BasicDataBean> queryBasicDataBeanByAttri(String dataType, String attri1);

    /**
     * @param dataTypeCode
     * @param subDataTypeCode
     * @param extra 
     * @return
     */
    List<Map<String, Object>> queryBasicDataBeanMapWithSub(String dataTypeCode, String subDataTypeCode, Map extra);

    /**
     * 刷新缓存
     * @return
     */
    boolean refreshCacheData();

}
