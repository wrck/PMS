import { del, get, post, put } from '@/utils/request'

/**
 * yudao 底座系统模块原生 API。
 *
 * <p>直接调用 yudao {@code /admin-api/system/*} 接口，
 * 分页使用 yudao 约定的 {@code pageNo/pageSize} + {@code list/total} 结构。</p>
 */

// ===================== 通用分页 =====================

/** yudao 分页请求参数（pageNo 从 1 开始） */
export interface YudaoPageParam {
  pageNo: number
  pageSize: number
}

/** yudao 分页结果 */
export interface YudaoPageResult<T> {
  list: T[]
  total: number
}

// ===================== 部门管理 =====================

export interface DeptRespVO {
  id: number
  name: string
  parentId: number
  sort: number
  leaderUserId?: number
  phone?: string
  email?: string
  status: number
  createTime: string
}

export interface DeptSaveReqVO {
  id?: number
  name: string
  parentId: number
  sort: number
  leaderUserId?: number
  phone?: string
  email?: string
  status: number
}

export interface DeptListReqVO {
  name?: string
  status?: number
}

export interface DeptSimpleRespVO {
  id: number
  name: string
  parentId: number
}

export function getDeptList(params?: DeptListReqVO): Promise<DeptRespVO[]> {
  return get<DeptRespVO[]>('/admin-api/system/dept/list', params)
}

export function getSimpleDeptList(): Promise<DeptSimpleRespVO[]> {
  return get<DeptSimpleRespVO[]>('/admin-api/system/dept/list-all-simple')
}

export function getDept(id: number): Promise<DeptRespVO> {
  return get<DeptRespVO>('/admin-api/system/dept/get', { id })
}

export function createDept(data: DeptSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/dept/create', data)
}

export function updateDept(data: DeptSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/dept/update', data)
}

export function deleteDept(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/dept/delete', { id })
}

// ===================== 岗位管理 =====================

export interface PostRespVO {
  id: number
  name: string
  code: string
  sort: number
  status: number
  remark?: string
  createTime: string
}

export interface PostSaveReqVO {
  id?: number
  name: string
  code: string
  sort: number
  status: number
  remark?: string
}

export interface PostPageReqVO extends YudaoPageParam {
  code?: string
  name?: string
  status?: number
}

export interface PostSimpleRespVO {
  id: number
  name: string
}

export function getPostPage(params: PostPageReqVO): Promise<YudaoPageResult<PostRespVO>> {
  return get<YudaoPageResult<PostRespVO>>('/admin-api/system/post/page', params)
}

export function getSimplePostList(): Promise<PostSimpleRespVO[]> {
  return get<PostSimpleRespVO[]>('/admin-api/system/post/list-all-simple')
}

export function getPost(id: number): Promise<PostRespVO> {
  return get<PostRespVO>('/admin-api/system/post/get', { id })
}

export function createPost(data: PostSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/post/create', data)
}

export function updatePost(data: PostSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/post/update', data)
}

export function deletePost(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/post/delete', { id })
}

// ===================== 通知公告 =====================

export interface NoticeRespVO {
  id: number
  title: string
  type: number
  content: string
  status: number
  createTime: string
}

export interface NoticeSaveReqVO {
  id?: number
  title: string
  type: number
  content: string
  status: number
}

export interface NoticePageReqVO extends YudaoPageParam {
  title?: string
  status?: number
}

export function getNoticePage(params: NoticePageReqVO): Promise<YudaoPageResult<NoticeRespVO>> {
  return get<YudaoPageResult<NoticeRespVO>>('/admin-api/system/notice/page', params)
}

export function getNotice(id: number): Promise<NoticeRespVO> {
  return get<NoticeRespVO>('/admin-api/system/notice/get', { id })
}

export function createNotice(data: NoticeSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/notice/create', data)
}

export function updateNotice(data: NoticeSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/notice/update', data)
}

export function deleteNotice(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/notice/delete', { id })
}

// ===================== 登录日志 =====================

export interface LoginLogRespVO {
  id: number
  logType: number
  userId?: number
  userType: number
  traceId?: string
  username: string
  result: number
  userIp: string
  userAgent?: string
  createTime: string
}

export interface LoginLogPageReqVO extends YudaoPageParam {
  userIp?: string
  username?: string
  status?: boolean
  createTime?: string[]
}

export function getLoginLogPage(params: LoginLogPageReqVO): Promise<YudaoPageResult<LoginLogRespVO>> {
  return get<YudaoPageResult<LoginLogRespVO>>('/admin-api/system/login-log/page', params)
}

export function getLoginLog(id: number): Promise<LoginLogRespVO> {
  return get<LoginLogRespVO>('/admin-api/system/login-log/get', { id })
}

// ===================== 操作日志 =====================

export interface OperateLogRespVO {
  id: number
  traceId: string
  userId: number
  userName: string
  userType: number
  type: string
  subType: string
  bizId: number
  action?: string
  extra?: string
  requestMethod: string
  requestUrl: string
  userIp: string
  userAgent: string
  createTime: string
}

export interface OperateLogPageReqVO extends YudaoPageParam {
  userId?: number
  bizId?: number
  type?: string
  subType?: string
  action?: string
  createTime?: string[]
}

export function getOperateLogPage(params: OperateLogPageReqVO): Promise<YudaoPageResult<OperateLogRespVO>> {
  return get<YudaoPageResult<OperateLogRespVO>>('/admin-api/system/operate-log/page', params)
}

export function getOperateLog(id: number): Promise<OperateLogRespVO> {
  return get<OperateLogRespVO>('/admin-api/system/operate-log/get', { id })
}

// ===================== OAuth2 令牌 =====================

export interface OAuth2AccessTokenRespVO {
  id: number
  accessToken: string
  refreshToken: string
  userId: number
  userType: number
  clientId: string
  createTime: string
  expiresTime: string
}

export interface OAuth2AccessTokenPageReqVO extends YudaoPageParam {
  userId?: number
  userType?: number
  clientId?: string
}

export function getOAuth2TokenPage(params: OAuth2AccessTokenPageReqVO): Promise<YudaoPageResult<OAuth2AccessTokenRespVO>> {
  return get<YudaoPageResult<OAuth2AccessTokenRespVO>>('/admin-api/system/oauth2-token/page', params)
}

export function deleteOAuth2Token(accessToken: string): Promise<boolean> {
  return del<boolean>('/admin-api/system/oauth2-token/delete', { accessToken })
}

// ===================== 租户管理 =====================

export interface TenantRespVO {
  id: number
  name: string
  contactName: string
  contactMobile: string
  status: number
  domain: string
  packageId: number
  username: string
  password: string
  expireTime: string
  accountCount: number
  websites: string[]
  createTime: string
}

export interface TenantSaveReqVO {
  id?: number
  name: string
  contactName: string
  contactMobile: string
  status: number
  domain: string
  packageId: number
  username: string
  password: string
  expireTime: string
  accountCount: number
  websites: string[]
}

export interface TenantPageReqVO extends YudaoPageParam {
  name?: string
  contactName?: string
  contactMobile?: string
  status?: number
  createTime?: string[]
}

export interface TenantSimpleRespVO {
  id: number
  name: string
}

export function getTenantPage(params: TenantPageReqVO): Promise<YudaoPageResult<TenantRespVO>> {
  return get<YudaoPageResult<TenantRespVO>>('/admin-api/system/tenant/page', params)
}

export function getTenant(id: number): Promise<TenantRespVO> {
  return get<TenantRespVO>('/admin-api/system/tenant/get', { id })
}

export function getTenantList(): Promise<TenantSimpleRespVO[]> {
  return get<TenantSimpleRespVO[]>('/admin-api/system/tenant/simple-list')
}

export function createTenant(data: TenantSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/tenant/create', data)
}

export function updateTenant(data: TenantSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/tenant/update', data)
}

export function deleteTenant(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/tenant/delete', { id })
}

// ===================== 租户套餐 =====================

export interface TenantPackageRespVO {
  id: number
  name: string
  status: number
  remark: string
  menuIds: number[]
  createTime: string
  updateTime: string
}

export interface TenantPackageSaveReqVO {
  id?: number
  name: string
  status: number
  remark: string
  menuIds: number[]
}

export interface TenantPackagePageReqVO extends YudaoPageParam {
  name?: string
  status?: number
  createTime?: string[]
}

export interface TenantPackageSimpleRespVO {
  id: number
  name: string
}

export function getTenantPackagePage(
  params: TenantPackagePageReqVO
): Promise<YudaoPageResult<TenantPackageRespVO>> {
  return get<YudaoPageResult<TenantPackageRespVO>>('/admin-api/system/tenant-package/page', params)
}

export function getTenantPackage(id: number): Promise<TenantPackageRespVO> {
  return get<TenantPackageRespVO>('/admin-api/system/tenant-package/get', { id })
}

export function getTenantPackageList(): Promise<TenantPackageSimpleRespVO[]> {
  return get<TenantPackageSimpleRespVO[]>('/admin-api/system/tenant-package/simple-list')
}

export function createTenantPackage(data: TenantPackageSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/tenant-package/create', data)
}

export function updateTenantPackage(data: TenantPackageSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/tenant-package/update', data)
}

export function deleteTenantPackage(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/tenant-package/delete', { id })
}

// ===================== 字典数据 =====================

export interface DictDataRespVO {
  id: number
  sort: number
  label: string
  value: string
  dictType: string
  status: number
  colorType: string
  cssClass: string
  remark: string
  createTime: string
}

export interface DictDataSaveReqVO {
  id?: number
  sort: number
  label: string
  value: string
  dictType: string
  status: number
  colorType: string
  cssClass: string
  remark: string
}

export interface DictDataPageReqVO extends YudaoPageParam {
  dictType?: string
  label?: string
  status?: number
}

export interface DictDataSimpleRespVO {
  dictType: string
  value: string
  label: string
  colorType: string
  cssClass: string
}

export function getSimpleDictDataList(): Promise<DictDataSimpleRespVO[]> {
  return get<DictDataSimpleRespVO[]>('/admin-api/system/dict-data/simple-list')
}

export function getDictDataPage(params: DictDataPageReqVO): Promise<YudaoPageResult<DictDataRespVO>> {
  return get<YudaoPageResult<DictDataRespVO>>('/admin-api/system/dict-data/page', params)
}

export function getDictData(id: number): Promise<DictDataRespVO> {
  return get<DictDataRespVO>('/admin-api/system/dict-data/get', { id })
}

export function getDictDataByType(dictType: string): Promise<DictDataRespVO[]> {
  return get<DictDataRespVO[]>('/admin-api/system/dict-data/type', { dictType })
}

export function createDictData(data: DictDataSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/dict-data/create', data)
}

export function updateDictData(data: DictDataSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/dict-data/update', data)
}

export function deleteDictData(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/dict-data/delete', { id })
}

// ===================== 字典类型 =====================

export interface DictTypeRespVO {
  id: number
  name: string
  type: string
  status: number
  remark: string
  createTime: string
}

export interface DictTypeSaveReqVO {
  id?: number
  name: string
  type: string
  status: number
  remark: string
}

export interface DictTypePageReqVO extends YudaoPageParam {
  name?: string
  type?: string
  status?: number
  createTime?: string[]
}

export interface DictTypeSimpleRespVO {
  id: number
  name: string
  type: string
}

export function getSimpleDictTypeList(): Promise<DictTypeSimpleRespVO[]> {
  return get<DictTypeSimpleRespVO[]>('/admin-api/system/dict-type/simple-list')
}

export function getDictTypePage(params: DictTypePageReqVO): Promise<YudaoPageResult<DictTypeRespVO>> {
  return get<YudaoPageResult<DictTypeRespVO>>('/admin-api/system/dict-type/page', params)
}

export function getDictType(id: number): Promise<DictTypeRespVO> {
  return get<DictTypeRespVO>('/admin-api/system/dict-type/get', { id })
}

export function createDictType(data: DictTypeSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/dict-type/create', data)
}

export function updateDictType(data: DictTypeSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/dict-type/update', data)
}

export function deleteDictType(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/dict-type/delete', { id })
}

// ===================== OAuth2 客户端 =====================

export interface OAuth2ClientRespVO {
  id: number
  clientId: string
  secret: string
  name: string
  logo: string
  description: string
  status: number
  accessTokenValiditySeconds: number
  refreshTokenValiditySeconds: number
  redirectUris: string[]
  autoApprove: boolean
  authorizedGrantTypes: string[]
  scopes: string[]
  authorities: string[]
  resourceIds: string[]
  additionalInformation: string
  createTime: string
}

export interface OAuth2ClientSaveReqVO {
  id?: number
  clientId: string
  secret: string
  name: string
  logo: string
  description: string
  status: number
  accessTokenValiditySeconds: number
  refreshTokenValiditySeconds: number
  redirectUris: string[]
  autoApprove: boolean
  authorizedGrantTypes: string[]
  scopes: string[]
  authorities: string[]
  resourceIds: string[]
  additionalInformation: string
}

export interface OAuth2ClientPageReqVO extends YudaoPageParam {
  clientId?: string
  name?: string
  status?: number
}

export function getOAuth2ClientPage(
  params: OAuth2ClientPageReqVO
): Promise<YudaoPageResult<OAuth2ClientRespVO>> {
  return get<YudaoPageResult<OAuth2ClientRespVO>>('/admin-api/system/oauth2-client/page', params)
}

export function getOAuth2Client(id: number): Promise<OAuth2ClientRespVO> {
  return get<OAuth2ClientRespVO>('/admin-api/system/oauth2-client/get', { id })
}

export function createOAuth2Client(data: OAuth2ClientSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/oauth2-client/create', data)
}

export function updateOAuth2Client(data: OAuth2ClientSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/oauth2-client/update', data)
}

export function deleteOAuth2Client(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/oauth2-client/delete', { id })
}

// ===================== 短信渠道 =====================

export interface SmsChannelRespVO {
  id: number
  code: string
  status: number
  signature: string
  remark: string
  apiKey: string
  apiSecret: string
  callbackUrl: string
  createTime: string
}

export interface SmsChannelSaveReqVO {
  id?: number
  code: string
  status: number
  signature: string
  remark: string
  apiKey: string
  apiSecret: string
  callbackUrl: string
}

export interface SmsChannelPageReqVO extends YudaoPageParam {
  signature?: string
  code?: string
  status?: number
}

export interface SmsChannelSimpleRespVO {
  id: number
  code: string
  signature: string
}

export function getSmsChannelPage(params: SmsChannelPageReqVO): Promise<YudaoPageResult<SmsChannelRespVO>> {
  return get<YudaoPageResult<SmsChannelRespVO>>('/admin-api/system/sms-channel/page', params)
}

export function getSimpleSmsChannelList(): Promise<SmsChannelSimpleRespVO[]> {
  return get<SmsChannelSimpleRespVO[]>('/admin-api/system/sms-channel/simple-list')
}

export function getSmsChannel(id: number): Promise<SmsChannelRespVO> {
  return get<SmsChannelRespVO>('/admin-api/system/sms-channel/get', { id })
}

export function createSmsChannel(data: SmsChannelSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/sms-channel/create', data)
}

export function updateSmsChannel(data: SmsChannelSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/sms-channel/update', data)
}

export function deleteSmsChannel(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/sms-channel/delete', { id })
}

// ===================== 短信模板 =====================

export interface SmsTemplateRespVO {
  id: number
  type: number
  status: number
  code: string
  name: string
  content: string
  remark: string
  apiTemplateId: string
  channelId: number
  channelCode: string
  params: string[]
  createTime: string
}

export interface SmsTemplateSaveReqVO {
  id?: number
  type: number
  status: number
  code: string
  name: string
  content: string
  remark: string
  apiTemplateId: string
  channelId: number
}

export interface SmsTemplatePageReqVO extends YudaoPageParam {
  code?: string
  name?: string
  channelId?: number
  status?: number
  createTime?: string[]
}

export interface SmsTemplateSimpleRespVO {
  id: number
  name: string
  code: string
}

export interface SendSmsReqVO {
  mobile: string
  templateCode: string
  templateParams: Record<string, unknown>
}

export function getSimpleSmsTemplateList(): Promise<SmsTemplateSimpleRespVO[]> {
  return get<SmsTemplateSimpleRespVO[]>('/admin-api/system/sms-template/simple-list')
}

export function getSmsTemplatePage(
  params: SmsTemplatePageReqVO
): Promise<YudaoPageResult<SmsTemplateRespVO>> {
  return get<YudaoPageResult<SmsTemplateRespVO>>('/admin-api/system/sms-template/page', params)
}

export function getSmsTemplate(id: number): Promise<SmsTemplateRespVO> {
  return get<SmsTemplateRespVO>('/admin-api/system/sms-template/get', { id })
}

export function createSmsTemplate(data: SmsTemplateSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/sms-template/create', data)
}

export function updateSmsTemplate(data: SmsTemplateSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/sms-template/update', data)
}

export function deleteSmsTemplate(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/sms-template/delete', { id })
}

export function sendSms(data: SendSmsReqVO): Promise<number> {
  return post<number>('/admin-api/system/sms-template/send-sms', data)
}

// ===================== 短信日志 =====================

export interface SmsLogRespVO {
  id: number
  channelId: number
  channelCode: string
  templateId: number
  templateCode: string
  templateType: number
  templateContent: string
  templateParams: Record<string, unknown>
  apiTemplateId: string
  mobile: string
  userId: number
  userType: number
  sendStatus: number
  sendTime: string
  apiSendCode: string
  apiSendMsg: string
  apiRequestId: string
  apiSerialNo: string
  receiveStatus: number
  receiveTime: string
  apiReceiveCode: string
  apiReceiveMsg: string
  createTime: string
}

export interface SmsLogPageReqVO extends YudaoPageParam {
  channelId?: number
  templateId?: number
  mobile?: string
  sendStatus?: number
  receiveStatus?: number
  sendTime?: string[]
  createTime?: string[]
}

export function getSmsLogPage(params: SmsLogPageReqVO): Promise<YudaoPageResult<SmsLogRespVO>> {
  return get<YudaoPageResult<SmsLogRespVO>>('/admin-api/system/sms-log/page', params)
}

// ===================== 邮箱账号 =====================

export interface MailAccountRespVO {
  id: number
  mail: string
  username: string
  password: string
  host: string
  port: number
  sslEnable: boolean
  starttlsEnable: boolean
  createTime: string
}

export interface MailAccountSaveReqVO {
  id?: number
  mail: string
  username: string
  password: string
  host: string
  port: number
  sslEnable: boolean
  starttlsEnable: boolean
}

export interface MailAccountPageReqVO extends YudaoPageParam {
  mail?: string
  host?: string
  username?: string
}

export interface MailAccountSimpleRespVO {
  id: number
  mail: string
  username: string
}

export function getMailAccountPage(
  params: MailAccountPageReqVO
): Promise<YudaoPageResult<MailAccountRespVO>> {
  return get<YudaoPageResult<MailAccountRespVO>>('/admin-api/system/mail-account/page', params)
}

export function getMailAccount(id: number): Promise<MailAccountRespVO> {
  return get<MailAccountRespVO>('/admin-api/system/mail-account/get', { id })
}

export function getSimpleMailAccountList(): Promise<MailAccountSimpleRespVO[]> {
  return get<MailAccountSimpleRespVO[]>('/admin-api/system/mail-account/simple-list')
}

export function createMailAccount(data: MailAccountSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/mail-account/create', data)
}

export function updateMailAccount(data: MailAccountSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/mail-account/update', data)
}

export function deleteMailAccount(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/mail-account/delete', { id })
}

// ===================== 邮件模板 =====================

export interface MailTemplateRespVO {
  id: number
  name: string
  code: string
  accountId: number
  nickname: string
  title: string
  content: string
  status: number
  params: string[]
  createTime: string
}

export interface MailTemplateSaveReqVO {
  id?: number
  name: string
  code: string
  accountId: number
  nickname: string
  title: string
  content: string
  status: number
}

export interface MailTemplatePageReqVO extends YudaoPageParam {
  name?: string
  code?: string
  accountId?: number
  status?: number
  createTime?: string[]
}

export interface MailTemplateSimpleRespVO {
  id: number
  name: string
  code: string
}

export interface MailSendReqVO {
  toMails: string[]
  ccMails?: string[]
  bccMails?: string[]
  templateCode: string
  templateParams: Record<string, unknown>
}

export function getSimpleMailTemplateList(): Promise<MailTemplateSimpleRespVO[]> {
  return get<MailTemplateSimpleRespVO[]>('/admin-api/system/mail-template/simple-list')
}

export function getMailTemplatePage(
  params: MailTemplatePageReqVO
): Promise<YudaoPageResult<MailTemplateRespVO>> {
  return get<YudaoPageResult<MailTemplateRespVO>>('/admin-api/system/mail-template/page', params)
}

export function getMailTemplate(id: number): Promise<MailTemplateRespVO> {
  return get<MailTemplateRespVO>('/admin-api/system/mail-template/get', { id })
}

export function createMailTemplate(data: MailTemplateSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/mail-template/create', data)
}

export function updateMailTemplate(data: MailTemplateSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/mail-template/update', data)
}

export function deleteMailTemplate(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/mail-template/delete', { id })
}

export function sendMail(data: MailSendReqVO): Promise<number> {
  return post<number>('/admin-api/system/mail-template/send-mail', data)
}

// ===================== 邮件日志 =====================

export interface MailLogRespVO {
  id: number
  userId: number
  userType: number
  toMails: string[]
  ccMails: string[]
  bccMails: string[]
  accountId: number
  fromMail: string
  fromNickname?: string
  templateId: number
  templateCode: string
  templateNickname: string
  templateTitle: string
  templateContent: string
  templateParams: string
  sendStatus: number
  sendTime: string
  sendMessageId: string
  sendException: string
  createTime: string
}

export interface MailLogPageReqVO extends YudaoPageParam {
  accountId?: number
  templateId?: number
  toMail?: string
  sendStatus?: number
  sendTime?: string[]
}

export function getMailLogPage(params: MailLogPageReqVO): Promise<YudaoPageResult<MailLogRespVO>> {
  return get<YudaoPageResult<MailLogRespVO>>('/admin-api/system/mail-log/page', params)
}

export function getMailLog(id: number): Promise<MailLogRespVO> {
  return get<MailLogRespVO>('/admin-api/system/mail-log/get', { id })
}

// ===================== 站内信模板 =====================

export interface NotifyTemplateRespVO {
  id: number
  name: string
  nickname: string
  code: string
  content: string
  type: number
  params: string[]
  status: number
  remark: string
  createTime: string
}

export interface NotifyTemplateSaveReqVO {
  id?: number
  name: string
  nickname: string
  code: string
  content: string
  type: number
  status: number
  remark: string
}

export interface NotifyTemplatePageReqVO extends YudaoPageParam {
  name?: string
  code?: string
  status?: number
  createTime?: string[]
}

export interface NotifyTemplateSimpleRespVO {
  id: number
  name: string
  code: string
}

export interface NotifySendReqVO {
  userId: number
  templateCode: string
  templateParams: Record<string, unknown>
}

export function getSimpleNotifyTemplateList(): Promise<NotifyTemplateSimpleRespVO[]> {
  return get<NotifyTemplateSimpleRespVO[]>('/admin-api/system/notify-template/simple-list')
}

export function getNotifyTemplatePage(
  params: NotifyTemplatePageReqVO
): Promise<YudaoPageResult<NotifyTemplateRespVO>> {
  return get<YudaoPageResult<NotifyTemplateRespVO>>('/admin-api/system/notify-template/page', params)
}

export function getNotifyTemplate(id: number): Promise<NotifyTemplateRespVO> {
  return get<NotifyTemplateRespVO>('/admin-api/system/notify-template/get', { id })
}

export function createNotifyTemplate(data: NotifyTemplateSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/notify-template/create', data)
}

export function updateNotifyTemplate(data: NotifyTemplateSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/notify-template/update', data)
}

export function deleteNotifyTemplate(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/notify-template/delete', { id })
}

export function sendNotify(data: NotifySendReqVO): Promise<boolean> {
  return post<boolean>('/admin-api/system/notify-template/send-notify', data)
}

// ===================== 站内信消息（含我的站内信） =====================

export interface NotifyMessageRespVO {
  id: number
  userId: number
  userType: number
  templateId: number
  templateCode: string
  templateNickname: string
  templateContent: string
  templateType: number
  templateParams: string
  readStatus: boolean
  readTime: string
  createTime: string
}

export interface NotifyMessagePageReqVO extends YudaoPageParam {
  userId?: number
  userType?: number
  templateCode?: string
  templateType?: number
  readStatus?: boolean
  createTime?: string[]
}

export interface MyNotifyMessagePageReqVO extends YudaoPageParam {
  readStatus?: boolean
  createTime?: string[]
}

export function getNotifyMessagePage(
  params: NotifyMessagePageReqVO
): Promise<YudaoPageResult<NotifyMessageRespVO>> {
  return get<YudaoPageResult<NotifyMessageRespVO>>('/admin-api/system/notify-message/page', params)
}

export function getMyNotifyMessagePage(
  params: MyNotifyMessagePageReqVO
): Promise<YudaoPageResult<NotifyMessageRespVO>> {
  return get<YudaoPageResult<NotifyMessageRespVO>>('/admin-api/system/notify-message/my-page', params)
}

export function updateNotifyMessageRead(ids: number[]): Promise<boolean> {
  return put<boolean>('/admin-api/system/notify-message/update-read', { ids })
}

export function updateAllNotifyMessageRead(): Promise<boolean> {
  return put<boolean>('/admin-api/system/notify-message/update-all-read')
}

export function getUnreadNotifyMessageList(): Promise<NotifyMessageRespVO[]> {
  return get<NotifyMessageRespVO[]>('/admin-api/system/notify-message/get-unread-list')
}

export function getUnreadNotifyMessageCount(): Promise<number> {
  return get<number>('/admin-api/system/notify-message/get-unread-count')
}

// ===================== 社交客户端 =====================

export interface SocialClientRespVO {
  id: number
  name: string
  socialType: number
  userType: number
  clientId: string
  clientSecret: string
  agentId: string
  publicKey: string
  status: number
  createTime: string
}

export interface SocialClientSaveReqVO {
  id?: number
  name: string
  socialType: number
  userType: number
  clientId: string
  clientSecret: string
  agentId: string
  publicKey: string
  status: number
}

export interface SocialClientPageReqVO extends YudaoPageParam {
  name?: string
  socialType?: number
  userType?: number
  status?: number
}

export function getSocialClientPage(
  params: SocialClientPageReqVO
): Promise<YudaoPageResult<SocialClientRespVO>> {
  return get<YudaoPageResult<SocialClientRespVO>>('/admin-api/system/social-client/page', params)
}

export function getSocialClient(id: number): Promise<SocialClientRespVO> {
  return get<SocialClientRespVO>('/admin-api/system/social-client/get', { id })
}

export function createSocialClient(data: SocialClientSaveReqVO): Promise<number> {
  return post<number>('/admin-api/system/social-client/create', data)
}

export function updateSocialClient(data: SocialClientSaveReqVO): Promise<boolean> {
  return put<boolean>('/admin-api/system/social-client/update', data)
}

export function deleteSocialClient(id: number): Promise<boolean> {
  return del<boolean>('/admin-api/system/social-client/delete', { id })
}

// ===================== 社交用户 =====================

export interface SocialUserRespVO {
  id: number
  type: number
  openid: string
  token: string
  rawTokenInfo: string
  nickname: string
  avatar: string
  rawUserInfo: string
  code: string
  state: string
  createTime: string
}

export interface SocialUserPageReqVO extends YudaoPageParam {
  type?: number
  openid?: string
  nickname?: string
  createTime?: string[]
}

export interface SocialUserBindRespVO {
  id: number
  type: number
  openid: string
  nickname: string
  avatar: string
}

export function getSocialUserPage(
  params: SocialUserPageReqVO
): Promise<YudaoPageResult<SocialUserRespVO>> {
  return get<YudaoPageResult<SocialUserRespVO>>('/admin-api/system/social-user/page', params)
}

export function getSocialUser(id: number): Promise<SocialUserRespVO> {
  return get<SocialUserRespVO>('/admin-api/system/social-user/get', { id })
}

export function getBindSocialUserList(): Promise<SocialUserBindRespVO[]> {
  return get<SocialUserBindRespVO[]>('/admin-api/system/social-user/get-bind-list')
}

// ===================== 地区 =====================

export interface AreaTreeNode {
  id: number
  name: string
  children?: AreaTreeNode[]
}

export function getAreaTree(): Promise<AreaTreeNode[]> {
  return get<AreaTreeNode[]>('/admin-api/system/area/tree')
}

export function getAreaByIp(ip: string): Promise<string> {
  return get<string>('/admin-api/system/area/get-by-ip', { ip })
}
