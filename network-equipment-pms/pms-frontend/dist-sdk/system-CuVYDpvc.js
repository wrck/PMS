import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/system.ts
function getUserPage(params) {
	return get("/api/system/user/page", params);
}
function createUser(data) {
	return post("/api/system/user", data);
}
function updateUser(data) {
	return put("/api/system/user", data);
}
function deleteUser(id) {
	return del(`/api/system/user/${id}`);
}
function getRolePage(params) {
	return get("/api/system/role/page", params);
}
/**
* 全部角色列表（审批链配置等场景下拉用）。
*
* <p>仅需登录（无需 system:role:list 权限），返回 id/roleName/roleCode 字段。
* 调用后端实际路径 /api/system/role/all。</p>
*/
function getAllRoles() {
	return get("/api/system/role/all");
}
function createRole(data) {
	return post("/api/system/role", data);
}
function updateRole(data) {
	return put("/api/system/role", data);
}
function deleteRole(id) {
	return del(`/api/system/role/${id}`);
}
function getMenuTree() {
	return get("/api/system/menu/tree");
}
function createMenu(data) {
	return post("/api/system/menu", data);
}
function updateMenu(data) {
	return put("/api/system/menu", data);
}
function deleteMenu(id) {
	return del(`/api/system/menu/${id}`);
}
function getDictPage(params) {
	return get("/api/system/dict/page", params);
}
function createDict(data) {
	return post("/api/system/dict", data);
}
function updateDict(data) {
	return put("/api/system/dict", data);
}
function deleteDict(id) {
	return del(`/api/system/dict/${id}`);
}
function getDictItems(dictType) {
	return get(`/api/system/dict/items/${dictType}`);
}
//#endregion
export { updateRole as _, deleteDict as a, deleteUser as c, getDictPage as d, getMenuTree as f, updateMenu as g, updateDict as h, createUser as i, getAllRoles as l, getUserPage as m, createMenu as n, deleteMenu as o, getRolePage as p, createRole as r, deleteRole as s, createDict as t, getDictItems as u, updateUser as v };

//# sourceMappingURL=system-CuVYDpvc.js.map