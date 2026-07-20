import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/project.ts
function createProject(data) {
	return post("/api/project", data);
}
function getProject(id) {
	return get(`/api/project/${id}`);
}
function listProjects(params) {
	return get("/api/project/list", params);
}
function updateProject(data) {
	return put("/api/project", data);
}
function deleteProject(id) {
	return del(`/api/project/${id}`);
}
function approveProject(id) {
	return post(`/api/project/${id}/approve`);
}
function getDashboard() {
	return get("/api/project/dashboard");
}
function createMilestone(data) {
	return post("/api/project/milestone", data);
}
function updateMilestone(data) {
	return put("/api/project/milestone", data);
}
function deleteMilestone(id) {
	return del(`/api/project/milestone/${id}`);
}
function listMilestones(projectId) {
	return get(`/api/project/milestone/project/${projectId}`);
}
function updateMilestoneProgress(id, data) {
	return post(`/api/project/milestone/${id}/progress`, data);
}
function applyAcceptance(data) {
	return post("/api/project/acceptance/apply", data);
}
function approveAcceptance(id, data) {
	return post(`/api/project/acceptance/${id}/approve`, data);
}
function rejectAcceptance(id, data) {
	return post(`/api/project/acceptance/${id}/reject`, data);
}
function getAcceptanceByProject(projectId) {
	return get(`/api/project/acceptance/${projectId}`);
}
//#endregion
export { createProject as a, getAcceptanceByProject as c, listMilestones as d, listProjects as f, updateProject as g, updateMilestoneProgress as h, createMilestone as i, getDashboard as l, updateMilestone as m, approveAcceptance as n, deleteMilestone as o, rejectAcceptance as p, approveProject as r, deleteProject as s, applyAcceptance as t, getProject as u };

//# sourceMappingURL=project-Brd7mmQb.js.map