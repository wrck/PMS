<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="员工姓名"><el-input v-model="queryForm.employeeName" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="工号"><el-input v-model="queryForm.employeeNo" clearable /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="部门"><el-input v-model="queryForm.deptName" clearable /></el-form-item></el-col>
          <el-col :span="6" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
            <el-button type="success" @click="handleSync">同步EHR</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>员工列表</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="employeeName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="entryDate" label="入职日期" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '在职' : '离职' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listEmployees, syncFromEHR } from '@/api/ehr'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, employeeName: '', employeeNo: '', deptName: '' })
const fetchData = async () => { loading.value = true; try { const r = await listEmployees(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { employeeName: '', employeeNo: '', deptName: '' }); handleQuery() }
const handleSync = () => { ElMessageBox.confirm('确认从EHR系统同步员工数据？', '提示', { type: 'info' }).then(async () => { await syncFromEHR(); ElMessage.success('同步成功'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
