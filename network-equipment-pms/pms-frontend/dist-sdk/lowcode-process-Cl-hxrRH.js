import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-process.ts
function getProcessBindings() {
	return get("/api/lowcode/process/bindings");
}
function saveProcessBinding(data) {
	return post("/api/lowcode/process/bindings", data);
}
function getProcessDefinitions(page = 1, size = 20) {
	return get("/api/lowcode/process/definitions", {
		page,
		size
	});
}
/**
* 查询流程实例当前活动节点 ID 列表（用于流程预览模式高亮）。
*
* <p>返回当前未完成的活动节点 taskDefinitionKey 数组。
* 若后端尚未实现，前端会以空数组降级处理。</p>
*/
function getProcessInstanceActivityIds(processInstanceId) {
	return get("/api/lowcode/process/instance/activity-ids", { processInstanceId });
}
/**
* 部署 BPMN XML 到 Flowable 流程引擎。
*
* <p>将 bpmn-js 导出的 BPMN 2.0 XML 以 JSON 形式提交到低代码部署接口，
* 后端转换为 MultipartFile 调用 WorkflowService.deployProcess。</p>
*/
function deployBpmnXml(bpmnXml, processName) {
	return post("/api/lowcode/process/deploy", {
		xml: bpmnXml,
		name: processName
	});
}
/**
* 获取已部署流程定义的最新版本 BPMN 2.0 XML 原文。
*
* <p>用于流程预览（bpmn-js Viewer）与节点表单绑定时解析 UserTask 节点列表。</p>
*/
function getProcessDefinitionBpmnXml(processDefinitionKey) {
	return get("/api/lowcode/process/bpmn-xml", { processDefinitionKey });
}
/**
* 获取流程实例的流程图（PNG 图片二进制）。
*
* <p>复用 pms-workflow 的 DefaultProcessDiagramGenerator，返回 image/png 字节流。
* 调用方可将其转为 blob URL 在 <img> 中展示。</p>
*/
function getProcessDiagram(processInstanceId) {
	return get(`/api/workflow/diagram/${processInstanceId}`, void 0, { responseType: "blob" });
}
/**
* 查询流程实例列表。
*
* <p>支持按 processDefinitionKey 与 status 过滤。status 可选值：
* <ul>
*   <li>未传 / running：仅返回运行中实例</li>
*   <li>completed：仅返回已完成实例</li>
*   <li>all：合并返回</li>
* </ul></p>
*/
function getProcessInstances(params) {
	return get("/api/lowcode/process/instances", params);
}
/**
* 终止流程实例（级联删除运行时数据）。
*
* @param id     流程实例 ID
* @param reason 终止原因（可选）
*/
function terminateInstance(id, reason) {
	return del(`/api/lowcode/process/instances/${id}`, reason ? { reason } : void 0);
}
//#endregion
export { getProcessDiagram as a, saveProcessBinding as c, getProcessDefinitions as i, terminateInstance as l, getProcessBindings as n, getProcessInstanceActivityIds as o, getProcessDefinitionBpmnXml as r, getProcessInstances as s, deployBpmnXml as t };

//# sourceMappingURL=lowcode-process-Cl-hxrRH.js.map