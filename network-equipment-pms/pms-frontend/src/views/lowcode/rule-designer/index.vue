<script setup lang="ts">
/**
 * 低代码规则设计器。
 *
 * <p>提供规则的列表查询、新建/编辑（含类型选择与定义 JSON 编辑）、删除与执行（输入事实 JSON）。
 * 规则类型支持决策表 / 表达式 / LiteFlow。</p>
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteRule,
  executeRule,
  getRuleList,
  saveRule,
  type LowCodeRule
} from '@/api/lowcode-rule'

defineOptions({ name: 'RuleDesignerView' })

const list = ref<LowCodeRule[]>([])
const current = ref<LowCodeRule | null>(null)
const dialogVisible = ref(false)
const execDialogVisible = ref(false)
const execFacts = ref('')
const execTarget = ref<LowCodeRule | null>(null)

const typeOptions = [
  { label: '决策表', value: 'DECISION_TABLE' },
  { label: '表达式', value: 'EXPRESSION' },
  { label: 'LiteFlow', value: 'LITEFLOW' }
]

async function load() {
  list.value = await getRuleList()
}

function openNew() {
  current.value = {
    code: '',
    name: '',
    description: '',
    type: 'EXPRESSION',
    definition: '',
    status: 'DRAFT'
  }
  dialogVisible.value = true
}

function openEdit(row: LowCodeRule) {
  current.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!current.value) return
  await saveRule(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

async function remove(row: LowCodeRule) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认删除规则「${row.name}」？`, '确认', { type: 'warning' })
    await deleteRule(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch {
    /* cancelled or error */
  }
}

function openExec(row: LowCodeRule) {
  execTarget.value = row
  execFacts.value = ''
  execDialogVisible.value = true
}

async function execute() {
  if (!execTarget.value) return
  let facts: Record<string, unknown> = {}
  try {
    facts = execFacts.value ? JSON.parse(execFacts.value) : {}
  } catch {
    ElMessage.error('输入事实 JSON 解析失败')
    return
  }
  const result = await executeRule(execTarget.value.code, facts)
  ElMessage.success('执行结果: ' + JSON.stringify(result))
  execDialogVisible.value = false
}

onMounted(load)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>规则设计器</span>
          <el-button type="primary" @click="openNew">新建规则</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" min-width="140" show-overflow-tooltip />
        <el-table-column label="名称" prop="name" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="140">
          <template #default="{ row }">
            <el-tag>{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="openExec(row)">执行</el-button>
            <el-button size="small" type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="规则编辑" width="800px">
      <el-form v-if="current" label-width="100px">
        <el-form-item label="编码"><el-input v-model="current.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="current.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="current.description" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="current.type">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="定义">
          <el-input v-model="current.definition" type="textarea" :rows="12" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="execDialogVisible" title="执行输入" width="600px">
      <el-input v-model="execFacts" type="textarea" :rows="6" placeholder='{"field":"value"}' />
      <template #footer>
        <el-button @click="execDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="execute">执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>
