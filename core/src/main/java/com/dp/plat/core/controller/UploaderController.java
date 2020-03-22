package com.dp.plat.core.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.util.Streams;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.pojo.AvatarResult;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.pojo.User;
import com.dp.plat.core.service.IUploaderService;
import com.dp.plat.core.service.IUserService;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.vo.Result;

@Controller
@RequestMapping("/file")
public class UploaderController {
	private final static String FILE_SEPARATOR = File.separator;

	@Resource
	private IUploaderService uploaderService;

	@Resource
	private IUserService userService;

	@RequestMapping("/avatarUpload")
	@ResponseBody
	public AvatarResult avatarUpload(String userId, HttpServletRequest httpRequest, HttpSession session)
			throws Exception {
		MultipartHttpServletRequest request = (MultipartHttpServletRequest) httpRequest;
		Map<String, MultipartFile> fileMap = request.getFileMap();
		String contentType = request.getContentType();
		if (contentType.indexOf("multipart/form-data") >= 0) {
			AvatarResult result = new AvatarResult();
			result.setAvatarUrls(new ArrayList<String>());
			result.setSuccess(false);
			result.setMsg("Failure!");

			// 定义一个变量用以储存当前头像的序号
			int avatarNumber = 1;
			User user = null;
			if (!StringUtils.isEmpty(userId)) {
				user = userService.selectByPrimaryKey(Integer.parseInt(userId));
			}
			if (user == null) {
				user = new User();
				user.setUserName("new");
			}
			String relPath = "upload" + FILE_SEPARATOR + "avatar";
			String dirPath = request.getServletContext().getRealPath("/");

			String initParams = "";

			BufferedInputStream inputStream;
			BufferedOutputStream outputStream;
			for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator(); it
					.hasNext(); avatarNumber++) {
				File filePath = new File(dirPath + relPath);
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
				Map.Entry<String, MultipartFile> entry = it.next();
				MultipartFile mFile = entry.getValue();
				String fileName = FileUtil.getFileMD5(mFile) + ".png";
				File file = new File(dirPath + relPath + FILE_SEPARATOR + fileName);
				if (file.isFile() && file.exists()) {
					result.getAvatarUrls().add("/" + relPath.replace("\\", "/") + "/" + fileName);
					// 保存图片信息
					// result.setMsg(
					// uploaderService.saveAvatar(userId, fileName, relPath +
					// File.separator + fileName, dirPath));
				} else {
					String fieldName = entry.getKey();
					Boolean isSourcePic = fieldName.equals("__source"); // 是否是原始图片域名称
					// 文件名，如果是本地或网络图片为原始文件名（不含扩展名）、如果是摄像头拍照则为 *FromWebcam
					// String name = fileItem.getName();
					// 当前头像基于原图的初始化参数（即只有上传原图时才会发送该数据），用于修改头像时保证界面的视图跟保存头像时一致，提升用户体验度。
					// 修改头像时设置默认加载的原图url为当前原图url+该参数即可，可直接附加到原图url中储存，不影响图片呈现。
					if (fieldName.equals("__initParams")) {
						inputStream = new BufferedInputStream(mFile.getInputStream());
						byte[] bytes = new byte[mFile.getInputStream().available()];
						inputStream.read(bytes);
						initParams = new String(bytes, "UTF-8");
						inputStream.close();
					} else if (isSourcePic || fieldName.startsWith("__avatar")) {
						String virtualPath = dirPath + relPath + FILE_SEPARATOR + fileName;
						if (avatarNumber > 1) {
							fileName = avatarNumber + fileName;
							virtualPath = dirPath + relPath + FILE_SEPARATOR + fileName;
						}
						// 原始图片(file 域的名称：__source，如果客户端定义可以上传的话，可在此处理）。
						if (isSourcePic) {
							fileName = "source" + fileName;
							virtualPath = dirPath + relPath + FILE_SEPARATOR + fileName;
							result.setSourceUrl("/" + relPath.replace("\\", "/") + "/" + fileName);
						}
						// 头像图片(file 域的名称：__avatar1,2,3...)。
						else {
							result.getAvatarUrls().add("/" + relPath.replace("\\", "/") + "/" + fileName);
						}
						inputStream = new BufferedInputStream(mFile.getInputStream());
						outputStream = new BufferedOutputStream(new FileOutputStream(virtualPath));
						Streams.copy(inputStream, outputStream, true);
						inputStream.close();
						outputStream.flush();
						outputStream.close();
						// 保存图片信息
						// result.setMsg(uploaderService.saveAvatar(userId,
						// fileName, relPath + File.separator + fileName,
						// dirPath));
					}
				}
			}
			if (result.getSourceUrl() != null) {
				result.setSourceUrl(result.getSourceUrl() + initParams);
			}
			result.setSuccess(true);
			return result;
		}
		return null;
	}

	@RequestMapping("/summernoteUpload")
	@ResponseBody
	public String summernoteUpload(HttpServletRequest httpRequest) throws IOException {
		MultipartHttpServletRequest request = (MultipartHttpServletRequest) httpRequest;
		Map<String, MultipartFile> fileMap = request.getFileMap();
		String contentType = request.getContentType();
		StringBuilder result = new StringBuilder();
		if (contentType.indexOf("multipart/form-data") >= 0) {
//			String relPath = "static" + FILE_SEPARATOR + "upload" + FILE_SEPARATOR + "summernote";
			String relPath = "upload" + FILE_SEPARATOR + "summernote";
			String dirPath = request.getServletContext().getRealPath("/");

			BufferedInputStream inputStream;
			BufferedOutputStream outputStream;
			for (Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator(); it.hasNext();) {
				File filePath = new File(dirPath + relPath);
				if (!filePath.exists()) {
					filePath.mkdirs();
				}
				Map.Entry<String, MultipartFile> entry = it.next();
				MultipartFile mFile = entry.getValue();
				String fileName = FileUtil.getFileMD5(mFile) + ".png";
				File file = new File(dirPath + relPath + FILE_SEPARATOR + fileName);
				if (file.isFile() && file.exists()) {

				} else {
					String virtualPath = dirPath + relPath + FILE_SEPARATOR + fileName;
//					result.append(virtualPath).append(";");
					inputStream = new BufferedInputStream(mFile.getInputStream());
					outputStream = new BufferedOutputStream(
							new FileOutputStream(virtualPath.replace("/", FILE_SEPARATOR)));
					Streams.copy(inputStream, outputStream, true);
					inputStream.close();
					outputStream.flush();
					outputStream.close();
				}
				String url = request.getContextPath();
				String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
						+ url;
				result.append(basePath + "/" + relPath.replace("\\", "/") + "/" + fileName).append(";");
			}
		}
		return result.toString();
	}
	
	
	/**
	 * 	文件上传
	 * @param fileType
	 * @param httpRequest
	 * @return
	 */
	@PostMapping("/baseUpload/{fileType}")
	@ResponseBody
	public Result baseUploadFile(@PathVariable("fileType")String fileType,HttpServletRequest httpRequest) {
		Result result = null;
		try {
			List<FileInfo> fileInfos = uploaderService.baseUploadFile(fileType ,httpRequest);
			result = new Result(true, fileInfos);
		} catch (Exception e) {
			ExceptionHandler.insertException(e);
			result = new Result(false);
			result.setMessage(e.getMessage());
		}
		return result;
	}
	
	
	/**
	 * 公开的下载方法，取消用户登录限制
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/down/public/{fileId}")
	public String publicDownload(@PathVariable("fileId")Integer fileId,HttpServletRequest request,
			HttpServletResponse response) {
		uploaderService.fileDownload(fileId,request,response);
		return null;
	}
	/**
	 *   公开的下载方法，取消用户登录限制
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/zipdown/public/{fileIds}")
	public String zipPublicDownload(@PathVariable("fileIds")String fileIds,String zipName, HttpServletRequest request,
			HttpServletResponse response) {
		uploaderService.zipFileDownload(fileIds,zipName,request ,response);
		return null;
	}
	
	/**
	 * 私有的下载方法，用户必须登录后下载
	 * @param request
	 * @param response
	 * @return
	 */
	@GetMapping("/down/private/{fileId}")
	public String privateDownload(@PathVariable("fileId")Integer fileId,HttpServletRequest request,
			HttpServletResponse response) {
		uploaderService.fileDownload(fileId,request,response);
		return null;
	}
	
}
