<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="项目编码">
          <el-input v-model="queryForm.projectCode" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="queryForm.projectName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="合同号">
          <el-input v-model="queryForm.contractNo" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>项目列表</span>
          <el-button type="primary" @click="handleAdd">新增项目</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="projectCode" label="项目编码" width="150" />
        <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contractNo" label="合同号" width="150" />
        <el-table-column prop="officeCode" label="办事处" width="120" />
        <el-table-column prop="projectType" label="项目类型" width="100" />
        <el-table-column prop="projectState" label="项目状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.projectState === 1 ? 'success' : 'info'">
              {{ row.projectState === 1 ? '进行中' : '其他' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="pmCode" label="项目经理" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="项目编码">
          <el-input v-model="formData.projectCode" />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="formData.projectName" />
        </el-form-item>
        <el-form-item label="合同号">
          <el-input v-model="formData.contractNo" />
        </el-form-item>
        <el-form-item label="办事处">
          <el-input v-model="formData.officeCode" />
        </el-form-item>
        <el-form-item label="项目类型">
          <el-input v-model="formData.projectType" />
        </el-form-item>
        <el-form-item label="项目经理">
          <el-input v-model="formData.pmCode" />
        </el-form-item>
        <el-form-item label="服务经理">
          <el-input v-model="formData.smCode" />
        </el-form-item>
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
import { useRouter } from 'vue-router'
import { getProjectList, addProject, updateProject } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')

const queryForm = reactive({
  pageNum: 1, pageSize: 10,
  projectCode: '', projectName: '', contractNo: ''
})

const formData = reactive({
  id: null, projectCode: '', projectName: '', contractNo: '',
  officeCode: '', projectType: '', pmCode: '', smCode: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getProjectList(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => { queryForm.pageNum = 1; fetchData() }
const resetQuery = () => {
  Object.assign(queryForm, { projectCode: '', projectName: '', contractNo: '' })
  handleQuery()
}

const handleAdd = () => {
  dialogTitle.value = '新增项目'
  Object.assign(formData, { id: null, projectCode: '', projectName: '', contractNo: '', officeCode: '', projectType: '', pmCode: '', smCode: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑项目'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDetail = (row) => {
  router.push(`/project/detail/${row.id}`)
}

const handleSubmit = async () => {
  if (formData.id) {
    await updateProject(formData)
    ElMessage.success('更新成功')
  } else {
    await addProject(formData)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  fetchData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该项目？', '提示', { type: 'warning' }).then(async () => {
    // TODO: 调用删除接口
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

onMounted(fetchData)
</script>
