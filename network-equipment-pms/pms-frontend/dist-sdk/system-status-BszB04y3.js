import { o as service } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { n as getFeedbackStatusStats } from "./feedback-EQdFkxOX.js";
import { o as getRecentActivities } from "./report-D_OreQK4.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, onMounted, openBlock, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives } from "vue";
//#region src/views/system-status/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "system-status-page" };
var _hoisted_2 = { class: "page-header" };
var _hoisted_3 = { class: "page-actions" };
var _hoisted_4 = {
	key: 0,
	class: "last-updated"
};
var _hoisted_5 = { class: "card-header" };
var _hoisted_6 = { class: "status-item" };
var _hoisted_7 = { class: "status-item" };
var _hoisted_8 = { class: "status-item" };
var _hoisted_9 = { class: "card-header" };
var _hoisted_10 = { class: "disk-usage" };
var _hoisted_11 = { class: "disk-usage__detail" };
var _hoisted_12 = { class: "card-header" };
var _hoisted_13 = { class: "card-header__extra" };
var _hoisted_14 = { class: "feedback-stat" };
var _hoisted_15 = { class: "feedback-stat__count" };
var _hoisted_16 = { class: "feedback-stat__label" };
var _hoisted_17 = {
	key: 1,
	class: "activity-list"
};
var _hoisted_18 = { class: "activity-item__left" };
var _hoisted_19 = { class: "activity-item__desc" };
var _hoisted_20 = { class: "activity-item__right" };
var _hoisted_21 = {
	key: 0,
	class: "activity-item__operator"
};
var _hoisted_22 = { class: "activity-item__time" };
//#endregion
//#region src/views/system-status/index.vue
var system_status_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	__name: "index",
	setup(__props) {
		const loading = ref(false);
		const health = ref(null);
		const feedbackStats = ref({
			PENDING: 0,
			PROCESSING: 0,
			RESOLVED: 0,
			CLOSED: 0
		});
		const recentActivities = ref([]);
		const lastUpdated = ref("");
		const overallStatus = computed(() => {
			var _health$value$status, _health$value;
			return (_health$value$status = (_health$value = health.value) === null || _health$value === void 0 ? void 0 : _health$value.status) !== null && _health$value$status !== void 0 ? _health$value$status : "UNKNOWN";
		});
		const dbStatus = computed(() => {
			var _health$value$compone, _health$value2;
			return (_health$value$compone = (_health$value2 = health.value) === null || _health$value2 === void 0 || (_health$value2 = _health$value2.components) === null || _health$value2 === void 0 || (_health$value2 = _health$value2.db) === null || _health$value2 === void 0 ? void 0 : _health$value2.status) !== null && _health$value$compone !== void 0 ? _health$value$compone : "UNKNOWN";
		});
		const redisStatus = computed(() => {
			var _health$value$compone2, _health$value3;
			return (_health$value$compone2 = (_health$value3 = health.value) === null || _health$value3 === void 0 || (_health$value3 = _health$value3.components) === null || _health$value3 === void 0 || (_health$value3 = _health$value3.redis) === null || _health$value3 === void 0 ? void 0 : _health$value3.status) !== null && _health$value$compone2 !== void 0 ? _health$value$compone2 : "UNKNOWN";
		});
		const diskStatus = computed(() => {
			var _health$value$compone3, _health$value4;
			return (_health$value$compone3 = (_health$value4 = health.value) === null || _health$value4 === void 0 || (_health$value4 = _health$value4.components) === null || _health$value4 === void 0 || (_health$value4 = _health$value4.diskSpace) === null || _health$value4 === void 0 ? void 0 : _health$value4.status) !== null && _health$value$compone3 !== void 0 ? _health$value$compone3 : "UNKNOWN";
		});
		const diskInfo = computed(() => {
			var _health$value5;
			const details = (_health$value5 = health.value) === null || _health$value5 === void 0 || (_health$value5 = _health$value5.components) === null || _health$value5 === void 0 || (_health$value5 = _health$value5.diskSpace) === null || _health$value5 === void 0 ? void 0 : _health$value5.details;
			if (!details) return {
				total: 0,
				free: 0,
				used: 0,
				usagePercent: 0
			};
			const total = details.total || 0;
			const free = details.free || 0;
			const used = total - free;
			return {
				total,
				free,
				used,
				usagePercent: total > 0 ? Math.round(used / total * 100) : 0
			};
		});
		const totalFeedback = computed(() => Object.values(feedbackStats.value).reduce((sum, n) => sum + n, 0));
		function formatBytes(bytes) {
			if (!bytes || bytes <= 0) return "0 B";
			const units = [
				"B",
				"KB",
				"MB",
				"GB",
				"TB"
			];
			let i = 0;
			let n = bytes;
			while (n >= 1024 && i < units.length - 1) {
				n /= 1024;
				i++;
			}
			return `${n.toFixed(2)} ${units[i]}`;
		}
		function statusTagType(status) {
			switch (status === null || status === void 0 ? void 0 : status.toUpperCase()) {
				case "UP": return "success";
				case "DOWN": return "danger";
				case "OUT_OF_SERVICE":
				case "DEGRADED": return "warning";
				default: return "info";
			}
		}
		function statusLabel(status) {
			switch (status === null || status === void 0 ? void 0 : status.toUpperCase()) {
				case "UP": return "正常运行";
				case "DOWN": return "已宕机";
				case "OUT_OF_SERVICE": return "服务不可用";
				case "DEGRADED": return "降级运行";
				case "UNKNOWN": return "未知";
				default: return status || "未知";
			}
		}
		function feedbackStatusTagType(status) {
			switch (status) {
				case "PENDING": return "danger";
				case "PROCESSING": return "warning";
				case "RESOLVED": return "success";
				case "CLOSED": return "info";
				default: return "info";
			}
		}
		function feedbackStatusLabel(status) {
			switch (status) {
				case "PENDING": return "待处理";
				case "PROCESSING": return "处理中";
				case "RESOLVED": return "已解决";
				case "CLOSED": return "已关闭";
				default: return status;
			}
		}
		async function loadHealth() {
			try {
				const res = await service.get("/actuator/health");
				health.value = res.data;
			} catch (_unused) {
				health.value = { status: "UNKNOWN" };
				ElMessage.warning("无法获取后端健康状态，请检查网络或服务是否启动");
			}
		}
		async function loadFeedbackStats() {
			try {
				feedbackStats.value = await getFeedbackStatusStats();
			} catch (_unused2) {}
		}
		async function loadRecentActivities() {
			try {
				recentActivities.value = await getRecentActivities(5);
			} catch (_unused3) {
				recentActivities.value = [];
			}
		}
		async function refresh() {
			loading.value = true;
			await Promise.all([
				loadHealth(),
				loadFeedbackStats(),
				loadRecentActivities()
			]);
			lastUpdated.value = (/* @__PURE__ */ new Date()).toLocaleString("zh-CN");
			loading.value = false;
		}
		function activityTagType(type) {
			switch (type) {
				case "LOGIN": return "success";
				case "OPER": return "info";
				case "SCHEDULE": return "warning";
				case "INTEGRATION": return "danger";
				default: return "info";
			}
		}
		function activityTypeLabel(type) {
			switch (type) {
				case "LOGIN": return "登录";
				case "OPER": return "操作";
				case "SCHEDULE": return "定时任务";
				case "INTEGRATION": return "集成";
				default: return type;
			}
		}
		function formatActivityTime(time) {
			if (!time) return "";
			return time.length >= 16 ? time.slice(0, 16).replace("T", " ") : time;
		}
		onMounted(() => {
			refresh();
		});
		return (_ctx, _cache) => {
			const _component_el_button = resolveComponent("el-button");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_empty = resolveComponent("el-empty");
			const _directive_loading = resolveDirective("loading");
			return withDirectives((openBlock(), createElementBlock("div", _hoisted_1, [
				createElementVNode("header", _hoisted_2, [_cache[1] || (_cache[1] = createElementVNode("h2", { class: "page-title" }, "系统状态", -1)), createElementVNode("div", _hoisted_3, [lastUpdated.value ? (openBlock(), createElementBlock("span", _hoisted_4, "最近更新：" + toDisplayString(lastUpdated.value), 1)) : createCommentVNode("", true), createVNode(_component_el_button, {
					type: "primary",
					onClick: refresh
				}, {
					default: withCtx(() => [..._cache[0] || (_cache[0] = [createTextVNode("刷新", -1)])]),
					_: 1
				})])]),
				createVNode(_component_el_card, { class: "status-card" }, {
					header: withCtx(() => [createElementVNode("div", _hoisted_5, [_cache[2] || (_cache[2] = createElementVNode("span", null, "总体状态", -1)), createVNode(_component_el_tag, {
						type: statusTagType(overallStatus.value),
						size: "large"
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(overallStatus.value)), 1)]),
						_: 1
					}, 8, ["type"])])]),
					default: withCtx(() => [createVNode(_component_el_row, { gutter: 16 }, {
						default: withCtx(() => [
							createVNode(_component_el_col, { span: 8 }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_6, [_cache[3] || (_cache[3] = createElementVNode("div", { class: "status-item__label" }, "后端服务", -1)), createVNode(_component_el_tag, { type: statusTagType(overallStatus.value) }, {
									default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(overallStatus.value)), 1)]),
									_: 1
								}, 8, ["type"])])]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 8 }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_7, [_cache[4] || (_cache[4] = createElementVNode("div", { class: "status-item__label" }, "数据库", -1)), createVNode(_component_el_tag, { type: statusTagType(dbStatus.value) }, {
									default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(dbStatus.value)), 1)]),
									_: 1
								}, 8, ["type"])])]),
								_: 1
							}),
							createVNode(_component_el_col, { span: 8 }, {
								default: withCtx(() => [createElementVNode("div", _hoisted_8, [_cache[5] || (_cache[5] = createElementVNode("div", { class: "status-item__label" }, "Redis 缓存", -1)), createVNode(_component_el_tag, { type: statusTagType(redisStatus.value) }, {
									default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(redisStatus.value)), 1)]),
									_: 1
								}, 8, ["type"])])]),
								_: 1
							})
						]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_card, { class: "status-card" }, {
					header: withCtx(() => [createElementVNode("div", _hoisted_9, [_cache[6] || (_cache[6] = createElementVNode("span", null, "磁盘使用率", -1)), createVNode(_component_el_tag, { type: statusTagType(diskStatus.value) }, {
						default: withCtx(() => [createTextVNode(toDisplayString(statusLabel(diskStatus.value)), 1)]),
						_: 1
					}, 8, ["type"])])]),
					default: withCtx(() => [createElementVNode("div", _hoisted_10, [createVNode(_component_el_progress, {
						percentage: diskInfo.value.usagePercent,
						color: diskInfo.value.usagePercent > 85 ? "#f56c6c" : "#409eff",
						"stroke-width": 20,
						"text-inside": true
					}, null, 8, ["percentage", "color"]), createElementVNode("div", _hoisted_11, [
						createElementVNode("span", null, "已用：" + toDisplayString(formatBytes(diskInfo.value.used)), 1),
						createElementVNode("span", null, "可用：" + toDisplayString(formatBytes(diskInfo.value.free)), 1),
						createElementVNode("span", null, "总计：" + toDisplayString(formatBytes(diskInfo.value.total)), 1)
					])])]),
					_: 1
				}),
				createVNode(_component_el_card, { class: "status-card" }, {
					header: withCtx(() => [createElementVNode("div", _hoisted_12, [_cache[7] || (_cache[7] = createElementVNode("span", null, "我的反馈处理状态", -1)), createElementVNode("span", _hoisted_13, "共 " + toDisplayString(totalFeedback.value) + " 条", 1)])]),
					default: withCtx(() => [createVNode(_component_el_row, { gutter: 16 }, {
						default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(feedbackStats.value, (count, status) => {
							return openBlock(), createBlock(_component_el_col, {
								key: status,
								span: 6
							}, {
								default: withCtx(() => [createElementVNode("div", _hoisted_14, [createElementVNode("div", _hoisted_15, toDisplayString(count), 1), createElementVNode("div", _hoisted_16, [createVNode(_component_el_tag, {
									type: feedbackStatusTagType(String(status)),
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(feedbackStatusLabel(String(status))), 1)]),
									_: 2
								}, 1032, ["type"])])])]),
								_: 2
							}, 1024);
						}), 128))]),
						_: 1
					})]),
					_: 1
				}),
				createVNode(_component_el_card, { class: "status-card" }, {
					header: withCtx(() => [..._cache[8] || (_cache[8] = [createElementVNode("div", { class: "card-header" }, [createElementVNode("span", null, "最近系统动态")], -1)])]),
					default: withCtx(() => [recentActivities.value.length === 0 ? (openBlock(), createBlock(_component_el_empty, {
						key: 0,
						description: "暂无动态"
					})) : (openBlock(), createElementBlock("ul", _hoisted_17, [(openBlock(true), createElementBlock(Fragment, null, renderList(recentActivities.value, (item) => {
						return openBlock(), createElementBlock("li", {
							key: item.id,
							class: "activity-item"
						}, [createElementVNode("div", _hoisted_18, [createVNode(_component_el_tag, {
							type: activityTagType(item.type),
							size: "small"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(activityTypeLabel(item.type)), 1)]),
							_: 2
						}, 1032, ["type"]), createElementVNode("span", _hoisted_19, toDisplayString(item.description), 1)]), createElementVNode("div", _hoisted_20, [item.operatorName ? (openBlock(), createElementBlock("span", _hoisted_21, toDisplayString(item.operatorName), 1)) : createCommentVNode("", true), createElementVNode("span", _hoisted_22, toDisplayString(formatActivityTime(item.createdAt)), 1)])]);
					}), 128))]))]),
					_: 1
				})
			])), [[_directive_loading, loading.value]]);
		};
	}
}), [["__scopeId", "data-v-ebf06190"]]);
//#endregion
export { system_status_default as default };

//# sourceMappingURL=system-status-BszB04y3.js.map