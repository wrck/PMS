#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
从 Java 代码中提取数据库字段与业务含义的映射关系。

扫描范围：
1. iBatis XML 映射文件（config-ibaits/*.xml）中的 resultMap 和 SQL 别名
2. Java 实体类（src/**/*.java）中的字段注释
3. SQL 脚本文件（**/*.sql）中的 COMMENT

输出格式：
{
  "table_name": {
    "fields": {
      "field_name": "从代码中提取的业务含义"
    }
  }
}
"""

import re
import json
import os
from collections import defaultdict

# ============ 路径配置 ============
PROJECT_ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
IBATIS_DIR = os.path.join(PROJECT_ROOT, 'config-ibaits')
SRC_DIR = os.path.join(PROJECT_ROOT, 'src')
OUT_DIR = os.path.join(os.path.dirname(__file__), 'schema_data')
OUT_PATH = os.path.join(OUT_DIR, 'code_biz_meanings.json')

# 重点关注表前缀
FOCUS_PREFIXES = ('t_', 'pm_', 'fnd_', 'fb_', 'prob_', 'act_', 'dp_')

# 通用字段（无业务含义，跳过注释提取）
COMMON_FIELDS = {
    'serialVersionUID', 'effectiveFrom', 'effectiveTo',
    'createTime', 'updateTime', 'createBy', 'updateBy',
}


def read_file(path):
    """读取文件内容，自动处理编码"""
    for enc in ('utf-8', 'gbk', 'latin-1'):
        try:
            with open(path, 'r', encoding=enc) as f:
                return f.read()
        except (UnicodeDecodeError, UnicodeError):
            continue
    return ''


def clean_comment(comment):
    """清理注释，去除 JavaDoc 标签和无关内容，只保留业务含义"""
    if not comment:
        return ''
    # 去除 @author, @date, @version, @since 等 JavaDoc 标签
    comment = re.sub(r'@author\s+\S+', '', comment)
    comment = re.sub(r'@date\s+\S+', '', comment)
    comment = re.sub(r'@version\s+\S+', '', comment)
    comment = re.sub(r'@since\s+\S+', '', comment)
    comment = re.sub(r'@see\s+\S+', '', comment)
    comment = re.sub(r'@return\s*', '', comment)
    comment = re.sub(r'@param\s*\S*\s*', '', comment)
    # 去除 @title:, @desc:, @connection: 等自定义标签内容
    comment = re.sub(r'@title:\s*', '', comment)
    comment = re.sub(r'@desc:\s*', '', comment)
    comment = re.sub(r'@connection:\s*', '', comment)
    # 去除 @xxx 引用（如 @user.username, @dp_act_proc_type.desc）
    comment = re.sub(r'@\S+', '', comment)
    # 去除残留的 // 前缀
    comment = comment.lstrip('/').strip()
    # 去除多余空白
    comment = re.sub(r'\s+', ' ', comment).strip()
    # 过滤类描述性注释（不是字段级业务含义）
    class_desc_patterns = [
        r'^对应\w+的表',  # "对应activity的表"
        r'^\w+bean$',     # "xxx bean"
        r'^\w+Bean$',     # "xxx Bean"
    ]
    for pat in class_desc_patterns:
        if re.match(pat, comment):
            return ''
    return comment


def is_valid_table_name(name):
    """检查是否是有效的数据库表名"""
    if not name:
        return False
    # 不能包含中文
    if any('\u4e00' <= c <= '\u9fff' for c in name):
        return False
    # 必须以字母或下划线开头，只包含字母数字下划线
    if not re.match(r'^[a-zA-Z_]\w*$', name):
        return False
    # 长度合理
    if len(name) > 64 or len(name) < 2:
        return False
    return True


def is_valid_field_name(name):
    """检查是否是有效的数据库字段名"""
    if not name:
        return False
    # 不能包含中文、空格、XML标记
    if any('\u4e00' <= c <= '\u9fff' for c in name):
        return False
    if '<' in name or '>' in name or ' ' in name or '=' in name:
        return False
    if '"' in name or "'" in name:
        return False
    # 必须以字母或下划线开头
    if not re.match(r'^[a-zA-Z_]\w*$', name):
        return False
    return True


# ============================================================
# 1. 解析 iBatis XML 映射文件
# ============================================================

def parse_ibatis_xml(directory):
    """
    从 iBatis XML 中提取：
    - resultMap 中的 property -> column 映射及 class 属性
    - SQL 中的表名引用
    - SQL 中的字段别名（AS）
    - INSERT 语句中的字段列表
    """
    result_map_data = {}  # {resultMap_id: {class_name, props: {property: column}}}
    sql_aliases = defaultdict(dict)  # {table_name: {column_alias: expression}}
    insert_fields = defaultdict(dict)  # {table_name: {field_name: ''}}

    if not os.path.isdir(directory):
        print(f'[WARN] iBatis 目录不存在: {directory}')
        return result_map_data, sql_aliases, insert_fields

    for fname in os.listdir(directory):
        if not fname.endswith('.xml'):
            continue
        fpath = os.path.join(directory, fname)
        content = read_file(fpath)

        # --- 解析 resultMap ---
        rm_pattern = re.compile(
            r'<resultMap[^>]*\bid\s*=\s*["\']([^"\']+)["\'][^>]*>(.*?)</resultMap>',
            re.DOTALL
        )
        for rm_match in rm_pattern.finditer(content):
            rm_id = rm_match.group(1)
            rm_body = rm_match.group(2)
            rm_full_tag = rm_match.group(0)

            # 提取 class 属性（Java 类名）
            class_match = re.search(r'\bclass\s*=\s*["\']([^"\']+)["\']', rm_full_tag)
            class_name = ''
            if class_match:
                full_class = class_match.group(1)
                class_name = full_class.split('.')[-1]  # 取简单类名

            props = {}
            # 匹配 <result property="xxx" column="yyy"/>
            for r_match in re.finditer(
                r'<result\s+[^>]*property\s*=\s*["\']([^"\']+)["\'][^>]*column\s*=\s*["\']([^"\']+)["\'][^>]*/?\s*>',
                rm_body
            ):
                prop = r_match.group(1)
                col = r_match.group(2)
                if is_valid_field_name(prop) and is_valid_field_name(col):
                    props[prop] = col

            # column 在 property 前面的情况
            for r_match in re.finditer(
                r'<result\s+[^>]*column\s*=\s*["\']([^"\']+)["\'][^>]*property\s*=\s*["\']([^"\']+)["\'][^>]*/?\s*>',
                rm_body
            ):
                col = r_match.group(1)
                prop = r_match.group(2)
                if prop not in props and is_valid_field_name(prop) and is_valid_field_name(col):
                    props[prop] = col

            if props or class_name:
                result_map_data[rm_id] = {
                    'class_name': class_name,
                    'props': props
                }

        # --- 解析 SQL 中的表引用 ---
        table_ref_pattern = re.compile(
            r'(?:FROM|JOIN)\s+[`"]?(\w+)[`"]?',
            re.IGNORECASE
        )
        tables_in_file = set()
        SQL_KEYWORDS = {
            'SELECT', 'WHERE', 'ON', 'AND', 'OR', 'SET', 'INTO', 'VALUES',
            'UPDATE', 'DELETE', 'INSERT', 'CREATE', 'ALTER', 'DROP', 'TABLE',
            'INDEX', 'NULL', 'NOT', 'EXISTS', 'INFORMATION_SCHEMA', 'DUAL',
            'CASE', 'WHEN', 'THEN', 'ELSE', 'END', 'AS', 'IS', 'LIKE',
            'GROUP', 'ORDER', 'BY', 'HAVING', 'LIMIT', 'OFFSET', 'UNION',
        }
        for tm in table_ref_pattern.finditer(content):
            tname = tm.group(1)
            if tname.upper() not in SQL_KEYWORDS and is_valid_table_name(tname):
                tables_in_file.add(tname)

        # --- 解析 SQL 中的 AS 别名 ---
        as_pattern = re.compile(
            r'(?:`?\w+`?\.)?`?(\w+)`?\s+AS\s+`?(\w+)`?',
            re.IGNORECASE
        )
        for as_match in as_pattern.finditer(content):
            expr = as_match.group(1)
            alias = as_match.group(2)
            for tname in tables_in_file:
                if any(tname.startswith(p) for p in FOCUS_PREFIXES):
                    if alias not in sql_aliases[tname]:
                        sql_aliases[tname][alias] = expr

        # --- 提取 INSERT 语句中的字段 ---
        # 先去除 CDATA 标记
        clean_content = content.replace('<![CDATA[', '').replace(']]>', '')
        insert_pattern = re.compile(
            r'insert\s+into\s+[`"]?(\w+)[`"]?\s*\(([^)]+)\)\s*values',
            re.IGNORECASE | re.DOTALL
        )
        for ins_match in insert_pattern.finditer(clean_content):
            table_name = ins_match.group(1)
            if not is_valid_table_name(table_name):
                continue
            field_list_str = ins_match.group(2)
            raw_fields = field_list_str.split(',')
            for f in raw_fields:
                f = f.strip().strip('`"').strip()
                if f and is_valid_field_name(f) and not f.startswith('#'):
                    if f not in insert_fields[table_name]:
                        insert_fields[table_name][f] = ''

    return result_map_data, sql_aliases, insert_fields


# ============================================================
# 2. 解析 Java 实体类
# ============================================================

def parse_java_entities(src_dir):
    """
    从 Java 实体类中提取：
    - 类注释中的表名对应关系
    - 字段注释（行注释或上方注释）
    - 字段名与业务含义的映射
    """
    entity_data = {}

    for root, dirs, files in os.walk(src_dir):
        if 'test' in root.lower():
            continue
        for fname in files:
            if not fname.endswith('.java'):
                continue
            fpath = os.path.join(root, fname)
            content = read_file(fpath)

            class_name = fname.replace('.java', '')
            table_hint = ''
            fields = {}

            # --- 提取类注释中的表名 ---
            class_comment_match = re.search(
                r'/\*\*\s*(.*?)\s*\*/', content, re.DOTALL
            )
            if class_comment_match:
                cc = class_comment_match.group(1)
                # 去除 * 号和 JavaDoc 标签
                cc_clean = re.sub(r'\*\s*', ' ', cc)
                table_match = re.search(r'(?:对应|映射|表名)[:\s]*(\w+)', cc_clean)
                if table_match and is_valid_table_name(table_match.group(1)):
                    table_hint = table_match.group(1)

            # --- 提取字段注释 ---
            lines = content.split('\n')

            inline_comment_re = re.compile(
                r'private\s+\S+\s+(\w+)\s*[;=]\s*//\s*(.+?)$'
            )

            prev_comment = ''
            prev_is_javadoc = False
            javadoc_buffer = []
            in_javadoc = False

            for i, line in enumerate(lines):
                stripped = line.strip()

                # 处理多行 JavaDoc
                if stripped.startswith('/**'):
                    in_javadoc = True
                    javadoc_buffer = []
                    first_line = stripped[3:].strip()
                    if first_line.endswith('*/'):
                        first_line = first_line[:-2].strip()
                        in_javadoc = False
                    first_line = first_line.lstrip('*').strip()
                    if first_line:
                        javadoc_buffer.append(first_line)
                    if not in_javadoc:
                        prev_comment = ' '.join(javadoc_buffer).strip()
                        prev_is_javadoc = True
                    continue

                if in_javadoc:
                    if '*/' in stripped:
                        last_part = stripped.replace('*/', '').strip().lstrip('*').strip()
                        if last_part:
                            javadoc_buffer.append(last_part)
                        in_javadoc = False
                        prev_comment = ' '.join(javadoc_buffer).strip()
                        prev_is_javadoc = True
                    else:
                        jl = stripped.lstrip('*').strip()
                        if jl and not jl.startswith('@'):
                            javadoc_buffer.append(jl)
                    continue

                # 单行注释
                if stripped.startswith('//'):
                    prev_comment = stripped[2:].strip()
                    prev_is_javadoc = False
                    continue

                # 单行块注释
                if stripped.startswith('/*') and stripped.endswith('*/') and not stripped.startswith('/**'):
                    prev_comment = stripped[2:-2].strip()
                    prev_is_javadoc = False
                    continue

                # 检测字段声明行
                field_match = re.match(
                    r'(?:\s*(?:@[\w.]+(?:\([^)]*\))?\s*)*)\s*private\s+(\S+)\s+(\w+)\s*[;=]',
                    stripped
                )
                if field_match:
                    field_name = field_match.group(2)

                    # 跳过通用字段
                    if field_name == 'serialVersionUID':
                        prev_comment = ''
                        continue

                    # 优先使用行内注释
                    inline_match = inline_comment_re.search(stripped)
                    if inline_match and inline_match.group(1) == field_name:
                        comment = inline_match.group(2).strip()
                        comment = clean_comment(comment)
                        if comment:
                            fields[field_name] = comment
                    elif prev_comment:
                        comment = clean_comment(prev_comment)
                        if comment:
                            fields[field_name] = comment

                    prev_comment = ''
                    prev_is_javadoc = False
                    continue

                # 非注释、非字段行，清空上方注释
                if stripped and not stripped.startswith('@') and not stripped.startswith('*'):
                    if not prev_is_javadoc:
                        prev_comment = ''
                    else:
                        # JavaDoc 后如果紧跟空行或非字段行，也清空
                        prev_is_javadoc = False

            if fields or table_hint:
                entity_data[class_name] = {
                    'table_hint': table_hint,
                    'fields': fields
                }

    return entity_data


# ============================================================
# 3. 解析 SQL 脚本文件
# ============================================================

def parse_sql_files(project_root):
    """从 SQL 文件中提取 COMMENT"""
    sql_data = {}

    for root, dirs, files in os.walk(project_root):
        if 'target' in root:
            continue
        for fname in files:
            if not fname.endswith('.sql'):
                continue
            fpath = os.path.join(root, fname)
            content = read_file(fpath)

            # CREATE TABLE ... COMMENT='xxx'
            ct_pattern = re.compile(
                r'CREATE\s+TABLE\s+[`"]?(\w+)[`"]?\s*\((.*?)\)\s*(?:ENGINE.*?)?COMMENT\s*=?\s*["\']([^"\']*)["\']',
                re.IGNORECASE | re.DOTALL
            )
            for ct_match in ct_pattern.finditer(content):
                table_name = ct_match.group(1)
                table_body = ct_match.group(2)

                fields = {}
                fc_pattern = re.compile(
                    r'[`"](\w+)[`"]\s+\S+(?:\([^)]*\))?\s+COMMENT\s+["\']([^"\']*)["\']',
                    re.IGNORECASE
                )
                for fc_match in fc_pattern.finditer(table_body):
                    fn = fc_match.group(1)
                    fc = fc_match.group(2)
                    if fc and is_valid_field_name(fn):
                        fields[fn] = fc

                if fields:
                    if table_name not in sql_data:
                        sql_data[table_name] = {}
                    sql_data[table_name].update(fields)

            # ALTER TABLE ADD COLUMN ... COMMENT
            alter_pattern = re.compile(
                r'ALTER\s+TABLE\s+[`"]?(\w+)[`"]?\s+ADD\s+(?:COLUMN\s+)?[`"](\w+)[`"]\s+\S+(?:\([^)]*\))?\s+COMMENT\s+["\']([^"\']*)["\']',
                re.IGNORECASE
            )
            for alt_match in alter_pattern.finditer(content):
                table_name = alt_match.group(1)
                field_name = alt_match.group(2)
                comment = alt_match.group(3)
                if comment and is_valid_field_name(field_name):
                    if table_name not in sql_data:
                        sql_data[table_name] = {}
                    sql_data[table_name][field_name] = comment

    return sql_data


# ============================================================
# 4. 智能关联：将 Java 属性名映射到数据库列名
# ============================================================

def java_field_to_db_column(java_field):
    """将 Java 驼峰命名转换为数据库下划线命名"""
    if not java_field:
        return ''
    result = []
    for i, ch in enumerate(java_field):
        if ch.isupper():
            if i > 0:
                result.append('_')
            result.append(ch.lower())
        else:
            result.append(ch)
    return ''.join(result)


# ============================================================
# 5. 已知表与实体类的映射关系
# ============================================================

ENTITY_TABLE_MAP = {
    # 项目管理域 pm_*
    'Project': 'pm_project_header',
    'ProjectMember': 'pm_project_member',
    'ProjectContract': 'pm_project_contract',
    'ProjectDeliver': 'pm_project_deliver',
    'ProjectLog': 'pm_project_log',
    'ProjectPlan': 'pm_project_plan',
    'ProjectPlanEvent': 'pm_project_plan_event',
    'ProjectTask': 'pm_project_task',
    'ProjectWeekly': 'pm_project_weekly',
    'WeeklyContent': 'pm_project_weekly_content',
    'WeeklyFeedback': 'pm_project_weekly_feedback',
    'ProjectSoftVersion': 'pm_project_soft_version',
    'ProjectSoftVersionEntity': 'pm_project_soft_version',
    'ShipmentInfo': 'pm_project_shipment_info',
    'ProductType': 'pm_project_product_line',
    'Product': 'pm_project_product_line',
    'Presales': 'pm_presales_project_header',
    'PresalesProduct': 'pm_presales_project_product_line',
    'PresalesComment': 'pm_presales_project_comment',
    'PresalesTask': 'pm_presales_project_task',
    'CallBack': 'pm_cl_callback',
    'CallBackComment': 'pm_cl_callback_comment',
    'CallBackQuesnaire': 'pm_cl_callback_quesnaire',
    'PmClosedLoopQuesnaire': 'pm_cl_quesnaire_template_header',
    'PmClosedLoopQuesnaireLine': 'pm_cl_quesnaire_template_line',
    'PmClosedLoopQuesnaireOpt': 'pm_cl_quesnaire_template_opt',
    'PmClEvaluationHeader': 'pm_cl_evaluation_header',
    'PmClQuesnaireResultHeader': 'pm_cl_quesnaire_result_header',
    'PmClQuesnaireResultLine': 'pm_cl_quesnaire_result_line',
    'PmClCBData': 'pm_cl_callback',
    'ProjectMaintenance': 'pm_project_maintenance',
    'ProjectSupervision': 'pm_project_supervision',
    'ProjectWarrantyCallback': 'pm_project_warranty_callback',

    # 转包
    'SubcontractProject': 'pm_subcontract_project_header',
    'SubcontractLine': 'pm_subcontract_project_line',
    'SubcontractPayment': 'pm_subcontract_project_payment',
    'SubcontractFacilitator': 'pm_subcontract_facilitator',
    'SubcontractDeliver': 'pm_subcontract_deliver',
    'SubcontractPrice': 'pm_subcontract_price',
    'SubcontractCallback': 'pm_subcontract_callback',

    # 技术公告
    'Prob': 'prob_main',
    'ProbRestore': 'prob_restore',
    'ProbProduct': 'prob_product',
    'ProbFile': 'prob_file',
    'ProbReadLog': 'prob_read_log',
    'ProbStatistic': 'prob_statistic',
    'SoftVersion': 'prob_soft_version',
    'DeviceVersionInfo': 'prob_device_version_info',
    'ProductComponent': 'prob_product_component',

    # 系统权限域 fnd_*
    'User': 'fnd_user_info',
    'UserLogin': 'fnd_user_info',
    'Role': 'fnd_roles',
    'RoleMenuPower': 'fnd_role_menu_power',
    'UserMenu': 'fnd_user_menus',
    'MenuForUser': 'fnd_user_menus',
    'Department': 'fnd_department',
    'BasicDataBean': 'fnd_basic_data',
    'OperateLog': 'fnd_operate_log',
    'Notification': 'fnd_notification',
    'NotificationTemplate': 'fnd_notification_template',
    'QualityParam': 'fnd_basic_data',
    'ProcdefDelegate': 'fnd_procdef_delegate',

    # 数据同步域
    'OrderDataFromSap': 'pm_order_data_from_erp_source',
    'OrderMainBean': 'pm_order_data_from_erp_source',
    'OrderLineBean': 'pm_order_line_from_erp_source',
    'OrderTodoBean': 'pm_order_todo',
    'OrderChangeState': 'pm_order_change_state',
    'Contract': 'pm_project_contract',
    'Instruction': 'pm_instruction',
    'RunTask': 'dp_act_unify_task',

    # 活动流程域
    'ActivityBaseBean': 'act_ru_task',
    'DpActProcDesc': 'dp_act_proc_desc',
    'DpActProcType': 'dp_act_proc_type',
    'DpComment': 'dp_act_comment',
    'ActComment': 'dp_act_comment',
    'Procdef': 'act_re_procdef',

    # 证书
    'CertificateAction': 'pm_certificate',
}


# ============================================================
# 6. 合并所有数据源
# ============================================================

def merge_all_data(entity_data, result_map_data, sql_data, sql_aliases, insert_fields):
    """
    合并 Java 实体注释、iBatis resultMap、SQL COMMENT、INSERT 字段四种来源。
    优先级：SQL COMMENT > resultMap 映射 + Java 注释 > Java 注释（驼峰转列名）> INSERT 字段
    """
    final = defaultdict(lambda: {'fields': {}})

    # --- 来源1: Java 实体类字段注释（驼峰转下划线作为 DB 列名）---
    for class_name, info in entity_data.items():
        table_name = info.get('table_hint', '') or ENTITY_TABLE_MAP.get(class_name, '')
        if not table_name or not is_valid_table_name(table_name):
            continue

        for java_field, comment in info['fields'].items():
            db_column = java_field_to_db_column(java_field)
            if comment and db_column and is_valid_field_name(db_column):
                existing = final[table_name]['fields'].get(db_column, '')
                if not existing:
                    final[table_name]['fields'][db_column] = comment

    # --- 来源2: iBatis resultMap 中的 property->column 映射 ---
    # resultMap 提供精确的 property(Java字段) -> column(DB列) 映射
    # 结合 Java 实体注释，将注释映射到精确的 DB 列名
    for rm_id, rm_info in result_map_data.items():
        rm_class = rm_info.get('class_name', '')
        props = rm_info.get('props', {})

        for prop, col in props.items():
            # 从 Java 实体类找注释
            comment = ''
            table_name = ''

            # 先从 resultMap 的 class 属性找对应的实体类
            if rm_class and rm_class in entity_data:
                info = entity_data[rm_class]
                if prop in info['fields']:
                    comment = info['fields'][prop]
                table_name = info.get('table_hint', '') or ENTITY_TABLE_MAP.get(rm_class, '')

            # 如果没找到，遍历所有实体类
            if not comment:
                for class_name, info in entity_data.items():
                    if prop in info['fields']:
                        comment = info['fields'][prop]
                        table_name = info.get('table_hint', '') or ENTITY_TABLE_MAP.get(class_name, '')
                        break

            if table_name and comment and col and is_valid_field_name(col):
                # resultMap 的 column 是精确的 DB 列名，优先使用
                existing = final[table_name]['fields'].get(col, '')
                if not existing:
                    final[table_name]['fields'][col] = comment

    # --- 来源3: SQL COMMENT（最高优先级）---
    for table_name, fields in sql_data.items():
        if not is_valid_table_name(table_name):
            continue
        for field_name, comment in fields.items():
            if comment and is_valid_field_name(field_name):
                final[table_name]['fields'][field_name] = comment

    # --- 来源4: INSERT 语句中的字段（补充无注释的字段）---
    for table_name, fields in insert_fields.items():
        if not is_valid_table_name(table_name):
            continue
        if table_name not in final:
            final[table_name] = {'fields': {}}
        for f in fields:
            if f and is_valid_field_name(f) and f not in final[table_name]['fields']:
                final[table_name]['fields'][f] = ''

    # --- 来源5: SQL AS 别名（仅保留有中文含义的）---
    for table_name, aliases in sql_aliases.items():
        if not is_valid_table_name(table_name):
            continue
        # SQL AS 别名不直接作为字段含义来源，跳过
        # 因为 AS 通常是查询结果的别名，不是表字段

    return dict(final)


# ============================================================
# 7. 去重：合并驼峰和下划线格式的重复字段
# ============================================================

def deduplicate_fields(fields):
    """
    去除重复字段：如果同时存在驼峰和下划线版本，
    保留下划线版本（DB 列名格式），删除驼峰版本。
    """
    result = {}
    snake_case_set = set()

    # 先收集所有下划线格式的字段名
    for fname in fields:
        if '_' in fname and fname.islower():
            snake_case_set.add(fname)

    for fname, comment in fields.items():
        # 如果是驼峰格式，检查是否有对应的下划线版本
        snake_version = java_field_to_db_column(fname)
        if snake_version in snake_case_set and snake_version != fname:
            # 驼峰版本有对应下划线版本，跳过驼峰版本
            # 但如果下划线版本没有注释而驼峰版本有，补充注释
            if not fields.get(snake_version, '') and comment:
                result[snake_version] = comment
            continue
        result[fname] = comment

    return result


# ============================================================
# 主流程
# ============================================================

def main():
    print('=' * 60)
    print('从 Java 代码提取数据库字段业务含义映射')
    print('=' * 60)
    print(f'项目根目录: {PROJECT_ROOT}')
    print(f'iBatis 目录: {IBATIS_DIR}')
    print(f'源码目录: {SRC_DIR}')
    print()

    # 1. 解析 iBatis XML
    print('[1/4] 解析 iBatis XML 映射文件...')
    result_map_data, sql_aliases, insert_fields = parse_ibatis_xml(IBATIS_DIR)
    total_rm = len(result_map_data)
    total_rm_mappings = sum(len(v.get('props', {})) for v in result_map_data.values())
    print(f'  找到 {total_rm} 个 resultMap')
    print(f'  resultMap 中共有 {total_rm_mappings} 个 property->column 映射')
    print(f'  SQL 别名映射涉及 {len(sql_aliases)} 个表')
    insert_tables = len(insert_fields)
    insert_total = sum(len(v) for v in insert_fields.values())
    print(f'  INSERT 语句涉及 {insert_tables} 个表, {insert_total} 个字段')

    # 2. 解析 Java 实体类
    print('[2/4] 解析 Java 实体类...')
    entity_data = parse_java_entities(SRC_DIR)
    entities_with_comments = sum(1 for v in entity_data.values() if v['fields'])
    total_field_comments = sum(len(v['fields']) for v in entity_data.values())
    print(f'  找到 {len(entity_data)} 个实体类')
    print(f'  其中 {entities_with_comments} 个有字段注释')
    print(f'  共有 {total_field_comments} 个字段注释')

    # 3. 解析 SQL 文件
    print('[3/4] 解析 SQL 脚本文件...')
    sql_data = parse_sql_files(PROJECT_ROOT)
    sql_tables_with_comments = len(sql_data)
    sql_field_comments = sum(len(v) for v in sql_data.values())
    print(f'  找到 {sql_tables_with_comments} 个表有 COMMENT')
    print(f'  共有 {sql_field_comments} 个字段 COMMENT')

    # 4. 合并数据
    print('[4/4] 合并所有数据源...')
    final_data = merge_all_data(entity_data, result_map_data, sql_data, sql_aliases, insert_fields)

    # 去重
    for table_name in final_data:
        final_data[table_name]['fields'] = deduplicate_fields(final_data[table_name]['fields'])

    # 过滤：只保留重点关注的表或有注释的表
    filtered_data = {}
    for table_name, info in final_data.items():
        if not is_valid_table_name(table_name):
            continue
        is_focus = any(table_name.lower().startswith(p) for p in FOCUS_PREFIXES)
        has_comments = any(v for v in info['fields'].values())
        if is_focus or has_comments:
            filtered_data[table_name] = info

    # 排序
    sorted_data = {}
    for table_name in sorted(filtered_data.keys()):
        fields = filtered_data[table_name]['fields']
        # 只保留有注释的字段（空注释的跳过，减少噪音）
        sorted_fields = dict(sorted(
            ((k, v) for k, v in fields.items() if v),
            key=lambda x: x[0]
        ))
        if sorted_fields:  # 只保留有注释的表
            sorted_data[table_name] = {'fields': sorted_fields}

    # 保存
    os.makedirs(OUT_DIR, exist_ok=True)
    with open(OUT_PATH, 'w', encoding='utf-8') as f:
        json.dump(sorted_data, f, ensure_ascii=False, indent=2)

    # 统计
    print()
    print('=' * 60)
    print('统计信息')
    print('=' * 60)
    total_tables = len(sorted_data)
    total_fields = sum(len(t['fields']) for t in sorted_data.values())
    fields_with_comments = sum(
        sum(1 for v in t['fields'].values() if v) for t in sorted_data.values()
    )

    print(f'总表数（有字段注释的）: {total_tables}')
    print(f'总字段数: {total_fields}')
    print(f'有业务含义的字段数: {fields_with_comments}')

    # 按前缀分组统计
    prefix_stats = defaultdict(lambda: {'tables': 0, 'fields': 0})
    for table_name, info in sorted_data.items():
        prefix = table_name.split('_')[0] + '_'
        if not any(table_name.startswith(p) for p in FOCUS_PREFIXES):
            prefix = 'other'
        prefix_stats[prefix]['tables'] += 1
        prefix_stats[prefix]['fields'] += len(info['fields'])

    print()
    print('按表前缀分组统计:')
    for prefix in sorted(prefix_stats.keys()):
        s = prefix_stats[prefix]
        print(f'  {prefix:12s}: {s["tables"]:3d} 表, {s["fields"]:4d} 有注释字段')

    print()
    print(f'JSON 已保存至: {OUT_PATH}')

    # 打印部分示例
    print()
    print('=' * 60)
    print('示例数据（前5个有注释的表）')
    print('=' * 60)
    shown = 0
    for table_name, info in sorted_data.items():
        commented_fields = {k: v for k, v in info['fields'].items() if v}
        if not commented_fields:
            continue
        print(f'\n  {table_name}:')
        for fn, fc in list(commented_fields.items())[:5]:
            print(f'    {fn}: {fc}')
        if len(commented_fields) > 5:
            print(f'    ... (共 {len(commented_fields)} 个有注释的字段)')
        shown += 1
        if shown >= 5:
            break


if __name__ == '__main__':
    main()
