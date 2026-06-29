package com.dp.plat.core.service;

import java.util.Collection;
import java.util.List;

import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;

/**
 *
 * Created by CodeGenerator
 */
public interface IFileInfoService extends IAbstractBaseService<FileInfo> {
    
    FileType selectFileTypeByCode(String typeCode);

    void insertFileInfo(FileInfo fileInfo, String userName);

    FileInfo selectFileInfoById(Integer fileId);

    List<FileInfo> selectFileInfoByIds(Collection<String> ids);
    
    List<FileInfo> selectFileInfoByIdsAndType(Collection<String> ids, Integer typeId);
    
    void insertdownlog(String fileIds, String remoteAddr);

    void insertdownlog(String fileIds, String remoteAddr, String user);
}