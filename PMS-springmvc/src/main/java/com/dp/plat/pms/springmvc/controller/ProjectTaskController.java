package com.dp.plat.pms.springmvc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dp.plat.core.exception.exceptionHandler.ExceptionHandler;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.pojo.FileInfo;
import com.dp.plat.core.service.IUploaderService;
import com.dp.plat.core.util.DownloadUtils;
import com.dp.plat.core.util.FileUtil;
import com.dp.plat.core.vo.DataTableColumn;
import com.dp.plat.core.vo.PermissionResult;
import com.dp.plat.core.vo.Result;
import com.dp.plat.pms.springmvc.constant.ProjectConstant;
import com.dp.plat.pms.springmvc.constant.ProjectConstant.URLPath;
import com.dp.plat.pms.springmvc.entity.ProjectHeader;
import com.dp.plat.pms.springmvc.entity.ProjectTask;
import com.dp.plat.pms.springmvc.service.IProjectHeaderService;
import com.dp.plat.pms.springmvc.service.IProjectTaskService;
import com.dp.plat.pms.springmvc.vo.CommonRelatedDataVO;
import com.dp.plat.pms.springmvc.vo.PmWorkFlowVO;
import com.dp.plat.pms.springmvc.vo.ProjectDeliver;
import com.dp.plat.pms.springmvc.vo.TaskVO;

@Controller
@RequestMapping(ProjectConstant.URLPath.PROJECT_MANAGER + "/project/task")
public class ProjectTaskController extends AbstractController<IProjectTaskService, ProjectTask, TaskVO> {

	@Autowired
	private IProjectHeaderService projectHeaderService;

	@Autowired
	private IUploaderService uploaderService;

	@PostConstruct
	public void init() {
		this.setUrlNameSpace(URLPath.PROJECT_MANAGER);
		this.setViewModel("projectTask");
		this.setUseTemplate(true);
		this.setKeyword("taskId");
		this.setViewNameSpace("project/task/");
	}
	
	@Override
	@RequestMapping(value = "/detail", method = RequestMethod.POST)
	public String create(TaskVO v, Model model) {
		if (!checkPermission(v, model, "projectTask:add")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		Boolean status = true;
		String message = null;
		try {
			ProjectHeader project = projectHeaderService.selectByPrimaryKey(v.getProjectId());
			v.setCustomInfoByKey("project", project);
			service.insertSelective(v);
			model.addAttribute("targetName", this.getTargetName(v.getClass()));
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
//		return super.create(v, model);
	}

	@Override
	@PutMapping(value = "{id}")
	public String update(@PathVariable("id") Integer id, TaskVO v, Model model) {
		if (!checkPermission(v, model, "projectTask:edit")) {
			return Consts.VIEW_UNAUTHORIZED;
		}
		
		// 终止正在进行中的任务
		PmWorkFlowVO workflow = new PmWorkFlowVO();
		workflow.setDataId(v.getTaskId());
		workflow.setDataType("projectTask");
		workflow.setObjId(v.getProjectId());
		workflow.setObjType("project");
		workflow.setStatus(PmWorkFlowVO.PENDING);
		pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");

		Integer progress = v.getProgress() == null ? 0 : v.getProgress();
		if (progress < 100) {
			v.setStatus("50");// 进行中
		} else if (progress == 100) {
			v.setStatus("100");// 已完成，待审核
		}
//		String view = super.update(id, v, model);
		
		Boolean status = true;
		String message = null;
		try {
			ProjectHeader project = projectHeaderService.selectByPrimaryKey(v.getProjectId());
			v.setCustomInfoByKey("project", project);
			service.updateByPrimaryKeySelective(v);
			model.addAttribute("targetName", this.getTargetName(v.getClass()));
		
			// 添加进度记录
			CommonRelatedDataVO relatedData = new CommonRelatedDataVO(getViewModel(), v.getTaskId(), getViewModel() + "Log");
	
			relatedData.setCustomInfoByKey("projectName", project.getProjectName());
			relatedData.setCustomInfoByKey("customerName", project.getColumn003());
	//		relatedData.setCustomInfoByKey("taskName", v.getTaskName());
	//		relatedData.setCustomInfoByKey("progress", v.getProgress());
	//		relatedData.setCustomInfoByKey("progressDesc", v.getProgressDesc());
	//		relatedData.setCustomInfoByKey("remark", v.getRemark());
			relatedData.setCustomInfoByKey("task", v);
			commonRelatedDataService.insertSelective(relatedData);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
		return getRealViewNameSpace() + "detail";
	}

	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Integer id, Model model) {
		if (!checkPermission(null, model, "projectTask:delete")) {
			return;
		}

		Boolean status = true;
		String message = null;
		try {
			// 终止正在进行中的任务
			ProjectTask v = service.selectByPrimaryKey(id);
			PmWorkFlowVO workflow = new PmWorkFlowVO();
			workflow.setDataId(v.getTaskId());
			workflow.setDataType("projectTask");
			workflow.setObjId(v.getProjectId());
			workflow.setObjType("project");
			workflow.setStatus(PmWorkFlowVO.PENDING);
			pmWorkFlowService.terminateProcess(workflow, "审批内容发生变更！");
			
			ProjectTask task = new ProjectTask();
			task.setTaskId(id);
			task.setEffectiveTo(new Date());
			service.updateByPrimaryKeySelective(task);
		} catch (Exception e) {
			status = false;
			Integer errorId = ExceptionHandler.insertException(e);
			model.addAttribute("errorId", errorId);
			message = e.getMessage();
		}
		model.addAttribute("status", status);
		model.addAttribute("message", message);
	}

	@GetMapping("/modals/upload")
	public String toUpload(ProjectDeliver projectDeliver, Model model) {
		if (!checkPermission(null, model, "uploadDeliverFile:upload")) {
			return Consts.VIEW_UNAUTHORIZED;
		}

		String ek = StringUtils.trimToEmpty(projectDeliver.getEventKey());// 获取事件节点
		String[] eksplit = ek.split("-");
		projectDeliver.setDataTypeCode(eksplit[0]);
		if (eksplit.length > 1) {
			projectDeliver.setBasicDataId(eksplit[1]);
		}
		String projectType = projectDeliver.getProjectType();
		if (StringUtils.isNotBlank(projectType) && StringUtils.isBlank(projectDeliver.getColumn010())) {
			projectDeliver.setColumn010(projectType);
		}
		if (StringUtils.isBlank(projectDeliver.getColumn011())) {
			projectDeliver.setColumn011("");
		}
		List<com.dp.plat.data.bean.ProjectDeliver> projectDeliverList = projectHeaderService.queryProjectDeliverList(projectDeliver);
		if (projectDeliverList.isEmpty()) {
			projectDeliver.setBasicDataId("");
			projectDeliverList = projectHeaderService.queryProjectDeliverList(projectDeliver);
		}
		model.addAttribute("projectDeliverList", projectDeliverList);
		return getViewNameSpace() + "upload";
	}

	@PostMapping("upload")
	public void uploadDeliverFile(ProjectDeliver projectDeliver, @RequestPart MultipartFile[] deliverFiles,
			@RequestParam String[] deliverTypes, HttpServletRequest httpRequest, Model model) {
		Boolean status = true;
		String message = null;
		try {
			TaskVO taskVO = new TaskVO(projectDeliver.getProjectId(), projectDeliver.getProjectType());
			taskVO.setTaskId(projectDeliver.getTaskId());
			if (!checkPermission(taskVO, model, "uploadDeliverFile:upload")) {
				return;
			}
	
			String[] deliverIds = StringUtils.trimToEmpty(projectDeliver.getDeliverId()).split(",");
			if (deliverFiles != null && deliverFiles.length > 0) {
				for (int i = 0; i < deliverFiles.length; i++) {
					MultipartFile multipartFile = deliverFiles[i];
					if (multipartFile.getSize() == 0) {
						continue;
					}
	
					ProjectDeliver deliver = new ProjectDeliver();
					BeanUtils.copyProperties(projectDeliver, deliver);
					String deliverableType = deliverTypes[i];
					if (StringUtils.isNotBlank(deliverableType)) {
						String[] splits = StringUtils.split(deliverableType, ",");
						deliverableType = splits[0];
					}
					deliver.setDeliverableType(deliverableType);
					deliver.setDeliverId(deliverIds[i]);
					service.uploadFile(deliver, multipartFile);
				}
				
				boolean needRefresh = service.updateEventActualFinishDateByTask(projectDeliver);
				model.addAttribute("refreshProjectState", needRefresh);
				projectHeaderService.updateProjectLastRefreshTime(projectDeliver.getProjectId());
			}
		} catch (Exception e) {
			status = false;
			message = e.getMessage();
		}
		model.addAllAttributes(new Result(status, message).getMap());
	}
	
	@DeleteMapping("upload/{deliverId}")
	public void uploadDeliverFile(@PathVariable("deliverId") String deliverId, ProjectDeliver projectDeliver, Model model) {
		TaskVO taskVO = new TaskVO(projectDeliver.getProjectId(), projectDeliver.getProjectType());
		taskVO.setTaskId(projectDeliver.getTaskId());
		if (!checkPermission(taskVO, model, "uploadDeliverFile:delete")) {
			return;
		}

		projectDeliver.setDeliverId(deliverId);
		service.deleteDeliverFile(projectDeliver);
	}

	@GetMapping("upload/list")
	public String uploadList(ProjectDeliver projectDeliver, Model model) {
		TaskVO taskVO = new TaskVO(projectDeliver.getProjectId(), projectDeliver.getProjectType());
		taskVO.setTaskId(projectDeliver.getTaskId());
		if (!checkPermission(taskVO, model, "uploadDeliverFile:list")) {
			return Consts.VIEW_UNAUTHORIZED;
		}

		List<ProjectDeliver> delivers = service.selectProjectDeliverBySelective(projectDeliver);
		List<DataTableColumn> columnList = findColumnList("projectTaskUploadList");
		model.addAttribute("columns", columnList);
		model.addAttribute("data", delivers);
		return getViewNameSpace() + "upload";
	}

	@RequestMapping("download")
	public void download(ProjectDeliver projectDeliver, HttpServletRequest request, HttpServletResponse response,
			Model model) {
		TaskVO taskVO = new TaskVO(projectDeliver.getProjectId(), projectDeliver.getProjectType());
		taskVO.setTaskId(projectDeliver.getTaskId());
		if (!checkPermission(taskVO, model, "uploadDeliverFile:download")) {
			return;
		}

		Object ids = projectDeliver.getIds();
		if (!(ids instanceof Collection || ids instanceof String[] || ids instanceof Integer[])) {
			if (ids instanceof String) {
				String[] split = StringUtils.split(StringUtils.trimToEmpty((String) ids), ",");
				ids = Arrays.asList(split);
			} else {
				ids = Arrays.asList(ids);
			}
			projectDeliver.setIds(ids);
		}
		List<ProjectDeliver> list = service.selectProjectDeliverBySelective(projectDeliver);
		if (list.isEmpty()) {
			model.addAttribute("status", false);
			model.addAttribute("message", "找不到指定文件！");
			return;
		}
		if (list.size() == 1) {
			ProjectDeliver deliver = list.get(0);

			FileInfo fileInfo = new FileInfo();
			fileInfo.setName(deliver.getDeliverableName());
			fileInfo.setPath(deliver.getDeliverablePath());
			uploaderService.fileDownload(fileInfo, request, response);
		} else {
			List<FileInfo> fileInfos = new ArrayList<FileInfo>(list.size());
			for (ProjectDeliver deliver : list) {
				FileInfo fileInfo = new FileInfo();
				fileInfo.setName(deliver.getDeliverableName());
				fileInfo.setPath(deliver.getDeliverablePath());
				fileInfos.add(fileInfo);
			}
			DownloadUtils.downTempZip("upload/temp", FileUtil.generZipFileName(), fileInfos, request, response);
//			uploaderService.zipFileDownload(null, fileInfos, request, response);
		}
	}

	@Override
	public boolean checkPermission(TaskVO v, Model model, String... permissions) {
		if (!super.checkPermission(v, model, permissions)) {
			return false;
		}
		PermissionResult result = service.checkPermission(v, permissions);
		model.addAllAttributes(result.getMap());
		return result.isPermit();
		//return super.checkPermission(v, model, permissions);
	}

}