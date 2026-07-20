import { r as get } from "./request-BQrAOfxW.js";
//#region src/api/report.ts
/** 项目交付统计 */
function getDeliveryStats(params) {
	return get("/api/report/delivery", params);
}
/** 设备资产统计 */
function getAssetStats() {
	return get("/api/report/asset");
}
/** 实施效能统计 */
function getImplementationStats() {
	return get("/api/report/implementation");
}
/** 仪表盘统计（项目/在库设备/待办/本月交付等） */
function getDashboardStats() {
	return get("/api/report/dashboard/stats");
}
/** 项目趋势（最近 6 月状态分布） */
function getProjectTrend() {
	return get("/api/report/project/trend");
}
/** 待办列表（Top N，默认 5） */
function getTodoList(limit = 5) {
	return get("/api/report/todo/list", { limit });
}
/** 近期动态（最近 N 条日志，默认 10） */
function getRecentActivities(limit = 10) {
	return get("/api/report/recent-activities", { limit });
}
//#endregion
export { getProjectTrend as a, getImplementationStats as i, getDashboardStats as n, getRecentActivities as o, getDeliveryStats as r, getTodoList as s, getAssetStats as t };

//# sourceMappingURL=report-D_OreQK4.js.map