<!-- src/views/lowcode/version-history/PromotionPipeline.vue -->
<script setup lang="ts">
/**
 * 晋升管道图（借鉴 Appsmith Git / OutSystems LifeTime 环境晋升可视化）。
 *
 * <p>横向三列布局：DEV → TEST → PROD，每列展示对应环境的版本卡片，
 * 列间用 CSS 箭头表示晋升方向。点击版本卡片可触发 Diff 对比或回滚。</p>
 *
 * <p>纯 CSS flexbox + CSS 三角箭头实现，不引入图形库。</p>
 */
import { computed } from 'vue'
import type { LowCodeConfigVersion } from '@/api/lowcode-version'
import type { EpTagType } from '@/types'

interface Props {
  /** 全部版本列表（含 DEV/TEST/PROD 三个环境） */
  versions: LowCodeConfigVersion[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'diff', version: LowCodeConfigVersion): void
  (e: 'rollback', version: LowCodeConfigVersion): void
}>()

/** 环境列定义：编码 / 显示名 / 标签颜色 */
const ENV_COLUMNS: Array<{ code: string; name: string; tag: EpTagType }> = [
  { code: 'DEV', name: '开发环境', tag: 'info' },
  { code: 'TEST', name: '测试环境', tag: 'warning' },
  { code: 'PROD', name: '生产环境', tag: 'danger' }
]

/** 按环境分组，每组内按版本号倒序（最新在上） */
const grouped = computed(() => {
  const map: Record<string, LowCodeConfigVersion[]> = { DEV: [], TEST: [], PROD: [] }
  for (const v of props.versions) {
    const env = map[v.environment] ? v.environment : 'DEV'
    map[env].push(v)
  }
  for (const env of Object.keys(map)) {
    map[env].sort((a, b) => b.version - a.version)
  }
  return map
})

function fmtTime(t?: string): string {
  return t ? t.replace('T', ' ').slice(0, 16) : '-'
}

function statusTag(status: string): EpTagType {
  return status === 'ACTIVE' ? 'success' : 'info'
}
</script>

<template>
  <div class="promotion-pipeline">
    <div v-for="(col, idx) in ENV_COLUMNS" :key="col.code" class="pipeline-col">
      <!-- 列头：环境名 + 箭头 -->
      <div class="pipeline-header">
        <el-tag :type="col.tag" effect="dark" size="large">{{ col.code }}</el-tag>
        <span class="pipeline-col-name">{{ col.name }}</span>
        <!-- 列间晋升箭头（最后一列不显示） -->
        <span v-if="idx < ENV_COLUMNS.length - 1" class="pipeline-arrow" aria-hidden="true">
          <span class="arrow-line"></span>
          <span class="arrow-head"></span>
        </span>
      </div>

      <!-- 版本卡片列表 -->
      <div class="pipeline-cards">
        <el-empty
          v-if="grouped[col.code].length === 0"
          description="暂无版本"
          :image-size="50"
        />
        <div
          v-for="v in grouped[col.code]"
          :key="`${col.code}-${v.version}`"
          class="version-card"
        >
          <div class="version-card-header">
            <span class="version-no">v{{ v.version }}</span>
            <el-tag size="small" :type="statusTag(v.status)">{{ v.status }}</el-tag>
          </div>
          <div class="version-card-body">
            <div class="version-card-row" :title="v.changeLog">
              {{ v.changeLog || '（无变更说明）' }}
            </div>
            <div class="version-card-meta">
              <span>{{ v.createBy || '-' }}</span>
              <span>{{ fmtTime(v.createTime) }}</span>
            </div>
          </div>
          <div class="version-card-actions">
            <el-button link type="primary" size="small" @click="emit('diff', v)">
              对比
            </el-button>
            <el-button link type="warning" size="small" @click="emit('rollback', v)">
              回滚
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.promotion-pipeline {
  display: flex;
  align-items: stretch;
  gap: 0;
  overflow-x: auto;
  padding: 8px 0;
}

.pipeline-col {
  flex: 1 1 0;
  min-width: 240px;
  display: flex;
  flex-direction: column;
  padding: 0 12px;
}

.pipeline-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 2px solid var(--el-border-color-light);
}

.pipeline-col-name {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

/* 列间晋升箭头：横线 + 三角头 */
.pipeline-arrow {
  position: absolute;
  right: -12px;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  pointer-events: none;
}

.arrow-line {
  width: 18px;
  height: 0;
  border-top: 2px dashed var(--el-color-primary);
}

.arrow-head {
  width: 0;
  height: 0;
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
  border-left: 8px solid var(--el-color-primary);
}

.pipeline-cards {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.version-card {
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  background: var(--el-bg-color);
  padding: 10px 12px;
  transition: box-shadow 0.2s, transform 0.2s;
  cursor: default;
}

.version-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.version-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.version-no {
  font-weight: 600;
  color: var(--el-color-primary);
}

.version-card-body {
  font-size: 13px;
}

.version-card-row {
  color: var(--el-text-color-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.version-card-meta {
  display: flex;
  justify-content: space-between;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.version-card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  margin-top: 4px;
  border-top: 1px dashed var(--el-border-color-lighter);
  padding-top: 4px;
}
</style>
