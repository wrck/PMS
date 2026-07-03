import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, h } from 'vue'
import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'

// Mock the user store — the dashboard greets the user from store.userInfo.
vi.mock('@/stores/user', () => ({
  useUserStore: () => ({
    userInfo: { nickname: 'Tester', username: 'tester' },
    token: 't',
    permissions: []
  })
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
        Folder: IconStub,
        Box: IconStub,
        Bell: IconStub,
        TrendCharts: IconStub
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
