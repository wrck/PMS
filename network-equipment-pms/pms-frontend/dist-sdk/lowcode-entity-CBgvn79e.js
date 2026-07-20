import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-entity.ts
function getEntityList() {
	return get("/api/lowcode/entity/list");
}
function getEntityDesign(id) {
	return get(`/api/lowcode/entity/${id}`);
}
function saveEntityDesign(data) {
	return post("/api/lowcode/entity", data);
}
function generateDdl(id) {
	return get(`/api/lowcode/entity/${id}/ddl`);
}
function publishEntity(id, changeLog) {
	return post(`/api/lowcode/entity/${id}/publish`, null, { params: { changeLog } });
}
function deleteEntity(id) {
	return del(`/api/lowcode/entity/${id}`);
}
function checkTableName(tableName, excludeId) {
	return get("/api/lowcode/entity/check-table-name", {
		tableName,
		excludeId
	});
}
/** 保存实体关联 */
function saveRelations(entityId, relations) {
	return post(`/api/lowcode/entity/${entityId}/relations`, relations);
}
/** 查询实体 DDL 备份记录列表（按时间倒序） */
function listDdlBackups(entityId) {
	return get(`/api/lowcode/entity/${entityId}/ddl-backups`);
}
/** 回滚最近一次 DDL 操作，返回回滚的备份类型 */
function rollbackLastDdl(entityId) {
	return post(`/api/lowcode/entity/${entityId}/rollback-ddl`);
}
/** 按备份记录 ID 回滚 DDL */
function rollbackByBackupId(backupId) {
	return post(`/api/lowcode/entity/ddl/rollback/${backupId}`);
}
//#endregion
export { getEntityList as a, rollbackByBackupId as c, saveRelations as d, getEntityDesign as i, rollbackLastDdl as l, deleteEntity as n, listDdlBackups as o, generateDdl as r, publishEntity as s, checkTableName as t, saveEntityDesign as u };

//# sourceMappingURL=lowcode-entity-CBgvn79e.js.map