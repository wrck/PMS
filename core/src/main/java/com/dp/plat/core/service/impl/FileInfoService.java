package com.dp.plat.core.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dp.plat.core.dao.FileInfoMapper;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;
import com.dp.plat.core.service.IFileInfoService;

/**
 *
 * @author w02611
 */
@Service("fileInfoService")
public class FileInfoService extends AbstractBaseService<FileInfoMapper, FileInfo> implements IFileInfoService {

    @Override
    public FileType selectFileTypeByCode(String typeCode) {
        return dao.selectFileTypeByCode(typeCode);
    }

    @Override
    public void insertFileInfo(FileInfo fileInfo, String userName) {
        this.insertSelective(fileInfo);
    }

    @Override
    public FileInfo selectFileInfoById(Integer fileId) {
        return dao.selectFileInfoById(fileId);
    }

    @Override
    public List<FileInfo> selectFileInfoByIds(Collection<String> ids) {
        return dao.selectFileInfoByIds(ids);
    }

    @Override
    public List<FileInfo> selectFileInfoByIdsAndType(Collection<String> ids, Integer typeId) {
        return dao.selectFileInfoByIdsAndType(ids, typeId);
    }

    @Override
    public void insertdownlog(String fileIds, String remoteAddr) {
        dao.insertdownlog(fileIds, remoteAddr);
    }

    @Override
    public void insertdownlog(String fileIds, String remoteAddr, String user) {
        dao.insertdownlog(fileIds, remoteAddr, user);
    }
}