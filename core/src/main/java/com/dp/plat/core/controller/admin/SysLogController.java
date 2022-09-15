package com.dp.plat.core.controller.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.context.HttpContext;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.SysLog;
import com.dp.plat.core.service.ISysLogService;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.util.UploadUtils;
import com.dp.plat.core.vo.PageParam;

/**
 * 日志管理Controller
 * 
 * @author sunmengyuan
 *
 */

@Controller()
@RequestMapping(Consts.URLPath.SYSTEM_MANAGER + "syslog")
public class SysLogController {
	@Resource
	private ISysLogService sysLogService;

	@RequestMapping
	public void listView() {
	}

	@RequestMapping("/list")
	public String getContractData(PageParam<SysLog> pageParam, SysLog data, Model model) {
		if (HttpContext.isJSON()) {
			pageParam.setModel(data);
			pageParam.setTotal(sysLogService.countBySelective(null));
			pageParam.setFiltered(sysLogService.countBySelective(pageParam));
			List<SysLog> dataList = new ArrayList<>();
			if (pageParam.getPageSize() == -1L) {
				pageParam.setPageSize(pageParam.getTotal());
			}
			dataList = sysLogService.selectBySelective(pageParam);
			model.addAttribute("data", dataList);
		}
		return Consts.URLPath.SYSTEM_MANAGER + "syslog";
	}

	@RequestMapping("{id}")
	public String getOne(@PathVariable("id") Integer id, Model model) {
		model.addAttribute("id", id);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog_detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.POST)
	public String findOne(@PathVariable("id") Integer id, Model model) {
		SysLog sysLog = sysLogService.selectByPrimaryKey(id);
		model.addAttribute("sysLog", sysLog);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog_detail";
	}

	@RequestMapping(value = "/disk")
	public String logsDisk(@RequestParam(value = "path", required = false) String path, HttpServletRequest request, Model model) {
		if (HttpContext.isJSON()) {
			Set<String> paths = new HashSet<String>();
			if (StringUtils.isBlank(path)) {
				String webRoot = FileUtil.getWebRoot();
				paths.add(webRoot);
				String catalinaHome = System.getProperty("catalina.home");
				paths.add(catalinaHome);
			} else {
				paths.add(new String(Base64Utils.decodeFromUrlSafeString(path)));
			}
			List<Map<String, Object>> diskList = new ArrayList<Map<String, Object>>();
			int deep = 10;
			for (String dir : paths) {
				int level = StringUtils.splitByWholeSeparator(dir, File.separator).length;
				while(dir != null) {
					List<File> filterFiles = FileUtil.filterFiles(dir, "logs", "all", false, false);
					for (File file : filterFiles) {
						String type = file.isDirectory() ? "dir" : "file";
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("fileName", file.getName());
						map.put("type", type);
						map.put("filePath", file.getPath());
						map.put("filePathEncode", Base64Utils.encodeToUrlSafeString(file.getPath().getBytes()));
						map.put("fileSize", FileUtils.sizeOf(file));
						map.put("lastModified", file.lastModified());
						map.put("typeLevel", type + String.format("%0" + deep + "d", level));
						diskList.add(map);
					}
					if (StringUtils.isBlank(path)) {
						dir = FileUtil.getParent(dir, 1);
						level--;
					} else {
						break;
					}
				}
			}
			PageParam<Object> pageParam = new PageParam<Object>();
			pageParam.setTotal(diskList.size());
			model.addAttribute("pageParam", pageParam);
			model.addAttribute("data", diskList);
		}
		model.addAttribute("path", path);
		return Consts.URLPath.SYSTEM_MANAGER + "syslog_disk";
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public void downloadLogs(@RequestParam("path") String path, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		String[] paths = path.split(";");
		List<File> fileList = new ArrayList<File>();
		for (String dir : paths) {
			dir = new String(Base64Utils.decodeFromUrlSafeString(dir));
			List<File> filterFiles = FileUtil.filterFiles(dir, "logs", "all", false, false);
			fileList.addAll(filterFiles);
		}
//		List<FileInfo> fileInfos = new ArrayList<FileInfo>(paths.length);
//		for (String filePath : paths) {
//			String[] split = filePath.split(File.pathSeparator);
//			String fileName = split[Math.max(0, split.length - 1)];
//			FileInfo fileInfo = new FileInfo(0, fileName, filePath, null, null);
//			fileInfos.add(fileInfo);
//		}
		DownloadUtils.downZip(UploadUtils.UPLOAD_PATH + "/temp", FileUtil.generZipFileName(), fileList, true, request, response);
	}
}
