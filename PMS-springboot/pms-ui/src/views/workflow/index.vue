<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>工作流管理</span>
        </div>
      </template>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 我的待办 -->
        <el-tab-pane label="我的待办" name="todo">
          <el-table :data="todoList" v-loading="todoLoading" stripe size="small">
            <el-table-column prop="taskName" label="任务名称" width="150" />
            <el-table-column prop="processName" label="流程名称" width="150" />
            <el-table-column prop="assignee" label="处理人" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
            <el-table-column prop="dueDate" label="到期时间" width="170"><template #default="{ row }">{{ formatDate(row.dueDate) }}</template></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="handleTask(row)">办理</el-button>
                <el-button size="small" link @click="viewProcess(row)">查看流程</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 流程部署 -->
        <el-tab-pane label="流程部署" name="deploy">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="showDeployDialog">发布流程</el-button>
          </div>
          <el-table :data="deployList" v-loading="deployLoading" stripe size="small">
            <el-table-column prop="deploymentId" label="部署ID" width="150" />
            <el-table-column prop="name" label="流程名称" min-width="200" />
            <el-table-column prop="deploymentTime" label="部署时间" width="170"><template #default="{ row }">{{ formatDate(row.deploymentTime) }}</template></el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="viewImage(row)">查看流程图</el-button>
                <el-button size="small" type="danger" link @click="deleteDeploy(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 历史任务 -->
        <el-tab-pane label="历史任务" name="history">
          <el-table :data="historyList" v-loading="historyLoading" stripe size="small">
            <el-table-column prop="taskName" label="任务名称" width="150" />
            <el-table-column prop="processName" label="流程名称" width="150" />
            <el-table-column prop="assignee" label="处理人" width="100" />
            <el-table-column prop="startTime" label="开始时间" width="170"><template #default="{ row }">{{ formatDate(row.startTime) }}</template></el-table-column>
            <el-table-column prop="endTime" label="完成时间" width="170"><template #default="{ row }">{{ formatDate(row.endTime) }}</template></el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 委派管理 -->
        <el-tab-pane label="委派管理" name="delegate">
          <div style="margin-bottom:12px">
            <el-button type="primary" size="small" @click="showDelegateDialog">添加委派规则</el-button>
          </div>
          <el-table :data="delegateList" v-loading="delegateLoading" stripe size="small">
            <el-table-column prop="processName" label="流程名称" width="150" />
            <el-table-column prop="fromUser" label="委派人" width="120" />
            <el-table-column prop="toUser" label="被委派人" width="120" />
            <el-table-column prop="startTime" label="开始时间" width="170"><template #default="{ row }">{{ formatDate(row.startTime) }}</template></el-table-column>
            <el-table-column prop="endTime" label="结束时间" width="170"><template #default="{ row }">{{ formatDate(row.endTime) }}</template></el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="showDelegateDialog(row)">编辑</el-button>
                <el-button size="small" type="danger" link @click="deleteDelegate(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 发布流程弹窗 -->
    <el-dialog v-model="deployDialogVisible" title="发布流程" width="500px">
      <el-form :model="deployForm" label-width="80px">
        <el-form-item label="流程名称" required><el-input v-model="deployForm.processName" /></el-form-item>
        <el-form-item label="流程Key" required><el-input v-model="deployForm.processKey" /></el-form-item>
        <el-form-item label="BPMN文件">
          <el-upload :action="`/api/workflow/deploy`" :headers="uploadHeaders" :data="deployForm" :on-success="handleDeploySuccess" :show-file-list="false">
            <el-button size="small">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="deployDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="deployProcess">发布</el-button>
      </template>
    </el-dialog>

    <!-- 委派规则弹窗 -->
    <el-dialog v-model="delegateDialogVisible" :title="delegateForm.id ? '编辑委派规则' : '添加委派规则'" width="500px">
      <el-form :model="delegateForm" label-width="80px">
        <el-form-item label="流程名称" required>
          <el-select v-model="delegateForm.processKey" style="width:100%">
            <el-option v-for="p in processList" :key="p.key" :label="p.name" :value="p.key" />
          </el-select>
        </el-form-item>
        <el-form-item label="委派人" required><el-input v-model="delegateForm.fromUser" /></el-form-item>
        <el-form-item label="被委派人" required><el-input v-model="delegateForm.toUser" /></el-form-item>
        <el-form-item label="开始时间" required><el-date-picker v-model="delegateForm.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
        <el-form-item label="结束时间" required><el-date-picker v-model="delegateForm.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="delegateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveDelegate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyTasks, getDeployments, deleteDeployment, getHistoryTasks, getDelegates, addDelegate, updateDelegate, deleteDelegate as apiDeleteDelegate } from '@/api/workflow'
import { ElMessage, ElMessageBox } from 'element-plus'
const router = useRouter()
const activeTab = ref('todo')
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('pms_token')}` }))

const todoList = ref([])
const todoLoading = ref(false)
const deployList = ref([])
const deployLoading = ref(false)
const historyList = ref([])
const historyLoading = ref(false)
const delegateList = ref([])
const delegateLoading = ref(false)
const processList = ref([])

const deployDialogVisible = ref(false)
const deployForm = reactive({ processName: '', processKey: '' })
const delegateDialogVisible = ref(false)
const delegateForm = reactive({ id: null, processKey: '', fromUser: '', toUser: '', startTime: '', endTime: '' })

const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''

const handleTabChange = async (tab) => {
  const loaders = { todo: fetchTodo, deploy: fetchDeploy, history: fetchHistory, delegate: fetchDelegate }
  if (loaders[tab]) await loaders[tab]()
}

const fetchTodo = async () => { todoLoading.value = true; try { const r = await getMyTasks(); todoList.value = r.data || [] } finally { todoLoading.value = false } }
const fetchDeploy = async () => { deployLoading.value = true; try { const r = await getDeployments(); deployList.value = r.data || [] } finally { deployLoading.value = false } }
const fetchHistory = async () => { historyLoading.value = true; try { const r = await getHistoryTasks(); historyList.value = r.data || [] } finally { historyLoading.value = false } }
const fetchDelegate = async () => { delegateLoading.value = true; try { const r = await getDelegates(); delegateList.value = r.data || [] } finally { delegateLoading.value = false } }

const handleTask = (row) => { router.push(`/workflow/task/${row.taskId || row.processInstanceId}`) }
const viewProcess = (row) => { window.open(`/api/workflow/instance/${row.processInstanceId}/image`) }
const viewImage = (row) => { window.open(`/api/workflow/deploy/${row.deploymentId}/image`) }
const deleteDeploy = (row) => { ElMessageBox.confirm('确认删除该流程部署？', '提示', { type: 'warning' }).then(async () => { await deleteDeployment(row.deploymentId); ElMessage.success('删除成功'); fetchDeploy() }).catch(() => {}) }

const showDeployDialog = () => { Object.assign(deployForm, { processName: '', processKey: '' }); deployDialogVisible.value = true }
const handleDeploySuccess = (res) => { if (res.code === 200) { ElMessage.success('发布成功'); deployDialogVisible.value = false; fetchDeploy() } }
const deployProcess = () => { /* manual deploy */ }

const showDelegateDialog = (row) => { if (row) Object.assign(delegateForm, row); else Object.assign(delegateForm, { id: null, processKey: '', fromUser: '', toUser: '', startTime: '', endTime: '' }); delegateDialogVisible.value = true }
const saveDelegate = async () => { if (delegateForm.id) await updateDelegate(delegateForm); else await addDelegate(delegateForm); ElMessage.success('保存成功'); delegateDialogVisible.value = false; fetchDelegate() }
const deleteDelegate = (row) => { ElMessageBox.confirm('确认删除该委派规则？', '提示', { type: 'warning' }).then(async () => { await apiDeleteDelegate(row.id); ElMessage.success('已删除'); fetchDelegate() }).catch(() => {}) }

onMounted(fetchTodo)
</script>
