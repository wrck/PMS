import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/asset.ts
function getCategoryTree() {
	return get("/api/asset/category/tree");
}
function createCategory(data) {
	return post("/api/asset/category", data);
}
function updateCategory(data) {
	return put("/api/asset/category", data);
}
function deleteCategory(id) {
	return del(`/api/asset/category/${id}`);
}
function listModels(params) {
	return get("/api/asset/model/list", params);
}
function createModel(data) {
	return post("/api/asset/model", data);
}
function updateModel(data) {
	return put("/api/asset/model", data);
}
function deleteModel(id) {
	return del(`/api/asset/model/${id}`);
}
/** Asset status enumeration values */
var ASSET_STATUS = {
	IN_STOCK: "IN_STOCK",
	ALLOCATED: "ALLOCATED",
	IN_TRANSIT: "IN_TRANSIT",
	SCRAPPED: "SCRAPPED"
};
function inboundAsset(data) {
	return post("/api/asset/inbound", data);
}
function allocateAsset(id, data) {
	return post(`/api/asset/${id}/allocate`, data);
}
function returnAsset(id) {
	return post(`/api/asset/${id}/return`);
}
function getAsset(id) {
	return get(`/api/asset/${id}`);
}
function listAssets(params) {
	return get("/api/asset/list", params);
}
function updateAsset(data) {
	return put("/api/asset", data);
}
function deleteAsset(id) {
	return del(`/api/asset/${id}`);
}
function getAssetLifecycle(id) {
	return get(`/api/asset/${id}/lifecycle`);
}
var TRANSFER_STATUS = {
	PENDING: "PENDING",
	APPROVED: "APPROVED",
	REJECTED: "REJECTED"
};
function applyTransfer(data) {
	return post("/api/asset/transfer/apply", data);
}
function approveTransfer(id, data) {
	return post(`/api/asset/transfer/${id}/approve`, data);
}
function rejectTransfer(id, data) {
	return post(`/api/asset/transfer/${id}/reject`, data);
}
function listTransfers(params) {
	return get("/api/asset/transfer/list", params);
}
//#endregion
export { updateModel as S, listTransfers as _, approveTransfer as a, updateAsset as b, deleteAsset as c, getAsset as d, getAssetLifecycle as f, listModels as g, listAssets as h, applyTransfer as i, deleteCategory as l, inboundAsset as m, TRANSFER_STATUS as n, createCategory as o, getCategoryTree as p, allocateAsset as r, createModel as s, ASSET_STATUS as t, deleteModel as u, rejectTransfer as v, updateCategory as x, returnAsset as y };

//# sourceMappingURL=asset-DozMltVh.js.map