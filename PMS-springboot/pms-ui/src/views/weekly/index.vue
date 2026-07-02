<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="queryForm.weeklyState" clearable>
                <el-option label="草稿" value="0" /><el-option label="已提交" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="填报人"><el-input v-model="queryForm.weeklyPerson" clearable /></el-form-item>
          </el-col>
          <el-col :span="6" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>周报管理</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="weeklyPerson" label="填报人" width="100" />
        <el-table-column prop="weeklyStartTime" label="开始日期" width="120"><template #default="{ row }">{{ formatDate(row.weeklyStartTime) }}</template></el-table-column>
        <el-table-column prop="weeklyEndTime" label="结束日期" width="120"><template #default="{ row }">{{ formatDate(row.weeklyEndTime) }}</template></el-table-column>
        <el-table-column prop="weeklyState" label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.weeklyState===1?'success':'info'" size="small">{{ row.weeklyState===1?'已提交':'草稿' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/weekly/detail/${row.id}`)">查看</el-button>
            <el-button v-if="row.weeklyState===0" size="small" link @click="$router.push(`/weekly/detail/${row.id}?edit=true`)">编辑</el-button>
            <el-button v-if="row.weeklyState===0" size="small" type="success" link @click="submitWeekly(row)">提交</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listWeeklys, submitWeekly as apiSubmitWeekly } from '@/api/weekly'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, projectName: '', weeklyState: '', weeklyPerson: '' })
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const fetchData = async () => { loading.value = true; try { const r = await listWeeklys(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { projectName: '', weeklyState: '', weeklyPerson: '' }); handleQuery() }
const submitWeekly = (row) => { ElMessageBox.confirm('确认提交该周报？', '提示', { type: 'warning' }).then(async () => { await apiSubmitWeekly(row.id); ElMessage.success('已提交'); fetchData() }).catch(() => {}) }
onMounted(fetchData)
</script>
