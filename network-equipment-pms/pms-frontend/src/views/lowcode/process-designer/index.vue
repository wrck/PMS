<script setup lang="ts">
/**
 * 低代码流程设计器。
 *
 * <p>提供流程绑定列表查询、新建/编辑绑定（流程定义 key + 节点表单绑定 JSON）。
 * 复用 pms-workflow 的 Flowable 流程定义查询。</p>
 */
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getProcessBindings,
  saveProcessBinding,
  type LowCodeProcessBinding
} from '@/api/lowcode-process'

defineOptions({ name: 'ProcessDesignerView' })

const list = ref<LowCodeProcessBinding[]>([])
const current = ref<LowCodeProcessBinding | null>(null)
const dialogVisible = ref(false)

async function load() {
  list.value = await getProcessBindings()
}

function openNew() {
  current.value = {
    processDefinitionKey: '',
    processDefinitionName: '',
    nodeFormBindings: '[]',
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

function openEdit(row: LowCodeProcessBinding) {
  current.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!current.value) return
  await saveProcessBinding(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

onMounted(load)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>流程设计器</span>
          <el-button type="primary" @click="openNew">新建流程绑定</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="流程定义 Key" prop="processDefinitionKey" min-width="160" show-overflow-tooltip />
        <el-table-column label="流程名称" prop="processDefinitionName" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="流程绑定编辑" width="800px">
      <el-form v-if="current" label-width="120px">
        <el-form-item label="流程定义 Key"><el-input v-model="current.processDefinitionKey" /></el-form-item>
        <el-form-item label="流程名称"><el-input v-model="current.processDefinitionName" /></el-form-item>
        <el-form-item label="节点表单绑定">
          <el-input
            v-model="current.nodeFormBindings"
            type="textarea"
            :rows="10"
            placeholder='[{"nodeId":"task1","formCode":"form-xxx","microflowCode":"mf-xxx"}]'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
