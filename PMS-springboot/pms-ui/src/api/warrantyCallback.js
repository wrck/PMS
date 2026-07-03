import request from '@/utils/request'

export function listWarrantyCallbacks(params) { return request.get('/api/warranty-callback/list', { params }) }
export function getWarrantyCallback(id) { return request.get(`/api/warranty-callback/${id}`) }
export function createWarrantyCallback(data) { return request.post('/api/warranty-callback', data) }
export function updateWarrantyCallback(data) { return request.put('/api/warranty-callback', data) }
export function deleteWarrantyCallback(id) { return request.delete(`/api/warranty-callback/${id}`) }
export function getWarrantyCallbacksByProject(projectId) { return request.get(`/api/warranty-callback/project/${projectId}`) }
export function getWarrantyCallbacksByCustomer(customerName) { return request.get('/api/warranty-callback/customer', { params: { customerName } }) }
