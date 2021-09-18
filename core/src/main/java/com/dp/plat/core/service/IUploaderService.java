package com.dp.plat.core.service;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.pojo.FileInfo;

/**
 * 文件上传
 * @author w02611
 *
 */
public interface IUploaderService {

    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    public File createFile(MultipartFile file);


    /**
     * 保存头像信息
     *
     * @param userId          用户ID
     * @param fileName       文件名
     * @param filePath       文件路径(相对路径)
     * @param dirPath        系统路径(绝对路径)
     */
    public String saveAvatar(String userId, String fileName, String filePath, String dirPath);

    /**
     * 创建文件
     *
     * @param file    文件
     * @param dirPath 文件存储路径
     * @return
     */
    public File createFile(MultipartFile file, String dirPath);
    /**
     * 	执行文件上传操作
     * @param fileType
     * @param httpRequest
     * @return
     * @throws Exception 
     */
	public List<FileInfo> baseUploadFile(String fileType, HttpServletRequest httpRequest) throws Exception;
	
	/**
	 * 文件下载
	 * 
	 * @param fileId
	 * @param request
	 * @param response
	 */
	public void fileDownload(Integer fileId, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 文件下载
	 * @param fileInfo
	 * @param request
	 * @param response
	 */
	public void fileDownload(FileInfo fileInfo, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 多文件zip打包下载
	 * @param fileIds
	 * @param zipName 
	 * @param request
	 * @param response
	 */
	public void zipFileDownload(String fileIds, String zipName, HttpServletRequest request, HttpServletResponse response);

	/**
	 * 多文件zip打包下载
	 * @param zipName
	 * @param fileInfos
	 * @param request
	 * @param response
	 */
	public void zipFileDownload(String zipName, List<FileInfo> fileInfos, HttpServletRequest request,
			HttpServletResponse response);


	/**
	 * 查询文件
	 * @param ids
	 * @param typeId
	 * @return
	 */
	public List<FileInfo> selectFileInfoByIdsAndType(List<String> ids, Integer typeId);



}
