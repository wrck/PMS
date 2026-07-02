<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="6"><el-form-item label="项目名称"><el-input v-model="queryForm.projectName" clearable /></el-form-item></el-col>
          <el-col :span="6">
            <el-form-item label="闭环状态">
              <el-select v-model="queryForm.applyState" clearable>
                <el-option label="待审批" value="0" /><el-option label="已通过" value="1" /><el-option label="已驳回" value="2" /><el-option label="无法闭环" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="当前步骤">
              <el-select v-model="queryForm.currentStep" clearable>
                <el-option label="PM申请" value="PM" /><el-option label="SM审核" value="SM" /><el-option label="CB回访" value="CB" /><el-option label="CL工程" value="CL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6" style="text-align:right">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>闭环管理</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="currentStep" label="当前步骤" width="100" />
        <el-table-column prop="applyState" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="{0:'warning',1:'success',2:'danger',3:'info'}[row.applyState]" size="small">
              {{ {0:'待审批',1:'已通过',2:'已驳回',3:'无法闭环'}[row.applyState] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyPerson" label="申请人" width="100" />
        <el-table-column prop="applyTime" label="申请时间" width="170"><template #default="{ row }">{{ formatDate(row.applyTime) }}</template></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/closed-loop/detail/${row.id}`)">详情</el-button>
            <el-button v-if="row.applyState === 0" size="small" type="success" link @click="handleApprove(row)">审批</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listClosedLoops } from '@/api/closedloop'
import { ElMessage } from 'element-plus'
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 20, projectName: '', applyState: '', currentStep: '' })
const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''
const fetchData = async () => { loading.value = true; try { const r = await listClosedLoops(queryForm); tableData.value = r.data?.records || []; total.value = r.data?.total || 0 } finally { loading.value = false } }
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { projectName: '', applyState: '', currentStep: '' }); handleQuery() }
const handleApprove = (row) => { ElMessage.info('审批功能开发中') }
onMounted(fetchData)
</script>
