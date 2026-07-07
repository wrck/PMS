import type { ComponentMeta, RegisteredComponent } from './types'

const registry = new Map<string, RegisteredComponent>()

export function register(name: string, component: any, meta: ComponentMeta) {
  registry.set(name, { component, meta })
}

export function get(name: string) {
  return registry.get(name)
}

export function list() {
  return Array.from(registry.values()).map((v) => v.meta)
}

export function has(name: string) {
  return registry.has(name)
}

// 初始化预置组件（懒加载，避免循环依赖）
export async function initBuiltinComponents() {
  const widgets = import.meta.glob('../LowCodeWidgets/*.vue')
  const metas: Record<string, ComponentMeta> = {
    UserSelector: { name: 'UserSelector', displayName: '用户选择器', category: 'SELECTOR', propsSchema: [{ key: 'multiple', type: 'boolean', default: false }] },
    DeptSelector: { name: 'DeptSelector', displayName: '部门选择器', category: 'SELECTOR', propsSchema: [{ key: 'multiple', type: 'boolean', default: false }] },
    DictSelect: { name: 'DictSelect', displayName: '数据字典下拉', category: 'SELECTOR', propsSchema: [{ key: 'dictCode', type: 'string', required: true }] },
    FileUploader: { name: 'FileUploader', displayName: '文件上传', category: 'INPUT', propsSchema: [{ key: 'accept', type: 'string' }, { key: 'maxSize', type: 'number', default: 10 }] },
    RichTextEditor: { name: 'RichTextEditor', displayName: '富文本编辑器', category: 'INPUT', propsSchema: [{ key: 'height', type: 'number', default: 300 }] },
    CodeEditor: { name: 'CodeEditor', displayName: '代码编辑器', category: 'INPUT', propsSchema: [{ key: 'language', type: 'string', default: 'javascript' }] },
    ColorPicker: { name: 'ColorPicker', displayName: '颜色选择器', category: 'INPUT', propsSchema: [{ key: 'showAlpha', type: 'boolean', default: true }] },
    TreeSelect: { name: 'TreeSelect', displayName: '树形选择', category: 'SELECTOR', propsSchema: [{ key: 'data', type: 'array' }] },
    DateRangePicker: { name: 'DateRangePicker', displayName: '日期范围', category: 'INPUT', propsSchema: [{ key: 'format', type: 'string', default: 'YYYY-MM-DD' }] },
    NumberRangeInput: { name: 'NumberRangeInput', displayName: '数字范围', category: 'INPUT', propsSchema: [{ key: 'min', type: 'number' }, { key: 'max', type: 'number' }] },
    AddressPicker: { name: 'AddressPicker', displayName: '地址选择', category: 'SELECTOR', propsSchema: [{ key: 'level', type: 'number', default: 3 }] },
    BarcodeInput: { name: 'BarcodeInput', displayName: '条码扫描', category: 'INPUT', propsSchema: [{ key: 'types', type: 'array', default: ['CODE_128', 'EAN_13'] }] },
    SignaturePad: { name: 'SignaturePad', displayName: '电子签名', category: 'INPUT', propsSchema: [{ key: 'width', type: 'number', default: 400 }, { key: 'height', type: 'number', default: 200 }] },
    ChartPreview: { name: 'ChartPreview', displayName: '图表预览', category: 'DISPLAY', propsSchema: [{ key: 'chartType', type: 'string', default: 'bar' }] },
    QrcodeDisplay: { name: 'QrcodeDisplay', displayName: '二维码展示', category: 'DISPLAY', propsSchema: [{ key: 'size', type: 'number', default: 128 }] }
  }
  for (const [path, loader] of Object.entries(widgets)) {
    const name = path.split('/').pop()!.replace('.vue', '')
    const meta = metas[name]
    if (!meta) continue
    const component = (await (loader as () => Promise<any>)()).default
    register(name, component, meta)
  }
}
