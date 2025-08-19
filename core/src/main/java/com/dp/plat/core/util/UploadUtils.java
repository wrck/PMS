package com.dp.plat.core.util;

import com.dp.plat.core.config.SystemConfig;
import com.dp.plat.core.exception.UploadException;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.FileType;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class UploadUtils {
	
	private final static String DEFAULT_UPLOAD_PATH = "upload";


	public final static String UPLOAD_PATH;

	static {
		String uploadPath = DEFAULT_UPLOAD_PATH;
		String uploadServerName = "";
		try {
			// 上传文件的基础路径
			uploadPath = PropertyUtil.getProperty("sys.upload.base.path");
			// 多机部署时是否需要增加服务名
			uploadServerName = PropertyUtil.getProperty("sys.upload.server.name");
			if (uploadPath == null || uploadPath.trim().length() == 0) {
				uploadPath = DEFAULT_UPLOAD_PATH;
			} else {
				uploadPath = uploadPath.trim();
			}
			if (uploadServerName != null && uploadServerName.trim().length() > 0) {
				uploadServerName = uploadServerName.trim();
				uploadPath = uploadPath + File.separator + uploadServerName;
			}
			uploadPath = uploadPath.replace("\\", "/").replace("//", "/").replace("/", File.separator);
		} catch (Throwable e) {
			uploadPath = DEFAULT_UPLOAD_PATH;
		}
		UPLOAD_PATH = uploadPath;
	}
	
	/**
	 * 文件上传处理
	 *
	 * @param fileType
	 * @param mFile
	 * @param httpRequest
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
		fullPath = fullPath.replace("//", File.separator);//处理操作系统的差异
		String shortPath = saveDir + fileName;
		shortPath = shortPath.replace("//", File.separator);//处理操作系统的差异
		//判断上传目录是否存在
		mkdir(webDir,  saveDir.replace("//", File.separator));
		
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
			return transferUploadPathWithServerName(dir);
		}
		String baseDir = SystemConfig.systemVariables.getOrDefault("base.dir", UploadUtils.UPLOAD_PATH);
		baseDir = transferUploadPathWithServerName(baseDir);
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
	 * @param fileType
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
			return uploadMultipartFile(fileType, fileSet, httpRequest);
		}else {
			throw new UploadException("contentType类型错误");
		}
	}

	/**
	 *	 多文件上传
	 *
	 * @param fileType
	 * @param multipartFiles
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	public static List<FileInfo> uploadMultipartFile(FileType fileType, List<MultipartFile> multipartFiles, HttpServletRequest httpRequest) throws Exception {
    		//返回结果
			List<FileInfo> results = new ArrayList<>();
		for (MultipartFile temp : multipartFiles) {
				results.add(simpleUpload(fileType, temp, httpRequest));
            }
			return results;
		}

	/**
	 *	 单文件上传
	 *
	 * @param fileType
	 * @param multipartFile
	 * @param httpRequest
	 * @return
	 * @throws Exception
	 */
	public static FileInfo uploadMultipartFile(FileType fileType, MultipartFile multipartFile, HttpServletRequest httpRequest) throws Exception {
		return simpleUpload(fileType, multipartFile, httpRequest);
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
	
	/**
	 * 对默认上传路径进行处理，转换为带服务名的上传路径，便于多机部署时根据不同的服务名生成对应的目录
	 * @param dir
	 * @return
	 */
	public static String transferUploadPathWithServerName(String dir) {
		if (StringUtils.isNotBlank(dir)) {
			dir = StringUtils.trimToEmpty(dir).replace("//", File.separator);
			// 默认上传路径匹配正则，其中正则中\是特定字符，所以需要对路径中的\进行处理，将\转化为\\
			String regex = "(^[/|\\\\]?)(\\b" + DEFAULT_UPLOAD_PATH.replace("\\", "\\\\") + "\\b)([/|\\\\]?.*)";
			// 如果不匹配，则补充完整默认上传路径
			if (!dir.matches(regex)) {
				if (dir.matches("(^[/|\\\\])(.*)")) {
					dir = DEFAULT_UPLOAD_PATH + dir;
				} else {
					dir = DEFAULT_UPLOAD_PATH + File.separator + dir;
				}
			}
			// 如果已经匹配带服务名的上传路径，则不进行处理，其中正则中\是特定字符，所以需要对路径中的\进行处理，将\转化为\\
			String regex2 = "(^[/|\\\\]?)(\\b" + UPLOAD_PATH.replace("\\", "\\\\") + "\\b)([/|\\\\]?.*)";
			if (!dir.matches(regex2)) {
				dir = dir.replaceAll(regex, "$1{uploadPath}$3").replace("{uploadPath}", UPLOAD_PATH);
			}
		}
		return dir;
	}

	public static void main(String[] args) {
		String dir = "upload/project/";
		System.out.println(transferUploadPathWithServerName(dir));
		dir = "/upload/project/";
		System.out.println(transferUploadPathWithServerName(dir));
		dir = "\\upload/project/";
		System.out.println(transferUploadPathWithServerName(dir));
		dir = "//upload/project/";
		System.out.println(transferUploadPathWithServerName(dir));
		dir = "/uploads/project/";
		System.out.println(transferUploadPathWithServerName(dir));
		dir = "uploads/project/";
		System.out.println(transferUploadPathWithServerName(dir));
	}
}
