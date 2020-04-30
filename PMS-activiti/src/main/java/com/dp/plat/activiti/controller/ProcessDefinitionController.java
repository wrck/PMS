package com.dp.plat.activiti.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.dp.plat.activiti.entity.BaseVO;
import com.dp.plat.activiti.entity.ProcessDefinitionEntity;
import com.dp.plat.activiti.service.IProcessService;
import com.dp.plat.activiti.utils.WorkflowUtils;
import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.PageParam;
import com.dp.plat.support.PropertiesUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "definition")
public class ProcessDefinitionController {


	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private IProcessService processService;
	
	/**
	 * 
	 * 列出所有流程模板
	 * 
	 */
	@RequestMapping
	public String list() {
		return Consts.URLPath.WORKFLOW_MANAGER + "process_list";
	}

	/**
	 * 流程定义的加载
	 * 
	 * @param page
	 * @param rows
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/list")
	public String listProcess(PageParam<BaseVO> pageParam, Model model) throws Exception {
		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
				.orderByDeploymentId().desc();
		pageParam.setTotal(processDefinitionQuery.list().size());
		List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(pageParam.getStart(),
				(int) pageParam.getPageSize());

		List<ProcessDefinitionEntity> pdList = new ArrayList<ProcessDefinitionEntity>();
		for (ProcessDefinition processDefinition : processDefinitionList) {
			ProcessDefinitionEntity pd = new ProcessDefinitionEntity();
			String deploymentId = processDefinition.getDeploymentId();
			Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
			// 封装到ProcessDefinitionEntity中
			pd.setId(processDefinition.getId());
			pd.setName(processDefinition.getName());
			pd.setKey(processDefinition.getKey());
			pd.setDeploymentId(processDefinition.getDeploymentId());
			pd.setVersion(processDefinition.getVersion());
			pd.setResourceName(processDefinition.getResourceName());
			pd.setDiagramResourceName(processDefinition.getDiagramResourceName());
			pd.setDeploymentTime(deployment.getDeploymentTime());
			pd.setSuspended(processDefinition.isSuspended());
			pdList.add(pd);
		}
		model.addAttribute("data", pdList);
		return Consts.URLPath.WORKFLOW_MANAGER + "process_list";
	}

	/**
	 * 导入部署 --@Value用于将一个SpEL表达式结果映射到到功能处理方法的参数上。
	 * 
	 * @RequestParam(value = "file", required = false) required =
	 *                     false时可以不用传递这个参数，默认为true
	 * @param exportDir
	 * @param file
	 * @return
	 */
	// @RequiresPermissions("admin:process:*")
	@RequestMapping(value = "/deploy")
	public void deploy(@RequestParam(value = "deployFile", required = false) MultipartFile file, HttpServletRequest httpRequest, Model model) {
		/*String exportDir = PropertiesUtil.getValue("export.diagram.path");
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("/");
			exportDir = properties.getProperty("export.diagram.path");
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/
		String fileName = file.getOriginalFilename();
		try {
			InputStream fileInputStream = file.getInputStream();
			Deployment deployment = null;

			String extension = FilenameUtils.getExtension(fileName);
			if (extension.equals("zip") || extension.equals("bar")) {
				ZipInputStream zip = new ZipInputStream(fileInputStream);
				deployment = this.repositoryService.createDeployment().name(fileName).addZipInputStream(zip).deploy();
			} else {
				deployment = this.repositoryService.createDeployment().addInputStream(fileName, fileInputStream)
						.deploy();
			}

			/*List<ProcessDefinition> list = this.repositoryService.createProcessDefinitionQuery()
					.deploymentId(deployment.getId()).list();

			for (ProcessDefinition processDefinition : list) {
				WorkflowUtils.exportDiagramToFile(this.repositoryService, processDefinition, exportDir);
			}*/
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "流程部署成功！");
		} catch (Exception e) {
			model.addAttribute("status", Boolean.FALSE);
			model.addAttribute("message", "流程部署失败！");
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return;
	}

	/**
	 * 删除部署的流程，级联删除流程实例 true。
	 * 不管是否指定级联删除，部署的相关数据均会被删除，这些数据包括流程定义的身份数据（IdentityLink）、流程定义数据（
	 * ProcessDefinition）、流程资源（Resource） 部署数据（Deployment）。
	 * 如果设置级联(true)，则会删除流程实例数据（ProcessInstance）,其中流程实例也包括流程任务（Task）与流程实例的历史数据；
	 * 如果设置flase 将不会级联删除。
	 * 如果数据库中已经存在流程实例数据，那么将会删除失败，因为在删除流程定义时，流程定义数据的ID已经被流程实例的相关数据所引用。
	 *
	 * @param deploymentId
	 *            流程部署ID
	 */
	@RequestMapping(value = "/{deploymentId}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("deploymentId") String deploymentId, Model model) {
		this.repositoryService.deleteDeployment(deploymentId, true);
		model.addAttribute("status", Boolean.TRUE);
		model.addAttribute("message", "删除成功！");
		return;
	}

	/**
	 * 转换为model
	 * 
	 * @param processDefinitionId
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws XMLStreamException
	 */
	@RequestMapping(value = "/model/{processDefinitionId}")
	public void convertToModel(@PathVariable("processDefinitionId") String processDefinitionId, Model model)
			throws UnsupportedEncodingException, XMLStreamException {
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
				processDefinition.getResourceName());
		XMLInputFactory xif = XMLInputFactory.newInstance();
		InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
		XMLStreamReader xtr = xif.createXMLStreamReader(in);
		BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

		BpmnJsonConverter converter = new BpmnJsonConverter();
		com.fasterxml.jackson.databind.node.ObjectNode modelNode = converter.convertToJson(bpmnModel);
		org.activiti.engine.repository.Model modelData = repositoryService.newModel();
		modelData.setKey(processDefinition.getKey());
		modelData.setName(processDefinition.getName());
		modelData.setCategory(processDefinition.getDeploymentId());

		ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
		modelData.setMetaInfo(modelObjectNode.toString());

		repositoryService.saveModel(modelData);
		repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

		model.addAttribute("status", Boolean.TRUE);
		model.addAttribute("message", "转换成功！请到[ 流程设计模型 ]菜单中查看！");
		return;
	}

	/**
	 * 显示图片通过部署id，不带流程跟踪(没有乱码问题)
	 * 
	 * @param processDefinitionId
	 * @param resourceType
	 *            资源类型(xml|image)
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/{resourceType}/{processDefinitionId}")
	public void loadByDeployment(@PathVariable("processDefinitionId") String processDefinitionId,
			@PathVariable("resourceType") String resourceType, HttpServletResponse response) throws Exception {
		InputStream resourceAsStream = this.processService.getDiagramByProDefinitionId_noTrace(resourceType,
				processDefinitionId);
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	/**
	 * 激活、挂起流程定义-根据processDefinitionId
	 * 
	 * @param status
	 * @param processInstanceId
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	// @RequiresPermissions("admin:process:suspend,active")
	@RequestMapping(value = "/{status}/{processDefinitionId}" , method = RequestMethod.POST)
	public void updateProcessStatusByProDefinitionId(@PathVariable("status") String status,
			@PathVariable("processDefinitionId") String processDefinitionId, Model model) throws Exception {
		// 如果用/{status}/{processDefinitionId}
		// rest风格，@PathVariable获取的processDefinitionId
		// 为com.zml.oa,实际是com.zml.oa.vacation:1:32529.难道是BUG?
		if (status.equals("active")) {
			repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "已激活ID为[" + processDefinitionId + "]的流程定义。");
		} else if (status.equals("suspend")) {
			repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
			model.addAttribute("status", Boolean.TRUE);
			model.addAttribute("message", "已挂起ID为[" + processDefinitionId + "]的流程定义。");
		}
		return;
	}

}
