<script setup lang="ts">
import { computed, ref } from 'vue'

/**
 * 版本日志页面。
 *
 * <p>展示项目版本历史，按版本号分组，支持折叠。每个版本包含：
 * 版本号、发布日期、变更类型标签（新增 / 修复 / 优化 / 废弃 / 安全）。</p>
 *
 * <p>数据来源：前端硬编码（与 CHANGELOG.md 同步），避免引入额外的
 * 后端接口或 Markdown 解析依赖。</p>
 */

type ChangeType = 'added' | 'changed' | 'deprecated' | 'removed' | 'fixed' | 'security'

interface ChangeEntry {
  type: ChangeType
  description: string
}

interface VersionRelease {
  version: string
  date: string
  description?: string
  changes: ChangeEntry[]
}

const VERSIONS: VersionRelease[] = [
  {
    version: 'Unreleased',
    date: '未发布',
    description: '开发中，尚未正式发布',
    changes: [
      { type: 'added', description: '用户引导系统：首次登录自动展示 5 步功能引导' },
      { type: 'added', description: '帮助中心页面：分类浏览、搜索、Markdown 渲染' },
      { type: 'added', description: '浮动反馈按钮：右下角悬浮，提交 BUG / 建议 / 咨询' },
      { type: 'added', description: '系统状态页：后端健康、磁盘、反馈统计、近期动态' },
      { type: 'added', description: '版本日志页：按版本展示变更记录' },
      { type: 'added', description: '后端 sys_help_content / sys_feedback 表与对应 API' }
    ]
  },
  {
    version: 'v1.1.0',
    date: '2026-06-15',
    description: '低代码能力 + 集成治理增强',
    changes: [
      { type: 'added', description: '低代码引擎：表单 / 列表 / 标签页 / 关联页 4 类可视化配置' },
      { type: 'added', description: 'D365 / FP / OA 集成推送日志与重试机制' },
      { type: 'added', description: '集成健康检查面板：实时监控外部系统连通性' },
      { type: 'added', description: '消息中心与 WebSocket 实时通知' },
      { type: 'added', description: 'Punch List、RMA 返修、质保期管理模块' },
      { type: 'added', description: '风险登记册、变更管理、问题日志（项目治理三件套）' },
      { type: 'changed', description: '审计日志增强：操作日志、登录日志、异常日志、调度日志分类存储' },
      { type: 'changed', description: '定时任务支持 Cron 表达式 + 失败重试' },
      { type: 'fixed', description: '修复高并发下乐观锁冲突导致的更新失败' },
      { type: 'fixed', description: '修复 Excel 导入大数据量内存溢出问题' },
      { type: 'security', description: '升级 Spring Boot 至 3.2.5，修复已知 CVE' }
    ]
  },
  {
    version: 'v1.0.0',
    date: '2026-03-01',
    description: '初始正式版本，覆盖项目交付全生命周期',
    changes: [
      { type: 'added', description: '项目管理：项目列表、详情、交付看板' },
      { type: 'added', description: '资产管理：设备分类、型号、资产清单、状态流转' },
      { type: 'added', description: '实施管理：施工任务、服务商、结算管理' },
      { type: 'added', description: '工作流引擎：Activiti 集成、待办中心' },
      { type: 'added', description: '报表统计：交付、资产、实施效能统计' },
      { type: 'added', description: '系统管理：用户、角色、菜单、字典管理' },
      { type: 'added', description: '安全：JWT 认证、RBAC 权限、字段加密、CSRF / XSS 防护' },
      { type: 'added', description: '审计日志：操作日志、登录日志' },
      { type: 'added', description: '定时任务：Quartz 调度' },
      { type: 'added', description: '缓存管理：Redis 缓存查询与清理' }
    ]
  }
]

const expandedVersions = ref<Set<string>>(new Set(VERSIONS.map((v) => v.version)))

function toggle(version: string) {
  if (expandedVersions.value.has(version)) {
    expandedVersions.value.delete(version)
  } else {
    expandedVersions.value.add(version)
  }
}

function isExpanded(version: string): boolean {
  return expandedVersions.value.has(version)
}

function expandAll() {
  expandedVersions.value = new Set(VERSIONS.map((v) => v.version))
}

function collapseAll() {
  expandedVersions.value = new Set()
}

const changeTypeMeta: Record<ChangeType, { label: string; tagType: 'success' | 'warning' | 'info' | 'danger' }> = {
  added: { label: '新增', tagType: 'success' },
  changed: { label: '优化', tagType: 'info' },
  deprecated: { label: '废弃', tagType: 'warning' },
  removed: { label: '移除', tagType: 'danger' },
  fixed: { label: '修复', tagType: 'warning' },
  security: { label: '安全', tagType: 'danger' }
}

function groupByVersion(changes: ChangeEntry[]) {
  const groups: Record<ChangeType, ChangeEntry[]> = {
    added: [],
    changed: [],
    deprecated: [],
    removed: [],
    fixed: [],
    security: []
  }
  changes.forEach((c) => {
    groups[c.type].push(c)
  })
  return groups
}

const groupedVersions = computed(() =>
  VERSIONS.map((v) => ({
    ...v,
    grouped: groupByVersion(v.changes)
  }))
)

const changeTypeOrder: ChangeType[] = ['added', 'changed', 'fixed', 'deprecated', 'removed', 'security']
</script>

<template>
  <div class="changelog-page">
    <header class="page-header">
      <h2 class="page-title">版本日志</h2>
      <div class="page-actions">
        <el-button size="small" @click="expandAll">全部展开</el-button>
        <el-button size="small" @click="collapseAll">全部折叠</el-button>
      </div>
    </header>

    <div class="changelog-list">
      <el-card
        v-for="ver in groupedVersions"
        :key="ver.version"
        class="version-card"
      >
        <template #header>
          <div class="version-header" @click="toggle(ver.version)">
            <div class="version-header__left">
              <el-icon class="version-header__toggle">
                <ArrowDown v-if="isExpanded(ver.version)" />
                <ArrowRight v-else />
              </el-icon>
              <span class="version-header__number">{{ ver.version }}</span>
              <el-tag size="small" type="info">{{ ver.date }}</el-tag>
              <span v-if="ver.description" class="version-header__desc">
                {{ ver.description }}
              </span>
            </div>
            <span class="version-header__count">{{ ver.changes.length }} 项变更</span>
          </div>
        </template>

        <div v-show="isExpanded(ver.version)" class="version-body">
          <div
            v-for="type in changeTypeOrder"
            :key="type"
            class="change-group"
          >
            <template v-if="ver.grouped[type].length > 0">
              <div class="change-group__title">
                <el-tag :type="changeTypeMeta[type].tagType" size="small">
                  {{ changeTypeMeta[type].label }}
                </el-tag>
              </div>
              <ul class="change-group__list">
                <li v-for="(item, idx) in ver.grouped[type]" :key="idx" class="change-item">
                  {{ item.description }}
                </li>
              </ul>
            </template>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.changelog-page {
  padding: 16px 24px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2d3d;
}

.page-actions {
  display: flex;
  gap: 8px;
}

.changelog-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.version-card {
  overflow: hidden;
}

.version-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  user-select: none;
}

.version-header__left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.version-header__toggle {
  color: #909399;
  flex-shrink: 0;
}

.version-header__number {
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.version-header__desc {
  font-size: 13px;
  color: #909399;
  margin-left: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.version-header__count {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}

.version-body {
  padding: 4px 0;
}

.change-group {
  margin-bottom: 16px;
}

.change-group:last-child {
  margin-bottom: 0;
}

.change-group__title {
  margin-bottom: 8px;
}

.change-group__list {
  list-style: disc;
  margin: 0;
  padding-left: 32px;
}

.change-item {
  font-size: 14px;
  color: #303133;
  line-height: 1.8;
}
</style>
