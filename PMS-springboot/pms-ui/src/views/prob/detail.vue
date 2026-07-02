<template>
  <div v-loading="loading">
    <el-card style="margin-bottom:16px">
      <template #header><div style="display:flex;justify-content:space-between;align-items:center"><span>技术公告详情</span><el-button @click="$router.back()">返回</el-button></div></template>
      <el-descriptions :column="3" border size="small">
        <el-descriptions-item label="公告编号">{{ data.probNum }}</el-descriptions-item>
        <el-descriptions-item label="主题">{{ data.theme }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">{{ data.probTypeName }}</el-descriptions-item>
        <el-descriptions-item label="影响类型">{{ data.affectedTypeName }}</el-descriptions-item>
        <el-descriptions-item label="跟踪人">{{ data.trackingUserName }}</el-descriptions-item>
        <el-descriptions-item label="状态"><el-tag :type="data.probState===1?'success':'info'" size="small">{{ data.probStateName }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="产品类型">{{ data.productType }}</el-descriptions-item>
        <el-descriptions-item label="生效日期">{{ data.effectDate }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ data.createTime }}</el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="3">{{ data.description }}</el-descriptions-item>
        <el-descriptions-item label="解决方案" :span="3">{{ data.solution }}</el-descriptions-item>
        <el-descriptions-item label="影响范围" :span="3">{{ data.impactScope }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-tabs v-model="activeTab">
      <!-- 受影响软件版本 -->
      <el-tab-pane label="受影响版本" name="softVersion">
        <el-table :data="softVersions" stripe size="small">
          <el-table-column prop="itemCode" label="产品编码" width="130" />
          <el-table-column prop="itemName" label="产品名称" min-width="200" />
          <el-table-column prop="oldVersion" label="旧版本" width="130" />
          <el-table-column prop="newVersion" label="新版本" width="130" />
          <el-table-column label="受影响项目" min-width="200">
            <template #default="{ row }">
              <span v-for="p in row.affectedProjects" :key="p.projectId">
                <el-link type="primary" @click="$router.push(`/project/detail/${p.projectId}`)">{{ p.projectCode }}</el-link>；
              </span>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 受影响项目 -->
      <el-tab-pane label="受影响项目" name="project">
        <el-table :data="affectedProjects" stripe size="small">
          <el-table-column prop="projectCode" label="项目编码" width="140">
            <template #default="{ row }"><el-link type="primary" @click="$router.push(`/project/detail/${row.projectId}`)">{{ row.projectCode }}</el-link></template>
          </el-table-column>
          <el-table-column prop="projectName" label="项目名称" min-width="200" />
          <el-table-column prop="officeName" label="办事处" width="100" />
          <el-table-column prop="softVersion" label="当前版本" width="130" />
          <el-table-column prop="projectStateName" label="状态" width="100" />
        </el-table>
      </el-tab-pane>

      <!-- 修复任务 -->
      <el-tab-pane label="修复任务" name="task">
        <div style="margin-bottom:12px">
          <el-button type="primary" size="small" @click="showTaskDialog()">发布修复任务</el-button>
        </div>
        <el-table :data="tasks" stripe size="small">
          <el-table-column prop="taskName" label="任务名称" min-width="200" />
          <el-table-column prop="assignee" label="负责人" width="100" />
          <el-table-column prop="taskState" label="状态" width="100" />
          <el-table-column prop="createTime" label="创建时间" width="170" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button size="small" type="primary" link @click="showTaskDialog(row)">编辑</el-button>
              <el-button size="small" link @click="updateTask(row)">更新状态</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 阅读记录 -->
      <el-tab-pane label="阅读记录" name="readLog">
        <el-table :data="readLogs" stripe size="small">
          <el-table-column prop="readerName" label="阅读人" width="100" />
          <el-table-column prop="readTime" label="阅读时间" width="170" />
          <el-table-column prop="readerDept" label="部门" width="150" />
        </el-table>
      </el-tab-pane>

      <!-- 附件 -->
      <el-tab-pane label="附件" name="file">
        <el-table :data="files" stripe size="small">
          <el-table-column prop="fileName" label="文件名" min-width="200" />
          <el-table-column prop="fileSize" label="大小" width="100" />
          <el-table-column prop="uploadBy" label="上传人" width="100" />
          <el-table-column prop="uploadTime" label="上传时间" width="170" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }"><el-button size="small" type="primary" link @click="downloadFile(row)">下载</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 任务弹窗 -->
    <el-dialog v-model="taskDialogVisible" :title="taskForm.id ? '编辑任务' : '发布修复任务'" width="500px">
      <el-form :model="taskForm" label-width="80px">
        <el-form-item label="任务名称" required><el-input v-model="taskForm.taskName" /></el-form-item>
        <el-form-item label="负责人" required><el-input v-model="taskForm.assignee" /></el-form-item>
        <el-form-item label="任务说明"><el-input v-model="taskForm.taskDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="计划日期"><el-date-picker v-model="taskForm.planDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTask">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProb, getProbSoftVersions, getProbAffectedProjects, getProbTasks, getProbReadLogs, getProbFiles, createProbTask, updateProbTask } from '@/api/prob'
import { ElMessage } from 'element-plus'
const route = useRoute()
const id = route.params.id
const loading = ref(false)
const activeTab = ref('softVersion')
const data = ref({})
const softVersions = ref([])
const affectedProjects = ref([])
const tasks = ref([])
const readLogs = ref([])
const files = ref([])
const taskDialogVisible = ref(false)
const taskForm = reactive({ id: null, taskName: '', assignee: '', taskDesc: '', planDate: '' })
const downloadFile = (row) => { window.open(`/api/file/download/${row.id}`) }
const showTaskDialog = (row) => { if (row) Object.assign(taskForm, row); else Object.assign(taskForm, { id: null, taskName: '', assignee: '', taskDesc: '', planDate: '' }); taskDialogVisible.value = true }
const saveTask = async () => { if (taskForm.id) await updateProbTask(taskForm); else await createProbTask({ ...taskForm, probId: id }); ElMessage.success('保存成功'); taskDialogVisible.value = false }
const updateTask = async (row) => { /* 更新任务状态 */ }
const handleTabChange = async (tab) => {
  const loaders = {
    softVersion: async () => { const r = await getProbSoftVersions(id); softVersions.value = r.data || [] },
    project: async () => { const r = await getProbAffectedProjects(id); affectedProjects.value = r.data || [] },
    task: async () => { const r = await getProbTasks(id); tasks.value = r.data || [] },
    readLog: async () => { const r = await getProbReadLogs(id); readLogs.value = r.data || [] },
    file: async () => { const r = await getProbFiles(id); files.value = r.data || [] }
  }
  if (loaders[tab]) await loaders[tab]()
}
onMounted(async () => { loading.value = true; try { const r = await getProb(id); data.value = r.data || {}; await handleTabChange('softVersion') } finally { loading.value = false } })
</script>
