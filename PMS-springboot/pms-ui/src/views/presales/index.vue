<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="售前编码">
          <el-input v-model="queryForm.presalesCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="queryForm.projectName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryForm.applyState" clearable placeholder="全部">
            <el-option label="未申请" :value="-1" />
            <el-option label="审批中" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>售前项目列表</span>
          <el-button type="primary" @click="handleAdd">新增售前</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="presalesCode" label="售前编码" width="140" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="officeCode" label="办事处" width="100" />
        <el-table-column prop="salesman" label="销售" width="100" />
        <el-table-column prop="serviceManagerName" label="服务经理" width="100" />
        <el-table-column prop="projectManagerName" label="项目经理" width="100" />
        <el-table-column prop="applyState" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="stateTagType(row.applyState)">{{ stateText(row.applyState) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.applyState === -1" type="success" link @click="handleStartFlow(row)">提交申请</el-button>
            <el-button v-if="row.applyState === 0" type="warning" link @click="handleApprove(row, true)">审批通过</el-button>
            <el-button v-if="row.applyState === 0" type="danger" link @click="handleApprove(row, false)">驳回</el-button>
            <el-button v-if="row.applyState !== 0" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination style="margin-top:16px;justify-content:flex-end"
        v-model:current-page="queryForm.pageNum" v-model:page-size="queryForm.pageSize"
        :page-sizes="[10,20,50]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData" @current-change="fetchData" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="项目名称"><el-input v-model="formData.projectName" /></el-form-item>
        <el-form-item label="售前编码"><el-input v-model="formData.presalesCode" /></el-form-item>
        <el-form-item label="办事处"><el-input v-model="formData.officeCode" /></el-form-item>
        <el-form-item label="销售"><el-input v-model="formData.salesman" /></el-form-item>
        <el-form-item label="项目类型"><el-input v-model="formData.projectType" /></el-form-item>
        <el-form-item label="市场"><el-input v-model="formData.marketName" /></el-form-item>
        <el-form-item label="系统"><el-input v-model="formData.systemName" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getPresalesList, addPresales, updatePresales, deletePresales, startPresalesFlow, approvePresales } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const queryForm = reactive({ pageNum: 1, pageSize: 10, presalesCode: '', projectName: '', applyState: null })
const formData = reactive({ id: null, projectName: '', presalesCode: '', officeCode: '', salesman: '', projectType: null, marketName: '', systemName: '' })

const stateText = (s) => ({ '-1': '未申请', '0': '审批中', '1': '已通过', '2': '已驳回' }[s] || '未知')
const stateTagType = (s) => ({ '-1': 'info', '0': 'warning', '1': 'success', '2': 'danger' }[s] || 'info')

const fetchData = async () => {
  loading.value = true
  try { const res = await getPresalesList(queryForm); tableData.value = res.data.records; total.value = res.data.total }
  finally { loading.value = false }
}
const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => { Object.assign(queryForm, { presalesCode: '', projectName: '', applyState: null }); handleQuery() }
const handleAdd = () => { dialogTitle.value = '新增售前'; Object.assign(formData, { id: null, projectName: '', presalesCode: '', officeCode: '', salesman: '', projectType: null, marketName: '', systemName: '' }); dialogVisible.value = true }
const handleDetail = (row) => { ElMessage.info('详情功能开发中') }
const handleSubmit = async () => {
  if (formData.id) { await updatePresales(formData); ElMessage.success('更新成功') }
  else { await addPresales(formData); ElMessage.success('创建成功') }
  dialogVisible.value = false; fetchData()
}
const handleStartFlow = (row) => {
  ElMessageBox.confirm('确认提交审批？', '提示', { type: 'warning' }).then(async () => {
    await startPresalesFlow(row.id); ElMessage.success('已提交审批'); fetchData()
  }).catch(() => {})
}
const handleApprove = (row, approved) => {
  const action = approved ? '通过' : '驳回'
  ElMessageBox.confirm(`确认${action}？`, '提示', { type: 'warning' }).then(async () => {
    await approvePresales(row.id, '', approved); ElMessage.success(`已${action}`); fetchData()
  }).catch(() => {})
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除？', '提示', { type: 'warning' }).then(async () => {
    await deletePresales(row.id); ElMessage.success('删除成功'); fetchData()
  }).catch(() => {})
}
onMounted(fetchData)
</script>
