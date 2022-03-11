package com.dp.plat.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.exception.UploadException;


/**
 * @author w02611
 *
 */
public class FileUtil {
	/**
	 * 上传类型白名单
	 */
	private final static String UPLOAD_EXT_WHITE_LIST = "doc|docx|xls|xlsx|ppt|pptx|xps|vsd|vsdx|csv|pdf|rar|zip|7z|txt|log|out|bmp|gif|jpg|jpeg|png";

	/**
	 * 删除文件夹里面的所有文件
	 *
	 * @param path
	 *            文件夹路径 如 c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
	}

	/**
	 * 删除文件夹
	 *
	 * @param folderPath
	 *            文件夹路径及名称 如c:/fqf
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * 复制单个文件
	 *
	 * @param oldPath
	 *            源文件路径
	 * @param newPath
	 *            复制后路径
	 * @return 文件大小
	 */
	public static int copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
			return bytesum;
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 复制文件流到新的文件
	 *
	 * @param inStream
	 *            文件流
	 * @param file
	 *            新文件
	 * @return 是否复制成功
	 */
	@SuppressWarnings("unused")
	public static boolean copyInputStreamToFile(final InputStream inStream, File file) throws IOException {
		int bytesum = 0;
		int byteread = 0;
		byte[] buffer = new byte[1024];
		FileOutputStream fs = new FileOutputStream(file);
		while ((byteread = inStream.read(buffer)) != -1) {
			bytesum += byteread; // 字节数 文件大小
			fs.write(buffer, 0, byteread);
		}
		inStream.close();
		fs.close();
		return true;
	}

	/**
	 * 删除指定路径下的文件
	 *
	 * @param filePathAndName
	 *            文件路径
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}

	}

	// 计算文件的 MD5 值
	public static String getFileMD5(MultipartFile mFile) {
		MessageDigest digest = null;
		InputStream in = null;
		byte buffer[] = new byte[8192];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = mFile.getInputStream();
			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(32);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	public static String getFileNameByMD5(MultipartFile mFile) {
		MessageDigest digest = null;
		InputStream in = null;
		byte buffer[] = new byte[8192];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = mFile.getInputStream();
			while ((len = in.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			BigInteger bigInt = new BigInteger(1, digest.digest());
			//文件后缀
			String fileName = mFile.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf("."));
			
			return bigInt.toString(32) + suffix;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	/**
	 * 检查文件后缀名称是否符合要求
	 * @param mFile
	 * @param property
	 * @throws Exception 
	 */
	public static void checkFileSuffix(MultipartFile mFile, String fileType) throws Exception {
		if(StringUtils.isNotEmpty(fileType)) {
			//获取文件后缀
			String fileName = mFile.getOriginalFilename();
			String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
			//允许的文件类型
			String[] fileTypes = fileType.split("\\|");
			List<String> list = Arrays.asList(fileTypes);
			if(!list.contains(suffix.toLowerCase())) {
				throw new UploadException("不允许上传类型为【."+suffix+"】的文件！");
			}
		}
	}
	
	/**
     * 检查上传的文件类型
     * @param files
     * @return 
     * @throws UploadException
     */
    public static boolean checkFileExt(File file) throws Exception {
    	return checkFileExt(file, null);
    }
    
    /**
     * 检查上传的文件类型
     * @param files
     * @return 
     * @throws UploadException
     */
    public static boolean checkFileExt(File file, String allowFileTypes) throws Exception {
    	allowFileTypes = StringUtils.defaultIfBlank(allowFileTypes, UPLOAD_EXT_WHITE_LIST);
    	return checkFileExt(file.getName(), allowFileTypes);
    }
    
    /**
     * 检查上传的文件类型
     * @param files
     * @return 
     * @throws UploadException
     */
    public static boolean checkFileExt(File[] files) {
    	return checkFileExt(files, null);
    }
    
    /**
     * 检查上传的文件类型
     * @param files
     * @return 
     * @throws UploadException
     */
    public static boolean checkFileExt(File[] files, String allowFileTypes) {
    	boolean result = true;
    	allowFileTypes = StringUtils.defaultIfBlank(allowFileTypes, UPLOAD_EXT_WHITE_LIST);
    	for (int i = 0; i < files.length; i++) {
			File file = files[i];
			result = result && checkFileExt(file.getName(), allowFileTypes);
		}
    	return result;
    }
    
    public static boolean checkFileExt(String fileName) {
		return checkFileExt(fileName, null);
	}
    
    /**
	 * 检查文件后缀名称是否符合要求
	 * 
	 * @param fileName
	 * @param allowFileType
     * @return 
	 * @throws UploadException
	 */
	public static boolean checkFileExt(String fileName, String allowFileType) {
		allowFileType = StringUtils.defaultIfBlank(allowFileType, UPLOAD_EXT_WHITE_LIST);
		if (StringUtils.isNotEmpty(allowFileType)) {
			// 获取文件后缀
			String suffix = extName(fileName);
			// 允许的文件类型
			String[] fileTypes = allowFileType.split("\\|");
			List<String> list = Arrays.asList(fileTypes);
			if (StringUtils.isBlank(suffix)) {
				throw new UploadException("不允许上传类型为空的文件！");
			} else if (!list.contains(suffix.toLowerCase())) {
				String prevExt = extName(fileName.replace("." + suffix, ""));
				boolean showError = true;
				// 判断是否为压缩包的分卷文件
				if (StringUtils.isNotBlank(prevExt) && list.contains(prevExt.toLowerCase())) {
					Pattern pattern = Pattern.compile("[A-Za-z]{1}[0-9]{2}|[0-9]{3}");
					// 如果符合压缩包并且分卷后缀符合要求，则
					showError = !pattern.matcher(suffix).matches();
				}
				if (showError) {
					throw new UploadException("不允许上传类型为【." + suffix + "】的文件！");
				}
			}
		}
		return true;
	}
	
	/**
	 * 获得文件的扩展名（后缀名），扩展名不带“.”
	 *
	 * @param fileName 文件名
	 * @return 扩展名
	 */
	public static String extName(String fileName) {
		if (fileName == null) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return fileName.substring(index + 1);
		}
	}

	/**
	 * 	生成唯一的文件名
	 * @param request
	 * @param suffix
	 * @return
	 */
	public static String generFileName(String suffix) {
		if(suffix.startsWith(".")) {
			return UUIDGenerator.getUUID()+suffix;
		}
		return UUIDGenerator.getUUID()+"."+suffix;
	}
	/**
	 * 	生成zip后缀的唯一文件名称O
	 * @return
	 */
	public static String generZipFileName() {
		return generFileName("zip");
	}
	
	public static String getWebRoot() {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL resource = classLoader.getResource("");
			String path = resource.getPath();
			if (path != null) {
				return getParent(new File(path), 2).getPath() + File.separator;
			}
		} catch (Exception e) {
			String webAppRootKey = System.getProperty("webAppRootKey");
			if (StringUtils.isBlank(webAppRootKey)) {
				webAppRootKey = "webapp.root";
			}
			return System.getProperty(webAppRootKey);
		}
		return null;
	}

	public static String getParent(String filePath, int level) {
		final File parent = getParent(new File(filePath), level);
		try {
			return null == parent ? null : parent.getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static File getParent(File file, int level) {
		if (level < 1 || null == file) {
			return file;
		}

		File parentFile;
		try {
			parentFile = file.getCanonicalFile().getParentFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (1 == level) {
			return parentFile;
		}
		return getParent(parentFile, level - 1);
	}

	/**
	 * 搜索文件
	 * @param path 指定路径下进行搜索
	 * @param keyword 关键词
	 * @param type 类型，all:文件和文件夹, dir:文件夹, file:文件
	 * @param fuzzy 是否模糊查找
	 * @return
	 */
	public static List<File> filterFiles(String path, String keyword, String type, Boolean fuzzy, Boolean searchSub) {
		File dir = new File(path);
		if (!dir.exists()) {
			return Collections.emptyList();
		}
		File[] files = null;
		if (dir.isDirectory()) {
			files = dir.listFiles();
		} else {
			files = new File[] {dir};
		}
		List<File> fileList = new ArrayList<File>(files.length);
		String pathKeyword = File.separator + keyword + File.separator;
		for (File file : files) {
			String filePath = file.getPath();
			String name = file.getName();
			if (((Boolean.TRUE.equals(fuzzy) && name.contains(keyword)) || name.equalsIgnoreCase(keyword) || filePath.contains(pathKeyword))
					&& ("all".equalsIgnoreCase(type) 
						|| ("dir".equalsIgnoreCase(type) && file.isDirectory())
						|| ("file".equalsIgnoreCase(type) && file.isFile()))) {
				fileList.add(file);
				if (Boolean.TRUE.equals(searchSub) && file.isDirectory()) {
					List<File> subDirFiles = filterFiles(file.getPath(), keyword, type, fuzzy, searchSub);
					fileList.addAll(subDirFiles);
				}
			}
		}
		return fileList;
	}
}
