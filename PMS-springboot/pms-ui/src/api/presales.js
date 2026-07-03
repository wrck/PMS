import request from '@/utils/request'

export function listPresales(params) { return request.get('/api/presales/list', { params }) }
export function getPresales(id) { return request.get(`/api/presales/${id}`) }
export function applyPresales(data) { return request.post('/api/presales/apply', data) }
export function savePresalesDraft(data) { return request.post('/api/presales/draft', data) }
export function startPresalesFlow(id) { return request.post(`/api/presales/${id}/start-flow`) }
export function reApplyPresales(id, data) { return request.post(`/api/presales/${id}/re-apply`, data) }
export function auditPresales(id, comment, approved) { return request.post(`/api/presales/${id}/approve`, null, { params: { comment, approved } }) }
export function terminatePresales(id, closeRemark) { return request.post(`/api/presales/${id}/terminate`, null, { params: { closeRemark } }) }
export function exportPresales(params) { return request.get('/api/presales/export', { params, responseType: 'blob' }) }
export function syncOaData() { return request.post('/api/presales/sync-oa') }
export function getPresalesProducts(id) { return request.get(`/api/presales/${id}/products`) }
export function getPresalesTasks(id) { return request.get(`/api/presales/${id}/tasks`) }
export function getPresalesComments(id) { return request.get(`/api/presales/${id}/comments`) }
export function getPresalesDelivers(id) { return request.get(`/api/presales/${id}/deliver`) }
export function getPresalesShipment(presalesCode, containRma) { return request.get('/api/presales/shipment-info', { params: { presalesCode, containRma: containRma || false } }) }
export function getPresalesLend2Sale(presalesCode) { return request.get('/api/presales/lend2sale-info', { params: { presalesCode } }) }
export function getPresalesLend2Rma(presalesCode) { return request.get('/api/presales/lend2rma-info', { params: { presalesCode } }) }
export function getPresalesTempAuth(presalesId) { return request.get('/api/presales/temp-auth-info', { params: { presalesId } }) }
export function getLend2Sale(presalesCode) { return request.get('/api/presales/lend2sale-info', { params: { presalesCode } }) }
export function getLend2Rma(presalesCode) { return request.get('/api/presales/lend2rma-info', { params: { presalesCode } }) }
export function getTempAuth(presalesId) { return request.get('/api/presales/temp-auth-info', { params: { presalesId } }) }
