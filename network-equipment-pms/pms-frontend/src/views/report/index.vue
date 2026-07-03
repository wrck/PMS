<script setup lang="ts">
import * as echarts from 'echarts'
import * as XLSX from 'xlsx'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAssetStats,
  getDeliveryStats,
  getImplementationStats,
  type AgentRankingRow,
  type AssetStats,
  type DeliveryStats,
  type ImplementationStats,
  type MonthlyStat
} from '@/api/report'

defineOptions({ name: 'ReportDashboard' })

// ---- Project delivery ----
const dateRange = ref<[string, string] | null>(getDefaultDateRange())

function getDefaultDateRange(): [string, string] {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth() - 5, 1)
  return [fmtDate(start), fmtDate(now)]
}

function fmtDate(d: Date): string {
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

const monthlyStats = ref<MonthlyStat[]>([])
const deliveryStats = ref({
  totalInitiated: 0,
  totalInProgress: 0,
  totalCompleted: 0,
  avgCycleDays: 0,
  delayRate: 0
})

// ---- Asset stats ----
const assetStats = ref<AssetStats>({
  byStatus: {},
  byCategory: {},
  totalValue: 0,
  total: 0,
  inStock: 0,
  allocated: 0,
  inTransfer: 0,
  scrapped: 0
})

const utilizationRate = computed(() => {
  const used = assetStats.value.allocated + assetStats.value.inTransfer
  return Math.round((used / Math.max(assetStats.value.total, 1)) * 1000) / 10
})

// ---- Implementation efficiency ----
const implementationStats = ref<ImplementationStats>({
  efficiency: [],
  efficiencyByType: {
    OEM: { total: 0, completed: 0, completionRate: 0, avgDurationDays: 0 },
    AGENT: { total: 0, completed: 0, completionRate: 0, avgDurationDays: 0 }
  },
  agentRanking: []
})

const efficiencyByType = computed(() => implementationStats.value.efficiencyByType)
const agentRanking = ref<AgentRankingRow[]>([])

// ---- Loading state ----
const loading = ref(false)

// ---- Charts ----
const projectChartRef = ref<HTMLElement>()
const assetChartRef = ref<HTMLElement>()
const completionRateChartRef = ref<HTMLElement>()
const durationChartRef = ref<HTMLElement>()

let chartInstances: echarts.ECharts[] = []

function initChart(dom: HTMLElement | undefined): echarts.ECharts | null {
  if (!dom) return null
  // Reuse existing instance if the dom already has one
  const existing = chartInstances.find((c) => c.getDom() === dom)
  if (existing) return existing
  const inst = echarts.init(dom)
  chartInstances.push(inst)
  return inst
}

function renderProjectChart() {
  const inst = initChart(projectChartRef.value)
  if (!inst) return
  const months = monthlyStats.value.map((m) => m.month)
  inst.setOption(
    {
      tooltip: { trigger: 'axis' },
      legend: { data: ['立项数', '完成数'], top: 0 },
      grid: { left: 40, right: 20, top: 40, bottom: 30 },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value', minInterval: 1 },
      series: [
        {
          name: '立项数',
          type: 'bar',
          data: monthlyStats.value.map((m) => m.initiated),
          itemStyle: { color: '#409EFF' }
        },
        {
          name: '完成数',
          type: 'bar',
          data: monthlyStats.value.map((m) => m.completed),
          itemStyle: { color: '#67C23A' }
        }
      ]
    },
    { notMerge: true }
  )
}

function renderAssetChart() {
  const inst = initChart(assetChartRef.value)
  if (!inst) return
  const a = assetStats.value
  inst.setOption(
    {
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { bottom: 0, left: 'center' },
      series: [
        {
          name: '设备状态',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '45%'],
          avoidLabelOverlap: true,
          label: { show: true, formatter: '{b}\n{d}%' },
          data: [
            { value: a.inStock, name: '在库', itemStyle: { color: '#409EFF' } },
            { value: a.allocated, name: '已分配', itemStyle: { color: '#67C23A' } },
            { value: a.inTransfer, name: '调拨中', itemStyle: { color: '#E6A23C' } },
            { value: a.scrapped, name: '报废', itemStyle: { color: '#909399' } }
          ]
        }
      ]
    },
    { notMerge: true }
  )
}

function renderCompletionRateChart() {
  const inst = initChart(completionRateChartRef.value)
  if (!inst) return
  const e = efficiencyByType.value
  inst.setOption(
    {
      tooltip: { trigger: 'axis', formatter: '{b}<br/>{a}: {c}%' },
      grid: { left: 50, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: ['原厂(OEM)', '代理商(Agent)'] },
      yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
      series: [
        {
          name: '任务完成率',
          type: 'bar',
          data: [
            { value: e.OEM.completionRate, itemStyle: { color: '#409EFF' } },
            { value: e.AGENT.completionRate, itemStyle: { color: '#E6A23C' } }
          ],
          barWidth: '40%',
          label: { show: true, position: 'top', formatter: '{c}%' }
        }
      ]
    },
    { notMerge: true }
  )
}

function renderDurationChart() {
  const inst = initChart(durationChartRef.value)
  if (!inst) return
  const e = efficiencyByType.value
  inst.setOption(
    {
      tooltip: { trigger: 'axis', formatter: '{b}<br/>{a}: {c} 天' },
      grid: { left: 50, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: ['原厂(OEM)', '代理商(Agent)'] },
      yAxis: { type: 'value', minInterval: 1, name: '天' },
      series: [
        {
          name: '平均任务周期',
          type: 'bar',
          data: [
            { value: e.OEM.avgDurationDays, itemStyle: { color: '#67C23A' } },
            { value: e.AGENT.avgDurationDays, itemStyle: { color: '#F56C6C' } }
          ],
          barWidth: '40%',
          label: { show: true, position: 'top', formatter: '{c}天' }
        }
      ]
    },
    { notMerge: true }
  )
}

function renderAllCharts() {
  nextTick(() => {
    renderProjectChart()
    renderAssetChart()
    renderCompletionRateChart()
    renderDurationChart()
  })
}

function disposeCharts() {
  chartInstances.forEach((c) => c.dispose())
  chartInstances = []
}

function handleResize() {
  chartInstances.forEach((c) => c.resize())
}

// ---- Data loading ----
async function loadAll() {
  loading.value = true
  try {
    const [delivery, asset, impl] = await Promise.all([
      getDeliveryStats({
        startDate: dateRange.value?.[0],
        endDate: dateRange.value?.[1]
      }),
      getAssetStats(),
      getImplementationStats()
    ])
    monthlyStats.value = delivery.monthlyStats ?? []
    deliveryStats.value = {
      totalInitiated: delivery.totalInitiated ?? 0,
      totalInProgress: delivery.totalInProgress ?? 0,
      totalCompleted: delivery.totalCompleted ?? 0,
      avgCycleDays: Number(delivery.avgCycleDays ?? 0),
      delayRate: Number(delivery.delayRate ?? 0)
    }
    assetStats.value = asset
    implementationStats.value = impl
    agentRanking.value = impl.agentRanking ?? []
    renderAllCharts()
  } catch (e) {
    // The request interceptor already shows an error message; nothing more to do.
    console.error('Failed to load report data', e)
  } finally {
    loading.value = false
  }
}

async function handleDateChange() {
  // Re-fetch delivery stats for the new range; asset / implementation stats are
  // not date-scoped so we only reload the delivery slice.
  try {
    const delivery = await getDeliveryStats({
      startDate: dateRange.value?.[0],
      endDate: dateRange.value?.[1]
    })
    monthlyStats.value = delivery.monthlyStats ?? []
    deliveryStats.value = {
      totalInitiated: delivery.totalInitiated ?? 0,
      totalInProgress: delivery.totalInProgress ?? 0,
      totalCompleted: delivery.totalCompleted ?? 0,
      avgCycleDays: Number(delivery.avgCycleDays ?? 0),
      delayRate: Number(delivery.delayRate ?? 0)
    }
    nextTick(() => renderProjectChart())
  } catch (e) {
    console.error('Failed to reload delivery stats', e)
  }
}

// ---- Excel export ----
function exportExcel() {
  const workbook = XLSX.utils.book_new()

  // Sheet 1: project delivery stats
  const deliveryRows = monthlyStats.value.map((m) => ({
    月份: m.month,
    立项数: m.initiated,
    完成数: m.completed
  }))
  deliveryRows.push({
    月份: '汇总',
    立项数: deliveryStats.value.totalInitiated,
    完成数: deliveryStats.value.totalCompleted
  })
  const deliverySheet = XLSX.utils.json_to_sheet(deliveryRows)
  XLSX.utils.book_append_sheet(workbook, deliverySheet, '项目交付统计')

  // Sheet 2: asset stats
  const assetRows = [
    { 项目: '设备总数', 数值: assetStats.value.total },
    { 项目: '在库数', 数值: assetStats.value.inStock },
    { 项目: '已分配数', 数值: assetStats.value.allocated },
    { 项目: '调拨中数', 数值: assetStats.value.inTransfer },
    { 项目: '已报废数', 数值: assetStats.value.scrapped },
    { 项目: '设备总价值', 数值: assetStats.value.totalValue },
    { 项目: '利用率(%)', 数值: utilizationRate.value }
  ]
  const assetSheet = XLSX.utils.json_to_sheet(assetRows)
  XLSX.utils.book_append_sheet(workbook, assetSheet, '设备资产统计')

  // Sheet 3: implementation efficiency
  const e = efficiencyByType.value
  const implRows = [
    {
      类型: '原厂(OEM)',
      任务总数: e.OEM.total,
      完成数: e.OEM.completed,
      完成率: e.OEM.completionRate,
      平均工期_天: e.OEM.avgDurationDays
    },
    {
      类型: '代理商(Agent)',
      任务总数: e.AGENT.total,
      完成数: e.AGENT.completed,
      完成率: e.AGENT.completionRate,
      平均工期_天: e.AGENT.avgDurationDays
    }
  ]
  const implSheet = XLSX.utils.json_to_sheet(implRows)
  XLSX.utils.book_append_sheet(workbook, implSheet, '实施效能统计')

  // Sheet 4: agent ranking
  const rankingRows = agentRanking.value.map((r) => ({
    排名: r.rank,
    代理商: r.agentName,
    综合评分: r.overallScore,
    响应速度: r.responseSpeedScore,
    施工质量: r.constructionQualityScore,
    文档完整性: r.documentCompletenessScore,
    任务数: r.taskCount
  }))
  const rankingSheet = XLSX.utils.json_to_sheet(rankingRows)
  XLSX.utils.book_append_sheet(workbook, rankingSheet, '代理商排名')

  const fileName = `报表_${new Date().toISOString().slice(0, 10)}.xlsx`
  XLSX.writeFile(workbook, fileName)
  ElMessage.success(`已导出: ${fileName}`)
}

watch(dateRange, () => {
  handleDateChange()
})

onMounted(() => {
  loadAll()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  disposeCharts()
})
</script>

<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">数据报表分析</span>
          <el-button type="success" :loading="loading" @click="exportExcel">
            <el-icon><Download /></el-icon>
            导出 Excel
          </el-button>
        </div>
      </template>

      <!-- Section 1: Project Delivery -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">项目交付统计</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 280px"
          />
        </div>
        <el-row :gutter="16" class="stat-row">
          <el-col :span="6">
            <div class="stat-card stat-blue">
              <div class="stat-label">总立项数</div>
              <div class="stat-value">{{ deliveryStats.totalInitiated }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card stat-green">
              <div class="stat-label">已完成数</div>
              <div class="stat-value">{{ deliveryStats.totalCompleted }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card stat-orange">
              <div class="stat-label">平均交付周期(天)</div>
              <div class="stat-value">{{ deliveryStats.avgCycleDays }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card stat-red">
              <div class="stat-label">延期率</div>
              <div class="stat-value">{{ deliveryStats.delayRate }}%</div>
            </div>
          </el-col>
        </el-row>
        <div ref="projectChartRef" class="chart-box" />
      </div>

      <el-divider />

      <!-- Section 2: Asset stats -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">设备资产统计</span>
        </div>
        <el-row :gutter="16">
          <el-col :span="10">
            <el-row :gutter="12" class="stat-row">
              <el-col :span="12">
                <div class="stat-card stat-blue">
                  <div class="stat-label">设备总数</div>
                  <div class="stat-value">{{ assetStats.total }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-card stat-green">
                  <div class="stat-label">在库数</div>
                  <div class="stat-value">{{ assetStats.inStock }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-card stat-orange">
                  <div class="stat-label">已分配数</div>
                  <div class="stat-value">{{ assetStats.allocated }}</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-card stat-purple">
                  <div class="stat-label">利用率</div>
                  <div class="stat-value">{{ utilizationRate }}%</div>
                </div>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="14">
            <div ref="assetChartRef" class="chart-box" />
          </el-col>
        </el-row>
      </div>

      <el-divider />

      <!-- Section 3: Implementation efficiency -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">实施效能分析</span>
        </div>
        <el-row :gutter="16">
          <el-col :span="12">
            <h4 class="chart-sub-title">任务完成率对比</h4>
            <div ref="completionRateChartRef" class="chart-box" />
          </el-col>
          <el-col :span="12">
            <h4 class="chart-sub-title">平均任务周期对比</h4>
            <div ref="durationChartRef" class="chart-box" />
          </el-col>
        </el-row>

        <h4 class="chart-sub-title">代理商质量评分排名</h4>
        <el-table :data="agentRanking" border stripe size="small">
          <el-table-column prop="rank" label="排名" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.rank <= 3 ? 'warning' : 'info'" size="small">{{ row.rank }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="agentName" label="代理商" min-width="180" />
          <el-table-column prop="overallScore" label="综合评分" width="110" align="center">
            <template #default="{ row }">
              <span class="score-emph">{{ Number(row.overallScore).toFixed(1) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="responseSpeedScore" label="响应速度" width="110" align="center">
            <template #default="{ row }">{{ Number(row.responseSpeedScore).toFixed(1) }}</template>
          </el-table-column>
          <el-table-column prop="constructionQualityScore" label="施工质量" width="110" align="center">
            <template #default="{ row }">{{ Number(row.constructionQualityScore).toFixed(1) }}</template>
          </el-table-column>
          <el-table-column prop="documentCompletenessScore" label="文档完整性" width="120" align="center">
            <template #default="{ row }">{{ Number(row.documentCompletenessScore).toFixed(1) }}</template>
          </el-table-column>
          <el-table-column prop="taskCount" label="任务数" width="100" align="center" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.section {
  margin-bottom: 8px;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.section-title {
  font-size: 15px;
  font-weight: 600;
  position: relative;
  padding-left: 10px;
}
.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: var(--el-color-primary);
  border-radius: 2px;
}
.stat-row {
  margin-bottom: 8px;
}
.stat-card {
  padding: 16px;
  border-radius: 8px;
  color: #fff;
  margin-bottom: 12px;
}
.stat-label {
  font-size: 13px;
  opacity: 0.9;
}
.stat-value {
  font-size: 26px;
  font-weight: 700;
  margin-top: 6px;
}
.stat-blue {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}
.stat-green {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}
.stat-orange {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}
.stat-red {
  background: linear-gradient(135deg, #F56C6C, #f78989);
}
.stat-purple {
  background: linear-gradient(135deg, #9b59b6, #b07cc6);
}
.chart-box {
  width: 100%;
  height: 320px;
}
.chart-sub-title {
  margin: 8px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
.score-emph {
  color: var(--el-color-warning);
  font-weight: 700;
  font-size: 15px;
}
</style>
