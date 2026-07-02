<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="任务名称"><el-input v-model="queryForm.taskName" clearable /></el-form-item>
        <el-form-item label="流程名称"><el-input v-model="queryForm.processName" clearable /></el-form-item>
        <el-form-item label="处理人"><el-input v-model="queryForm.assignee" clearable /></el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryForm.startTime" type="datetime" placeholder="开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:180px" />
          <span style="margin:0 4px">-</span>
          <el-date-picker v-model="queryForm.endTime" type="datetime" placeholder="结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>历史任务</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="taskName" label="任务名称" width="150" />
        <el-table-column prop="processName" label="流程名称" width="150" />
        <el-table-column prop="assignee" label="处理人" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="完成时间" width="170" />
        <el-table-column prop="duration" label="耗时" width="100" />
        <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="viewProcess(row)">查看流程</el-button>
            <el-button size="small" link @click="viewForm(row)">查看表单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getHistoryTasks } from '@/api/workflow'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, taskName: '', processName: '', assignee: '', startTime: '', endTime: '' })
const fetchData = async () => { loading.value = true; try { const r = await getHistoryTasks(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.keys(queryForm).forEach(k => { if (k !== 'pageNum' && k !== 'pageSize') queryForm[k] = '' }); handleQuery() }
const viewProcess = (row) => { window.open(`/api/workflow/instance/${row.processInstanceId}/image`) }
const viewForm = (row) => { /* 查看表单 */ }
onMounted(fetchData)
</script>
