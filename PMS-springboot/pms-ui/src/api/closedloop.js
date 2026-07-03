import request from '@/utils/request'

export function listClosedLoops(params) { return request.get('/api/closed-loop/list', { params }) }
export function getClosedLoop(id) { return request.get(`/api/closed-loop/${id}`) }
export function applyClosedLoop(data) { return request.post('/api/closed-loop', data) }
export function approveClosedLoop(id, comment, approved, role) { return request.post(`/api/closed-loop/${id}/approve`, null, { params: { comment, approved, role } }) }
export function addPmCLApply(data) { return request.post('/api/closed-loop/pm-apply', data) }
export function addSmCLApply(data) { return request.post('/api/closed-loop/sm-apply', data) }
export function addCbCLApply(data) { return request.post('/api/closed-loop/cb-apply', data) }
export function cantClose(id, reason) { return request.post(`/api/closed-loop/${id}/cant-close`, null, { params: { reason } }) }
export function cantCB(id, reason) { return request.post(`/api/closed-loop/${id}/cant-close`, null, { params: { reason } }) }
export function addClCLApply(data) { return request.post('/api/closed-loop/cl-apply', data) }
export function getClosedLoopsByProject(projectId) { return request.get(`/api/closed-loop/project/${projectId}`) }
export function getRunningClosedLoopsByProject(projectId) { return request.get(`/api/closed-loop/project/${projectId}/running`) }
export function getClosedLoopHistory(id) { return request.get(`/api/closed-loop/${id}/history`) }
export function getHisProcess(id) { return request.get(`/api/closed-loop/${id}/history`) }
export function getUserPower(id) { return request.get(`/api/closed-loop/${id}/user-power`) }
export function getClosedLoopQuestionnaire(id) { return request.get(`/api/closed-loop/${id}/questionnaire`) }
