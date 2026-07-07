<!-- src/views/lowcode/entity-designer/index.vue -->
<script setup lang="ts">
import { onMounted, onBeforeUnmount, ref, shallowRef } from 'vue'
import { Graph } from '@antv/x6'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getEntityList,
  getEntityDesign,
  saveEntityDesign,
  generateDdl,
  publishEntity,
  deleteEntity,
  checkTableName,
  type LowCodeEntity,
  type LowCodeField,
  type LowCodeRelation,
  type EntityDesignDTO,
  type DdlResultDTO
} from '@/api/lowcode-entity'
import FieldPanel from '@/components/EntityDesigner/FieldPanel.vue'

defineOptions({ name: 'EntityDesignerView' })

const entityList = ref<LowCodeEntity[]>([])
const currentEntity = ref<LowCodeEntity>({
  code: '',
  name: '',
  tableName: '',
  description: '',
  bizType: '',
  status: 'DRAFT'
})
const currentFields = ref<LowCodeField[]>([])
const currentRelations = ref<LowCodeRelation[]>([])
const ddlDialogVisible = ref(false)
const ddlResult = ref<DdlResultDTO | null>(null)
const loading = ref(false)

const graphRef = shallowRef<Graph | null>(null)
const canvasContainer = ref<HTMLDivElement>()

async function loadEntityList() {
  loading.value = true
  try {
    entityList.value = await getEntityList()
  } catch (e) {
    ElMessage.error('加载实体列表失败')
  } finally {
    loading.value = false
  }
}

async function selectEntity(entity: LowCodeEntity) {
  if (!entity.id) return
  const design = await getEntityDesign(entity.id)
  currentEntity.value = design.entity
  currentFields.value = design.fields
  currentRelations.value = design.relations || []
  renderGraph()
}

function renderGraph() {
  if (!graphRef.value) return
  graphRef.value.clearCells()

  // 渲染实体节点
  graphRef.value.addNode({
    shape: 'rect',
    x: 100,
    y: 100,
    width: 220,
    height: 200,
    label: currentEntity.value.name,
    attrs: {
      body: { fill: '#fff', stroke: '#409eff', strokeWidth: 2 },
      label: { fontSize: 14, fill: '#303133' }
    },
    data: {
      entityName: currentEntity.value.name,
      tableName: currentEntity.value.tableName,
      fields: currentFields.value
    }
  })
}

async function saveDesign() {
  if (!currentEntity.value.code || !currentEntity.value.tableName) {
    ElMessage.warning('请填写实体编码和物理表名')
    return
  }
  // 校验表名格式
  if (!/^pms_lc_[a-z][a-z0-9_]*$/.test(currentEntity.value.tableName)) {
    ElMessage.warning('物理表名必须以 pms_lc_ 开头，小写字母+数字+下划线')
    return
  }
  // 校验表名唯一
  const exists = await checkTableName(currentEntity.value.tableName, currentEntity.value.id)
  if (exists) {
    ElMessage.error('物理表名已存在')
    return
  }

  const design: EntityDesignDTO = {
    entity: currentEntity.value,
    fields: currentFields.value,
    relations: currentRelations.value
  }
  try {
    const saved = await saveEntityDesign(design)
    currentEntity.value = saved
    ElMessage.success('保存成功')
    await loadEntityList()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

async function previewDdl() {
  if (!currentEntity.value.id) {
    ElMessage.warning('请先保存实体')
    return
  }
  try {
    ddlResult.value = await generateDdl(currentEntity.value.id)
    ddlDialogVisible.value = true
  } catch (e) {
    ElMessage.error('生成 DDL 失败')
  }
}

async function publish() {
  if (!currentEntity.value.id) {
    ElMessage.warning('请先保存实体')
    return
  }
  try {
    const { value: changeLog } = await ElMessageBox.prompt('请输入变更说明', '发布实体', {
      confirmButtonText: '发布',
      cancelButtonText: '取消'
    })
    await publishEntity(currentEntity.value.id, changeLog || '')
    ElMessage.success('发布成功')
    await loadEntityList()
    await selectEntity(currentEntity.value)
  } catch (e) {
    // 用户取消
  }
}

async function removeEntity(entity: LowCodeEntity) {
  if (!entity.id) return
  try {
    await ElMessageBox.confirm(`确认删除实体 ${entity.name}？`, '提示', { type: 'warning' })
    await deleteEntity(entity.id)
    ElMessage.success('删除成功')
    await loadEntityList()
  } catch (e) {
    // 取消
  }
}

function newEntity() {
  currentEntity.value = {
    code: '',
    name: '',
    tableName: '',
    description: '',
    bizType: '',
    status: 'DRAFT'
  }
  currentFields.value = []
  currentRelations.value = []
}

function initGraph() {
  if (!canvasContainer.value) return
  graphRef.value = new Graph({
    container: canvasContainer.value,
    background: { color: '#f5f5f5' },
    grid: { visible: true, size: 10 },
    interacting: { nodeMovable: true, edgeMovable: true },
    mousewheel: { enabled: true, modifiers: ['ctrl'] },
    connecting: {
      allowBlank: false,
      allowLoop: true,
      allowMulti: true,
      router: 'orth',
      connector: 'rounded'
    }
  })
}

onMounted(() => {
  initGraph()
  loadEntityList()
})

onBeforeUnmount(() => {
  graphRef.value?.dispose()
})
</script>

<template>
  <div class="entity-designer" v-loading="loading">
    <!-- 左侧：实体列表 -->
    <div class="entity-list-panel">
      <div class="panel-header">
        <span>实体列表</span>
        <el-button type="primary" size="small" @click="newEntity">新建</el-button>
      </div>
      <el-scrollbar>
        <div
          v-for="entity in entityList"
          :key="entity.id"
          class="entity-item"
          :class="{ active: entity.id === currentEntity.id }"
          @click="selectEntity(entity)"
        >
          <div class="entity-item-name">{{ entity.name }}</div>
          <div class="entity-item-code">{{ entity.code }}</div>
          <el-tag size="small" :type="entity.status === 'PUBLISHED' ? 'success' : 'info'">
            {{ entity.status }}
          </el-tag>
          <el-button
            type="danger"
            size="small"
            link
            @click.stop="removeEntity(entity)"
          >删除</el-button>
        </div>
      </el-scrollbar>
    </div>

    <!-- 中间：X6 画布 -->
    <div class="canvas-panel">
      <div class="toolbar">
        <el-button type="primary" size="small" @click="saveDesign">保存</el-button>
        <el-button size="small" @click="previewDdl">DDL 预览</el-button>
        <el-button type="success" size="small" @click="publish">发布</el-button>
      </div>
      <div ref="canvasContainer" class="canvas-container"></div>
    </div>

    <!-- 右侧：属性面板 -->
    <div class="property-panel">
      <FieldPanel
        :entity="currentEntity"
        :fields="currentFields"
        @update:entity="currentEntity = $event"
        @update:fields="currentFields = $event"
      />
    </div>

    <!-- DDL 预览对话框 -->
    <el-dialog v-model="ddlDialogVisible" title="DDL 预览" width="700px">
      <div v-if="ddlResult">
        <el-alert
          v-if="ddlResult.hasJunctionTable"
          type="info"
          title="检测到多对多关联，已自动生成中间表 DDL"
          :closable="false"
          style="margin-bottom: 10px"
        />
        <pre v-for="(sql, i) in ddlResult.ddlStatements" :key="i" class="ddl-block">{{ sql }};</pre>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.entity-designer {
  display: flex;
  height: calc(100vh - 84px);
  gap: 1px;
  background: #dcdfe6;
}

.entity-list-panel {
  width: 220px;
  background: #fff;
  display: flex;
  flex-direction: column;
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ebeef5;
    font-weight: 600;
  }
  .entity-item {
    padding: 8px 10px;
    border-bottom: 1px solid #f5f5f5;
    cursor: pointer;
    &:hover { background: #f5f7fa; }
    &.active { background: #ecf5ff; border-left: 3px solid #409eff; }
    .entity-item-name { font-size: 13px; font-weight: 500; }
    .entity-item-code { font-size: 11px; color: #909399; margin: 2px 0; }
  }
}

.canvas-panel {
  flex: 1;
  background: #fff;
  display: flex;
  flex-direction: column;
  .toolbar {
    padding: 8px 10px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    gap: 8px;
  }
  .canvas-container {
    flex: 1;
    overflow: hidden;
  }
}

.property-panel {
  width: 380px;
  background: #fff;
  overflow-y: auto;
}

.ddl-block {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  overflow-x: auto;
  margin-bottom: 10px;
}
</style>
