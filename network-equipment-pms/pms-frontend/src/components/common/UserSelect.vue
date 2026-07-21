<script setup lang="ts">
// =============================================================================
// UserSelect - 用户选择查找字段（主档数据选择器）
// -----------------------------------------------------------------------------
// 所有需要选择「用户」这类主档数据的表单字段，必须使用本组件，禁止直接
// 用 el-input 输入用户名或 el-input-number 输入用户 ID。
//
// 功能：
//   - v-model 绑定用户 ID（number | undefined）
//   - 远程搜索：调用 /api/system/user/search 按关键词匹配用户
//   - 初始回填：传入已存在的 ID 时自动加载用户名显示
//   - change 事件：选中后回传完整用户对象，便于同步 name/nickname 等冗余字段
//
// 用法示例：
//   <UserSelect v-model="form.engineerId" @change="onEngineerChange" />
//   function onEngineerChange(u: MentionUser | null) {
//     form.engineerName = u?.realName ?? u?.username ?? ''
//   }
// =============================================================================
import { ref, watch, computed } from 'vue'
import { searchUsers, getUserById, type MentionUser } from '@/api/system'

const props = withDefaults(
  defineProps<{
    /** 当前选中的用户 ID */
    modelValue?: number | null
    /** 占位提示 */
    placeholder?: string
    /** 是否禁用 */
    disabled?: boolean
    /** 是否允许清空 */
    clearable?: boolean
    /** 选择框宽度 */
    width?: string
  }>(),
  {
    modelValue: undefined,
    placeholder: '请输入关键字搜索用户',
    disabled: false,
    clearable: true,
    width: '100%'
  }
)

const emit = defineEmits<{
  (e: 'update:modelValue', v: number | undefined): void
  (e: 'change', user: MentionUser | null): void
}>()

/** 远程搜索结果列表 */
const options = ref<MentionUser[]>([])
/** 当前下拉框中显示的标签（用于回显已选用户名） */
const currentLabel = ref<string>('')
/** 是否正在加载（搜索 / 回填） */
const loading = ref(false)

const styleWidth = computed(() => ({ width: props.width }))

/** 用户显示名：优先 realName，其次 username */
function labelOf(u: MentionUser | undefined | null): string {
  if (!u) return ''
  return u.realName || u.username || `#${u.id}`
}

/** 远程搜索回调 */
async function handleSearch(keyword: string) {
  if (!keyword) {
    options.value = []
    return
  }
  loading.value = true
  try {
    const list = await searchUsers(keyword, 20)
    options.value = list ?? []
  } catch {
    options.value = []
  } finally {
    loading.value = false
  }
}

/** 选中变化：emit id + 完整用户对象 */
function handleChange(val: number | undefined) {
  emit('update:modelValue', val)
  const matched = options.value.find((u) => u.id === val) ?? null
  emit('change', matched)
  if (matched) currentLabel.value = labelOf(matched)
}

/** 清空时同步事件 */
function handleClear() {
  emit('update:modelValue', undefined)
  emit('change', null)
  currentLabel.value = ''
}

/** 初始值回填：当父组件传入 ID 但还没有 label 时，按 ID 拉取用户信息显示 */
async function loadInitial(id: number) {
  // 先在已加载列表中找
  const found = options.value.find((u) => u.id === id)
  if (found) {
    currentLabel.value = labelOf(found)
    return
  }
  loading.value = true
  try {
    const u = await getUserById(id)
    if (u) {
      // 转换为 MentionUser 形态
      const mention: MentionUser = {
        id: u.id!,
        username: u.username,
        realName: u.nickname
      }
      options.value = [mention]
      currentLabel.value = labelOf(mention)
    }
  } catch {
    currentLabel.value = `#${id}`
  } finally {
    loading.value = false
  }
}

// 监听外部 modelValue 变化（如表单回填），自动同步 label
watch(
  () => props.modelValue,
  (val) => {
    if (val == null) {
      currentLabel.value = ''
      return
    }
    // 已有 label 时不重复请求
    const found = options.value.find((u) => u.id === val)
    if (found) {
      currentLabel.value = labelOf(found)
    } else {
      loadInitial(val)
    }
  },
  { immediate: true }
)
</script>

<template>
  <el-select
    :model-value="modelValue"
    :placeholder="placeholder"
    :disabled="disabled"
    :clearable="clearable"
    filterable
    remote
    :remote-method="handleSearch"
    :loading="loading"
    :style="styleWidth"
    @change="handleChange"
    @clear="handleClear"
  >
    <el-option
      v-for="u in options"
      :key="u.id"
      :label="labelOf(u)"
      :value="u.id"
    >
      <span class="user-opt-name">{{ labelOf(u) }}</span>
      <span class="user-opt-id">@{{ u.username }}</span>
    </el-option>
    <!-- 已选中但 options 为空时，仍能显示当前 label -->
    <template v-if="!options.length && modelValue">
      <el-option :label="currentLabel" :value="modelValue" />
    </template>
  </el-select>
</template>

<style scoped>
.user-opt-name {
  margin-right: 8px;
}
.user-opt-id {
  color: var(--pms-color-text-secondary, #909399);
  font-size: 12px;
}
</style>
