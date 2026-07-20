-- 完整低代码演示：网络割接流程

CREATE TABLE IF NOT EXISTS demo_network_cutover (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cutover_no VARCHAR(64) NOT NULL,
  title VARCHAR(200) NOT NULL,
  applicant VARCHAR(64) NOT NULL,
  device_scope VARCHAR(500) NOT NULL,
  impact_level VARCHAR(16) NOT NULL DEFAULT 'MEDIUM',
  risk_score INT NOT NULL DEFAULT 0,
  window_start DATETIME NOT NULL,
  window_end DATETIME NOT NULL,
  implementation_plan TEXT NOT NULL,
  rollback_plan TEXT NOT NULL,
  verification_plan TEXT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  result_summary TEXT NULL,
  create_by VARCHAR(64) DEFAULT '',
  update_by VARCHAR(64) DEFAULT '',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted TINYINT DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE KEY uk_demo_cutover_no (cutover_no),
  KEY idx_demo_cutover_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码演示-网络割接';

INSERT IGNORE INTO pms_lowcode_entity
  (code,name,table_name,description,biz_type,status,version,create_by,update_by,deleted)
VALUES
  ('demo_network_cutover','网络割接','demo_network_cutover','完整低代码割接流程演示','NETWORK_CUTOVER','PUBLISHED',1,'demo','demo',0);
SET @cutover_entity_id=(SELECT id FROM pms_lowcode_entity WHERE code='demo_network_cutover' LIMIT 1);

INSERT INTO pms_lowcode_field
  (entity_id,name,label,field_type,length,nullable,primary_key,indexed,unique_flag,sort_order,create_by,update_by,deleted)
SELECT @cutover_entity_id, f.name, f.label, f.field_type, f.length, f.nullable, 0, f.indexed, f.unique_flag, f.sort_order, 'demo','demo',0
FROM (
  SELECT 'cutover_no' name,'割接编号' label,'STRING' field_type,64 length,0 nullable,1 indexed,1 unique_flag,1 sort_order UNION ALL
  SELECT 'title','割接主题','STRING',200,0,0,0,2 UNION ALL
  SELECT 'applicant','申请人','STRING',64,0,0,0,3 UNION ALL
  SELECT 'device_scope','设备范围','TEXT',NULL,0,0,0,4 UNION ALL
  SELECT 'impact_level','影响等级','STRING',16,0,1,0,5 UNION ALL
  SELECT 'risk_score','风险分','INTEGER',NULL,0,0,0,6 UNION ALL
  SELECT 'window_start','窗口开始','DATETIME',NULL,0,1,0,7 UNION ALL
  SELECT 'window_end','窗口结束','DATETIME',NULL,0,0,0,8 UNION ALL
  SELECT 'implementation_plan','实施方案','TEXT',NULL,0,0,0,9 UNION ALL
  SELECT 'rollback_plan','回退方案','TEXT',NULL,0,0,0,10 UNION ALL
  SELECT 'verification_plan','验证方案','TEXT',NULL,0,0,0,11 UNION ALL
  SELECT 'status','状态','STRING',32,0,1,0,12 UNION ALL
  SELECT 'result_summary','结果摘要','TEXT',NULL,1,0,0,13
) f
WHERE NOT EXISTS (
  SELECT 1 FROM pms_lowcode_field x WHERE x.entity_id=@cutover_entity_id AND x.name=f.name AND x.deleted=0
);

INSERT IGNORE INTO pms_lowcode_form
  (code,name,description,form_config,events,status,version,biz_type,create_by,update_by,deleted)
VALUES (
 'form_demo_network_cutover','网络割接申请单','割接申请、审核、实施、验证共用表单',
 JSON_OBJECT('title','网络割接申请','layout','top','labelWidth','110px','fields',JSON_ARRAY(
  JSON_OBJECT('id','f1','prop','cutover_no','label','割接编号','type','input','required',true,'span',12),
  JSON_OBJECT('id','f2','prop','title','label','割接主题','type','input','required',true,'span',12),
  JSON_OBJECT('id','f3','prop','applicant','label','申请人','type','input','required',true,'span',12),
  JSON_OBJECT('id','f4','prop','impact_level','label','影响等级','type','select','required',true,'span',12,'props',JSON_OBJECT('options',JSON_ARRAY(
    JSON_OBJECT('label','低','value','LOW'),JSON_OBJECT('label','中','value','MEDIUM'),
    JSON_OBJECT('label','高','value','HIGH'),JSON_OBJECT('label','重大','value','CRITICAL')))),
  JSON_OBJECT('id','f5','prop','window_start','label','窗口开始','type','datetime','required',true,'span',12),
  JSON_OBJECT('id','f6','prop','window_end','label','窗口结束','type','datetime','required',true,'span',12),
  JSON_OBJECT('id','f7','prop','device_scope','label','设备范围','type','textarea','required',true,'span',24),
  JSON_OBJECT('id','f8','prop','implementation_plan','label','实施方案','type','textarea','required',true,'span',24),
  JSON_OBJECT('id','f9','prop','rollback_plan','label','回退方案','type','textarea','required',true,'span',24),
  JSON_OBJECT('id','f10','prop','verification_plan','label','验证方案','type','textarea','required',true,'span',24),
  JSON_OBJECT('id','f11','prop','risk_score','label','风险分','type','number','required',true,'span',12),
  JSON_OBJECT('id','f12','prop','status','label','状态','type','select','span',12),
  JSON_OBJECT('id','f13','prop','result_summary','label','结果摘要','type','textarea','span',24)
 )),
 JSON_OBJECT('onSubmit',JSON_OBJECT('type','PROCESS','code','demo_network_cutover')),
 'PUBLISHED',1,'NETWORK_CUTOVER','demo','demo',0
);

INSERT IGNORE INTO pms_lowcode_list
 (code,name,description,list_config,status,version,biz_type,create_by,update_by,deleted)
VALUES (
 'list_demo_network_cutover','网络割接台账','割接全生命周期台账',
 JSON_OBJECT('title','网络割接台账','entityCode','demo_network_cutover','showSelection',false,'showIndex',true,
 'columns',JSON_ARRAY(
  JSON_OBJECT('id','c1','prop','cutover_no','label','割接编号','width',150),
  JSON_OBJECT('id','c2','prop','title','label','割接主题','minWidth',220),
  JSON_OBJECT('id','c3','prop','applicant','label','申请人','width',100),
  JSON_OBJECT('id','c4','prop','impact_level','label','影响等级','width',100,'type','tag'),
  JSON_OBJECT('id','c5','prop','window_start','label','割接窗口','width',180,'type','datetime'),
  JSON_OBJECT('id','c6','prop','risk_score','label','风险分','width',90),
  JSON_OBJECT('id','c7','prop','status','label','状态','width',120,'type','tag')),
 'filters',JSON_ARRAY(
  JSON_OBJECT('id','q1','prop','cutover_no','label','割接编号','type','input','span',6),
  JSON_OBJECT('id','q2','prop','status','label','状态','type','select','span',6)),
 'operations',JSON_ARRAY(
  JSON_OBJECT('id','o1','label','查看','action','view','type','primary'),
  JSON_OBJECT('id','o2','label','编辑','action','edit','type','primary')),
 'pagination',JSON_OBJECT('pageSize',20)),
 'PUBLISHED',1,'NETWORK_CUTOVER','demo','demo',0
);

INSERT IGNORE INTO pms_lowcode_rule
 (code,name,description,type,definition,status,version,biz_type,create_by,update_by,deleted)
VALUES (
 'rule_demo_cutover_risk','割接风险分级决策表','根据风险分决定审批等级','DECISION_TABLE',
 JSON_OBJECT('hitPolicy','FIRST',
  'conditionColumns',JSON_ARRAY(JSON_OBJECT('field','risk_score','operator','LE')),
  'actionColumns',JSON_ARRAY(JSON_OBJECT('field','risk_level'),JSON_OBJECT('field','approval_group')),
  'rows',JSON_ARRAY(
   JSON_OBJECT('conditions',JSON_ARRAY(JSON_OBJECT('value',30)),'actions',JSON_ARRAY(JSON_OBJECT('value','LOW'),JSON_OBJECT('value','network_manager'))),
   JSON_OBJECT('conditions',JSON_ARRAY(JSON_OBJECT('value',60)),'actions',JSON_ARRAY(JSON_OBJECT('value','MEDIUM'),JSON_OBJECT('value','change_manager'))),
   JSON_OBJECT('conditions',JSON_ARRAY(JSON_OBJECT('value',100)),'actions',JSON_ARRAY(JSON_OBJECT('value','HIGH'),JSON_OBJECT('value','change_committee')))
  )),
 'PUBLISHED',1,'NETWORK_CUTOVER','demo','demo',0
);

INSERT IGNORE INTO pms_lowcode_microflow
 (code,name,description,definition,status,version,biz_type,create_by,update_by,deleted)
VALUES (
 'microflow_demo_cutover_precheck','割接前置检查','风险路由与回退方案完整性检查',
 JSON_OBJECT('nodes',JSON_ARRAY(
  JSON_OBJECT('id','start','type','START','label','开始','x',80,'y',160,'config',JSON_OBJECT()),
  JSON_OBJECT('id','rule','type','CALL_RULE','label','风险分级','x',300,'y',160,'config',JSON_OBJECT('ruleCode','rule_demo_cutover_risk')),
  JSON_OBJECT('id','condition','type','CONDITION','label','存在回退方案','x',520,'y',160,'config',JSON_OBJECT('expression','rollback_plan != null && rollback_plan.size() > 20')),
  JSON_OBJECT('id','success','type','END','label','检查通过','x',760,'y',100,'config',JSON_OBJECT()),
  JSON_OBJECT('id','failed','type','THROW_EXCEPTION','label','检查失败','x',760,'y',240,'config',JSON_OBJECT('code','CUTOVER_PRECHECK_FAILED','message','回退方案不完整'))
 ),'edges',JSON_ARRAY(
  JSON_OBJECT('id','e1','source','start','target','rule'),
  JSON_OBJECT('id','e2','source','rule','target','condition'),
  JSON_OBJECT('id','e3','source','condition','target','success','sourcePort','condition-true'),
  JSON_OBJECT('id','e4','source','condition','target','failed','sourcePort','condition-false')
 ),'variables',JSON_OBJECT('inputs',JSON_ARRAY(),'locals',JSON_ARRAY())),
 'PUBLISHED',1,'NETWORK_CUTOVER','demo','demo',0
);

INSERT IGNORE INTO pms_lowcode_process_binding
 (process_definition_key,process_definition_name,node_form_bindings,task_callbacks,status,create_by,update_by,deleted)
VALUES (
 'demo_network_cutover','网络割接流程',
 JSON_ARRAY(
  JSON_OBJECT('nodeId','risk_review','formCode','form_demo_network_cutover'),
  JSON_OBJECT('nodeId','command_confirm','formCode','form_demo_network_cutover'),
  JSON_OBJECT('nodeId','cutover_execute','formCode','form_demo_network_cutover'),
  JSON_OBJECT('nodeId','business_verify','formCode','form_demo_network_cutover'),
  JSON_OBJECT('nodeId','rollback_task','formCode','form_demo_network_cutover'),
  JSON_OBJECT('nodeId','archive_task','formCode','form_demo_network_cutover')),
 JSON_OBJECT('risk_review',JSON_OBJECT('onCreate','microflow_demo_cutover_precheck')),
 'ACTIVE','demo','demo',0
);

INSERT IGNORE INTO demo_network_cutover
 (cutover_no,title,applicant,device_scope,impact_level,risk_score,window_start,window_end,
  implementation_plan,rollback_plan,verification_plan,status,result_summary,create_by,update_by)
VALUES
 ('CO20260718001','核心园区双核心路由器割接','张明','CORE-R01、CORE-R02','HIGH',72,
  '2026-07-18 00:30:00','2026-07-18 03:30:00','备份配置，逐台切换路由邻居并观察收敛。',
  '任一核心指标异常时恢复原配置并切回旧链路，预计回退时间15分钟。',
  '验证办公网、生产网、互联网出口及监控告警。','PENDING_REVIEW',NULL,'demo','demo'),
 ('CO20260712001','分支机构防火墙版本升级','李华','FW-BRANCH-03','MEDIUM',45,
  '2026-07-12 01:00:00','2026-07-12 02:00:00','主备切换后升级备用设备，再切回升级主设备。',
  '升级失败立即启动旧版本镜像并恢复配置。',
  '验证VPN、互联网访问、日志上传。','COMPLETED','割接成功，无业务中断。','demo','demo');

INSERT INTO sys_menu
 (menu_name,parent_id,order_num,path,component,menu_type,visible,status,perms,icon,create_by,update_by,deleted)
SELECT '网络割接演示',parent_id,80,'network-cutover-demo',NULL,'M','1','1','','Connection','demo','demo',0
FROM sys_menu WHERE menu_name='低代码管理' AND deleted=0 LIMIT 1;
SET @cutover_menu_id=LAST_INSERT_ID();

INSERT INTO sys_menu
 (menu_name,parent_id,order_num,path,component,menu_type,visible,status,perms,icon,create_by,update_by,deleted)
VALUES
 ('网络割接台账',@cutover_menu_id,1,'/lowcode/list/list_demo_network_cutover','','L','1','1','lowcode:page:list:list_demo_network_cutover','Connection','demo','demo',0),
 ('网络割接申请',@cutover_menu_id,2,'/lowcode/form/form_demo_network_cutover','','L','1','1','lowcode:page:form:form_demo_network_cutover','EditPen','demo','demo',0),
 ('割接数据-查看',@cutover_menu_id,10,'','','F','1','1','lowcode:data:demo_network_cutover:list','','demo','demo',0),
 ('割接数据-新增',@cutover_menu_id,11,'','','F','1','1','lowcode:data:demo_network_cutover:add','','demo','demo',0),
 ('割接数据-编辑',@cutover_menu_id,12,'','','F','1','1','lowcode:data:demo_network_cutover:edit','','demo','demo',0),
 ('割接数据-删除',@cutover_menu_id,13,'','','F','1','1','lowcode:data:demo_network_cutover:delete','','demo','demo',0);

SET @admin_role_id=(SELECT id FROM sys_role WHERE role_code='admin' AND deleted=0 LIMIT 1);
INSERT INTO sys_role_menu (role_id,menu_id,create_by,update_by,deleted)
SELECT @admin_role_id,id,'demo','demo',0 FROM sys_menu
WHERE (id=@cutover_menu_id OR parent_id=@cutover_menu_id) AND deleted=0;
