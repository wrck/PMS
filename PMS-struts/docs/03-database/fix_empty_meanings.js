const fs = require('fs');
const filePath = 'd:\\EclipseWorkspace\\Parctice\\PMS\\PMS-struts\\docs\\03-database\\database_dict_part1.md';
let content = fs.readFileSync(filePath, 'utf8');
let lines = content.split('\n');
let currentTable = '';
let fixedCount = 0;

for (let i = 0; i < lines.length; i++) {
  const line = lines[i];

  // Track current table name
  const tableMatch = line.match(/^### \d+\.\d+ (\S+) --/);
  if (tableMatch) {
    currentTable = tableMatch[1];
  }

  // Check if this is a field row with empty business meaning (ends with '| |')
  if (line.endsWith('| |') && line.startsWith('|')) {
    const parts = line.split('|');
    if (parts.length >= 8) {
      const fieldName = parts[1].trim();
      const fieldDesc = parts[6] ? parts[6].trim() : '';
      let meaning = '';

      // Common audit fields - universal
      if (fieldName === 'id') { meaning = '自增主键，记录唯一标识'; }
      else if (fieldName === 'createTime' || fieldDesc === '创建时间') { meaning = '记录创建时间'; }
      else if (fieldName === 'createBy' || fieldDesc === '创建人' || fieldDesc === '创建用户') { meaning = '记录创建用户编码'; }
      else if (fieldName === 'updateTime' || fieldDesc === '修改时间' || fieldDesc === '最新更新时间') { meaning = '记录最新更新时间'; }
      else if (fieldName === 'updateBy' || fieldDesc === '修改人' || fieldDesc === '最新更新用户') { meaning = '记录最新更新用户编码'; }
      else if (fieldName === 'effectiveFrom' || fieldDesc === '生效时间_起' || fieldDesc === '有效开始时间') { meaning = '数据有效性开始时间（软删除模式）'; }
      else if (fieldName === 'effectiveTo' || fieldDesc === '生效时间_止' || fieldDesc === '有效结束时间') { meaning = '数据有效性结束时间，NULL=当前有效'; }
      else if (fieldName === 'disabled') { meaning = '0=有效，1=失效'; }
      else if (fieldName === 'customInfo') { meaning = 'JSON扩展字段，存储动态属性'; }

      // pm_project_group
      else if (fieldName === 'projectGroupName') { meaning = '项目组的业务名称'; }

      // pm_project_member
      else if (fieldName === 'memberName') { meaning = '项目成员的真实姓名'; }
      else if (fieldName === 'phoneNum') { meaning = '项目成员联系电话'; }
      else if (fieldName === 'email') { meaning = '项目成员邮箱地址'; }

      // pm_project_state
      else if (fieldName === 'projectplanTime') { meaning = '工程计划状态最后变更时间'; }
      else if (fieldName === 'shipmentTime') { meaning = '发货状态最后变更时间'; }
      else if (fieldName === 'executionStateTime') { meaning = '实施状态最后变更时间'; }
      else if (fieldName === 'closeProcessStateTime') { meaning = '闭环流程状态最后变更时间'; }

      // pm_project_task
      else if (fieldName === 'taskId' && currentTable === 'pm_project_task') { meaning = '任务自增主键，任务唯一标识'; }
      else if (fieldName === 'contractNo' && currentTable === 'pm_project_task') { meaning = '关联合同编号'; }
      else if (fieldName === 'taskName') { meaning = '任务业务名称'; }
      else if (fieldName === 'eventPlanHappenDate') { meaning = '款项计划发生日期'; }
      else if (fieldName === 'eventPlanHappenDateENG') { meaning = '工程计划发生日期'; }
      else if (fieldName === 'planStartTime') { meaning = '任务计划开始时间'; }
      else if (fieldName === 'planEndTime') { meaning = '任务计划结束时间'; }
      else if (fieldName === 'actualStartTime') { meaning = '任务实际开始时间'; }
      else if (fieldName === 'eventActualFinishDate') { meaning = '任务实际完成日期'; }
      else if (fieldName === 'priority') { meaning = '任务优先级'; }
      else if (fieldName === 'progressDesc') { meaning = '任务进度文字描述'; }
      else if (fieldName === 'status') { meaning = '任务状态，0=未开始'; }
      else if (fieldName === 'remark') { meaning = '备注说明'; }

      // pm_project_related_party
      else if (fieldName === 'partyCode') { meaning = '相关方（渠道商/代理商）编码'; }
      else if (fieldName === 'partyName') { meaning = '相关方（渠道商/代理商）名称'; }

      // pm_project_product_line
      else if (fieldName === 'itemCode' && currentTable === 'pm_project_product_line') { meaning = 'ERP系统产品编码'; }
      else if (fieldName === 'itemName' && currentTable === 'pm_project_product_line') { meaning = '产品名称'; }
      else if (fieldName === 'projectQuantity') { meaning = '项目产品总数量'; }
      else if (fieldName === 'orderQuantity') { meaning = '已下单产品数量'; }
      else if (fieldName === 'deliverQuantity') { meaning = '已发货产品数量'; }
      else if (fieldName === 'openQuantity') { meaning = '未发货产品数量'; }
      else if (fieldName === 'orderNumber') { meaning = 'ERP订单号'; }
      else if (fieldName === 'lineNum') { meaning = '订单行号'; }

      // pm_project_shipment
      else if (fieldName === 'itemCode' && currentTable === 'pm_project_shipment') { meaning = '发货产品编码'; }
      else if (fieldName === 'itemModel' && currentTable === 'pm_project_shipment') { meaning = '产品型号'; }
      else if (fieldName === 'itemName' && currentTable === 'pm_project_shipment') { meaning = '产品名称'; }
      else if (fieldName === 'receiveName') { meaning = '收货人姓名'; }
      else if (fieldName === 'emsNum') { meaning = '快递/物流单号'; }
      else if (fieldName === 'emsCompany') { meaning = '快递/物流公司名称'; }
      else if (fieldName === 'packdate') { meaning = '设备打包日期'; }
      else if (fieldName === 'contractNo' && currentTable === 'pm_project_shipment') { meaning = '关联合同编号'; }
      else if (fieldName === 'installAddress') { meaning = '设备安装地址'; }
      else if (fieldName === 'chProjectId') { meaning = '串货转移前所属项目ID'; }
      else if (fieldName === 'chContractNo') { meaning = '串货转移前合同编号'; }
      else if (fieldName === 'transferProjectId') { meaning = '串货转移后目标项目ID'; }
      else if (fieldName === 'transferContractNo') { meaning = '串货转移后合同编号'; }

      // pm_project_maintenance
      else if (fieldName === 'projectCode' && currentTable === 'pm_project_maintenance') { meaning = '项目编码，冗余存储'; }
      else if (fieldName === 'projectName' && currentTable === 'pm_project_maintenance') { meaning = '项目名称，冗余存储'; }
      else if (fieldName === 'projectExecutionState') { meaning = '项目当前实施状态'; }
      else if (fieldName === 'contractNo' && currentTable === 'pm_project_maintenance') { meaning = '关联合同编号'; }
      else if (fieldName === 'type') { meaning = '服务任务性质编码'; }
      else if (fieldName === 'category') { meaning = '服务任务分类编码'; }
      else if (fieldName === 'subCategory') { meaning = '服务任务小类编码'; }
      else if (fieldName === 'processTime') { meaning = '服务处理时间'; }
      else if (fieldName === 'processDesc') { meaning = '服务事项描述'; }
      else if (fieldName === 'processStep') { meaning = '问题解决进展'; }
      else if (fieldName === 'remainProblem') { meaning = '遗留问题描述'; }
      else if (fieldName === 'transitHour') { meaning = '在途耗时（小时）'; }
      else if (fieldName === 'processHour') { meaning = '处理耗时（小时）'; }
      else if (fieldName === 'itemModel' && currentTable === 'pm_project_maintenance') { meaning = '服务产品型号'; }
      else if (fieldName === 'softVersion') { meaning = '设备在网软件版本'; }
      else if (fieldName === 'enabledFeatures') { meaning = '设备启用功能列表'; }
      else if (fieldName === 'customTos') { meaning = '自定义邮件主送人'; }
      else if (fieldName === 'customCcs') { meaning = '自定义邮件抄送人'; }
      else if (fieldName === 'hasReport') { meaning = '0=无巡检报告，1=有巡检报告'; }
      else if (fieldName === 'warrantyStatus') { meaning = '项目维保状态'; }
      else if (fieldName === 'industryName') { meaning = '客户所属行业'; }
      else if (fieldName === 'userOffice') { meaning = '用户所属办事处'; }
      else if (fieldName === 'year') { meaning = '服务记录所属年度'; }
      else if (fieldName === 'quarter') { meaning = '服务记录所属季度(1-4)'; }
      else if (fieldName === 'month') { meaning = '服务记录所属月份(1-12)'; }
      else if (fieldName === 'wsCount') { meaning = '当前维保服务次数'; }
      else if (fieldName === 'wafCount') { meaning = '当前其他服务次数'; }
      else if (fieldName === 'wsYearCount') { meaning = '年度维保服务累计次数'; }
      else if (fieldName === 'wafYearCount') { meaning = '年度其他服务累计次数'; }
      else if (fieldName === 'warrantyInfo') { meaning = '维保服务详细信息'; }
      else if (fieldName === 'serviceInfo') { meaning = '其他服务详细信息'; }

      // pm_project_log
      else if (fieldName === 'handleName') { meaning = '操作名称（如：指定项目经理）'; }
      else if (fieldName === 'handleDesc') { meaning = '操作描述或原因说明'; }
      else if (fieldName === 'handleUser') { meaning = '执行操作的用户编码'; }
      else if (fieldName === 'taskStartTime') { meaning = '操作开始时间'; }
      else if (fieldName === 'handleEndTime') { meaning = '操作结束时间'; }

      // pm_project_instruction
      else if (fieldName === 'instructionsInfo') { meaning = '批示/反馈的具体内容'; }
      else if (fieldName === 'instructionsTime') { meaning = '批示/反馈的时间'; }
      else if (fieldName === 'instructionsUser') { meaning = '批示/反馈的用户编码'; }
      else if (fieldName === 'instructionsId') { meaning = '反馈对应的原始批示记录ID'; }

      // pm_project_supervision
      else if (fieldName === 'projectCode' && currentTable === 'pm_project_supervision') { meaning = '项目编码'; }
      else if (fieldName === 'projectName' && currentTable === 'pm_project_supervision') { meaning = '项目名称'; }
      else if (fieldName === 'channel') { meaning = '代理商/服务商名称'; }
      else if (fieldName === 'officeCode' && currentTable === 'pm_project_supervision') { meaning = '办事处编码'; }
      else if (fieldName === 'type' && currentTable === 'pm_project_supervision') { meaning = '督查任务性质'; }
      else if (fieldName === 'processTime' && currentTable === 'pm_project_supervision') { meaning = '督查处理时间'; }
      else if (fieldName === 'state') { meaning = '0=未完成，1=已完成'; }
      else if (fieldName === 'isDelete') { meaning = '0=未删除，1=已删除'; }

      // pm_project_warranty_callback
      else if (fieldName === 'projectCode' && currentTable === 'pm_project_warranty_callback') { meaning = '项目编码'; }
      else if (fieldName === 'officeCode' && currentTable === 'pm_project_warranty_callback') { meaning = '办事处编码'; }
      else if (fieldName === 'contractNos') { meaning = '关联合同编号（多个逗号分隔）'; }
      else if (fieldName === 'projectIds') { meaning = '关联项目ID（多个逗号分隔）'; }
      else if (fieldName === 'projectName' && currentTable === 'pm_project_warranty_callback') { meaning = '项目名称'; }
      else if (fieldName === 'serviceImpl') { meaning = '项目实施方式编码'; }
      else if (fieldName === 'industryName' && currentTable === 'pm_project_warranty_callback') { meaning = '客户所属行业'; }
      else if (fieldName === 'agentChannel') { meaning = '下单代理商名称'; }
      else if (fieldName === 'finalCustomerName') { meaning = '最终客户单位名称'; }
      else if (fieldName === 'customer1') { meaning = '客户联系人1姓名'; }
      else if (fieldName === 'customerContact1') { meaning = '客户联系人1联系方式'; }
      else if (fieldName === 'customer2') { meaning = '客户联系人2姓名'; }
      else if (fieldName === 'customerContact2') { meaning = '客户联系人2联系方式'; }
      else if (fieldName === 'warrantyStartTime') { meaning = '维保合同开始日期'; }
      else if (fieldName === 'warrantyEndTime') { meaning = '维保合同结束日期'; }
      else if (fieldName === 'quesnaireVersion') { meaning = '问卷模板版本号'; }
      else if (fieldName === 'quesnaireState' && currentTable === 'pm_project_warranty_callback') { meaning = '-1=草稿，1=已提交'; }
      else if (fieldName === 'isDelete' && currentTable === 'pm_project_warranty_callback') { meaning = '0=未删除，1=已删除'; }
      else if (fieldName === 'compId' && currentTable === 'pm_project_warranty_callback') { meaning = '所属公司ID'; }

      // pm_project_weekly
      else if (fieldName === 'weeklyId') { meaning = '周报自增主键'; }
      else if (fieldName === 'currentTask') { meaning = '当前工程阶段名称'; }
      else if (fieldName === 'taskStartTime' && currentTable === 'pm_project_weekly') { meaning = '当前阶段开始时间'; }
      else if (fieldName === 'taskEndTime') { meaning = '当前阶段结束时间'; }
      else if (fieldName === 'taskDeviation') { meaning = '进度偏差说明'; }
      else if (fieldName === 'weeklyStartTime') { meaning = '周报统计周期开始时间'; }
      else if (fieldName === 'weeklyEndTime') { meaning = '周报统计周期结束时间'; }

      // pm_project_weekly_content
      else if (fieldName === 'optionDesc001') { meaning = '周报选项描述1（工作内容）'; }
      else if (fieldName === 'optionDesc002') { meaning = '周报选项描述2（下周计划）'; }
      else if (fieldName === 'optionType') { meaning = '选项类型，对应周报不同部分'; }

      // pm_project_soft_version
      else if (fieldName === 'contractNo' && currentTable === 'pm_project_soft_version') { meaning = '关联合同编号'; }
      else if (fieldName === 'itemCode' && currentTable === 'pm_project_soft_version') { meaning = '产品编码'; }
      else if (fieldName === 'conpType') { meaning = 'CONP版本类型'; }
      else if (fieldName === 'conpSeries') { meaning = 'CONP版本系列'; }
      else if (fieldName === 'conpMark') { meaning = '软件版本掩码，用于版本范围匹配'; }
      else if (fieldName === 'conpBak') { meaning = 'CONP变更前备份版本号'; }
      else if (fieldName === 'conpChange') { meaning = '0=CONP无更新，1=CONP有更新'; }
      else if (fieldName === 'cpld' && currentTable === 'pm_project_soft_version') { meaning = 'CPLD版本号'; }
      else if (fieldName === 'cpldBak') { meaning = 'CPLD变更前备份版本号'; }
      else if (fieldName === 'cpldChange') { meaning = '0=CPLD无更新，1=CPLD有更新'; }
      else if (fieldName === 'boot' && currentTable === 'pm_project_soft_version') { meaning = 'Boot版本号'; }
      else if (fieldName === 'bootBak') { meaning = 'Boot变更前备份版本号'; }
      else if (fieldName === 'bootChange') { meaning = '0=Boot无更新，1=Boot有更新'; }
      else if (fieldName === 'pcb' && currentTable === 'pm_project_soft_version') { meaning = 'PCB版本号'; }
      else if (fieldName === 'pcbBak') { meaning = 'PCB变更前备份版本号'; }
      else if (fieldName === 'pcbChange') { meaning = '0=PCB无更新，1=PCB有更新'; }
      else if (fieldName === 'executeTime') { meaning = '版本更新执行日期'; }
      else if (fieldName === 'datastate') { meaning = '0=失效，1=有效'; }

      // pm_project_notification
      else if (fieldName === 'notifySubject') { meaning = '通知标题'; }
      else if (fieldName === 'notifyContent') { meaning = '通知正文内容'; }

      // pm_project_notification_state
      else if (fieldName === 'notifyObject') { meaning = '通知接收用户编码'; }
      else if (fieldName === 'checkTime') { meaning = '用户查看通知的时间'; }

      // pm_project_header_view_cache
      else if (fieldName === 'projectCode' && currentTable === 'pm_project_header_view_cache') { meaning = '项目编码'; }
      else if (fieldName === 'subProjectCode') { meaning = '子项目/合同级别编码'; }
      else if (fieldName === 'projectName' && currentTable === 'pm_project_header_view_cache') { meaning = '项目名称'; }
      else if (fieldName === 'contractNo' && currentTable === 'pm_project_header_view_cache') { meaning = '合同编号'; }
      else if (fieldName === 'majorProjectLevel' && currentTable === 'pm_project_header_view_cache') { meaning = '重大项目级别'; }
      else if (fieldName === 'officeName') { meaning = '办事处名称（冗余）'; }
      else if (fieldName === 'customerName' && currentTable === 'pm_project_header_view_cache') { meaning = '客户名称（冗余）'; }
      else if (fieldName === 'marketName' && currentTable === 'pm_project_header_view_cache') { meaning = '市场部名称（冗余）'; }
      else if (fieldName === 'systemName' && currentTable === 'pm_project_header_view_cache') { meaning = '系统部名称（冗余）'; }
      else if (fieldName === 'expendName' && currentTable === 'pm_project_header_view_cache') { meaning = '拓展部名称（冗余）'; }
      else if (fieldName === 'industryName' && currentTable === 'pm_project_header_view_cache') { meaning = '行业名称（冗余）'; }
      else if (fieldName === 'salesManCode') { meaning = '销售人员编码'; }
      else if (fieldName === 'salesManName') { meaning = '销售人员姓名'; }
      else if (fieldName === 'salesManTel') { meaning = '销售人员电话'; }
      else if (fieldName === 'salesManMail') { meaning = '销售人员邮箱'; }
      else if (fieldName === 'smCode') { meaning = '服务经理编码'; }
      else if (fieldName === 'smName') { meaning = '服务经理姓名'; }
      else if (fieldName === 'pmCode1') { meaning = '项目经理1编码'; }
      else if (fieldName === 'pmName1') { meaning = '项目经理1姓名'; }
      else if (fieldName === 'pmCode2') { meaning = '项目经理2编码'; }
      else if (fieldName === 'pmName2') { meaning = '项目经理2姓名'; }
      else if (fieldName === 'compId' && currentTable === 'pm_project_header_view_cache') { meaning = '公司ID'; }
      else if (fieldName === 'compName') { meaning = '公司名称（冗余）'; }
      else if (fieldName === 'ssfsName') { meaning = '实施方式名称'; }
      else if (fieldName === 'partnerChannel') { meaning = '合作伙伴渠道名称'; }
      else if (fieldName === 'projectType' && currentTable === 'pm_project_header_view_cache') { meaning = '项目类型编码'; }
      else if (fieldName === 'finalCustomerName' && currentTable === 'pm_project_header_view_cache') { meaning = '最终客户名称'; }
      else if (fieldName === 'customerProjectName' && currentTable === 'pm_project_header_view_cache') { meaning = '客户项目名称'; }

      // pm_cl_callback
      else if (fieldName === 'remark' && currentTable === 'pm_cl_callback') { meaning = '回访申请备注'; }
      else if (fieldName === 'applyBy') { meaning = '回访申请人编码'; }
      else if (fieldName === 'applyTime') { meaning = '回访申请时间'; }

      // pm_cl_callback_quesnaire
      else if (fieldName === 'quesnaireVersion' && currentTable === 'pm_cl_callback_quesnaire') { meaning = '问卷模板版本号'; }

      // pm_cl_evaluation_header
      else if (fieldName === 'evaluationName') { meaning = '评价名称/标题'; }
      else if (fieldName === 'evaluationDesc') { meaning = '评价详细描述'; }
      else if (fieldName === 'nextAcceptPerson') { meaning = '下一处理人编码'; }
      else if (fieldName === 'nextAcceptPersonName') { meaning = '下一处理人姓名'; }
      else if (fieldName === 'evaluationState') { meaning = '评价状态'; }

      // pm_cl_quesnaire_result_header
      else if (fieldName === 'totalScore') { meaning = '问卷填写总得分'; }
      else if (fieldName === 'passScore') { meaning = '问卷及格分数线'; }

      // pm_cl_quesnaire_result_line
      else if (fieldName === 'score') { meaning = '该题实际得分'; }
      else if (fieldName === 'resultDesc') { meaning = '回答内容/结果描述'; }

      // pm_cl_quesnaire_template_header
      else if (fieldName === 'questionnaireTemplateName') { meaning = '问卷模板名称'; }
      else if (fieldName === 'questionnaireTemplateNum') { meaning = '问卷模板编号'; }
      else if (fieldName === 'questionnaireScore') { meaning = '问卷满分'; }
      else if (fieldName === 'questionnairePassScore') { meaning = '问卷及格分'; }
      else if (fieldName === 'questionnaireStatus') { meaning = '模板状态，0=禁用，1=启用'; }
      else if (fieldName === 'quesType') { meaning = '问卷业务类型'; }
      else if (fieldName === 'markIndexs') { meaning = '标记索引，用于模板配置'; }
      else if (fieldName === 'createdPerson') { meaning = '模板创建人'; }
      else if (fieldName === 'createdTime') { meaning = '模板创建时间'; }
      else if (fieldName === 'updatedPerson') { meaning = '模板最后修改人'; }
      else if (fieldName === 'updatedTime') { meaning = '模板最后修改时间'; }
      else if (fieldName === 'effectiveStartTime') { meaning = '模板生效开始时间'; }
      else if (fieldName === 'effectiveEndTime') { meaning = '模板生效结束时间'; }

      // pm_cl_quesnaire_template_line
      else if (fieldName === 'questionDesc') { meaning = '题目描述文本'; }
      else if (fieldName === 'questionType') { meaning = '题目类型（单选/多选/文本）'; }
      else if (fieldName === 'questionScore') { meaning = '题目分值'; }
      else if (fieldName === 'sortNo') { meaning = '排序序号'; }
      else if (fieldName === 'required') { meaning = '0=非必填，1=必填'; }

      // pm_cl_quesnaire_template_options
      else if (fieldName === 'optionDesc') { meaning = '选项描述文本'; }
      else if (fieldName === 'optionScore') { meaning = '选择该选项的得分'; }

      // pm_presales_project_header
      else if (fieldName === 'presalesId') { meaning = '售前项目自增主键'; }
      else if (fieldName === 'presalesCode') { meaning = '售前项目唯一编码'; }
      else if (fieldName === 'projectCode' && currentTable === 'pm_presales_project_header') { meaning = '关联的原售后项目编码'; }
      else if (fieldName === 'projectName' && currentTable === 'pm_presales_project_header') { meaning = '项目名称'; }
      else if (fieldName === 'projectState' && currentTable === 'pm_presales_project_header') { meaning = '项目状态编码'; }
      else if (fieldName === 'marketName' && currentTable === 'pm_presales_project_header') { meaning = '市场部名称'; }
      else if (fieldName === 'systemName' && currentTable === 'pm_presales_project_header') { meaning = '系统部名称'; }
      else if (fieldName === 'expendName' && currentTable === 'pm_presales_project_header') { meaning = '拓展部名称'; }
      else if (fieldName === 'industryName' && currentTable === 'pm_presales_project_header') { meaning = '行业名称'; }
      else if (fieldName === 'salesman') { meaning = '销售人员姓名'; }
      else if (fieldName === 'salesmanLink') { meaning = '销售人员联系方式'; }
      else if (fieldName === 'productManager') { meaning = '产品经理姓名'; }
      else if (fieldName === 'lendfiles') { meaning = '借货交付件信息'; }
      else if (fieldName === 'hasTransfer') { meaning = '是否存在借转销数据'; }
      else if (fieldName === 'hasRma') { meaning = '是否存在未核销RMA数据'; }
      else if (fieldName === 'closeRemark') { meaning = '项目关闭备注'; }
      else if (fieldName === 'finshedTime') { meaning = '项目完成测试时间'; }
      else if (fieldName === 'applyBy' && currentTable === 'pm_presales_project_header') { meaning = '售前申请人编码'; }
      else if (fieldName === 'applyTime' && currentTable === 'pm_presales_project_header') { meaning = '售前申请时间'; }
      else if (fieldName === 'endTime') { meaning = '项目结束时间'; }
      else if (fieldName === 'source') { meaning = '数据来源标识'; }

      // pm_presales_project_product_line
      else if (fieldName === 'productLineId') { meaning = '产品线自增主键'; }
      else if (fieldName === 'lendInfoId' && currentTable === 'pm_presales_project_product_line') { meaning = '借货信息ID'; }
      else if (fieldName === 'productFirstName') { meaning = '产品一级分类名称'; }
      else if (fieldName === 'productTypeName') { meaning = '产品类型名称'; }
      else if (fieldName === 'itemCode' && currentTable === 'pm_presales_project_product_line') { meaning = '产品编码'; }
      else if (fieldName === 'itemModel' && currentTable === 'pm_presales_project_product_line') { meaning = '产品型号'; }
      else if (fieldName === 'itemDesc') { meaning = '产品描述'; }
      else if (fieldName === 'productNum') { meaning = '借货产品数量'; }
      else if (fieldName === 'transferNum') { meaning = '借转销数量'; }
      else if (fieldName === 'hexiaoNum') { meaning = '核销数量'; }
      else if (fieldName === 'remark' && currentTable === 'pm_presales_project_product_line') { meaning = '备注'; }

      // pm_presales_project_callback
      else if (fieldName === 'quesnaireState' && currentTable === 'pm_presales_project_callback') { meaning = '0=未填写，1=已填写'; }

      // pm_presales_project_duration
      else if (fieldName === 'applyDuration') { meaning = '项目同步到项目开始的时间间隔'; }
      else if (fieldName === 'totalDuration') { meaning = '项目开始到结束的时间间隔'; }
      else if (fieldName === 'serviceDuration') { meaning = '服务经理指派耗时'; }
      else if (fieldName === 'programDuration') { meaning = '项目经理指派耗时'; }
      else if (fieldName === 'testDuration') { meaning = '测试跟踪耗时'; }
      else if (fieldName === 'callbackDuration') { meaning = '回访耗时'; }
      else if (fieldName === 'serviceApproveDuration') { meaning = '服务经理审批耗时'; }

      // pm_presales_project_rma_info
      else if (fieldName === 'rmaNo') { meaning = 'RMA退货授权编号'; }
      else if (fieldName === 'contractNo' && currentTable === 'pm_presales_project_rma_info') { meaning = '关联合同编号'; }
      else if (fieldName === 'itemCode' && currentTable === 'pm_presales_project_rma_info') { meaning = '产品编码'; }
      else if (fieldName === 'itemModel' && currentTable === 'pm_presales_project_rma_info') { meaning = '产品型号'; }
      else if (fieldName === 'rmaStatus') { meaning = 'RMA处理状态'; }

      // pm_subcontract_project_header
      else if (fieldName === 'subcontractName') { meaning = '转包项目名称'; }
      else if (fieldName === 'contractNos' && currentTable === 'pm_subcontract_project_header') { meaning = '关联合同编号（多个逗号分隔）'; }
      else if (fieldName === 'type' && currentTable === 'pm_subcontract_project_header') { meaning = '转包类型编码'; }
      else if (fieldName === 'state' && currentTable === 'pm_subcontract_project_header') { meaning = '转包项目状态'; }
      else if (fieldName === 'callbackState') { meaning = '回访状态'; }
      else if (fieldName === 'facilitatorName') { meaning = '服务商名称'; }
      else if (fieldName === 'bankInfo' && currentTable === 'pm_subcontract_project_header') { meaning = '服务商开户行信息'; }
      else if (fieldName === 'bankAccount' && currentTable === 'pm_subcontract_project_header') { meaning = '服务商收款银行账号'; }
      else if (fieldName === 'profitDepCode') { meaning = '收益部门编码'; }
      else if (fieldName === 'subcontractNo') { meaning = '转包合同编号'; }
      else if (fieldName === 'isAccrued') { meaning = '0=未计提，1=已计提'; }
      else if (fieldName === 'isInvoiced') { meaning = '0=未提供发票，1=已提供发票'; }
      else if (fieldName === 'subcontractAmount') { meaning = '转包合同金额'; }
      else if (fieldName === 'reason') { meaning = '转包原因说明'; }
      else if (fieldName === 'remark' && currentTable === 'pm_subcontract_project_header') { meaning = '备注'; }
      else if (fieldName === 'zrApproveTime') { meaning = '最新主任审批时间'; }
      else if (fieldName === 'orgId') { meaning = '组织ID'; }

      // pm_subcontract_project_line
      else if (fieldName === 'itemCode' && currentTable === 'pm_subcontract_project_line') { meaning = '产品编码'; }
      else if (fieldName === 'itemModel' && currentTable === 'pm_subcontract_project_line') { meaning = '产品型号'; }
      else if (fieldName === 'itemName' && currentTable === 'pm_subcontract_project_line') { meaning = '产品名称'; }
      else if (fieldName === 'quantity') { meaning = '转包产品数量'; }
      else if (fieldName === 'unitPrice') { meaning = '产品单价'; }
      else if (fieldName === 'amount') { meaning = '产品金额（数量*单价）'; }
      else if (fieldName === 'remark' && currentTable === 'pm_subcontract_project_line') { meaning = '备注'; }

      // pm_subcontract_facilitator
      else if (fieldName === 'facilitatorName' && currentTable === 'pm_subcontract_facilitator') { meaning = '服务商名称'; }
      else if (fieldName === 'facilitatorCode') { meaning = '服务商编码'; }
      else if (fieldName === 'bankInfo' && currentTable === 'pm_subcontract_facilitator') { meaning = '开户行信息'; }
      else if (fieldName === 'bankAccount' && currentTable === 'pm_subcontract_facilitator') { meaning = '银行账号'; }
      else if (fieldName === 'contactPerson') { meaning = '服务商联系人'; }
      else if (fieldName === 'contactPhone') { meaning = '服务商联系电话'; }
      else if (fieldName === 'remark' && currentTable === 'pm_subcontract_facilitator') { meaning = '备注'; }

      // pm_subcontract_project_payment
      else if (fieldName === 'paymentAmount') { meaning = '付款金额'; }
      else if (fieldName === 'paymentDate') { meaning = '付款日期'; }
      else if (fieldName === 'paymentState') { meaning = '付款状态'; }
      else if (fieldName === 'remark' && currentTable === 'pm_subcontract_project_payment') { meaning = '备注'; }

      // pm_subcontract_deliver_files
      else if (fieldName === 'fileName') { meaning = '交付件文件名'; }
      else if (fieldName === 'filePath') { meaning = '交付件文件存储路径'; }
      else if (fieldName === 'uploadBy') { meaning = '文件上传人编码'; }
      else if (fieldName === 'uploadTime') { meaning = '文件上传时间'; }

      // prob_main
      else if (fieldName === 'probTicketNo') { meaning = '关联工单编号'; }
      else if (fieldName === 'theme') { meaning = '技术公告主题'; }
      else if (fieldName === 'desc') { meaning = '问题描述详细内容'; }
      else if (fieldName === 'solution') { meaning = '问题解决方案'; }
      else if (fieldName === 'startdate') { meaning = '问题发现/开始日期'; }
      else if (fieldName === 'duedate') { meaning = '问题计划完成日期'; }
      else if (fieldName === 'attachments') { meaning = '附件路径'; }
      else if (fieldName === 'attachmentNames') { meaning = '附件文件名'; }
      else if (fieldName === 'productType' && currentTable === 'prob_main') { meaning = '产品类型'; }
      else if (fieldName === 'mitigationActionTypes') { meaning = '规避方案操作类型（逗号分隔）'; }
      else if (fieldName === 'solutionActionTypes') { meaning = '解决方案操作类型（逗号分隔）'; }
      else if (fieldName === 'affectedType' && currentTable === 'prob_main') { meaning = '1=盒式系列，2=框式系列'; }
      else if (fieldName === 'visibleRange') { meaning = '可见范围设置'; }
      else if (fieldName === 'reader') { meaning = '已阅读用户列表'; }
      else if (fieldName === 'readStatus') { meaning = '阅读状态标记'; }
      else if (fieldName === 'remark' && currentTable === 'prob_main') { meaning = '备注'; }

      // prob_product
      else if (fieldName === 'productType' && currentTable === 'prob_product') { meaning = '产品类型'; }
      else if (fieldName === 'productModel') { meaning = '产品型号'; }
      else if (fieldName === 'productName' && currentTable === 'prob_product') { meaning = '产品名称'; }
      else if (fieldName === 'affectedVersion') { meaning = '受影响版本范围'; }

      // prob_softwares
      else if (fieldName === 'conp' && currentTable === 'prob_softwares') { meaning = '受影响主控版本号'; }
      else if (fieldName === 'cpld' && currentTable === 'prob_softwares') { meaning = '受影响CPLD版本号'; }
      else if (fieldName === 'boot' && currentTable === 'prob_softwares') { meaning = '受影响Boot版本号'; }
      else if (fieldName === 'pcb' && currentTable === 'prob_softwares') { meaning = '受影响PCB版本号'; }
      else if (fieldName === 'manualEntry') { meaning = '手动录入的版本号'; }
      else if (fieldName === 'markStart') { meaning = '版本范围起始标记'; }
      else if (fieldName === 'markEnd') { meaning = '版本范围结束标记'; }
      else if (fieldName === 'datastate' && currentTable === 'prob_softwares') { meaning = '0=失效，1=有效'; }

      // prob_restore
      else if (fieldName === 'restoreDesc') { meaning = '恢复/修复方案描述'; }
      else if (fieldName === 'restoreType') { meaning = '恢复类型编码'; }
      else if (fieldName === 'restoreDate') { meaning = '恢复日期'; }

      // prob_read_log
      else if (fieldName === 'reader' && currentTable === 'prob_read_log') { meaning = '阅读用户编码'; }
      else if (fieldName === 'readTime') { meaning = '阅读时间'; }

      // fnd_act_hi_comment
      else if (fieldName === 'taskKey') { meaning = 'Activiti任务定义Key'; }
      else if (fieldName === 'assigneeTime') { meaning = '任务办理时间'; }
      else if (fieldName === 'message') { meaning = '审批意见内容'; }

      // fnd_basic_data
      else if (fieldName === 'basicDataDesc') { meaning = '数据项补充描述'; }

      // fnd_basic_data_type
      else if (fieldName === 'dataTypeCode' && currentTable === 'fnd_basic_data_type') { meaning = '数据类型唯一编码'; }
      else if (fieldName === 'dataTypeName') { meaning = '数据类型名称'; }
      else if (fieldName === 'dataTypeDesc') { meaning = '数据类型描述'; }

      // fnd_company
      else if (fieldName === 'compCode') { meaning = '公司编码'; }
      else if (fieldName === 'compAbbr') { meaning = '公司简称'; }
      else if (fieldName === 'compName') { meaning = '公司全名'; }
      else if (fieldName === 'compDesc') { meaning = '公司描述'; }

      // fnd_department
      else if (fieldName === 'departmentNum') { meaning = '部门编码，全局唯一'; }
      else if (fieldName === 'departmentName') { meaning = '部门名称'; }
      else if (fieldName === 'departmentDesc') { meaning = '部门描述'; }
      else if (fieldName === 'parentDepartmentNum') { meaning = '上级部门编码，用于构建部门层级'; }

      // fnd_user_info
      else if (fieldName === 'username') { meaning = '用户登录名，全局唯一'; }
      else if (fieldName === 'realName') { meaning = '用户真实姓名'; }
      else if (fieldName === 'password') { meaning = '加密后的用户密码'; }
      else if (fieldName === 'email' && currentTable === 'fnd_user_info') { meaning = '用户邮箱'; }
      else if (fieldName === 'phone' && currentTable === 'fnd_user_info') { meaning = '用户电话'; }
      else if (fieldName === 'departmentNum' && currentTable === 'fnd_user_info') { meaning = '所属部门编码'; }
      else if (fieldName === 'disabled' && currentTable === 'fnd_user_info') { meaning = '0=启用，1=禁用'; }

      // fnd_files
      else if (fieldName === 'fileName' && currentTable === 'fnd_files') { meaning = '上传文件原始名'; }
      else if (fieldName === 'filePath' && currentTable === 'fnd_files') { meaning = '文件服务器存储路径'; }
      else if (fieldName === 'fileSize') { meaning = '文件大小（字节）'; }
      else if (fieldName === 'fileType') { meaning = '文件MIME类型'; }
      else if (fieldName === 'uploadBy' && currentTable === 'fnd_files') { meaning = '上传用户编码'; }
      else if (fieldName === 'uploadTime' && currentTable === 'fnd_files') { meaning = '文件上传时间'; }

      // fnd_roles
      else if (fieldName === 'roleCode') { meaning = '角色编码，全局唯一'; }
      else if (fieldName === 'roleName') { meaning = '角色名称'; }
      else if (fieldName === 'roleDesc') { meaning = '角色描述'; }

      // fnd_user_power
      else if (fieldName === 'powerCode') { meaning = '权限编码'; }
      else if (fieldName === 'powerValue') { meaning = '权限值'; }

      // fnd_mails
      else if (fieldName === 'mailTo') { meaning = '收件人地址（多个逗号分隔）'; }
      else if (fieldName === 'mailCc') { meaning = '抄送人地址（多个逗号分隔）'; }
      else if (fieldName === 'mailSubject') { meaning = '邮件主题'; }
      else if (fieldName === 'mailContent') { meaning = '邮件正文HTML内容'; }
      else if (fieldName === 'sendState') { meaning = '0=未发送，1=已发送'; }
      else if (fieldName === 'sendTime') { meaning = '邮件发送时间'; }

      // fnd_data_refresh_log
      else if (fieldName === 'dataSource') { meaning = '数据来源（SMS/OA/CRM/SAP/D365）'; }
      else if (fieldName === 'refreshType') { meaning = '刷新类型编码'; }
      else if (fieldName === 'refreshState') { meaning = '0=失败，1=成功'; }
      else if (fieldName === 'refreshTime') { meaning = '数据刷新执行时间'; }
      else if (fieldName === 'recordCount') { meaning = '本次刷新处理记录数'; }
      else if (fieldName === 'errorMsg') { meaning = '刷新失败时的错误信息'; }

      // fnd_sys_arg
      else if (fieldName === 'argCode' && currentTable === 'fnd_sys_arg') { meaning = '系统参数编码'; }
      else if (fieldName === 'argName' && currentTable === 'fnd_sys_arg') { meaning = '系统参数名称'; }
      else if (fieldName === 'argValue') { meaning = '系统参数值'; }
      else if (fieldName === 'argDesc' && currentTable === 'fnd_sys_arg') { meaning = '系统参数描述'; }

      // fnd_menus
      else if (fieldName === 'menuCode') { meaning = '菜单编码'; }
      else if (fieldName === 'menuName') { meaning = '菜单名称'; }
      else if (fieldName === 'menuUrl') { meaning = '菜单访问URL'; }
      else if (fieldName === 'parentMenuCode') { meaning = '父级菜单编码'; }
      else if (fieldName === 'sortNo' && currentTable === 'fnd_menus') { meaning = '菜单排序序号'; }

      // fnd_basic_prjstate
      else if (fieldName === 'stateCode') { meaning = '项目状态编码'; }
      else if (fieldName === 'stateName') { meaning = '项目状态名称'; }
      else if (fieldName === 'stateDesc') { meaning = '项目状态描述'; }

      // fnd_spms_arg
      else if (fieldName === 'argCode' && currentTable === 'fnd_spms_arg') { meaning = 'SPMS参数编码'; }
      else if (fieldName === 'argName' && currentTable === 'fnd_spms_arg') { meaning = 'SPMS参数名称'; }
      else if (fieldName === 'argValue' && currentTable === 'fnd_spms_arg') { meaning = 'SPMS参数值'; }
      else if (fieldName === 'argDesc' && currentTable === 'fnd_spms_arg') { meaning = 'SPMS参数描述'; }

      if (meaning !== '') {
        lines[i] = line.replace(/\| \|$/, '| ' + meaning + ' |');
        fixedCount++;
      }
    }
  }
}

fs.writeFileSync(filePath, lines.join('\n'), 'utf8');
console.log('Fixed ' + fixedCount + ' empty business meanings');
