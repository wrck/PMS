<template>
  <div>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6"><el-card><el-statistic title="项目总数" :value="stats.total" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="进行中" :value="stats.running" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="已完成" :value="stats.completed" /></el-card></el-col>
      <el-col :span="6"><el-card><el-statistic title="指派率" :value="stats.assignRate" suffix="%" /></el-card></el-col>
    </el-row>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div style="display:flex;justify-content:space-between;align-items:center">
              <span>项目趋势图</span>
              <el-select v-model="trendOffice" size="small" style="width:120px" @change="fetchTrend">
                <el-option label="全部" value="" />
                <el-option v-for="d in deptList" :key="d.departmentNum" :label="d.departmentName" :value="d.departmentNum" />
              </el-select>
            </div>
          </template>
          <div ref="trendChartRef" style="height:350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>闭环质量趋势图</span></template>
          <div ref="qualityChartRef" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <el-card>
          <template #header><span>指派率统计</span></template>
          <div ref="assignedChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>跟踪率统计</span></template>
          <div ref="traceChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header><span>季度闭环比</span></template>
          <div ref="closeChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header><span>实施方式占比</span></template>
          <div ref="implChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>项目状态综合统计</span></template>
          <div ref="statusChartRef" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getLineData, getLineQualityData, getAssignedRate, getTraceRate, getCloseRate, getImplRate, getProjectSummaryStatus } from '@/api/report'
import { listDepts } from '@/api/system'

const stats = ref({ total: 0, running: 0, completed: 0, assignRate: 0 })
const trendOffice = ref('')
const deptList = ref([])

const trendChartRef = ref(null)
const qualityChartRef = ref(null)
const assignedChartRef = ref(null)
const traceChartRef = ref(null)
const closeChartRef = ref(null)
const implChartRef = ref(null)
const statusChartRef = ref(null)

let charts = []

const initChart = (el, option) => {
  if (!el) return null
  const chart = echarts.init(el)
  chart.setOption(option)
  charts.push(chart)
  return chart
}

const fetchStats = async () => {
  try {
    const r = await getProjectSummaryStatus()
    if (r.data) Object.assign(stats.value, r.data)
  } catch (e) {}
}

const fetchTrend = async () => {
  try {
    const r = await getLineData({ officeCode: trendOffice.value })
    const data = r.data || {}
    initChart(trendChartRef.value, {
      tooltip: { trigger: 'axis' },
      legend: { data: data.legend || [] },
      xAxis: { type: 'category', data: data.xAxis || [] },
      yAxis: { type: 'value' },
      series: (data.series || []).map(s => ({ ...s, type: 'line', smooth: true }))
    })
  } catch (e) {}
}

const fetchQuality = async () => {
  try {
    const r = await getLineQualityData()
    const data = r.data || {}
    initChart(qualityChartRef.value, {
      tooltip: { trigger: 'axis' },
      legend: { data: data.legend || [] },
      xAxis: { type: 'category', data: data.xAxis || [] },
      yAxis: { type: 'value' },
      series: (data.series || []).map(s => ({ ...s, type: 'line', smooth: true }))
    })
  } catch (e) {}
}

const fetchAssigned = async () => {
  try {
    const r = await getAssignedRate()
    const data = r.data || {}
    initChart(assignedChartRef.value, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.xAxis || [] },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
      series: [{ type: 'bar', data: data.values || [], itemStyle: { color: '#409EFF' } }]
    })
  } catch (e) {}
}

const fetchTrace = async () => {
  try {
    const r = await getTraceRate()
    const data = r.data || {}
    initChart(traceChartRef.value, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.xAxis || [] },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
      series: [{ type: 'bar', data: data.values || [], itemStyle: { color: '#67C23A' } }]
    })
  } catch (e) {}
}

const fetchClose = async () => {
  try {
    const r = await getCloseRate()
    const data = r.data || {}
    initChart(closeChartRef.value, {
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.xAxis || [] },
      yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
      series: [{ type: 'bar', data: data.values || [], itemStyle: { color: '#E6A23C' } }]
    })
  } catch (e) {}
}

const fetchImpl = async () => {
  try {
    const r = await getImplRate()
    const data = r.data || {}
    initChart(implChartRef.value, {
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '60%', data: data.pieData || [], label: { formatter: '{b}: {d}%' } }]
    })
  } catch (e) {}
}

const fetchStatus = async () => {
  try {
    const r = await getProjectSummaryStatus()
    const data = r.data || {}
    initChart(statusChartRef.value, {
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: ['40%', '60%'], data: data.statusPie || [], label: { formatter: '{b}: {c}' } }]
    })
  } catch (e) {}
}

const handleResize = () => { charts.forEach(c => c.resize()) }

onMounted(async () => {
  try { const r = await listDepts(); deptList.value = r.data || [] } catch (e) {}
  await fetchStats()
  await nextTick()
  await Promise.all([fetchTrend(), fetchQuality(), fetchAssigned(), fetchTrace(), fetchClose(), fetchImpl(), fetchStatus()])
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  charts.forEach(c => c.dispose())
})
</script>
