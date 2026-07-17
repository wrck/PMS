<script setup lang="ts">
import { computed } from 'vue'
import type { ApprovalHistory } from '@/api/approval-center'

/**
 * 审批时间轴组件（Story 6）。
 *
 * <p>按轮次（round）分组渲染审批历史，每组内按操作时间升序展示。
 * 退回后重新提交的审批会在新轮次下展示，体现多轮次追溯。</p>
 *
 * <p>关联设计文档：§3.5 Story 6 验收 2（行 472-484）、§5.7（行 1123-1146）。</p>
 */
const props = defineProps<{
  /** 审批历史列表（含所有轮次） */
  history: ApprovalHistory[]
}>()

interface RoundGroup {
  round: number
  items: ApprovalHistory[]
}

/** 按 round 分组并按 operatedAt 升序。 */
const groupedRounds = computed<RoundGroup[]>(() => {
  const map = new Map<number, ApprovalHistory[]>()
  for (const h of props.history) {
    const r = h.round || 1
    if (!map.has(r)) map.set(r, [])
    map.get(r)!.push(h)
  }
  const groups: RoundGroup[] = []
  for (const [round, items] of map.entries()) {
    items.sort((a, b) => {
      const ta = a.operatedAt ? new Date(a.operatedAt).getTime() : 0
      const tb = b.operatedAt ? new Date(b.operatedAt).getTime() : 0
      return ta - tb
    })
    groups.push({ round, items })
  }
  groups.sort((a, b) => a.round - b.round)
  return groups
})

function actionTagType(action?: string) {
  switch (action) {
    case 'APPROVE':
      return 'success'
    case 'REJECT':
      return 'danger'
    case 'WITHDRAW':
      return 'info'
    case 'RESUBMIT':
      return 'primary'
    case 'SUBMIT':
      return 'warning'
    case 'TIMEOUT':
      return 'danger'
    case 'ESCALATE':
      return 'danger'
    default:
      return 'info'
  }
}

function timelineItemType(action?: string) {
  switch (action) {
    case 'APPROVE':
      return 'success'
    case 'REJECT':
    case 'TIMEOUT':
    case 'ESCALATE':
      return 'danger'
    case 'RESUBMIT':
      return 'primary'
    case 'WITHDRAW':
      return 'info'
    case 'SUBMIT':
    default:
      return 'primary'
  }
}

function actionLabel(action?: string) {
  const map: Record<string, string> = {
    SUBMIT: '提交',
    APPROVE: '通过',
    REJECT: '退回',
    WITHDRAW: '撤回',
    RESUBMIT: '重新提交',
    ESCALATE: '升级',
    TIMEOUT: '超时'
  }
  return map[action || ''] || action || '-'
}
</script>

<template>
  <div class="approval-timeline">
    <template v-if="groupedRounds.length === 0">
      <el-empty description="暂无审批历史" />
    </template>

    <div v-for="group in groupedRounds" :key="group.round" class="round-block">
      <div class="round-header">
        <el-tag type="primary" effect="dark" size="default">第 {{ group.round }} 轮</el-tag>
        <span class="round-count">{{ group.items.length }} 条记录</span>
      </div>
      <el-timeline>
        <el-timeline-item
          v-for="(item, idx) in group.items"
          :key="item.id || idx"
          :timestamp="item.operatedAt || ''"
          placement="top"
          :type="timelineItemType(item.action)"
        >
          <div class="timeline-item">
            <span class="timeline-node">{{ item.nodeName }}</span>
            <el-tag :type="actionTagType(item.action)" size="small" effect="plain">
              {{ actionLabel(item.action) }}
            </el-tag>
            <span v-if="item.operatorName" class="timeline-operator">
              操作人：{{ item.operatorName }}
            </span>
          </div>
          <div v-if="item.opinion" class="timeline-opinion">{{ item.opinion }}</div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<style scoped>
.approval-timeline {
  padding: 8px 0;
}
.round-block {
  margin-bottom: 20px;
}
.round-block:last-child {
  margin-bottom: 0;
}
.round-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  padding: 6px 12px;
  background: var(--el-fill-color-light);
  border-radius: 4px;
}
.round-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.timeline-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}
.timeline-node {
  font-weight: 600;
}
.timeline-operator {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
.timeline-opinion {
  margin-top: 6px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-lighter);
  padding: 6px 10px;
  border-radius: 4px;
}
</style>
