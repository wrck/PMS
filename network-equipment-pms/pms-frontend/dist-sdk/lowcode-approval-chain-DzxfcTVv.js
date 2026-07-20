import { a as put, i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-approval-chain.ts
/** 查询全部审批链 */
function getApprovalChainList() {
	return get("/api/lowcode/approval-chain");
}
/** 新建审批链 */
function createApprovalChain(data) {
	return post("/api/lowcode/approval-chain", data);
}
/** 更新审批链 */
function updateApprovalChain(id, data) {
	return put(`/api/lowcode/approval-chain/${id}`, data);
}
/** 删除审批链 */
function deleteApprovalChain(id) {
	return del(`/api/lowcode/approval-chain/${id}`);
}
/** 解析 levels JSON 字符串为级别数组（容错：空串/非法 JSON 返回空数组） */
function parseLevels(levelsJson) {
	if (!levelsJson) return [];
	try {
		const parsed = JSON.parse(levelsJson);
		if (!Array.isArray(parsed)) return [];
		return parsed.sort((a, b) => {
			var _a$level, _b$level;
			return ((_a$level = a.level) !== null && _a$level !== void 0 ? _a$level : 0) - ((_b$level = b.level) !== null && _b$level !== void 0 ? _b$level : 0);
		});
	} catch (_unused) {
		return [];
	}
}
/** 将级别数组序列化为 levels JSON 字符串 */
function serializeLevels(levels) {
	return JSON.stringify(levels, null, 2);
}
//#endregion
export { serializeLevels as a, parseLevels as i, deleteApprovalChain as n, updateApprovalChain as o, getApprovalChainList as r, createApprovalChain as t };

//# sourceMappingURL=lowcode-approval-chain-DzxfcTVv.js.map