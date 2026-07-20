import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-microflow.ts
function getMicroflowList() {
	return get("/api/lowcode/microflow");
}
function saveMicroflow(data) {
	return post("/api/lowcode/microflow", data);
}
function executeMicroflow(code, inputs) {
	return post(`/api/lowcode/microflow/${code}/execute`, inputs);
}
/** 查询某次执行的节点级轨迹日志 */
function getExecutionLogs(executionId) {
	return get(`/api/lowcode/microflow-execution-log/${executionId}`);
}
/** 启动微流调试会话 */
function startMicroflowDebug(code, req) {
	return post(`/api/lowcode/microflow/${code}/debug/start`, req);
}
/** 单步执行（step over） */
function stepOverMicroflowDebug(sessionId) {
	return post(`/api/lowcode/microflow/debug/${sessionId}/step`);
}
/** 继续执行到下一断点 */
function continueMicroflowDebug(sessionId) {
	return post(`/api/lowcode/microflow/debug/${sessionId}/continue`);
}
/** 终止微流调试会话 */
function terminateMicroflowDebug(sessionId) {
	return del(`/api/lowcode/microflow/debug/${sessionId}`);
}
/** 查询某微流最近若干次执行日志（按时间倒序） */
function getRecentExecutionLogs(microflowId, limit = 10) {
	return get(`/api/lowcode/microflow-execution-log/recent?microflowId=${microflowId}&limit=${limit}`);
}
//#endregion
export { getRecentExecutionLogs as a, stepOverMicroflowDebug as c, getMicroflowList as i, terminateMicroflowDebug as l, executeMicroflow as n, saveMicroflow as o, getExecutionLogs as r, startMicroflowDebug as s, continueMicroflowDebug as t };

//# sourceMappingURL=lowcode-microflow-CXsjmWyP.js.map