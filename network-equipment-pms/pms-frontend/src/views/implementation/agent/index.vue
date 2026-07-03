<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  createAgent,
  deleteAgent,
  evaluateAgent,
  getAgentPage,
  getScoresByAgent,
  updateAgent,
  type Agent,
  type AgentScore
} from '@/api/implementation'

const loading = ref(false)
const tableData = ref<Agent[]>([])
const total = ref(0)
const query = reactive({ page: 1, size: 10, keyword: '' })

// Agent form dialog
const dialogVisible = ref(false)
const dialogTitle = ref('')
const submitting = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Agent>(createEmptyForm())

const rules: FormRules = {
  agentName: [{ required: true, message: '请输入代理商名称', trigger: 'blur' }],
  agentCode: [{ required: true, message: '请输入代理商编码', trigger: 'blur' }]
}

function createEmptyForm(): Agent {
  return {
    agentName: '',
    agentCode: '',
    contactPerson: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    qualification: '',
    status: 1,
    remark: ''
  }
}

// Evaluation dialog
const evalVisible = ref(false)
const evalSubmitting = ref(false)
const evalForm = reactive({
  agentId: 0,
  agentName: '',
  taskId: undefined as number | undefined,
  responseSpeedScore: 8,
  constructionQualityScore: 8,
  documentCompletenessScore: 8,
  comment: ''
})

// Evaluation history dialog
const historyVisible = ref(false)
const historyLoading = ref(false)
const historyData = ref<AgentScore[]>([])
const historyAgentName = ref('')

async function loadData() {
  loading.value = true
  try {
    const res = await getAgentPage(query)
    tableData.value = res.records
    total.value = res.total
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.keyword = ''
  query.page = 1
  loadData()
}

function handleAdd() {
  dialogTitle.value = '新增代理商'
  Object.assign(form, createEmptyForm())
  dialogVisible.value = true
}

function handleEdit(row: Agent) {
  dialogTitle.value = '编辑代理商'
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await updateAgent(form)
        ElMessage.success('更新成功')
      } else {
        await createAgent(form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadData()
    } catch {
      /* handled by interceptor */
    } finally {
      submitting.value = false
    }
  })
}

function handleDelete(row: Agent) {
  if (!row.id) return
  ElMessageBox.confirm(`确定删除代理商「${row.agentName}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteAgent(row.id!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

function handleEvaluate(row: Agent) {
  if (!row.id) return
  evalForm.agentId = row.id
  evalForm.agentName = row.agentName
  evalForm.taskId = undefined
  evalForm.responseSpeedScore = 8
  evalForm.constructionQualityScore = 8
  evalForm.documentCompletenessScore = 8
  evalForm.comment = ''
  evalVisible.value = true
}

async function handleEvalSubmit() {
  evalSubmitting.value = true
  try {
    await evaluateAgent({
      agentId: evalForm.agentId,
      taskId: evalForm.taskId,
      responseSpeedScore: evalForm.responseSpeedScore,
      constructionQualityScore: evalForm.constructionQualityScore,
      documentCompletenessScore: evalForm.documentCompletenessScore,
      comment: evalForm.comment
    })
    ElMessage.success('评价成功')
    evalVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    evalSubmitting.value = false
  }
}

async function handleViewHistory(row: Agent) {
  if (!row.id) return
  historyAgentName.value = row.agentName
  historyVisible.value = true
  historyLoading.value = true
  try {
    historyData.value = await getScoresByAgent(row.id)
  } catch {
    historyData.value = []
  } finally {
    historyLoading.value = false
  }
}

function avgScore(row: AgentScore): string {
  const sum =
    (row.responseSpeedScore ?? 0) +
    (row.constructionQualityScore ?? 0) +
    (row.documentCompletenessScore ?? 0)
  return (sum / 3).toFixed(1)
}

function handlePageChange(p: number) {
  query.page = p
  loadData()
}

function handleSizeChange(s: number) {
  query.size = s
  query.page = 1
  loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">代理商管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="关键字">
          <el-input
            v-model="query.keyword"
            placeholder="代理商名称/编码"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleSearch">查询</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAdd">新增代理商</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="agentName" label="代理商名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="agentCode" label="编码" min-width="120" />
        <el-table-column prop="contactPerson" label="联系人" min-width="100" />
        <el-table-column prop="contactPhone" label="联系电话" min-width="130" />
        <el-table-column prop="contactEmail" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="qualification" label="资质" min-width="120" show-overflow-tooltip />
        <el-table-column label="综合评分" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.overallScore != null" type="warning" size="small">
              {{ row.overallScore.toFixed(1) }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleEvaluate(row)">评价</el-button>
            <el-button link type="info" @click="handleViewHistory(row)">评价记录</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        :current-page="query.page"
        :page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- Agent form dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="代理商名称" prop="agentName">
          <el-input v-model="form.agentName" placeholder="请输入代理商名称" />
        </el-form-item>
        <el-form-item label="代理商编码" prop="agentCode">
          <el-input v-model="form.agentCode" :disabled="!!form.id" placeholder="请输入代理商编码" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="资质" prop="qualification">
          <el-input v-model="form.qualification" placeholder="请输入资质信息" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Evaluation dialog -->
    <el-dialog v-model="evalVisible" :title="`评价代理商 - ${evalForm.agentName}`" width="520px" destroy-on-close>
      <el-form label-width="120px">
        <el-form-item label="关联任务ID">
          <el-input-number
            v-model="evalForm.taskId"
            :min="1"
            controls-position="right"
            placeholder="可选"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="响应速度评分">
          <el-rate v-model="evalForm.responseSpeedScore" :max="10" show-score />
        </el-form-item>
        <el-form-item label="施工质量评分">
          <el-rate v-model="evalForm.constructionQualityScore" :max="10" show-score />
        </el-form-item>
        <el-form-item label="文档完整性评分">
          <el-rate v-model="evalForm.documentCompletenessScore" :max="10" show-score />
        </el-form-item>
        <el-form-item label="评价意见">
          <el-input
            v-model="evalForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入评价意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evalVisible = false">取消</el-button>
        <el-button type="primary" :loading="evalSubmitting" @click="handleEvalSubmit">提交评价</el-button>
      </template>
    </el-dialog>

    <!-- Evaluation history dialog -->
    <el-dialog v-model="historyVisible" :title="`评价记录 - ${historyAgentName}`" width="760px" destroy-on-close>
      <el-table v-loading="historyLoading" :data="historyData" border stripe size="small">
        <el-table-column prop="scoreTime" label="评价时间" min-width="160" />
        <el-table-column prop="taskId" label="任务ID" width="100" />
        <el-table-column prop="responseSpeedScore" label="响应速度" width="100" align="center" />
        <el-table-column prop="constructionQualityScore" label="施工质量" width="100" align="center" />
        <el-table-column prop="documentCompletenessScore" label="文档完整性" width="110" align="center" />
        <el-table-column label="综合评分" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="warning" size="small">{{ avgScore(row) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scorer" label="评价人" min-width="100" />
        <el-table-column prop="comment" label="评价意见" min-width="180" show-overflow-tooltip />
        <template #empty>
          <el-empty description="暂无评价记录" />
        </template>
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
