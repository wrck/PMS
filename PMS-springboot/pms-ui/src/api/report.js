import request from '@/utils/request'

export function getLineData(params) { return request.get('/api/report/line-data', { params }) }
export function getLineQualityData(params) { return request.get('/api/report/line-quality-data', { params }) }
export function getAssignedRate(params) { return request.get('/api/report/assigned-rate', { params }) }
export function getTraceRate(params) { return request.get('/api/report/trace-rate', { params }) }
export function getCloseRate(params) { return request.get('/api/report/close-rate', { params }) }
export function getImplRate(params) { return request.get('/api/report/impl-rate', { params }) }
export function getQuality(params) { return request.get('/api/report/quality', { params }) }
export function getProjectSummaryStatus(params) { return request.get('/api/report/project-summary-status', { params }) }
