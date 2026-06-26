#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""填充 complete-data-dictionary.md 中空业务含义字段"""
import json, re, os

BASE = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs'
JSON_PATH = os.path.join(BASE, 'util', 'db', 'step1_columns.json')
MD_PATH = os.path.join(BASE, '03-database', 'complete-data-dictionary.md')

# 1. 加载数据库列注释
with open(JSON_PATH, 'r', encoding='utf-16') as f:
    cols = json.load(f)
db = {}
for r in cols:
    t, c, cm = r['TABLE_NAME'], r['COLUMN_NAME'], r.get('COLUMN_COMMENT','') or ''
    db.setdefault(t, {})[c] = cm
print(f"已加载 {len(db)} 个表的列注释")

# 2. 读取文档
with open(MD_PATH, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# 3. 常见字段推断规则
RULES = {
    'id':'ID标识','ID':'ID标识','Id':'ID标识',
    'createBy':'创建人','create_by':'创建人','createTime':'创建时间','create_time':'创建时间',
    'createDate':'创建日期','create_date':'创建日期','creator':'创建人',
    'updateBy':'更新人','update_by':'更新人','updateTime':'更新时间','update_time':'更新时间',
    'updateDate':'更新日期','update_date':'更新日期','modifier':'修改人','modifyBy':'修改人',
    'delFlag':'删除标记','del_flag':'删除标记','isDeleted':'是否删除','is_deleted':'是否删除',
    'deleted':'是否删除','deleteFlag':'删除标记','delete_flag':'删除标记',
    'state':'状态','status':'状态','enabled':'是否启用','disabled':'是否禁用',
    'name':'名称','Name':'名称','NAME':'名称','title':'标题','Title':'标题',
    'desc':'描述','description':'描述','Description':'描述',
    'remark':'备注','Remark':'备注','remarks':'备注','note':'备注','notes':'备注',
    'code':'编码','Code':'编码','CODE':'编码',
    'type':'类型','Type':'类型','TYPE':'类型',
    'category':'分类','Category':'分类','kind':'种类','sort':'排序',
    'order':'排序','orderBy':'排序字段','order_by':'排序字段',
    'seq':'序号','sequence':'序列号','seqNo':'序号','seq_no':'序号',
    'no':'编号','No':'编号','NO':'编号',
    'num':'数量','Num':'数量','number':'数量','Number':'数量','count':'计数',
    'version':'版本','Version':'版本','level':'级别','Level':'级别',
    'grade':'等级','Grade':'等级','priority':'优先级','Priority':'优先级',
    'time':'时间','Time':'时间','date':'日期','Date':'日期',
    'startTime':'开始时间','start_time':'开始时间','endTime':'结束时间','end_time':'结束时间',
    'effectiveFrom':'生效时间','effective_from':'生效时间',
    'effectiveTo':'失效时间','effective_to':'失效时间',
    'expireDate':'过期日期','expire_date':'过期日期',
    'dueDate':'到期日期','due_date':'到期日期','timestamp':'时间戳',
    'userId':'用户ID','user_id':'用户ID','userName':'用户名','user_name':'用户名',
    'username':'用户名','owner':'拥有者','assignee':'办理人','operator':'操作人',
    'approver':'审批人','companyId':'公司ID','company_id':'公司ID',
    'companyName':'公司名称','company_name':'公司名称','company':'公司',
    'deptId':'部门ID','dept_id':'部门ID','deptName':'部门名称','dept_name':'部门名称',
    'department':'部门','orgId':'组织ID','org_id':'组织ID',
    'orgName':'组织名称','org_name':'组织名称',
    'groupId':'组ID','group_id':'组ID','groupName':'组名称','group_name':'组名称',
    'projectId':'项目ID','project_id':'项目ID','projectName':'项目名称','project_name':'项目名称',
    'projectCode':'项目编码','project_code':'项目编码',
    'productId':'产品ID','product_id':'产品ID','productName':'产品名称','product_name':'产品名称',
    'productCode':'产品编码','product_code':'产品编码',
    'productLine':'产品线','product_line':'产品线',
    'productLineId':'产品线ID','product_line_id':'产品线ID',
    'productLineName':'产品线名称','product_line_name':'产品线名称',
    'customerId':'客户ID','customer_id':'客户ID','customerName':'客户名称','customer_name':'客户名称',
    'customerCode':'客户编码','customer_code':'客户编码',
    'amount':'金额','price':'价格','cost':'成本',
    'totalAmount':'总金额','total_amount':'总金额','totalPrice':'总价','total_price':'总价',
    'tax':'税额','taxRate':'税率','tax_rate':'税率','currency':'币种',
    'discount':'折扣','payment':'付款',
    'address':'地址','addr':'地址','city':'城市','province':'省份','country':'国家',
    'zipCode':'邮编','zip_code':'邮编','phone':'电话','tel':'电话',
    'mobile':'手机','email':'邮箱','mail':'邮箱','fax':'传真',
    'contact':'联系人','contactPerson':'联系人','contact_person':'联系人',
    'contactName':'联系人','contact_name':'联系人',
    'contactTel':'联系电话','contact_tel':'联系电话',
    'contactPhone':'联系电话','contact_phone':'联系电话',
    'parentId':'父级ID','parent_id':'父级ID','parentName':'父级名称','parent_name':'父级名称',
    'path':'路径','url':'URL地址','URL':'URL地址','link':'链接',
    'fileName':'文件名','file_name':'文件名','filePath':'文件路径','file_path':'文件路径',
    'fileSize':'文件大小','file_size':'文件大小','fileType':'文件类型','file_type':'文件类型',
    'source':'来源','target':'目标','sourceId':'来源ID','source_id':'来源ID',
    'targetId':'目标ID','target_id':'目标ID',
    'action':'操作','method':'方法','module':'模块','menu':'菜单',
    'permission':'权限','role':'角色','roleId':'角色ID','role_id':'角色ID',
    'roleName':'角色名称','role_name':'角色名称',
    'resource':'资源','resourceId':'资源ID','resource_id':'资源ID',
    'ip':'IP地址','IP':'IP地址','ipAddress':'IP地址','ip_address':'IP地址',
    'token':'令牌','sessionId':'会话ID','session_id':'会话ID',
    'tenantId':'租户ID','tenant_id':'租户ID','tenantName':'租户名称','tenant_name':'租户名称',
    'tag':'标签','tags':'标签','flag':'标记','sign':'签名','signature':'签名',
    'hash':'哈希值','checksum':'校验和',
    'key':'键','Key':'键','KEY':'键','value':'值','Value':'值','VALUE':'值',
    'text':'文本','Text':'文本','TEXT':'文本','content':'内容','Content':'内容',
    'data':'数据','Data':'数据','DATA':'数据','info':'信息','Info':'信息',
    'detail':'明细','extra':'扩展信息','extend':'扩展','ext':'扩展',
    'attr':'属性','attribute':'属性','property':'属性','prop':'属性',
    'param':'参数','parameter':'参数','config':'配置','configuration':'配置',
    'setting':'设置','option':'选项','filter':'过滤条件','condition':'条件',
    'rule':'规则','formula':'公式','expression':'表达式','script':'脚本',
    'template':'模板','templateId':'模板ID','template_id':'模板ID',
    'format':'格式','pattern':'模式','strategy':'策略',
    'handler':'处理器','listener':'监听器','callback':'回调',
    'event':'事件','message':'消息','msg':'消息',
    'notification':'通知','alert':'告警','warning':'警告',
    'error':'错误','exception':'异常','fail':'失败','success':'成功',
    'result':'结果','response':'响应','request':'请求',
    'input':'输入','output':'输出','scope':'作用域',
    'prefix':'前缀','suffix':'后缀','separator':'分隔符',
    'encoding':'编码方式','charset':'字符集','locale':'区域设置',
    'language':'语言','timezone':'时区',
    'duration':'耗时','interval':'间隔','period':'周期','frequency':'频率',
    'rate':'比率','ratio':'比例','percent':'百分比','percentage':'百分比',
    'weight':'权重','score':'分数','rating':'评级','rank':'排名',
    'position':'位置','offset':'偏移量','limit':'限制数',
    'page':'页码','pageSize':'每页条数','page_size':'每页条数',
    'pageNum':'页码','page_num':'页码',
    'total':'总数','Total':'总数','size':'大小',
    'length':'长度','width':'宽度','height':'高度','depth':'深度',
    'volume':'体积','area':'面积','color':'颜色','icon':'图标',
    'image':'图片','img':'图片','logo':'标志','thumbnail':'缩略图',
    'avatar':'头像','photo':'照片',
    'visible':'是否可见','hidden':'是否隐藏','readonly':'是否只读',
    'editable':'是否可编辑','locked':'是否锁定','selected':'是否选中',
    'checked':'是否勾选','expanded':'是否展开','collapsed':'是否折叠',
    'style':'样式','className':'CSS类名','class_name':'CSS类名',
    'tooltip':'提示信息','placeholder':'占位符',
    'direction':'方向','alignment':'对齐方式','layout':'布局',
    'margin':'外边距','padding':'内边距','border':'边框','shadow':'阴影',
    'column':'列','row':'行','gap':'间距','wrap':'换行',
    'overflow':'溢出','scroll':'滚动','zoom':'缩放','scale':'缩放比例',
    'rotate':'旋转','transform':'变换','origin':'原点',
    'coordinate':'坐标','latitude':'纬度','longitude':'经度',
    'distance':'距离','angle':'角度','speed':'速度',
    'temperature':'温度','humidity':'湿度','voltage':'电压',
    'current':'电流','power':'功率','energy':'能量',
    'capacity':'容量','load':'负载','threshold':'阈值',
    'min':'最小值','max':'最大值','minimum':'最小值','maximum':'最大值',
    'average':'平均值','mean':'平均值','median':'中位数',
    'mode':'模式','deviation':'偏差','tolerance':'容差',
    'precision':'精度','accuracy':'准确度','resolution':'分辨率',
    'baseline':'基线','benchmark':'基准','metric':'指标',
    'measure':'度量','indicator':'指标','kpi':'关键绩效指标',
    'milestone':'里程碑','progress':'进度','completion':'完成度',
    'phase':'阶段','stage':'阶段','step':'步骤',
    'iteration':'迭代','sprint':'冲刺','release':'发布',
    'revision':'修订版本','build':'构建','compile':'编译',
    'deploy':'部署','install':'安装','upgrade':'升级','downgrade':'降级',
    'migrate':'迁移','rollback':'回滚','backup':'备份','restore':'恢复',
    'archive':'归档','compress':'压缩','extract':'提取',
    'import':'导入','export':'导出','sync':'同步','async':'异步',
    'batch':'批次','batchId':'批次ID','batch_id':'批次ID',
    'batchNo':'批次号','batch_no':'批次号',
    'serial':'序列号','serialNo':'序列号','serial_no':'序列号',
    'sn':'序列号','SN':'序列号','barcode':'条码',
    'protocol':'协议','port':'端口','host':'主机','hostname':'主机名',
    'domain':'域名','cert':'证书','certificate':'证书','license':'许可证',
    'subscription':'订阅','plan':'计划','package':'套餐',
    'sku':'SKU','SKU':'SKU','spu':'SPU','SPU':'SPU',
    'inventory':'库存','stock':'库存','warehouse':'仓库',
    'warehouseId':'仓库ID','warehouse_id':'仓库ID',
    'warehouseName':'仓库名称','warehouse_name':'仓库名称',
    'location':'库位','bin':'库位','slot':'槽位','shelf':'货架','rack':'机架',
    'supplier':'供应商','supplierId':'供应商ID','supplier_id':'供应商ID',
    'supplierName':'供应商名称','supplier_name':'供应商名称',
    'vendor':'供应商','vendorId':'供应商ID','vendor_id':'供应商ID',
    'vendorName':'供应商名称','vendor_name':'供应商名称',
    'manufacturer':'制造商','brand':'品牌','model':'型号',
    'spec':'规格','specification':'规格',
    'material':'物料','materialCode':'物料编码','material_code':'物料编码',
    'materialName':'物料名称','material_name':'物料名称',
    'unit':'单位','unitOfMeasure':'计量单位','unit_of_measure':'计量单位',
    'quantity':'数量','Quantity':'数量','qty':'数量','Qty':'数量',
    'exchangeRate':'汇率','exchange_rate':'汇率',
    'taxAmount':'税额','tax_amount':'税额',
    'discountRate':'折扣率','discount_rate':'折扣率',
    'discountAmount':'折扣金额','discount_amount':'折扣金额',
    'freight':'运费','shippingFee':'运费','shipping_fee':'运费',
    'insurance':'保险','warranty':'质保','warrantyPeriod':'质保期',
    'warranty_period':'质保期','warrantyStartDate':'质保开始日期',
    'warranty_start_date':'质保开始日期','warrantyEndDate':'质保结束日期',
    'warranty_end_date':'质保结束日期',
    'maintenance':'维护','repair':'维修','service':'服务',
    'serviceType':'服务类型','service_type':'服务类型',
    'serviceLevel':'服务等级','service_level':'服务等级',
    'contract':'合同','contractId':'合同ID','contract_id':'合同ID',
    'contractNo':'合同编号','contract_no':'合同编号',
    'contractName':'合同名称','contract_name':'合同名称',
    'contractType':'合同类型','contract_type':'合同类型',
    'contractStatus':'合同状态','contract_status':'合同状态',
    'contractAmount':'合同金额','contract_amount':'合同金额',
    'signDate':'签订日期','sign_date':'签订日期','signPerson':'签订人',
    'invoice':'发票','invoiceNo':'发票号','invoice_no':'发票号',
    'invoiceDate':'开票日期','invoice_date':'开票日期',
    'invoiceAmount':'发票金额','invoice_amount':'发票金额',
    'paymentMethod':'付款方式','payment_method':'付款方式',
    'paymentStatus':'付款状态','payment_status':'付款状态',
    'paymentDate':'付款日期','payment_date':'付款日期',
    'paymentAmount':'付款金额','payment_amount':'付款金额',
    'receipt':'收据','receiptNo':'收据号','receipt_no':'收据号',
    'refund':'退款','refundAmount':'退款金额','refund_amount':'退款金额',
    'orderId':'订单ID','order_id':'订单ID','orderNo':'订单号','order_no':'订单号',
    'orderDate':'订单日期','order_date':'订单日期',
    'orderType':'订单类型','order_type':'订单类型',
    'orderStatus':'订单状态','order_status':'订单状态',
    'orderLineId':'订单行ID','order_line_id':'订单行ID',
    'shipment':'发货','shipmentId':'发货ID','shipment_id':'发货ID',
    'shipmentNo':'发货单号','shipment_no':'发货单号',
    'shipmentDate':'发货日期','shipment_date':'发货日期',
    'shipmentStatus':'发货状态','shipment_status':'发货状态',
    'delivery':'交付','deliveryDate':'交付日期','delivery_date':'交付日期',
    'deliveryAddress':'交付地址','delivery_address':'交付地址',
    'deliveryMethod':'交付方式','delivery_method':'交付方式',
    'tracking':'跟踪','trackingNo':'快递单号','tracking_no':'快递单号',
    'carrier':'承运商','carrierName':'承运商名称','carrier_name':'承运商名称',
    'logistics':'物流','logisticsCompany':'物流公司','logistics_company':'物流公司',
    'express':'快递','expressNo':'快递单号','express_no':'快递单号',
    'returnNo':'退货单号','return_no':'退货单号',
    'returnDate':'退货日期','return_date':'退货日期',
    'returnReason':'退货原因','return_reason':'退货原因',
    'compatible':'兼容','dependency':'依赖','prerequisite':'前置条件',
    'constraint':'约束','restriction':'限制',
    'standard':'标准','compliance':'合规','regulation':'法规',
    'policy':'策略','agreement':'协议','terms':'条款','clause':'条款',
    'component':'组件','plugin':'插件','extension':'扩展',
    'feature':'功能','function':'函数','algorithm':'算法',
    'logic':'逻辑','calculation':'计算','variable':'变量','constant':'常量',
    'trigger':'触发器','signal':'信号','timeout':'超时',
    'cancel':'取消','suspend':'暂停','resume':'恢复','pause':'暂停',
    'stop':'停止','start':'开始','restart':'重启','reset':'重置',
    'refresh':'刷新','reload':'重新加载',
    'initialize':'初始化','destroy':'销毁','dispose':'释放',
    'acquire':'获取','grant':'授权','revoke':'撤销','deny':'拒绝',
    'allow':'允许','permit':'许可','forbid':'禁止','prohibit':'禁止',
    'validate':'验证','verify':'校验','confirm':'确认',
    'accept':'接受','reject':'拒绝','approve':'审批通过',
    'authorize':'授权','authenticate':'认证',
    'login':'登录','logout':'登出','register':'注册',
    'publish':'发布','notify':'通知','warn':'警告',
    'ignore':'忽略','skip':'跳过','defer':'延迟','delay':'延迟',
    'schedule':'调度','cron':'Cron表达式',
    'cycle':'周期','round':'轮次','attempt':'尝试次数',
    'trial':'试用','demo':'演示','sample':'样本','example':'示例',
    'prototype':'原型','draft':'草稿',
    'design':'设计','architecture':'架构','structure':'结构',
    'framework':'框架','infrastructure':'基础设施',
    'system':'系统','application':'应用',
    'container':'容器','cluster':'集群','node':'节点',
    'instance':'实例','replica':'副本','partition':'分区',
    'shard':'分片','segment':'段','fragment':'片段',
    'chunk':'块','block':'块','frame':'帧','packet':'包',
    'payload':'载荷','header':'头部','body':'主体','footer':'尾部',
    'metadata':'元数据','schema':'模式','definition':'定义',
    'declaration':'声明','reference':'引用','pointer':'指针',
    'handle':'句柄','descriptor':'描述符','identifier':'标识符',
    'alias':'别名','nickname':'昵称','displayName':'显示名称','display_name':'显示名称',
    'fullName':'全名','full_name':'全名','shortName':'简称','short_name':'简称',
    'legalName':'法定名称','legal_name':'法定名称',
    'officialName':'官方名称','official_name':'官方名称',
    'internalName':'内部名称','internal_name':'内部名称',
    'externalName':'外部名称','external_name':'外部名称',
    'technicalName':'技术名称','technical_name':'技术名称',
    'businessName':'业务名称','business_name':'业务名称',
    'logicalName':'逻辑名称','logical_name':'逻辑名称',
    'physicalName':'物理名称','physical_name':'物理名称',
    'tableName':'表名','table_name':'表名',
    'columnName':'列名','column_name':'列名',
    'fieldName':'字段名','field_name':'字段名',
    'propertyName':'属性名','property_name':'属性名',
    'methodName':'方法名','method_name':'方法名',
    'className':'类名','class_name':'类名',
    'packageName':'包名','package_name':'包名',
    'layer':'层','context':'上下文','profile':'配置文件',
    'tenant':'租户','workspace':'工作空间','repository':'仓库',
    'branch':'分支','commit':'提交','change':'变更',
    'diff':'差异','patch':'补丁','hotfix':'热修复',
    'enhancement':'增强','improvement':'改进','optimization':'优化',
    'refactor':'重构','deprecation':'废弃','removal':'移除',
    'migration':'迁移','compatibility':'兼容性',
    'documentation':'文档','manual':'手册','guide':'指南',
    'tutorial':'教程','faq':'常见问题',
    'help':'帮助','support':'支持','feedback':'反馈',
    'review':'审核','approval':'审批','rejection':'驳回',
    'like':'点赞','favorite':'收藏','bookmark':'书签',
    'share':'分享','forward':'转发','reply':'回复',
    'folder':'文件夹','directory':'目录','file':'文件',
    'document':'文档','record':'记录','entry':'条目',
    'item':'项目','element':'元素','entity':'实体',
    'object':'对象','subject':'主体','association':'关联',
    'mapping':'映射','binding':'绑定','connection':'连接',
    'join':'连接','union':'联合','group':'分组',
    'aggregate':'聚合','distinct':'去重','unique':'唯一',
    'duplicate':'重复','redundant':'冗余','conflict':'冲突',
    'overlap':'重叠','missing':'缺失','orphan':'孤立',
    'stale':'过期','obsolete':'废弃','deprecated':'已废弃',
    'legacy':'遗留','inheritance':'继承','interface':'接口',
    'implementation':'实现','delegation':'委托','composition':'组合',
    'consistency':'一致性','integrity':'完整性','validity':'有效性',
    'reliability':'可靠性','availability':'可用性',
    'scalability':'可扩展性','resilience':'韧性',
    'robustness':'健壮性','maintainability':'可维护性',
    'testability':'可测试性','observability':'可观测性',
    'traceability':'可追溯性','security':'安全性',
    'privacy':'隐私','confidentiality':'机密性',
    'transparency':'透明性','usability':'可用性',
    'accessibility':'可访问性','localization':'本地化',
    'internationalization':'国际化','customization':'定制化',
    'personalization':'个性化','switch':'开关','toggle':'切换',
    'operation':'操作','transaction':'事务','process':'流程',
    'workflow':'工作流','pipeline':'管道','chain':'链',
    'consequence':'后果','impact':'影响','cause':'原因','reason':'原因',
    'purpose':'目的','goal':'目标','objective':'目标',
    'destination':'目的地','root':'根','base':'基础',
    'core':'核心','center':'中心','middle':'中间',
    'edge':'边缘','boundary':'边界','ceiling':'上限',
    'floor':'下限','cap':'上限','quota':'配额',
    'allocation':'分配','distribution':'分布',
    'supply':'供应','demand':'需求',
    'utilization':'利用率','consumption':'消耗',
    'production':'生产','manufacturing':'制造',
    'assembly':'装配','processing':'处理','treatment':'处理',
    'management':'管理','governance':'治理','control':'控制',
    'monitoring':'监控','detection':'检测','prevention':'预防',
    'protection':'保护','defense':'防御','threat':'威胁',
    'vulnerability':'漏洞','mitigation':'缓解',
    'remediation':'修复','resolution':'解决',
    'solution':'解决方案','workaround':'临时方案',
    'alternative':'替代方案','compromise':'折中',
    'balance':'平衡','equilibrium':'均衡',
    'parity':'奇偶校验','digest':'摘要',
    'credential':'凭证','secret':'密钥','password':'密码',
    'passphrase':'口令','ticket':'票据','voucher':'凭证',
    'coupon':'优惠券','rebate':'返利','bonus':'奖金',
    'commission':'佣金','fee':'费用','charge':'收费',
    'expense':'费用','expenditure':'支出',
    'revenue':'收入','income':'收入','profit':'利润','loss':'亏损',
    'markup':'加价','valuation':'估值','appraisal':'评估',
    'assessment':'评估','evaluation':'评价',
    'audit':'审计','inspection':'检查','examination':'检验',
    'test':'测试','simulation':'仿真','emulation':'模拟',
    'stress':'压力','performance':'性能',
    'latency':'延迟','throughput':'吞吐量','bandwidth':'带宽',
    'concurrency':'并发数','efficiency':'效率',
    'productivity':'生产力','quality':'质量',
    'satisfaction':'满意度','experience':'体验',
    'perception':'感知','awareness':'认知',
    'knowledge':'知识','intelligence':'智能',
    'insight':'洞察','vision':'愿景','mission':'使命',
    'methodology':'方法论','paradigm':'范式',
    'practice':'实践','principle':'原则',
    'law':'定律','theorem':'定理','axiom':'公理',
    'corollary':'推论','lemma':'引理','proof':'证明',
    'evidence':'证据','fact':'事实',
    'interpretation':'解释','explanation':'说明',
    'clarification':'澄清','elaboration':'详述',
    'detail':'细节','implication':'含义',
    'semantics':'语义','syntax':'语法',
    'vocabulary':'词汇','glossary':'术语表',
    'terminology':'术语','jargon':'行话',
    'acronym':'缩写','abbreviation':'缩写',
    'supplement':'补充','addendum':'附录','appendix':'附录',
    'embed':'嵌入','wrapper':'包装器','holder':'持有者',
    'provider':'提供者','consumer':'消费者','producer':'生产者',
    'publisher':'发布者','subscriber':'订阅者',
    'processor':'处理器','executor':'执行器',
    'worker':'工作者','thread':'线程','task':'任务',
    'job':'作业','work':'工作','effort':'工作量',
    'manpower':'人力','asset':'资产','ownership':'所有权',
    'possession':'占有','custody':'保管',
    'supervision':'监督','oversight':'监督',
    'direction':'指导','guidance':'指导','leadership':'领导',
    'coaching':'辅导','training':'培训','education':'教育',
    'learning':'学习','study':'研究','research':'研究',
    'investigation':'调查','inquiry':'查询','query':'查询',
    'search':'搜索','selection':'选择','choice':'选择',
    'preference':'偏好','decision':'决策','judgment':'判断',
    'verdict':'裁决','ruling':'裁定',
    'penalty':'处罚','punishment':'惩罚','reward':'奖励',
    'prize':'奖品','award':'奖项','honor':'荣誉',
    'recognition':'认可','acknowledgment':'致谢',
    'credit':'积分','debit':'扣减','account':'账户',
    'ledger':'分类账','journal':'日记账',
    'transfer':'转账','deposit':'存款','withdrawal':'取款',
    'settlement':'结算','clearing':'清算','reconciliation':'对账',
    'adjustment':'调整','correction':'更正','amendment':'修正',
    'modification':'修改','alteration':'变更','variation':'变更',
    'variance':'差异','discrepancy':'不一致','mismatch':'不匹配',
    'contradiction':'矛盾','inconsistency':'不一致',
    'ambiguity':'歧义','uncertainty':'不确定性',
    'risk':'风险','hazard':'危险','danger':'危险',
    'caution':'注意','announcement':'公告','bulletin':'公告',
    'broadcast':'广播','communication':'通信',
    'dialog':'对话','conversation':'会话',
    'discussion':'讨论','debate':'辩论','argument':'论点',
    'negotiation':'谈判','mediation':'调解',
    'arbitration':'仲裁','litigation':'诉讼','dispute':'争议',
    'concession':'让步','waiver':'豁免','exemption':'豁免',
    'immunity':'免疫','privilege':'特权',
    'right':'权利','entitlement':'资格','authority':'权限',
    'jurisdiction':'管辖权','sovereignty':'主权',
    'autonomy':'自治','independence':'独立',
    'freedom':'自由','liberty':'自由',
    'consent':'同意','endorsement':'认可',
    'sanction':'制裁','embargo':'禁运','boycott':'抵制',
    'blockade':'封锁','obligation':'义务','duty':'职责',
    'responsibility':'责任','liability':'责任',
    'accountability':'问责','blame':'过失',
    'guilt':'罪责','innocence':'无罪',
    'prosecution':'起诉','hearing':'听证',
    'testimony':'证词','witness':'证人',
    'fine':'罚金','forfeiture':'没收','confiscation':'没收',
    'seizure':'扣押','lien':'留置权','mortgage':'抵押',
    'pledge':'质押','collateral':'担保物','guarantee':'保证',
    'indemnity':'赔偿','coverage':'覆盖范围',
    'premium':'保费','deductible':'免赔额','claim':'索赔',
    'benefit':'福利','compensation':'补偿',
    'remuneration':'报酬','salary':'薪水','wage':'工资',
    'pay':'薪酬','earning':'收益','turnover':'营业额',
    'sales':'销售额','dividend':'红利','interest':'利息',
    'principal':'本金','capital':'资本','equity':'权益',
    'share':'股份','bond':'债券','derivative':'衍生品',
    'hedge':'对冲','arbitrage':'套利','speculation':'投机',
    'investment':'投资','portfolio':'投资组合',
    'debt':'债务','loan':'贷款','budget':'预算',
    'forecast':'预测','estimate':'估算','projection':'预测',
    'timeline':'时间线','deadline':'截止日期',
    'deliverable':'交付物','yield':'收益率',
    'achievement':'成就','mistake':'失误',
    'defect':'缺陷','bug':'缺陷','issue':'问题','problem':'问题',
    'challenge':'挑战','obstacle':'障碍','barrier':'壁垒',
    'hurdle':'障碍','bottleneck':'瓶颈',
    'extent':'程度','degree':'程度','variety':'品种',
    'flavor':'风格','edition':'版本','generation':'代',
    'indicator':'指标','marker':'标记','stamp':'印章',
    'seal':'封印','mark':'标记','notation':'记号',
    'symbol':'符号','emoji':'表情','glyph':'字形',
    'character':'字符','digit':'数字','letter':'字母',
    'word':'单词','phrase':'短语','paragraph':'段落',
    'subsection':'小节','article':'条','point':'点',
    'cell':'单元格','field':'字段','tuple':'元组',
    'dataset':'数据集','table':'表','view':'视图',
    'primary':'主键','foreign':'外键','composite':'复合',
    'candidate':'候选','surrogate':'代理',
    'natural':'自然','artificial':'人工','synthetic':'合成',
    'derived':'派生','calculated':'计算','computed':'计算',
    'stored':'存储','cached':'缓存','buffered':'缓冲',
    'queued':'排队','pending':'待处理','waiting':'等待中',
    'running':'运行中','executing':'执行中',
    'completed':'已完成','finished':'已完成','done':'已完成',
    'ended':'已结束','closed':'已关闭','resolved':'已解决',
    'fixed':'已修复','cancelled':'已取消','aborted':'已中止',
    'terminated':'已终止','expired':'已过期','invalidated':'已失效',
    'archived':'已归档','purged':'已清除','removed':'已移除',
    'restored':'已恢复','recovered':'已恢复','reverted':'已还原',
    'undone':'已撤销','retried':'已重试','escalated':'已升级',
    'transferred':'已转移','delegated':'已委托',
    'redirected':'已重定向','routed':'已路由',
    'delivered':'已送达','received':'已接收',
    'acknowledged':'已确认','confirmed':'已确认',
    'verified':'已验证','validated':'已校验',
    'approved':'已审批','rejected':'已拒绝','denied':'已拒绝',
    'accepted':'已接受','agreed':'已同意',
    'contested':'已争议','disputed':'已争议',
    'challenged':'已质疑','questioned':'已质疑',
    'doubted':'已怀疑','suspected':'已怀疑',
    'investigated':'已调查','audited':'已审计',
    'inspected':'已检查','examined':'已检验',
    'tested':'已测试','certified':'已认证',
    'qualified':'已合格','compliant':'已合规',
    'conformant':'已符合','standardized':'已标准化',
    'normalized':'已规范化','sanitized':'已清理',
    'escaped':'已转义','encoded':'已编码','decoded':'已解码',
    'encrypted':'已加密','decrypted':'已解密',
    'compressed':'已压缩','decompressed':'已解压',
    'serialized':'已序列化','deserialized':'已反序列化',
    'formatted':'已格式化','parsed':'已解析',
    'compiled':'已编译','interpreted':'已解释',
    'executed':'已执行','evaluated':'已求值',
    'rendered':'已渲染','displayed':'已显示',
    'printed':'已打印','exported':'已导出','imported':'已导入',
    'uploaded':'已上传','downloaded':'已下载',
    'transmitted':'已传输','sent':'已发送',
    'dispatched':'已派发','distributed':'已分发',
    'published':'已发布','notified':'已通知',
    'alerted':'已告警','informed':'已通知',
    'reminded':'已提醒','prompted':'已提示',
    'requested':'已请求','commanded':'已命令',
    'instructed':'已指示','assisted':'已协助',
    'helped':'已帮助','supported':'已支持',
    'facilitated':'已促进','permitted':'已许可',
    'granted':'已授予','awarded':'已授予',
    'rewarded':'已奖励','compensated':'已补偿',
    'reimbursed':'已报销','refunded':'已退款',
    'returned':'已退货','exchanged':'已换货',
    'replaced':'已替换','substituted':'已替代',
    'swapped':'已交换','traded':'已交易',
    'purchased':'已购买','sold':'已出售',
    'leased':'已租赁','rented':'已出租',
    'hired':'已雇佣','employed':'已雇用',
    'engaged':'已聘用','contracted':'已签约',
    'commissioned':'已委托','appointed':'已任命',
    'designated':'已指定','allocated':'已分配',
    'apportioned':'已分摊','allotted':'已分配',
    'budgeted':'已预算','estimated':'已估算',
    'projected':'已预测','forecasted':'已预测',
    'predicted':'已预测','anticipated':'已预期',
    'expected':'已期望','automated':'已自动化',
    'mechanized':'已机械化','computerized':'已计算机化',
    'digitized':'已数字化','virtualized':'已虚拟化',
    'containerized':'已容器化','orchestrated':'已编排',
    'coordinated':'已协调','synchronized':'已同步',
    'integrated':'已集成','consolidated':'已整合',
    'merged':'已合并','combined':'已组合',
    'unified':'已统一','harmonized':'已协调',
    'aligned':'已对齐','conformed':'已符合',
    'complied':'已遵从','adhered':'已遵守',
    'followed':'已遵循','observed':'已遵守',
    'respected':'已尊重','honored':'已兑现',
    'fulfilled':'已履行','satisfied':'已满足',
    'met':'已达成','achieved':'已实现',
    'accomplished':'已完成','attained':'已达到',
    'reached':'已到达','arrived':'已到达',
    'departed':'已出发','exited':'已退出',
    'entered':'已进入','joined':'已加入',
    'participated':'已参与','attended':'已出席',
    'present':'在场','absent':'缺席',
    'available':'可用','unavailable':'不可用',
    'accessible':'可访问','reachable':'可达',
    'unreachable':'不可达','connected':'已连接',
    'disconnected':'已断开','online':'在线','offline':'离线',
    'active':'活跃','inactive':'不活跃','dormant':'休眠',
    'idle':'空闲','busy':'忙碌','occupied':'占用',
    'reserved':'已预留','booked':'已预订',
    'tentative':'暂定','provisional':'临时',
    'temporary':'临时','permanent':'永久',
    'persistent':'持久','transient':'瞬态',
    'volatile':'易失','static':'静态','dynamic':'动态',
    'mutable':'可变','immutable':'不可变',
    'profession':'职业','occupation':'职业',
    'career':'职业生涯','position':'职位',
    'title':'头衔','rank':'职级','grade':'等级',
    'seniority':'资历','tenure':'任期',
    'appointment':'任命','promotion':'晋升',
    'demotion':'降职','resignation':'辞职',
    'retirement':'退休','leave':'请假',
    'absence':'缺勤','attendance':'出勤',
    'overtime':'加班','shift':'班次','roster':'排班',
    'timesheet':'工时表','payroll':'工资单',
    'allowance':'津贴','deduction':'扣款',
    'pension':'养老金','provident':'公积金',
    'fund':'基金','contribution':'缴款',
    'perk':'额外福利','reimbursement':'报销',
    'verification':'核实','confirmation':'确认',
    'authorization':'授权','authentication':'认证',
    'access':'访问','membership':'成员资格',
    'assignment':'分配','substitution':'代理',
    'proxy':'代理','representative':'代表',
    'agent':'代理人','broker':'经纪人',
    'intermediary':'中介','mediator':'调解人',
    'facilitator':'协调人','coordinator':'协调员',
    'administrator':'管理员','supervisor':'主管',
    'manager':'经理','director':'总监',
    'executive':'高管','officer':'官员',
    'chief':'首席','head':'负责人','lead':'负责人',
    'senior':'高级','junior':'初级',
    'intern':'实习生','trainee':'培训生',
    'apprentice':'学徒','novice':'新手',
    'beginner':'初学者','expert':'专家',
    'specialist':'专家','professional':'专业人员',
    'consultant':'顾问','advisor':'顾问',
    'counselor':'咨询师','mentor':'导师',
    'coach':'教练','trainer':'培训师',
    'instructor':'讲师','teacher':'教师',
    'professor':'教授','lecturer':'讲师',
    'tutor':'辅导老师','student':'学生',
    'scholar':'学者','researcher':'研究员',
    'scientist':'科学家','engineer':'工程师',
    'developer':'开发人员','programmer':'程序员',
    'coder':'编码员','designer':'设计师',
    'architect':'架构师','analyst':'分析师',
    'tester':'测试人员','technician':'技术员',
    'employee':'员工','staff':'员工',
    'personnel':'人事','workforce':'劳动力',
    'headcount':'人数','attrition':'流失率',
    'retention':'留存率','recruitment':'招聘',
    'hiring':'招聘','onboarding':'入职',
    'offboarding':'离职','orientation':'入职培训',
    'probation':'试用期','performance':'绩效',
    'appraisal':'考核','feedback':'反馈',
    'scorecard':'计分卡','objective':'目标',
    'target':'指标','measure':'衡量标准',
    'quota':'配额','limit':'限额',
    'actual':'实际值','shortfall':'缺口',
    'deficit':'赤字','surplus':'盈余',
    'balance':'余额','equality':'平等',
    'fairness':'公正','justice':'正义',
    'impartiality':'公正','neutrality':'中立',
    'objectivity':'客观','subjectivity':'主观',
    'bias':'偏见','prejudice':'偏见',
    'discrimination':'歧视','favoritism':'偏袒',
    'corruption':'腐败','bribery':'贿赂',
    'fraud':'欺诈','embezzlement':'挪用',
    'misuse':'滥用','abuse':'滥用',
    'neglect':'疏忽','negligence':'过失',
    'malpractice':'渎职','misconduct':'不当行为',
    'violation':'违规','infringement':'侵权',
    'breach':'违约','offense':'违规',
    'crime':'犯罪','felony':'重罪',
    'misdemeanor':'轻罪','infraction':'违章',
    'discipline':'纪律','reprimand':'训诫',
    'censure':'谴责','suspension':'停职',
    'dismissal':'解雇','firing':'辞退',
    'layoff':'裁员','redundancy':'冗余裁员',
    'downsizing':'缩编','outsourcing':'外包',
    'procurement':'采购','purchasing':'采购',
    'sourcing':'寻源','tendering':'招标',
    'bidding':'投标','auction':'拍卖',
    'quotation':'报价','proposal':'方案',
    'offer':'报价','bid':'出价','tender':'标书',
    'covenant':'契约','pact':'协定',
    'treaty':'条约','convention':'公约',
    'charter':'章程','constitution':'章程',
    'bylaw':'规章','statute':'法规',
    'ordinance':'条例','decree':'法令',
    'edict':'布告','mandate':'命令',
    'directive':'指令','instruction':'指示',
    'command':'命令','demand':'要求',
    'requirement':'要求','specification':'规格',
    'criterion':'标准','criteria':'标准',
    'guideline':'指导方针','procedure':'程序',
    'protocol':'协议','custom':'习俗',
    'tradition':'传统','habit':'习惯',
    'routine':'常规','ritual':'仪式',
    'ceremony':'典礼','celebration':'庆祝',
    'festival':'节日','holiday':'假日',
    'vacation':'假期','break':'休息',
    'rest':'休息','pause':'暂停',
    'intermission':'间歇','space':'空间',
    'room':'空间','territory':'领地',
    'domain':'领域','realm':'领域',
    'sphere':'范围','span':'跨度',
    'stretch':'延伸','reach':'范围',
    'proximity':'接近度','vicinity':'附近',
    'neighborhood':'邻近','adjacency':'相邻',
    'contiguity':'毗邻','frontier':'边界',
    'perimeter':'周长','circumference':'周长',
    'diameter':'直径','hub':'枢纽',
    'nexus':'连接点','junction':'交汇点',
    'intersection':'交叉点','roundabout':'环岛',
    'tunnel':'隧道','bridge':'桥梁',
    'road':'道路','street':'街道',
    'highway':'高速公路','freeway':'高速公路',
    'expressway':'快速路','pathway':'小径',
    'walkway':'步道','sidewalk':'人行道',
    'trail':'小径','track':'轨道',
    'railway':'铁路','subway':'地铁',
    'metro':'地铁','transit':'公共交通',
    'transport':'交通','transportation':'交通',
    'vehicle':'车辆','automobile':'汽车',
    'car':'汽车','truck':'卡车',
    'airplane':'飞机','aircraft':'航空器',
    'ship':'轮船','boat':'船','vessel':'船只',
    'ferry':'渡轮','yacht':'游艇',
    'satellite':'卫星','probe':'探测器',
    'drone':'无人机','robot':'机器人',
    'machine':'机器','device':'设备',
    'appliance':'电器','equipment':'设备',
    'apparatus':'装置','instrument':'仪器',
    'tool':'工具','implement':'器具',
    'utensil':'器具','gadget':'小工具',
    'widget':'小部件','part':'零件',
    'piece':'件','subassembly':'子组件',
    'accessory':'配件','spare':'备件',
    'consumable':'耗材','substance':'物质',
    'matter':'物质','compound':'化合物',
    'mixture':'混合物','solution':'溶液',
    'emulsion':'乳液','gel':'凝胶',
    'paste':'糊状物','powder':'粉末',
    'dust':'粉尘','particle':'颗粒',
    'grain':'颗粒','crystal':'晶体',
    'fiber':'纤维','fabric':'织物',
    'textile':'纺织品','cloth':'布料',
    'leather':'皮革','rubber':'橡胶',
    'plastic':'塑料','polymer':'聚合物',
    'resin':'树脂','adhesive':'粘合剂',
    'glue':'胶水','cement':'水泥',
    'concrete':'混凝土','mortar':'砂浆',
    'brick':'砖','stone':'石材',
    'wood':'木材','timber':'木材',
    'lumber':'板材','plywood':'胶合板',
    'board':'板','panel':'面板',
    'sheet':'薄板','plate':'板',
    'foil':'箔','film':'薄膜',
    'membrane':'膜','coating':'涂层',
    'stratum':'地层','story':'楼层',
    'storey':'楼层','basement':'地下室',
    'attic':'阁楼','loft':'阁楼',
    'roof':'屋顶','ceiling':'天花板',
    'wall':'墙壁','door':'门',
    'window':'窗户','gate':'大门',
    'portal':'入口','entrance':'入口',
    'corridor':'走廊','hallway':'过道',
    'passage':'通道','aisle':'过道',
    'lobby':'大堂','foyer':'门厅',
    'reception':'前台','counter':'柜台',
    'desk':'桌子','chair':'椅子',
    'seat':'座位','bench':'长凳',
    'sofa':'沙发','bed':'床',
    'mattress':'床垫','pillow':'枕头',
    'blanket':'毯子','quilt':'被子',
    'towel':'毛巾','curtain':'窗帘',
    'blind':'百叶窗','shade':'遮阳帘',
    'carpet':'地毯','rug':'小地毯',
    'mat':'垫子','tile':'瓷砖',
    'fixture':'固定装置','fitting':'配件',
    'furniture':'家具','decoration':'装饰',
    'ornament':'装饰品','accent':'点缀',
    'trim':'修饰','molding':'线条',
    'finish':'饰面','surface':'表面',
    'texture':'纹理','contour':'轮廓',
    'outline':'轮廓','silhouette':'剪影',
    'reflection':'倒影','photograph':'照片',
    'portrait':'肖像','landscape':'风景',
    'scene':'场景','perspective':'透视',
    'dimension':'维度','proportion':'比例',
    'fraction':'分数','quotient':'商',
    'dividend':'被除数','divisor':'除数',
    'factor':'因子','multiple':'倍数',
    'exponent':'指数','logarithm':'对数',
    'coefficient':'系数','inequality':'不等式',
    'identity':'恒等式','postulate':'公设',
    'conjecture':'猜想','author':'作者',
    'creator':'创建者','contributor':'贡献者',
    'editor':'编辑','reviewer':'审核者',
    'approver':'审批者','distributor':'分发者',
    'customer':'客户','client':'客户',
    'buyer':'买方','purchaser':'采购方',
    'seller':'卖方','dealer':'经销商',
    'trader':'交易员','merchant':'商人',
    'retailer':'零售商','wholesaler':'批发商',
    'partner':'合作伙伴','affiliate':'关联方',
    'subsidiary':'子公司','holding':'控股公司',
    'parent':'母公司','sibling':'兄弟公司',
    'competitor':'竞争对手','rival':'竞争者',
    'opponent':'对手','adversary':'对手',
    'enemy':'敌人','ally':'盟友',
    'friend':'朋友','companion':'同伴',
    'colleague':'同事','coworker':'同事',
    'associate':'合伙人','collaborator':'合作者',
    'teammate':'队友','member':'成员',
    'participant':'参与者','attendee':'出席者',
    'guest':'客人','visitor':'访客',
    'spectator':'观众','audience':'观众',
    'viewer':'观看者','reader':'读者',
    'follower':'关注者','fan':'粉丝',
    'supporter':'支持者','advocate':'倡导者',
    'promoter':'推广者','champion':'冠军',
    'winner':'获胜者','loser':'失败者',
    'victim':'受害者','survivor':'幸存者',
    'bystander':'旁观者','onlooker':'旁观者',
    'passerby':'路人','stranger':'陌生人',
    'foreigner':'外国人','immigrant':'移民',
    'refugee':'难民','exile':'流亡者',
    'expatriate':'侨民','citizen':'公民',
    'national':'国民','resident':'居民',
    'inhabitant':'居民','dweller':'居住者',
    'occupant':'占用者','landlord':'房东',
    'proprietor':'业主','custodian':'保管人',
    'guardian':'监护人','trustee':'受托人',
    'secretary':'秘书','assistant':'助理',
    'deputy':'副手','vice':'副职',
    'successor':'继任者','predecessor':'前任',
    'ancestor':'祖先','descendant':'后代',
    'heir':'继承人','beneficiary':'受益人',
    'recipient':'接收人','sender':'发送人',
    'originator':'发起人','initiator':'发起人',
    'stakeholder':'利益相关者','shareholder':'股东',
    'stockholder':'股东','bondholder':'债券持有人',
    'creditor':'债权人','debtor':'债务人',
    'lender':'贷款人','borrower':'借款人',
    'insurer':'保险人','insured':'被保险人',
    'claimant':'索赔人','defendant':'被告',
    'plaintiff':'原告','petitioner':'申请人',
    'respondent':'被申请人','appellant':'上诉人',
    'appellee':'被上诉人','prosecutor':'检察官',
    'defender':'辩护人','attorney':'律师',
    'lawyer':'律师','counsel':'法律顾问',
    'notary':'公证员','magistrate':'治安法官',
    'judge':'法官','justice':'大法官',
    'referee':'裁判','umpire':'裁判',
    'arbiter':'仲裁人','conciliator':'调解人',
    'negotiator':'谈判人','diplomat':'外交官',
    'ambassador':'大使','envoy':'使节',
    'consul':'领事','delegate':'代表',
    'spokesperson':'发言人','spokesman':'发言人',
    'liaison':'联络人','moderator':'主持人',
    'chairperson':'主席','chairman':'主席',
    'president':'总裁','vicePresident':'副总裁',
    'vice_president':'副总裁',
    'ceo':'首席执行官','CEO':'首席执行官',
    'cfo':'首席财务官','CFO':'首席财务官',
    'cto':'首席技术官','CTO':'首席技术官',
    'coo':'首席运营官','COO':'首席运营官',
    'cio':'首席信息官','CIO':'首席信息官',
    'cpo':'首席产品官','CPO':'首席产品官',
    'cso':'首席安全官','CSO':'首席安全官',
    'cdo':'首席数据官','CDO':'首席数据官',
}

# Activiti 字段注释
ACT_COMMENTS = {
    'LOG_NR_':'日志编号','TYPE_':'事件类型','PROC_DEF_ID_':'流程定义ID',
    'PROC_INST_ID_':'流程实例ID','EXECUTION_ID_':'执行ID','TASK_ID_':'任务ID',
    'TIME_STAMP_':'时间戳','USER_ID_':'用户ID','DATA_':'事件数据',
    'LOCK_OWNER_':'锁持有者','LOCK_TIME_':'锁获取时间','IS_PROCESSED_':'是否已处理',
    'ID_':'ID标识','REV_':'版本号','NAME_':'资源名称','DEPLOYMENT_ID_':'部署ID',
    'BYTES_':'字节数据','GENERATED_':'是否自动生成','VALUE_':'属性值',
    'TITLE_':'部署标题','CATEGORY_':'分类','KEY_':'部署标识','TENANT_ID_':'租户ID',
    'DEPLOY_TIME_':'部署时间','DERIVED_FROM_':'派生来源','DERIVED_FROM_ROOT_':'派生根来源',
    'PARENT_DEPLOYMENT_ID_':'父部署ID','ENGINE_VERSION_':'引擎版本',
    'VERSION_':'版本号','DESCRIPTION_':'描述','RESOURCE_NAME_':'资源文件名',
    'DGRM_RESOURCE_NAME_':'流程图资源文件名','HAS_START_FORM_KEY_':'是否有开始表单键',
    'HAS_GRAPHICAL_NOTATION_':'是否有图形化标记','SUSPENSION_STATE_':'挂起状态',
    'EDITOR_SOURCE_VALUE_ID_':'编辑器源值ID','EDITOR_SOURCE_EXTRA_VALUE_ID_':'编辑器扩展源值ID',
    'EDITOR_SOURCE_':'编辑器源数据','EDITOR_SOURCE_EXTRA_':'编辑器扩展源数据',
    'META_INFO_':'元信息',
    'ACT_ID_':'活动节点ID','IS_ACTIVE_':'是否活跃','IS_CONCURRENT_':'是否并发',
    'IS_SCOPE_':'是否作用域','IS_EVENT_SCOPE_':'是否事件作用域','SUPER_EXEC_':'父执行ID',
    'PARENT_ID_':'父执行ID','ROOT_PROC_INST_ID_':'根流程实例ID','BUSINESS_KEY_':'业务键',
    'CACHED_ENT_STATE_':'缓存实体状态','IS_COUNT_ENABLED_':'是否启用计数',
    'EVT_SUBSCR_COUNT_':'事件订阅计数','TASK_COUNT_':'任务计数','JOB_COUNT_':'作业计数',
    'TIMER_JOB_COUNT_':'定时作业计数','SUSP_JOB_COUNT_':'挂起作业计数',
    'DEADLETTER_JOB_COUNT_':'死信作业计数','EXTERNAL_JOB_COUNT_':'外部作业计数',
    'TASK_DEF_KEY_':'任务定义键','OWNER_':'任务拥有者','ASSIGNEE_':'办理人',
    'DELEGATION_':'委托状态','PRIORITY_':'优先级','CREATE_TIME_':'创建时间',
    'DUE_DATE_':'到期日期','FORM_KEY_':'表单键','CLAIM_TIME_':'认领时间',
    'CLAIM_USER_ID_':'认领用户ID',
    'BYTEARRAY_ID_':'字节数组ID','DOUBLE_':'双精度值','LONG_':'长整型值',
    'TEXT_':'文本值','TEXT2_':'文本值2',
    'GROUP_ID_':'组ID',
    'LOCK_EXP_TIME_':'锁过期时间','EXCLUSIVE_':'是否排他','PROCESS_INSTANCE_ID_':'流程实例ID',
    'PROCESS_DEF_ID_':'流程定义ID','RETRIES_':'重试次数','EXCEPTION_STACK_ID_':'异常堆栈ID',
    'EXCEPTION_MSG_':'异常消息','DUEDATE_':'到期时间','REPEAT_':'重复表达式',
    'HANDLER_TYPE_':'处理器类型','HANDLER_CFG_':'处理器配置',
    'START_TIME_':'开始时间','END_TIME_':'结束时间','DURATION_':'耗时',
    'START_USER_ID_':'发起人ID','DELETE_REASON_':'删除原因',
    'SUPER_PROCESS_INSTANCE_ID_':'父流程实例ID',
    'ACT_TYPE_':'活动类型','ACT_NAME_':'活动名称','CALL_PROC_INST_ID_':'调用流程实例ID',
    'VAR_TYPE_':'变量类型','VAR_INST_ID_':'变量实例ID','ACT_INST_ID_':'活动实例ID',
    'LAST_UPDATED_TIME_':'最后更新时间',
    'TIME_':'时间戳',
    'MESSAGE_':'评论内容','FULL_MSG_':'完整消息',
    'URL_':'附件URL','CONTENT_ID_':'内容ID',
    'SCOPE_ID_':'作用域ID','SUB_SCOPE_ID_':'子作用域ID','SCOPE_TYPE_':'作用域类型',
    'HISTORIC_JOB_ID_':'历史作业ID','EXCEPTION_CFG_':'异常配置',
    'CUSTOMER_ID_':'客户ID','CUSTOMER_NAME_':'客户名称',
    'REFERENCED_ID_':'引用ID','REFERENCED_TYPE_':'引用类型',
    'PROP_KEY_':'属性键','PROP_VALUE_':'属性值',
    'SUPPORTS_EVENT_COMPENSATION_':'是否支持事件补偿',
    'IS_MIRRORED_':'是否镜像','IS_BATCH_EXECUTABLE_':'是否批量可执行',
    'IS_USELESS_':'是否无用','BATCH_ID_':'批次ID',
    'SEARCH_KEY_':'搜索键','SEARCH_KEY2_':'搜索键2',
    'STATUS_':'状态','BATCH_STATUS_':'批次状态',
    'COMPLETE_TIME_':'完成时间','BATCH_PART_DESC_':'批次部分描述',
    'TYPE_NAME_':'类型名称','APP_VERSION_':'应用版本',
    'OPERATION_TYPE_':'操作类型','OPERATION_USER_ID_':'操作用户ID',
    'OPERATION_TIME_':'操作时间','ENTITY_ID_':'实体ID',
    'NEW_VALUE_':'新值','OLD_VALUE_':'旧值',
    'CHANGE_TYPE_':'变更类型','PROPERTY_':'属性',
    'JOB_ID_':'作业ID','JOB_TYPE_':'作业类型',
    'JOB_HANDLER_TYPE_':'作业处理器类型','JOB_HANDLER_CFG_':'作业处理器配置',
    'CREATION_TIME_':'创建时间','SCOPE_DEFINITION_ID_':'作用域定义ID',
    'CORRELATION_ID_':'关联ID','ELEMENT_ID_':'元素ID','ELEMENT_NAME_':'元素名称',
    'EVENT_STATE_':'事件状态','EVENT_TYPE_':'事件类型',
    'ACTIVITY_ID_':'活动ID','ACTIVITY_NAME_':'活动名称','ACTIVITY_TYPE_':'活动类型',
    'COMPENSATION_':'补偿','FIRST_':'是否第一个','SECOND_':'是否第二个',
    'IS_EVENT_SCOPE_':'是否事件作用域','CONFIGURATION_':'配置',
    'IS_MI_ROOT_':'是否多实例根',
}

# 4. 字段名推断函数
def infer_meaning(col_name, table_name=''):
    # Activiti 表优先用专用映射
    if table_name.startswith('act_') and col_name in ACT_COMMENTS:
        return ACT_COMMENTS[col_name]
    # 精确匹配
    if col_name in RULES:
        return RULES[col_name]
    # 下划线命名拆分推断
    parts = col_name.replace('_', ' ').split()
    if len(parts) > 1:
        # 尝试常见后缀推断
        cn = col_name
        # Id/ID 后缀
        if cn.endswith('Id') or cn.endswith('_id') or cn.endswith('_ID'):
            prefix = cn[:-2] if cn.endswith('Id') else cn[:-3]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + 'ID'
            return p + 'ID'
        # Name 后缀
        if cn.endswith('Name') or cn.endswith('_name') or cn.endswith('_NAME'):
            prefix = cn[:-4] if cn.endswith('Name') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '名称'
            return p + '名称'
        # Code 后缀
        if cn.endswith('Code') or cn.endswith('_code') or cn.endswith('_CODE'):
            prefix = cn[:-4] if cn.endswith('Code') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '编码'
            return p + '编码'
        # Type 后缀
        if cn.endswith('Type') or cn.endswith('_type') or cn.endswith('_TYPE'):
            prefix = cn[:-4] if cn.endswith('Type') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '类型'
            return p + '类型'
        # Time 后缀
        if cn.endswith('Time') or cn.endswith('_time') or cn.endswith('_TIME'):
            prefix = cn[:-4] if cn.endswith('Time') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '时间'
            return p + '时间'
        # Date 后缀
        if cn.endswith('Date') or cn.endswith('_date') or cn.endswith('_DATE'):
            prefix = cn[:-4] if cn.endswith('Date') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '日期'
            return p + '日期'
        # Status 后缀
        if cn.endswith('Status') or cn.endswith('_status') or cn.endswith('_STATUS'):
            prefix = cn[:-6] if cn.endswith('Status') else cn[:-7]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '状态'
            return p + '状态'
        # Flag 后缀
        if cn.endswith('Flag') or cn.endswith('_flag') or cn.endswith('_FLAG'):
            prefix = cn[:-4] if cn.endswith('Flag') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '标记'
            return p + '标记'
        # No 后缀
        if cn.endswith('No') or cn.endswith('_no') or cn.endswith('_NO'):
            prefix = cn[:-2] if cn.endswith('No') else cn[:-3]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '编号'
            return p + '编号'
        # Desc 后缀
        if cn.endswith('Desc') or cn.endswith('_desc') or cn.endswith('_DESC'):
            prefix = cn[:-4] if cn.endswith('Desc') else cn[:-5]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '描述'
            return p + '描述'
        # Amount 后缀
        if cn.endswith('Amount') or cn.endswith('_amount') or cn.endswith('_AMOUNT'):
            prefix = cn[:-6] if cn.endswith('Amount') else cn[:-7]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '金额'
            return p + '金额'
        # Count 后缀
        if cn.endswith('Count') or cn.endswith('_count') or cn.endswith('_COUNT'):
            prefix = cn[:-5] if cn.endswith('Count') else cn[:-6]
            p = prefix.rstrip('_')
            if p in RULES:
                return RULES[p] + '计数'
            return p + '计数'
        # is_ 前缀
        if cn.startswith('is_') or cn.startswith('Is') or cn.startswith('is'):
            rest = cn[2:] if cn.startswith('is') else cn[3:]
            rest = rest.lstrip('_')
            if rest:
                return '是否' + rest
        # has_ 前缀
        if cn.startswith('has_') or cn.startswith('Has') or cn.startswith('has'):
            rest = cn[3:] if cn.startswith('has') else cn[4:]
            rest = rest.lstrip('_')
            if rest:
                return '是否有' + rest
    # Activiti 下划线结尾字段
    if col_name.endswith('_') and table_name.startswith('act_'):
        bare = col_name.rstrip('_')
        if bare in ACT_COMMENTS:
            return ACT_COMMENTS[bare]
        if bare in RULES:
            return RULES[bare]
    return ''

# 5. 解析文档并填充
current_table = ''
filled = 0
total_empty = 0
new_lines = []

for i, line in enumerate(lines):
    stripped = line.strip()
    # 检测当前表名
    if stripped.startswith('### ') and not stripped.startswith('### 目录'):
        current_table = stripped[4:].strip()
        new_lines.append(line)
        continue

    # 只处理以 | 开头的表格行
    if not stripped.startswith('|'):
        new_lines.append(line)
        continue

    parts = stripped.split('|')
    # 需要8个部分（首尾空+6列）
    if len(parts) < 8:
        new_lines.append(line)
        continue

    # parts[0]是空的，parts[1]-parts[6]是6列，parts[7]是空的
    col_name = parts[1].strip()
    col_type = parts[2].strip()
    nullable = parts[3].strip()
    default_val = parts[4].strip()
    key_info = parts[5].strip()
    meaning = parts[6].strip()

    # 跳过表头行和分隔行
    if col_name == '字段名' or col_name.startswith('-'):
        new_lines.append(line)
        continue

    # 检查业务含义是否为空
    if not meaning:
        total_empty += 1
        # 优先级1: 数据库注释
        new_meaning = ''
        if current_table in db and col_name in db[current_table]:
            new_meaning = db[current_table][col_name]
        # 优先级2: 字段名推断
        if not new_meaning:
            new_meaning = infer_meaning(col_name, current_table)
        # 优先级3: 待确认
        if not new_meaning:
            new_meaning = '待确认'
        # 构建新行
        new_line = f'| {col_name} | {col_type} | {nullable} | {default_val} | {key_info} | {new_meaning} |\n'
        new_lines.append(new_line)
        filled += 1
        if filled % 200 == 0:
            print(f"已处理 {filled} 个空字段...")
    else:
        new_lines.append(line)

print(f"\n空业务含义字段总数: {total_empty}")
print(f"已填充字段数: {filled}")

# 6. 写回文件
with open(MD_PATH, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print(f"\n文件已更新: {MD_PATH}")
