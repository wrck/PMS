<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConnectorList, saveConnector, testConnector, type LowCodeConnector } from '@/api/lowcode-connector'

defineOptions({ name: 'ConnectorDesignerView' })

const list = ref<LowCodeConnector[]>([])
const current = ref<LowCodeConnector | null>(null)
const dialogVisible = ref(false)

async function load() {
  list.value = await getConnectorList()
}

function openNew() {
  current.value = { code: '', name: '', description: '', type: 'REST', config: '{"url":"","method":"GET"}', status: 'ACTIVE' }
  dialogVisible.value = true
}

function openEdit(row: LowCodeConnector) {
  current.value = { ...row }
  dialogVisible.value = true
}

async function save() {
  if (!current.value) return
  await saveConnector(current.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

async function test(row: LowCodeConnector) {
  const result = await testConnector(row.code)
  ElMessage.success('测试结果: ' + JSON.stringify(result))
}

onMounted(load)
</script>

<template>
  <div style="padding: 16px">
    <el-card shadow="never">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>连接器配置</span>
          <el-button type="primary" @click="openNew">新建</el-button>
        </div>
      </template>
      <el-table :data="list">
        <el-table-column label="编码" prop="code" />
        <el-table-column label="名称" prop="name" />
        <el-table-column label="类型" prop="type" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === 'REST' ? '' : 'success'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="业务" prop="bizType" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" @click="test(row)">测试</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="连接器编辑" width="700px">
      <el-form v-if="current" label-width="100px">
        <el-form-item label="编码"><el-input v-model="current.code" /></el-form-item>
        <el-form-item label="名称"><el-input v-model="current.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="current.type">
            <el-option label="REST" value="REST" />
            <el-option label="DB" value="DB" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型"><el-input v-model="current.bizType" /></el-form-item>
        <el-form-item label="配置 JSON">
          <el-input v-model="current.config" type="textarea" :rows="8" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
