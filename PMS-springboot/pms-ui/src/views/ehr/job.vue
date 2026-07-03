<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="岗位名称"><el-input v-model="queryForm.jobName" clearable /></el-form-item>
        <el-form-item label="部门"><el-input v-model="queryForm.deptName" clearable /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>岗位列表</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="jobCode" label="岗位编码" width="140" />
        <el-table-column prop="jobName" label="岗位名称" width="180" />
        <el-table-column prop="deptName" label="所属部门" width="180" />
        <el-table-column prop="employeeCount" label="在职人数" width="100" />
        <el-table-column prop="description" label="岗位描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listJobs } from '@/api/ehr'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, jobName: '', deptName: '' })
const fetchData = async () => { loading.value = true; try { const r = await listJobs(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { jobName: '', deptName: '' }); handleQuery() }
onMounted(fetchData)
</script>
