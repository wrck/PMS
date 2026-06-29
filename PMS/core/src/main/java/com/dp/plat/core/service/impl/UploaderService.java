package com.dp.plat.core.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.context.UserContext;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.pojo.UserInfo;
import com.dp.plat.core.service.IFileInfoService;
import com.dp.plat.core.service.IUploaderService;
import com.dp.plat.core.service.IUserInfoService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.util.UploadUtils;
import com.dp.plat.support.PropertiesUtil;

@Service("uploaderService")
public class UploaderService implements IUploaderService {

	@Resource
	private IUserService userService;

	@Resource
	private IUserInfoService userInfoService;

	@Resource
	private IFileInfoService fileInfoService;

	/**
	 * 创建文件
	 *
	 * @param file
	 * @return
	 */
	public File createFile(MultipartFile file) {
		String dirPath = PropertiesUtil.getValue("uploadPath");
		return createFile(file, dirPath);
	}

	public File createFile(MultipartFile file, String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String filePath = dirPath + "/" + (new Date().getTime()) + "_" + file.getOriginalFilename();
		File newFile = new File(filePath);
		try {
			InputStream ins = file.getInputStream();
			OutputStream os = new FileOutputStream(newFile);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			ins.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFile;
	}

	public String saveAvatar(String userId, String fileName, String filePath, String dirPath) {
		UserInfo userInfo = null;
		User user = null;
		if (!StringUtils.isEmpty(userId) && !userId.equals("0")) {
			user = userService.selectByPrimaryKey(Integer.parseInt(userId));
			if (user == null) {
				return "0";
			}
			userInfo = userInfoService.selectByPrimaryKey(Integer.parseInt(userId));
		}
		Integer avatar_id = userInfo == null ? null : userInfo.getId();
		// 图片替换
		if (userInfo != null) {
			File file = new File(dirPath + userInfo.getAvatar());
			if (file.exists()) {
			    file.delete();
			}
			userInfo.setAvatar(filePath);
			userInfoService.updateByPrimaryKeySelective(userInfo);
		}
		// 新增图片
		else {
			user = new User();
			int uid = userService.insert(user);
			userInfo = new UserInfo();
			userInfo.setAvatar(filePath);
			userInfo.setUserId(uid);
			avatar_id = userInfoService.insertSelective(userInfo);
		}
		user.setUpdateTime(new Date());
		userService.updateByPrimaryKeySelective(user);
		return avatar_id.toString();
	}

	@Override
	@Transactional
	public List<FileInfo> baseUploadFile(String typeCode, HttpServletRequest httpRequest) throws Exception {
		FileType fileType = fileInfoService.selectFileTypeByCode(typeCode);
		// 执行文件上传
		List<FileInfo> list = UploadUtils.uploadMultipartFile(httpRequest, fileType);
		
		// 文件上传信息插入数据库
		String dataType = httpRequest.getParameter("dataType");
		String dataId = httpRequest.getParameter("dataId");
		for (FileInfo fileInfo : list) {
		    fileInfo.setDataType(StringUtils.defaultIfBlank(dataType, fileType.getCode()));
		    fileInfo.setDataId(NumberUtils.isNumber(dataId) ? NumberUtils.createInteger(dataId) : 0);
			fileInfoService.insertSelective(fileInfo);
		}
		return list;
	}
	
	@Override
	@Transactional
	public List<FileInfo> baseUploadFile(String typeCode, List<MultipartFile> multipartFiles, HttpServletRequest httpRequest) throws Exception {
		FileType fileType = fileInfoService.selectFileTypeByCode(typeCode);
		// 执行文件上传
		List<FileInfo> list = UploadUtils.uploadMultipartFile(fileType, multipartFiles, httpRequest);
		// 文件上传信息插入数据库
		String dataType = httpRequest.getParameter("dataType");
        String dataId = httpRequest.getParameter("dataId");
		for (FileInfo fileInfo : list) {
		    fileInfo.setDataType(StringUtils.defaultIfBlank(dataType, fileType.getCode()));
            fileInfo.setDataId(NumberUtils.isNumber(dataId) ? NumberUtils.createInteger(dataId) : 0);
            fileInfoService.insertSelective(fileInfo);
		}
		return list;
	}

	@Override
	@Transactional
	public FileInfo baseUploadFile(String typeCode, MultipartFile multipartFile, HttpServletRequest httpRequest) throws Exception {
		FileType fileType = fileInfoService.selectFileTypeByCode(typeCode);
		// 执行文件上传
		FileInfo fileInfo = UploadUtils.uploadMultipartFile(fileType, multipartFile,httpRequest);
		// 文件上传信息插入数据库
		String dataType = httpRequest.getParameter("dataType");
        String dataId = httpRequest.getParameter("dataId");

        fileInfo.setDataType(StringUtils.defaultIfBlank(dataType, fileType.getCode()));
        fileInfo.setDataId(NumberUtils.isNumber(dataId) ? NumberUtils.createInteger(dataId) : 0);
		fileInfoService.insertSelective(fileInfo);
		return fileInfo;
	}

	@Override
	public List<FileInfo> selectFileInfoByIdsAndType(Collection<String> ids, Integer typeId) {
		return fileInfoService.selectFileInfoByIdsAndType(ids, typeId);
	}

	@Override
	public void fileDownload(Integer fileId, HttpServletRequest request, HttpServletResponse response) {
		FileInfo fileInfo = fileInfoService.selectFileInfoById(fileId);
		// 项目路径
		String webPath = request.getSession().getServletContext().getRealPath("/");
		DownloadUtils.downFile(response, request, webPath + fileInfo.getPath(), fileInfo.getName());
		// 记录下载日志，方便统计文件下载次数
		fileInfoService.insertdownlog(String.valueOf(fileId), HttpContext.getCurrentIp(request), UserContext.getUsername());
	}

	@Override
	public void zipFileDownload(String fileIds, String zipName, HttpServletRequest request,
			HttpServletResponse response) {
		List<String> ids = Arrays.asList(fileIds.split(","));
		List<FileInfo> fileInfos = fileInfoService.selectFileInfoByIds(ids);
		if (StringUtils.isEmpty(zipName)) {
			zipName = FileUtil.generZipFileName();
		}
		//DownloadUtils.downZip("upload/temp", zipName, fileInfos, request, response);
		DownloadUtils.downZip(UploadUtils.UPLOAD_PATH + "/temp", zipName, fileInfos, request, response);
		// 记录下载日志，方便统计文件下载次数
		fileInfoService.insertdownlog(fileIds, HttpContext.getCurrentIp(request), UserContext.getUsername());
	}

	@Override
	public void fileDownload(FileInfo fileInfo, HttpServletRequest request, HttpServletResponse response) {
		// 项目路径
		String webPath = request.getSession().getServletContext().getRealPath("/");
		DownloadUtils.downFile(response, request, webPath + fileInfo.getPath(), fileInfo.getName());
		// 记录下载日志，方便统计文件下载次数
		Integer fileId = fileInfo.getId();
		if (fileId != null && fileId != 0) {
			fileInfoService.insertdownlog(String.valueOf(fileId), HttpContext.getCurrentIp(request), UserContext.getUsername());
		}
	}

	@Override
	public void zipFileDownload(String zipName, List<FileInfo> fileInfos, HttpServletRequest request,
			HttpServletResponse response) {
		if (StringUtils.isEmpty(zipName)) {
			zipName = FileUtil.generZipFileName();
		}
//		DownloadUtils.downZip("upload/temp", zipName, fileInfos, request, response);
		DownloadUtils.downZip(UploadUtils.UPLOAD_PATH + "/temp", zipName, fileInfos, request, response);
		// 记录下载日志，方便统计文件下载次数
		List<Integer> fileIds = new ArrayList<Integer>(fileInfos.size());
		for (FileInfo fileInfo : fileInfos) {
			Integer id = fileInfo.getId();
			if (id != null && id > 0) {
				fileIds.add(id);
			}
		}
		if (!fileIds.isEmpty()) {
			fileInfoService.insertdownlog(StringUtils.join(fileIds, ","), HttpContext.getCurrentIp(request), UserContext.getUsername());
		}
	}
}
