package com.dp.plat.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.dp.plat.exception.UploadException;

/**
 * 文件上传方法
 * 
 * @author admin
 *
 */
public class UploadFileUtil {

	/**
	 * 上传类型白名单
	 */
	private final static String UPLOAD_EXT_WHITE_LIST = "doc|docx|xls|xlsx|ppt|pptx|xps|vsd|vsdx|csv|pdf|rar|zip|7z|txt|log|bmp|gif|jpg|jpeg|png";

	private final static String DEFAULT_UPLOAD_PATH = "upload";

	private final static String UPLOAD_PATH_KEY = "plat.upload.path";

	public final static String UPLOAD_PATH;

	static {
		String uploadPath = DEFAULT_UPLOAD_PATH;
		try {
			uploadPath = StringEscUtil.getText(UPLOAD_PATH_KEY);
			if (uploadPath == null || uploadPath.trim().length() == 0 || UPLOAD_PATH_KEY.equals(uploadPath)) {
				uploadPath = DEFAULT_UPLOAD_PATH;
			} else {
				uploadPath = uploadPath.trim();
			}
			uploadPath = uploadPath.replace("\\", "/").replace("//", "/").replace("/", File.separator);
		} catch (Throwable e) {
			uploadPath = DEFAULT_UPLOAD_PATH;
		}
		UPLOAD_PATH = uploadPath;
	}

	/**
	 * 上传单个文件
	 * 
	 * @param file        前台页面file标签的name值
	 * @param filename    文件名称
	 * @param contentType 文件类型
	 * @param realpath    文件上传目录
	 * @return
	 */
	public boolean uploadFile(File file, String filename, String contentType, String realpath, ServletContext context) {
		try {
			// 检查文件上传类型
			if (!checkFileExt(filename)) {
				return false;
			}
			String targetDirectory = context.getRealPath(realpath);
			String targetFileName = Util.generateFileName(filename);
			File target = new File(targetDirectory, targetFileName);
			FileUtils.copyFile(file, target);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 文件上传
	 * 
	 * @param upload         上传文件[]
	 * @param dir            上传目录
	 * @param uploadFileName 上传文件名称[]
	 * @param source         保存对象
	 * @param property       保存路径属性
	 * @throws IOException
	 */
	public static void upload(File[] upload, String dir, String uploadFileName) throws Exception {
		if (upload != null && !upload.equals("")) {// 为空表示无附件上传
			boolean bool = Util.createDir(dir);// 创建上传路径
			if (!bool) {
				throw new RuntimeException("上传路径不存在");
			}
			String targetDirectory = ServletActionContext.getServletContext().getRealPath(dir);
			String[] uploadFileNames = uploadFileName.split(",");
			String targetFileName = "";
			for (int i = 0; i < uploadFileNames.length; i++) {// 循环上传附件
				targetFileName = uploadFileNames[i].trim();
				// 检查文件上传类型
				if (!checkFileExt(targetFileName)) {
					return;
				}
				File target = new File(targetDirectory, targetFileName);
				FileUtils.copyFile(upload[i], target);
			}
		}
	}

	/**
	 * 文件上传,进行MD5校验 ,文件不重复
	 * 
	 * @param upload         上传文件[]
	 * @param dir            上传目录
	 * @param uploadFileName 上传文件名称字符串
	 * @throws IOException
	 */
	public static String uploadNoRepeat(File[] upload, String dir, String uploadFileName) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		if (upload != null && !upload.equals("")) {// 为空表示无附件上传
			File directory = new File(dir);// 创建上传路径
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String targetDirectory = ServletActionContext.getServletContext().getRealPath(dir);
			String[] uploadFileNames = uploadFileName.split(",");
			for (int i = 0; i < uploadFileNames.length; i++) {
				File file = upload[i];
				String fileName = uploadFileNames[i].trim();
				// 检查文件上传类型
				if (!checkFileExt(fileName)) {
					return stringBuffer.toString();
				}
				int index = fileName.lastIndexOf(".");
				String suffex = fileName.substring(index);
				fileName = getFileMD5(file) + suffex;
				File target = new File(targetDirectory, fileName);
				if (!target.exists()) {
					FileUtils.copyFile(file, target);
				}
				stringBuffer.append(fileName).append(",");
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 文件上传,进行MD5校验 ,文件不重复
	 * 
	 * @param upload         上传文件
	 * @param dir            上传目录
	 * @param uploadFileName 上传文件名称
	 * @throws IOException
	 */
	public static String uploadNoRepeat(File upload, String dir, String uploadFileName) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		if (upload != null && !upload.equals("")) {// 为空表示无附件上传
			File directory = new File(dir);// 创建上传路径
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String targetDirectory = ServletActionContext.getServletContext().getRealPath(dir);
			File file = upload;
			String fileName = StringUtils.trimToEmpty(uploadFileName);
			// 检查文件上传类型
			if (!checkFileExt(fileName)) {
				return stringBuffer.toString();
			}
			int index = fileName.lastIndexOf(".");
			String suffex = fileName.substring(index);
			fileName = getFileMD5(file) + suffex;
			File target = new File(targetDirectory, fileName);
			if (!target.exists()) {
				FileUtils.copyFile(file, target);
			}
			stringBuffer.append(fileName);
		}
		return stringBuffer.toString();
	}

	/**
	 * 
	 * 文件上传,进行MD5校验 ,文件不重复
	 * 
	 * @param upload          上传文件[]
	 * @param dir             上传目录
	 * @param uploadFileNames 上传文件名称[]
	 * @throws IOException
	 */
	public static String uploadNoRepeat(File[] upload, String dir, String[] uploadFileNames) throws Exception {
		StringBuffer stringBuffer = new StringBuffer();
		if (upload != null && !upload.equals("")) {// 为空表示无附件上传
			File directory = new File(dir);// 创建上传路径
			if (!directory.exists()) {
				directory.mkdirs();
			}
			String targetDirectory = ServletActionContext.getServletContext().getRealPath(dir);
			for (int i = 0; i < uploadFileNames.length; i++) {
				File file = upload[i];
				String fileName = uploadFileNames[i].trim();
				// 检查文件上传类型
				if (!checkFileExt(fileName)) {
					return stringBuffer.toString();
				}
				int index = fileName.lastIndexOf(".");
				String suffex = fileName.substring(index);
				fileName = getFileMD5(file) + suffex;
				File target = new File(targetDirectory, fileName);
				if (!target.exists()) {
					FileUtils.copyFile(file, target);
				}
				stringBuffer.append(fileName).append(",");
			}
		}
		return stringBuffer.toString();
	}

	// 计算文件的 MD5 值
	public static String getFileMD5(File mFile) throws FileNotFoundException {
		MessageDigest digest = null;
		InputStream in = new FileInputStream(mFile);
		byte buffer[] = new byte[8192];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
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

	public static String getUploadFileRename(String targetFileName) {
		return getRename(targetFileName);
	}

	private static String getRename(String targetFileName) {
		String[] arr = targetFileName.split("\\.");
		String diff = "";
		String name = getName();
		if (arr.length > 0) {
			diff = arr[arr.length - 1];
		}

		return name + "." + diff;
	}

	private static String getName() {
		String[] arr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
				"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		StringBuilder name = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		name.append(sdf.format(new Date()));
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			name.append(arr[random.nextInt(arr.length - 1)]);
		}
		return name.toString();
	}

	/**
	 * 检查上传的文件类型
	 * 
	 * @param files
	 * @return
	 * @throws UploadException
	 */
	public static boolean checkFileExt(File file) throws Exception {
		return checkFileExt(file, null);
	}

	/**
	 * 检查上传的文件类型
	 * 
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
	 * 
	 * @param files
	 * @return
	 * @throws UploadException
	 */
	public static boolean checkFileExt(File[] files) {
		return checkFileExt(files, null);
	}

	/**
	 * 检查上传的文件类型
	 * 
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
				throw new UploadException("不允许上传类型为空的文件，请上传类型为|" + allowFileType + "|的文件！");
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
					throw new UploadException("不允许上传类型为【." + suffix + "】的文件，请上传类型为|" + allowFileType + "|的文件！");
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

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("[A-Za-z]{1}[0-9]{2}|[0-9]{3}");
		String suffix = "c01";
		System.out.println(suffix + pattern.matcher(suffix).matches());
		suffix = "r01";
		System.out.println(suffix + pattern.matcher(suffix).matches());
		suffix = "s01";
		System.out.println(suffix + pattern.matcher(suffix).matches());
		suffix = "001";
		System.out.println(suffix + pattern.matcher(suffix).matches());
		suffix = "0011";
		System.out.println(suffix + pattern.matcher(suffix).matches());
		suffix = "s011";
		System.out.println(suffix + pattern.matcher(suffix).matches());
	}
}
