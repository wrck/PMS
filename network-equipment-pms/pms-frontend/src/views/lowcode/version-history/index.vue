<!-- src/views/lowcode/version-history/index.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import JsonTreeDiff from '@/components/JsonTreeDiff/index.vue'
import PromotionPipeline from './PromotionPipeline.vue'
import {
  getVersionHistory,
  getVersionTree,
  diffVersions,
  rollbackVersion,
  exportPackageZip,
  importPackage,
  type LowCodeConfigVersion,
  type VersionDiffDTO,
  type VersionTreeNode
} from '@/api/lowcode-version'
import type { EpTagType } from '@/types'

defineOptions({ name: 'VersionHistoryView' })

const configType = ref('ENTITY')
const configId = ref<number>()
const versionList = ref<LowCodeConfigVersion[]>([])
const versionTree = ref<VersionTreeNode[]>([])
const selectedVersions = ref<number[]>([])
const diffResult = ref<VersionDiffDTO | null>(null)
const loading = ref(false)
const diffMode = ref<'tree' | 'flat'>('tree')
const oldSnapshot = ref<unknown>(null)
const newSnapshot = ref<unknown>(null)
const exportDialogVisible = ref(false)
const importDialogVisible = ref(false)
const exportCodes = ref('')
const exportTargetEnv = ref('TEST')
const importFile = ref<File | null>(null)
const importOverwrite = ref(false)
/** 视图切换：列表 / 树形 / 管道图 */
const activeTab = ref<'list' | 'tree' | 'pipeline'>('list')

async function loadHistory() {
  if (!configId.value) {
    ElMessage.warning('请输入配置ID')
    return
  }
  loading.value = true
  try {
    versionList.value = await getVersionHistory(configType.value, configId.value)
    // 树形视图懒加载：仅当当前在树形/管道图视图时拉取，避免多余请求
    if (activeTab.value !== 'list') {
      await loadTree()
    }
  } catch (e) {
    ElMessage.error('加载版本历史失败')
  } finally {
    loading.value = false
  }
}

async function loadTree() {
  if (!configId.value) return
  try {
    versionTree.value = await getVersionTree(configType.value, configId.value)
  } catch (e) {
    // 树加载失败不阻断主流程
    versionTree.value = []
  }
}

/** 切换到树形/管道图视图时按需加载版本树 */
async function onTabChange(tab: string) {
  if ((tab === 'tree' || tab === 'pipeline') && versionTree.value.length === 0 && configId.value) {
    await loadTree()
  }
}

async function showDiff() {
  if (selectedVersions.value.length !== 2) {
    ElMessage.warning('请选择两个版本进行对比')
    return
  }
  const [from, to] = [...selectedVersions.value].sort((a, b) => a - b)
  await doDiff(from, to)
}

/** 对比指定版本与其前一版本（树形/管道图卡片点击触发） */
async function diffSingle(version: LowCodeConfigVersion) {
  if (version.version <= 1) {
    ElMessage.warning('首个版本无前置版本可对比')
    return
  }
  await doDiff(version.version - 1, version.version)
}

async function doDiff(from: number, to: number) {
  try {
    diffResult.value = await diffVersions(configType.value, configId.value!, from, to)
    const fromVersion = versionList.value.find(v => v.version === from)
    const toVersion = versionList.value.find(v => v.version === to)
    if (fromVersion && toVersion) {
      try {
        oldSnapshot.value = JSON.parse(fromVersion.snapshot || '{}')
        newSnapshot.value = JSON.parse(toVersion.snapshot || '{}')
      } catch (e) {
        oldSnapshot.value = null
        newSnapshot.value = null
      }
    }
  } catch (e) {
    ElMessage.error('Diff 计算失败')
  }
}

async function rollback(version: LowCodeConfigVersion) {
  try {
    const { value: changeLog } = await ElMessageBox.prompt(
      `确认回滚到版本 ${version.version}？请输入变更说明`,
      '版本回滚',
      { confirmButtonText: '回滚', cancelButtonText: '取消' }
    )
    await rollbackVersion(configType.value, configId.value!, version.version, changeLog)
    ElMessage.success('回滚成功，已生成新版本')
    await loadHistory()
  } catch (e) {
    // 用户取消
  }
}

async function onExport() {
  const codes = exportCodes.value.split(',').map(s => s.trim()).filter(Boolean)
  if (codes.length === 0) {
    ElMessage.warning('请输入至少一个配置编码')
    return
  }
  try {
    const blob = await exportPackageZip(codes, exportTargetEnv.value)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `lowcode-package-${exportTargetEnv.value}.zip`
    a.click()
    URL.revokeObjectURL(url)
    exportDialogVisible.value = false
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

async function onImport() {
  if (!importFile.value) {
    ElMessage.warning('请选择文件')
    return
  }
  try {
    await importPackage(importFile.value, importOverwrite.value)
    ElMessage.success('导入成功')
    importDialogVisible.value = false
    importFile.value = null
    if (configId.value) await loadHistory()
  } catch (e) {
    ElMessage.error('导入失败')
  }
}

function onFileChange(file: any) {
  importFile.value = file.raw
}

function changeTypeTag(type: string): EpTagType {
  if (type === 'ADDED') return 'success'
  if (type === 'REMOVED') return 'danger'
  return 'warning'
}

function changeTypeLabel(type: string) {
  if (type === 'ADDED') return '新增'
  if (type === 'REMOVED') return '删除'
  return '修改'
}

function handleSelectionChange(rows: LowCodeConfigVersion[]) {
  selectedVersions.value = rows.map((r) => r.version)
}

/** el-tree 配置：子节点字段为 children */
const treeProps = { children: 'children', label: 'version' }

/** 树节点显示文案 */
function treeNodeLabel(data: VersionTreeNode): string {
  return `v${data.version}${data.changeLog ? ' · ' + data.changeLog : ''}`
}

/** 环境标签颜色 */
function envTagType(env: string): EpTagType {
  if (env === 'PROD') return 'danger'
  if (env === 'TEST') return 'warning'
  return 'info'
}

const hasDiff = computed(() => diffResult.value && diffResult.value.entries.length > 0)
</script>

<template>
  <div class="version-history" v-loading="loading">
    <el-card shadow="never">
      <template #header>
        <span>版本历史与对比</span>
      </template>
      <el-form inline>
        <el-form-item label="配置类型">
          <el-select v-model="configType" style="width: 150px">
            <el-option label="实体" value="ENTITY" />
            <el-option label="表单" value="FORM" />
            <el-option label="列表" value="LIST" />
            <el-option label="微流" value="MICROFLOW" />
            <el-option label="规则" value="RULE" />
            <el-option label="连接器" value="CONNECTOR" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置ID">
          <el-input-number v-model="configId" :min="1" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadHistory">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>
        <span>环境晋升</span>
      </template>
      <el-button type="success" @click="exportDialogVisible = true">导出配置包</el-button>
      <el-button type="warning" @click="importDialogVisible = true">导入配置包</el-button>
    </el-card>

    <!-- 导出对话框 -->
    <el-dialog v-model="exportDialogVisible" title="导出配置包" width="500px">
      <el-form label-width="120px">
        <el-form-item label="配置编码（逗号分隔）">
          <el-input v-model="exportCodes" placeholder="如 entity_user,entity_role" />
        </el-form-item>
        <el-form-item label="目标环境">
          <el-select v-model="exportTargetEnv" style="width: 200px">
            <el-option label="测试环境" value="TEST" />
            <el-option label="生产环境" value="PROD" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onExport">导出</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入配置包" width="500px">
      <el-form label-width="120px">
        <el-form-item label="选择文件">
          <el-upload :auto-upload="false" :on-change="onFileChange" :limit="1" accept=".zip,.json">
              <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="覆盖已存在">
          <el-switch v-model="importOverwrite" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onImport">导入</el-button>
      </template>
    </el-dialog>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>版本列表</span>
              <el-button size="small" @click="showDiff" :disabled="selectedVersions.length !== 2">
                对比选中版本
              </el-button>
            </div>
          </template>
          <!-- 三视图切换：列表 / 树形 / 管道图 -->
          <el-tabs v-model="activeTab" @tab-change="onTabChange">
            <el-tab-pane label="列表" name="list">
              <el-table
                :data="versionList"
                @selection-change="handleSelectionChange"
                row-key="version"
                size="small"
              >
                <el-table-column type="selection" :reserve-selection="true" width="40" />
                <el-table-column label="版本" prop="version" width="60" />
                <el-table-column label="变更说明" prop="changeLog" show-overflow-tooltip />
                <el-table-column label="环境" prop="environment" width="70">
                  <template #default="{ row }">
                    <el-tag size="small" :type="envTagType(row.environment)">
                      {{ row.environment }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作人" prop="createBy" width="100" />
                <el-table-column label="时间" prop="createTime" width="160">
                  <template #default="{ row }">
                    {{ row.createTime?.replace('T', ' ').slice(0, 16) }}
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template #default="{ row }">
                    <el-button type="warning" size="small" link @click="rollback(row)">回滚</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="版本树" name="tree">
              <el-empty
                v-if="versionTree.length === 0"
                description="暂无版本数据"
                :image-size="60"
              />
              <el-tree
                v-else
                :data="versionTree"
                :props="treeProps"
                node-key="version"
                default-expand-all
                :expand-on-click-node="false"
              >
                <template #default="{ data }">
                  <div class="tree-node">
                    <span class="tree-node-label">
                      <strong>{{ treeNodeLabel(data) }}</strong>
                    </span>
                    <el-tag size="small" :type="envTagType(data.environment)" class="tree-node-tag">
                      {{ data.environment }}
                    </el-tag>
                    <span class="tree-node-meta">
                      {{ data.createBy || '-' }} · {{ data.createTime?.replace('T', ' ').slice(0, 16) }}
                    </span>
                    <span class="tree-node-actions" @click.stop>
                      <el-button link type="primary" size="small" @click="diffSingle(data)">对比</el-button>
                      <el-button link type="warning" size="small" @click="rollback(data)">回滚</el-button>
                    </span>
                  </div>
                </template>
              </el-tree>
            </el-tab-pane>

            <el-tab-pane label="晋升管道图" name="pipeline">
              <PromotionPipeline
                :versions="versionList"
                @diff="diffSingle"
                @rollback="rollback"
              />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>版本差异</span>
              <el-radio-group v-model="diffMode" size="small" v-if="diffResult">
                <el-radio-button value="tree">树形视图</el-radio-button>
                <el-radio-button value="flat">扁平表格</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <el-empty v-if="!diffResult" description="请选择两个版本进行对比" :image-size="60" />
          <div v-else-if="!hasDiff" style="text-align: center; padding: 40px; color: #909399">
            两个版本无差异
          </div>
          <JsonTreeDiff v-else-if="diffMode === 'tree'" :old-data="oldSnapshot" :new-data="newSnapshot" />
          <el-table v-else :data="diffResult.entries" size="small" border>
            <el-table-column label="类型" prop="changeType" width="80">
              <template #default="{ row }">
                <el-tag size="small" :type="changeTypeTag(row.changeType)">
                  {{ changeTypeLabel(row.changeType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="字段路径" prop="fieldPath" />
            <el-table-column label="旧值" prop="oldValue" show-overflow-tooltip />
            <el-table-column label="新值" prop="newValue" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.version-history {
  padding: 16px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding-right: 8px;
}

.tree-node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tree-node-tag {
  flex-shrink: 0;
}

.tree-node-meta {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  flex-shrink: 0;
}

.tree-node-actions {
  flex-shrink: 0;
}
</style>
