import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h } from 'vue'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// Mock the report API so the dashboard does not fire real network requests
vi.mock('@/api/report', () => ({
  getDashboardStats: vi.fn().mockResolvedValue({
    projectTotal: 12,
    assetInStock: 5,
    todoCount: 3,
    monthDelivery: 1,
    projectInProgress: 4,
    monthNewProject: 2,
    monthNewAsset: 1,
    alertCount: 0
  }),
  getProjectTrend: vi.fn().mockResolvedValue([
    { month: '2026-02', status: 'IN_PROGRESS', count: 2 },
    { month: '2026-02', status: 'COMPLETED', count: 1 }
  ]),
  getTodoList: vi.fn().mockResolvedValue([]),
  getRecentActivities: vi.fn().mockResolvedValue([]),
  getAssetStats: vi.fn().mockResolvedValue({
    byStatus: {},
    byCategory: {},
    totalValue: 0,
    total: 0,
    inStock: 0,
    allocated: 0,
    inTransfer: 0,
    scrapped: 0
  })
}))

// Mock vue-router so the dashboard's useRouter() works without a real router
vi.mock('vue-router', () => ({
  useRouter: () => ({ push: vi.fn() })
}))

// Mock the user store — the dashboard greets the user from store.userInfo.
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    userInfo: { nickname: 'Tester', username: 'tester' },
    token: 't',
    permissions: []
  })
}))

// Mock echarts so chart rendering does not depend on a real canvas in jsdom
vi.mock('echarts', () => ({
  init: vi.fn(() => ({
    setOption: vi.fn(),
    resize: vi.fn(),
    dispose: vi.fn(),
    getDom: () => ({})
  })),
  ECharts: class {}
}))

import Dashboard from '@/views/dashboard/index.vue'

// --- Element Plus component stubs ---

const ElCardStub = defineComponent({
  name: 'ElCard',
  setup(_, { slots }) {
    return () => h('div', { class: 'el-card' }, slots.default?.())
  }
})

const ElRowStub = defineComponent({
  name: 'ElRow',
  setup(_, { slots }) {
    return () => h('div', { class: 'el-row' }, slots.default?.())
  }
})

const ElColStub = defineComponent({
  name: 'ElCol',
  setup(_, { slots }) {
    return () => h('div', { class: 'el-col' }, slots.default?.())
  }
})

const ElIconStub = defineComponent({
  name: 'ElIcon',
  setup(_, { slots }) {
    return () => h('span', { class: 'el-icon' }, slots.default?.())
  }
})

const ElEmptyStub = defineComponent({
  name: 'ElEmpty',
  setup() {
    return () => h('div', { class: 'el-empty' })
  }
})

const ElTagStub = defineComponent({
  name: 'ElTag',
  setup(_, { slots }) {
    return () => h('span', { class: 'el-tag' }, slots.default?.())
  }
})

const ElButtonStub = defineComponent({
  name: 'ElButton',
  setup(_, { slots }) {
    return () => h('button', { class: 'el-button' }, slots.default?.())
  }
})

// The dashboard renders `<component :is="item.icon" />` with string icon names.
// Provide stubs for those names so Vue does not warn about unknown components.
const IconStub = defineComponent({
  name: 'IconStub',
  setup() {
    return () => h('i')
  }
})

function mountDashboard() {
  return mount(Dashboard, {
    global: {
      stubs: {
        ElCard: ElCardStub,
        ElRow: ElRowStub,
        ElCol: ElColStub,
        ElIcon: ElIconStub,
        ElEmpty: ElEmptyStub,
        ElTag: ElTagStub,
        ElButton: ElButtonStub,
        Folder: IconStub,
        Box: IconStub,
        Bell: IconStub,
        TrendCharts: IconStub,
        FolderAdd: IconStub,
        Tickets: IconStub
      }
    }
  })
}

describe('DashboardView', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
  })

  it('renders the welcome card greeting the user by nickname', () => {
    const wrapper = mountDashboard()
    expect(wrapper.find('.welcome-title').text()).toContain('Tester')
  })

  it('renders the expected number of stat cards', () => {
    const wrapper = mountDashboard()
    // There are 4 stat cards inside the .stat-row
    const statCards = wrapper.findAll('.stat-card')
    expect(statCards.length).toBe(4)
  })

  it('renders the correct card titles', () => {
    const wrapper = mountDashboard()
    const titles = wrapper.findAll('.stat-title').map((w) => w.text())
    expect(titles).toEqual(['项目总数', '在库设备', '待办任务', '本月交付'])
  })

  it('initializes each stat value to 0', () => {
    const wrapper = mountDashboard()
    const values = wrapper.findAll('.stat-value').map((w) => w.text())
    expect(values).toEqual(['0', '0', '0', '0'])
  })
})
