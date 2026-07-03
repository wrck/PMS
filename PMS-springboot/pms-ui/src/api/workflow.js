import request from '@/utils/request'

export function getMyTasks(params) { return request.get('/api/workflow/tasks/todo', { params }) }
export function getTaskDetail(taskId) { return request.get(`/api/workflow/tasks/${taskId}`) }
export function getTaskForm(taskId) { return request.get(`/api/workflow/tasks/${taskId}`) }
export function submitTask(data) { return request.post(`/api/workflow/tasks/${data.taskId}/complete`, data) }
export function completeTask(taskId, processInstanceId, params) { return request.post(`/api/workflow/tasks/${taskId}/complete`, params, { params: { processInstanceId } }) }
export function claimTask(taskId) { return request.post(`/api/workflow/tasks/${taskId}/claim`) }
export function delegateTask(taskId, data) { return request.post(`/api/workflow/tasks/${taskId}/assign`, data) }
export function getTaskComments(objId, procdefKey) { return request.get('/api/workflow/comments', { params: { objId, procdefKey } }) }
export function getTaskCommentsByInstance(processInstanceId) { return request.get(`/api/workflow/comments/instance/${processInstanceId}`) }
export function getTaskProcessVariables(taskId) { return request.get(`/api/workflow/tasks/${taskId}/variables`) }
export function getProcessEnded(processInstanceId) { return request.get(`/api/workflow/instances/${processInstanceId}/ended`) }
export function deployProcess(data) { return request.post('/api/workflow/deploy', data) }
export function deleteDeployment(id) { return request.delete(`/api/workflow/deploy/${id}`) }
export function getDeployments(params) { return request.get('/api/workflow/deployments', { params }) }
export function viewImage(deploymentId) { return request.get(`/api/workflow/deploy/${deploymentId}/image`, { responseType: 'blob' }) }
export function viewCurrentImage(processInstanceId) { return request.get(`/api/workflow/instance/${processInstanceId}/image`, { responseType: 'blob' }) }
export function getHistoryTasks(params) { return request.get('/api/workflow/history', { params }) }
export function getDelegates() { return request.get('/api/workflow/delegates') }
export function addDelegate(data) { return request.post('/api/workflow/delegate', data) }
export function updateDelegate(data) { return request.put('/api/workflow/delegate', data) }
export function deleteDelegate(id) { return request.delete(`/api/workflow/delegate/${id}`) }
export function getTaskProcessImage(taskId) { return request.get(`/api/workflow/tasks/${taskId}/process-image`, { responseType: 'blob' }) }
