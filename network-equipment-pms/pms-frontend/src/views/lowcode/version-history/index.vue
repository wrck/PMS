<!-- src/views/lowcode/version-history/index.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getVersionHistory,
  diffVersions,
  rollbackVersion,
  type LowCodeConfigVersion,
  type VersionDiffDTO
} from '@/api/lowcode-version'
import type { EpTagType } from '@/types'

defineOptions({ name: 'VersionHistoryView' })

const configType = ref('ENTITY')
const configId = ref<number>()
const versionList = ref<LowCodeConfigVersion[]>([])
const selectedVersions = ref<number[]>([])
const diffResult = ref<VersionDiffDTO | null>(null)
const loading = ref(false)

async function loadHistory() {
  if (!configId.value) {
    ElMessage.warning('请输入配置ID')
    return
  }
  loading.value = true
  try {
    versionList.value = await getVersionHistory(configType.value, configId.value)
  } catch (e) {
    ElMessage.error('加载版本历史失败')
  } finally {
    loading.value = false
  }
}

async function showDiff() {
  if (selectedVersions.value.length !== 2) {
    ElMessage.warning('请选择两个版本进行对比')
    return
  }
  const [from, to] = [...selectedVersions.value].sort((a, b) => a - b)
  try {
    diffResult.value = await diffVersions(configType.value, configId.value!, from, to)
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
          <el-table
            :data="versionList"
            @selection-change="handleSelectionChange"
            row-key="version"
          >
            <el-table-column type="selection" :reserve-selection="true" width="40" />
            <el-table-column label="版本" prop="version" width="60" />
            <el-table-column label="变更说明" prop="changeLog" show-overflow-tooltip />
            <el-table-column label="环境" prop="environment" width="70">
              <template #default="{ row }">
                <el-tag size="small" :type="row.environment === 'PROD' ? 'danger' : row.environment === 'TEST' ? 'warning' : 'info'">
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
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>版本差异</span>
          </template>
          <el-empty v-if="!diffResult" description="请选择两个版本进行对比" :image-size="60" />
          <div v-else-if="!hasDiff" style="text-align: center; padding: 40px; color: #909399">
            两个版本无差异
          </div>
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
</style>
