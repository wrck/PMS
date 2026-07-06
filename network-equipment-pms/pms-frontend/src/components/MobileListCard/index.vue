<script lang="ts">
// 类型导出（普通 script 块支持 export）
export interface MobileColumn<T = Record<string, unknown>> {
  /** 字段名 */
  prop: string
  /** 显示标签 */
  label: string
  /** 自定义格式化函数，返回字符串 */
  formatter?: (row: T, value: unknown) => string
  /** 渲染类型：text 文本（默认），tag 标签 */
  render?: 'text' | 'tag'
  /** render='tag' 时的标签类型，可基于行数据动态返回 */
  tagType?: (row: T) => 'primary' | 'success' | 'warning' | 'danger' | 'info'
  /** 该列是否作为副标题显示在标题下方 */
  subtitle?: boolean
}

export interface MobileOperation<T = Record<string, unknown>> {
  /** 按钮文本 */
  label: string
  /** 按钮类型 */
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  /** 是否显示该操作，默认始终显示 */
  show?: (row: T) => boolean
  /** 点击回调 */
  onClick: (row: T) => void
}
</script>

<script setup lang="ts">
// =============================================================================
// MobileListCard 通用移动端列表卡片组件
// -----------------------------------------------------------------------------
// 用于在移动端（md-down）替代 el-table，将每行数据渲染为一张卡片。
// 配合 .mobile-card-list 容器使用：移动端显示卡片，桌面端显示 el-table。
//
// 用法示例：
//   <MobileListCard
//     :data="tableData"
//     :columns="columns"
//     :operations="operations"
//     title-prop="name"
//     empty-text="暂无数据"
//   />
// =============================================================================

import { computed } from 'vue'
import { ElEmpty, ElTag, ElButton, ElIcon } from 'element-plus'

/**
 * 内部行类型 —— 接受任意业务实体（Project / Asset 等），
 * 通过 `as Record<string, unknown>` 完成窄化以便按字段名取值。
 */
type Row = Record<string, unknown>

const props = withDefaults(
  defineProps<{
    /** 数据数组（接受任意业务实体数组） */
    data: unknown[]
    /**
     * 列配置。
     * 行类型参数使用 `any` 是组件边界的妥协：消费端持有 `MobileColumn<Asset>` 等具体实体类型，
     * 由于 formatter/tagType 的入参为逆变位置，`MobileColumn<Asset>` 无法协变赋值给
     * `MobileColumn<Record<string, unknown>>`。组件内部统一通过 asRow() 窄化为 Record 取值，
     * 因此在此边界放宽类型以避免每个调用处都写 `as unknown as` 断言。
     */
    columns: MobileColumn<any>[]
    /** 操作按钮配置（同上，行类型使用 any 作为边界妥协） */
    operations?: MobileOperation<any>[]
    /** 作为卡片标题的字段名（默认取 columns[0].prop） */
    titleProp?: string
    /** 标题前缀图标名（Element Plus 图标组件名，可选） */
    titleIcon?: string
    /** 空数据提示文案 */
    emptyText?: string
  }>(),
  {
    operations: () => [],
    titleProp: '',
    titleIcon: '',
    emptyText: '暂无数据'
  }
)

// 实际使用的标题字段：优先用 titleProp，否则取第一列
const resolvedTitleProp = computed(() => props.titleProp || props.columns[0]?.prop || '')

// 副标题列
const subtitleColumn = computed(() => props.columns.find((c) => c.subtitle))

// 主体列（排除标题列与副标题列）
const bodyColumns = computed(() =>
  props.columns.filter((c) => c.prop !== resolvedTitleProp.value && !c.subtitle)
)

// 将 unknown 行窄化为可索引访问的 Record
function asRow(row: unknown): Row {
  return (row ?? {}) as Row
}

// 取单元格显示文本
function cellText(row: unknown, col: MobileColumn): string {
  const r = asRow(row)
  const raw = r[col.prop]
  if (col.formatter) return col.formatter(asRow(row), raw)
  if (raw === undefined || raw === null || raw === '') return '-'
  return String(raw)
}

// 取标题文本
function titleText(row: unknown): string {
  const raw = asRow(row)[resolvedTitleProp.value]
  if (raw === undefined || raw === null || raw === '') return '未命名'
  return String(raw)
}

// 取副标题文本
function subtitleText(row: unknown): string {
  if (!subtitleColumn.value) return ''
  return cellText(row, subtitleColumn.value)
}

// 取标签类型
function resolveTagType(row: unknown, col: MobileColumn): 'primary' | 'success' | 'warning' | 'danger' | 'info' {
  return col.tagType ? col.tagType(asRow(row)) : 'info'
}

// 取该行可见的操作
function visibleOperations(row: unknown): MobileOperation[] {
  return props.operations.filter((op) => !op.show || op.show(asRow(row)))
}
</script>

<template>
  <div class="mobile-list-card">
    <el-empty v-if="!data || data.length === 0" :description="emptyText" />
    <template v-else>
      <div v-for="(row, idx) in data" :key="(asRow(row).id as string | number | undefined) ?? idx" class="mlc-item">
        <!-- 顶部：标题 + 索引 -->
        <div class="mlc-header">
          <div class="mlc-title-wrap">
            <el-icon v-if="titleIcon" class="mlc-title-icon">
              <component :is="titleIcon" />
            </el-icon>
            <span class="mlc-title">{{ titleText(row) }}</span>
          </div>
          <span class="mlc-index">#{{ idx + 1 }}</span>
        </div>

        <!-- 副标题 -->
        <div v-if="subtitleColumn" class="mlc-subtitle">{{ subtitleText(row) }}</div>

        <!-- 主体：label / value 网格 -->
        <div v-if="bodyColumns.length > 0" class="mlc-body">
          <div v-for="col in bodyColumns" :key="col.prop" class="mlc-field">
            <span class="mlc-label">{{ col.label }}</span>
            <span class="mlc-value">
              <el-tag
                v-if="col.render === 'tag'"
                :type="resolveTagType(row, col)"
                size="small"
                effect="light"
              >
                {{ cellText(row, col) }}
              </el-tag>
              <template v-else>{{ cellText(row, col) }}</template>
            </span>
          </div>
        </div>

        <!-- 底部：操作按钮 -->
        <div v-if="visibleOperations(row).length > 0" class="mlc-actions">
          <el-button
            v-for="(op, i) in visibleOperations(row)"
            :key="i"
            link
            :type="op.type || 'primary'"
            size="small"
            @click="op.onClick(asRow(row))"
          >
            {{ op.label }}
          </el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.mobile-list-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mlc-item {
  background: var(--pms-color-bg-card, #fff);
  border: 1px solid var(--pms-color-border-light, #e4e7ed);
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.mlc-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.mlc-title-wrap {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  flex: 1;
}

.mlc-title-icon {
  color: var(--pms-color-primary, #409eff);
  flex-shrink: 0;
}

.mlc-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--pms-color-text-primary, #1f2d3d);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.mlc-index {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  flex-shrink: 0;
}

.mlc-subtitle {
  font-size: 13px;
  color: var(--pms-color-text-regular, #606266);
}

.mlc-body {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px 12px;
}

.mlc-field {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.mlc-label {
  font-size: 12px;
  color: var(--pms-color-text-secondary, #909399);
  line-height: 1.4;
}

.mlc-value {
  font-size: 13px;
  color: var(--pms-color-text-primary, #1f2d3d);
  line-height: 1.5;
  word-break: break-all;
}

.mlc-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 12px;
  padding-top: 4px;
  border-top: 1px dashed var(--pms-color-border-light, #e4e7ed);
}
</style>
