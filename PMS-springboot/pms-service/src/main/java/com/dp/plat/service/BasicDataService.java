package com.dp.plat.service;

import com.dp.plat.model.entity.SysBasicData;
import com.dp.plat.model.entity.SysFileInfo;

import java.util.List;
import java.util.Map;

public interface BasicDataService {

    /** 根据类型查询基础数据(仅启用的) */
    List<SysBasicData> queryByType(String dataType);

    /** 查询所有基础数据(包括禁用的) */
    List<SysBasicData> queryAllByType(String dataType);

    /** 添加基础数据 */
    void addBasicData(SysBasicData data);

    /** 更新基础数据 */
    void updateBasicData(SysBasicData data);

    /** 删除基础数据 */
    void deleteBasicData(Long id);

    /** 查询系统参数 */
    String querySysArg(String code);

    /** 根据ID查询基础数据名称 */
    String queryBasicDataNameById(String basicDataId);

    /** 根据数据编码查询基础数据 */
    SysBasicData queryByDataCode(String dataType, String dataCode);

    /** 插入文件信息 */
    Long insertFileInfo(String fileName, String filePath, String module);

    /** 查询文件信息 */
    SysFileInfo queryFileInfo(Long fileId);

    /** 删除文件 */
    void deleteFile(Long fileId);

    /** 查询基础数据Map(dataCode -> dataName) */
    Map<String, String> queryBasicDataMap(String dataType);

    /** 查询所有基础数据类型 */
    List<SysBasicData> queryBasicDataType();

    /** 根据ID查询单条基础数据 */
    SysBasicData queryBasicDataBean(Long id);

    /** 根据条件Map查找基础数据ID */
    int findBasicDataId(Map<String, Object> paramMap);

    /** 执行原生SQL */
    void executeSql(String sql);

    /** 批量插入文件信息（含文件类型），返回逗号分隔的ID */
    String batchInsertFileInfo(String path, String uploadFileName, String uploadFileType);

    /** 根据逗号分隔的文件ID查询文件Map(id->fileName) */
    Map<Long, String> queryFileMap(String fileIds);

    /** 根据文件ID列表查询文件列表 */
    List<SysFileInfo> queryFileList(String fileIds);

    /** 根据dataId查询基础数据Bean */
    SysBasicData queryBasicDataBeanByDataId(String basicDataId);

    /** 根据类型和属性查询基础数据 */
    List<SysBasicData> queryBasicDataBeanByAttri(String dataType, String attri1);

    /** 查询带子类型的基础数据Map */
    List<Map<String, Object>> queryBasicDataBeanMapWithSub(String dataTypeCode, String subDataTypeCode, Map<String, Object> extra);

    /** 刷新缓存 */
    boolean refreshCacheData();
}
