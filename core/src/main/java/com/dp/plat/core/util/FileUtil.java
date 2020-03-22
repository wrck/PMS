package com.dp.plat.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.exception.UploadException;

/**
 * @author w02611
 *
 */
public class FileUtil {

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
				throw new UploadException("不允许上传类型为【."+suffix+"】的文件，请上传类型为|"+fileType+"|的文件！");
			}
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
}
