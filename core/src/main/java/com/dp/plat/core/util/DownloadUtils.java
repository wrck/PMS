package com.dp.plat.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dp.plat.core.pojo.FileInfo;

public class DownloadUtils {

	private final static Logger logger = LoggerFactory.getLogger(DownloadUtils.class);
	
	private static final int BUFFER_SIZE = 2 * 1024;

	/**
	 * zip打包文件并提供下载
	 * 
	 * @param zipPath         压缩文件临时路径 路径最后不要有 /
	 * @param zipName         压缩为文件名 **.zip
	 * @param createFilesPath 需要压缩的文件列表
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static void downZip(String zipPath, String zipName, List<FileInfo> filesPath, HttpServletRequest request,
			HttpServletResponse response) {
		// 项目路径
		String webPath = request.getSession().getServletContext().getRealPath("/");
		byte[] buffer = new byte[BUFFER_SIZE];
		String strZipPath = webPath + zipPath + "/" + zipName;
		File tmpZipFile = null;
		try {
			File tmpZip = new File(webPath + zipPath);
			if (!tmpZip.exists())
				tmpZip.mkdirs();
			tmpZipFile = new File(strZipPath);
			if (!tmpZipFile.exists())
				tmpZipFile.createNewFile();
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmpZipFile));

			File[] file1 = new File[filesPath.size()];

			for (int i = 0; i < filesPath.size(); i++) {
				file1[i] = new File(webPath + filesPath.get(i).getPath());
			}
			for (int i = 0; i < file1.length; i++) {
				FileInputStream fis = new FileInputStream(file1[i]);
				out.putNextEntry(new ZipEntry(filesPath.get(i).getName()));
				int len;
				// 读入需要下载的文件的内容，打包到zip文件
				while ((len = fis.read(buffer)) > 0) {
					out.write(buffer, 0, len);
				}
				out.closeEntry();
				fis.close();
			}
			out.close();

			downFile(response, request, tmpZipFile.getPath(), zipName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * zip打包文件并提供下载
	 * 
	 * @param zipPath         压缩文件临时路径 路径最后不要有 /
	 * @param zipName         压缩为文件名 **.zip
	 * @param createFilesPath 需要压缩的文件列表
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static void downTempZip(String zipPath, String zipName, List<FileInfo> filesPath, HttpServletRequest request,
			HttpServletResponse response) {
		// 项目路径
		String webPath = request.getSession().getServletContext().getRealPath("/");
		byte[] buffer = new byte[BUFFER_SIZE];
		File tmpZipFile = null;
		String zipDir = webPath + zipPath;
		try {
			File tmpZip = new File(zipDir);
			if (!tmpZip.exists()) {
				tmpZip.mkdirs();
			}
			
			File[] innerFiles = new File[filesPath.size()];

			for (int i = 0; i < filesPath.size(); i++) {
				innerFiles[i] = new File(webPath + filesPath.get(i).getPath());
			}
			String md5 = getFileByMD5(innerFiles) + ".zip";
			String strZipPath = zipDir + "/" + md5;
			tmpZipFile = new File(strZipPath);
			if (!tmpZipFile.exists()) {
					tmpZipFile.createNewFile();
	//				tmpZipFile = File.createTempFile("ZIP", ".zip", tmpZip);
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmpZipFile));
	
				for (int i = 0; i < innerFiles.length; i++) {
					FileInputStream fis = new FileInputStream(innerFiles[i]);
					out.putNextEntry(new ZipEntry(filesPath.get(i).getName()));
					int len;
					// 读入需要下载的文件的内容，打包到zip文件
					while ((len = fis.read(buffer)) > 0) {
						out.write(buffer, 0, len);
					}
					out.closeEntry();
					fis.close();
				}
				out.close();
			}

			downFile(response, request, tmpZipFile.getPath(), zipName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tmpZipFile.deleteOnExit();
		}
	}
	
	public static void downZip(String zipPath, String zipName, List<File> fileList, boolean isTemp,
			HttpServletRequest request, HttpServletResponse response) {
		// 项目路径
		String webPath = request.getSession().getServletContext().getRealPath("/");
		byte[] buffer = new byte[BUFFER_SIZE];
		File tmpZipFile = null;
		String zipDir = webPath + zipPath;
		try {
			File tmpZip = new File(zipDir);
			if (!tmpZip.exists()) {
				tmpZip.mkdirs();
			}
			String md5 = getFileByMD5(fileList.toArray(new File[] {})) + ".zip";
			String strZipPath = zipDir + "/" + md5;
			tmpZipFile = new File(strZipPath);
			if (!tmpZipFile.exists()) {
					tmpZipFile.createNewFile();
	//				tmpZipFile = File.createTempFile("ZIP", ".zip", tmpZip);
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmpZipFile));
				for (int i = 0; i < fileList.size(); i++) {
					File file = fileList.get(i);
					compress(file, out, file.getName(), true);
				}
				out.flush();
				out.close();
			}

			downFile(response, request, tmpZipFile.getPath(), zipName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isTemp) {
				tmpZipFile.deleteOnExit();
			}
		}
	}

	/**
	 * 文件下载，支持断点续传
	 * 
	 * @param response
	 * @param request
	 * @param filePath
	 * @param fileName
	 */
	public static void downFile(HttpServletResponse response, HttpServletRequest request, String filePath,
			String fileName) {
		BufferedInputStream bis = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				long p = 0L;
				long toLength = 0L;
				long contentLength = 0L;
				int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的下载（bytes=27000-）；2,从某字节开始到某字节结束的下载（bytes=27000-39000）
				long fileLength;
				String rangBytes = "";
				fileLength = file.length();

				// get file content
				InputStream ins = new FileInputStream(file);
				bis = new BufferedInputStream(ins);

				// tell the client to allow accept-ranges
				response.reset();
				response.setHeader("Accept-Ranges", "bytes");

				// client requests a file block download start byte
				String range = request.getHeader("Range");
				if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
					response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
					rangBytes = range.replaceAll("bytes=", "");
					if (rangBytes.endsWith("-")) { // bytes=270000-
						rangeSwitch = 1;
						p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
						contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
					} else { // bytes=270000-320000
						rangeSwitch = 2;
						String temp1 = rangBytes.substring(0, rangBytes.indexOf("-"));
						String temp2 = rangBytes.substring(rangBytes.indexOf("-") + 1, rangBytes.length());
						p = Long.parseLong(temp1);
						toLength = Long.parseLong(temp2);
						contentLength = toLength - p + 1; // 客户端请求的是
															// 270000-320000
															// 之间的字节
					}
				} else {
					contentLength = fileLength;
				}

				// 如果设设置了Content-Length，则客户端会自动进行多线程下载。如果不希望支持多线程，则不要设置这个参数。
				// Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]
				response.setHeader("Content-Length", new Long(contentLength).toString());

				// 断点开始
				// 响应的格式是:
				// Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
				if (rangeSwitch == 1) {
					String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
							.append(new Long(fileLength - 1).toString()).append("/")
							.append(new Long(fileLength).toString()).toString();
					response.setHeader("Content-Range", contentRange);
					bis.skip(p);
				} else if (rangeSwitch == 2) {
					String contentRange = range.replace("=", " ") + "/" + new Long(fileLength).toString();
					response.setHeader("Content-Range", contentRange);
					bis.skip(p);
				} else {
					String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
							.append(fileLength).toString();
					response.setHeader("Content-Range", contentRange);
				}

				fileName = processFileName(request, fileName); // 解决中文乱码问题
				response.setCharacterEncoding("utf-8");
				response.setContentType("application/octet-stream");
				response.addHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");// 文件名称加双引号，解决火狐浏览器下下载中文文件名名称缺失问题

				OutputStream out = response.getOutputStream();
				int n = 0;
				long readLength = 0;
				int bsize = BUFFER_SIZE;
				byte[] bytes = new byte[bsize];
				if (rangeSwitch == 2) {
					// 针对 bytes=27000-39000 的请求，从27000开始写数据
					while (readLength <= contentLength - bsize) {
						n = bis.read(bytes);
						readLength += n;
						out.write(bytes, 0, n);
					}
					if (readLength <= contentLength) {
						n = bis.read(bytes, 0, (int) (contentLength - readLength));
						out.write(bytes, 0, n);
					}
				} else {
					while ((n = bis.read(bytes)) != -1) {
						out.write(bytes, 0, n);
					}
				}
				out.flush();
				out.close();
				bis.close();
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Error: file " + filePath + " not found.");
				}
			}
		} catch (IOException ie) {
			// 忽略 ClientAbortException 之类的异常
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 
	 * @throws UnsupportedEncodingException
	 * @Title: processFileName
	 * 
	 * @Description: ie,chrom,firfox下处理文件名显示乱码
	 */
	public static String processFileName(HttpServletRequest request, String fileName)
			throws UnsupportedEncodingException {

		if (isMSBrowser(request)) {// 微软浏览器
			fileName = URLEncoder.encode(fileName, "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
		} else {
			fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		}
		return fileName;
	}
	
	/**
     * 压缩成ZIP 方法1
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出流
     * @param KeepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *                         <p>
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure) throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);
            long end = System.currentTimeMillis();
            logger.info("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            logger.error("文件压缩失败：" + e.getMessage());
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	/**
	 * 递归压缩方法
	 *
	 * @param sourceFile       源文件
	 * @param zos              zip输出流
	 * @param name             压缩后的名称
	 * @param KeepDirStructure 是否保留原来的目录结构, true:保留目录结构;
	 *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @throws Exception
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception {
		byte[] buf = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			// Complete the entry
			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// 需要保留原来的文件结构时,需要对空文件夹进行处理
				if (KeepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));
					// 没有文件，不需要文件的copy
					zos.closeEntry();
				}
			} else {
				for (File file : listFiles) {
					// 判断是否需要保留原来的文件结构
					if (KeepDirStructure) {
						// 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
						// 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
						compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
					} else {
						compress(file, zos, file.getName(), KeepDirStructure);
					}
				}
			}
		}
	}

	private static String[] IEBrowserSignals = { "MSIE", "Trident", "Edge" };

	/**
	 * 使用Trident和Edge关键字来判断是否是微软的浏览器（微软抛弃了IE，开始使用Edge了）
	 * 
	 * @param request
	 * @return
	 */
	private static boolean isMSBrowser(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		for (String signal : IEBrowserSignals) {
			if (userAgent.contains(signal))
				return true;
		}
		return false;
	}

	private static String getFileByMD5(File[] files) {
		MessageDigest digest = null;
		InputStream in = null;
		byte buffer[] = new byte[8192];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			Arrays.parallelSort(files);
			for (File mFile : files) {
				if (mFile.isDirectory()) {
					byte[] bytes = mFile.getName().getBytes("UTF-8");
					digest.update(bytes, 0, bytes.length);
					String md5 = getFileByMD5(mFile.listFiles());
					if (md5 != null) {
						BigInteger md5Int = new BigInteger(md5, 32);
						bytes = md5Int.toByteArray();
						digest.update(bytes, 0, bytes.length);
					}
				} else {
					in = new FileInputStream(mFile);
					while ((len = in.read(buffer)) != -1) {
						digest.update(buffer, 0, len);
					}
					try {
						in.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(32);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
