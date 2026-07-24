import { del, get, post, put } from '@/utils/request'
import type { YudaoPageParam, YudaoPageResult } from './yudao-system'

/**
 * yudao 底座基础设施模块原生 API。
 *
 * <p>直接调用 yudao {@code /admin-api/infra/*} 接口。</p>
 */

// ===================== 参数配置 =====================

export interface ConfigRespVO {
  id: number
  category: string
  name: string
  key: string
  value: string
  type: number
  visible: boolean
  remark?: string
  createTime: string
}

export interface ConfigSaveReqVO {
  id?: number
  category: string
  name: string
  key: string
  value: string
  visible: boolean
  remark?: string
}

export interface ConfigPageReqVO extends YudaoPageParam {
  name?: string
  key?: string
  type?: number
  createTime?: string[]
}

export function getConfigPage(params: ConfigPageReqVO): Promise<YudaoPageResult<ConfigRespVO>> {
  return get<YudaoPageResult<ConfigRespVO>>('/admin-api/infra/config/page', params)
}

export function getConfig(id: number): Promise<ConfigRespVO> {
  return get<ConfigRespVO>('/admin-api/infra/config/get', { id })
}

export function getConfigValueByKey(key: string): Promise<string> {
  return get<string>('/admin-api/infra/config/get-value-by-key', { key })
}

export function createConfig(data: ConfigSaveReqVO): Promise<number> {
  return post<number>('/admin-api/infra/config/create', data)
}

export function updateConfig(data: ConfigSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/infra/config/update', data)
}

export function deleteConfig(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/config/delete', { id })
}

// ===================== Redis 监控 =====================

export interface RedisCommandStat {
  command: string
  calls: number
  usec: number
}

export interface RedisMonitorRespVO {
  info: Record<string, string>
  dbSize: number
  commandStats: RedisCommandStat[]
}

export function getRedisMonitorInfo(): Promise<RedisMonitorRespVO> {
  return get<RedisMonitorRespVO>('/admin-api/infra/redis/get-monitor-info')
}

// ===================== API 访问日志 =====================

export interface ApiAccessLogRespVO {
  id: number
  traceId: string
  userId: number
  userType: number
  applicationName: string
  requestMethod: string
  requestUrl: string
  requestParams?: string
  responseBody?: string
  userIp: string
  userAgent: string
  operateModule: string
  operateName: string
  operateType: number
  beginTime: string
  endTime: string
  duration: number
  resultCode: number
  resultMsg?: string
  createTime: string
}

export interface ApiAccessLogPageReqVO extends YudaoPageParam {
  userId?: number
  userType?: number
  applicationName?: string
  requestUrl?: string
  beginTime?: string[]
  duration?: number
  resultCode?: number
}

export function getApiAccessLogPage(params: ApiAccessLogPageReqVO): Promise<YudaoPageResult<ApiAccessLogRespVO>> {
  return get<YudaoPageResult<ApiAccessLogRespVO>>('/admin-api/infra/api-access-log/page', params)
}

export function getApiAccessLog(id: number): Promise<ApiAccessLogRespVO> {
  return get<ApiAccessLogRespVO>('/admin-api/infra/api-access-log/get', { id })
}

// ===================== API 错误日志 =====================

export interface ApiErrorLogRespVO {
  id: number
  traceId: string
  userId: number
  userType: number
  applicationName: string
  requestMethod: string
  requestUrl: string
  requestParams: string
  userIp: string
  userAgent: string
  exceptionTime: string
  exceptionName: string
  exceptionMessage: string
  exceptionRootCauseMessage: string
  exceptionStackTrace: string
  exceptionClassName: string
  exceptionFileName: string
  exceptionMethodName: string
  exceptionLineNumber: number
  processStatus: number
  processTime: string
  processUserId?: number
  createTime: string
}

export interface ApiErrorLogPageReqVO extends YudaoPageParam {
  userId?: number
  userType?: number
  applicationName?: string
  requestUrl?: string
  exceptionTime?: string[]
  processStatus?: number
}

export function getApiErrorLogPage(params: ApiErrorLogPageReqVO): Promise<YudaoPageResult<ApiErrorLogRespVO>> {
  return get<YudaoPageResult<ApiErrorLogRespVO>>('/admin-api/infra/api-error-log/page', params)
}

export function getApiErrorLog(id: number): Promise<ApiErrorLogRespVO> {
  return get<ApiErrorLogRespVO>('/admin-api/infra/api-error-log/get', { id })
}

export function updateApiErrorLogStatus(id: number, processStatus: number): Promise<boolean> {
  return put<boolean>('/admin-api/infra/api-error-log/update-status', { id, processStatus })
}

// ===================== 定时任务 =====================

export interface JobRespVO {
  id: number
  name: string
  status: number
  handlerName: string
  handlerParam?: string
  cronExpression: string
  retryCount: number
  retryInterval: number
  monitorTimeout?: number
  createTime: string
}

export interface JobSaveReqVO {
  id?: number
  name: string
  handlerName: string
  handlerParam?: string
  cronExpression: string
  retryCount: number
  retryInterval: number
  monitorTimeout?: number
}

export interface JobPageReqVO extends YudaoPageParam {
  name?: string
  status?: number
  handlerName?: string
}

export function getJobPage(params: JobPageReqVO): Promise<YudaoPageResult<JobRespVO>> {
  return get<YudaoPageResult<JobRespVO>>('/admin-api/infra/job/page', params)
}

export function getJob(id: number): Promise<JobRespVO> {
  return get<JobRespVO>('/admin-api/infra/job/get', { id })
}

export function createJob(data: JobSaveReqVO): Promise<number> {
  return post<number>('/admin-api/infra/job/create', data)
}

export function updateJob(data: JobSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/infra/job/update', data)
}

export function updateJobStatus(id: number, status: number): Promise<boolean> {
  return put<boolean>('/admin-api/infra/job/update-status', { id, status })
}

export function deleteJob(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/job/delete', { id })
}

export function triggerJob(id: number): Promise<boolean> {
  return put<boolean>('/admin-api/infra/job/trigger', { id })
}

export function getJobNextTimes(id: number): Promise<string[]> {
  return get<string[]>('/admin-api/infra/job/get_next_times', { id })
}

// ===================== 代码生成 =====================

export interface CodegenTableRespVO {
  id: number
  tableId: number
  isParentMenuIdValid: boolean
  dataSourceConfigId: number
  scene: number
  tableName: string
  tableComment: string
  remark: string
  moduleName: string
  businessName: string
  className: string
  classComment: string
  author: string
  createTime: string
  updateTime: string
  templateType: number
  parentMenuId: number
}

export interface CodegenTableSaveReqVO extends CodegenTableRespVO {
  frontType?: number | null
  genPath?: string
  genType?: string
  masterTableId?: number
  subJoinColumnId?: number
  subJoinMany?: boolean
  treeParentColumnId?: number
  treeNameColumnId?: number
}

export interface CodegenColumnRespVO {
  id: number
  tableId: number
  columnName: string
  dataType: string
  columnComment: string
  nullable: number
  primaryKey: number
  ordinalPosition: number
  javaType: string
  javaField: string
  dictType: string
  example: string
  createOperation: number
  updateOperation: number
  listOperation: number
  listOperationCondition: string
  listOperationResult: number
  htmlType: string
}

export interface DatabaseTableVO {
  name: string
  comment: string
}

export interface CodegenPreviewVO {
  filePath: string
  code: string
}

export interface CodegenUpdateReqVO {
  table: CodegenTableSaveReqVO
  columns: CodegenColumnRespVO[]
}

export interface CodegenTablePageReqVO extends YudaoPageParam {
  tableName?: string
  tableComment?: string
  createTime?: string[]
}

export function getCodegenTableList(dataSourceConfigId: number): Promise<CodegenTableRespVO[]> {
  return get<CodegenTableRespVO[]>('/admin-api/infra/codegen/table/list', { dataSourceConfigId })
}

export function getCodegenTablePage(
  params: CodegenTablePageReqVO
): Promise<YudaoPageResult<CodegenTableRespVO>> {
  return get<YudaoPageResult<CodegenTableRespVO>>('/admin-api/infra/codegen/table/page', params)
}

export function getCodegenTable(id: number): Promise<CodegenUpdateReqVO> {
  return get<CodegenUpdateReqVO>('/admin-api/infra/codegen/detail', { tableId: id })
}

export function updateCodegenTable(data: CodegenUpdateReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/infra/codegen/update', data)
}

export function syncCodegenFromDB(id: number): Promise<boolean> {
  return put<boolean>('/admin-api/infra/codegen/sync-from-db', { tableId: id })
}

export function previewCodegen(id: number): Promise<CodegenPreviewVO[]> {
  return get<CodegenPreviewVO[]>('/admin-api/infra/codegen/preview', { tableId: id })
}

export function downloadCodegen(id: number): Promise<Blob> {
  return get<Blob>('/admin-api/infra/codegen/download', { tableId: id }, { responseType: 'blob' })
}

export function getSchemaTableList(params: {
  dataSourceConfigId: number
  name?: string
}): Promise<DatabaseTableVO[]> {
  return get<DatabaseTableVO[]>('/admin-api/infra/codegen/db/table/list', params)
}

export function createCodegenList(data: {
  dataSourceConfigId: number
  tableNames: string[]
}): Promise<number[]> {
  return post<number[]>('/admin-api/infra/codegen/create-list', data)
}

export function deleteCodegenTable(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/codegen/delete', { tableId: id })
}

export function deleteCodegenTableList(ids: number[]): Promise<boolean> {
  return del<boolean>('/admin-api/infra/codegen/delete-list', { tableIds: ids.join(',') })
}

// ===================== 数据源配置 =====================

export interface DataSourceConfigRespVO {
  id?: number
  name: string
  url: string
  username: string
  password: string
  createTime?: string
}

export function createDataSourceConfig(data: DataSourceConfigRespVO): Promise<number> {
  return post<number>('/admin-api/infra/data-source-config/create', data)
}

export function updateDataSourceConfig(data: DataSourceConfigRespVO): Promise<boolean> {
  return put<boolean>('/admin-api/infra/data-source-config/update', data)
}

export function deleteDataSourceConfig(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/data-source-config/delete', { id })
}

export function getDataSourceConfig(id: number): Promise<DataSourceConfigRespVO> {
  return get<DataSourceConfigRespVO>('/admin-api/infra/data-source-config/get', { id })
}

export function getDataSourceConfigList(): Promise<DataSourceConfigRespVO[]> {
  return get<DataSourceConfigRespVO[]>('/admin-api/infra/data-source-config/list')
}

// ===================== 文件管理 =====================

export interface FileRespVO {
  id: number
  configId: number
  name: string
  path: string
  url: string
  type: string
  size: number
  createTime: string
}

export interface FilePageReqVO extends YudaoPageParam {
  name?: string
  type?: string
  createTime?: string[]
}

export interface FilePresignedUrlRespVO {
  configId: number
  uploadUrl: string
  url: string
  path: string
}

export function getFilePage(params: FilePageReqVO): Promise<YudaoPageResult<FileRespVO>> {
  return get<YudaoPageResult<FileRespVO>>('/admin-api/infra/file/page', params)
}

export function deleteFile(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/file/delete', { id })
}

export function getFilePresignedUrl(
  name: string,
  directory?: string
): Promise<FilePresignedUrlRespVO> {
  return get<FilePresignedUrlRespVO>('/admin-api/infra/file/presigned-url', { name, directory })
}

export function createFile(data: {
  configId: number
  name: string
  path: string
  url: string
  type?: string
  size?: number
}): Promise<number> {
  return post<number>('/admin-api/infra/file/create', data)
}

export function uploadFile(
  data: FormData,
  onUploadProgress?: (e: { loaded: number; total?: number }) => void
): Promise<string> {
  return post<string>(
    '/admin-api/infra/file/upload',
    data,
    onUploadProgress ? { onUploadProgress } : undefined
  )
}

// ===================== 文件配置 =====================

export interface FileClientConfig {
  basePath: string
  host?: string
  port?: number
  username?: string
  password?: string
  mode?: string
  endpoint?: string
  bucket?: string
  accessKey?: string
  accessSecret?: string
  enablePathStyleAccess?: boolean
  enablePublicAccess?: boolean
  region?: string
  domain: string
}

export interface FileConfigRespVO {
  id: number
  name: string
  storage?: number
  master: boolean
  visible: boolean
  config: FileClientConfig
  remark: string
  createTime: string
}

export interface FileConfigSaveReqVO {
  id?: number
  name: string
  storage: number
  master?: boolean
  visible: boolean
  config: FileClientConfig
  remark?: string
}

export interface FileConfigPageReqVO extends YudaoPageParam {
  name?: string
  storage?: number
  createTime?: string[]
}

export function getFileConfigPage(
  params: FileConfigPageReqVO
): Promise<YudaoPageResult<FileConfigRespVO>> {
  return get<YudaoPageResult<FileConfigRespVO>>('/admin-api/infra/file-config/page', params)
}

export function getFileConfig(id: number): Promise<FileConfigRespVO> {
  return get<FileConfigRespVO>('/admin-api/infra/file-config/get', { id })
}

export function updateFileConfigMaster(id: number): Promise<boolean> {
  return put<boolean>('/admin-api/infra/file-config/update-master', { id })
}

export function createFileConfig(data: FileConfigSaveReqVO): Promise<number> {
  return post<number>('/admin-api/infra/file-config/create', data)
}

export function updateFileConfig(data: FileConfigSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/infra/file-config/update', data)
}

export function deleteFileConfig(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/infra/file-config/delete', { id })
}

export function testFileConfig(id: number): Promise<string> {
  return get<string>('/admin-api/infra/file-config/test', { id })
}

// ===================== 任务日志 =====================

export interface JobLogRespVO {
  id: number
  jobId: number
  handlerName: string
  handlerParam: string
  cronExpression: string
  executeIndex: string
  beginTime: string
  endTime: string
  duration: string
  status: number
  createTime: string
  result: string
}

export interface JobLogPageReqVO extends YudaoPageParam {
  jobId?: number
  handlerName?: string
  beginTime?: string[]
  status?: number
}

export function getJobLogPage(params: JobLogPageReqVO): Promise<YudaoPageResult<JobLogRespVO>> {
  return get<YudaoPageResult<JobLogRespVO>>('/admin-api/infra/job-log/page', params)
}

export function getJobLog(id: number): Promise<JobLogRespVO> {
  return get<JobLogRespVO>('/admin-api/infra/job-log/get', { id })
}

export function exportJobLog(params: JobLogPageReqVO): Promise<Blob> {
  return get<Blob>('/admin-api/infra/job-log/export-excel', params, { responseType: 'blob' })
}

// ===================== 构建信息 =====================

export interface BuildInfoRespVO {
  timestamp: string
  artifact: string
  name: string
  version: string
}

export function getBuildInfo(): Promise<BuildInfoRespVO> {
  return get<BuildInfoRespVO>('/admin-api/infra/build/info')
}

// ===================== 服务器监控 =====================

export interface ServerCpuVO {
  cpuNum: number
  total: number
  sys: number
  used: number
  wait: number
  free: number
}

export interface ServerMemVO {
  total: number
  used: number
  free: number
  usage: number
}

export interface ServerJvmVO {
  total: number
  max: number
  free: number
  version: string
  home: string
  name: string
  startTime: string
  usage: number
  runTime: string
  inputArgs: string[]
}

export interface ServerDiskStatVO {
  dirName: string
  sysTypeName: string
  typeName: string
  total: string
  free: string
  used: string
  usage: number
}

export interface ServerRespVO {
  cpu: ServerCpuVO
  mem: ServerMemVO
  jvm: ServerJvmVO
  sys: Record<string, string>
  sysFiles: ServerDiskStatVO[]
}

export function getServerInfo(): Promise<ServerRespVO> {
  return get<ServerRespVO>('/admin-api/infra/server')
}
