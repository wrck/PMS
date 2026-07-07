<script setup lang="ts">
/**
 * 低代码微流设计器。
 *
 * <p>提供微流的列表查询、新建/编辑（含定义 JSON 编辑）、删除与执行（输入参数 JSON）。</p>
 */
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteMicroflow,
  executeMicroflow,
  getMicroflowList,
  saveMicroflow,
  type LowCodeMicroflow
} from '@/api/lowcode-microflow'

defineOptions({ name: 'MicroflowDesignerView' })

const list = ref<LowCodeMicroflow[]>([])
const current = ref<LowCodeMicroflow | null>(null)
const dialogVisible = ref(false)
const execDialogVisible = ref(false)
const execInputs = ref('')
const execTarget = ref<LowCodeMicroflow | null>(null)

async function load() {
  list.value = await getMicroflowList()
}

function openNew() {
  current.value = {
    code: '',
    name: '',
    description: '',
    definition: '{"nodes":[],"edges":[]}',
    status: 'DRAFT'
  }
  dialogVisible.value = true
}

function openEdit(row: LowCodeMicroflow) {
  current.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!current.value) return
  await saveMicroflow(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

async function remove(row: LowCodeMicroflow) {
  if (!row.id) return
  try {
    await ElMessageBox.confirm(`确认删除微流「${row.name}」？`, '确认', { type: 'warning' })
    await deleteMicroflow(row.id)
    ElMessage.success('删除成功')
    await load()
  } catch {
    /* cancelled or error */
  }
}

function openExec(row: LowCodeMicroflow) {
  execTarget.value = row
  execInputs.value = ''
  execDialogVisible.value = true
}

async function execute() {
  if (!execTarget.value) return
  let inputs: Record<string, unknown> = {}
  try {
    inputs = execInputs.value ? JSON.parse(execInputs.value) : {}
  } catch {
    ElMessage.error('输入参数 JSON 解析失败')
    return
  }
  const result = await executeMicroflow(execTarget.value.code, inputs)
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
          <span>微流设计器</span>
          <el-button type="primary" @click="openNew">新建微流</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" min-width="140" show-overflow-tooltip />
        <el-table-column label="名称" prop="name" min-width="140" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" title="微流编辑" width="800px">
      <el-form v-if="current" label-width="100px">
        <el-form-item label="编码"><el-input v-model="current.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="current.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="current.description" /></el-form-item>
        <el-form-item label="定义 JSON">
          <el-input v-model="current.definition" type="textarea" :rows="12" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="execDialogVisible" title="执行输入" width="600px">
      <el-input v-model="execInputs" type="textarea" :rows="6" placeholder='{"key":"value"}' />
      <template #footer>
        <el-button @click="execDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="execute">执行</el-button>
      </template>
    </el-dialog>
  </div>
</template>
