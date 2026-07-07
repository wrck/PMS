export interface ComponentPropDef {
  key: string
  type: 'string' | 'number' | 'boolean' | 'array' | 'object'
  default?: any
  required?: boolean
}

export interface ComponentMeta {
  name: string
  displayName: string
  category: string
  propsSchema: ComponentPropDef[]
}

export interface RegisteredComponent {
  component: any
  meta: ComponentMeta
}
