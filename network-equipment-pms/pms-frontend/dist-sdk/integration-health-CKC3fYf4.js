import { i as post, r as get } from "./request-BQrAOfxW.js";
import { t as _plugin_vue_export_helper_default } from "./_plugin-vue_export-helper-BOaGB7Aw.js";
import { ElMessage } from "element-plus";
import { Fragment, computed, createBlock, createCommentVNode, createElementBlock, createElementVNode, createTextVNode, createVNode, defineComponent, normalizeStyle, onBeforeUnmount, onMounted, openBlock, reactive, ref, renderList, resolveComponent, resolveDirective, toDisplayString, withCtx, withDirectives, withModifiers } from "vue";
//#region src/api/integration-health.ts
function getIntegrationHealth() {
	return get("/api/integration/health");
}
function listIntegrationLogs(params) {
	return get("/api/integration/log/list", params);
}
function retryPush(logId) {
	return post(`/api/integration/log/${logId}/retry`);
}
//#endregion
//#region src/views/integration-health/index.vue?vue&type=script&setup=true&lang.ts
var _hoisted_1 = { class: "page-container" };
var _hoisted_2 = { class: "system-card-header" };
var _hoisted_3 = { class: "system-name" };
var _hoisted_4 = { class: "status-indicator" };
var _hoisted_5 = { class: "system-row" };
var _hoisted_6 = { class: "system-row" };
var _hoisted_7 = { class: "row-value" };
var _hoisted_8 = { class: "system-row" };
var _hoisted_9 = {
	key: 1,
	class: "row-value"
};
var _hoisted_10 = { class: "system-row" };
var _hoisted_11 = { class: "row-value" };
var _hoisted_12 = { class: "system-row" };
var _hoisted_13 = {
	key: 0,
	class: "system-message"
};
var _hoisted_14 = { class: "system-card-footer" };
var _hoisted_15 = {
	key: 1,
	class: "text-muted"
};
//#endregion
//#region src/views/integration-health/index.vue
var integration_health_default = /*#__PURE__*/ _plugin_vue_export_helper_default(/* @__PURE__ */ defineComponent({
	name: "IntegrationHealth",
	__name: "index",
	setup(__props) {
		const healthLoading = ref(false);
		const healthItems = ref([]);
		const overallStatus = ref("HEALTHY");
		const timestamp = ref("");
		const alertMeta = computed(() => {
			switch (overallStatus.value) {
				case "HEALTHY": return {
					type: "success",
					title: "集成系统总体状态：健康",
					desc: "所有集成系统运行正常"
				};
				case "DEGRADED": return {
					type: "warning",
					title: "集成系统总体状态：降级",
					desc: "部分集成系统存在异常"
				};
				case "DOWN": return {
					type: "error",
					title: "集成系统总体状态：不可用",
					desc: "集成系统存在严重故障"
				};
				default: return {
					type: "info",
					title: "集成系统状态：未知",
					desc: ""
				};
			}
		});
		function statusMeta(status) {
			switch (status) {
				case "UP": return {
					color: "#67C23A",
					label: "正常"
				};
				case "DEGRADED": return {
					color: "#E6A23C",
					label: "降级"
				};
				case "DOWN": return {
					color: "#F56C6C",
					label: "不可用"
				};
				default: return {
					color: "#909399",
					label: "未知"
				};
			}
		}
		function tokenTagType(valid) {
			return valid ? "success" : "danger";
		}
		function rateStatus(rate) {
			if (rate >= 95) return "success";
			if (rate >= 80) return "warning";
			return "exception";
		}
		async function loadHealth() {
			healthLoading.value = true;
			try {
				var _res$items, _res$overallStatus, _res$timestamp;
				const res = await getIntegrationHealth();
				healthItems.value = (_res$items = res.items) !== null && _res$items !== void 0 ? _res$items : [];
				overallStatus.value = (_res$overallStatus = res.overallStatus) !== null && _res$overallStatus !== void 0 ? _res$overallStatus : "HEALTHY";
				timestamp.value = (_res$timestamp = res.timestamp) !== null && _res$timestamp !== void 0 ? _res$timestamp : "";
			} catch (_unused) {} finally {
				healthLoading.value = false;
			}
		}
		function handleRefreshSystem(item) {
			ElMessage.info(`正在刷新「${item.system}」状态...`);
			loadHealth();
		}
		const logLoading = ref(false);
		const logData = ref([]);
		const logTotal = ref(0);
		const query = reactive({
			page: 1,
			size: 10,
			logType: "",
			responseStatus: ""
		});
		async function loadLogs() {
			logLoading.value = true;
			try {
				var _res$records, _res$total;
				const params = {
					page: query.page,
					size: query.size
				};
				if (query.logType) params.logType = query.logType;
				if (query.responseStatus) params.responseStatus = query.responseStatus;
				const res = await listIntegrationLogs(params);
				logData.value = (_res$records = res.records) !== null && _res$records !== void 0 ? _res$records : [];
				logTotal.value = (_res$total = res.total) !== null && _res$total !== void 0 ? _res$total : 0;
			} catch (_unused2) {} finally {
				logLoading.value = false;
			}
		}
		function handleSearch() {
			query.page = 1;
			loadLogs();
		}
		function handleReset() {
			query.logType = "";
			query.responseStatus = "";
			query.page = 1;
			loadLogs();
		}
		function handlePageChange(p) {
			query.page = p;
			loadLogs();
		}
		function handleSizeChange(s) {
			query.size = s;
			query.page = 1;
			loadLogs();
		}
		function responseTagType(status) {
			return status === "SUCCESS" ? "success" : "danger";
		}
		async function handleRetry(row) {
			try {
				await retryPush(row.id);
				ElMessage.success("已触发重试");
				loadLogs();
				loadHealth();
			} catch (_unused3) {}
		}
		let refreshTimer = null;
		function startAutoRefresh() {
			stopAutoRefresh();
			refreshTimer = setInterval(() => {
				loadHealth();
				loadLogs();
			}, 6e4);
		}
		function stopAutoRefresh() {
			if (refreshTimer) {
				clearInterval(refreshTimer);
				refreshTimer = null;
			}
		}
		onMounted(() => {
			loadHealth();
			loadLogs();
			startAutoRefresh();
		});
		onBeforeUnmount(() => {
			stopAutoRefresh();
		});
		return (_ctx, _cache) => {
			const _component_el_alert = resolveComponent("el-alert");
			const _component_el_tag = resolveComponent("el-tag");
			const _component_el_progress = resolveComponent("el-progress");
			const _component_el_button = resolveComponent("el-button");
			const _component_el_card = resolveComponent("el-card");
			const _component_el_col = resolveComponent("el-col");
			const _component_el_empty = resolveComponent("el-empty");
			const _component_el_row = resolveComponent("el-row");
			const _component_el_option = resolveComponent("el-option");
			const _component_el_select = resolveComponent("el-select");
			const _component_el_form_item = resolveComponent("el-form-item");
			const _component_el_form = resolveComponent("el-form");
			const _component_el_table_column = resolveComponent("el-table-column");
			const _component_el_table = resolveComponent("el-table");
			const _component_el_pagination = resolveComponent("el-pagination");
			const _directive_loading = resolveDirective("loading");
			return openBlock(), createElementBlock("div", _hoisted_1, [
				createVNode(_component_el_alert, {
					title: alertMeta.value.title,
					description: alertMeta.value.desc + (timestamp.value ? `（更新时间：${timestamp.value}）` : ""),
					type: alertMeta.value.type,
					closable: false,
					"show-icon": ""
				}, null, 8, [
					"title",
					"description",
					"type"
				]),
				withDirectives((openBlock(), createBlock(_component_el_row, { gutter: 16 }, {
					default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(healthItems.value, (item) => {
						return openBlock(), createBlock(_component_el_col, {
							key: item.system,
							span: 8
						}, {
							default: withCtx(() => [createVNode(_component_el_card, {
								shadow: "hover",
								class: "system-card"
							}, {
								default: withCtx(() => {
									var _item$successRate, _item$successRate2;
									return [
										createElementVNode("div", _hoisted_2, [createElementVNode("span", _hoisted_3, toDisplayString(item.system), 1), createElementVNode("span", _hoisted_4, [createElementVNode("span", {
											class: "status-dot",
											style: normalizeStyle({ background: statusMeta(item.status).color })
										}, null, 4), createVNode(_component_el_tag, {
											size: "small",
											type: item.status === "UP" ? "success" : item.status === "DEGRADED" ? "warning" : "danger"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(statusMeta(item.status).label), 1)]),
											_: 2
										}, 1032, ["type"])])]),
										createElementVNode("div", _hoisted_5, [_cache[3] || (_cache[3] = createElementVNode("span", { class: "row-label" }, "Token 有效性：", -1)), createVNode(_component_el_tag, {
											size: "small",
											type: tokenTagType(item.tokenValid)
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(item.tokenValid ? "有效" : "失效"), 1)]),
											_: 2
										}, 1032, ["type"])]),
										createElementVNode("div", _hoisted_6, [_cache[4] || (_cache[4] = createElementVNode("span", { class: "row-label" }, "最近推送时间：", -1)), createElementVNode("span", _hoisted_7, toDisplayString(item.lastPushTime || "-"), 1)]),
										createElementVNode("div", _hoisted_8, [_cache[5] || (_cache[5] = createElementVNode("span", { class: "row-label" }, "最近推送状态：", -1)), item.lastPushStatus ? (openBlock(), createBlock(_component_el_tag, {
											key: 0,
											size: "small",
											type: responseTagType(item.lastPushStatus)
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(item.lastPushStatus), 1)]),
											_: 2
										}, 1032, ["type"])) : (openBlock(), createElementBlock("span", _hoisted_9, "-"))]),
										createElementVNode("div", _hoisted_10, [_cache[6] || (_cache[6] = createElementVNode("span", { class: "row-label" }, "推送统计：", -1)), createElementVNode("span", _hoisted_11, " 总数 " + toDisplayString(item.totalPushes) + " / 失败 " + toDisplayString(item.failedPushes), 1)]),
										createElementVNode("div", _hoisted_12, [_cache[7] || (_cache[7] = createElementVNode("span", { class: "row-label" }, "成功率：", -1)), createVNode(_component_el_progress, {
											percentage: Number((_item$successRate = item.successRate) !== null && _item$successRate !== void 0 ? _item$successRate : 0),
											status: rateStatus(Number((_item$successRate2 = item.successRate) !== null && _item$successRate2 !== void 0 ? _item$successRate2 : 0)),
											"stroke-width": 10
										}, null, 8, ["percentage", "status"])]),
										item.message ? (openBlock(), createElementBlock("div", _hoisted_13, toDisplayString(item.message), 1)) : createCommentVNode("", true),
										createElementVNode("div", _hoisted_14, [createVNode(_component_el_button, {
											size: "small",
											type: "primary",
											plain: "",
											onClick: ($event) => handleRefreshSystem(item)
										}, {
											default: withCtx(() => [..._cache[8] || (_cache[8] = [createTextVNode(" 手动重试 ", -1)])]),
											_: 1
										}, 8, ["onClick"])])
									];
								}),
								_: 2
							}, 1024)]),
							_: 2
						}, 1024);
					}), 128)), healthItems.value.length === 0 ? (openBlock(), createBlock(_component_el_col, {
						key: 0,
						span: 24
					}, {
						default: withCtx(() => [createVNode(_component_el_empty, { description: "暂无集成系统数据" })]),
						_: 1
					})) : createCommentVNode("", true)]),
					_: 1
				})), [[_directive_loading, healthLoading.value]]),
				createVNode(_component_el_card, { shadow: "never" }, {
					header: withCtx(() => [..._cache[9] || (_cache[9] = [createElementVNode("span", { class: "page-title" }, "推送历史记录", -1)])]),
					default: withCtx(() => [
						createVNode(_component_el_form, {
							inline: true,
							onSubmit: _cache[2] || (_cache[2] = withModifiers(() => {}, ["prevent"]))
						}, {
							default: withCtx(() => [
								createVNode(_component_el_form_item, { label: "系统" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.logType,
										"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => query.logType = $event),
										placeholder: "全部系统",
										clearable: "",
										style: { "width": "140px" }
									}, {
										default: withCtx(() => [
											createVNode(_component_el_option, {
												label: "D365",
												value: "D365"
											}),
											createVNode(_component_el_option, {
												label: "FP",
												value: "FP"
											}),
											createVNode(_component_el_option, {
												label: "OA",
												value: "OA"
											})
										]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "响应状态" }, {
									default: withCtx(() => [createVNode(_component_el_select, {
										modelValue: query.responseStatus,
										"onUpdate:modelValue": _cache[1] || (_cache[1] = ($event) => query.responseStatus = $event),
										placeholder: "全部状态",
										clearable: "",
										style: { "width": "140px" }
									}, {
										default: withCtx(() => [createVNode(_component_el_option, {
											label: "SUCCESS",
											value: "SUCCESS"
										}), createVNode(_component_el_option, {
											label: "FAIL",
											value: "FAIL"
										})]),
										_: 1
									}, 8, ["modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, null, {
									default: withCtx(() => [createVNode(_component_el_button, {
										type: "primary",
										icon: "Search",
										onClick: handleSearch
									}, {
										default: withCtx(() => [..._cache[10] || (_cache[10] = [createTextVNode("查询", -1)])]),
										_: 1
									}), createVNode(_component_el_button, {
										icon: "Refresh",
										onClick: handleReset
									}, {
										default: withCtx(() => [..._cache[11] || (_cache[11] = [createTextVNode("重置", -1)])]),
										_: 1
									})]),
									_: 1
								})
							]),
							_: 1
						}),
						withDirectives((openBlock(), createBlock(_component_el_table, {
							data: logData.value,
							border: "",
							stripe: ""
						}, {
							empty: withCtx(() => [createVNode(_component_el_empty, { description: "暂无推送日志" })]),
							default: withCtx(() => [
								createVNode(_component_el_table_column, {
									prop: "createTime",
									label: "时间",
									width: "170"
								}),
								createVNode(_component_el_table_column, {
									prop: "logType",
									label: "系统",
									width: "90",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									prop: "businessType",
									label: "业务类型",
									width: "140",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "requestUrl",
									label: "URL",
									"min-width": "220",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									label: "响应状态",
									width: "120",
									align: "center"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_tag, {
										type: responseTagType(row.responseStatus),
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(row.responseStatus), 1)]),
										_: 2
									}, 1032, ["type"])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "errorMessage",
									label: "错误信息",
									"min-width": "200",
									"show-overflow-tooltip": ""
								}),
								createVNode(_component_el_table_column, {
									prop: "retryCount",
									label: "重试次数",
									width: "100",
									align: "center"
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "100",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [row.responseStatus === "FAIL" ? (openBlock(), createBlock(_component_el_button, {
										key: 0,
										link: "",
										type: "primary",
										onClick: ($event) => handleRetry(row)
									}, {
										default: withCtx(() => [..._cache[12] || (_cache[12] = [createTextVNode(" 重试 ", -1)])]),
										_: 1
									}, 8, ["onClick"])) : (openBlock(), createElementBlock("span", _hoisted_15, "-"))]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["data"])), [[_directive_loading, logLoading.value]]),
						createVNode(_component_el_pagination, {
							class: "pagination",
							background: "",
							"current-page": query.page,
							"page-size": query.size,
							total: logTotal.value,
							"page-sizes": [
								10,
								20,
								50
							],
							layout: "total, sizes, prev, pager, next, jumper",
							onCurrentChange: handlePageChange,
							onSizeChange: handleSizeChange
						}, null, 8, [
							"current-page",
							"page-size",
							"total"
						])
					]),
					_: 1
				})
			]);
		};
	}
}), [["__scopeId", "data-v-2fa99461"]]);
//#endregion
export { integration_health_default as default };

//# sourceMappingURL=integration-health-CKC3fYf4.js.map