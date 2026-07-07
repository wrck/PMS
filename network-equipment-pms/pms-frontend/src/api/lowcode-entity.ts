// src/api/lowcode-entity.ts
import { del, get, post } from '@/utils/request'

export interface LowCodeEntity {
  id?: number
  code: string
  name: string
  tableName: string
  description?: string
  bizType?: string
  status?: string
  version?: number
}

export interface LowCodeField {
  id?: number
  entityId?: number
  name: string
  label: string
  fieldType: 'STRING' | 'INTEGER' | 'LONG' | 'DECIMAL' | 'BOOLEAN' | 'DATE' | 'DATETIME' | 'TEXT'
  length?: number
  scale?: number
  nullable: number
  primaryKey: number
  indexed: number
  uniqueFlag: number
  defaultValue?: string
  sortOrder: number
}

export interface LowCodeRelation {
  id?: number
  fromEntityId: number
  toEntityId: number
  relationType: 'ONE_TO_ONE' | 'ONE_TO_MANY' | 'MANY_TO_ONE' | 'MANY_TO_MANY'
  fromFieldName: string
  toFieldName?: string
  reverseName?: string
  junctionTable?: string
  onDelete: 'CASCADE' | 'SET_NULL' | 'RESTRICT'
  onUpdate: 'CASCADE' | 'RESTRICT'
}

export interface EntityDesignDTO {
  entity: LowCodeEntity
  fields: LowCodeField[]
  relations?: LowCodeRelation[]
}

export interface DdlResultDTO {
  tableName: string
  ddlStatements: string[]
  hasJunctionTable: boolean
  junctionTableDdl?: string
}

export function getEntityList() {
  return get<LowCodeEntity[]>('/api/lowcode/entity/list')
}

export function getEntityDesign(id: number) {
  return get<EntityDesignDTO>(`/api/lowcode/entity/${id}`)
}

export function saveEntityDesign(data: EntityDesignDTO) {
  return post<LowCodeEntity>('/api/lowcode/entity', data)
}

export function generateDdl(id: number) {
  return get<DdlResultDTO>(`/api/lowcode/entity/${id}/ddl`)
}

export function publishEntity(id: number, changeLog?: string) {
  return post<LowCodeEntity>(`/api/lowcode/entity/${id}/publish`, null, {
    params: { changeLog }
  })
}

export function deleteEntity(id: number) {
  return del(`/api/lowcode/entity/${id}`)
}

export function checkTableName(tableName: string, excludeId?: number) {
  return get<boolean>('/api/lowcode/entity/check-table-name', {
    tableName,
    excludeId
  })
}

/** 保存实体关联 */
export function saveRelations(entityId: number, relations: LowCodeRelation[]) {
  return post(`/api/lowcode/entity/${entityId}/relations`, relations)
}
