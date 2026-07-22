<script setup lang="ts">
// =============================================================================
// DeliverableRefEntitySelector - 交付件引用实体选择器
// -----------------------------------------------------------------------------
// 用于 ENTITY_REF 类型交付件选择被引用的实体。
// 功能：
//   1. 选择引用实体类型（TASK/ASSET/PHASE/PROJECT/DELIVERABLE/REPORT）
//   2. 从已有实体列表中选择具体实体（可搜索）
//   3. 提供"新建实体"入口，跳转到对应模块创建页面
// 双向绑定：v-model:refEntityType + v-model:refEntityId
// =============================================================================
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  listReferencedEntities,
  loadDeliverableRefEntityTypes,
  translateRefEntityType,
  type RefEntityOption,
  type SysDictItem
} from '@/api/deliverable'
import { CirclePlus } from '@element-plus/icons-vue'

defineOptions({ name: 'DeliverableRefEntitySelector' })

interface Props {
  /** 引用实体类型（双向绑定） */
  refEntityType?: string
  /** 引用实体ID（双向绑定） */
  refEntityId?: number
  /** 项目ID（用于过滤可选实体范围） */
  projectId?: number
  /** 是否禁用 */
  disabled?: boolean
  /** 尺寸 */
  size?: 'small' | 'default' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  refEntityType: '',
  refEntityId: undefined,
  projectId: undefined,
  disabled: false,
  size: 'default'
})

const emit = defineEmits<{
  'update:refEntityType': [value: string]
  'update:refEntityId': [value: number | undefined]
}>()

// ============ 字典：引用实体类型 ============
const refEntityTypeOptions = ref<SysDictItem[]>([])
const entityOptions = ref<RefEntityOption[]>([])
const loadingEntities = ref(false)

// ============ 新建实体的跳转路径映射 ============
const createRoutes: Record<string, string> = {
  TASK: '/implementation/task',
  ASSET: '/asset',
  PHASE: '/project/phase',
  PROJECT: '/project',
  DELIVERABLE: '/deliverable/lifecycle',
  REPORT: '/report'
}

// ============ 实体类型变更 ============
function onTypeChange(val: string) {
  emit('update:refEntityType', val)
  // 类型变更时清空已选实体
  emit('update:refEntityId', undefined)
  entityOptions.value = []
  if (val) {
    loadEntities(val)
  }
}

// ============ 实体选择变更 ============
function onEntityChange(val: number | undefined) {
  emit('update:refEntityId', val)
}

// ============ 加载可选实体列表 ============
async function loadEntities(type: string) {
  loadingEntities.value = true
  try {
    entityOptions.value = (await listReferencedEntities(type, props.projectId)) ?? []
  } catch {
    entityOptions.value = []
  } finally {
    loadingEntities.value = false
  }
}

// ============ 新建实体（跳转到对应模块） ============
function handleCreateEntity() {
  const type = props.refEntityType
  if (!type) {
    ElMessage.warning('请先选择引用实体类型')
    return
  }
  const route = createRoutes[type]
  if (!route) {
    ElMessage.info('该实体类型暂不支持快速创建，请到对应模块手动创建')
    return
  }
  // 在新标签页打开对应模块
  const url = props.projectId ? `${route}?projectId=${props.projectId}` : route
  window.open(url, '_blank')
  ElMessage.info('已在新标签页打开创建页面，创建后请回来选择新实体并刷新列表')
}

// ============ 刷新实体列表 ============
function refreshEntities() {
  if (props.refEntityType) {
    loadEntities(props.refEntityType)
  }
}

// ============ 选中实体的显示名称 ============
const selectedEntityName = computed(() => {
  if (!props.refEntityId) return ''
  const item = entityOptions.value.find((e) => e.id === props.refEntityId)
  return item?.name ?? `#${props.refEntityId}`
})

// ============ 初始化 ============
watch(
  () => props.refEntityType,
  (val) => {
    if (val) loadEntities(val)
  },
  { immediate: false }
)

// 组件挂载时加载字典和已有实体
async function init() {
  try {
    refEntityTypeOptions.value = await loadDeliverableRefEntityTypes()
  } catch {
    refEntityTypeOptions.value = []
  }
  if (props.refEntityType) {
    await loadEntities(props.refEntityType)
  }
}

init()
</script>

<template>
  <div class="ref-entity-selector">
    <!-- 引用实体类型选择 -->
    <el-select
      :model-value="refEntityType"
      :size="size"
      :disabled="disabled"
      placeholder="选择实体类型"
      style="width: 120px"
      @change="onTypeChange"
    >
      <el-option
        v-for="item in refEntityTypeOptions"
        :key="item.itemValue"
        :label="item.itemText"
        :value="item.itemValue"
      />
    </el-select>

    <!-- 引用实体选择 -->
    <el-select
      :model-value="refEntityId"
      :size="size"
      :disabled="disabled || !refEntityType"
      :loading="loadingEntities"
      :placeholder="refEntityType ? '选择或搜索实体' : '请先选择实体类型'"
      filterable
      clearable
      style="flex: 1; min-width: 0"
      @change="onEntityChange"
    >
      <el-option
        v-for="item in entityOptions"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>

    <!-- 操作按钮 -->
    <el-button
      v-if="refEntityType && createRoutes[refEntityType]"
      :size="size"
      :icon="CirclePlus"
      :disabled="disabled"
      @click="handleCreateEntity"
    >新建</el-button>
    <el-button
      v-if="refEntityType"
      :size="size"
      :disabled="disabled"
      @click="refreshEntities"
    >刷新</el-button>
  </div>
</template>

<style scoped>
.ref-entity-selector {
  display: flex;
  gap: 8px;
  align-items: center;
  width: 100%;
}
</style>
