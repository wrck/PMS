# -*- coding: utf-8 -*-
import json
import os
from collections import defaultdict

BASE = r'd:\EclipseWorkspace\Parctice\PMS\PMS-struts\docs\util'

def load_json(filename):
    filepath = os.path.join(BASE, filename)
    # Try different encodings
    for enc in ['utf-16', 'utf-8', 'utf-8-sig', 'gbk', 'latin-1']:
        try:
            with open(filepath, 'r', encoding=enc) as f:
                return json.load(f)
        except (UnicodeDecodeError, json.JSONDecodeError):
            continue
    raise Exception(f"Cannot load {filename}")

# 加载所有数据
columns_data = load_json('step1_columns.json')
indexes_data = load_json('step2_indexes.json')
table_rows_data = load_json('step3_table_rows.json')
enums_data = load_json('step4_enums.json')
column_rel_data = load_json('step5_column_rel.json')
basic_data_type_data = load_json('step6_basic_data.json')
samples_data = load_json('step7_samples.json')
fkeys_data = load_json('step8_fkeys.json')

# 按表名组织列信息
table_columns = defaultdict(list)
for col in columns_data:
    table_columns[col['TABLE_NAME']].append(col)

# 按表名组织索引信息
table_indexes = defaultdict(list)
for idx in indexes_data:
    table_indexes[idx['TABLE_NAME']].append(idx)

# 按表名组织索引（合并同索引名的列）
table_index_grouped = defaultdict(lambda: defaultdict(list))
for idx in indexes_data:
    table_index_grouped[idx['TABLE_NAME']][idx['INDEX_NAME']].append(idx)

# 表数据量映射
table_rows_map = {}
for t in table_rows_data:
    table_rows_map[t['TABLE_NAME']] = t

# 外键关系映射
table_fkeys = defaultdict(list)
for fk in fkeys_data:
    table_fkeys[fk['TABLE_NAME']].append(fk)

# 枚举值按字段分组
enum_groups = defaultdict(list)
for e in enums_data:
    enum_groups[e['field']].append(e)

# 表业务含义映射（基于代码和样例数据推断）
table_business_meaning = {
    'pm_project': '项目主表 - 存储项目基本信息，包括项目类型、状态、编码、名称等',
    'pm_project_state': '项目状态流转记录表 - 记录项目状态变更历史',
    'pm_project_member': '项目成员表 - 记录项目参与人员及角色',
    'pm_presales_project_header': '售前项目主表 - 存储售前项目申请信息',
    'pm_presales_project_duration': '售前项目工期表 - 记录售前项目工期信息',
    'pm_presales_project_product_line': '售前项目产品线表 - 记录售前项目关联的产品线',
    'pm_cl_callback': '售后回访表 - 记录售后回访信息',
    'pm_cl_quesnaire_result_line': '问卷结果明细表 - 记录回访问卷的调查结果',
    'pm_subcontract_project_header': '分包项目主表 - 存储分包项目信息',
    'prob_main': '问题跟踪主表 - 记录项目问题/故障信息',
    'prob_handle': '问题处理记录表 - 记录问题处理过程',
    'pm_project_maintenance': '维保项目表 - 记录维保项目信息',
    'pm_project_shipment': '项目发货表 - 记录项目发货信息',
    'pm_project_soft_version': '项目软件版本表 - 记录项目软件版本信息',
    'pm_project_soft_version_history': '项目软件版本历史表',
    'pm_order_data_from_erp_source': 'ERP订单数据源表 - 从ERP系统同步的订单数据',
    'pm_order_line_from_erp': 'ERP订单行表 - 从ERP同步的订单行明细',
    'pm_column_of_relationship': '项目字段映射关系表 - 定义不同项目类型的动态字段映射',
    'fnd_user_info': '用户信息基础表',
    'fnd_department': '部门信息表 - 组织架构部门信息',
    'fnd_basic_data_type': '基础数据类型表 - 定义系统枚举值分类',
    'fnd_basic_data': '基础数据表 - 存储系统枚举值明细',
    'fnd_company': '公司信息表',
    'fnd_role': '角色表',
    'fnd_user_role': '用户角色关联表',
    'addressee_info': '收件人信息表',
    'agent_info': '代理商信息表',
    'app_accessory_info': '附件信息表',
    'app_comment': '审批评论表',
    'mes_oqc_info': 'MES出货检验信息表',
    'tb_sys_log': '系统日志表',
    't_user': '用户认证表',
    't_user_info': '用户详细信息表',
    't_user_role': '用户角色关联表',
    't_role': '角色表',
    'view_warranty': '维保视图表',
    'view_warranty_with_presales': '维保含售前视图表',
    'view_warranty_temp': '维保临时表',
    'shipment_barcode_from_spms_unique': '发货条码唯一表',
    'tmp_tb_project_shipment': '项目发货临时表',
    'tmp_tb_contract_shipment': '合同发货临时表',
    'tmp_tb_view_shipment_info_4_pm': '项目发货信息临时表',
    'temp_query_shipment_barcode': '发货条码查询临时表',
    'af_industry_asset': '行业资产表',
    'af_industry_asset_leak_relation': '行业资产漏洞关联表',
    'af_industry_asset_project_relation': '行业资产项目关联表',
    'af_industry_leak': '行业漏洞表',
    'af_industry_leak_warning': '行业漏洞预警表',
}

# 字段业务含义推断（基于column_comment和样例数据）
field_meaning_overrides = {
    # pm_project
    ('pm_project', 'projectId'): '项目ID（主键）',
    ('pm_project', 'projectType'): '项目类型：10=实施类, afss=安全服务, afxx=安全营销',
    ('pm_project', 'projectCode'): '项目编码',
    ('pm_project', 'projectName'): '项目名称',
    ('pm_project', 'projectState'): '项目状态：10=待确认, 20=进行中, 30=已暂停, 31=暂停待确认, 32=暂停中, 40=待关闭, 50=已关闭, 100=已完成',
    ('pm_project', 'isback'): '回退状态',
    ('pm_project', 'column001'): '办事处编码',
    ('pm_project', 'column002'): '客户编码',
    ('pm_project', 'column003'): '客户名称',
    ('pm_project', 'column004'): '市场部编码',
    ('pm_project', 'column005'): '系统部/行业',
    ('pm_project', 'column006'): '拓展部/客户类型',
    ('pm_project', 'column007'): '子行业',
    ('pm_project', 'column008'): '不予跟踪原因',
    ('pm_project', 'column009'): '订单创建时间',
    ('pm_project', 'column010'): '项目阶段',
    ('pm_project', 'column011'): '项目子阶段',
    ('pm_project', 'column012'): '项目标记',
    ('pm_project', 'columno12_readonly'): 'column012只读标记',
    ('pm_project', 'column013'): '最终用户',
    ('pm_project', 'column014'): '备注',
    ('pm_project', 'customerProjectName'): '客户项目名称',
    ('pm_project', 'salesType'): '销售类型：01=直销, 02=渠道',
    ('pm_project', 'majorProjectLevel'): '重大项目级别',
    ('pm_project', 'compId'): '公司ID',
    ('pm_project', 'createTime'): '创建时间',
    ('pm_project', 'createBy'): '创建人',
    ('pm_project', 'updateTime'): '更新时间',
    ('pm_project', 'updateBy'): '更新人',
    ('pm_project', 'effectiveFrom'): '生效时间',
    ('pm_project', 'effectiveTo'): '失效时间',
    ('pm_project', 'disabled'): '是否禁用',
    ('pm_project', 'projectStartTime'): '项目开始时间',
    ('pm_project', 'projectRefreshTime'): '项目刷新时间',
    ('pm_project', 'projectCloseTime'): '项目关闭时间',
    ('pm_project', 'customInfo'): '自定义信息',
    ('pm_project', 'customConfig'): '自定义配置',
    # pm_project_member
    ('pm_project_member', 'memberRole'): '成员角色：10=项目经理, 15=副项目经理, 20=项目成员, 30=技术负责人, 40=质量负责人, 50=安全负责人, 60=远程支持, 71=驻场工程师, 80=其他',
    # pm_presales_project_header
    ('pm_presales_project_header', 'applyState'): '申请状态：1=待审批, 2=已审批',
    # prob_main
    ('prob_main', 'status'): '问题状态：0=草稿, 1=待处理, 4=已解决, 5=已关闭, 6=已验证, 8=处理中',
    # pm_subcontract_project_header
    ('pm_subcontract_project_header', 'state'): '分包状态：-100=已拒绝, -30=已撤回, -20=已退回, -15=待修改, 0=草稿, 10=待审批, 15=审批中, 20=已通过, 30=执行中, 40=已完成',
    # fnd_department
    ('fnd_department', 'isparam'): '是否参数化部门：0=否, 1=是',
}

def get_field_meaning(table_name, col_name, col_comment):
    key = (table_name, col_name)
    if key in field_meaning_overrides:
        return field_meaning_overrides[key]
    if col_comment:
        return col_comment
    # 根据字段名推断
    name_lower = col_name.lower()
    if name_lower.endswith('id') or name_lower.endswith('_id'):
        return 'ID标识'
    if name_lower.endswith('code'):
        return '编码'
    if name_lower.endswith('name'):
        return '名称'
    if name_lower.endswith('time') or name_lower.endswith('date'):
        return '时间'
    if name_lower.endswith('by'):
        return '操作人'
    if 'create' in name_lower:
        return '创建信息'
    if 'update' in name_lower:
        return '更新信息'
    if name_lower == 'state' or name_lower == 'status':
        return '状态'
    if name_lower == 'remark' or name_lower == 'description':
        return '备注/描述'
    if name_lower == 'disabled':
        return '是否禁用'
    return ''

# 枚举值含义推断
enum_meaning_map = {
    'projectType': {'10': '实施类项目', 'afss': '安全服务项目', 'afxx': '安全营销项目'},
    'projectState': {'10': '待确认', '20': '进行中', '30': '已暂停', '31': '暂停待确认', '32': '暂停中', '40': '待关闭', '50': '已关闭', '100': '已完成'},
    'memberRole': {'10': '项目经理', '15': '副项目经理', '20': '项目成员', '30': '技术负责人', '40': '质量负责人', '50': '安全负责人', '60': '远程支持', '71': '驻场工程师', '80': '其他', 'other': '其他'},
    'applyState_presales': {'1': '待审批', '2': '已审批', 'null': '未申请'},
    'status_prob': {'0': '草稿', '1': '待处理', '4': '已解决', '5': '已关闭', '6': '已验证', '8': '处理中'},
    'state_subcontract': {'-100': '已拒绝', '-30': '已撤回', '-20': '已退回', '-15': '待修改', '0': '草稿', '10': '待审批', '15': '审批中', '20': '已通过', '30': '执行中', '40': '已完成'},
    'source_order': {'d365': 'D365系统', 'spms': 'SPMS系统'},
    'orderType': {},
    'isparam_dept': {'0': '否', '1': '是'},
    'salesType': {'01': '直销', '02': '渠道'},
}

# 过滤掉视图和临时表（保留业务核心表）
def is_core_table(table_name):
    skip_prefixes = ['view_', 'tmp_', 'temp_', 'dp_v_', 'act_', 'fb_', 'ehr_']
    for p in skip_prefixes:
        if table_name.startswith(p):
            return False
    return True

# 获取所有表名（按字母排序）
all_tables = sorted(set(table_columns.keys()))

# 索引分析
def analyze_indexes(table_name):
    idx_group = table_index_grouped.get(table_name, {})
    redundant = []
    missing = []
    
    idx_list = list(idx_group.keys())
    # 检查冗余索引：如果一个索引的前缀列与另一个索引完全相同
    for i, name1 in enumerate(idx_list):
        cols1 = [c['COLUMN_NAME'] for c in idx_group[name1]]
        for j, name2 in enumerate(idx_list):
            if i >= j:
                continue
            cols2 = [c['COLUMN_NAME'] for c in idx_group[name2]]
            # 如果cols1是cols2的前缀，则name1可能是冗余的
            if len(cols1) <= len(cols2) and cols1 == cols2[:len(cols1)] and name1 != 'PRIMARY':
                redundant.append(f"索引 `{name1}` 可能被索引 `{name2}` 覆盖（前缀列相同）")
    
    return redundant, missing

# 生成Markdown文档
lines = []
lines.append('# PMS主数据库 dppms_d365 完整数据字典')
lines.append('')
lines.append('> 生成时间：2026-05-19 | 数据库：dppms_d365 | 排除表前缀：act_*, dp_v_*, fb_*, ehr_*')
lines.append('')

# 目录
lines.append('## 目录')
lines.append('')
lines.append('- [1. 数据库概览](#1-数据库概览)')
lines.append('- [2. 表结构详细说明](#2-表结构详细说明)')
lines.append('- [3. 枚举值汇总](#3-枚举值汇总)')
lines.append('- [4. 字段映射关系](#4-字段映射关系)')
lines.append('- [5. 基础数据类型](#5-基础数据类型)')
lines.append('- [6. 外键关系](#6-外键关系)')
lines.append('- [7. 索引有效性分析](#7-索引有效性分析)')
lines.append('')

# 1. 数据库概览
lines.append('## 1. 数据库概览')
lines.append('')
total_tables = len([t for t in all_tables if is_core_table(t)])
total_rows = sum(t.get('TABLE_ROWS', 0) for t in table_rows_data if is_core_table(t.get('TABLE_NAME', '')))
lines.append(f'- **业务表总数**：{total_tables}')
lines.append(f'- **总数据行数（估算）**：{total_rows:,}')
lines.append(f'- **外键约束数**：{len(fkeys_data)}')
lines.append('')

# 表数据量TOP20
lines.append('### 1.1 数据量TOP 20表')
lines.append('')
lines.append('| 表名 | 业务含义 | 估算行数 | 数据大小 | 索引大小 |')
lines.append('|------|----------|----------|----------|----------|')
top20 = sorted(table_rows_data, key=lambda x: x.get('TABLE_ROWS', 0), reverse=True)[:20]
for t in top20:
    tn = t['TABLE_NAME']
    meaning = table_business_meaning.get(tn, '')
    rows = t.get('TABLE_ROWS', 0)
    data_len = t.get('DATA_LENGTH', 0)
    idx_len = t.get('INDEX_LENGTH', 0)
    data_mb = f"{data_len / 1024 / 1024:.1f} MB" if data_len else "0 MB"
    idx_mb = f"{idx_len / 1024 / 1024:.1f} MB" if idx_len else "0 MB"
    lines.append(f'| {tn} | {meaning} | {rows:,} | {data_mb} | {idx_mb} |')
lines.append('')

# 2. 表结构详细说明
lines.append('## 2. 表结构详细说明')
lines.append('')

for table_name in all_tables:
    if not is_core_table(table_name):
        continue
    
    meaning = table_business_meaning.get(table_name, '')
    row_info = table_rows_map.get(table_name, {})
    rows = row_info.get('TABLE_ROWS', 0) if row_info else 0
    data_len = row_info.get('DATA_LENGTH', 0) if row_info else 0
    idx_len = row_info.get('INDEX_LENGTH', 0) if row_info else 0
    
    lines.append(f'### {table_name}')
    lines.append('')
    lines.append(f'**业务含义**：{meaning if meaning else "（待补充）"}')
    lines.append('')
    data_mb = f"{data_len / 1024 / 1024:.1f} MB" if data_len else "0 MB"
    idx_mb = f"{idx_len / 1024 / 1024:.1f} MB" if idx_len else "0 MB"
    lines.append(f'**数据量**：约 {rows:,} 行 | 数据大小：{data_mb} | 索引大小：{idx_mb}')
    lines.append('')
    
    # 字段列表
    cols = table_columns.get(table_name, [])
    if cols:
        lines.append('**字段列表**：')
        lines.append('')
        lines.append('| 字段名 | 类型 | 可空 | 默认值 | 键 | 业务含义 |')
        lines.append('|--------|------|------|--------|-----|----------|')
        for col in cols:
            cn = col['COLUMN_NAME']
            ct = col['COLUMN_TYPE']
            nullable = col['IS_NULLABLE']
            default = str(col['COLUMN_DEFAULT']) if col['COLUMN_DEFAULT'] is not None else ''
            key = col['COLUMN_KEY']
            extra = col['EXTRA']
            comment = col['COLUMN_COMMENT']
            biz_meaning = get_field_meaning(table_name, cn, comment)
            if extra == 'auto_increment':
                biz_meaning += '（自增）' if biz_meaning else '自增主键'
            lines.append(f'| {cn} | {ct} | {nullable} | {default} | {key} | {biz_meaning} |')
        lines.append('')
    
    # 索引列表
    idx_group = table_index_grouped.get(table_name, {})
    if idx_group:
        lines.append('**索引列表**：')
        lines.append('')
        lines.append('| 索引名 | 列 | 唯一性 | 索引类型 |')
        lines.append('|--------|-----|--------|----------|')
        for idx_name, idx_cols in idx_group.items():
            col_list = ', '.join([c['COLUMN_NAME'] for c in sorted(idx_cols, key=lambda x: x['SEQ_IN_INDEX'])])
            non_unique = '否' if idx_cols[0]['NON_UNIQUE'] == 0 else '是'
            idx_type = idx_cols[0]['INDEX_TYPE']
            lines.append(f'| {idx_name} | {col_list} | {non_unique} | {idx_type} |')
        lines.append('')
    
    # 外键
    fkeys = table_fkeys.get(table_name, [])
    if fkeys:
        lines.append('**外键关系**：')
        lines.append('')
        for fk in fkeys:
            lines.append(f'- `{fk['COLUMN_NAME']}` → `{fk['REFERENCED_TABLE_NAME']}.{fk['REFERENCED_COLUMN_NAME']}`（约束名：{fk['CONSTRAINT_NAME']}）')
        lines.append('')
    
    # 样例数据
    samples = samples_data.get(table_name)
    if samples and isinstance(samples, list) and len(samples) > 0:
        lines.append('**样例数据**：')
        lines.append('')
        # 表头
        sample_keys = list(samples[0].keys())
        lines.append('| ' + ' | '.join(sample_keys) + ' |')
        lines.append('| ' + ' | '.join(['---'] * len(sample_keys)) + ' |')
        for s in samples[:3]:
            vals = []
            for k in sample_keys:
                v = str(s.get(k, ''))[:50]  # 截断过长的值
                vals.append(v)
            lines.append('| ' + ' | '.join(vals) + ' |')
        lines.append('')
    
    lines.append('---')
    lines.append('')

# 3. 枚举值汇总
lines.append('## 3. 枚举值汇总')
lines.append('')
lines.append('| 字段名 | 枚举值 | 推断含义 | 数据分布数量 |')
lines.append('|--------|--------|----------|-------------|')
for field, values in enum_groups.items():
    meaning_map = enum_meaning_map.get(field, {})
    for v in values:
        val = str(v['val']) if v['val'] is not None else 'NULL'
        cnt = v['cnt']
        meaning = meaning_map.get(val, meaning_map.get(str(v['val']), ''))
        lines.append(f'| {field} | {val} | {meaning} | {cnt:,} |')
lines.append('')

# 4. 字段映射关系
lines.append('## 4. 字段映射关系')
lines.append('')
lines.append('pm_column_of_relationship 定义了不同项目类型下动态字段（column001~column014）的业务含义映射。')
lines.append('')
lines.append('| ID | 项目类型 | 字段编码 | 字段名 | 字段描述 | 生效时间 | 失效时间 |')
lines.append('|-----|----------|----------|--------|----------|----------|----------|')
for cr in column_rel_data:
    lines.append(f'| {cr["id"]} | {cr["projectType"]} | {cr["columnCode"]} | {cr["colemnName"]} | {cr["columnDesc"]} | {cr.get("effectiveFrom","")} | {cr.get("effectiveTo","") or "永久"} |')
lines.append('')

# 5. 基础数据类型
lines.append('## 5. 基础数据类型')
lines.append('')
lines.append('fnd_basic_data_type 定义了系统中所有枚举值分类。')
lines.append('')
lines.append('| ID | 类型编码 | 类型名称 | 状态 | 生效时间 | 失效时间 |')
lines.append('|-----|----------|----------|------|----------|----------|')
for bd in basic_data_type_data:
    status = '启用' if bd.get('status') == 1 else '停用' if bd.get('status') == 0 else str(bd.get('status', ''))
    lines.append(f'| {bd["id"]} | {bd["dataTypeCode"]} | {bd["dataTypeName"]} | {status} | {bd.get("effectiveFrom","")} | {bd.get("effectiveTo","") or "永久"} |')
lines.append('')

# 6. 外键关系
lines.append('## 6. 外键关系')
lines.append('')
if fkeys_data:
    lines.append('| 约束名 | 表名 | 列名 | 引用表 | 引用列 |')
    lines.append('|--------|------|------|--------|--------|')
    for fk in fkeys_data:
        lines.append(f'| {fk["CONSTRAINT_NAME"]} | {fk["TABLE_NAME"]} | {fk["COLUMN_NAME"]} | {fk["REFERENCED_TABLE_NAME"]} | {fk["REFERENCED_COLUMN_NAME"]} |')
else:
    lines.append('未发现外键约束（业务关系通过应用层维护）。')
lines.append('')

# 7. 索引有效性分析
lines.append('## 7. 索引有效性分析')
lines.append('')

# 冗余索引检测
lines.append('### 7.1 可能的冗余索引')
lines.append('')
found_redundant = False
for table_name in all_tables:
    if not is_core_table(table_name):
        continue
    redundant, missing = analyze_indexes(table_name)
    if redundant:
        found_redundant = True
        lines.append(f'**{table_name}**：')
        for r in redundant:
            lines.append(f'- {r}')
        lines.append('')
if not found_redundant:
    lines.append('未发现明显的冗余索引。')
lines.append('')

# 缺失索引建议
lines.append('### 7.2 缺失索引建议')
lines.append('')
lines.append('基于表数据量和常见查询模式，建议关注以下索引：')
lines.append('')

# 分析大表缺少索引的情况
large_tables_no_idx = []
for t in table_rows_data:
    tn = t['TABLE_NAME']
    if not is_core_table(tn):
        continue
    rows = t.get('TABLE_ROWS', 0)
    if rows > 10000:
        idx_group = table_index_grouped.get(tn, {})
        has_non_pk = any(name != 'PRIMARY' for name in idx_group.keys())
        if not has_non_pk:
            large_tables_no_idx.append((tn, rows))

if large_tables_no_idx:
    lines.append('**大表但仅有主键索引的表**：')
    lines.append('')
    for tn, rows in large_tables_no_idx:
        lines.append(f'- `{tn}`（{rows:,} 行）- 建议根据查询条件添加合适索引')
    lines.append('')

# 常见查询字段索引建议
lines.append('**常见查询字段索引建议**：')
lines.append('')
lines.append('- `pm_project.projectState` - 项目状态是高频查询条件，建议确认索引覆盖')
lines.append('- `pm_project.projectType` - 项目类型是高频查询条件')
lines.append('- `pm_project_member.projectId` - 按项目查成员是核心查询')
lines.append('- `pm_project.createTime` - 时间范围查询频繁')
lines.append('- `pm_order_data_from_erp_source.source` - 订单来源筛选')
lines.append('')

# 写入文件
output_path = os.path.join(BASE, 'docs', '03-database', 'complete-data-dictionary.md')
os.makedirs(os.path.dirname(output_path), exist_ok=True)
with open(output_path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f"数据字典已生成：{output_path}")
print(f"总行数：{len(lines)}")
