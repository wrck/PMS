import request from '@/utils/request'

export function getMyTasks(params) { return request.get('/api/workflow/my-tasks', { params }) }
export function getTaskForm(taskId) { return request.get(`/api/workflow/task/${taskId}/form`) }
export function submitTask(data) { return request.post('/api/workflow/task/submit', data) }
export function deployProcess(data) { return request.post('/api/workflow/deploy', data) }
export function deleteDeployment(id) { return request.delete(`/api/workflow/deploy/${id}`) }
export function getDeployments(params) { return request.get('/api/workflow/deployments', { params }) }
export function viewImage(deploymentId) { return request.get(`/api/workflow/deploy/${deploymentId}/image`, { responseType: 'blob' }) }
export function viewCurrentImage(processInstanceId) { return request.get(`/api/workflow/instance/${processInstanceId}/image`, { responseType: 'blob' }) }
export function getHistoryTasks(params) { return request.get('/api/workflow/history', { params }) }
export function getDelegates() { return request.get('/api/workflow/delegates') }
export function addDelegate(data) { return request.post('/api/workflow/delegate', data) }
export function updateDelegate(data) { return request.put('/api/workflow/delegate', data) }
