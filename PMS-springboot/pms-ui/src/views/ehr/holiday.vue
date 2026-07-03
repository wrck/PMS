<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="假期类型">
          <el-select v-model="queryForm.holidayType" clearable>
            <el-option label="年假" value="annual" /><el-option label="事假" value="personal" /><el-option label="病假" value="sick" /><el-option label="调休" value="compensatory" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工姓名"><el-input v-model="queryForm.employeeName" clearable /></el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryForm.dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>假期记录</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="employeeName" label="员工姓名" width="100" />
        <el-table-column prop="employeeNo" label="工号" width="120" />
        <el-table-column prop="holidayType" label="假期类型" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="days" label="天数" width="80" />
        <el-table-column prop="reason" label="事由" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="{0:'warning',1:'success',2:'danger'}[row.status]" size="small">{{ {0:'待审批',1:'已通过',2:'已驳回'}[row.status] }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listHolidays } from '@/api/ehr'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, holidayType: '', employeeName: '', dateRange: null })
const fetchData = async () => {
  loading.value = true
  try {
    const params = { ...queryForm }
    if (queryForm.dateRange) { params.startDate = queryForm.dateRange[0]; params.endDate = queryForm.dateRange[1] }
    delete params.dateRange
    const r = await listHolidays(params); tableData.value = r.data?.records || []; total.value = r.data?.total || 0
  } finally { loading.value = false }
}
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { holidayType: '', employeeName: '', dateRange: null }); handleQuery() }
onMounted(fetchData)
</script>
