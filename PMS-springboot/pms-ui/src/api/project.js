import request from '@/utils/request'

// ===== 项目基础 =====
export function listProjects(params) { return request.get('/api/project/list', { params }) }
export function getProject(id) { return request.get(`/api/project/${id}`) }
export function addProject(data) { return request.post('/api/project', data) }
export function updateProject(data) { return request.put('/api/project', data) }
export function deleteProject(id) { return request.delete(`/api/project/${id}`) }
export function backToLastStep(data) { return request.post('/api/project/back-to-last-step', data) }
export function batchImport(formData) { return request.post('/api/project/batch-import', formData) }

// ===== 项目成员 =====
export function getProjectMembers(id) { return request.get(`/api/project/${id}/members`) }
export function addProjectMember(data) { return request.post('/api/project/member', data) }
export function createProjectMember(data) { return request.post('/api/project/member', data) }
export function updateProjectMember(data) { return request.put('/api/project/member', data) }
export function deleteProjectMember(id) { return request.delete(`/api/project/member/${id}`) }
export function apiDeleteMember(id) { return request.delete(`/api/project/member/${id}`) }

// ===== 工程计划 =====
export function getProjectPlan(id) { return request.get(`/api/project/plan/list`, { params: { projectId: id } }) }
export function getProjectPlans(id) { return request.get(`/api/project/plan/list`, { params: { projectId: id } }) }
export function createProjectPlan(data) { return request.post('/api/project/plan', data) }
export function updateProjectPlan(data) { return request.put('/api/project/plan', data) }
export function editPlan(data) { return request.put('/api/project/plan', data) }
export function apiDeletePlan(id) { return request.delete(`/api/project/plan/${id}`) }

// ===== 交付件 =====
export function getProjectFiles(id) { return request.get(`/api/project/${id}/files`) }
export function getProjectDeliverables(id) { return request.get(`/api/project/deliver/list`, { params: { projectId: id } }) }
export function uploadFile(data) { return request.post('/api/file/upload', data) }
export function deleteFile(id) { return request.delete(`/api/file/${id}`) }
export function downloadFile(id) { return request.get(`/api/file/${id}/download`, { responseType: 'blob' }) }
export function apiDeleteDeliverable(id) { return request.delete(`/api/project/deliver/${id}`) }

// ===== 订单 & 发货 =====
export function getOrderData(id) { return request.get(`/api/project/${id}/order-data`) }
export function getProjectOrders(id) { return request.get(`/api/project/${id}/order-data`) }
export function getProjectRealOrders(id) { return request.get(`/api/project/${id}/real-order-data`) }
export function getShipmentInfo(id) { return request.get(`/api/project/${id}/shipment-info`) }
export function getProjectShipments(id) { return request.get(`/api/project/${id}/shipment-info`) }
export function apiDeleteShipment(id) { return request.delete(`/api/project/${id}/shipment-info`) }
export function apiSaveInstallAddress(id, address) { return request.post(`/api/project/${id}/install-address`, { address }) }
export function transferShipment(data) { return request.post('/api/project/transfer/shipment', data) }
export function apiTransferShipment(data) { return request.post('/api/project/transfer/shipment', data) }
export function apiQueryTransferProjectList(keyword) { return request.get('/api/project/transfer/list', { params: { keyword } }) }

// ===== 现场验货 =====
export function exportSpotCheck(projectId) { return request.get(`/api/project/${projectId}/export-spot-check`, { responseType: 'blob' }) }
export function apiExportSpotCheck(projectId) { return request.get(`/api/project/${projectId}/export-spot-check`, { responseType: 'blob' }) }
export function apiExportOverWarranty(projectId) { return request.get(`/api/project/${projectId}/export-over-warranty-remind`, { responseType: 'blob' }) }

// ===== 软件版本 =====
export function getSoftVersion(id) { return request.get(`/api/project/${id}/soft-version`) }
export function getProjectSoftVersions(id) { return request.get(`/api/project/${id}/soft-version`) }
export function updateSoftVersion(data) { return request.put('/api/project/soft-version', data) }
export function apiUpdateSoftVersion(projectId, versions) { return request.put('/api/project/soft-version', { projectId, versions }) }
export function apiGetSoftVersionHistory(id) { return request.get(`/api/project/soft-version/history/${id}`) }

// ===== 租赁 & 配置 =====
export function getProjectLeaseLines(id) { return request.get(`/api/project/${id}/lease-line`) }
export function getProjectConfigLevels(id) { return request.get(`/api/project/${id}/config-level-info`) }

// ===== 周报 =====
export function getProjectWeekly(id) { return request.get('/api/project/weekly/list', { params: { projectId: id } }) }
export function getProjectWeeklys(id) { return request.get('/api/project/weekly/list', { params: { projectId: id } }) }
export function createWeekly(data) { return request.post('/api/project/weekly', data) }
export function apiCreateWeekly(data) { return request.post('/api/project/weekly', data) }
export function submitWeekly(id) { return request.post(`/api/project/weekly/${id}/submit`) }
export function apiSubmitWeekly(id) { return request.post(`/api/project/weekly/${id}/submit`) }
export function apiFeedbackWeekly(id, content) { return request.post('/api/project/weekly/feedback', { weeklyId: id, content }) }

// ===== 通知批示 =====
export function getProjectNotifications(id) { return request.get('/api/notification/list', { params: { projectId: id } }) }
export function apiCreateInstruction(data) { return request.post(`/api/project/${data.projectId}/instruction`, data) }

// ===== 闭环 & 回访（项目维度）=====
export function getProjectClosedLoop(id) { return request.get(`/api/closed-loop/project/${id}`) }
export function getProjectCallback(id) { return request.get('/api/callback/list', { params: { projectId: id } }) }

// ===== 转包 & 维保 & 督查（项目维度）=====
export function getProjectSubcontracts(id) { return request.get('/api/subcontract/list', { params: { projectId: id } }) }
export function getProjectMaintenances(id) { return request.get('/api/maintenance/list', { params: { projectId: id } }) }
export function getProjectSupervisions(id) { return request.get('/api/supervision/list', { params: { projectId: id } }) }

// ===== 问题单 & License =====
export function getProjectProblemTickets(id) { return request.get(`/api/project/${id}/problem-tickets`) }
export function getProjectLicenseInfos(id) { return request.get(`/api/project/${id}/license-infos`) }
