package com.dp.plat.activiti.controller;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverterUtil;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.bpmn.parser.BpmnParser;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dp.plat.core.param.Consts;
import com.dp.plat.core.vo.PageParam;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping(Consts.URLPath.WORKFLOW_MANAGER + "model")
public class ModelController {

	private Logger logger = LoggerFactory.getLogger(ModelController.class);

	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private ManagementService managementService;

	/**
	 * 跳转模型列表
	 * 
	 * @return
	 */
	@RequestMapping
	public String toListModel() {
		return "workflow/model_list";
	}

	/**
	 * 模型列表
	 * 
	 * @return
	 */
	@RequestMapping("/list")
	public String findAll(PageParam<?> pageParam, org.springframework.ui.Model model) {
		ModelQuery modelQuery = repositoryService.createModelQuery();
		pageParam.setTotal(modelQuery.count());
		if (pageParam.getPageSize() == -1L) {
			pageParam.setPageSize(pageParam.getTotal());
		}
		List<Model> list = modelQuery.listPage(pageParam.getStart(), (int) pageParam.getPageSize());
		model.addAttribute("data", list);
		return "workflow/model_list";
	}

	@RequestMapping("/{modelId}")
	public String findOne(@PathVariable("modelId") String modelId, org.springframework.ui.Model model) {
		model.addAttribute("modelId", modelId);
		return "redirect:/modeler.html";
	}

	/**
	 * 跳转创建模型页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/create")
	public String toCreateModel() {
		return "workflow/add_model";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	public void create(@RequestParam("name") String name, @RequestParam("key") String key,
			@RequestParam("description") String description, HttpServletRequest request, HttpServletResponse response) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			Model modelData = repositoryService.newModel();

			ObjectNode modelObjectNode = objectMapper.createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			description = StringUtils.defaultString(description);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
			modelData.setMetaInfo(modelObjectNode.toString());
			modelData.setName(name);
			modelData.setKey(StringUtils.defaultString(key));

			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));

			response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
		} catch (Exception e) {
			logger.error("创建模型失败：", e);
		}
	}

	/**
	 * 根据Model部署流程
	 * 
	 * @param modelId
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "{modelId}", method = RequestMethod.PATCH)
	public String deploy(@PathVariable("modelId") String modelId, org.springframework.ui.Model modelUi) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			ObjectNode modelNode = (ObjectNode) new ObjectMapper()
					.readTree(repositoryService.getModelEditorSource(modelData.getId()));
			byte[] bpmnBytes = null;
			
			BpmnModel model = new com.dp.plat.activiti.converter.BpmnJsonConverter().convertToBpmnModel(modelNode);
		
			bpmnBytes = new BpmnXMLConverter().convertToXML(model);
			String processName = modelData.getName() + ".bpmn20.xml";
			Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
					.addString(processName, new String(bpmnBytes)).deploy();
			modelUi.addAttribute("status", Boolean.TRUE);
			modelUi.addAttribute("message", "部署成功，部署ID=" + deployment.getId() + " 请到【流程定义】菜单中查看！");
		} catch (Exception e) {
			modelUi.addAttribute("status", Boolean.FALSE);
			modelUi.addAttribute("message", "根据模型部署流程失败:modelId=" + modelId);
			logger.error("根据模型部署流程失败：modelId={}" + modelId, e);
		}
		return "workflow/model_list";
	}

	/**
	 * 删除模型
	 * 
	 * @param modelId
	 * @return
	 */
	@RequestMapping(value = "{modelId}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("modelId") String modelId, org.springframework.ui.Model modelUi) {
		try {
			repositoryService.deleteModel(modelId);
			modelUi.addAttribute("status", Boolean.TRUE);
			modelUi.addAttribute("message", "删除成功！");
		} catch (Exception e) {
			modelUi.addAttribute("status", Boolean.FALSE);
			modelUi.addAttribute("message", "删除失败！");
		}
		return "workflow/model_list";
	}
}
