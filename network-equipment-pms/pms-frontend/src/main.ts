import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VxeUI from 'vxe-pc-ui'
import 'vxe-pc-ui/es/style.css'
import VxeUITable from 'vxe-table'
import 'vxe-table/es/style.css'
import App from './App.vue'
import router from './router'
import { registerDirectives } from './directives'
// bpmn-js / diagram-js 核心样式（流程设计器必需）
import 'bpmn-js/dist/assets/diagram-js.css'
import 'bpmn-js/dist/assets/bpmn-js.css'
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css'
import './style.css'
import './styles/index.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
// vxe-table 4.x：先注册 vxe-pc-ui（基础能力），再注册 vxe-table（表格组件）
app.use(VxeUI)
app.use(VxeUITable)

// 注册全局自定义指令（v-debounce / v-permission）
registerDirectives(app)

// Register all Element Plus icons globally so they can be referenced by name
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
