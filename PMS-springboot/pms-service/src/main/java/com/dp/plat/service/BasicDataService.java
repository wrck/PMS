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

    /** 刷新缓存 */
    boolean refreshCacheData();
}
