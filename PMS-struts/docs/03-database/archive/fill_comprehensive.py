#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
综合填充脚本：
1. 同名字段优先使用前面表已维护的字段描述和业务含义
2. 空字段描述用业务含义填充
3. 空业务含义用字段描述填充
4. 两者皆空时用字段名推断
"""
import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
MD_FILE = os.path.join(BASE, 'database_dict final.md')

def read_md(filepath):
    with open(filepath, 'rb') as f:
        data = f.read()
    return data.decode('utf-8', errors='replace').replace('\r\n','\n').replace('\r','\n')

def parse_field_row(raw_line):
    """解析字段行，保留空列"""
    all_parts = raw_line.split('|')
    if all_parts and all_parts[0].strip() == '':
        all_parts = all_parts[1:]
    if all_parts and all_parts[-1].strip() == '':
        all_parts = all_parts[:-1]
    stripped = [p.strip() for p in all_parts]
    col_count = len(stripped)

    if col_count >= 7:
        return {
            'fname': stripped[0], 'dtype': stripped[1], 'nullable': stripped[2],
            'default_val': stripped[3], 'constraint': stripped[4],
            'comment': ' | '.join(stripped[5:-1]), 'biz': stripped[-1],
        }
    elif col_count == 6:
        fifth = stripped[4]
        if fifth in ('PRI', 'UNI', 'MUL', 'AUTO_INCREMENT', 'PRI, AUTO_INCREMENT', 'PRI,auto_increment') or 'auto_increment' in fifth.lower() or fifth in ('-', ''):
            return {
                'fname': stripped[0], 'dtype': stripped[1], 'nullable': stripped[2],
                'default_val': stripped[3], 'constraint': fifth,
                'comment': stripped[5], 'biz': '',
            }
        else:
            return {
                'fname': stripped[0], 'dtype': stripped[1], 'nullable': stripped[2],
                'default_val': stripped[3], 'constraint': '',
                'comment': ' | '.join(stripped[4:-1]),
                'biz': stripped[-1] if stripped[-1] != stripped[4] else '',
            }
    elif col_count == 5:
        return {
            'fname': stripped[0], 'dtype': stripped[1], 'nullable': stripped[2],
            'default_val': stripped[3], 'constraint': '',
            'comment': stripped[4], 'biz': '',
        }
    return None

def is_empty(val):
    return not val or val in ('-', 'None', '', '业务含义待确认')

def rebuild_row(fname, dtype, nullable, default_val, constraint, comment, biz):
    return f"| {fname} | {dtype} | {nullable} | {default_val} | {constraint} | {comment} | {biz} |"

# 字段名推断映射
SUFFIX_MAP = {
    'Id': 'ID', 'ID': 'ID', 'id': 'ID',
    'Name': '名称', 'name': '名称', 'Code': '编码', 'code': '编码',
    'Type': '类型', 'type': '类型', 'Status': '状态', 'status': '状态',
    'Time': '时间', 'time': '时间', 'Date': '日期', 'date': '日期',
    'Desc': '描述', 'desc': '描述', 'Description': '描述',
    'Count': '数量', 'count': '数量', 'No': '编号', 'no': '编号',
    'Flag': '标识', 'flag': '标识', 'Mark': '标记', 'mark': '标记',
    'Level': '级别', 'level': '级别', 'Rate': '比率', 'rate': '比率',
    'Amount': '金额', 'amount': '金额', 'Price': '价格', 'price': '价格',
    'State': '状态', 'state': '状态', 'Remark': '备注', 'remark': '备注',
    'Order': '排序', 'order': '排序', 'Sort': '排序', 'sort': '排序',
    'Path': '路径', 'path': '路径', 'Url': '链接', 'url': '链接',
    'Size': '大小', 'size': '大小', 'Value': '值', 'value': '值',
    'Group': '分组', 'group': '分组', 'Category': '分类', 'category': '分类',
}

EXACT_MAP = {
    'id': '主键ID', 'createTime': '创建时间', 'createBy': '创建人',
    'updateTime': '更新时间', 'updateBy': '更新人',
    'create_time': '创建时间', 'create_by': '创建人',
    'update_time': '更新时间', 'update_by': '更新人',
    'isDeleted': '删除标识', 'is_deleted': '删除标识',
    'isDelete': '删除标识', 'is_delete': '删除标识',
    'delFlag': '删除标识', 'del_flag': '删除标识',
    'deleted': '删除标识', 'enabled': '启用标识',
    'projectId': '项目ID', 'projectCode': '项目编码',
    'projectName': '项目名称', 'projectType': '项目类型',
    'companyId': '公司ID', 'companyName': '公司名称',
    'departmentId': '部门ID', 'departmentCode': '部门编码',
    'departmentName': '部门名称', 'userId': '用户ID',
    'userName': '用户名称', 'userCode': '用户编码',
    'memberCode': '成员编码', 'memberName': '成员名称',
    'roleId': '角色ID', 'roleName': '角色名称', 'roleCode': '角色编码',
    'orgId': '组织ID', 'orgCode': '组织编码', 'orgName': '组织名称',
    'parentId': '父级ID', 'sort': '排序', 'orderNum': '排序号',
    'remark': '备注', 'description': '描述', 'title': '标题',
    'content': '内容', 'status': '状态', 'type': '类型',
    'code': '编码', 'name': '名称', 'label': '标签',
    'version': '版本', 'seq': '序号', 'index': '索引',
    'total': '合计', 'amount': '金额', 'count': '数量',
    'startDate': '开始日期', 'endDate': '结束日期',
    'startTime': '开始时间', 'endTime': '结束时间',
    'processTime': '处理时间', 'processDesc': '处理描述',
    'feedback': '反馈', 'feedbacker': '反馈人', 'feedbackTime': '反馈时间',
    'contractNo': '合同编号', 'contractId': '合同ID',
    'customerCode': '客户编码', 'customerName': '客户名称',
    'productCode': '产品编码', 'productName': '产品名称',
    'productModel': '产品型号', 'itemModel': '产品型号',
    'serialNumber': '序列号', 'serialNo': '序列号',
    'barcode': '条码', 'shipmentCode': '发货编码',
    'weeklyId': '周报ID', 'maintenanceId': '维保ID',
    'serviceType': '服务类型', 'deliveried': '已交付标识',
    'year': '年度', 'quarter': '季度', 'month': '月份',
    'diff': '差异', 'describe_2': '描述2',
    'gradenames': '等级名称', 'gradecodes': '等级编码',
    'level': '级别', 'enable': '启用标识',
    'target': '目标', 'customer': '客户',
    'businessunit': '业务单元', 'dutyperson': '负责人',
    'BU': '业务单元', 'totaljine': '总金额',
    'usernamec2': '用户名称C2', 'partition': '分区',
    'field1': '扩展字段1', 'field2': '扩展字段2',
    'field3': '扩展字段3', 'field4': '扩展字段4',
    'field5': '扩展字段5', 'field6': '扩展字段6',
    'field7': '扩展字段7', 'field8': '扩展字段8',
    'field9': '扩展字段9', 'field10': '扩展字段10',
    'assignee': '处理人', 'assigneeId': '处理人ID',
    'assigneeName': '处理人名称', 'owner': '拥有者',
    'ownerId': '拥有者ID', 'ownerName': '拥有者名称',
    'creator': '创建人', 'modifier': '修改人',
    'modifierName': '修改人名称', 'modifierId': '修改人ID',
    'approver': '审批人', 'approverId': '审批人ID',
    'approverName': '审批人名称', 'reviewer': '审核人',
    'handler': '处理人', 'handlerId': '处理人ID',
    'handlerName': '处理人名称', 'executor': '执行人',
    'executorId': '执行人ID', 'executorName': '执行人名称',
    'supplier': '供应商', 'supplierCode': '供应商编码',
    'supplierName': '供应商名称', 'manufacturer': '制造商',
    'manufacturerName': '制造商名称', 'brand': '品牌',
    'brandName': '品牌名称', 'model': '型号',
    'spec': '规格', 'specification': '规格参数',
    'unit': '单位', 'quantity': '数量',
    'unitPrice': '单价', 'totalPrice': '总价',
    'discount': '折扣', 'taxRate': '税率',
    'tax': '税额', 'currency': '币种',
    'payment': '付款', 'paymentMethod': '付款方式',
    'paymentStatus': '付款状态', 'invoiceNo': '发票号',
    'invoiceStatus': '发票状态', 'receiptNo': '收据号',
    'warehouse': '仓库', 'warehouseCode': '仓库编码',
    'warehouseName': '仓库名称', 'location': '库位',
    'locationCode': '库位编码', 'locationName': '库位名称',
    'stock': '库存', 'stockQty': '库存数量',
    'inQty': '入库数量', 'outQty': '出库数量',
    'source': '来源', 'sourceId': '来源ID',
    'sourceType': '来源类型', 'sourceNo': '来源编号',
    'targetId': '目标ID', 'targetType': '目标类型',
    'refId': '关联ID', 'refType': '关联类型',
    'refCode': '关联编码', 'refName': '关联名称',
    'relatedId': '关联ID', 'relatedType': '关联类型',
    'relatedCode': '关联编码', 'relatedName': '关联名称',
    'linkId': '链接ID', 'linkType': '链接类型',
    'parentId': '父级ID', 'parentCode': '父级编码',
    'parentName': '父级名称', 'children': '子级',
    'treePath': '树路径', 'treeLevel': '树层级',
    'leaf': '叶子节点', 'isLeaf': '是否叶子节点',
    'priority': '优先级', 'weight': '权重',
    'score': '分数', 'grade': '等级',
    'step': '步骤', 'phase': '阶段',
    'stage': '阶段', 'milestone': '里程碑',
    'progress': '进度', 'percentage': '百分比',
    'ratio': '比率', 'proportion': '比例',
    'duration': '时长', 'interval': '间隔',
    'period': '周期', 'cycle': '周期',
    'frequency': '频率', 'times': '次数',
    'limit': '限制', 'threshold': '阈值',
    'max': '最大值', 'min': '最小值',
    'avg': '平均值', 'sum': '合计',
    'result': '结果', 'outcome': '结果',
    'reason': '原因', 'cause': '原因',
    'solution': '解决方案', 'method': '方法',
    'action': '操作', 'operation': '操作',
    'task': '任务', 'job': '作业',
    'schedule': '计划', 'plan': '计划',
    'strategy': '策略', 'rule': '规则',
    'condition': '条件', 'criteria': '标准',
    'filter': '过滤', 'search': '搜索',
    'keyword': '关键词', 'query': '查询',
    'page': '页码', 'pageSize': '每页条数',
    'totalPage': '总页数', 'totalRow': '总行数',
    'rowNum': '行号', 'lineNo': '行号',
    'colNo': '列号', 'cellNo': '单元格号',
    'sheet': '工作表', 'template': '模板',
    'templateId': '模板ID', 'templateCode': '模板编码',
    'templateName': '模板名称', 'config': '配置',
    'configKey': '配置键', 'configValue': '配置值',
    'setting': '设置', 'param': '参数',
    'paramName': '参数名称', 'paramValue': '参数值',
    'property': '属性', 'attribute': '属性',
    'tag': '标签', 'tags': '标签',
    'category': '分类', 'group': '分组',
    'module': '模块', 'menu': '菜单',
    'permission': '权限', 'authority': '权限',
    'resource': '资源', 'action': '操作',
    'log': '日志', 'logType': '日志类型',
    'logContent': '日志内容', 'error': '错误',
    'errorMsg': '错误信息', 'errorCode': '错误码',
    'warn': '警告', 'info': '信息',
    'debug': '调试', 'trace': '追踪',
    'message': '消息', 'msg': '消息',
    'subject': '主题', 'body': '正文',
    'attachment': '附件', 'attachId': '附件ID',
    'fileName': '文件名', 'filePath': '文件路径',
    'fileSize': '文件大小', 'fileType': '文件类型',
    'fileExt': '文件扩展名', 'mimeType': 'MIME类型',
    'uploadTime': '上传时间', 'uploadBy': '上传人',
    'downloadTime': '下载时间', 'downloadBy': '下载人',
    'ip': 'IP地址', 'mac': 'MAC地址',
    'port': '端口', 'url': '链接',
    'host': '主机', 'domain': '域名',
    'protocol': '协议', 'method': '方法',
    'header': '头部', 'token': '令牌',
    'secret': '密钥', 'key': '键',
    'value': '值', 'extra': '扩展',
    'ext': '扩展', 'extend': '扩展',
    'alias': '别名', 'nick': '昵称',
    'avatar': '头像', 'icon': '图标',
    'color': '颜色', 'font': '字体',
    'width': '宽度', 'height': '高度',
    'length': '长度', 'depth': '深度',
    'radius': '半径', 'area': '面积',
    'volume': '体积', 'weight': '重量',
    'mass': '质量', 'density': '密度',
    'temperature': '温度', 'pressure': '压力',
    'speed': '速度', 'acceleration': '加速度',
    'direction': '方向', 'angle': '角度',
    'latitude': '纬度', 'longitude': '经度',
    'address': '地址', 'city': '城市',
    'province': '省份', 'country': '国家',
    'zipCode': '邮编', 'postalCode': '邮政编码',
    'phone': '电话', 'mobile': '手机号',
    'email': '邮箱', 'fax': '传真',
    'website': '网站', 'contact': '联系人',
    'contactName': '联系人名称', 'contactPhone': '联系人电话',
    'contactEmail': '联系人邮箱', 'contactAddress': '联系人地址',
    'account': '账号', 'accountNo': '账号',
    'accountId': '账号ID', 'accountName': '账号名称',
    'password': '密码', 'salt': '盐值',
    'sign': '签名', 'signature': '签名',
    'cert': '证书', 'license': '许可证',
    'valid': '有效标识', 'isValid': '是否有效',
    'active': '激活标识', 'isActive': '是否激活',
    'visible': '可见标识', 'isVisible': '是否可见',
    'readonly': '只读标识', 'required': '必填标识',
    'disabled': '禁用标识', 'locked': '锁定标识',
    'expired': '过期标识', 'published': '发布标识',
    'draft': '草稿标识', 'pending': '待处理标识',
    'approved': '已审批标识', 'rejected': '已拒绝标识',
    'cancelled': '已取消标识', 'closed': '已关闭标识',
    'completed': '已完成标识', 'failed': '失败标识',
    'success': '成功标识', 'running': '运行中标识',
    'paused': '已暂停标识', 'suspended': '已挂起标识',
    'archived': '已归档标识', 'deleted': '删除标识',
    'merged': '已合并标识', 'split': '拆分标识',
    'copied': '已复制标识', 'moved': '已移动标识',
    'converted': '已转换标识', 'migrated': '已迁移标识',
    'synced': '已同步标识', 'imported': '已导入标识',
    'exported': '已导出标识', 'printed': '已打印标识',
    'emailed': '已邮件发送标识', 'notified': '已通知标识',
    'reminded': '已提醒标识', 'escalated': '已升级标识',
    'delegated': '已委派标识', 'assigned': '已分配标识',
    'forwarded': '已转发标识', 'replied': '已回复标识',
    'resolved': '已解决标识', 'verified': '已验证标识',
    'confirmed': '已确认标识', 'acknowledged': '已确认标识',
    'rated': '已评分标识', 'evaluated': '已评估标识',
    'reviewed': '已审核标识', 'audited': '已审计标识',
    'inspected': '已检查标识', 'tested': '已测试标识',
    'deployed': '已部署标识', 'released': '已发布标识',
    'installed': '已安装标识', 'configured': '已配置标识',
    'initialized': '已初始化标识', 'registered': '已注册标识',
    'subscribed': '已订阅标识', 'unsubscribed': '已取消订阅标识',
    'enrolled': '已注册标识', 'applied': '已申请标识',
    'accepted': '已接受标识', 'declined': '已拒绝标识',
    'withdrawn': '已撤回标识', 'resubmitted': '已重新提交标识',
    'returned': '已退回标识', 'replaced': '已替换标识',
    'swapped': '已交换标识', 'transferred': '已转移标识',
    'shared': '已共享标识', 'revoked': '已撤销标识',
    'expired': '已过期标识', 'renewed': '已续期标识',
    'upgraded': '已升级标识', 'downgraded': '已降级标识',
    'patched': '已修补标识', 'fixed': '已修复标识',
    'bypassed': '已绕过标识', 'overridden': '已覆盖标识',
    'skipped': '已跳过标识', 'retried': '已重试标识',
    'aborted': '已中止标识', 'timedout': '已超时标识',
    'IP': 'IP地址', 'ACTION': '操作', 'RESULT': '结果',
    'INFO': '信息', 'TIME': '时间', 'rank': '排名',
    'ssfs': '实施方式',
    'REV_': '版本号', 'PROC_DEF_ID_': '流程定义ID',
    'BUS_KEY': '业务键', 'PARENT_TASK_ID_': '父任务ID',
}

def infer_from_fieldname(fname):
    """从字段名推断描述"""
    # 精确匹配
    if fname in EXACT_MAP:
        return EXACT_MAP[fname]

    # 中文字段名直接作为描述
    if re.search(r'[\u4e00-\u9fff]', fname):
        return fname

    # 后缀匹配
    for suffix, meaning in SUFFIX_MAP.items():
        if fname.endswith(suffix) and len(fname) > len(suffix):
            prefix = fname[:-len(suffix)]
            # 驼峰拆分
            parts = re.findall(r'[A-Z]?[a-z]+|[A-Z]+(?=[A-Z]|$)', prefix)
            if parts:
                prefix_cn = ''.join(parts)
                return f"{prefix_cn}{meaning}"
            return f"{prefix}{meaning}"

    # 前缀匹配
    if fname.startswith('is_') or fname.startswith('is'):
        return '标识'
    if fname.startswith('has_') or fname.startswith('has'):
        return '拥有标识'
    if fname.startswith('can_') or fname.startswith('can'):
        return '权限标识'
    if fname.startswith('need_') or fname.startswith('need'):
        return '需求标识'
    if fname.startswith('fk_'):
        return '外键'

    # 驼峰拆分
    parts = re.findall(r'[A-Z]?[a-z]+|[A-Z]+(?=[A-Z]|$)', fname)
    if len(parts) > 1:
        return ''.join(parts)

    return ''

text = read_md(MD_FILE)
lines = text.split('\n')

# ============================================================
# 第一遍：收集同名字段的字段描述和业务含义缓存
# ============================================================
field_cache = {}  # fname -> {'comment': best, 'biz': best}

i = 0
while i < len(lines):
    s = lines[i].strip()
    if s.startswith('| 字段名') and '数据类型' in s:
        j = i + 2
        while j < len(lines) and lines[j].strip().startswith('|') and '---' not in lines[j]:
            field = parse_field_row(lines[j].strip())
            if field:
                fname = field['fname']
                comment = field['comment']
                biz = field['biz']
                if fname not in field_cache:
                    field_cache[fname] = {'comment': comment, 'biz': biz}
                else:
                    existing = field_cache[fname]
                    if not is_empty(comment) and (is_empty(existing['comment']) or len(comment) > len(existing['comment'])):
                        existing['comment'] = comment
                    if not is_empty(biz) and (is_empty(existing['biz']) or len(biz) > len(existing['biz'])):
                        existing['biz'] = biz
            j += 1
    i += 1

print(f"收集到 {len(field_cache)} 个字段名的缓存")
has_cache = sum(1 for v in field_cache.values() if not is_empty(v['comment']) or not is_empty(v['biz']))
print(f"有描述或含义缓存的字段名: {has_cache}")

# ============================================================
# 第二遍：填充空字段描述和空业务含义
# ============================================================
fill_comment_by_cache = 0
fill_biz_by_cache = 0
fill_comment_by_biz = 0
fill_biz_by_comment = 0
fill_by_infer = 0
total_fixed = 0

i = 0
while i < len(lines):
    s = lines[i].strip()
    if s.startswith('| 字段名') and '数据类型' in s:
        j = i + 2
        while j < len(lines) and lines[j].strip().startswith('|') and '---' not in lines[j]:
            field = parse_field_row(lines[j].strip())
            if field:
                fname = field['fname']
                comment = field['comment']
                biz = field['biz']
                changed = False

                # 1. 空字段描述：优先用同名字段缓存
                if is_empty(comment) and fname in field_cache and not is_empty(field_cache[fname]['comment']):
                    comment = field_cache[fname]['comment']
                    fill_comment_by_cache += 1
                    changed = True

                # 2. 空业务含义：优先用同名字段缓存
                if is_empty(biz) and fname in field_cache and not is_empty(field_cache[fname]['biz']):
                    biz = field_cache[fname]['biz']
                    fill_biz_by_cache += 1
                    changed = True

                # 3. 空字段描述：用业务含义填充
                if is_empty(comment) and not is_empty(biz):
                    comment = biz
                    fill_comment_by_biz += 1
                    changed = True

                # 4. 空业务含义：用字段描述填充
                if is_empty(biz) and not is_empty(comment):
                    biz = comment
                    fill_biz_by_comment += 1
                    changed = True

                # 5. 两者皆空：用字段名推断
                if is_empty(comment) and is_empty(biz):
                    inferred = infer_from_fieldname(fname)
                    if inferred:
                        comment = inferred
                        biz = inferred
                        fill_by_infer += 1
                        changed = True

                if changed:
                    lines[j] = rebuild_row(fname, field['dtype'], field['nullable'],
                                            field['default_val'], field['constraint'],
                                            comment, biz)
                    total_fixed += 1
            j += 1
    i += 1

with open(MD_FILE, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))

print(f"\n填充结果:")
print(f"  同名字段缓存填充字段描述: {fill_comment_by_cache}")
print(f"  同名字段缓存填充业务含义: {fill_biz_by_cache}")
print(f"  业务含义填充字段描述: {fill_comment_by_biz}")
print(f"  字段描述填充业务含义: {fill_biz_by_comment}")
print(f"  字段名推断填充: {fill_by_infer}")
print(f"  总修改行数: {total_fixed}")
