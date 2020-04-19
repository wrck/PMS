package com.dp.plat.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.UploadException;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;

public class UploadUtils {
	
	/**
	 * 文件上传处理
	 * @param entry
	 * @param fileAllowType
	 * @param dirPath
	 * @param relPath
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static FileInfo simpleUpload(FileType fileType ,MultipartFile mFile ,HttpServletRequest httpRequest) throws Exception{
		//文件类型判断
		FileUtil.checkFileSuffix(mFile ,fileType.getAllowType());
		//文件名
		String fileName =mFile.getOriginalFilename();
		if(fileType.isRename()) {//是否重命名
			fileName = FileUtil.getFileNameByMD5(mFile);
		}
		String webDir = getWebDir(httpRequest);
		//获取文件保存目录
		String saveDir = getSaveDir(fileType.getDir());
		//构造完整的文件保存路径
		String fullPath = webDir + saveDir + fileName;
		fullPath = fullPath.replaceAll("//", File.separator);//处理操作系统的差异
		String shortPath = saveDir + fileName;
		shortPath = shortPath.replaceAll("//", File.separator);//处理操作系统的差异
		//判断上传目录是否存在
		mkdir(webDir,  saveDir.replaceAll("//", File.separator));
		
		File file = new File(fullPath);
		if (file.isFile() && file.exists()&& fileType.isRename()) {//
			//重命名的情况下，文件已经存在的不用再重复上传
		} else {
			BufferedInputStream inputStream = new BufferedInputStream(mFile.getInputStream());
			BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(fullPath));
			Streams.copy(inputStream, outputStream, true);
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		}
		return new FileInfo(fileType.getId(), mFile.getOriginalFilename(), shortPath, fileName.substring(fileName.lastIndexOf(".")+1), mFile.getSize());
	}
	/**
	 * 获取文件保存目录,如果为空，则根据当前日期创建目录
	 * @param dir
	 * @return
	 */
	public static String getSaveDir(String dir) {
		if(StringUtils.isNotEmpty(dir)) {
			return dir;
		}
		String baseDir = SystemConfig.systemVariables.getOrDefault("base.dir","upload");
		return baseDir+File.separator + DateFormatUtils.format(new Date(), "yyyyMMdd") + File.separator;
	}

	/**
	 * 	获取项目根目录
	 * @param httpRequest
	 * @return
	 */
	public static String getWebDir(HttpServletRequest httpRequest) {
		return httpRequest.getServletContext().getRealPath("/");
	}


	/**
	 *	 多文件上传
	 * @param httpRequest
	 * @param fileAllowType
	 * @return
	 * @throws Exception
	 */
	public static List<FileInfo> uploadMultipartFile(HttpServletRequest httpRequest ,FileType fileType) throws Exception {
		MultipartHttpServletRequest request = (MultipartHttpServletRequest) httpRequest;
		String contentType = request.getContentType();
		if (contentType.indexOf("multipart/form-data") >= 0) {
			//支持多文件上传
			MultiValueMap<String,MultipartFile> multiFileMap = request.getMultiFileMap();
            List<MultipartFile> fileSet = new LinkedList<>();
            for(Entry<String, List<MultipartFile>> temp : multiFileMap.entrySet()){
                fileSet = temp.getValue();
            }
    		//返回结果
			List<FileInfo> results = new ArrayList<>();
            for(MultipartFile temp : fileSet){
				results.add(simpleUpload(fileType, temp, httpRequest));
            }
			return results;
		}else {
			throw new UploadException("contentType类型错误");
		}
	} 
	
	/**
	 * 判断目录是否存在，不存在则创建
	 * @param dirPath
	 * @param dir
	 */
	public static void mkdir(String dirPath ,String dir) {
		File filePath = new File(dirPath + dir);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
	}

	
}
