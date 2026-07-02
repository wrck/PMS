<template>
  <div>
    <el-card style="margin-bottom:16px">
      <el-form :inline="true" :model="queryForm" label-width="80px">
        <el-form-item label="公告编号"><el-input v-model="queryForm.probNum" clearable /></el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="queryForm.taskState" clearable>
            <el-option label="待处理" value="0" /><el-option label="处理中" value="1" /><el-option label="已完成" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <template #header><span>修复任务管理</span></template>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="probNum" label="公告编号" width="140">
          <template #default="{ row }"><el-link type="primary" @click="$router.push(`/prob/detail/${row.probId}`)">{{ row.probNum }}</el-link></template>
        </el-table-column>
        <el-table-column prop="theme" label="公告主题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="taskName" label="任务名称" width="200" />
        <el-table-column prop="assignee" label="负责人" width="100" />
        <el-table-column prop="taskState" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="{0:'warning',1:'',2:'success'}[row.taskState]" size="small">{{ {0:'待处理',1:'处理中',2:'已完成'}[row.taskState] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="planDate" label="计划日期" width="120" />
        <el-table-column prop="actualDate" label="实际日期" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleUpdate(row)">更新状态</el-button>
            <el-button size="small" link @click="handleUploadWeekly(row)">上传周报</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProbTasks, updateProbTask } from '@/api/prob'
import { ElMessage, ElMessageBox } from 'element-plus'
const route = useRoute()
const probId = route.query.probId
const loading = ref(false)
const tableData = ref([])
const queryForm = reactive({ probNum: '', taskState: '' })
const fetchData = async () => { loading.value = true; try { const r = await getProbTasks(probId); tableData.value = r.data || [] } finally { loading.value = false } }
const handleQuery = () => fetchData()
const resetQuery = () => { Object.assign(queryForm, { probNum: '', taskState: '' }); fetchData() }
const handleUpdate = (row) => {
  ElMessageBox.prompt('请输入更新说明', '更新任务状态', { inputType: 'textarea' }).then(async ({ value }) => {
    await updateProbTask({ id: row.id, taskState: (row.taskState + 1) % 3, updateRemark: value })
    ElMessage.success('更新成功'); fetchData()
  }).catch(() => {})
}
const handleUploadWeekly = (row) => { ElMessage.info('上传周报功能开发中') }
onMounted(fetchData)
</script>
