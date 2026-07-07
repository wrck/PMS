import { get } from '@/utils/request'

export interface LowCodeComponentMeta {
  id: number
  name: string
  displayName: string
  category: string
  propsSchema: string
  description?: string
  builtin: number
}

export function getComponentMetaList() {
  return get<LowCodeComponentMeta[]>('/api/lowcode/component-meta')
}
