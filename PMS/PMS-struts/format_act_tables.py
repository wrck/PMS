# -*- coding: utf-8 -*-
import json
import re

with open(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\act_tables_data.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

table_order = [
    'act_evt_log', 'act_ge_bytearray', 'act_ge_property', 'act_hi_actinst',
    'act_hi_attachment', 'act_hi_comment', 'act_hi_detail', 'act_hi_identitylink',
    'act_hi_procinst', 'act_hi_taskinst', 'act_hi_varinst', 'act_id_group',
    'act_id_info', 'act_id_membership', 'act_id_user', 'act_procdef_info',
    'act_re_deployment', 'act_re_model', 'act_re_procdef', 'act_ru_event_subscr',
    'act_ru_execution', 'act_ru_identitylink', 'act_ru_job', 'act_ru_task',
    'act_ru_task_callback_task_w04649', 'act_ru_variable'
]

table_meanings = {
    'act_evt_log': 'Activiti事件日志表，记录流程引擎产生的事件日志',
    'act_ge_bytearray': 'Activiti通用字节数组表，存储流程定义资源文件、序列化数据等二进制内容',
    'act_ge_property': 'Activiti通用属性表，存储引擎级别的属性键值对（如版本号等）',
    'act_hi_actinst': 'Activiti历史活动实例表，记录流程中每个活动（节点）的执行历史',
    'act_hi_attachment': 'Activiti历史附件表，记录流程实例或任务的附件信息',
    'act_hi_comment': 'Activiti历史评论表，记录流程实例或任务的审批意见/评论',
    'act_hi_detail': 'Activiti历史详情表，记录流程变量的变更历史详情',
    'act_hi_identitylink': 'Activiti历史参与者关系表，记录流程实例或任务的历史参与者信息',
    'act_hi_procinst': 'Activiti历史流程实例表，记录已完成的流程实例信息',
    'act_hi_taskinst': 'Activiti历史任务实例表，记录已完成/已删除的任务实例信息',
    'act_hi_varinst': 'Activiti历史变量实例表，记录流程变量的历史值',
    'act_id_group': 'Activiti身份-组表，存储用户组（角色）信息',
    'act_id_info': 'Activiti身份-信息表，存储用户的扩展信息',
    'act_id_membership': 'Activiti身份-成员关系表，存储用户与组的关联关系',
    'act_id_user': 'Activiti身份-用户表，存储用户基本信息',
    'act_procdef_info': 'Activiti流程定义信息表，存储流程定义的动态信息（如版本更新状态）',
    'act_re_deployment': 'Activiti仓库-部署表，记录流程部署操作信息',
    'act_re_model': 'Activiti仓库-模型表，存储流程模型设计器中创建的模型信息',
    'act_re_procdef': 'Activiti仓库-流程定义表，存储已部署的流程定义信息',
    'act_ru_event_subscr': 'Activiti运行时-事件订阅表，记录运行时的事件订阅（如信号/消息事件）',
    'act_ru_execution': 'Activiti运行时-执行实例表，记录流程执行路径信息',
    'act_ru_identitylink': 'Activiti运行时-参与者关系表，记录当前运行流程/任务的参与者',
    'act_ru_job': 'Activiti运行时-作业表，记录定时器、异步操作等作业信息',
    'act_ru_task': 'Activiti运行时-任务表，记录当前待办任务信息',
    'act_ru_task_callback_task_w04649': 'Activiti运行时-任务回调扩展表，自定义扩展的任务回调信息表（业务定制）',
    'act_ru_variable': 'Activiti运行时-变量表，记录当前运行流程实例的变量值'
}

# Context-specific field meanings: (table_name, field_name) -> meaning
context_field_meanings = {
    # act_evt_log
    ('act_evt_log', 'TYPE_'): '事件类型（如process-start, task-create等）',
    ('act_evt_log', 'TIME_STAMP_'): '事件时间戳',
    ('act_evt_log', 'DATA_'): '事件数据（二进制序列化）',
    ('act_evt_log', 'LOCK_OWNER_'): '事件处理锁拥有者',
    ('act_evt_log', 'LOCK_TIME_'): '事件处理锁获取时间',
    ('act_evt_log', 'IS_PROCESSED_'): '是否已处理（0:未处理 1:已处理）',
    
    # act_ge_bytearray
    ('act_ge_bytearray', 'GENERATED_'): '是否自动生成（0:否 1:是）',
    
    # act_hi_actinst
    ('act_hi_actinst', 'ACT_ID_'): '活动节点ID（BPMN中的节点标识）',
    ('act_hi_actinst', 'ACT_NAME_'): '活动节点名称',
    ('act_hi_actinst', 'ACT_TYPE_'): '活动节点类型（如userTask, startEvent等）',
    
    # act_hi_attachment
    ('act_hi_attachment', 'TYPE_'): '附件类型',
    ('act_hi_attachment', 'TIME_'): '附件创建时间',
    
    # act_hi_comment
    ('act_hi_comment', 'TYPE_'): '评论类型（如comment, event等）',
    ('act_hi_comment', 'ACTION_'): '评论动作（如AddComment, AddAttachment等）',
    ('act_hi_comment', 'TIME_'): '评论时间',
    
    # act_hi_detail
    ('act_hi_detail', 'TYPE_'): '详情类型（如VariableUpdate, FormProperty等）',
    ('act_hi_detail', 'ACT_INST_ID_'): '活动实例ID，关联act_hi_actinst',
    ('act_hi_detail', 'TIME_'): '变量变更时间',
    
    # act_hi_identitylink
    ('act_hi_identitylink', 'TYPE_'): '参与者关系类型（如candidate, participant, assignee等）',
    
    # act_hi_procinst
    ('act_hi_procinst', 'START_USER_ID_'): '流程发起人ID',
    ('act_hi_procinst', 'START_ACT_ID_'): '开始活动节点ID',
    ('act_hi_procinst', 'END_ACT_ID_'): '结束活动节点ID',
    
    # act_hi_taskinst
    ('act_hi_taskinst', 'CLAIM_TIME_'): '任务签收/认领时间',
    
    # act_hi_varinst
    ('act_hi_varinst', 'LAST_UPDATED_TIME_'): '最后更新时间',
    
    # act_id_group
    ('act_id_group', 'TYPE_'): '组类型（如assignment, security-role等）',
    
    # act_id_info
    ('act_id_info', 'TYPE_'): '信息类型（如account, userinfo等）',
    ('act_id_info', 'KEY_'): '信息键名',
    ('act_id_info', 'PARENT_ID_'): '父信息ID',
    
    # act_id_user
    ('act_id_user', 'PWD_'): '用户密码',
    ('act_id_user', 'PICTURE_ID_'): '头像资源ID，关联act_ge_bytearray',
    
    # act_re_deployment
    ('act_re_deployment', 'DEPLOY_TIME_'): '部署时间',
    
    # act_re_model
    ('act_re_model', 'KEY_'): '模型标识Key',
    ('act_re_model', 'LAST_UPDATE_TIME_'): '最后更新时间',
    
    # act_re_procdef
    ('act_re_procdef', 'KEY_'): '流程定义Key（BPMN中的process id）',
    ('act_re_procdef', 'HAS_GRAPHICAL_NOTATION_'): '是否有图形化标记（0:否 1:是）',
    
    # act_ru_event_subscr
    ('act_ru_event_subscr', 'EVENT_TYPE_'): '事件类型（如message, signal等）',
    ('act_ru_event_subscr', 'EVENT_NAME_'): '事件名称',
    ('act_ru_event_subscr', 'ACTIVITY_ID_'): '关联活动节点ID',
    ('act_ru_event_subscr', 'CREATED_'): '订阅创建时间',
    
    # act_ru_execution
    ('act_ru_execution', 'CACHED_ENT_STATE_'): '缓存实体状态位掩码',
    ('act_ru_execution', 'LOCK_TIME_'): '流程实例锁定时间',
    
    # act_ru_identitylink
    ('act_ru_identitylink', 'TYPE_'): '参与者关系类型（如candidate, participant, assignee等）',
    
    # act_ru_job
    ('act_ru_job', 'TYPE_'): '作业类型（如timer, message等）',
    ('act_ru_job', 'PROCESS_INSTANCE_ID_'): '流程实例ID',
    
    # act_ru_variable
    ('act_ru_variable', 'TYPE_'): '变量类型（如string, integer, boolean等）',
}

# Common field meanings (fallback)
common_field_meanings = {
    'ID_': '主键ID',
    'REV_': '乐观锁版本号，用于并发控制',
    'NAME_': '名称',
    'VALUE_': '值',
    'BYTES_': '二进制数据内容',
    'GENERATED_': '是否自动生成（0:否 1:是）',
    'DEPLOYMENT_ID_': '部署ID，关联act_re_deployment',
    'USER_ID_': '用户ID',
    'GROUP_ID_': '组ID',
    'PROC_DEF_ID_': '流程定义ID，关联act_re_procdef',
    'PROC_INST_ID_': '流程实例ID，关联act_ru_execution',
    'EXECUTION_ID_': '执行实例ID，关联act_ru_execution',
    'TASK_ID_': '任务ID，关联act_ru_task',
    'ASSIGNEE_': '任务受理人/办理人',
    'OWNER_': '任务拥有者（委托前的原受理人）',
    'DESCRIPTION_': '描述',
    'PRIORITY_': '优先级',
    'SUSPENSION_STATE_': '挂起状态（1:激活 2:挂起）',
    'TENANT_ID_': '租户ID，多租户隔离',
    'CREATE_TIME_': '创建时间',
    'DUE_DATE_': '到期日期',
    'FORM_KEY_': '表单Key，关联表单标识',
    'CATEGORY_': '分类',
    'CALL_PROC_INST_ID_': '调用子流程实例ID',
    'DURATION_': '持续时间（毫秒）',
    'START_TIME_': '开始时间',
    'END_TIME_': '结束时间',
    'URL_': '附件URL地址',
    'CONTENT_ID_': '内容ID，关联act_ge_bytearray',
    'MESSAGE_': '评论内容',
    'FULL_MSG_': '完整消息内容（二进制）',
    'VAR_TYPE_': '变量类型',
    'BYTEARRAY_ID_': '字节数组ID，关联act_ge_bytearray',
    'DOUBLE_': '双精度浮点值',
    'LONG_': '长整型值',
    'TEXT_': '文本值',
    'TEXT2_': '文本值2（存储长文本的第二部分）',
    'BUSINESS_KEY_': '业务主键',
    'DELETE_REASON_': '删除原因',
    'SUPER_PROCESS_INSTANCE_ID_': '父流程实例ID',
    'TASK_DEF_KEY_': '任务定义Key（BPMN中的任务标识）',
    'PARENT_TASK_ID_': '父任务ID',
    'DELEGATION_': '委托状态（PENDING:待委托 RESOLVED:已委托）',
    'FIRST_': '名',
    'LAST_': '姓',
    'EMAIL_': '邮箱',
    'PASSWORD_': '密码',
    'VERSION_': '版本号',
    'META_INFO_': '元信息（JSON格式）',
    'EDITOR_SOURCE_VALUE_ID_': '编辑器源数据ID，关联act_ge_bytearray',
    'EDITOR_SOURCE_EXTRA_VALUE_ID_': '编辑器扩展源数据ID，关联act_ge_bytearray',
    'INFO_JSON_ID_': '信息JSON数据ID，关联act_ge_bytearray',
    'RESOURCE_NAME_': '资源文件名',
    'DGRM_RESOURCE_NAME_': '流程图资源文件名',
    'HAS_START_FORM_KEY_': '是否有开始表单Key（0:否 1:是）',
    'EXCLUSIVE_': '是否独占执行（0:否 1:是）',
    'RETRIES_': '重试次数',
    'EXCEPTION_STACK_ID_': '异常堆栈ID，关联act_ge_bytearray',
    'EXCEPTION_MSG_': '异常消息',
    'REPEAT_': '重复执行表达式（如定时器的cron表达式）',
    'HANDLER_TYPE_': '处理器类型',
    'HANDLER_CFG_': '处理器配置',
    'LOCK_EXP_TIME_': '锁过期时间',
    'DUEDATE_': '到期执行时间',
    'LOCK_OWNER_': '锁拥有者',
    'CONFIGURATION_': '配置信息',
    'SUPER_EXEC_': '父流程执行实例ID',
    'PARENT_ID_': '父执行实例ID',
    'IS_ACTIVE_': '是否激活（0:否 1:是）',
    'IS_CONCURRENT_': '是否并发（0:否 1:是）',
    'IS_SCOPE_': '是否作用域（0:否 1:是）',
    'IS_EVENT_SCOPE_': '是否事件作用域（0:否 1:是）',
    'ACT_ID_': '当前活动节点ID',
    'ACT_NAME_': '活动节点名称',
    'ACT_TYPE_': '活动节点类型（如userTask, startEvent等）',
    'LOG_NR_': '日志编号（自增主键）',
    'varId': '变量ID（自定义扩展字段）',
    'linkId': '关联ID（自定义扩展字段）',
}

def format_size(size_bytes):
    if size_bytes is None:
        return '0 MB'
    mb = size_bytes / (1024 * 1024)
    if mb >= 1:
        return f'{mb:.2f} MB'
    kb = size_bytes / 1024
    return f'{kb:.2f} KB'

def get_constraint_str(col, create_sql):
    constraints = []
    if col['key'] == 'PRI':
        constraints.append('PRI')
    elif col['key'] == 'UNI':
        constraints.append('UNI')
    elif col['key'] == 'MUL':
        constraints.append('MUL')
    if 'auto_increment' in col.get('extra', '').lower():
        constraints.append('auto_increment')
    col_name = col['field']
    fk_pattern = rf'FOREIGN KEY \(`{re.escape(col_name)}`\)'
    if re.search(fk_pattern, create_sql):
        constraints.append('FK')
    return ', '.join(constraints) if constraints else '-'

def get_default_str(col):
    default = col.get('default')
    if default is None and col['null'] == 'YES':
        return 'NULL'
    elif default is None:
        return '-'
    elif default == '':
        return "''"
    else:
        return str(default)

def get_field_meaning(field_name, table_name):
    # First check context-specific
    key = (table_name, field_name)
    if key in context_field_meanings:
        return context_field_meanings[key]
    # Then check common
    if field_name in common_field_meanings:
        return common_field_meanings[field_name]
    return '业务含义待确认'

def get_indexes_table(indexes):
    idx_groups = {}
    for idx in indexes:
        key = idx['key_name']
        if key not in idx_groups:
            idx_groups[key] = {
                'non_unique': idx['non_unique'],
                'index_type': idx['index_type'],
                'columns': []
            }
        idx_groups[key]['columns'].append({
            'column_name': idx['column_name'],
            'seq': idx['seq_in_index']
        })
    
    lines = []
    lines.append('| 索引名 | 索引类型 | 唯一性 | 索引字段 |')
    lines.append('|--------|----------|--------|----------|')
    if not idx_groups:
        lines.append('| - | - | - | - |')
    else:
        for key_name, info in idx_groups.items():
            sorted_cols = sorted(info['columns'], key=lambda x: x['seq'])
            col_str = ', '.join([c['column_name'] for c in sorted_cols])
            uniqueness = 'UNIQUE' if info['non_unique'] == 0 else 'NON-UNIQUE'
            lines.append(f'| {key_name} | {info["index_type"]} | {uniqueness} | {col_str} |')
    return '\n'.join(lines)

def get_foreign_keys_table(create_sql):
    fk_pattern = r'CONSTRAINT\s+`(\w+)`\s+FOREIGN KEY\s+\(`(\w+)`\)\s+REFERENCES\s+`(\w+)`\s+\(`(\w+)`\)'
    fks = re.findall(fk_pattern, create_sql)
    if not fks:
        return None
    lines = []
    lines.append('| 外键名 | 本表字段 | 引用表 | 引用字段 |')
    lines.append('|--------|----------|--------|----------|')
    for fk_name, fk_col, ref_table, ref_col in fks:
        lines.append(f'| {fk_name} | {fk_col} | {ref_table} | {ref_col} |')
    return '\n'.join(lines)

output_lines = []
for idx, table_name in enumerate(table_order, 1):
    t = data[table_name]
    
    table_comment = t['table_comment'] if t['table_comment'] else '-'
    business_meaning = table_meanings.get(table_name, '业务含义待确认')
    rows = t['table_rows']
    data_size = format_size(t['data_length'] + t['index_length'])
    
    output_lines.append(f'### {idx} {table_name} -- {table_comment if table_comment != "-" else business_meaning}')
    output_lines.append('')
    output_lines.append('| 属性 | 值 |')
    output_lines.append('|------|-----|')
    output_lines.append(f'| 对象类型 | BASE TABLE |')
    output_lines.append(f'| 业务含义 | {business_meaning} |')
    output_lines.append(f'| 数据量 | ~{rows} 行 |')
    output_lines.append(f'| 数据大小 | {data_size} |')
    output_lines.append('')
    output_lines.append('**字段列表**')
    output_lines.append('')
    output_lines.append('| 字段名 | 数据类型 | 可空 | 默认值 | 约束 | 字段描述 | 业务含义 |')
    output_lines.append('|--------|----------|------|--------|------|----------|----------|')
    
    for col in t['columns']:
        field_name = col['field']
        data_type = col['type']
        nullable = col['null']
        default_val = get_default_str(col)
        constraint = get_constraint_str(col, t['create_sql'])
        col_comment = t['column_comments'].get(field_name, '')
        meaning = get_field_meaning(field_name, table_name)
        
        output_lines.append(f'| {field_name} | {data_type} | {nullable} | {default_val} | {constraint} | {col_comment} | {meaning} |')
    
    output_lines.append('')
    output_lines.append('**索引列表**')
    output_lines.append('')
    output_lines.append(get_indexes_table(t['indexes']))
    
    fk_table = get_foreign_keys_table(t['create_sql'])
    if fk_table:
        output_lines.append('')
        output_lines.append('**外键列表**')
        output_lines.append('')
        output_lines.append(fk_table)
    
    output_lines.append('')
    output_lines.append('---')
    output_lines.append('')

result = '\n'.join(output_lines)
with open(r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\act_tables_dict.txt', 'w', encoding='utf-8') as f:
    f.write(result)

print("Done! Report written to act_tables_dict.txt")
