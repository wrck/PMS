<script setup lang="ts">
import { computed } from 'vue'
import type { MaskedField } from '@/api/approval-center'

/**
 * 敏感字段脱敏展示组件（Story 6）。
 *
 * <p>在审批详情中展示业务字段值，若该字段被标记为 MASKED，则在值后显示 ⓘ 提示图标，
 * 鼠标悬浮展示脱敏规则说明。HIDDEN 字段不会出现在业务数据中（后端已过滤）。</p>
 *
 * <p>关联设计文档：§3.5 Story 6 验收 1（行 444-470）、§5.7（行 1094-1121）。</p>
 */
const props = defineProps<{
  /** 字段名 */
  fieldName: string
  /** 字段值（已脱敏） */
  value: unknown
  /** 脱敏字段元数据列表 */
  maskedFields?: MaskedField[]
}>()

/** 查找当前字段的脱敏元数据。 */
const maskedMeta = computed<MaskedField | undefined>(() => {
  return props.maskedFields?.find((m) => m.fieldName === props.fieldName)
})

/** 是否为脱敏字段。 */
const isMasked = computed(() => maskedMeta.value?.permission === 'MASKED')

/** 是否为隐藏字段（理论上后端已过滤，此处兜底）。 */
const isHidden = computed(() => maskedMeta.value?.permission === 'HIDDEN')

const maskPatternLabel = computed(() => {
  switch (maskedMeta.value?.maskPattern) {
    case 'phone-mask':
      return '手机号脱敏（保留前 3 后 4）'
    case 'amount-mask':
      return '金额脱敏（保留前 2 位整数与小数）'
    case 'email-mask':
      return '邮箱脱敏（本地部分保留首字符）'
    case 'custom':
      return '自定义正则脱敏'
    default:
      return '已脱敏'
  }
})

const tooltipContent = computed(() => {
  if (!maskedMeta.value) return ''
  return `该字段已脱敏：${maskPatternLabel.value}`
})

const displayValue = computed(() => {
  if (isHidden.value) return '（已隐藏）'
  if (props.value === null || props.value === undefined || props.value === '') return '-'
  return String(props.value)
})
</script>

<template>
  <span class="sensitive-field">
    <span class="field-value" :class="{ masked: isMasked, hidden: isHidden }">
      {{ displayValue }}
    </span>
    <el-tooltip v-if="isMasked" :content="tooltipContent" placement="top">
      <el-icon class="info-icon"><InfoFilled /></el-icon>
    </el-tooltip>
  </span>
</template>

<style scoped>
.sensitive-field {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.field-value {
  font-variant-numeric: tabular-nums;
}
.field-value.masked {
  color: var(--el-color-warning);
  font-style: italic;
}
.field-value.hidden {
  color: var(--el-text-color-placeholder);
}
.info-icon {
  color: var(--el-color-info);
  font-size: 14px;
  cursor: help;
}
</style>
