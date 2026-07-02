import request from '@/utils/request'

export function listClosedLoops(params) { return request.get('/api/closed-loop/list', { params }) }
export function getClosedLoop(id) { return request.get(`/api/closed-loop/${id}`) }
export function addPmCLApply(data) { return request.post('/api/closed-loop/pm-apply', data) }
export function addSmCLApply(data) { return request.post('/api/closed-loop/sm-apply', data) }
export function addCbCLApply(data) { return request.post('/api/closed-loop/cb-apply', data) }
export function cantCB(data) { return request.post('/api/closed-loop/cant-cb', data) }
export function addClCLApply(data) { return request.post('/api/closed-loop/cl-apply', data) }
export function getHisProcess(id) { return request.get(`/api/closed-loop/${id}/history`) }
export function getUserPower(id) { return request.get(`/api/closed-loop/${id}/user-power`) }
