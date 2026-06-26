const fs = require('fs');

// Helper to read persisted MCP output files
function readMcpOutput(filePath) {
  const raw = fs.readFileSync(filePath, 'utf8');
  // Format: "The MCP server responded with: [{\"type\":\"text\",\"text\":\"[...]\"}]"
  const prefix = 'The MCP server responded with: ';
  const jsonStr = raw.trim().startsWith(prefix) ? raw.trim().substring(prefix.length) : raw.trim();
  const outer = JSON.parse(jsonStr);
  const innerText = outer[0].text;
  return JSON.parse(innerText);
}

// Read persisted output files
const tables = readMcpOutput('C:\\Users\\user\\AppData\\Local\\Temp\\trae\\toolcall-output\\d863af5e-6d87-49aa-a8ff-e9965927207c.txt');
const pmProjectCols = readMcpOutput('C:\\Users\\user\\AppData\\Local\\Temp\\trae\\toolcall-output\\1c2929c1-cb1d-4d5c-946d-d7cd7e9375cc.txt');
const pmClCols = readMcpOutput('C:\\Users\\user\\AppData\\Local\\Temp\\trae\\toolcall-output\\06289199-e730-4af4-b26e-529d9609208c.txt');
const otherCols = readMcpOutput('C:\\Users\\user\\AppData\\Local\\Temp\\trae\\toolcall-output\\43d0426f-2998-4643-858f-1c59289c2026.txt');
const indexes = readMcpOutput('C:\\Users\\user\\AppData\\Local\\Temp\\trae\\toolcall-output\\6a8b9897-25e9-461e-8ecf-e1268e04817d.txt');

// Merge all columns
const allColumns = [...pmProjectCols, ...pmClCols, ...otherCols];

// Group columns and indexes by table
const columnsByTable = {};
for (const col of allColumns) {
  if (!columnsByTable[col.TABLE_NAME]) columnsByTable[col.TABLE_NAME] = [];
  columnsByTable[col.TABLE_NAME].push(col);
}

const indexesByTable = {};
for (const idx of indexes) {
  if (!indexesByTable[idx.TABLE_NAME]) indexesByTable[idx.TABLE_NAME] = {};
  if (!indexesByTable[idx.TABLE_NAME][idx.INDEX_NAME]) {
    indexesByTable[idx.TABLE_NAME][idx.INDEX_NAME] = {
      name: idx.INDEX_NAME,
      type: idx.INDEX_TYPE,
      unique: idx.NON_UNIQUE === 0,
      columns: []
    };
  }
  indexesByTable[idx.TABLE_NAME][idx.INDEX_NAME].columns.push(idx.COLUMN_NAME);
}

// Table metadata
const tableMeta = {};
for (const t of tables) {
  tableMeta[t.TABLE_NAME] = t;
}

// Business meaning mapping - comprehensive dictionary
// Key format: "tableName.fieldName" or just "fieldName" for universal fields
const meaningMap = {
  // Universal audit fields
  'id': '自增主键，记录唯一标识',
  'createTime': '记录创建时间',
  'createBy': '记录创建用户编码',
  'updateTime': '记录最新更新时间',
  'updateBy': '记录最新更新用户编码',
  'effectiveFrom': '数据有效性开始时间（软删除模式）',
  'effectiveTo': '数据有效性结束时间，NULL=当前有效',
  'disabled': '0=有效，1=失效',
  'customInfo': 'JSON扩展字段，存储动态属性',

  // pm_project
  'pm_project.projectId': '项目唯一标识，关联项目其他子表',
  'pm_project.projectType': '用服售后:10，安服售后:afss，安服先行:afxx',
  'pm_project.projectCode': '项目唯一业务编码，由系统生成',
  'pm_project.projectName': '项目的业务名称',
  'pm_project.projectState': '项目阶段状态，1=初始创建，0=不予跟踪，对应fnd_basic_data(dataTypeCode=02)',
  'pm_project.isback': '30=创建项目，32=指定项目经理，34=填写渠道信息，40=工程管理部不予跟踪，42=项目经理选择不予跟踪',
  'pm_project.column001': '逻辑外键 -> fnd_department.departmentNum',
  'pm_project.column002': 'ERP系统中的客户编码',
  'pm_project.column003': 'ERP系统中的客户名称',
  'pm_project.column004': '市场部组织编码',
  'pm_project.column005': '系统部组织ID',
  'pm_project.column006': '拓展部组织ID',
  'pm_project.column007': '子行业分类ID',
  'pm_project.column008': 'notGrantTailCause，项目不予跟踪的原因说明',
  'pm_project.column009': '来自SMS系统的订单创建时间',
  'pm_project.column010': '逻辑外键 -> fnd_basic_data(dataTypeCode=05)，项目等级分类',
  'pm_project.column011': '项目业务分类',
  'pm_project.column012': '实施方式编码，0/1/2/3/4对应不同实施模式',
  'pm_project.columno12_readonly': '-1=可修改，其他值=只读（来自SMS的不可修改）',
  'pm_project.column013': '最终客户单位名称',
  'pm_project.column014': '项目回退时的说明文字',
  'pm_project.customerProjectName': '客户侧的项目名称',
  'pm_project.salesType': '01=正常，02=借转销，14=销售类借货',
  'pm_project.majorProjectLevel': '重大项目等级标识',
  'pm_project.compId': '逻辑外键 -> fnd_company.id',
  'pm_project.createTime': '记录创建时间（指定服务经理时间）',
  'pm_project.createBy': '记录创建用户',
  'pm_project.updateTime': '记录最新更新时间',
  'pm_project.updateBy': '记录最新更新用户',
  'pm_project.effectiveFrom': '数据有效性开始时间（软删除模式）',
  'pm_project.effectiveTo': '数据有效性结束时间，NULL=当前有效',
  'pm_project.disabled': '0=有效，1=失效',
  'pm_project.projectStartTime': '指定项目经理的时间',
  'pm_project.projectRefreshTime': '项目相关数据最后编辑时间',
  'pm_project.projectCloseTime': '项目闭环的时间点',
  'pm_project.customInfo': 'JSON扩展字段，存储serviceManagerCode/programManagerCode/programManagerCodeB等动态属性',
  'pm_project.customConfig': 'JSON配置字段',

  // pm_project_contract
  'pm_project_contract.id': '合同记录唯一标识',
  'pm_project_contract.contractNo': '合同编号，逻辑外键 -> pm_project_product_line.contractNo',
  'pm_project_contract.projectGroupCode': '逻辑外键 -> pm_project_group.projectGroupCode',

  // pm_project_group
  'pm_project_group.id': '自增主键，项目组唯一标识',
  'pm_project_group.projectGroupCode': '项目组唯一编码',
  'pm_project_group.projectGroupName': '项目组的业务名称',
  'pm_project_group.projectType': '默认10=工程管理售后项目',

  // pm_project_group_relationship
  'pm_project_group_relationship.id': '自增主键，关系记录唯一标识',
  'pm_project_group_relationship.projectGroupCode': '逻辑外键 -> pm_project_group.projectGroupCode',
  'pm_project_group_relationship.projectCode': '逻辑外键 -> pm_project.projectCode',
  'pm_project_group_relationship.mergeBranchMark': '标识项目拆分/合并的业务标记',
  'pm_project_group_relationship.smsProjectCode': '从SMS系统迁移过来的原始项目编码',

  // pm_project_member
  'pm_project_member.id': '自增主键，成员记录唯一标识',
  'pm_project_member.projectId': '逻辑外键 -> pm_project.projectId 或 pm_presales_project_header.presalesId',
  'pm_project_member.projectType': '售后10/售前20，详见fnd_basic_data',
  'pm_project_member.memberRole': '10=销售人员,20=服务经理,30=项目经理',
  'pm_project_member.memberCode': '逻辑外键 -> fnd_user_info.username，外部人员为空',
  'pm_project_member.memberName': '项目成员的真实姓名',
  'pm_project_member.phoneNum': '项目成员联系电话',
  'pm_project_member.email': '项目成员邮箱地址',
  'pm_project_member.fromFlag': '1=来源于项目信息，2=来源于成员信息',
  'pm_project_member.effectiveTo': 'NULL=当前有效，非NULL=已失效',

  // pm_project_state
  'pm_project_state.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_state.projectPlanState': '逻辑外键 -> fnd_basic_data',
  'pm_project_state.projectplanTime': '工程计划状态最后变更时间',
  'pm_project_state.shipmentState': '-1=已发货，1=未发货，2=部分发货',
  'pm_project_state.shipmentTime': '发货状态最后变更时间',
  'pm_project_state.executionState': '项目实施阶段状态',
  'pm_project_state.executionStateTime': '实施状态最后变更时间',
  'pm_project_state.closeProcessState': '项目闭环流程阶段',
  'pm_project_state.closeProcessStateTime': '闭环流程状态最后变更时间',

  // pm_project_task
  'pm_project_task.taskId': '任务自增主键，任务唯一标识',
  'pm_project_task.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_task.projectType': '默认10=售后，20=售前测试',
  'pm_project_task.contractNo': '关联合同编号',
  'pm_project_task.taskTypeCode': '逻辑外键 -> fnd_basic_data',
  'pm_project_task.taskTypeId': '如completeTest=完成测试',
  'pm_project_task.taskName': '任务业务名称',
  'pm_project_task.eventPlanHappenDate': '款项计划发生日期',
  'pm_project_task.eventPlanHappenDateENG': '工程计划发生日期',
  'pm_project_task.planStartTime': '任务计划开始时间',
  'pm_project_task.planEndTime': '任务计划结束时间',
  'pm_project_task.actualStartTime': '任务实际开始时间',
  'pm_project_task.eventActualFinishDate': '任务实际完成日期',
  'pm_project_task.priority': '任务优先级',
  'pm_project_task.progress': '0-100',
  'pm_project_task.progressDesc': '任务进度文字描述',
  'pm_project_task.status': '任务状态，0=未开始',
  'pm_project_task.parentId': '支持树形任务结构',
  'pm_project_task.remark': '备注说明',
  'pm_project_task.visibleFlag': '1=可见，2=不可见',
  'pm_project_task.deliverFileIds': '逻辑外键 -> fnd_files.id',
  'pm_project_task.customInfo': 'JSON扩展字段',

  // pm_project_related_party
  'pm_project_related_party.id': '自增主键，相关方记录唯一标识',
  'pm_project_related_party.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_related_party.partyRole': '0=服务商渠道，1=代理商渠道',
  'pm_project_related_party.partyCode': '相关方（渠道商/代理商）编码',
  'pm_project_related_party.partyName': '相关方（渠道商/代理商）名称',

  // pm_project_product_line
  'pm_project_product_line.id': '自增主键，产品线记录唯一标识',
  'pm_project_product_line.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_product_line.contractNo': '逻辑外键 -> pm_project_contract.contractNo',
  'pm_project_product_line.itemCode': 'ERP系统产品编码',
  'pm_project_product_line.itemName': '产品名称',
  'pm_project_product_line.projectQuantity': '项目产品总数量',
  'pm_project_product_line.orderQuantity': '已下单产品数量',
  'pm_project_product_line.deliverQuantity': '已发货产品数量',
  'pm_project_product_line.openQuantity': '未发货产品数量',
  'pm_project_product_line.orderNumber': 'ERP订单号',
  'pm_project_product_line.lineNum': '订单行号',

  // pm_project_shipment
  'pm_project_shipment.id': '自增主键，发货记录唯一标识',
  'pm_project_shipment.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_shipment.barcode': '设备序列号/条码',
  'pm_project_shipment.itemCode': '发货产品编码',
  'pm_project_shipment.itemModel': '产品型号',
  'pm_project_shipment.itemName': '产品名称',
  'pm_project_shipment.receiveName': '收货人姓名',
  'pm_project_shipment.emsNum': '快递/物流单号',
  'pm_project_shipment.emsCompany': '快递/物流公司名称',
  'pm_project_shipment.packdate': '设备打包日期',
  'pm_project_shipment.contractNo': '关联合同编号',
  'pm_project_shipment.installAddress': '设备安装地址',
  'pm_project_shipment.chProjectId': '串货转移前所属项目ID',
  'pm_project_shipment.chContractNo': '串货转移前合同编号',
  'pm_project_shipment.transferProjectId': '串货转移后目标项目ID',
  'pm_project_shipment.transferContractNo': '串货转移后合同编号',
  'pm_project_shipment.transferFlag': '-1=默认，1=转出，0=转入',

  // pm_project_maintenance
  'pm_project_maintenance.id': '自增主键，维护记录唯一标识',
  'pm_project_maintenance.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_maintenance.projectCode': '项目编码，冗余存储',
  'pm_project_maintenance.projectName': '项目名称，冗余存储',
  'pm_project_maintenance.projectType': '售前:20/售后:10',
  'pm_project_maintenance.projectExecutionState': '项目当前实施状态',
  'pm_project_maintenance.contractNo': '关联合同编号',
  'pm_project_maintenance.officeCode': '逻辑外键 -> fnd_department.departmentNum',
  'pm_project_maintenance.compId': '逻辑外键 -> fnd_company.id',
  'pm_project_maintenance.type': '服务任务性质编码',
  'pm_project_maintenance.category': '服务任务分类编码',
  'pm_project_maintenance.subCategory': '服务任务小类编码',
  'pm_project_maintenance.processTime': '服务处理时间',
  'pm_project_maintenance.processDesc': '服务事项描述',
  'pm_project_maintenance.processStep': '问题解决进展',
  'pm_project_maintenance.remainProblem': '遗留问题描述',
  'pm_project_maintenance.transitHour': '在途耗时（小时）',
  'pm_project_maintenance.processHour': '处理耗时（小时）',
  'pm_project_maintenance.itemModel': '服务产品型号',
  'pm_project_maintenance.softVersion': '设备在网软件版本',
  'pm_project_maintenance.enabledFeatures': '设备启用功能列表',
  'pm_project_maintenance.customTos': '自定义邮件主送人',
  'pm_project_maintenance.customCcs': '自定义邮件抄送人',
  'pm_project_maintenance.hasReport': '0=无巡检报告，1=有巡检报告',
  'pm_project_maintenance.quesnaireId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_project_maintenance.deliverFileIds': '逻辑外键 -> fnd_files.id',
  'pm_project_maintenance.warrantyStatus': '项目维保状态',
  'pm_project_maintenance.industryName': '客户所属行业',
  'pm_project_maintenance.userOffice': '用户所属办事处',
  'pm_project_maintenance.year': '服务记录所属年度',
  'pm_project_maintenance.quarter': '服务记录所属季度(1-4)',
  'pm_project_maintenance.month': '服务记录所属月份(1-12)',
  'pm_project_maintenance.wsCount': '当前维保服务次数',
  'pm_project_maintenance.wafCount': '当前其他服务次数',
  'pm_project_maintenance.wsYearCount': '年度维保服务累计次数',
  'pm_project_maintenance.wafYearCount': '年度其他服务累计次数',
  'pm_project_maintenance.warrantyInfo': '维保服务详细信息',
  'pm_project_maintenance.serviceInfo': '其他服务详细信息',
  'pm_project_maintenance.remark': '备注说明',
  'pm_project_maintenance.customInfo': 'JSON扩展字段',

  // pm_project_log
  'pm_project_log.id': '自增主键，日志记录唯一标识',
  'pm_project_log.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_log.handleName': '操作名称（如：指定项目经理）',
  'pm_project_log.handleDesc': '操作描述或原因说明',
  'pm_project_log.handleUser': '执行操作的用户编码',
  'pm_project_log.taskStartTime': '操作开始时间',
  'pm_project_log.handleEndTime': '操作结束时间',
  'pm_project_log.handleState': '0=无通知，1=已通知',

  // pm_project_instruction
  'pm_project_instruction.id': '自增主键，批示记录唯一标识',
  'pm_project_instruction.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_instruction.instructionsInfo': '批示/反馈的具体内容',
  'pm_project_instruction.instructionsTime': '批示/反馈的时间',
  'pm_project_instruction.instructionsUser': '批示/反馈的用户编码',
  'pm_project_instruction.dataType': '0=批示信息，1=批示反馈',
  'pm_project_instruction.instructionsId': '反馈对应的原始批示记录ID',

  // pm_project_supervision
  'pm_project_supervision.id': '自增主键，督查记录唯一标识',
  'pm_project_supervision.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_supervision.projectCode': '项目编码',
  'pm_project_supervision.projectName': '项目名称',
  'pm_project_supervision.channel': '代理商/服务商名称',
  'pm_project_supervision.officeCode': '办事处编码',
  'pm_project_supervision.type': '督查任务性质',
  'pm_project_supervision.processTime': '督查处理时间',
  'pm_project_supervision.state': '0=未完成，1=已完成',
  'pm_project_supervision.isDelete': '0=未删除，1=已删除',
  'pm_project_supervision.quesnaireId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_project_supervision.deliverFileIds': '逻辑外键 -> fnd_files.id',
  'pm_project_supervision.remark': '备注说明',

  // pm_project_warranty_callback
  'pm_project_warranty_callback.id': '自增主键，维保回访记录唯一标识',
  'pm_project_warranty_callback.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_warranty_callback.projectCode': '项目编码',
  'pm_project_warranty_callback.officeCode': '办事处编码',
  'pm_project_warranty_callback.contractNos': '关联合同编号（多个逗号分隔）',
  'pm_project_warranty_callback.projectIds': '关联项目ID（多个逗号分隔）',
  'pm_project_warranty_callback.projectName': '项目名称',
  'pm_project_warranty_callback.serviceImpl': '项目实施方式编码',
  'pm_project_warranty_callback.industryName': '客户所属行业',
  'pm_project_warranty_callback.agentChannel': '下单代理商名称',
  'pm_project_warranty_callback.finalCustomerName': '最终客户单位名称',
  'pm_project_warranty_callback.customer1': '客户联系人1姓名',
  'pm_project_warranty_callback.customerContact1': '客户联系人1联系方式',
  'pm_project_warranty_callback.customer2': '客户联系人2姓名',
  'pm_project_warranty_callback.customerContact2': '客户联系人2联系方式',
  'pm_project_warranty_callback.warrantyStartTime': '维保合同开始日期',
  'pm_project_warranty_callback.warrantyEndTime': '维保合同结束日期',
  'pm_project_warranty_callback.renewalIntention': '0=无，1=有，2=待定',
  'pm_project_warranty_callback.callbackTime': '回访时间',
  'pm_project_warranty_callback.nextCallbackTime': '下次回访时间',
  'pm_project_warranty_callback.taskId': 'Activiti任务ID',
  'pm_project_warranty_callback.quesnaireId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_project_warranty_callback.quesnaireVersion': '问卷模板版本号',
  'pm_project_warranty_callback.quesnaireState': '-1=草稿，1=已提交',
  'pm_project_warranty_callback.isDelete': '0=未删除，1=已删除',
  'pm_project_warranty_callback.remark': '备注说明',
  'pm_project_warranty_callback.compId': '所属公司ID',
  'pm_project_warranty_callback.customInfo': 'JSON扩展字段',

  // pm_project_weekly
  'pm_project_weekly.weeklyId': '周报自增主键',
  'pm_project_weekly.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_weekly.currentTask': '当前工程阶段名称',
  'pm_project_weekly.taskStartTime': '当前阶段开始时间',
  'pm_project_weekly.taskEndTime': '当前阶段结束时间',
  'pm_project_weekly.taskDeviation': '进度偏差说明',
  'pm_project_weekly.remark': '备注说明',
  'pm_project_weekly.weeklyStartTime': '周报统计周期开始时间',
  'pm_project_weekly.weeklyEndTime': '周报统计周期结束时间',
  'pm_project_weekly.weeklyState': '0=草稿，1=已提交',

  // pm_project_weekly_content
  'pm_project_weekly_content.id': '自增主键，内容记录唯一标识',
  'pm_project_weekly_content.weeklyId': '逻辑外键 -> pm_project_weekly.weeklyId',
  'pm_project_weekly_content.optionDesc001': '周报选项描述1（工作内容）',
  'pm_project_weekly_content.optionDesc002': '周报选项描述2（下周计划）',
  'pm_project_weekly_content.optionType': '选项类型，对应周报不同部分',

  // pm_project_soft_version
  'pm_project_soft_version.id': '自增主键，版本记录唯一标识',
  'pm_project_soft_version.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_project_soft_version.logId': '逻辑外键 -> pm_project_soft_change_logs.id',
  'pm_project_soft_version.contractNo': '关联合同编号',
  'pm_project_soft_version.itemCode': '产品编码',
  'pm_project_soft_version.barCode': '设备序列号/条码',
  'pm_project_soft_version.conp': 'CONP软件版本号',
  'pm_project_soft_version.conpType': 'CONP版本类型',
  'pm_project_soft_version.conpSeries': 'CONP版本系列',
  'pm_project_soft_version.conpMark': '软件版本掩码，用于版本范围匹配',
  'pm_project_soft_version.conpBak': 'CONP变更前备份版本号',
  'pm_project_soft_version.conpChange': '0=CONP无更新，1=CONP有更新',
  'pm_project_soft_version.cpld': 'CPLD版本号',
  'pm_project_soft_version.cpldBak': 'CPLD变更前备份版本号',
  'pm_project_soft_version.cpldChange': '0=CPLD无更新，1=CPLD有更新',
  'pm_project_soft_version.boot': 'Boot版本号',
  'pm_project_soft_version.bootBak': 'Boot变更前备份版本号',
  'pm_project_soft_version.bootChange': '0=Boot无更新，1=Boot有更新',
  'pm_project_soft_version.pcb': 'PCB版本号',
  'pm_project_soft_version.pcbBak': 'PCB变更前备份版本号',
  'pm_project_soft_version.pcbChange': '0=PCB无更新，1=PCB有更新',
  'pm_project_soft_version.executeTime': '版本更新执行日期',
  'pm_project_soft_version.datastate': '0=失效，1=有效',
  'pm_project_soft_version.customInfo': 'JSON扩展字段',

  // pm_project_notification
  'pm_project_notification.id': '自增主键，通知记录唯一标识',
  'pm_project_notification.notifySubject': '通知标题',
  'pm_project_notification.notifyContent': '通知正文内容',
  'pm_project_notification.projectId': '逻辑外键 -> pm_project.projectId',

  // pm_project_notification_state
  'pm_project_notification_state.id': '自增主键，状态记录唯一标识',
  'pm_project_notification_state.notifyId': '逻辑外键 -> pm_project_notification.id',
  'pm_project_notification_state.notifyObject': '通知接收用户编码',
  'pm_project_notification_state.notifyState': '0=未读，1=已读',
  'pm_project_notification_state.checkTime': '用户查看通知的时间',

  // pm_project_header_view_cache
  'pm_project_header_view_cache.projectCode': '项目编码',
  'pm_project_header_view_cache.subProjectCode': '子项目/合同级别编码',
  'pm_project_header_view_cache.projectName': '项目名称',
  'pm_project_header_view_cache.contractNo': '合同编号',
  'pm_project_header_view_cache.majorProjectLevel': '重大项目级别',
  'pm_project_header_view_cache.officeName': '办事处名称（冗余）',
  'pm_project_header_view_cache.customerName': '客户名称（冗余）',
  'pm_project_header_view_cache.marketName': '市场部名称（冗余）',
  'pm_project_header_view_cache.systemName': '系统部名称（冗余）',
  'pm_project_header_view_cache.expendName': '拓展部名称（冗余）',
  'pm_project_header_view_cache.industryName': '行业名称（冗余）',
  'pm_project_header_view_cache.salesManCode': '销售人员编码',
  'pm_project_header_view_cache.salesManName': '销售人员姓名',
  'pm_project_header_view_cache.salesManTel': '销售人员电话',
  'pm_project_header_view_cache.salesManMail': '销售人员邮箱',
  'pm_project_header_view_cache.smCode': '服务经理编码',
  'pm_project_header_view_cache.smName': '服务经理姓名',
  'pm_project_header_view_cache.pmCode1': '项目经理1编码',
  'pm_project_header_view_cache.pmName1': '项目经理1姓名',
  'pm_project_header_view_cache.pmCode2': '项目经理2编码',
  'pm_project_header_view_cache.pmName2': '项目经理2姓名',
  'pm_project_header_view_cache.compId': '公司ID',
  'pm_project_header_view_cache.compName': '公司名称（冗余）',
  'pm_project_header_view_cache.ssfsName': '实施方式名称',
  'pm_project_header_view_cache.partnerChannel': '合作伙伴渠道名称',
  'pm_project_header_view_cache.projectType': '项目类型编码',
  'pm_project_header_view_cache.finalCustomerName': '最终客户名称',
  'pm_project_header_view_cache.customerProjectName': '客户项目名称',

  // pm_cl_callback
  'pm_cl_callback.id': '回访申请ID',
  'pm_cl_callback.projectId': '逻辑外键 -> pm_project.projectId',
  'pm_cl_callback.instId': 'Activiti流程实例ID',
  'pm_cl_callback.remark': '回访申请备注',
  'pm_cl_callback.applyState': '-1=草稿，1=审批中，2=审批通过',
  'pm_cl_callback.applyBy': '回访申请人编码',
  'pm_cl_callback.applyTime': '回访申请时间',

  // pm_cl_callback_quesnaire
  'pm_cl_callback_quesnaire.id': '自增主键，关联记录唯一标识',
  'pm_cl_callback_quesnaire.callBackId': '逻辑外键 -> pm_cl_callback.id',
  'pm_cl_callback_quesnaire.taskId': 'Activiti任务ID',
  'pm_cl_callback_quesnaire.quesnaireId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_cl_callback_quesnaire.quesnaireVersion': '问卷模板版本号',
  'pm_cl_callback_quesnaire.quesnaireState': '0=未填写，1=已填写',

  // pm_cl_evaluation_header
  'pm_cl_evaluation_header.id': '自增主键，评价记录唯一标识',
  'pm_cl_evaluation_header.projectId': '支持多项目逗号分隔',
  'pm_cl_evaluation_header.evaluationName': '评价名称/标题',
  'pm_cl_evaluation_header.evaluationDesc': '评价详细描述',
  'pm_cl_evaluation_header.nextAcceptPerson': '下一处理人编码',
  'pm_cl_evaluation_header.nextAcceptPersonName': '下一处理人姓名',
  'pm_cl_evaluation_header.evaluationState': '评价状态',

  // pm_cl_quesnaire_result_header
  'pm_cl_quesnaire_result_header.id': '自增主键，问卷结果唯一标识',
  'pm_cl_quesnaire_result_header.quesnaireTemplateHeaderId': '逻辑外键 -> pm_cl_quesnaire_template_header.id',
  'pm_cl_quesnaire_result_header.evaluationHeaderId': '逻辑外键 -> pm_cl_evaluation_header.id',
  'pm_cl_quesnaire_result_header.totalScore': '问卷填写总得分',
  'pm_cl_quesnaire_result_header.passScore': '问卷及格分数线',
  'pm_cl_quesnaire_result_header.isPass': '0=不及格，1=及格',

  // pm_cl_quesnaire_result_line
  'pm_cl_quesnaire_result_line.id': '自增主键，结果行唯一标识',
  'pm_cl_quesnaire_result_line.quesnaireResultHeaderId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_cl_quesnaire_result_line.quesnaireTemplateLineId': '逻辑外键 -> pm_cl_quesnaire_template_line.id',
  'pm_cl_quesnaire_result_line.optionId': '逻辑外键 -> pm_cl_quesnaire_template_options.id',
  'pm_cl_quesnaire_result_line.score': '该题实际得分',
  'pm_cl_quesnaire_result_line.resultDesc': '回答内容/结果描述',

  // pm_cl_quesnaire_template_header
  'pm_cl_quesnaire_template_header.id': '自增主键，模板唯一标识',
  'pm_cl_quesnaire_template_header.questionnaireTemplateName': '问卷模板名称',
  'pm_cl_quesnaire_template_header.questionnaireTemplateNum': '问卷模板编号',
  'pm_cl_quesnaire_template_header.questionnaireScore': '问卷满分',
  'pm_cl_quesnaire_template_header.questionnairePassScore': '问卷及格分',
  'pm_cl_quesnaire_template_header.questionnaireStatus': '模板状态，0=禁用，1=启用',
  'pm_cl_quesnaire_template_header.quesType': '问卷业务类型',
  'pm_cl_quesnaire_template_header.markIndexs': '标记索引，用于模板配置',
  'pm_cl_quesnaire_template_header.createdPerson': '模板创建人',
  'pm_cl_quesnaire_template_header.createdTime': '模板创建时间',
  'pm_cl_quesnaire_template_header.updatedPerson': '模板最后修改人',
  'pm_cl_quesnaire_template_header.updatedTime': '模板最后修改时间',
  'pm_cl_quesnaire_template_header.effectiveStartTime': '模板生效开始时间',
  'pm_cl_quesnaire_template_header.effectiveEndTime': '模板生效结束时间',

  // pm_cl_quesnaire_template_line
  'pm_cl_quesnaire_template_line.id': '自增主键，题目唯一标识',
  'pm_cl_quesnaire_template_line.quesnaireTemplateHeaderId': '逻辑外键 -> pm_cl_quesnaire_template_header.id',
  'pm_cl_quesnaire_template_line.questionDesc': '题目描述文本',
  'pm_cl_quesnaire_template_line.questionType': '题目类型（单选/多选/文本）',
  'pm_cl_quesnaire_template_line.questionScore': '题目分值',
  'pm_cl_quesnaire_template_line.sortNo': '排序序号',
  'pm_cl_quesnaire_template_line.required': '0=非必填，1=必填',

  // pm_cl_quesnaire_template_options
  'pm_cl_quesnaire_template_options.id': '自增主键，选项唯一标识',
  'pm_cl_quesnaire_template_options.quesnaireTemplateLineId': '逻辑外键 -> pm_cl_quesnaire_template_line.id',
  'pm_cl_quesnaire_template_options.optionDesc': '选项描述文本',
  'pm_cl_quesnaire_template_options.optionScore': '选择该选项的得分',
  'pm_cl_quesnaire_template_options.sortNo': '排序序号',

  // pm_presales_project_header
  'pm_presales_project_header.presalesId': '售前项目自增主键',
  'pm_presales_project_header.presalesCode': '售前项目唯一编码',
  'pm_presales_project_header.projectCode': '关联的原售后项目编码',
  'pm_presales_project_header.projectName': '项目名称',
  'pm_presales_project_header.projectType': '逻辑外键 -> fnd_basic_data(dataTypeCode=presalesType)',
  'pm_presales_project_header.projectState': '项目状态编码',
  'pm_presales_project_header.marketName': '市场部名称',
  'pm_presales_project_header.systemName': '系统部名称',
  'pm_presales_project_header.expendName': '拓展部名称',
  'pm_presales_project_header.industryName': '行业名称',
  'pm_presales_project_header.officeCode': '逻辑外键 -> fnd_department.departmentNum',
  'pm_presales_project_header.salesman': '销售人员姓名',
  'pm_presales_project_header.salesmanLink': '销售人员联系方式',
  'pm_presales_project_header.productManager': '产品经理姓名',
  'pm_presales_project_header.lendfiles': '借货交付件信息',
  'pm_presales_project_header.lendInfoId': '逻辑外键 -> pm_presales_lend_info_from_oa',
  'pm_presales_project_header.hasTransfer': '是否存在借转销数据',
  'pm_presales_project_header.hasRma': '是否存在未核销RMA数据',
  'pm_presales_project_header.closeRemark': '项目关闭备注',
  'pm_presales_project_header.confirmFileIds': '逻辑外键 -> fnd_files.id',
  'pm_presales_project_header.finshedTime': '项目完成测试时间',
  'pm_presales_project_header.instId': 'Activiti流程实例ID',
  'pm_presales_project_header.applyState': '-1=草稿，1=审批中，2=审批通过',
  'pm_presales_project_header.applyBy': '售前申请人编码',
  'pm_presales_project_header.applyTime': '售前申请时间',
  'pm_presales_project_header.endTime': '项目结束时间',
  'pm_presales_project_header.source': '数据来源标识',
  'pm_presales_project_header.customInfo': 'JSON扩展字段',

  // pm_presales_project_product_line
  'pm_presales_project_product_line.productLineId': '产品线自增主键',
  'pm_presales_project_product_line.presalesId': '逻辑外键 -> pm_presales_project_header.presalesId',
  'pm_presales_project_product_line.lendInfoId': '借货信息ID',
  'pm_presales_project_product_line.productFirstName': '产品一级分类名称',
  'pm_presales_project_product_line.productTypeName': '产品类型名称',
  'pm_presales_project_product_line.itemCode': '产品编码',
  'pm_presales_project_product_line.itemModel': '产品型号',
  'pm_presales_project_product_line.itemDesc': '产品描述',
  'pm_presales_project_product_line.productNum': '借货产品数量',
  'pm_presales_project_product_line.transferNum': '借转销数量',
  'pm_presales_project_product_line.hexiaoNum': '核销数量',
  'pm_presales_project_product_line.remark': '备注',

  // pm_presales_project_callback
  'pm_presales_project_callback.id': '自增主键，回访记录唯一标识',
  'pm_presales_project_callback.presalesId': '逻辑外键 -> pm_presales_project_header.presalesId',
  'pm_presales_project_callback.taskId': 'Activiti任务ID',
  'pm_presales_project_callback.quesnaireId': '逻辑外键 -> pm_cl_quesnaire_result_header.id',
  'pm_presales_project_callback.quesnaireState': '0=未填写，1=已填写',

  // pm_presales_project_duration
  'pm_presales_project_duration.id': '自增主键，耗时记录唯一标识',
  'pm_presales_project_duration.presalesId': '逻辑外键 -> pm_presales_project_header.presalesId',
  'pm_presales_project_duration.applyDuration': '项目同步到项目开始的时间间隔',
  'pm_presales_project_duration.totalDuration': '项目开始到结束的时间间隔',
  'pm_presales_project_duration.serviceDuration': '服务经理指派耗时',
  'pm_presales_project_duration.programDuration': '项目经理指派耗时',
  'pm_presales_project_duration.testDuration': '测试跟踪耗时',
  'pm_presales_project_duration.callbackDuration': '回访耗时',
  'pm_presales_project_duration.serviceApproveDuration': '服务经理审批耗时',

  // pm_presales_project_rma_info
  'pm_presales_project_rma_info.id': '自增主键，RMA记录唯一标识',
  'pm_presales_project_rma_info.presalesId': '逻辑外键 -> pm_presales_project_header.presalesId',
  'pm_presales_project_rma_info.rmaNo': 'RMA退货授权编号',
  'pm_presales_project_rma_info.contractNo': '关联合同编号',
  'pm_presales_project_rma_info.itemCode': '产品编码',
  'pm_presales_project_rma_info.itemModel': '产品型号',
  'pm_presales_project_rma_info.rmaStatus': 'RMA处理状态',
  'pm_presales_project_rma_info.customInfo': 'JSON扩展字段',

  // pm_subcontract_project_header
  'pm_subcontract_project_header.id': '自增主键，转包项目唯一标识',
  'pm_subcontract_project_header.subcontractName': '转包项目名称',
  'pm_subcontract_project_header.contractNos': '关联合同编号（多个逗号分隔）',
  'pm_subcontract_project_header.projectIds': '逻辑外键 -> pm_project.projectId',
  'pm_subcontract_project_header.type': '转包类型编码',
  'pm_subcontract_project_header.state': '转包项目状态',
  'pm_subcontract_project_header.callbackState': '回访状态',
  'pm_subcontract_project_header.facilitatorId': '逻辑外键 -> pm_subcontract_facilitator.id',
  'pm_subcontract_project_header.facilitatorName': '服务商名称',
  'pm_subcontract_project_header.bankInfo': '服务商开户行信息',
  'pm_subcontract_project_header.bankAccount': '服务商收款银行账号',
  'pm_subcontract_project_header.officeCode': '逻辑外键 -> fnd_department.departmentNum',
  'pm_subcontract_project_header.profitDepCode': '收益部门编码',
  'pm_subcontract_project_header.subcontractNo': '转包合同编号',
  'pm_subcontract_project_header.isAccrued': '0=未计提，1=已计提',
  'pm_subcontract_project_header.isInvoiced': '0=未提供发票，1=已提供发票',
  'pm_subcontract_project_header.subcontractAmount': '转包合同金额',
  'pm_subcontract_project_header.reason': '转包原因说明',
  'pm_subcontract_project_header.remark': '备注',
  'pm_subcontract_project_header.effectiveFrom': '数据有效性开始时间',
  'pm_subcontract_project_header.effectiveTo': '数据有效性结束时间',
  'pm_subcontract_project_header.zrApproveTime': '最新主任审批时间',
  'pm_subcontract_project_header.orgId': '组织ID',
  'pm_subcontract_project_header.customInfo': '使用JSON_MERGE_PATCH增量更新',

  // pm_subcontract_project_line
  'pm_subcontract_project_line.id': '自增主键，转包明细唯一标识',
  'pm_subcontract_project_line.subcontractId': '逻辑外键 -> pm_subcontract_project_header.id',
  'pm_subcontract_project_line.itemCode': '产品编码',
  'pm_subcontract_project_line.itemModel': '产品型号',
  'pm_subcontract_project_line.itemName': '产品名称',
  'pm_subcontract_project_line.quantity': '转包产品数量',
  'pm_subcontract_project_line.unitPrice': '产品单价',
  'pm_subcontract_project_line.amount': '产品金额（数量*单价）',
  'pm_subcontract_project_line.remark': '备注',

  // pm_subcontract_facilitator
  'pm_subcontract_facilitator.id': '自增主键，服务商唯一标识',
  'pm_subcontract_facilitator.facilitatorName': '服务商名称',
  'pm_subcontract_facilitator.facilitatorCode': '服务商编码',
  'pm_subcontract_facilitator.bankInfo': '开户行信息',
  'pm_subcontract_facilitator.bankAccount': '银行账号',
  'pm_subcontract_facilitator.contactPerson': '服务商联系人',
  'pm_subcontract_facilitator.contactPhone': '服务商联系电话',
  'pm_subcontract_facilitator.remark': '备注',

  // pm_subcontract_project_payment
  'pm_subcontract_project_payment.id': '自增主键，付款记录唯一标识',
  'pm_subcontract_project_payment.subcontractId': '逻辑外键 -> pm_subcontract_project_header.id',
  'pm_subcontract_project_payment.paymentAmount': '付款金额',
  'pm_subcontract_project_payment.paymentDate': '付款日期',
  'pm_subcontract_project_payment.paymentState': '付款状态',
  'pm_subcontract_project_payment.remark': '备注',

  // pm_subcontract_deliver_files
  'pm_subcontract_deliver_files.id': '自增主键，交付件唯一标识',
  'pm_subcontract_deliver_files.subcontractId': '逻辑外键 -> pm_subcontract_project_header.id',
  'pm_subcontract_deliver_files.paymentId': '逻辑外键 -> pm_subcontract_project_payment.id',
  'pm_subcontract_deliver_files.fileName': '交付件文件名',
  'pm_subcontract_deliver_files.filePath': '交付件文件存储路径',
  'pm_subcontract_deliver_files.type': '0=用服交付合同，1=用服服务单，2=工程合同',
  'pm_subcontract_deliver_files.uploadBy': '文件上传人编码',
  'pm_subcontract_deliver_files.uploadTime': '文件上传时间',

  // prob_main
  'prob_main.id': 'Java Bean中映射为probId',
  'prob_main.probNum': '技术公告编号',
  'prob_main.probTicketNo': '关联工单编号',
  'prob_main.watch': '逻辑外键 -> fnd_basic_data(dataTypeCode=30)',
  'prob_main.theme': '技术公告主题',
  'prob_main.desc': '问题描述详细内容',
  'prob_main.solution': '问题解决方案',
  'prob_main.status': '逻辑外键 -> fnd_basic_data(dataTypeCode=31)',
  'prob_main.startdate': '问题发现/开始日期',
  'prob_main.duedate': '问题计划完成日期',
  'prob_main.attachments': '附件路径',
  'prob_main.attachmentNames': '附件文件名',
  'prob_main.priority': '逻辑外键 -> fnd_basic_data(dataTypeCode=32)',
  'prob_main.productType': '产品类型',
  'prob_main.relatedSceneTypes': '逗号分隔的多值',
  'prob_main.relatedSceneTypesMark': '位运算标记，用于高效筛选',
  'prob_main.mitigationActionTypes': '规避方案操作类型（逗号分隔）',
  'prob_main.mitigationActionTypesMark': '规避方案操作类型位运算标记',
  'prob_main.solutionActionTypes': '解决方案操作类型（逗号分隔）',
  'prob_main.solutionActionTypesMark': '解决方案操作类型位运算标记',
  'prob_main.trackingUser': '逻辑外键 -> fnd_user_info.username',
  'prob_main.affectedType': '1=盒式系列，2=框式系列',
  'prob_main.visibleRange': '可见范围设置',
  'prob_main.reader': '已阅读用户列表',
  'prob_main.readStatus': '阅读状态标记',
  'prob_main.remark': '备注',
  'prob_main.customInfo': '存储relatedSceneTypes等动态属性',

  // prob_product
  'prob_product.id': '自增主键，产品关联唯一标识',
  'prob_product.probId': '逻辑外键 -> prob_main.id',
  'prob_product.productType': '产品类型',
  'prob_product.productModel': '产品型号',
  'prob_product.productName': '产品名称',
  'prob_product.affectedVersion': '受影响版本范围',
  'prob_product.customInfo': 'JSON扩展字段',

  // prob_softwares
  'prob_softwares.id': '自增主键，软件版本唯一标识',
  'prob_softwares.probId': '逻辑外键 -> prob_main.id',
  'prob_softwares.conp': '受影响主控版本号',
  'prob_softwares.cpld': '受影响CPLD版本号',
  'prob_softwares.boot': '受影响Boot版本号',
  'prob_softwares.pcb': '受影响PCB版本号',
  'prob_softwares.manualEntry': '手动录入的版本号',
  'prob_softwares.affectedType': '1=盒式，2=框式',
  'prob_softwares.markStart': '版本范围起始标记',
  'prob_softwares.markEnd': '版本范围结束标记',
  'prob_softwares.datastate': '0=失效，1=有效',

  // prob_restore
  'prob_restore.id': '自增主键，恢复记录唯一标识',
  'prob_restore.probId': '逻辑外键 -> prob_main.id',
  'prob_restore.restoreDesc': '恢复/修复方案描述',
  'prob_restore.restoreType': '恢复类型编码',
  'prob_restore.restoreDate': '恢复日期',
  'prob_restore.customInfo': 'JSON扩展字段',

  // prob_read_log
  'prob_read_log.id': '自增主键，阅读记录唯一标识',
  'prob_read_log.probId': '逻辑外键 -> prob_main.id',
  'prob_read_log.reader': '阅读用户编码',
  'prob_read_log.readTime': '阅读时间',

  // fnd_act_hi_comment
  'fnd_act_hi_comment.id': '自增主键，审批意见唯一标识',
  'fnd_act_hi_comment.objId': '关联各业务表的主键',
  'fnd_act_hi_comment.procdefKey': '如CallBack、Presales等',
  'fnd_act_hi_comment.taskKey': 'Activiti任务定义Key',
  'fnd_act_hi_comment.taskId': 'Activiti任务ID',
  'fnd_act_hi_comment.instId': 'Activiti流程实例ID',
  'fnd_act_hi_comment.assignee': '逻辑外键 -> fnd_user_info.username',
  'fnd_act_hi_comment.assigneeTime': '任务办理时间',
  'fnd_act_hi_comment.result': '逻辑外键 -> fnd_basic_data(dataTypeCode=26)',
  'fnd_act_hi_comment.message': '审批意见内容',

  // fnd_basic_data
  'fnd_basic_data.id': '自增主键，基础数据唯一标识',
  'fnd_basic_data.basicDataId': '数据项编码',
  'fnd_basic_data.basicDataName': '数据项名称',
  'fnd_basic_data.dataTypeCode': '逻辑外键 -> fnd_basic_data_type.dataTypeCode',
  'fnd_basic_data.basicDataDesc': '数据项补充描述',

  // fnd_basic_data_type
  'fnd_basic_data_type.id': '自增主键，数据类型唯一标识',
  'fnd_basic_data_type.dataTypeCode': '数据类型唯一编码',
  'fnd_basic_data_type.dataTypeName': '数据类型名称',
  'fnd_basic_data_type.dataTypeDesc': '数据类型描述',

  // fnd_company
  'fnd_company.id': '自增主键，公司唯一标识',
  'fnd_company.compCode': '公司编码',
  'fnd_company.compAbbr': '公司简称',
  'fnd_company.compName': '公司全名',
  'fnd_company.compDesc': '公司描述',

  // fnd_department
  'fnd_department.id': '自增主键，部门唯一标识',
  'fnd_department.departmentNum': '部门编码，全局唯一',
  'fnd_department.departmentName': '部门名称',
  'fnd_department.departmentDesc': '部门描述',
  'fnd_department.parentDepartmentNum': '上级部门编码，用于构建部门层级',

  // fnd_user_info
  'fnd_user_info.id': '自增主键，用户唯一标识',
  'fnd_user_info.username': '用户登录名，全局唯一',
  'fnd_user_info.realName': '用户真实姓名',
  'fnd_user_info.password': '加密后的用户密码',
  'fnd_user_info.email': '用户邮箱',
  'fnd_user_info.phone': '用户电话',
  'fnd_user_info.officeCode': '逻辑外键 -> fnd_department.departmentNum',
  'fnd_user_info.departmentNum': '所属部门编码',
  'fnd_user_info.compId': '逻辑外键 -> fnd_company.id',
  'fnd_user_info.disabled': '0=启用，1=禁用',

  // fnd_files
  'fnd_files.id': '自增主键，文件唯一标识',
  'fnd_files.fileName': '上传文件原始名',
  'fnd_files.filePath': '文件服务器存储路径',
  'fnd_files.fileSize': '文件大小（字节）',
  'fnd_files.fileType': '文件MIME类型',
  'fnd_files.uploadBy': '上传用户编码',
  'fnd_files.uploadTime': '文件上传时间',

  // fnd_roles
  'fnd_roles.id': '自增主键，角色唯一标识',
  'fnd_roles.roleCode': '角色编码，全局唯一',
  'fnd_roles.roleName': '角色名称',
  'fnd_roles.roleDesc': '角色描述',

  // fnd_user_power
  'fnd_user_power.id': '自增主键，权限记录唯一标识',
  'fnd_user_power.username': '逻辑外键 -> fnd_user_info.username',
  'fnd_user_power.powerCode': '权限编码',
  'fnd_user_power.powerValue': '权限值',

  // fnd_mails
  'fnd_mails.id': '自增主键，邮件记录唯一标识',
  'fnd_mails.mailTo': '收件人地址（多个逗号分隔）',
  'fnd_mails.mailCc': '抄送人地址（多个逗号分隔）',
  'fnd_mails.mailSubject': '邮件主题',
  'fnd_mails.mailContent': '邮件正文HTML内容',
  'fnd_mails.sendState': '0=未发送，1=已发送',
  'fnd_mails.sendTime': '邮件发送时间',

  // fnd_data_refresh_log
  'fnd_data_refresh_log.id': '自增主键，刷新日志唯一标识',
  'fnd_data_refresh_log.dataSource': '数据来源（SMS/OA/CRM/SAP/D365）',
  'fnd_data_refresh_log.refreshType': '刷新类型编码',
  'fnd_data_refresh_log.refreshState': '0=失败，1=成功',
  'fnd_data_refresh_log.refreshTime': '数据刷新执行时间',
  'fnd_data_refresh_log.recordCount': '本次刷新处理记录数',
  'fnd_data_refresh_log.errorMsg': '刷新失败时的错误信息',

  // fnd_sys_arg
  'fnd_sys_arg.id': '自增主键，参数唯一标识',
  'fnd_sys_arg.argCode': '系统参数编码',
  'fnd_sys_arg.argName': '系统参数名称',
  'fnd_sys_arg.argValue': '系统参数值',
  'fnd_sys_arg.argDesc': '系统参数描述',

  // fnd_menus
  'fnd_menus.id': '自增主键，菜单唯一标识',
  'fnd_menus.menuCode': '菜单编码',
  'fnd_menus.menuName': '菜单名称',
  'fnd_menus.menuUrl': '菜单访问URL',
  'fnd_menus.parentMenuCode': '父级菜单编码',
  'fnd_menus.sortNo': '菜单排序序号',

  // fnd_role_menus
  'fnd_role_menus.id': '自增主键，角色菜单关联唯一标识',
  'fnd_role_menus.roleCode': '逻辑外键 -> fnd_roles.roleCode',
  'fnd_role_menus.menuCode': '逻辑外键 -> fnd_menus.menuCode',

  // fnd_user_menus
  'fnd_user_menus.id': '自增主键，用户菜单关联唯一标识',
  'fnd_user_menus.username': '逻辑外键 -> fnd_user_info.username',
  'fnd_user_menus.menuCode': '逻辑外键 -> fnd_menus.menuCode',

  // fnd_basic_prjstate
  'fnd_basic_prjstate.id': '自增主键，项目状态唯一标识',
  'fnd_basic_prjstate.stateCode': '项目状态编码',
  'fnd_basic_prjstate.stateName': '项目状态名称',
  'fnd_basic_prjstate.stateDesc': '项目状态描述',

  // fnd_spms_arg
  'fnd_spms_arg.id': '自增主键，SPMS参数唯一标识',
  'fnd_spms_arg.argCode': 'SPMS参数编码',
  'fnd_spms_arg.argName': 'SPMS参数名称',
  'fnd_spms_arg.argValue': 'SPMS参数值',
  'fnd_spms_arg.argDesc': 'SPMS参数描述',
};

// Table business descriptions
const tableDescMap = {
  'pm_project': '项目主表，存储项目核心信息，是整个PMS系统的核心实体',
  'pm_project_contract': '项目对应的合同信息，一个项目组可关联多个合同',
  'pm_project_group': '项目组信息，多个项目编码可归入同一项目组',
  'pm_project_group_relationship': '项目编码与项目组的关联关系，支持项目拆分合并',
  'pm_project_member': '项目相关人员信息，通过memberRole区分角色(10=销售,20=服务经理,30=项目经理)',
  'pm_project_state': '项目各维度状态信息（工程计划/发货/实施/闭环），以projectId为主键',
  'pm_project_task': '项目具体任务，支持树形结构(parentId)，关联Activiti工作流',
  'pm_project_related_party': '项目相关的团体信息（渠道商、代理商、服务商等）',
  'pm_project_product_line': '订单产品信息，记录项目下的产品明细',
  'pm_project_shipment': '项目发货记录，支持串货转移',
  'pm_project_maintenance': '项目维护/巡检记录，记录售后服务的详细过程',
  'pm_project_log': '项目主要操作跟踪日志',
  'pm_project_instruction': '总部或领导对项目的批示及反馈',
  'pm_project_supervision': '项目督查头信息，记录督查任务',
  'pm_project_warranty_callback': '项目维保回访问卷表，记录维保回访详情',
  'pm_project_weekly': '项目周报主表',
  'pm_project_weekly_content': '项目周报详细内容',
  'pm_project_soft_version': '项目设备软件版本信息，记录conp/cpld/boot/pcb等版本',
  'pm_project_notification': '项目通知信息',
  'pm_project_notification_state': '通知的阅读状态记录',
  'pm_project_header_view_cache': 'pm_project_header视图的物化缓存表，加速项目列表查询',
  'pm_cl_callback': '运营商直签项目回访申请主表，关联Activiti工作流',
  'pm_cl_callback_quesnaire': '回访与问卷的关联表，一次回访可关联多个问卷版本',
  'pm_cl_evaluation_header': '客户评价表头，记录评价基本信息',
  'pm_cl_quesnaire_result_header': '问卷结果头表，记录一次问卷填写的结果',
  'pm_cl_quesnaire_result_line': '问卷结果行表，记录每个问题的回答',
  'pm_cl_quesnaire_template_header': '问卷模板定义头表',
  'pm_cl_quesnaire_template_line': '问卷模板题目定义',
  'pm_cl_quesnaire_template_options': '问卷模板题目选项',
  'pm_presales_project_header': '售前测试项目主表，关联Activiti工作流',
  'pm_presales_project_product_line': '售前项目产品明细（数据量最大的表）',
  'pm_presales_project_callback': '售前项目回访记录',
  'pm_presales_project_duration': '售前项目各阶段耗时统计',
  'pm_presales_project_rma_info': '售前项目RMA（退货授权）信息',
  'pm_subcontract_project_header': '转包项目主表，记录转包项目基本信息',
  'pm_subcontract_project_line': '转包项目明细行',
  'pm_subcontract_facilitator': '转包服务商信息',
  'pm_subcontract_project_payment': '转包项目付款记录',
  'pm_subcontract_deliver_files': '转包项目交付件文件',
  'prob_main': '技术公告/问题主表，使用bitMark位运算进行多值筛选',
  'prob_product': '技术公告关联的产品信息',
  'prob_softwares': '技术公告关联的软件版本信息，用于版本范围匹配',
  'prob_restore': '技术公告恢复/修复方案记录',
  'prob_read_log': '技术公告阅读记录',
  'fnd_act_hi_comment': 'Activiti工作流审批意见记录，被各业务模块共用',
  'fnd_basic_data': '系统基础数据字典，通过dataTypeCode区分不同数据类型',
  'fnd_basic_data_type': '基础数据类型定义',
  'fnd_company': '公司/组织机构信息',
  'fnd_department': '部门/办事处信息',
  'fnd_user_info': '系统用户信息',
  'fnd_files': '文件上传记录',
  'fnd_roles': '系统角色定义',
  'fnd_user_power': '用户权限配置',
  'fnd_mails': '系统发送的邮件记录',
  'fnd_data_refresh_log': '外部系统数据同步刷新日志',
  'fnd_sys_arg': '系统参数配置',
  'fnd_menus': '系统菜单定义',
  'fnd_role_menus': '角色与菜单的关联关系',
  'fnd_user_menus': '用户与菜单的关联关系',
  'fnd_basic_prjstate': '项目状态基础配置',
  'fnd_spms_arg': 'SPMS系统参数配置',
};

// Domain groupings
const domainTables = {
  'pm_project': ['pm_project', 'pm_project_contract', 'pm_project_group', 'pm_project_group_relationship', 'pm_project_member', 'pm_project_state', 'pm_project_task', 'pm_project_related_party', 'pm_project_product_line', 'pm_project_shipment', 'pm_project_maintenance', 'pm_project_log', 'pm_project_instruction', 'pm_project_supervision', 'pm_project_warranty_callback', 'pm_project_weekly', 'pm_project_weekly_content', 'pm_project_soft_version', 'pm_project_notification', 'pm_project_notification_state', 'pm_project_header_view_cache'],
  'pm_cl': ['pm_cl_callback', 'pm_cl_callback_quesnaire', 'pm_cl_evaluation_header', 'pm_cl_quesnaire_result_header', 'pm_cl_quesnaire_result_line', 'pm_cl_quesnaire_template_header', 'pm_cl_quesnaire_template_line', 'pm_cl_quesnaire_template_options'],
  'pm_presales': ['pm_presales_project_header', 'pm_presales_project_product_line', 'pm_presales_project_callback', 'pm_presales_project_duration', 'pm_presales_project_rma_info'],
  'pm_subcontract': ['pm_subcontract_project_header', 'pm_subcontract_project_line', 'pm_subcontract_facilitator', 'pm_subcontract_project_payment', 'pm_subcontract_deliver_files'],
  'prob': ['prob_main', 'prob_product', 'prob_softwares', 'prob_restore', 'prob_read_log'],
  'fnd': ['fnd_act_hi_comment', 'fnd_basic_data', 'fnd_basic_data_type', 'fnd_company', 'fnd_department', 'fnd_user_info', 'fnd_files', 'fnd_roles', 'fnd_user_power', 'fnd_mails', 'fnd_data_refresh_log', 'fnd_sys_arg', 'fnd_menus', 'fnd_role_menus', 'fnd_user_menus', 'fnd_basic_prjstate', 'fnd_spms_arg'],
};

const domainNames = {
  'pm_project': '项目管理域',
  'pm_cl': '回访管理域',
  'pm_presales': '售前管理域',
  'pm_subcontract': '转包管理域',
  'prob': '问题管理域',
  'fnd': '基础平台域',
};

const domainPrefixes = {
  'pm_project': 'pm_project',
  'pm_cl': 'pm_cl',
  'pm_presales': 'pm_presales',
  'pm_subcontract': 'pm_subcontract',
  'prob': 'prob',
  'fnd': 'fnd',
};

function getMeaning(tableName, fieldName, columnComment) {
  const key = tableName + '.' + fieldName;
  if (meaningMap[key]) return meaningMap[key];
  if (meaningMap[fieldName]) return meaningMap[fieldName];
  // Fallback: use column comment or infer from field name
  if (columnComment && columnComment.trim()) return columnComment.trim();
  return '业务含义待确认';
}

function formatSize(bytes) {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
}

function generateTableSection(tableName, index) {
  const meta = tableMeta[tableName];
  const cols = columnsByTable[tableName] || [];
  const idxs = indexesByTable[tableName] || {};
  const desc = tableDescMap[tableName] || '业务含义待确认';

  let lines = [];
  lines.push('### ' + index + ' ' + tableName + ' -- ' + (desc.split('，')[0]));
  lines.push('');
  lines.push('| 属性 | 值 |');
  lines.push('|------|-----|');
  lines.push('| 对象类型 | BASE TABLE |');
  lines.push('| 业务含义 | ' + desc + ' |');
  if (meta) {
    lines.push('| 数据量 | ~' + meta.TABLE_ROWS.toLocaleString() + ' 行 |');
    lines.push('| 数据大小 | ' + formatSize(meta.DATA_LENGTH) + ' |');
  }
  lines.push('');
  lines.push('**字段列表**');
  lines.push('');
  lines.push('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |');
  lines.push('|--------|----------|------|--------|------|----------|----------|');

  for (const col of cols) {
    const nullable = col.IS_NULLABLE === 'YES' ? 'YES' : 'NO';
    const defaultVal = col.COLUMN_DEFAULT !== null ? col.COLUMN_DEFAULT : '-';
    const constraint = [col.COLUMN_KEY, col.EXTRA].filter(x => x && x.trim()).join(', ') || '';
    const comment = col.COLUMN_COMMENT || '';
    const meaning = getMeaning(tableName, col.COLUMN_NAME, comment);

    lines.push('| ' + col.COLUMN_NAME + ' | ' + col.COLUMN_TYPE + ' | ' + nullable + ' | ' + defaultVal + ' | ' + constraint + ' | ' + comment + ' | ' + meaning + ' |');
  }

  // Index section
  const idxList = Object.values(idxs);
  if (idxList.length > 0) {
    lines.push('');
    lines.push('**索引列表**');
    lines.push('');
    lines.push('| 索引名 | 索引类型 | 唯一性 | 索引字段 |');
    lines.push('|--------|----------|--------|----------|');
    for (const idx of idxList) {
      lines.push('| ' + idx.name + ' | ' + idx.type + ' | ' + (idx.unique ? 'UNIQUE' : 'NON-UNIQUE') + ' | ' + idx.columns.join(', ') + ' |');
    }
  }

  lines.push('');
  lines.push('---');
  lines.push('');
  return lines.join('\n');
}

// Generate the complete document
let doc = [];
doc.push('# DPPMS D365 全量数据字典');
doc.push('');
doc.push('> 数据库: dppms_d365 | 生成时间: 2026-06-12 | 数据基准: 生产环境 information_schema 实时查询');
doc.push('> 业务含义来源: Java Bean 注释 + iBatis SQL映射 + 字段命名推断');
doc.push('');
doc.push('---');
doc.push('');
doc.push('## 目录');
doc.push('');

const domainKeys = ['pm_project', 'pm_cl', 'pm_presales', 'pm_subcontract', 'prob', 'fnd'];
const domainAnchors = {
  'pm_project': '一项目管理域-pm_project',
  'pm_cl': '二回访管理域-pm_cl',
  'pm_presales': '三售前管理域-pm_presales',
  'pm_subcontract': '四转包管理域-pm_subcontract',
  'prob': '五问题管理域-prob',
  'fnd': '六基础平台域-fnd',
};

for (const dk of domainKeys) {
  doc.push('- [' + domainNames[dk] + ' (' + domainPrefixes[dk] + '*)](#' + domainAnchors[dk] + ')');
}

doc.push('');
doc.push('---');
doc.push('');

let sectionNum = 0;
for (const dk of domainKeys) {
  sectionNum++;
  doc.push('## ' + '一二三四五六'[sectionNum - 1] + '、' + domainNames[dk] + ' (' + domainPrefixes[dk] + ')');
  doc.push('');

  const tList = domainTables[dk];
  for (let i = 0; i < tList.length; i++) {
    const idx = sectionNum + '.' + (i + 1);
    doc.push(generateTableSection(tList[i], idx));
  }
}

// Appendices
doc.push('## 附录A: 数据量TOP 10表');
doc.push('');
doc.push('| 排名 | 表名 | 行数 | 数据大小 |');
doc.push('|------|------|------|----------|');

const allDomainTableNames = new Set(Object.values(domainTables).flat());
const sortedTables = Object.values(tableMeta)
  .filter(t => allDomainTableNames.has(t.TABLE_NAME))
  .sort((a, b) => b.TABLE_ROWS - a.TABLE_ROWS);
for (let i = 0; i < Math.min(10, sortedTables.length); i++) {
  const t = sortedTables[i];
  doc.push('| ' + (i + 1) + ' | ' + t.TABLE_NAME + ' | ' + t.TABLE_ROWS.toLocaleString() + ' | ' + formatSize(t.DATA_LENGTH) + ' |');
}

doc.push('');
doc.push('---');
doc.push('');
doc.push('## 附录B: customInfo JSON字段常用Key');
doc.push('');
doc.push('以下Key存储在各表的customInfo JSON字段中：');
doc.push('');
doc.push('| 表名 | Key | 含义 |');
doc.push('|------|-----|------|');
doc.push('| pm_project | serviceManagerCode | 服务经理编码 |');
doc.push('| pm_project | programManagerCode | 项目经理编码 |');
doc.push('| pm_project | programManagerCodeB | 第二项目经理编码 |');
doc.push('| pm_project | smsProjectAmount | SMS项目金额 |');
doc.push('| pm_project | salesManCode | 销售人员编码 |');
doc.push('| pm_subcontract_project_header | parentOfficeCode | 上级办事处编码 |');
doc.push('| prob_main | relatedSceneTypes | 关联场景类型列表 |');
doc.push('| prob_main | mitigationActionTypes | 规避方案操作类型列表 |');
doc.push('| prob_main | solutionActionTypes | 解决方案操作类型列表 |');
doc.push('');
doc.push('---');
doc.push('');
doc.push('## 附录C: bitMark位运算模式说明');
doc.push('');
doc.push('prob_main表使用bitMark位运算实现多值筛选，原理如下：');
doc.push('');
doc.push('- `relatedSceneTypes`: 存储逗号分隔的场景类型值（如"1,2,4"）');
doc.push('- `relatedSceneTypesMark`: 将场景类型值进行位或运算得到的长整型（如 1|2|4 = 7）');
doc.push('- 查询时使用位与运算：`relatedSceneTypesMark & #{mark} > 0` 判断是否包含指定场景');
doc.push('');
doc.push('此模式同样适用于 `mitigationActionTypesMark` 和 `solutionActionTypesMark`。');
doc.push('');
doc.push('---');
doc.push('');
doc.push('## 附录D: customInfo更新策略差异');
doc.push('');
doc.push('| 表名 | 更新策略 | SQL模式 |');
doc.push('|------|----------|---------|');
doc.push('| pm_subcontract_project_header | 增量合并 | `JSON_MERGE_PATCH(IFNULL(customInfo, "{}"), #{customInfo:JSON})` |');
doc.push('| pm_project | 直接赋值 | `customInfo = #{customInfo:JSON}` |');
doc.push('| pm_project_maintenance | 直接赋值 | `customInfo = #{customInfo:JSON}` |');
doc.push('');
doc.push('注意：增量合并策略只更新传入的Key，不影响其他Key；直接赋值策略会整体替换customInfo内容。');
doc.push('');
doc.push('---');
doc.push('');
doc.push('*文档结束 - 数据基准时间: 2026-06-12 | 数据库: dppms_d365*');

const outputPath = 'd:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\docs\\03-database\\database_dict_part1.md';
fs.writeFileSync(outputPath, doc.join('\n'), 'utf8');

// Count empty meanings
let emptyCount = 0;
for (const line of doc) {
  if (line.endsWith('| 业务含义待确认 |')) emptyCount++;
}
console.log('File generated. Empty meanings: ' + emptyCount);
console.log('Total lines: ' + doc.length);
