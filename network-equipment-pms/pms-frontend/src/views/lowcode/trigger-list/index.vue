<script setup lang="ts">
/**
 * 低代码触发器列表。
 *
 * <p>提供触发器的列表查询、新建/编辑、删除与手动执行。
 * 触发类型支持 CRUD / QUARTZ / EVENT，目标类型支持 MICROFLOW / PROCESS。</p>
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteTrigger,
  executeTrigger,
  getTriggerList,
  saveTrigger,
  type LowCodeTrigger
} from '@/api/lowcode-trigger'

defineOptions({ name: 'TriggerListView' })

const list = ref<LowCodeTrigger[]>([])
const current = ref<LowCodeTrigger | null>(null)
const dialogVisible = ref(false)
const execDialogVisible = ref(false)
const execData = ref('')
const execTarget = ref<LowCodeTrigger | null>(null)

const typeOptions = [
  { label: 'CRUD', value: 'CRUD' },
  { label: 'Quartz', value: 'QUARTZ' },
  { label: 'Event', value: 'EVENT' }
]
const targetTypeOptions = [
  { label: '微流', value: 'MICROFLOW' },
  { label: '流程', value: 'PROCESS' }
]

async function load() {
  list.value = await getTriggerList()
}

function openNew() {
  current.value = {
    code: '',
    name: '',
    type: 'CRUD',
    config: '{}',
    targetType: 'MICROFLOW',
    targetCode: '',
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

function openEdit(row: LowCodeTrigger) {
  current.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!current.value) return
  await saveTrigger(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

async function remove(row: LowCodeTrigger) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认删除触发器「${row.name}」？`, '确认', { type: 'warning' })
    await deleteTrigger(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch {
    /* cancelled or error */
  }
}

function openExec(row: LowCodeTrigger) {
  execTarget.value = row
  execData.value = ''
  execDialogVisible.value = true
}

async function execute() {
  if (!execTarget.value) return
  let data: Record<string, unknown> = {}
  try {
    data = execData.value ? JSON.parse(execData.value) : {}
  } catch {
    ElMessage.error('输入数据 JSON 解析失败')
    return
  }
  const result = await executeTrigger(execTarget.value.code, data)
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
          <span>触发器</span>
          <el-button type="primary" @click="openNew">新建触发器</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" min-width="140" show-overflow-tooltip />
        <el-table-column label="名称" prop="name" min-width="140" show-overflow-tooltip />
        <el-table-column label="类型" prop="type" width="100" />
        <el-table-column label="目标" min-width="160">
          <template #default="{ row }">
            <el-tag size="small">{{ row.targetType }}</el-tag>
            <span style="margin-left: 6px">{{ row.targetCode }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
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

    <el-dialog v-model="dialogVisible" title="触发器编辑" width="800px">
      <el-form v-if="current" label-width="100px">
        <el-form-item label="编码"><el-input v-model="current.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="current.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="current.type">
            <el-option v-for="o in typeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置 JSON">
          <el-input v-model="current.config" type="textarea" :rows="4" placeholder='{"entityCode":"xxx","operation":"create"}' />
        </el-form-item>
        <el-form-item label="目标类型">
          <el-select v-model="current.targetType">
            <el-option v-for="o in targetTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标编码"><el-input v-model="current.targetCode" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="execDialogVisible" title="执行输入" width="600px">
      <el-input v-model="execData" type="textarea" :rows="6" placeholder='{"key":"value"}' />
      <template #footer>
        <el-button @click="execDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="execute">执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>
