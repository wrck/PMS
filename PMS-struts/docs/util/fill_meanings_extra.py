#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""补充填充：对"待确认"字段进行更精细的推断"""
import re, os

MD_PATH = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\03-database\complete-data-dictionary.md'

# 额外的推断规则 - 基于第一次运行后的"待确认"字段分析
EXTRA_RULES = {
    # Activiti 字段补充
    'ACTION_': '操作类型', 'START_ACT_ID_': '开始活动节点ID', 'END_ACT_ID_': '结束活动节点ID',
    'PARENT_TASK_ID_': '父任务ID', 'PASSWORD_': '密码',
    'LAST_': '姓氏', 'EMAIL_': '邮箱', 'PWD_': '密码',
    'PICTURE_ID_': '头像ID', 'INFO_JSON_ID_': '信息JSON ID',
    'LAST_UPDATE_TIME_': '最后更新时间', 'EVENT_NAME_': '事件名称',
    'CREATED_': '创建时间', 'CONFIGURATION_': '配置',
    'IS_MI_ROOT_': '是否多实例根', 'SCOPE_ID_': '作用域ID',
    'SUB_SCOPE_ID_': '子作用域ID', 'SCOPE_TYPE_': '作用域类型',
    'SCOPE_DEFINITION_ID_': '作用域定义ID',
    'REFERENCED_ID_': '引用ID', 'REFERENCED_TYPE_': '引用类型',
    'PROP_KEY_': '属性键', 'PROP_VALUE_': '属性值',
    'CORRELATION_ID_': '关联ID', 'ELEMENT_ID_': '元素ID',
    'ELEMENT_NAME_': '元素名称', 'EVENT_STATE_': '事件状态',
    'EVENT_TYPE_': '事件类型', 'ACTIVITY_ID_': '活动ID',
    'ACTIVITY_NAME_': '活动名称', 'ACTIVITY_TYPE_': '活动类型',
    'COMPENSATION_': '补偿', 'FIRST_': '是否第一个', 'SECOND_': '是否第二个',
    'IS_MIRRORED_': '是否镜像', 'IS_BATCH_EXECUTABLE_': '是否批量可执行',
    'IS_USELESS_': '是否无用', 'BATCH_ID_': '批次ID',
    'SEARCH_KEY_': '搜索键', 'SEARCH_KEY2_': '搜索键2',
    'STATUS_': '状态', 'BATCH_STATUS_': '批次状态',
    'COMPLETE_TIME_': '完成时间', 'BATCH_PART_DESC_': '批次部分描述',
    'TYPE_NAME_': '类型名称', 'APP_VERSION_': '应用版本',
    'OPERATION_TYPE_': '操作类型', 'OPERATION_USER_ID_': '操作用户ID',
    'OPERATION_TIME_': '操作时间', 'ENTITY_ID_': '实体ID',
    'NEW_VALUE_': '新值', 'OLD_VALUE_': '旧值',
    'CHANGE_TYPE_': '变更类型', 'PROPERTY_': '属性',
    'JOB_ID_': '作业ID', 'JOB_TYPE_': '作业类型',
    'JOB_HANDLER_TYPE_': '作业处理器类型', 'JOB_HANDLER_CFG_': '作业处理器配置',
    'CREATION_TIME_': '创建时间', 'CUSTOMER_ID_': '客户ID',
    'CUSTOMER_NAME_': '客户名称', 'HISTORIC_JOB_ID_': '历史作业ID',
    'EXCEPTION_CFG_': '异常配置', 'SUPPORTS_EVENT_COMPENSATION_': '是否支持事件补偿',
    'PROC_DEF_ID_': '流程定义ID', 'PROC_INST_ID_': '流程实例ID',
    'SUPER_EXEC_': '父执行ID', 'CACHED_ENT_STATE_': '缓存实体状态',
    'IS_COUNT_ENABLED_': '是否启用计数', 'EVT_SUBSCR_COUNT_': '事件订阅计数',
    'TASK_COUNT_': '任务计数', 'JOB_COUNT_': '作业计数',
    'TIMER_JOB_COUNT_': '定时作业计数', 'SUSP_JOB_COUNT_': '挂起作业计数',
    'DEADLETTER_JOB_COUNT_': '死信作业计数', 'EXTERNAL_JOB_COUNT_': '外部作业计数',
    'TASK_DEF_KEY_': '任务定义键', 'OWNER_': '拥有者', 'ASSIGNEE_': '办理人',
    'DELEGATION_': '委托状态', 'PRIORITY_': '优先级', 'CREATE_TIME_': '创建时间',
    'DUE_DATE_': '到期日期', 'FORM_KEY_': '表单键', 'CLAIM_TIME_': '认领时间',
    'CLAIM_USER_ID_': '认领用户ID', 'BYTEARRAY_ID_': '字节数组ID',
    'DOUBLE_': '双精度值', 'LONG_': '长整型值', 'TEXT_': '文本值', 'TEXT2_': '文本值2',
    'GROUP_ID_': '组ID', 'LOCK_EXP_TIME_': '锁过期时间', 'EXCLUSIVE_': '是否排他',
    'PROCESS_INSTANCE_ID_': '流程实例ID', 'PROCESS_DEF_ID_': '流程定义ID',
    'RETRIES_': '重试次数', 'EXCEPTION_STACK_ID_': '异常堆栈ID',
    'EXCEPTION_MSG_': '异常消息', 'DUEDATE_': '到期时间', 'REPEAT_': '重复表达式',
    'HANDLER_TYPE_': '处理器类型', 'HANDLER_CFG_': '处理器配置',
    'START_TIME_': '开始时间', 'END_TIME_': '结束时间', 'DURATION_': '耗时',
    'START_USER_ID_': '发起人ID', 'DELETE_REASON_': '删除原因',
    'SUPER_PROCESS_INSTANCE_ID_': '父流程实例ID',
    'ACT_TYPE_': '活动类型', 'ACT_NAME_': '活动名称', 'CALL_PROC_INST_ID_': '调用流程实例ID',
    'VAR_TYPE_': '变量类型', 'VAR_INST_ID_': '变量实例ID', 'ACT_INST_ID_': '活动实例ID',
    'LAST_UPDATED_TIME_': '最后更新时间', 'TIME_': '时间戳',
    'MESSAGE_': '评论内容', 'FULL_MSG_': '完整消息',
    'URL_': '附件URL', 'CONTENT_ID_': '内容ID',
    'USER_ID_': '用户ID', 'TENANT_ID_': '租户ID',
    'ID_': 'ID标识', 'REV_': '版本号', 'NAME_': '名称',
    'DEPLOYMENT_ID_': '部署ID', 'BYTES_': '字节数据', 'GENERATED_': '是否自动生成',
    'VALUE_': '属性值', 'KEY_': '标识', 'TYPE_': '类型',
    'DESCRIPTION_': '描述', 'PARENT_ID_': '父级ID',
    'BUSINESS_KEY_': '业务键', 'SUSPENSION_STATE_': '挂起状态',
    'EXECUTION_ID_': '执行ID', 'TASK_ID_': '任务ID',
    'LOG_NR_': '日志编号', 'TIME_STAMP_': '时间戳',
    'DATA_': '数据', 'LOCK_OWNER_': '锁持有者', 'LOCK_TIME_': '锁时间',
    'IS_PROCESSED_': '是否已处理', 'IS_ACTIVE_': '是否活跃',
    'IS_CONCURRENT_': '是否并发', 'IS_SCOPE_': '是否作用域',
    'IS_EVENT_SCOPE_': '是否事件作用域', 'ROOT_PROC_INST_ID_': '根流程实例ID',
    'VERSION_': '版本号', 'RESOURCE_NAME_': '资源文件名',
    'DGRM_RESOURCE_NAME_': '流程图资源文件名', 'HAS_START_FORM_KEY_': '是否有开始表单键',
    'HAS_GRAPHICAL_NOTATION_': '是否有图形化标记',
    'TITLE_': '标题', 'CATEGORY_': '分类',
    'DEPLOY_TIME_': '部署时间', 'DERIVED_FROM_': '派生来源',
    'DERIVED_FROM_ROOT_': '派生根来源', 'PARENT_DEPLOYMENT_ID_': '父部署ID',
    'ENGINE_VERSION_': '引擎版本',
    'EDITOR_SOURCE_VALUE_ID_': '编辑器源值ID',
    'EDITOR_SOURCE_EXTRA_VALUE_ID_': '编辑器扩展源值ID',
    'EDITOR_SOURCE_': '编辑器源数据', 'EDITOR_SOURCE_EXTRA_': '编辑器扩展源数据',
    'META_INFO_': '元信息', 'ACT_ID_': '活动节点ID',
    'INFO_JSON_ID_': '信息JSON ID', 'PICTURE_ID_': '图片ID',
    'LAST_': '姓氏', 'EMAIL_': '邮箱', 'PWD_': '密码', 'PASSWORD_': '密码',
    'ACTION_': '操作', 'START_ACT_ID_': '开始活动ID', 'END_ACT_ID_': '结束活动ID',
    'PARENT_TASK_ID_': '父任务ID', 'LAST_UPDATE_TIME_': '最后更新时间',
    'EVENT_NAME_': '事件名称', 'CREATED_': '创建时间',
    'CONFIGURATION_': '配置', 'IS_MI_ROOT_': '是否多实例根',

    # 业务字段补充 - D365 PMS系统
    'ocrCode': 'OCR编码', 'ocrName': 'OCR名称', 'isparam': '是否参数',
    'itemCode': '项目编码', 'itemName': '项目名称',
    'spare_serialNum': '备件序列号', 'sheetID': '工单ID',
    'compID': '公司ID', 'compCode': '公司编码', 'compName': '公司名称',
    'compAbbr': '公司简称', 'adminID': '管理员ID',
    'compGrade': '公司等级', 'compType': '公司类型', 'compArea': '公司区域',
    'effectDate': '生效日期', 'regAddress': '注册地址',
    'postCode': '邮政编码', 'webSite': '网站地址',
    'isDisabled': '是否禁用', 'disabledDate': '禁用日期',
    'depID': '部门ID', 'depCode': '部门编码', 'depName': '部门名称',
    'depAbbr': '部门简称', 'depGrade': '部门等级', 'depType': '部门类型',
    'depProperty': '部门属性', 'depCost': '部门成本中心',
    'director2': '副主管', 'depEmp': '部门人数', 'depNum': '部门编号',
    'xOrder': '排序', 'depCustom1': '部门自定义1', 'depCustom2': '部门自定义2',
    'depCustom3': '部门自定义3', 'depCustom4': '部门自定义4', 'depCustom5': '部门自定义5',
    'empID': '员工ID', 'workNo': '工号',
    'eName': '员工姓名', 'jobID': '岗位ID',
    'reportTo': '汇报对象', 'wfreportTo': '流程汇报对象',
    'empStatus': '员工状态', 'jobStatus': '岗位状态', 'empType': '员工类型',
    'joinDate': '入职日期', 'workBeginDate': '工作开始日期',
    'jobBeginDate': '岗位开始日期', 'pracBeginDate': '实习开始日期',
    'pracEndDate': '实习结束日期', 'probBeginDate': '试用期开始日期',
    'probEndDate': '试用期结束日期', 'leaveDate': '离职日期',
    'gender': '性别', 'officePhone': '办公电话',
    'empCustom1': '员工自定义1', 'empCustom2': '员工自定义2',
    'empCustom3': '员工自定义3', 'empCustom4': '员工自定义4', 'empCustom5': '员工自定义5',
    'depIDs': '部门ID列表', 'extraDepIDs': '额外部门ID列表',
    'adminDepIDs': '管理部门ID列表', 'empIDs': '员工ID列表', 'extraEmpIDs': '额外员工ID列表',
    'jobCode': '岗位编码', 'jobName': '岗位名称', 'jobAbbr': '岗位简称',
    'jobGrage': '岗位等级', 'jobType': '岗位类型', 'jobProperty': '岗位属性',
    'jobCustom1': '岗位自定义1', 'jobCustom2': '岗位自定义2',
    'jobCustom3': '岗位自定义3', 'jobCustom4': '岗位自定义4', 'jobCustom5': '岗位自定义5',
    'isLeaf': '是否叶子节点', 'fullName': '全名', 'cName': '中文名称',
    'eName': '英文名称', 'isDefault': '是否默认', 'isSystem': '是否系统',
    'isHidden': '是否隐藏', 'isReadonly': '是否只读', 'isRequired': '是否必填',
    'isMultiple': '是否多选', 'isVirtual': '是否虚拟', 'isMain': '是否主要',
    'isPrimary': '是否主要', 'isTemplate': '是否模板', 'isDraft': '是否草稿',
    'isFree': '是否免费', 'isPaid': '是否付费', 'isOnline': '是否在线',
    'isManual': '是否手动', 'isAuto': '是否自动', 'isProcess': '是否处理',
    'isComplete': '是否完成', 'isFinish': '是否完成', 'isCancel': '是否取消',
    'isClose': '是否关闭', 'isOpen': '是否开启', 'isStart': '是否开始',
    'isEnd': '是否结束', 'isPass': '是否通过', 'isFail': '是否失败',
    'isSuccess': '是否成功', 'isError': '是否错误', 'isWarning': '是否警告',
    'isCheck': '是否审核', 'isSelect': '是否选中', 'isExpand': '是否展开',
    'isSync': '是否同步', 'isEnable': '是否启用', 'isApprove': '是否审批',
    'isCurrent': '是否当前', 'isInternal': '是否内部', 'isExternal': '是否外部',
    'isTest': '是否测试', 'isFinal': '是否终稿', 'isParent': '是否父节点',
    'isDel': '是否删除', 'isLocked': '是否锁定', 'isPublic': '是否公开',
    'isPrivate': '是否私有', 'isProtected': '是否受保护', 'isShared': '是否共享',
    'isGroup': '是否分组', 'isCategory': '是否分类', 'isTag': '是否标签',
    'isFilter': '是否过滤', 'isSearch': '是否搜索', 'isSort': '是否排序',
    'isPage': '是否分页', 'isExport': '是否导出', 'isImport': '是否导入',
    'isPrint': '是否打印', 'isCopy': '是否复制', 'isMove': '是否移动',
    'isRename': '是否重命名', 'isDelete': '是否删除', 'isRestore': '是否恢复',
    'isArchive': '是否归档', 'isBackup': '是否备份', 'isCompress': '是否压缩',
    'isEncrypt': '是否加密', 'isDecrypt': '是否解密', 'isUpload': '是否上传',
    'isDownload': '是否下载', 'isPreview': '是否预览', 'isReview': '是否审核',
    'isApprove': '是否审批', 'isReject': '是否驳回', 'isAccept': '是否接受',
    'isConfirm': '是否确认', 'isVerify': '是否验证', 'isValidate': '是否校验',
    'isAuthorize': '是否授权', 'isAuthenticate': '是否认证', 'isLogin': '是否登录',
    'isLogout': '是否登出', 'isRegister': '是否注册', 'isSubscribe': '是否订阅',
    'isPublish': '是否发布', 'isNotify': '是否通知', 'isAlert': '是否告警',
    'isWarn': '是否警告', 'isIgnore': '是否忽略', 'isSkip': '是否跳过',
    'isDelay': '是否延迟', 'isSchedule': '是否调度', 'isRetry': '是否重试',
    'isAbort': '是否中止', 'isCancel': '是否取消', 'isSuspend': '是否暂停',
    'isResume': '是否恢复', 'isRestart': '是否重启', 'isReset': '是否重置',
    'isRefresh': '是否刷新', 'isReload': '是否重新加载', 'isInitialize': '是否初始化',
    'isDestroy': '是否销毁', 'isDispose': '是否释放', 'isCleanup': '是否清理',
    'isGrant': '是否授权', 'isRevoke': '是否撤销', 'isDeny': '是否拒绝',
    'isAllow': '是否允许', 'isPermit': '是否许可', 'isForbid': '是否禁止',
    'isProhibit': '是否禁止', 'isRestrict': '是否限制', 'isLimit': '是否限制',
    'isControl': '是否控制', 'isMonitor': '是否监控', 'isDetect': '是否检测',
    'isPrevent': '是否预防', 'isProtect': '是否保护', 'isDefend': '是否防御',
    'isAttack': '是否攻击', 'isThreat': '是否威胁', 'isVulnerable': '是否有漏洞',
    'isSecure': '是否安全', 'isSafe': '是否安全', 'isDangerous': '是否危险',
    'isRisk': '是否有风险', 'isHazardous': '是否危险', 'isHarmful': '是否有害',
    'isBeneficial': '是否有益', 'isUseful': '是否有用', 'isUseless': '是否无用',
    'isEffective': '是否有效', 'isInvalid': '是否无效', 'isExpired': '是否过期',
    'isObsolete': '是否废弃', 'isDeprecated': '是否已废弃', 'isLegacy': '是否遗留',
    'isNew': '是否新增', 'isOld': '是否旧', 'isLatest': '是否最新',
    'isPrevious': '是否上一个', 'isNext': '是否下一个', 'isFirst': '是否第一个',
    'isLast': '是否最后一个', 'isMiddle': '是否中间', 'isTop': '是否顶部',
    'isBottom': '是否底部', 'isLeft': '是否左侧', 'isRight': '是否右侧',
    'isFront': '是否前面', 'isBack': '是否后面', 'isUp': '是否上方',
    'isDown': '是否下方', 'isInner': '是否内部', 'isOuter': '是否外部',
    'isUpper': '是否大写', 'isLower': '是否小写', 'isCapital': '是否大写',
    'isSmall': '是否小', 'isLarge': '是否大', 'isMedium': '是否中等',
    'isFull': '是否完整', 'isPartial': '是否部分', 'isEmpty': '是否为空',
    'isNull': '是否为空', 'isBlank': '是否为空', 'isZero': '是否为零',
    'isOne': '是否为一', 'isMultiple': '是否多个', 'isSingle': '是否单个',
    'isUnique': '是否唯一', 'isDuplicate': '是否重复', 'isRedundant': '是否冗余',
    'isConflict': '是否冲突', 'isConsistent': '是否一致', 'isInconsistent': '是否不一致',
    'isCorrect': '是否正确', 'isIncorrect': '是否不正确', 'isValid': '是否有效',
    'isInvalid': '是否无效', 'isTrue': '是否为真', 'isFalse': '是否为假',
    'isPositive': '是否为正', 'isNegative': '是否为负', 'isNeutral': '是否中性',
    'isNormal': '是否正常', 'isAbnormal': '是否异常', 'isException': '是否异常',
    'isError': '是否错误', 'isWarning': '是否警告', 'isInfo': '是否信息',
    'isDebug': '是否调试', 'isTrace': '是否追踪', 'isLog': '是否日志',
    'isMetric': '是否指标', 'isMeasure': '是否度量', 'isIndicator': '是否指标',
    'isKpi': '是否关键绩效指标', 'isTarget': '是否目标', 'isGoal': '是否目标',
    'isObjective': '是否目标', 'isMilestone': '是否里程碑', 'isProgress': '是否进度',
    'isComplete': '是否完成', 'isIncomplete': '是否未完成', 'isPending': '是否待处理',
    'isWaiting': '是否等待中', 'isRunning': '是否运行中', 'isExecuting': '是否执行中',
    'isFinished': '是否已完成', 'isDone': '是否已完成', 'isEnded': '是否已结束',
    'isClosed': '是否已关闭', 'isResolved': '是否已解决', 'isFixed': '是否已修复',
    'isCancelled': '是否已取消', 'isAborted': '是否已中止', 'isTerminated': '是否已终止',
    'isArchived': '是否已归档', 'isRemoved': '是否已移除', 'isRestored': '是否已恢复',
    'isRecovered': '是否已恢复', 'isReverted': '是否已还原', 'isUndone': '是否已撤销',
    'isRetried': '是否已重试', 'isEscalated': '是否已升级', 'isTransferred': '是否已转移',
    'isDelegated': '是否已委托', 'isRedirected': '是否已重定向', 'isRouted': '是否已路由',
    'isDelivered': '是否已送达', 'isReceived': '是否已接收', 'isAcknowledged': '是否已确认',
    'isConfirmed': '是否已确认', 'isVerified': '是否已验证', 'isValidated': '是否已校验',
    'isApproved': '是否已审批', 'isRejected': '是否已拒绝', 'isDenied': '是否已拒绝',
    'isAccepted': '是否已接受', 'isAgreed': '是否已同意', 'isDisputed': '是否已争议',
    'isChallenged': '是否已质疑', 'isInvestigated': '是否已调查', 'isAudited': '是否已审计',
    'isInspected': '是否已检查', 'isTested': '是否已测试', 'isCertified': '是否已认证',
    'isQualified': '是否已合格', 'isCompliant': '是否已合规', 'isStandardized': '是否已标准化',
    'isNormalized': '是否已规范化', 'isEncoded': '是否已编码', 'isDecoded': '是否已解码',
    'isEncrypted': '是否已加密', 'isDecrypted': '是否已解密', 'isCompressed': '是否已压缩',
    'isSerialized': '是否已序列化', 'isFormatted': '是否已格式化', 'isParsed': '是否已解析',
    'isCompiled': '是否已编译', 'isExecuted': '是否已执行', 'isRendered': '是否已渲染',
    'isDisplayed': '是否已显示', 'isPrinted': '是否已打印', 'isExported': '是否已导出',
    'isImported': '是否已导入', 'isUploaded': '是否已上传', 'isDownloaded': '是否已下载',
    'isTransmitted': '是否已传输', 'isSent': '是否已发送', 'isDispatched': '是否已派发',
    'isDistributed': '是否已分发', 'isPublished': '是否已发布', 'isNotified': '是否已通知',
    'isAlerted': '是否已告警', 'isInformed': '是否已通知', 'isReminded': '是否已提醒',
    'isRequested': '是否已请求', 'isCommanded': '是否已命令', 'isInstructed': '是否已指示',
    'isAssisted': '是否已协助', 'isSupported': '是否已支持', 'isGranted': '是否已授权',
    'isAwarded': '是否已授予', 'isRewarded': '是否已奖励', 'isCompensated': '是否已补偿',
    'isReimbursed': '是否已报销', 'isRefunded': '是否已退款', 'isReturned': '是否已退货',
    'isExchanged': '是否已换货', 'isReplaced': '是否已替换', 'isPurchased': '是否已购买',
    'isSold': '是否已出售', 'isLeased': '是否已租赁', 'isHired': '是否已雇佣',
    'isEmployed': '是否已雇用', 'isContracted': '是否已签约', 'isAppointed': '是否已任命',
    'isDesignated': '是否已指定', 'isAllocated': '是否已分配', 'isBudgeted': '是否已预算',
    'isEstimated': '是否已估算', 'isPredicted': '是否已预测', 'isAutomated': '是否已自动化',
    'isDigitized': '是否已数字化', 'isVirtualized': '是否已虚拟化', 'isContainerized': '是否已容器化',
    'isOrchestrated': '是否已编排', 'isCoordinated': '是否已协调', 'isSynchronized': '是否已同步',
    'isIntegrated': '是否已集成', 'isConsolidated': '是否已整合', 'isMerged': '是否已合并',
    'isCombined': '是否已组合', 'isUnified': '是否已统一', 'isAligned': '是否已对齐',
    'isConformed': '是否已符合', 'isComplied': '是否已遵从', 'isAdhered': '是否已遵守',
    'isFulfilled': '是否已履行', 'isSatisfied': '是否已满足', 'isAchieved': '是否已实现',
    'isAccomplished': '是否已完成', 'isAttained': '是否已达到', 'isReached': '是否已到达',
    'isArrived': '是否已到达', 'isDeparted': '是否已出发', 'isExited': '是否已退出',
    'isEntered': '是否已进入', 'isJoined': '是否已加入', 'isParticipated': '是否已参与',
    'isAttended': '是否已出席', 'isPresent': '是否在场', 'isAbsent': '是否缺席',
    'isAvailable': '是否可用', 'isAccessible': '是否可访问', 'isConnected': '是否已连接',
    'isDisconnected': '是否已断开', 'isOnline': '是否在线', 'isOffline': '是否离线',
    'isActive': '是否活跃', 'isInactive': '是否不活跃', 'isDormant': '是否休眠',
    'isIdle': '是否空闲', 'isBusy': '是否忙碌', 'isOccupied': '是否占用',
    'isReserved': '是否已预留', 'isBooked': '是否已预订', 'isTentative': '是否暂定',
    'isProvisional': '是否临时', 'isTemporary': '是否临时', 'isPermanent': '是否永久',
    'isPersistent': '是否持久', 'isTransient': '是否瞬态', 'isStatic': '是否静态',
    'isDynamic': '是否动态', 'isMutable': '是否可变', 'isImmutable': '是否不可变',
}

# 更多的后缀推断规则
SUFFIX_RULES = {
    'ID': 'ID', 'Id': 'ID', 'id': 'ID',
    'Code': '编码', 'code': '编码', 'Code': '编码',
    'Name': '名称', 'name': '名称',
    'Abbr': '简称', 'abbr': '简称',
    'Type': '类型', 'type': '类型',
    'Grade': '等级', 'grade': '等级',
    'Status': '状态', 'status': '状态',
    'Date': '日期', 'date': '日期',
    'Time': '时间', 'time': '时间',
    'No': '编号', 'no': '编号',
    'Num': '数量', 'num': '数量',
    'Desc': '描述', 'desc': '描述',
    'Amount': '金额', 'amount': '金额',
    'Count': '计数', 'count': '计数',
    'Flag': '标记', 'flag': '标记',
    'Property': '属性', 'property': '属性',
    'Custom1': '自定义1', 'Custom2': '自定义2', 'Custom3': '自定义3',
    'Custom4': '自定义4', 'Custom5': '自定义5',
    'custom1': '自定义1', 'custom2': '自定义2', 'custom3': '自定义3',
    'custom4': '自定义4', 'custom5': '自定义5',
    'Phone': '电话', 'phone': '电话',
    'Address': '地址', 'address': '地址',
    'Area': '区域', 'area': '区域',
    'Cost': '成本', 'cost': '成本',
    'Emp': '员工', 'emp': '员工',
    'Dep': '部门', 'dep': '部门',
    'Comp': '公司', 'comp': '公司',
    'Job': '岗位', 'job': '岗位',
    'Director': '主管', 'director': '主管',
    'Admin': '管理员', 'admin': '管理员',
    'Order': '排序', 'order': '排序',
    'Serial': '序列号', 'serial': '序列号',
    'Sheet': '工单', 'sheet': '工单',
    'Spare': '备件', 'spare': '备件',
    'Item': '项目', 'item': '项目',
    'Ocr': 'OCR', 'ocr': 'OCR',
    'Param': '参数', 'param': '参数',
    'Reg': '注册', 'reg': '注册',
    'Post': '邮政', 'post': '邮政',
    'Web': '网站', 'web': '网站',
    'Disabled': '禁用', 'disabled': '禁用',
    'Work': '工作', 'work': '工作',
    'Join': '入职', 'join': '入职',
    'Leave': '离职', 'leave': '离职',
    'Gender': '性别', 'gender': '性别',
    'Office': '办公', 'office': '办公',
    'Report': '汇报', 'report': '汇报',
    'Wfreport': '流程汇报', 'wfreport': '流程汇报',
    'Prac': '实习', 'prac': '实习',
    'Prob': '试用', 'prob': '试用',
    'Extra': '额外', 'extra': '额外',
    'Begin': '开始', 'begin': '开始',
    'End': '结束', 'end': '结束',
    'Effect': '生效', 'effect': '生效',
    'XOrder': '排序', 'xOrder': '排序',
}

# 前缀推断规则
PREFIX_RULES = {
    'comp': '公司', 'dep': '部门', 'emp': '员工', 'job': '岗位',
    'admin': '管理员', 'director': '主管', 'spare': '备件',
    'item': '项目', 'ocr': 'OCR', 'sheet': '工单',
    'reg': '注册', 'post': '邮政', 'web': '网站',
    'office': '办公', 'work': '工作', 'gender': '性别',
    'report': '汇报', 'wfreport': '流程汇报', 'prac': '实习',
    'prob': '试用', 'extra': '额外', 'depCustom': '部门自定义',
    'empCustom': '员工自定义', 'jobCustom': '岗位自定义',
}

def infer_extra(col_name, table_name=''):
    # 精确匹配
    if col_name in EXTRA_RULES:
        return EXTRA_RULES[col_name]

    # 尝试后缀匹配
    for suffix, meaning in SUFFIX_RULES.items():
        if col_name.endswith(suffix) and len(col_name) > len(suffix):
            prefix = col_name[:-len(suffix)]
            # 尝试前缀匹配
            for pfx, pmeaning in PREFIX_RULES.items():
                if prefix.lower().startswith(pfx.lower()):
                    return pmeaning + meaning

    # is前缀
    if col_name.startswith('is') and len(col_name) > 2:
        rest = col_name[2:]
        if rest:
            return '是否' + rest

    # has前缀
    if col_name.startswith('has') and len(col_name) > 3:
        rest = col_name[3:]
        if rest:
            return '是否有' + rest

    # 下划线分隔 - 尝试组合
    if '_' in col_name:
        parts = col_name.split('_')
        meanings = []
        for p in parts:
            if p in EXTRA_RULES:
                meanings.append(EXTRA_RULES[p])
            elif p.lower() in PREFIX_RULES:
                meanings.append(PREFIX_RULES[p.lower()])
            elif p in SUFFIX_RULES:
                meanings.append(SUFFIX_RULES[p])
            else:
                meanings.append(p)
        return ''.join(meanings)

    # 驼峰分隔 - 尝试组合
    import re as re_mod
    parts = re_mod.findall('[A-Z]?[a-z]+|[A-Z]+(?=[A-Z]|$)', col_name)
    if len(parts) > 1:
        meanings = []
        for p in parts:
            pl = p.lower()
            if pl in PREFIX_RULES:
                meanings.append(PREFIX_RULES[pl])
            elif p in SUFFIX_RULES:
                meanings.append(SUFFIX_RULES[p])
            elif p in EXTRA_RULES:
                meanings.append(EXTRA_RULES[p])
            else:
                meanings.append(p)
        return ''.join(meanings)

    return ''

# 读取文档
with open(MD_PATH, 'r', encoding='utf-8') as f:
    lines = f.readlines()

current_table = ''
filled = 0
total_tbc = 0
new_lines = []

for i, line in enumerate(lines):
    stripped = line.strip()
    if stripped.startswith('### ') and not stripped.startswith('### 目录'):
        current_table = stripped[4:].strip()
        new_lines.append(line)
        continue

    if not stripped.startswith('|'):
        new_lines.append(line)
        continue

    parts = stripped.split('|')
    if len(parts) < 8:
        new_lines.append(line)
        continue

    col_name = parts[1].strip()
    meaning = parts[6].strip()

    if meaning == '待确认':
        total_tbc += 1
        new_meaning = infer_extra(col_name, current_table)
        if not new_meaning:
            new_meaning = '待确认'
        else:
            filled += 1

        parts[6] = f' {new_meaning} '
        new_line = '|'.join(parts) + '\n'
        new_lines.append(new_line)
    else:
        new_lines.append(line)

print(f"待确认字段总数: {total_tbc}")
print(f"本次补充填充: {filled}")
print(f"剩余待确认: {total_tbc - filled}")

with open(MD_PATH, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print(f"文件已更新")
