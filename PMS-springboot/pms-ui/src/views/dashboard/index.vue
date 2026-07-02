<template>
  <div>
    <el-card>
      <template #header><span>工作台</span></template>
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <!-- 待办任务 -->
        <el-tab-pane label="待办任务" name="task">
          <el-table :data="taskList" v-loading="taskLoading" stripe size="small">
            <el-table-column prop="projectCode" label="项目编码" width="140" />
            <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="taskName" label="任务名称" width="150" />
            <el-table-column prop="taskState" label="任务状态" width="100" />
            <el-table-column prop="officeName" label="办事处" width="100" />
            <el-table-column prop="programManager" label="项目经理" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" link @click="$router.push(`/project/detail/${row.projectId}`)">办理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 日常项目跟踪 -->
        <el-tab-pane label="日常项目跟踪" name="daily">
          <el-form :inline="true" :model="dailyQuery" style="margin-bottom:12px">
            <el-form-item label="项目名称"><el-input v-model="dailyQuery.projectName" clearable /></el-form-item>
            <el-form-item label="办事处">
              <el-select v-model="dailyQuery.officeCode" clearable filterable>
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </el-form-item>
            <el-form-item label="销售人员"><el-input v-model="dailyQuery.salesManName" clearable /></el-form-item>
            <el-form-item><el-button type="primary" @click="fetchDaily">查询</el-button></el-form-item>
          </el-form>
          <el-table :data="dailyList" v-loading="dailyLoading" stripe size="small">
            <el-table-column prop="projectCode" label="项目编码" width="140" />
            <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip>
              <template #default="{ row }"><el-link type="primary" @click="$router.push(`/project/detail/${row.projectId}`)">{{ row.projectName }}</el-link></template>
            </el-table-column>
            <el-table-column prop="officeName" label="办事处" width="100" />
            <el-table-column prop="salesManName" label="销售" width="80" />
            <el-table-column prop="serviceManagerName" label="服务经理" width="100" />
            <el-table-column prop="programManagerName" label="项目经理" width="100" />
            <el-table-column prop="projectStateName" label="状态" width="100" />
            <el-table-column prop="executionStateName" label="实施状态" width="100" />
            <el-table-column prop="shipmentStateName" label="发货状态" width="100" />
            <el-table-column prop="projectStartTime" label="开始时间" width="120"><template #default="{ row }">{{ formatDate(row.projectStartTime) }}</template></el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 已办理任务 -->
        <el-tab-pane label="已办理任务" name="history">
          <el-table :data="historyList" v-loading="historyLoading" stripe size="small">
            <el-table-column prop="projectCode" label="项目编码" width="140" />
            <el-table-column prop="projectName" label="项目名称" min-width="200" show-overflow-tooltip />
            <el-table-column prop="taskName" label="任务名称" width="150" />
            <el-table-column prop="assigneeName" label="处理人" width="100" />
            <el-table-column prop="endTime" label="完成时间" width="170"><template #default="{ row }">{{ formatDate(row.endTime) }}</template></el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 技术公告任务 -->
        <el-tab-pane label="技术公告任务" name="prob">
          <el-table :data="probTaskList" v-loading="probLoading" stripe size="small">
            <el-table-column prop="probNum" label="公告编号" width="140" />
            <el-table-column prop="theme" label="主题" min-width="200" show-overflow-tooltip>
              <template #default="{ row }"><el-link type="primary" @click="$router.push(`/prob/detail/${row.probId}`)">{{ row.theme }}</el-link></template>
            </el-table-column>
            <el-table-column prop="taskName" label="任务" width="150" />
            <el-table-column prop="taskState" label="状态" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
          </el-table>
        </el-tab-pane>

        <!-- 转包任务 -->
        <el-tab-pane label="转包任务" name="subcontract">
          <el-table :data="subcontractTaskList" v-loading="subcontractLoading" stripe size="small">
            <el-table-column prop="subcontractNo" label="转包编号" width="140" />
            <el-table-column prop="subcontractName" label="转包名称" min-width="200" show-overflow-tooltip>
              <template #default="{ row }"><el-link type="primary" @click="$router.push(`/subcontract/detail/${row.id}`)">{{ row.subcontractName }}</el-link></template>
            </el-table-column>
            <el-table-column prop="taskName" label="任务" width="150" />
            <el-table-column prop="taskState" label="状态" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="170"><template #default="{ row }">{{ formatDate(row.createTime) }}</template></el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTodoTasks, getDailyTasks, getHistoryTasks, getProbTasks, getSubcontractTasks } from '@/api/workspace'
import { listDepts } from '@/api/system'
const activeTab = ref('task')
const deptList = ref([])

const taskList = ref([])
const taskLoading = ref(false)
const dailyList = ref([])
const dailyLoading = ref(false)
const historyList = ref([])
const historyLoading = ref(false)
const probTaskList = ref([])
const probLoading = ref(false)
const subcontractTaskList = ref([])
const subcontractLoading = ref(false)

const dailyQuery = reactive({ projectName: '', officeCode: '', salesManName: '' })

const formatDate = (d) => d ? new Date(d).toLocaleDateString('zh-CN') : ''

const handleTabChange = async (tab) => {
  const loaders = {
    task: fetchTask, daily: fetchDaily, history: fetchHistory,
    prob: fetchProb, subcontract: fetchSubcontract
  }
  if (loaders[tab]) await loaders[tab]()
}

const fetchTask = async () => { taskLoading.value = true; try { const r = await getTodoTasks(); taskList.value = r.data || [] } finally { taskLoading.value = false } }
const fetchDaily = async () => { dailyLoading.value = true; try { const r = await getDailyTasks(dailyQuery); dailyList.value = r.data || [] } finally { dailyLoading.value = false } }
const fetchHistory = async () => { historyLoading.value = true; try { const r = await getHistoryTasks(); historyList.value = r.data || [] } finally { historyLoading.value = false } }
const fetchProb = async () => { probLoading.value = true; try { const r = await getProbTasks(); probTaskList.value = r.data || [] } finally { probLoading.value = false } }
const fetchSubcontract = async () => { subcontractLoading.value = true; try { const r = await getSubcontractTasks(); subcontractTaskList.value = r.data || [] } finally { subcontractLoading.value = false } }

onMounted(async () => { fetchTask(); try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {} })
</script>
