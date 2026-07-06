<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadRequestOptions } from 'element-plus'
import { getUserPage, type SysUser } from '@/api/system'
import {
  completeTask,
  deleteDeployment,
  deployProcess,
  getDoneTasks,
  getProcessDefinitions,
  getProcessDiagramUrl,
  getProcessHistory,
  getTodoTasks,
  transferTask,
  type ProcessDefinition,
  type ProcessHistory,
  type WorkflowTask
} from '@/api/workflow'

type TabName = 'todo' | 'done' | 'definition'
const activeTab = ref<TabName>('todo')
const loading = ref(false)
const total = ref(0)
const query = reactive({ page: 1, size: 10 })

const todoData = ref<WorkflowTask[]>([])
const doneData = ref<WorkflowTask[]>([])
const defData = ref<ProcessDefinition[]>([])

// Handle task dialog
const handleVisible = ref(false)
const handleLoading = ref(false)
const handleSubmitting = ref(false)
const currentTask = ref<WorkflowTask | null>(null)
const handleComment = ref('')
const historyData = ref<ProcessHistory[]>([])

// Transfer task dialog
const transferVisible = ref(false)
const transferSubmitting = ref(false)
const transferTaskId = ref('')
const targetUserId = ref<number | undefined>(undefined)
const userOptions = ref<SysUser[]>([])

// Diagram dialog
const diagramVisible = ref(false)
const diagramUrl = ref('')

async function loadData() {
  loading.value = true
  try {
    if (activeTab.value === 'todo') {
      const res = await getTodoTasks(query)
      todoData.value = res.records
      total.value = res.total
    } else if (activeTab.value === 'done') {
      const res = await getDoneTasks(query)
      doneData.value = res.records
      total.value = res.total
    } else {
      const res = await getProcessDefinitions(query)
      defData.value = res.records
      total.value = res.total
    }
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  query.page = 1
  loadData()
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

// Load users for transfer
async function loadUsers() {
  try {
    const res = await getUserPage({ page: 1, size: 100 })
    userOptions.value = res.records
  } catch {
    /* handled by interceptor */
  }
}

async function openHandleDialog(row: WorkflowTask) {
  currentTask.value = row
  handleComment.value = ''
  historyData.value = []
  handleVisible.value = true
  if (row.processInstanceId) {
    handleLoading.value = true
    try {
      historyData.value = await getProcessHistory(row.processInstanceId)
    } catch {
      historyData.value = []
    } finally {
      handleLoading.value = false
    }
  }
}

async function doCompleteTask(approved: boolean) {
  if (!currentTask.value?.id) return
  handleSubmitting.value = true
  try {
    await completeTask({
      taskId: currentTask.value.id,
      variables: { approved },
      comment: handleComment.value
    })
    ElMessage.success(approved ? '审批通过' : '已驳回')
    handleVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    handleSubmitting.value = false
  }
}

function openTransferDialog(row: WorkflowTask) {
  if (!row.id) return
  transferTaskId.value = row.id
  targetUserId.value = undefined
  transferVisible.value = true
}

async function handleTransferSubmit() {
  const uid = targetUserId.value
  if (!uid) {
    ElMessage.warning('请选择转办用户')
    return
  }
  transferSubmitting.value = true
  try {
    await transferTask({ taskId: transferTaskId.value, targetUserId: uid })
    ElMessage.success('转办成功')
    transferVisible.value = false
    loadData()
  } catch {
    /* handled by interceptor */
  } finally {
    transferSubmitting.value = false
  }
}

function viewDiagram(row: WorkflowTask) {
  if (!row.processInstanceId) {
    ElMessage.warning('该任务无流程实例信息')
    return
  }
  diagramUrl.value = getProcessDiagramUrl(row.processInstanceId)
  diagramVisible.value = true
}

function handleDeleteDeployment(row: ProcessDefinition) {
  if (!row.deploymentId) {
    ElMessage.warning('该流程无部署ID')
    return
  }
  ElMessageBox.confirm(`确定删除流程「${row.name}」的部署吗？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteDeployment(row.deploymentId!)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {
      /* cancelled */
    })
}

async function handleDeploy(options: UploadRequestOptions): Promise<void> {
  const file = options.file as File
  if (!file) return
  const isValid = /\.bpmn(\.xml)?|\.xml$/i.test(file.name)
  if (!isValid) {
    ElMessage.warning('请上传 BPMN 或 XML 流程文件')
    options.onError(new Error('invalid file type') as unknown as Parameters<typeof options.onError>[0])
    return
  }
  try {
    await deployProcess(file)
    ElMessage.success('流程部署成功')
    if (activeTab.value !== 'definition') activeTab.value = 'definition'
    loadData()
    options.onSuccess({})
  } catch {
    options.onError(new Error('deploy failed') as unknown as Parameters<typeof options.onError>[0])
  }
}

onMounted(() => {
  loadUsers()
  loadData()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span class="page-title">工作流中心</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待办任务" name="todo" />
        <el-tab-pane label="已办任务" name="done" />
        <el-tab-pane label="流程定义" name="definition" />
      </el-tabs>

      <!-- Todo tasks -->
      <template v-if="activeTab === 'todo'">
        <div class="toolbar">
          <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
        </div>
        <el-table v-loading="loading" :data="todoData" border stripe>
          <el-table-column prop="name" label="任务名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="processDefinitionName" label="流程名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="businessKey" label="业务编号" min-width="150" show-overflow-tooltip />
          <el-table-column prop="startUserName" label="申请人" min-width="110" />
          <el-table-column prop="createTime" label="创建时间" min-width="160" />
          <el-table-column label="操作" width="170" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openHandleDialog(row)">办理</el-button>
              <el-button link type="warning" @click="openTransferDialog(row)">转办</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无待办任务" />
          </template>
        </el-table>
      </template>

      <!-- Done tasks -->
      <template v-else-if="activeTab === 'done'">
        <div class="toolbar">
          <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
        </div>
        <el-table v-loading="loading" :data="doneData" border stripe>
          <el-table-column prop="name" label="任务名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="processDefinitionName" label="流程名称" min-width="160" show-overflow-tooltip />
          <el-table-column prop="businessKey" label="业务编号" min-width="150" show-overflow-tooltip />
          <el-table-column prop="endTime" label="完成时间" min-width="160" />
          <el-table-column prop="duration" label="耗时" min-width="130" />
          <el-table-column label="操作" width="130" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="viewDiagram(row)">查看流程图</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无已办任务" />
          </template>
        </el-table>
      </template>

      <!-- Process definitions -->
      <template v-else>
        <div class="toolbar">
          <el-upload
            :http-request="handleDeploy"
            :show-file-list="false"
            accept=".bpmn,.bpmn.xml,.xml"
          >
            <el-button type="primary" :icon="'Upload'">部署流程</el-button>
          </el-upload>
          <el-button :icon="'Refresh'" @click="loadData">刷新</el-button>
        </div>
        <el-table v-loading="loading" :data="defData" border stripe>
          <el-table-column prop="name" label="流程名称" min-width="180" show-overflow-tooltip />
          <el-table-column prop="key" label="流程Key" min-width="150" />
          <el-table-column prop="version" label="版本" width="90" align="center" />
          <el-table-column prop="category" label="分类" min-width="120" show-overflow-tooltip />
          <el-table-column prop="deployTime" label="部署时间" min-width="160" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.suspended ? 'danger' : 'success'" size="small">
                {{ row.suspended ? '已挂起' : '激活' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button link type="danger" @click="handleDeleteDeployment(row)">删除部署</el-button>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无流程定义" />
          </template>
        </el-table>
      </template>

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

    <!-- Handle task dialog -->
    <el-dialog v-model="handleVisible" title="办理任务" width="640px" destroy-on-close>
      <div v-if="currentTask" class="task-info">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="任务名称">{{ currentTask.name }}</el-descriptions-item>
          <el-descriptions-item label="流程名称">{{ currentTask.processDefinitionName }}</el-descriptions-item>
          <el-descriptions-item label="业务编号">{{ currentTask.businessKey || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentTask.startUserName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentTask.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <h4 class="sub-title">审批意见</h4>
      <el-input
        v-model="handleComment"
        type="textarea"
        :rows="3"
        placeholder="请输入审批意见"
      />

      <h4 class="sub-title">流程历史</h4>
      <el-timeline v-loading="handleLoading">
        <el-timeline-item
          v-for="item in historyData"
          :key="item.id"
          :timestamp="item.endTime || item.startTime"
          placement="top"
          :type="item.endTime ? 'success' : 'primary'"
        >
          <div class="history-item">
            <span class="history-node">{{ item.activityName }}</span>
            <span v-if="item.assigneeName" class="history-user">办理人：{{ item.assigneeName }}</span>
            <span v-if="item.duration" class="history-duration">耗时：{{ item.duration }}</span>
          </div>
          <div v-if="item.comment" class="history-comment">{{ item.comment }}</div>
        </el-timeline-item>
        <el-empty v-if="!handleLoading && historyData.length === 0" description="暂无历史记录" />
      </el-timeline>

      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="danger" :loading="handleSubmitting" @click="doCompleteTask(false)">驳回</el-button>
        <el-button type="success" :loading="handleSubmitting" @click="doCompleteTask(true)">通过</el-button>
      </template>
    </el-dialog>

    <!-- Transfer task dialog -->
    <el-dialog v-model="transferVisible" title="转办任务" width="460px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item label="转办给">
          <el-select
            v-model="targetUserId"
            placeholder="请选择用户"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="u in userOptions"
              :key="u.id"
              :label="u.nickname"
              :value="u.id!"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" :loading="transferSubmitting" @click="handleTransferSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- Diagram dialog -->
    <el-dialog v-model="diagramVisible" title="流程图" width="900px" destroy-on-close>
      <div class="diagram-wrapper">
        <img :src="diagramUrl" alt="流程图" class="diagram-img" />
      </div>
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
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
.sub-title {
  margin: 16px 0 8px;
  font-size: 14px;
  font-weight: 600;
}
.task-info {
  margin-bottom: 8px;
}
.history-item {
  display: flex;
  gap: 16px;
  font-size: 13px;
}
.history-node {
  font-weight: 600;
}
.history-user,
.history-duration {
  color: var(--el-text-color-secondary);
}
.history-comment {
  margin-top: 4px;
  font-size: 13px;
  color: var(--el-text-color-regular);
}
.diagram-wrapper {
  text-align: center;
  max-height: 70vh;
  overflow: auto;
}
.diagram-img {
  max-width: 100%;
}
</style>
