import { i as post, n as del, r as get } from "./request-BQrAOfxW.js";
//#region src/api/lowcode-rule.ts
function getRuleList() {
	return get("/api/lowcode/rule");
}
function saveRule(data) {
	return post("/api/lowcode/rule", data);
}
function deleteRule(id) {
	return del(`/api/lowcode/rule/${id}`);
}
function executeRule(code, facts) {
	return post(`/api/lowcode/rule/${code}/execute`, facts);
}
/** 发布规则并生成版本快照 */
function publishRuleWithVersion(id) {
	return post(`/api/lowcode/rule/${id}/publish`);
}
/** 查询规则版本历史 */
function getRuleVersions(id) {
	return get(`/api/lowcode/rule/${id}/versions`);
}
/** 回滚规则到指定版本 */
function rollbackRule(id, targetVersion) {
	return post(`/api/lowcode/rule/${id}/rollback/${targetVersion}`);
}
//#endregion
export { publishRuleWithVersion as a, getRuleVersions as i, executeRule as n, rollbackRule as o, getRuleList as r, saveRule as s, deleteRule as t };

//# sourceMappingURL=lowcode-rule-BZ8USwM5.js.map