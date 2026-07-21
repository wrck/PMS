<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Bell,
  Calendar,
  Check,
  Connection,
  Files,
  Plus,
  Refresh,
  Setting,
  User,
  Delete
} from '@element-plus/icons-vue'
import {
  getProject,
  updateProject,
  type Project,
  listMilestones,
  createMilestone,
  updateMilestone,
  deleteMilestone,
  type Milestone
} from '@/api/project'
import { getProjectConfigs, updateProjectConfigs } from '@/api/project-config'
import type { MentionUser } from '@/api/system'
import PageHeader from '@/components/common/PageHeader.vue'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import UserSelect from '@/components/common/UserSelect.vue'

defineOptions({ name: 'ProjectConfig' })

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.id))

const loading = ref(false)
const project = ref<Project | null>(null)

// 基础配置
const basicForm = reactive<Project>({
  projectName: '',
  projectType: 'NETWORK_DEVICE',
  customerName: '',
  planStartDate: '',
  planEndDate: '',
  description: ''
})

// 通用配置（key-value）
const configMap = ref<Record<string, string>>({})

// 里程碑
const milestones = ref<Milestone[]>([])

// 成员（占位：后端暂无统一 API）
interface Member {
  id: string
  userId: number
  userName: string
  role: string
}
const members = ref<Member[]>([])

// 通知配置（占位）
interface NotifyRule {
  id: string
  event: string
  recipients: string
  channel: string
}
const notifyRules = ref<NotifyRule[]>([])

// 集成配置（占位）
interface Integration {
  id: string
  system: string
  endpoint: string
  apiKey: string
  enabled: boolean
}
const integrations = ref<Integration[]>([])

// 各分组保存状态
const savingGroup = ref<Record<string, boolean>>({
  basic: false,
  phase: false,
  member: false,
  milestone: false,
  notify: false,
  integration: false
})

// ============== 加载 ==============

async function loadProject() {
  if (!projectId.value) return
  try {
    const res = await getProject(projectId.value)
    project.value = res
    Object.assign(basicForm, res)
  } catch {
    /* handled by interceptor */
  }
}

async function loadConfigs() {
  if (!projectId.value) return
  loading.value = true
  try {
    const configs = await getProjectConfigs(projectId.value)
    configMap.value = configs ?? {}
  } catch {
    configMap.value = {}
  } finally {
    loading.value = false
  }
}

async function loadMilestones() {
  if (!projectId.value) return
  try {
    const res = await listMilestones(projectId.value)
    milestones.value = res ?? []
  } catch {
    milestones.value = []
  }
}

// ============== 基础配置 ==============

async function saveBasic() {
  savingGroup.value.basic = true
  try {
    await updateProject({
      ...project.value,
      ...basicForm,
      id: projectId.value
    } as Project)
    ElMessage.success('基础配置已保存')
  } finally {
    savingGroup.value.basic = false
  }
}

// ============== 阶段配置（写入 configMap） ==============

const phaseConfigKeys = [
  { key: 'phase.auto_advance', label: '自动推进阶段', placeholder: 'true / false' },
  { key: 'phase.exit_gate.strict', label: '严格退出条件', placeholder: 'true / false' },
  { key: 'phase.exit.check.approval', label: '阶段退出强制审批', placeholder: 'true / false' },
  { key: 'task.rollup.weight.field', label: '任务汇总权重字段', placeholder: 'PLANNED_HOURS / TASK_WEIGHT' }
]

const baselineConfigKeys = [
  { key: 'baseline.variance.days.threshold', label: '基线偏差天数阈值', placeholder: '默认 5' },
  { key: 'baseline.variance.percent.threshold', label: '基线偏差百分比阈值', placeholder: '默认 10' }
]

const approvalConfigKeys = [
  { key: 'approval.timeout.hours', label: '审批超时小时数', placeholder: '默认 48' },
  { key: 'approval.timeout.action', label: '超时动作', placeholder: 'AUTO_APPROVE / AUTO_REJECT / ESCALATE' },
  { key: 'approval.escalate.hours', label: '审批升级小时数', placeholder: '默认 24' },
  { key: 'approval.reminder.hours', label: '审批提醒小时数', placeholder: '默认 12' },
  { key: 'approval.max.rounds', label: '审批最大轮次', placeholder: '默认 3' }
]

async function saveConfigGroup(group: 'phase' | 'baseline' | 'approval') {
  savingGroup.value.phase = true
  try {
    await updateProjectConfigs(projectId.value, configMap.value)
    ElMessage.success(`${group === 'phase' ? '阶段' : group === 'baseline' ? '基线' : '审批'}配置已保存`)
  } finally {
    savingGroup.value.phase = false
  }
}

// ============== 成员 ==============

function addMember() {
  members.value.push({
    id: `m_${Date.now()}`,
    userId: 0,
    userName: '',
    role: ''
  })
}

function onMemberUserChange(row: Member, user: MentionUser | null) {
  if (user) {
    row.userId = user.id
    row.userName = user.realName || user.username
  } else {
    row.userId = 0
    row.userName = ''
  }
}

function removeMember(idx: number) {
  members.value.splice(idx, 1)
}

async function saveMembers() {
  savingGroup.value.member = true
  try {
    // 占位：成员管理 API 暂缺，仅本地保存提示
    ElMessage.success('成员配置已保存（占位 - 后端 API 待接入）')
  } finally {
    savingGroup.value.member = false
  }
}

// ============== 里程碑 ==============

function addMilestoneRow() {
  milestones.value.push({
    projectId: projectId.value,
    name: '新里程碑',
    type: '',
    plannedDate: '',
    description: '',
    status: 'PLANNED'
  })
}

async function saveMilestone(m: Milestone) {
  if (!m.name?.trim()) {
    ElMessage.warning('请填写里程碑名称')
    return
  }
  try {
    if (m.id) {
      await updateMilestone(m)
      ElMessage.success('里程碑已更新')
    } else {
      const created = await createMilestone(m)
      m.id = created.id
      ElMessage.success('里程碑已创建')
    }
    await loadMilestones()
  } catch {
    /* handled */
  }
}

async function removeMilestoneRow(idx: number) {
  const m = milestones.value[idx]
  if (!m) return
  if (!m.id) {
    milestones.value.splice(idx, 1)
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除里程碑「${m.name}」？`, '删除确认', { type: 'warning' })
    await deleteMilestone(m.id!)
    ElMessage.success('已删除')
    await loadMilestones()
  } catch {
    /* cancelled */
  }
}

async function saveAllMilestones() {
  savingGroup.value.milestone = true
  try {
    for (const m of milestones.value) {
      if (!m.name?.trim()) continue
      if (m.id) {
        await updateMilestone(m)
      } else {
        const created = await createMilestone(m)
        m.id = created.id
      }
    }
    ElMessage.success('里程碑已批量保存')
    await loadMilestones()
  } finally {
    savingGroup.value.milestone = false
  }
}

// ============== 通知 ==============

function addNotifyRule() {
  notifyRules.value.push({
    id: `n_${Date.now()}`,
    event: 'PHASE_EXIT',
    recipients: '',
    channel: 'EMAIL'
  })
}

function removeNotifyRule(idx: number) {
  notifyRules.value.splice(idx, 1)
}

async function saveNotifyRules() {
  savingGroup.value.notify = true
  try {
    ElMessage.success('通知配置已保存（占位 - 后端 API 待接入）')
  } finally {
    savingGroup.value.notify = false
  }
}

// ============== 集成 ==============

function addIntegration() {
  integrations.value.push({
    id: `i_${Date.now()}`,
    system: 'SAP',
    endpoint: '',
    apiKey: '',
    enabled: true
  })
}

function removeIntegration(idx: number) {
  integrations.value.splice(idx, 1)
}

async function saveIntegrations() {
  savingGroup.value.integration = true
  try {
    ElMessage.success('集成配置已保存（占位 - 后端 API 待接入）')
  } finally {
    savingGroup.value.integration = false
  }
}

// ============== 初始化 ==============

onMounted(async () => {
  await loadProject()
  await Promise.all([loadConfigs(), loadMilestones()])
})
</script>

<template>
  <div class="project-config-page">
    <PageHeader
      :title="`项目配置${project?.projectName ? ' · ' + project.projectName : ''}`"
      description="6 组配置卡片，每组可独立保存"
    >
      <template #actions>
        <el-button :icon="ArrowLeft" @click="router.back()">返回</el-button>
        <el-button :icon="Refresh" @click="loadConfigs">刷新</el-button>
      </template>
    </PageHeader>

    <SkeletonCard v-if="loading" :loading="true" :rows="6" />

    <div v-else class="config-groups">
      <!-- 1. 基础配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><Setting /></el-icon>基础配置</span>
            <el-button
              type="primary"
              size="small"
              :icon="Check"
              :loading="savingGroup.basic"
              @click="saveBasic"
            >
              保存
            </el-button>
          </div>
        </template>
        <el-form :model="basicForm" label-width="100px">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="项目名称">
                <el-input v-model="basicForm.projectName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目类型">
                <el-select v-model="basicForm.projectType" style="width: 100%">
                  <el-option label="网络设备" value="NETWORK_DEVICE" />
                  <el-option label="安全" value="SECURITY" />
                  <el-option label="数据中心" value="DATACENTER" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="客户">
                <el-input v-model="basicForm.customerName" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="开始日期">
                <el-date-picker
                  v-model="basicForm.planStartDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="结束日期">
                <el-date-picker
                  v-model="basicForm.planEndDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述">
                <el-input v-model="basicForm.description" type="textarea" :rows="2" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>

      <!-- 2. 阶段配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><Files /></el-icon>阶段配置</span>
            <el-button
              type="primary"
              size="small"
              :icon="Check"
              :loading="savingGroup.phase"
              @click="saveConfigGroup('phase')"
            >
              保存
            </el-button>
          </div>
        </template>
        <el-form label-width="180px">
          <el-form-item v-for="item in phaseConfigKeys" :key="item.key" :label="item.label">
            <el-input
              v-model="configMap[item.key]"
              :placeholder="item.placeholder"
              style="max-width: 360px"
            />
            <span class="config-key-hint">{{ item.key }}</span>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 3. 成员配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><User /></el-icon>成员配置</span>
            <div>
              <el-button size="small" :icon="Plus" @click="addMember">添加成员</el-button>
              <el-button
                type="primary"
                size="small"
                :icon="Check"
                :loading="savingGroup.member"
                @click="saveMembers"
              >
                保存
              </el-button>
            </div>
          </div>
        </template>
        <el-table :data="members" border stripe size="small">
          <el-table-column label="用户" min-width="220">
            <template #default="{ row }">
              <UserSelect
                :model-value="row.userId || undefined"
                placeholder="搜索选择用户"
                @change="(u) => onMemberUserChange(row, u)"
              />
            </template>
          </el-table-column>
          <el-table-column label="角色" min-width="160">
            <template #default="{ row }">
              <el-select v-model="row.role" size="small" style="width: 100%">
                <el-option label="项目经理" value="PM" />
                <el-option label="技术负责人" value="TECH_LEAD" />
                <el-option label="开发" value="DEV" />
                <el-option label="测试" value="QA" />
                <el-option label="运维" value="OPS" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeMember($index)" />
            </template>
          </el-table-column>
          <template #empty>
            <el-empty :image-size="60" description="暂无成员，点击「添加成员」" />
          </template>
        </el-table>
      </el-card>

      <!-- 4. 里程碑配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><Calendar /></el-icon>里程碑配置</span>
            <div>
              <el-button size="small" :icon="Plus" @click="addMilestoneRow">添加里程碑</el-button>
              <el-button
                type="primary"
                size="small"
                :icon="Check"
                :loading="savingGroup.milestone"
                @click="saveAllMilestones"
              >
                批量保存
              </el-button>
            </div>
          </div>
        </template>
        <el-table :data="milestones" border stripe size="small">
          <el-table-column label="名称" min-width="180">
            <template #default="{ row }">
              <el-input v-model="row.name" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="类型" width="140">
            <template #default="{ row }">
              <el-select v-model="row.type" size="small" clearable style="width: 100%">
                <el-option label="阶段" value="PHASE" />
                <el-option label="交付" value="DELIVERY" />
                <el-option label="验收" value="ACCEPTANCE" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="计划日期" width="160">
            <template #default="{ row }">
              <el-date-picker
                v-model="row.plannedDate"
                type="date"
                size="small"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </template>
          </el-table-column>
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-select v-model="row.status" size="small" style="width: 100%">
                <el-option label="计划中" value="PLANNED" />
                <el-option label="已完成" value="COMPLETED" />
                <el-option label="已延期" value="DELAYED" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="描述" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.description" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row, $index }">
              <el-button link type="primary" size="small" @click="saveMilestone(row)">
                保存
              </el-button>
              <el-button link type="danger" size="small" :icon="Delete" @click="removeMilestoneRow($index)" />
            </template>
          </el-table-column>
          <template #empty>
            <el-empty :image-size="60" description="暂无里程碑" />
          </template>
        </el-table>
      </el-card>

      <!-- 5. 通知配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title">
              <el-icon><Bell /></el-icon>通知配置
              <el-tag size="small" type="warning" effect="plain" round>占位</el-tag>
            </span>
            <div>
              <el-button size="small" :icon="Plus" @click="addNotifyRule">添加规则</el-button>
              <el-button
                type="primary"
                size="small"
                :icon="Check"
                :loading="savingGroup.notify"
                @click="saveNotifyRules"
              >
                保存
              </el-button>
            </div>
          </div>
        </template>
        <el-table :data="notifyRules" border stripe size="small">
          <el-table-column label="事件" min-width="180">
            <template #default="{ row }">
              <el-select v-model="row.event" size="small" style="width: 100%">
                <el-option label="阶段退出" value="PHASE_EXIT" />
                <el-option label="审批超时" value="APPROVAL_TIMEOUT" />
                <el-option label="基线偏差" value="BASELINE_VARIANCE" />
                <el-option label="里程碑达成" value="MILESTONE_REACHED" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="通知人" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.recipients" size="small" placeholder="用户 ID 列表，逗号分隔" />
            </template>
          </el-table-column>
          <el-table-column label="渠道" width="160">
            <template #default="{ row }">
              <el-select v-model="row.channel" size="small" style="width: 100%">
                <el-option label="邮件" value="EMAIL" />
                <el-option label="短信" value="SMS" />
                <el-option label="站内信" value="IN_APP" />
                <el-option label="Webhook" value="WEBHOOK" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeNotifyRule($index)" />
            </template>
          </el-table-column>
          <template #empty>
            <el-empty :image-size="60" description="暂无通知规则" />
          </template>
        </el-table>
      </el-card>

      <!-- 6. 集成配置 -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title">
              <el-icon><Connection /></el-icon>集成配置
              <el-tag size="small" type="warning" effect="plain" round>占位</el-tag>
            </span>
            <div>
              <el-button size="small" :icon="Plus" @click="addIntegration">添加集成</el-button>
              <el-button
                type="primary"
                size="small"
                :icon="Check"
                :loading="savingGroup.integration"
                @click="saveIntegrations"
              >
                保存
              </el-button>
            </div>
          </div>
        </template>
        <el-table :data="integrations" border stripe size="small">
          <el-table-column label="系统" width="140">
            <template #default="{ row }">
              <el-select v-model="row.system" size="small" style="width: 100%">
                <el-option label="SAP" value="SAP" />
                <el-option label="D365" value="D365" />
                <el-option label="MES" value="MES" />
                <el-option label="CRM" value="CRM" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="Endpoint" min-width="240">
            <template #default="{ row }">
              <el-input v-model="row.endpoint" size="small" placeholder="https://..." />
            </template>
          </el-table-column>
          <el-table-column label="API Key" min-width="200">
            <template #default="{ row }">
              <el-input v-model="row.apiKey" size="small" type="password" show-password />
            </template>
          </el-table-column>
          <el-table-column label="启用" width="80" align="center">
            <template #default="{ row }">
              <el-switch v-model="row.enabled" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" :icon="Delete" @click="removeIntegration($index)" />
            </template>
          </el-table-column>
          <template #empty>
            <el-empty :image-size="60" description="暂无集成" />
          </template>
        </el-table>
      </el-card>

      <!-- 附：基线 / 审批 配置（继承自原 ProjectConfig，归入阶段管理类） -->
      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><Setting /></el-icon>基线管理</span>
            <el-button
              type="primary"
              size="small"
              :icon="Check"
              :loading="savingGroup.phase"
              @click="saveConfigGroup('baseline')"
            >
              保存
            </el-button>
          </div>
        </template>
        <el-form label-width="180px">
          <el-form-item v-for="item in baselineConfigKeys" :key="item.key" :label="item.label">
            <el-input
              v-model="configMap[item.key]"
              :placeholder="item.placeholder"
              style="max-width: 360px"
            />
            <span class="config-key-hint">{{ item.key }}</span>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="config-card">
        <template #header>
          <div class="card-title-row">
            <span class="card-title"><el-icon><Setting /></el-icon>审批管理</span>
            <el-button
              type="primary"
              size="small"
              :icon="Check"
              :loading="savingGroup.phase"
              @click="saveConfigGroup('approval')"
            >
              保存
            </el-button>
          </div>
        </template>
        <el-form label-width="180px">
          <el-form-item v-for="item in approvalConfigKeys" :key="item.key" :label="item.label">
            <el-input
              v-model="configMap[item.key]"
              :placeholder="item.placeholder"
              style="max-width: 360px"
            />
            <span class="config-key-hint">{{ item.key }}</span>
          </el-form-item>
        </el-form>
      </el-card>

      <EmptyState
        v-if="!project && !loading"
        title="未找到项目"
        description="可能 projectId 无效或未选择项目"
      />
    </div>
  </div>
</template>

<style scoped>
.project-config-page {
  height: 100%;
  padding: 16px 24px;
  overflow-y: auto;
  box-sizing: border-box;
}

.config-groups {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.config-card {
  border-radius: var(--pms-radius-lg);
}

.config-card :deep(.el-card__body) {
  overflow: visible;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--pms-color-text-primary);
}

.config-key-hint {
  font-size: 11px;
  color: var(--pms-color-text-placeholder);
  margin-left: 12px;
  font-family: var(--pms-font-family-mono);
}
</style>
