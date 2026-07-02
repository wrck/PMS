<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="流程名称"><el-input v-model="queryForm.processName" clearable /></el-form-item>
        <el-form-item label="流程Key"><el-input v-model="queryForm.processKey" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="showDeployDialog">发布新流程</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>流程部署管理</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="deploymentId" label="部署ID" width="150" />
        <el-table-column prop="name" label="流程名称" min-width="200" />
        <el-table-column prop="key" label="流程Key" width="150" />
        <el-table-column prop="deploymentTime" label="部署时间" width="170" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="viewImage(row)">查看流程图</el-button>
            <el-button size="small" link @click="viewDefinition(row)">查看定义</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="deployDialogVisible" title="发布流程" width="500px">
      <el-form :model="deployForm" label-width="80px">
        <el-form-item label="流程名称" required><el-input v-model="deployForm.processName" /></el-form-item>
        <el-form-item label="流程Key" required><el-input v-model="deployForm.processKey" /></el-form-item>
        <el-form-item label="BPMN文件" required>
          <el-upload ref="uploadRef" :action="`/api/workflow/deploy`" :headers="uploadHeaders" :data="deployForm" :auto-upload="false" :limit="1" accept=".bpmn,.bpmn20.xml,.xml">
            <el-button size="small">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDeploy">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getDeployments, deleteDeployment } from '@/api/workflow'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const deployDialogVisible = ref(false)
const uploadRef = ref(null)
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))
const queryForm = reactive({ processName: '', processKey: '' })
const deployForm = reactive({ processName: '', processKey: '' })
const fetchData = async () => { loading.value = true; try { const r = await getDeployments(queryForm); tableData.value = r.data || [] } finally { loading.value = false } }
const handleQuery = () => fetchData()
const showDeployDialog = () => { Object.assign(deployForm, { processName: '', processKey: '' }); deployDialogVisible.value = true }
const handleDeploy = () => { uploadRef.value.submit() }
const viewImage = (row) => { window.open(`/api/workflow/deploy/${row.deploymentId}/image`) }
const viewDefinition = (row) => { window.open(`/api/workflow/deploy/${row.deploymentId}/definition`) }
const handleDelete = (row) => { ElMessageBox.confirm('确认删除该流程部署？', '提示', { type: 'warning' }).then(async () => { await deleteDeployment(row.deploymentId); ElMessage.success('已删除'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
