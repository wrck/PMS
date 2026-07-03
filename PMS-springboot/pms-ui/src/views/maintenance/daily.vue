<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable /></el-form-item>
        <el-form-item label="维护人"><el-input v-model="queryForm.maintenancePerson" clearable /></el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryForm.reportDate" type="date" value-format="YYYY-MM-DD" placeholder="日报日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>维保日报</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="maintenancePerson" label="维护人" width="100" />
        <el-table-column prop="maintenanceContent" label="维护内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="maintenanceTime" label="维护时间" width="170" />
        <el-table-column prop="maintenanceType" label="类型" width="100" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMaintenanceDailyReport } from '@/api/maintenance'
import { ElMessage } from 'element-plus'
import { ElMessage } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, projectName: '', maintenancePerson: '', reportDate: '' })
const fetchData = async () => { loading.value = true; try { const r = await getMaintenanceDailyReport(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const handleExport = async () => {
  try {
    const r = await getMaintenanceDailyReport({ ...queryForm, export: true })
    const url = URL.createObjectURL(r.data)
    const a = document.createElement('a'); a.href = url; a.download = '维保日报.xlsx'; a.click(); URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) { ElMessage.error('导出失败') }
}
onMounted(fetchData)
</script>
