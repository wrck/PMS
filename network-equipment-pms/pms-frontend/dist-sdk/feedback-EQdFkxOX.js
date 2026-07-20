import { i as post, r as get } from "./request-BQrAOfxW.js";
//#region src/api/feedback.ts
/** 提交新反馈（任意已登录用户） */
function createFeedback(data) {
	return post("/api/system/feedback", data);
}
/** 按状态统计当前用户的反馈数量 */
function getFeedbackStatusStats() {
	return get("/api/system/feedback/stats");
}
//#endregion
export { getFeedbackStatusStats as n, createFeedback as t };

//# sourceMappingURL=feedback-EQdFkxOX.js.map