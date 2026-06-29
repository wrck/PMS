package com.dp.plat.core.dao;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;

public interface FileInfoMapper extends AbstractBaseMapper<FileInfo> {

	FileType selectFileTypeByCode(String typeCode);

	void insertFileInfo(@Param("fileInfo") FileInfo fileInfo,@Param("createBy")String userName);

	FileInfo selectFileInfoById(@Param("fileId")Integer fileId);

	List<FileInfo> selectFileInfoByIds(@Param("ids") Collection<String> ids);
	
	List<FileInfo> selectFileInfoByIdsAndType(@Param("ids") Collection<String> ids, @Param("typeId") Integer typeId);
	
	void insertdownlog(@Param("fileIds")String fileIds,@Param("ip") String remoteAddr);

	void insertdownlog(@Param("fileIds")String fileIds,@Param("ip") String remoteAddr, @Param("user") String user);
}
