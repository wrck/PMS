#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
用业务含义填充 database_dict final.md 中空的字段描述
策略：1. 字段描述为空但业务含义有值 → 用简洁业务含义填充
     2. 两者都为空 → 从字段名推断（常见命名模式映射）
     3. 查数据库 COMMENT 补全
"""
import re, os, pymysql

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

# 常见字段名→描述映射
FIELD_NAME_MAP = {
    'id': '主键ID',
    'create_time': '创建时间',
    'createTime': '创建时间',
    'created_time': '创建时间',
    'update_time': '更新时间',
    'updateTime': '更新时间',
    'updated_time': '更新时间',
    'create_date': '创建日期',
    'update_date': '更新日期',
    'create_by': '创建人',
    'create_user': '创建人',
    'creator': '创建人',
    'update_by': '更新人',
    'update_user': '更新人',
    'modifier': '修改人',
    'modified_by': '修改人',
    'modified_time': '修改时间',
    'del_flag': '删除标志',
    'is_deleted': '是否删除',
    'is_valid': '是否有效',
    'status': '状态',
    'state': '状态',
    'remark': '备注',
    'remarks': '备注',
    'description': '描述',
    'desc': '描述',
    'name': '名称',
    'code': '编码',
    'type': '类型',
    'order_no': '排序号',
    'sort': '排序',
    'sort_order': '排序',
    'version': '版本',
    'enable': '是否启用',
    'enabled': '是否启用',
    'disabled': '是否禁用',
    'visible': '是否可见',
    'deleted': '是否删除',
    'tenant_id': '租户ID',
    'org_id': '组织ID',
    'dept_id': '部门ID',
    'user_id': '用户ID',
    'company_id': '公司ID',
    'project_id': '项目ID',
    'project_no': '项目编号',
    'project_name': '项目名称',
    'contract_no': '合同编号',
    'contract_id': '合同ID',
    'customer_id': '客户ID',
    'customer_name': '客户名称',
    'product_id': '产品ID',
    'product_name': '产品名称',
    'product_code': '产品编码',
    'barcode': '条码',
    'bar_code': '条码',
    'serial_no': '序列号',
    'serial_number': '序列号',
    'amount': '金额',
    'total': '合计',
    'quantity': '数量',
    'qty': '数量',
    'price': '价格',
    'unit': '单位',
    'phone': '电话',
    'email': '邮箱',
    'address': '地址',
    'url': '链接地址',
    'path': '路径',
    'file_name': '文件名',
    'file_path': '文件路径',
    'content': '内容',
    'title': '标题',
    'label': '标签',
    'category': '分类',
    'level': '级别',
    'priority': '优先级',
    'start_time': '开始时间',
    'end_time': '结束时间',
    'begin_time': '开始时间',
    'finish_time': '完成时间',
    'due_date': '到期日期',
    'deadline': '截止日期',
    'parent_id': '父级ID',
    'pid': '父级ID',
    'children': '子级',
    'source': '来源',
    'source_type': '来源类型',
    'target': '目标',
    'action': '操作',
    'method': '方法',
    'module': '模块',
    'permission': '权限',
    'role_id': '角色ID',
    'role_name': '角色名称',
    'menu_id': '菜单ID',
    'ip': 'IP地址',
    'oper_time': '操作时间',
    'operation': '操作',
    'op_time': '操作时间',
    'op_user': '操作人',
    'operator': '操作人',
    'extra': '扩展信息',
    'ext': '扩展',
    'ext1': '扩展字段1',
    'ext2': '扩展字段2',
    'ext3': '扩展字段3',
    'ext4': '扩展字段4',
    'ext5': '扩展字段5',
    'field1': '自定义字段1',
    'field2': '自定义字段2',
    'field3': '自定义字段3',
    'field4': '自定义字段4',
    'field5': '自定义字段5',
    'reserve1': '预留字段1',
    'reserve2': '预留字段2',
    'reserve3': '预留字段3',
    'reserve4': '预留字段4',
    'reserve5': '预留字段5',
    'is_default': '是否默认',
    'is_active': '是否激活',
    'is_primary': '是否主键',
    'order_id': '订单ID',
    'order_num': '订单号',
    'line_no': '行号',
    'line_num': '行号',
    'item_code': '物料编码',
    'item_name': '物料名称',
    'item_no': '物料编号',
    'material_code': '物料编码',
    'material_name': '物料名称',
    'warehouse_id': '仓库ID',
    'warehouse_code': '仓库编码',
    'location': '位置',
    'region': '区域',
    'area': '区域',
    'country': '国家',
    'province': '省份',
    'city': '城市',
    'district': '区县',
    'zip': '邮编',
    'postal_code': '邮编',
    'fax': '传真',
    'contact': '联系人',
    'contact_person': '联系人',
    'contact_phone': '联系电话',
    'contact_email': '联系邮箱',
    'approval_status': '审批状态',
    'approve_status': '审批状态',
    'approve_time': '审批时间',
    'approve_user': '审批人',
    'submit_time': '提交时间',
    'submit_user': '提交人',
    'assignee': '处理人',
    'assignee_id': '处理人ID',
    'assignee_name': '处理人名称',
    'process_id': '流程ID',
    'process_key': '流程标识',
    'task_id': '任务ID',
    'task_name': '任务名称',
    'execution_id': '执行ID',
    'activity_id': '活动ID',
    'proc_inst_id': '流程实例ID',
    'business_key': '业务主键',
    'suspension_state': '挂起状态',
    'revision': '版本号',
    'seq_counter': '序列计数器',
    'seq_count': '序列计数',
    'is_suspended': '是否挂起',
    'callback_url': '回调地址',
    'error_msg': '错误信息',
    'error_code': '错误码',
    'retry_count': '重试次数',
    'sync_time': '同步时间',
    'sync_status': '同步状态',
    'sync_type': '同步类型',
    'data_source': '数据来源',
    'batch_no': '批次号',
    'batch_id': '批次ID',
    'ref_id': '关联ID',
    'ref_no': '关联编号',
    'ref_type': '关联类型',
    'related_id': '关联ID',
    'related_no': '关联编号',
    'mapping_id': '映射ID',
    'mapping_code': '映射编码',
    'src_id': '源ID',
    'src_name': '源名称',
    'dst_id': '目标ID',
    'dst_name': '目标名称',
    'from_id': '来源ID',
    'to_id': '目标ID',
    'old_value': '原值',
    'new_value': '新值',
    'change_type': '变更类型',
    'change_time': '变更时间',
    'change_reason': '变更原因',
    'log_type': '日志类型',
    'log_content': '日志内容',
    'request_url': '请求地址',
    'request_method': '请求方法',
    'request_param': '请求参数',
    'response_code': '响应码',
    'response_msg': '响应信息',
    'cost_time': '耗时',
    'duration': '持续时间',
    'elapsed': '耗时',
    'tenant_code': '租户编码',
    'app_id': '应用ID',
    'app_name': '应用名称',
    'app_key': '应用标识',
    'secret': '密钥',
    'token': '令牌',
    'access_key': '访问密钥',
    'encrypt': '加密',
    'sign': '签名',
    'charset': '字符集',
    'language': '语言',
    'timezone': '时区',
    'format': '格式',
    'page_size': '每页条数',
    'page_no': '页码',
    'current_page': '当前页',
    'total_count': '总条数',
    'total_page': '总页数',
}

def infer_from_field_name(fname):
    """从字段名推断描述"""
    # 精确匹配
    if fname in FIELD_NAME_MAP:
        return FIELD_NAME_MAP[fname]
    # 下划线分隔的驼峰
    lower = fname.lower()
    if lower in FIELD_NAME_MAP:
        return FIELD_NAME_MAP[lower]
    # 后缀匹配
    if fname.endswith('_id'):
        base = fname[:-3]
        return f"{base}ID"
    if fname.endswith('_name'):
        base = fname[:-5]
        return f"{base}名称"
    if fname.endswith('_code'):
        base = fname[:-5]
        return f"{base}编码"
    if fname.endswith('_type'):
        base = fname[:-5]
        return f"{base}类型"
    if fname.endswith('_time'):
        base = fname[:-5]
        return f"{base}时间"
    if fname.endswith('_date'):
        base = fname[:-5]
        return f"{base}日期"
    if fname.endswith('_status'):
        base = fname[:-7]
        return f"{base}状态"
    if fname.endswith('_flag'):
        base = fname[:-5]
        return f"{base}标志"
    if fname.endswith('_no'):
        base = fname[:-3]
        return f"{base}编号"
    if fname.endswith('_num'):
        base = fname[:-4]
        return f"{base}数量"
    if fname.endswith('_count'):
        base = fname[:-6]
        return f"{base}计数"
    if fname.endswith('_desc'):
        base = fname[:-5]
        return f"{base}描述"
    if fname.endswith('_rate'):
        base = fname[:-5]
        return f"{base}比率"
    if fname.endswith('_ratio'):
        base = fname[:-6]
        return f"{base}比例"
    if fname.endswith('_amount'):
        base = fname[:-7]
        return f"{base}金额"
    if fname.endswith('_price'):
        base = fname[:-6]
        return f"{base}价格"
    if fname.endswith('_qty'):
        base = fname[:-4]
        return f"{base}数量"
    # 前缀匹配
    if fname.startswith('is_'):
        return fname[3:] + "标识"
    if fname.startswith('has_'):
        return "是否有" + fname[4:]
    if fname.startswith('can_'):
        return "是否可以" + fname[4:]
    if fname.startswith('need_'):
        return "是否需要" + fname[5:]
    # 驼峰转下划线再试
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', fname)
    snake = re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()
    if snake in FIELD_NAME_MAP:
        return FIELD_NAME_MAP[snake]
    return ''

def biz_to_comment(biz):
    """将业务含义转为简洁字段描述"""
    if not biz or biz in ('-', '业务含义待确认', 'None'):
        return ''
    # 如果业务含义包含等号说明（如 "0=正常 1=删除"），取等号前的部分
    if '=' in biz:
        # 取第一个=号前的文字
        parts = biz.split('=')
        prefix = parts[0].strip()
        # 如果前缀是纯数字则不处理
        if prefix and not prefix.isdigit():
            return prefix
    # 如果业务含义太长，截取前30字
    if len(biz) > 40:
        return biz[:40]
    return biz

def main():
    print("=" * 60)
    print("填充 database_dict final.md 中空的字段描述")
    print("=" * 60)

    text = read_md(MD_FILE)
    lines = text.split('\n')

    # 先从数据库获取所有 COMMENT
    print("\n1. 从数据库获取字段COMMENT...")
    db_comments = {}
    try:
        conn = pymysql.connect(host='localhost', user='root', password='!Q@W3e4r', database='dppms_d365', charset='utf8mb4')
        cursor = conn.cursor()
        cursor.execute(
            "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_COMMENT "
            "FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='dppms_d365' AND COLUMN_COMMENT != ''"
        )
        for row in cursor.fetchall():
            key = f"{row[0]}.{row[1]}"
            db_comments[key] = row[2]
        cursor.close(); conn.close()
        print(f"   获取到 {len(db_comments)} 个字段COMMENT")
    except Exception as e:
        print(f"   数据库连接失败: {e}")

    # 解析并填充
    print("\n2. 解析并填充空字段描述...")
    filled_by_biz = 0
    filled_by_db = 0
    filled_by_infer = 0
    still_empty = 0
    current_table = ''

    i = 0
    while i < len(lines):
        line = lines[i].strip()
        # 检测表名
        m = re.match(r'^#{1,5}\s+[\d.]*\s*(\S+)\s*(?:--\s*(.+))?', line)
        if m and m.group(1) and is_valid_table_name(m.group(1)):
            current_table = m.group(1)

        # 检测字段数据行
        if line.startswith('|') and '---' not in line and not line.startswith('| 字段名') and not line.startswith('| 属性') and not line.startswith('| 索引') and not line.startswith('| 外键'):
            # 检查是否在字段表中（回溯找表头）
            in_field_table = False
            for j in range(i-1, max(i-10, 0), -1):
                if lines[j].strip().startswith('| 字段名'):
                    in_field_table = True
                    break
                if lines[j].strip().startswith('| 索引') or lines[j].strip().startswith('| 外键') or lines[j].strip().startswith('| 属性'):
                    break

            if in_field_table:
                all_parts = line.split('|')
                if all_parts and all_parts[0].strip() == '':
                    all_parts = all_parts[1:]
                if all_parts and all_parts[-1].strip() == '':
                    all_parts = all_parts[:-1]
                stripped = [p.strip() for p in all_parts]
                col_count = len(stripped)

                if col_count >= 7:
                    fname = stripped[0]
                    comment = ' | '.join(stripped[5:-1])
                    biz = stripped[-1]
                elif col_count == 6:
                    fname = stripped[0]
                    comment = stripped[5]
                    biz = ''
                elif col_count == 5:
                    fname = stripped[0]
                    comment = stripped[4]
                    biz = ''
                else:
                    i += 1
                    continue

                # 检查是否需要填充
                if not comment or comment in ('-', 'None', ''):
                    new_comment = ''

                    # 策略1: 用业务含义
                    if biz and biz not in ('-', '业务含义待确认', 'None', ''):
                        new_comment = biz_to_comment(biz)
                        if new_comment:
                            filled_by_biz += 1

                    # 策略2: 用数据库COMMENT
                    if not new_comment and current_table:
                        db_key = f"{current_table}.{fname}"
                        if db_key in db_comments:
                            new_comment = db_comments[db_key]
                            filled_by_db += 1

                    # 策略3: 从字段名推断
                    if not new_comment:
                        new_comment = infer_from_field_name(fname)
                        if new_comment:
                            filled_by_infer += 1

                    if new_comment:
                        # 替换字段描述列
                        raw = lines[i]
                        raw_parts = raw.split('|')
                        # 找到字段描述列的位置
                        if raw_parts[0].strip() == '':
                            raw_parts = raw_parts[1:]
                        if raw_parts[-1].strip() == '':
                            trailing = [raw_parts.pop()]
                        else:
                            trailing = []

                        if col_count >= 7:
                            # 第6列到倒数第2列合并为字段描述
                            # 替换第6列，删除多余的中间列
                            raw_parts[5] = f' {new_comment} '
                            # 删除第7列到倒数第2列（如果有的话）
                            if len(raw_parts) > 7:
                                del raw_parts[6:-1]
                        elif col_count == 6:
                            raw_parts[5] = f' {new_comment} '
                        elif col_count == 5:
                            raw_parts[4] = f' {new_comment} '

                        lines[i] = '|' + '|'.join(raw_parts) + ('|'.join(trailing) if trailing else '|')
                    else:
                        still_empty += 1

        i += 1

    # 写回文件
    with open(MD_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    print(f"\n填充完成:")
    print(f"  用业务含义填充: {filled_by_biz}")
    print(f"  用数据库COMMENT填充: {filled_by_db}")
    print(f"  从字段名推断填充: {filled_by_infer}")
    print(f"  仍为空: {still_empty}")
    print(f"  总填充: {filled_by_biz + filled_by_db + filled_by_infer}")

def is_valid_table_name(name):
    if not name or len(name) < 2:
        return False
    if re.search(r'[\u4e00-\u9fff]', name):
        return False
    if name.startswith('**'):
        return False
    return True

if __name__ == '__main__':
    main()
