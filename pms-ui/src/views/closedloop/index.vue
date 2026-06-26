<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.applyState" clearable placeholder="全部">
            <el-option label="审批中" :value="0" /><el-option label="已通过" :value="1" /><el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>项目闭环列表</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="applyBy" label="申请人" width="100" />
        <el-table-column prop="evaluationScore" label="评分" width="80" />
        <el-table-column prop="applyState" label="状态" width="100">
          <template #default="{ row }"><el-tag :type="row.applyState===1?'success':row.applyState===2?'danger':'warning'">{{ {0:'审批中',1:'已通过',2:'已驳回'}[row.applyState]||'未知' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.applyState===0" type="success" link @click="approve(row,true)">通过</el-button>
            <el-button v-if="row.applyState===0" type="danger" link @click="approve(row,false)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination style="margin-top:16px;justify-content:flex-end" v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize" :total="total" layout="total, prev, pager, next" @current-change="fetchData" />
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getClosedLoopList, approveClosedLoop } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
const loading = ref(false); const tableData = ref([]); const total = ref(0)
const queryForm = reactive({ pageNum: 1, pageSize: 10, applyState: null })
const fetchData = async () => { loading.value = true; try { const r = await getClosedLoopList(queryForm); tableData.value = r.data.records; total.value = r.data.total } finally { loading.value = false } }
const approve = (row, ok) => { ElMessageBox.confirm(ok?'确认通过？':'确认驳回？').then(async () => { await approveClosedLoop(row.id,'',ok,'sm'); ElMessage.success(ok?'已通过':'已驳回'); fetchData() }).catch(()=>{}) }
onMounted(fetchData)
</script>
