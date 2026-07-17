<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProject, type Project } from '@/api/project'
import { getProjectConfigs, updateProjectConfigs } from '@/api/project-config'

defineOptions({ name: 'ProjectConfig' })

const route = useRoute()
const router = useRouter()

const projectId = computed(() => Number(route.params.id))

const loading = ref(false)
const saving = ref(false)
const project = ref<Project | null>(null)
/** 配置项行（key/value/description） */
interface ConfigRow {
  key: string
  value: string
  /** 来自系统默认描述表，未知则为空 */
  description?: string
  /** 是否新增行（用于标识删除按钮可见性） */
  isNew?: boolean
}
const rows = ref<ConfigRow[]>([])

/**
 * 已知系统默认配置项的描述（来自 V66 迁移脚本初始化数据）。
 * 后端 API 仅返回 key-value，描述由前端基于已知 defaults 表补充展示。
 */
const knownDescriptions: Record<string, string> = {
  'baseline.variance.days.threshold': '基线偏差天数阈值',
  'baseline.variance.percent.threshold': '基线偏差百分比阈值',
  'approval.timeout.hours': '审批超时小时数',
  'approval.escalate.hours': '审批升级小时数',
  'approval.reminder.hours': '审批提醒小时数',
  'approval.timeout.action': '超时动作：ESCALATE/AUTO_APPROVE/AUTO_REJECT',
  'task.rollup.weight.field': '任务汇总权重字段（PLANNED_HOURS/TASK_WEIGHT）',
  'phase.exit.check.approval': '阶段退出是否强制审批',
  'approval.max.rounds': '审批最大轮次'
}

async function loadProject() {
  try {
    project.value = await getProject(projectId.value)
  } catch {
    /* handled by interceptor */
  }
}

async function loadConfigs() {
  loading.value = true
  try {
    const configs = await getProjectConfigs(projectId.value)
    rows.value = Object.entries(configs).map(([key, value]) => ({
      key,
      value,
      description: knownDescriptions[key] ?? '',
      isNew: false
    }))
    if (rows.value.length === 0) {
      // 没有项目级覆盖时，预填系统默认配置项作为编辑起点
      rows.value = Object.entries(knownDescriptions).map(([key, desc]) => ({
        key,
        value: '',
        description: desc,
        isNew: true
      }))
    }
  } catch {
    rows.value = []
  } finally {
    loading.value = false
  }
}

function handleAddRow() {
  rows.value.push({ key: '', value: '', description: '', isNew: true })
}

function handleDeleteRow(index: number) {
  rows.value.splice(index, 1)
}

function handleReset() {
  loadConfigs()
}

async function handleSave() {
  // 校验：key 不能为空、不能重复
  const seen = new Set<string>()
  for (const row of rows.value) {
    const k = row.key.trim()
    if (!k) {
      ElMessage.warning('存在配置项 key 为空的行，请补全或删除')
      return
    }
    if (seen.has(k)) {
      ElMessage.warning(`配置项 key 重复：${k}`)
      return
    }
    seen.add(k)
  }

  ElMessageBox.confirm('确定保存项目配置吗？', '保存确认', { type: 'warning' })
    .then(async () => {
      saving.value = true
      try {
        const configs: Record<string, string> = {}
        rows.value.forEach((r) => {
          configs[r.key.trim()] = r.value
        })
        await updateProjectConfigs(projectId.value, configs)
        ElMessage.success('保存成功')
        await loadConfigs()
      } catch {
        /* handled by interceptor */
      } finally {
        saving.value = false
      }
    })
    .catch(() => {
      /* cancelled */
    })
}

onMounted(async () => {
  await loadProject()
  await loadConfigs()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="header-row">
          <el-button :icon="'ArrowLeft'" link @click="router.back()">返回</el-button>
          <span class="page-title">项目配置</span>
          <span v-if="project" class="project-name">{{ project.name }}</span>
        </div>
      </template>

      <el-alert
        type="info"
        :closable="false"
        show-icon
        class="info-alert"
        title="项目级配置会覆盖系统默认配置"
        description="留空的配置项将被忽略；保存后立即生效（审批超时、基线偏差阈值、任务汇总权重等）。"
      />

      <div class="toolbar">
        <el-button type="primary" :icon="'Plus'" @click="handleAddRow">新增配置项</el-button>
        <el-button :icon="'Refresh'" @click="handleReset">重新加载</el-button>
        <span class="toolbar-tip">共 {{ rows.length }} 项配置</span>
      </div>

      <el-table v-loading="loading" :data="rows" border stripe row-key="key">
        <el-table-column type="index" label="#" width="50" align="center" />
        <el-table-column label="配置 Key" min-width="260">
          <template #default="{ row }">
            <el-input
              v-model="row.key"
              :placeholder="row.isNew ? '请输入配置 key' : ''"
              :disabled="!row.isNew"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column label="配置值" min-width="200">
          <template #default="{ row }">
            <el-input v-model="row.value" placeholder="请输入配置值" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="说明" min-width="280" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.description">{{ row.description }}</span>
            <span v-else class="text-muted">（自定义配置）</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ $index }">
            <el-button link type="danger" size="small" @click="handleDeleteRow($index)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无配置项，点击「新增配置项」" />
        </template>
      </el-table>

      <div class="footer-actions">
        <el-button @click="router.back()">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.header-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.project-name {
  font-size: 13px;
  color: #909399;
}
.info-alert {
  margin-bottom: 12px;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.toolbar-tip {
  font-size: 12px;
  color: #909399;
}
.text-muted {
  color: #c0c4cc;
  font-style: italic;
}
.footer-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
