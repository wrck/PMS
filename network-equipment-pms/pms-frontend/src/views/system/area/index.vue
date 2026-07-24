<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAreaByIp,
  getAreaTree,
  type AreaTreeNode
} from '@/api/yudao-system'

interface AreaTableRow extends AreaTreeNode {
  /** 计算出的层级，根节点为 1 */
  level: number
  /** 父节点 id，根节点为 0 */
  parentId: number
  children?: AreaTableRow[]
}

const loading = ref(false)
const treeData = ref<AreaTableRow[]>([])
const filterText = ref('')

const ipQuery = reactive<{ ip: string; area: string; loading: boolean }>({
  ip: '',
  area: '',
  loading: false
})

/** 将后端返回的树结构补齐 level 和 parentId 字段 */
function decorateTree(list: AreaTreeNode[], level = 1, parentId = 0): AreaTableRow[] {
  return list.map((node) => {
    const decorated: AreaTableRow = {
      id: node.id,
      name: node.name,
      level,
      parentId,
      children: []
    }
    if (node.children && node.children.length) {
      decorated.children = decorateTree(node.children, level + 1, node.id)
    }
    return decorated
  })
}

/** 树形表格过滤：保留命中节点及其所有祖先和子树 */
function filterTree(list: AreaTableRow[], keyword: string): AreaTableRow[] {
  if (!keyword) return list
  const result: AreaTableRow[] = []
  for (const node of list) {
    const children = node.children ? filterTree(node.children, keyword) : []
    if (node.name.includes(keyword) || children.length > 0) {
      result.push({ ...node, children })
    }
  }
  return result
}

const displayedTree = computed(() => filterTree(treeData.value, filterText.value.trim()))

async function loadData() {
  loading.value = true
  try {
    const list = await getAreaTree()
    treeData.value = decorateTree(list || [])
  } catch {
    /* handled by interceptor */
  } finally {
    loading.value = false
  }
}

function handleFilter() {
  // displayedTree 为 computed，filterText 变化会自动重新计算，无需手动触发
}

function handleReset() {
  filterText.value = ''
}

async function handleIpQuery() {
  if (!ipQuery.ip.trim()) {
    ElMessage.warning('请输入 IP 地址')
    return
  }
  ipQuery.loading = true
  ipQuery.area = ''
  try {
    const area = await getAreaByIp(ipQuery.ip.trim())
    ipQuery.area = area || '未查询到地区'
  } catch {
    /* handled by interceptor */
  } finally {
    ipQuery.loading = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <span>地区管理</span>
      </template>

      <el-form :inline="true" @submit.prevent>
        <el-form-item label="地区名称">
          <el-input
            v-model="filterText"
            placeholder="请输入地区名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="'Search'" @click="handleFilter">搜索</el-button>
          <el-button :icon="'Refresh'" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="ipQuery.area"
        :title="`IP「${ipQuery.ip}」归属地：${ipQuery.area}`"
        type="success"
        :closable="false"
        style="margin-bottom: 12px"
      />

      <el-form :inline="true" @submit.prevent style="margin-bottom: 12px">
        <el-form-item label="IP 查询">
          <el-input
            v-model="ipQuery.ip"
            placeholder="请输入 IP 地址"
            clearable
            style="width: 240px"
            @keyup.enter="handleIpQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="ipQuery.loading" @click="handleIpQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="displayedTree"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        stripe
        default-expand-all
      >
        <el-table-column prop="name" label="地区名称" min-width="220" />
        <el-table-column prop="id" label="编号" width="120" />
        <el-table-column label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level === 1 ? 'primary' : row.level === 2 ? 'success' : 'info'">
              第 {{ row.level }} 级
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="parentId" label="父级编号" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 12px;
  justify-content: flex-end;
}
</style>
