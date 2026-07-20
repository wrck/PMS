import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/implementation.ts
function assignOemTask(data) {
	return post("/api/impl/task/oem/assign", data);
}
function assignAgentTask(data) {
	return post("/api/impl/task/agent/assign", data);
}
function acceptTask(id) {
	return post(`/api/impl/task/${id}/accept`);
}
function startTask(id) {
	return post(`/api/impl/task/${id}/start`);
}
function reportTaskProgress(id, data) {
	return post(`/api/impl/task/${id}/progress`, data);
}
function completeTask(id, description) {
	return post(`/api/impl/task/${id}/complete`, { description });
}
function confirmTask(id, opinion) {
	return post(`/api/impl/task/${id}/confirm`, { opinion });
}
function rejectTask(id, opinion) {
	return post(`/api/impl/task/${id}/reject`, { opinion });
}
function getTaskPage(params) {
	return get("/api/impl/task/list", params);
}
function getAgentPage(params) {
	return get("/api/impl/agent/list", params);
}
function createAgent(data) {
	return post("/api/impl/agent", data);
}
function updateAgent(data) {
	return put("/api/impl/agent", data);
}
function deleteAgent(id) {
	return del(`/api/impl/agent/${id}`);
}
function evaluateAgent(data) {
	return post("/api/impl/agent/score/evaluate", data);
}
function getScoresByAgent(agentId) {
	return get(`/api/impl/agent/score/agent/${agentId}`);
}
function getSettlementPage(params) {
	return get("/api/impl/settlement/list", params);
}
function createSettlement(data) {
	return post("/api/impl/settlement", data);
}
function approveSettlement(id, opinion) {
	return post(`/api/impl/settlement/${id}/approve`, { opinion });
}
function rejectSettlement(id, opinion) {
	return post(`/api/impl/settlement/${id}/reject`, { opinion });
}
function getSettlementDetail(id) {
	return get(`/api/impl/settlement/${id}`);
}
//#endregion
export { rejectTask as _, completeTask as a, updateAgent as b, createSettlement as c, getAgentPage as d, getScoresByAgent as f, rejectSettlement as g, getTaskPage as h, assignOemTask as i, deleteAgent as l, getSettlementPage as m, approveSettlement as n, confirmTask as o, getSettlementDetail as p, assignAgentTask as r, createAgent as s, acceptTask as t, evaluateAgent as u, reportTaskProgress as v, startTask as y };

//# sourceMappingURL=implementation-DHYgyd55.js.map